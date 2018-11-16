package com.reign.gcld.player.service;

import org.springframework.stereotype.*;
import com.reign.gcld.building.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.general.service.*;
import com.reign.gcld.battle.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.player.dto.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.player.domain.*;
import java.util.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.framework.json.*;
import com.reign.util.*;
import com.reign.gcld.common.*;

@Component("resourceService")
public class ResourceService implements IResourceService
{
    @Autowired
    private IBuildingOutputCache buildingOutputCache;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private IGeneralService generalService;
    @Autowired
    private IBattleDataCache battleDataCache;
    @Autowired
    private TroopCache troopCache;
    @Autowired
    private WorldCityCache worldCityCache;
    @Autowired
    private HallsCache hallsCache;
    @Autowired
    private IPlayerOfficeRelativeDao playerOfficeRelativeDao;
    @Autowired
    private IJuBenService juBenService;
    @Autowired
    private IDataGetter dataGetter;
    private static final Map<Integer, Integer> resourceLimitBuildingMap;
    
    static {
        (resourceLimitBuildingMap = new HashMap<Integer, Integer>()).put(1, 16);
        ResourceService.resourceLimitBuildingMap.put(2, 32);
        ResourceService.resourceLimitBuildingMap.put(3, 48);
        ResourceService.resourceLimitBuildingMap.put(4, 64);
    }
    
    @Transactional
    @Override
    public void output(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final Date nowDate = new Date();
        final PlayerResource pr = this.playerResourceDao.read(playerId);
        if (pr == null) {
            return;
        }
        final long nowDateTime = nowDate.getTime();
        final long updateTime = pr.getUpdateTime().getTime();
        final long interval = nowDateTime - updateTime;
        long ratio = interval / 10000L;
        if (ratio < 1L) {
            return;
        }
        ratio *= 10L;
        if (ratio >= 60L) {
            final List<ResourceDto> list = this.buildingOutput(playerId, ratio);
            this.playerResourceDao.updateResourceCareMax(playerId, list, new Date(pr.getUpdateTime().getTime() + 1000L * ratio), "\u5efa\u7b51\u4ea7\u51fa", pr, true);
            this.pushData(playerId, list, false);
            this.checkReputationInfo(playerId);
        }
        this.dealTroop(playerDto, ratio);
    }
    
    private void checkReputationInfo(final int playerId) {
        try {
            final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerId);
            if (por != null) {
                final int priId = (por.getLastOfficerId() == null) ? 0 : por.getLastOfficerId();
                final long reputationTime = (por.getReputationTime() == null) ? System.currentTimeMillis() : por.getReputationTime().getTime();
                final Halls halls = (Halls)this.hallsCache.get((Object)priId);
                if (halls != null) {
                    final int officerId = halls.getOfficialId();
                    if (officerId > 5 || reputationTime < System.currentTimeMillis()) {
                        this.playerOfficeRelativeDao.updateReputationTime(playerId, null, 0);
                        this.buildingOutputCache.clearOfficer(playerId);
                    }
                }
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error("checkReputationInfo exception...playerId:" + playerId, e);
        }
    }
    
    private List<ResourceDto> buildingOutput(final int playerId, final long ratio) {
        final List<ResourceDto> list = new ArrayList<ResourceDto>();
        for (int i = 1; i <= 4; ++i) {
            final int buildingOutput = this.buildingOutputCache.getBuildingsOutput(playerId, i);
            final double secOutput = buildingOutput * 1.0 / 3600.0;
            final long maxValue = this.getMax(playerId, i);
            final double value = secOutput * ratio;
            final ResourceDto rd = new ResourceDto(i, value, maxValue);
            list.add(rd);
        }
        return list;
    }
    
    @Override
    public List<ResourceDto> buildingOutputPerHour(final int playerId) {
        final List<ResourceDto> list = new ArrayList<ResourceDto>();
        for (int i = 1; i <= 4; ++i) {
            final int buildingOutput = this.buildingOutputCache.getBuildingsOutput(playerId, i);
            final long maxValue = this.getMax(playerId, i);
            final double value = buildingOutput;
            final ResourceDto rd = new ResourceDto(i, value, maxValue);
            list.add(rd);
        }
        return list;
    }
    
    @Override
    public void dealTroop(final PlayerDto dto, final long ratio) {
        final List<PlayerGeneralMilitary> pgmList = this.playerGeneralMilitaryDao.getMilitaryByState(dto.playerId, 1);
        if (pgmList.size() < 1) {
            return;
        }
        final Date date = new Date();
        final PlayerResource playerResource = this.playerResourceDao.read(dto.playerId);
        int resource = 0;
        final Map<Integer, Long> forces = new HashMap<Integer, Long>();
        int generalId = 0;
        int rate = 0;
        boolean isInNormalJuben = false;
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(dto.playerId);
        if (juBenDto != null && !this.juBenService.isInWorldDrama(dto.playerId)) {
            isInNormalJuben = true;
        }
        final Map<Integer, GeneralOutPut> gopMap = new HashMap<Integer, GeneralOutPut>();
        for (int i = 0; i < pgmList.size(); ++i) {
            final PlayerGeneralMilitary pg = pgmList.get(i);
            generalId = pg.getGeneralId();
            final WorldCity worldCity = (WorldCity)this.worldCityCache.get((Object)pg.getLocationId());
            rate = this.generalService.getRate(dto.forceId, worldCity);
            final long time = (date.getTime() - pg.getUpdateForcesTime().getTime()) / 1000L;
            final General general = (General)this.generalCache.get((Object)generalId);
            final Troop troop = this.troopCache.getTroop(general.getTroop(), dto.playerId);
            double output = this.getOutput(dto.playerId) * rate / 100.0;
            final int secOutput = (int)output;
            final double comsume = this.generalService.getRecuitConsume(troop.getId(), dto.forceId, worldCity);
            output *= time;
            final int maxHp = this.battleDataCache.getMaxHp(pg);
            if (pg.getForces() + output >= maxHp) {
                output = maxHp - pg.getForces();
            }
            else if (ratio < 60L && maxHp - pg.getForces() > secOutput * 60) {
                continue;
            }
            if (output >= 1.0) {
                final int consumeFood = (int)(comsume * output);
                final GeneralOutPut gop = new GeneralOutPut();
                gop.consumeFood = consumeFood;
                gop.output = output;
                gop.maxHp = maxHp;
                if (resource + consumeFood <= playerResource.getFood()) {
                    resource += consumeFood;
                    gop.canConsume = true;
                }
                else if (isInNormalJuben) {
                    gop.canConsume = true;
                }
                else {
                    gop.canConsume = false;
                }
                gopMap.put(pg.getGeneralId(), gop);
            }
        }
        if (resource > 0 && !isInNormalJuben) {
            this.playerResourceDao.consumeFood(dto.playerId, resource, "\u52df\u5175\u6d88\u8017\u8d44\u6e90");
        }
        for (int i = 0; i < pgmList.size(); ++i) {
            final PlayerGeneralMilitary pg = pgmList.get(i);
            final GeneralOutPut gop2 = gopMap.get(pg.getGeneralId());
            if (gop2 != null) {
                long realValue = 0L;
                if (gop2.canConsume) {
                    this.playerGeneralMilitaryDao.updateForcesDate(dto.playerId, pg.getGeneralId(), date, gop2.output, gop2.maxHp);
                    realValue = (int)(pg.getForces() + gop2.output);
                    forces.put(pg.getGeneralId(), realValue);
                    if (realValue >= gop2.maxHp) {
                        this.dataGetter.getAutoBattleService().recoverAutoBattleAfterMuBing(pg.getPlayerId(), pg.getGeneralId());
                        final int result = this.playerGeneralMilitaryDao.updateState(pg.getVId(), date, 0, 1);
                        if (result == 1) {
                            TaskMessageHelper.sendFullBloodTaskMessage(dto.playerId);
                            this.generalService.sendGeneralMilitaryRecruitInfo(dto.playerId, pg.getGeneralId());
                        }
                    }
                }
                else {
                    this.playerResourceDao.pushIncenseData(dto.playerId, 3);
                    final int result = this.playerGeneralMilitaryDao.updateStateAuto(pg.getVId(), date, 0, 1, 1);
                    if (result == 1) {
                        this.generalService.sendGmStateSet(dto.playerId, pg.getGeneralId(), 0);
                    }
                }
            }
        }
        this.generalService.sendGmForcesSet(dto.playerId, forces);
    }
    
    @Override
    public Double getOutput(final int playerId) {
        final int output = this.buildingOutputCache.getBuildingsOutput(playerId, 5);
        return output * 1.0 / 3600.0;
    }
    
    private void pushData(final int playerId, final List<ResourceDto> list, final boolean flag) {
        final PlayerResource newPr = this.playerResourceDao.read(playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("outputInfo");
        for (int i = 0; i < list.size(); ++i) {
            final ResourceDto rd = list.get(i);
            doc.startObject();
            doc.createElement("buildingType", rd.getType());
            doc.createElement("output", (int)rd.getValue());
            doc.createElement("realValue", this.getResourceByType(newPr, rd.getType()));
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("isChangeDisplaye", flag);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_BUILDING_OUTPUT, doc.toByte());
    }
    
    private long getResourceByType(final PlayerResource pr, final int type) {
        long value = 0L;
        switch (type) {
            case 1: {
                value = pr.getCopper();
                break;
            }
            case 2: {
                value = pr.getWood();
                break;
            }
            case 3: {
                value = pr.getFood();
                break;
            }
            case 4: {
                value = pr.getIron();
                break;
            }
        }
        return value;
    }
    
    @Override
    public long getMax(final int playerId, final int type) {
        int max = 0;
        switch (type) {
            case 1: {
                max = this.buildingOutputCache.getBuildingOutput(playerId, 16);
                break;
            }
            case 2: {
                max = this.buildingOutputCache.getBuildingOutput(playerId, 32);
                break;
            }
            case 3: {
                max = this.buildingOutputCache.getBuildingOutput(playerId, 48);
                break;
            }
            case 4: {
                max = this.buildingOutputCache.getBuildingOutput(playerId, 64);
                break;
            }
        }
        return max;
    }
    
    @Override
    public Tuple<Boolean, String> canAddResource(final int resourceType, final int playerId) {
        final Tuple<Boolean, String> result = new Tuple();
        final PlayerResource pr = this.playerResourceDao.read(playerId);
        long max = 0L;
        long nowCount = -1L;
        String message = "";
        switch (resourceType) {
            case 1: {
                max = this.buildingOutputCache.getBuildingOutput(playerId, 16);
                nowCount = pr.getCopper();
                message = LocalMessages.RESOURCE_COPEER_TOP;
                break;
            }
            case 2: {
                max = this.buildingOutputCache.getBuildingOutput(playerId, 32);
                nowCount = pr.getWood();
                message = LocalMessages.RESOURCE_WOOD_TOP;
                break;
            }
            case 3: {
                max = this.buildingOutputCache.getBuildingOutput(playerId, 48);
                nowCount = pr.getFood();
                message = LocalMessages.RESOURCE_FOOD_TOP;
                break;
            }
            case 4: {
                max = this.buildingOutputCache.getBuildingOutput(playerId, 64);
                nowCount = pr.getIron();
                message = LocalMessages.RESOURCE_IRON_TOP;
                break;
            }
        }
        final boolean canAdd = max > nowCount;
        result.left = canAdd;
        result.right = message;
        return result;
    }
    
    @Override
    public void pushOutput(final int playerId) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("output");
        for (int i = 1; i <= 4; ++i) {
            doc.startObject();
            doc.createElement("outputType", i);
            doc.createElement("outputValue", this.buildingOutputCache.getBuildingsOutput(playerId, i));
            doc.createElement("outputMax", this.buildingOutputCache.getBuildingOutput(playerId, ResourceService.resourceLimitBuildingMap.get(i)));
            doc.endObject();
        }
        doc.startObject();
        doc.createElement("outputType", 5);
        doc.createElement("outputValue", this.buildingOutputCache.getBuildingsOutput(playerId, 5) / 60);
        doc.endObject();
        doc.endArray();
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
    }
}
