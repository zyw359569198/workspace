package com.reign.gcld.building.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.job.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.timer.dao.*;
import com.reign.gcld.politics.service.*;
import com.reign.gcld.general.service.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.building.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.politics.dao.*;
import com.reign.gcld.building.common.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.task.message.*;
import com.reign.gcld.common.event.*;
import org.springframework.transaction.annotation.*;
import com.reign.framework.json.*;
import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.tech.domain.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.player.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.timer.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.building.domain.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.rank.common.*;

@Component("buildingService")
public class BuildingService implements IBuildingService
{
    private static final Logger log;
    @Autowired
    private BuildingCache buildingCache;
    @Autowired
    private IPlayerBuildingDao playerBuildingDao;
    @Autowired
    private IPlayerBuildingWorkDao playerBuildingWorkDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private SerialCache serialCache;
    @Autowired
    private IJobService jobService;
    @Autowired
    private IBuildingOutputCache buildingOutputCache;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IPlayerJobDao playerJobDao;
    @Autowired
    private IPoliticsService politicsService;
    @Autowired
    private CCache cCache;
    @Autowired
    private ChargeitemCache chargeItemCache;
    @Autowired
    private IPlayerResourceAdditionDao playerResourceAdditionDao;
    @Autowired
    private HallsCache hallsCache;
    @Autowired
    private IGeneralService generalService;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private EventDailyCache eventDailyCache;
    @Autowired
    private IResourceService resourceService;
    @Autowired
    private IChatService chatService;
    @Autowired
    private IBluePrintDao bluePrintDao;
    @Autowired
    private BuildingDrawingCache buildingDrawingCache;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IPlayerPoliticsEventDao playerPoliticsEventDao;
    private static Map<Integer, AutoBuildingTimes> autoUPMap;
    private static Map<Integer, PlayerBuilding[]> pbMap;
    
    static {
        log = CommonLog.getLog(BuildingService.class);
        BuildingService.autoUPMap = new HashMap<Integer, AutoBuildingTimes>();
        BuildingService.pbMap = new HashMap<Integer, PlayerBuilding[]>();
    }
    
    public static void clearPlayerBuilding(final int playerId) {
        BuildingService.pbMap.remove(playerId);
    }
    
    @Override
    public void reloadBuilding(final int playerId, final int buildingId) {
        final PlayerBuilding[] pbs = BuildingService.pbMap.get(playerId);
        if (pbs == null) {
            return;
        }
        pbs[buildingId] = this.playerBuildingDao.getPlayerBuilding(playerId, buildingId);
    }
    
    @Override
    public void initPlayerBuilding(final int playerId) {
        final PlayerBuilding[] pbs = new PlayerBuilding[81];
        BuildingService.pbMap.put(playerId, pbs);
        final List<PlayerBuilding> pbList = this.playerBuildingDao.getPlayerBuildings(playerId);
        for (final PlayerBuilding pb : pbList) {
            pbs[pb.getBuildingId()] = pb;
        }
    }
    
    public int upgradeBuilding(final int playerId, final int buildingId, final int addLv, final int state, final int maxLv) {
        final PlayerBuilding[] pbs = BuildingService.pbMap.get(playerId);
        if (pbs == null) {
            return this.playerBuildingDao.upgradeBuilding(playerId, buildingId, addLv, 0, maxLv);
        }
        pbs[buildingId].setState(state);
        return this.playerBuildingDao.update(pbs[buildingId]);
    }
    
    public void upgradeBuilding(final int playerId, final int buildingId, final Date endTime) {
        final PlayerBuilding[] pbs = BuildingService.pbMap.get(playerId);
        if (pbs != null) {
            pbs[buildingId].setUpdateTime(endTime);
        }
    }
    
    public void upgradeBuildingState(final int playerId, final int buildingId, final int state, final int speedUpNum, final Date endTime) {
        final PlayerBuilding[] pbs = BuildingService.pbMap.get(playerId);
        if (pbs == null) {
            this.playerBuildingDao.upgradeBuildingState(playerId, buildingId, state, speedUpNum);
            return;
        }
        pbs[buildingId].setState(state);
        pbs[buildingId].setSpeedUpNum(speedUpNum);
        pbs[buildingId].setUpdateTime(endTime);
        this.playerBuildingDao.update(pbs[buildingId]);
    }
    
    private void resetSpeedNums(final int playerId, final int buildingId) {
        final PlayerBuilding[] pbs = BuildingService.pbMap.get(playerId);
        if (pbs == null) {
            return;
        }
        final Building building = (Building)this.buildingCache.get((Object)buildingId);
        final int speedUpNum = this.serialCache.get(building.getTimeT(), 1);
        pbs[buildingId].setSpeedUpNum(speedUpNum);
    }
    
    private void setSpeedNums(final int playerId, final int buildingId) {
        final PlayerBuilding[] pbs = BuildingService.pbMap.get(playerId);
        if (pbs == null) {
            return;
        }
        pbs[buildingId].setSpeedUpNum(0);
    }
    
    private boolean reduceSpeedNums(final int playerId, final int buildingId) {
        final PlayerBuilding[] pbs = BuildingService.pbMap.get(playerId);
        if (pbs == null) {
            return false;
        }
        if (pbs[buildingId].getSpeedUpNum() > 0) {
            pbs[buildingId].setSpeedUpNum(pbs[buildingId].getSpeedUpNum() - 1);
            return true;
        }
        return false;
    }
    
    public void updateBuildingNewState(final int playerId, final int buildingId, final int isNew) {
        final PlayerBuilding[] pbs = BuildingService.pbMap.get(playerId);
        if (pbs == null) {
            this.playerBuildingDao.updateBuildingNewState(playerId, buildingId, isNew);
            return;
        }
        pbs[buildingId].setIsNew(isNew);
        this.playerBuildingDao.update(pbs[buildingId]);
    }
    
    @Override
    public void updateEventId(final PlayerBuilding pb, final int eventId) {
        final PlayerBuilding[] pbs = BuildingService.pbMap.get(pb.getPlayerId());
        if (pbs == null) {
            this.playerBuildingDao.updateEventId(pb, eventId);
            return;
        }
        pbs[pb.getBuildingId()].setEventId(eventId);
        this.playerBuildingDao.update(pbs[pb.getBuildingId()]);
    }
    
    @Override
    public PlayerBuilding getPlayerBuilding(final int playerId, final int buildingId) {
        final PlayerBuilding[] pbs = BuildingService.pbMap.get(playerId);
        if (pbs == null) {
            return this.playerBuildingDao.getPlayerBuilding(playerId, buildingId);
        }
        return pbs[buildingId];
    }
    
    @Override
    public List<PlayerBuilding> getPlayerBuildingWithoutEvent(final int playerId) {
        final PlayerBuilding[] pbs = BuildingService.pbMap.get(playerId);
        if (pbs == null) {
            return this.playerBuildingDao.getPlayerBuildingWithoutEvent(playerId);
        }
        final List<PlayerBuilding> list = new ArrayList<PlayerBuilding>();
        PlayerBuilding[] array;
        for (int length = (array = pbs).length, i = 0; i < length; ++i) {
            final PlayerBuilding pb = array[i];
            if (pb != null) {
                if (pb.getEventId() == 0) {
                    list.add(pb);
                }
            }
        }
        return list;
    }
    
    @Override
    public PlayerBuilding getNextBuildingWithEvent(final int playerId, final int buildingId) {
        final PlayerBuilding[] pbs = BuildingService.pbMap.get(playerId);
        if (pbs == null) {
            return this.playerBuildingDao.getNextBuildingWithEvent(playerId, buildingId);
        }
        PlayerBuilding[] array;
        for (int length = (array = pbs).length, i = 0; i < length; ++i) {
            final PlayerBuilding pb = array[i];
            if (pb != null) {
                if (pb.getEventId() > 0 && pb.getBuildingId() != buildingId) {
                    return pb;
                }
            }
        }
        return null;
    }
    
    @Override
    public List<PlayerBuilding> getPlayerBuildingWithoutEvent2(final int playerId, final int areaId) {
        final PlayerBuilding[] pbs = BuildingService.pbMap.get(playerId);
        if (pbs == null) {
            return this.playerBuildingDao.getPlayerBuildingWithoutEvent2(playerId, areaId);
        }
        final List<PlayerBuilding> list = new ArrayList<PlayerBuilding>();
        PlayerBuilding[] array;
        for (int length = (array = pbs).length, i = 0; i < length; ++i) {
            final PlayerBuilding pb = array[i];
            if (pb != null) {
                if (pb.getEventId() == 0 && pb.getAreaId() == areaId) {
                    list.add(pb);
                }
            }
        }
        return list;
    }
    
    @Override
    public List<PlayerBuilding> getPlayerBuildingByType(final int playerId, final int outputType) {
        final PlayerBuilding[] pbs = BuildingService.pbMap.get(playerId);
        if (pbs == null) {
            return this.playerBuildingDao.getPlayerBuildingByType(playerId, outputType);
        }
        final List<PlayerBuilding> list = new ArrayList<PlayerBuilding>();
        PlayerBuilding[] array;
        for (int length = (array = pbs).length, i = 0; i < length; ++i) {
            final PlayerBuilding pb = array[i];
            if (pb != null) {
                if (pb.getOutputType() == outputType) {
                    list.add(pb);
                }
            }
        }
        return list;
    }
    
    @Override
    public List<PlayerBuilding> getPlayerBuildings(final int playerId, final int areaId) {
        final PlayerBuilding[] pbs = BuildingService.pbMap.get(playerId);
        if (pbs == null) {
            return this.playerBuildingDao.getPlayerBuildingByAreaId(playerId, areaId);
        }
        final List<PlayerBuilding> list = new ArrayList<PlayerBuilding>();
        final int startIdx = (areaId - 1) * 16 + 1;
        for (int endIdx = areaId * 16, i = startIdx; i <= endIdx; ++i) {
            if (pbs[i] != null) {
                list.add(pbs[i]);
            }
        }
        return list;
    }
    
    @Transactional
    @Override
    public void createBuilding(final int playerId, final int buildingId, final int isNew) {
        try {
            Constants.locks[playerId % Constants.LOCKS_LEN].lock();
            PlayerBuilding pb = this.getPlayerBuilding(playerId, buildingId);
            final Building building = (Building)this.buildingCache.get((Object)buildingId);
            if (building == null) {
                return;
            }
            if (pb == null) {
                final Date nowDate = new Date();
                pb = new PlayerBuilding();
                this.copyProperties(pb, building, isNew);
                pb.setPlayerId(playerId);
                pb.setUpdateTime(nowDate);
                this.playerBuildingDao.create(pb);
            }
            this.buildingOutputCache.clearBase(playerId, buildingId);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.startObject("player");
            if (pb.getBuildingId() == 16) {
                doc.createElement("copperMax", this.buildingOutputCache.getBuildingOutput(playerId, 16));
            }
            else if (pb.getBuildingId() == 48) {
                doc.createElement("foodMax", this.buildingOutputCache.getBuildingOutput(playerId, 48));
            }
            else if (pb.getBuildingId() == 32) {
                doc.createElement("woodMax", this.buildingOutputCache.getBuildingOutput(playerId, 32));
            }
            else if (pb.getBuildingId() == 64) {
                doc.createElement("ironMax", this.buildingOutputCache.getBuildingOutput(playerId, 64));
            }
            doc.endObject();
            doc.endObject();
            this.reloadBuilding(playerId, buildingId);
            this.resetSpeedNums(playerId, buildingId);
            TaskMessageHelper.sendBuildingTaskMessage(playerId, buildingId, pb.getLv());
            EventListener.fireEvent(new CommonEvent(11, playerId));
        }
        finally {
            Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
        }
        Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
    }
    
    @Override
    public void assignedBuildingWork(final int playerId) {
        final Date nowDate = new Date();
        final PlayerBuildingWork pbw = new PlayerBuildingWork();
        pbw.setPlayerId(playerId);
        pbw.setStartTime(nowDate);
        pbw.setEndTime(nowDate);
        pbw.setTargetBuildId(0);
        pbw.setWorkId(1);
        pbw.setWorkState(0);
        this.playerBuildingWorkDao.create(pbw);
    }
    
    private int getMinLv(final List<PlayerBuilding> buildings, final Set<Integer> set, final int playerLv) {
        int buildingId = 0;
        int minLv = 9999;
        for (final PlayerBuilding pb : buildings) {
            if (pb.getLv() < minLv && !set.contains(pb.getBuildingId())) {
                minLv = pb.getLv();
                buildingId = pb.getBuildingId();
            }
        }
        if (minLv >= playerLv) {
            buildingId = 0;
        }
        return buildingId;
    }
    
    @Override
    public byte[] stopAutoUpBuilding(final int playerId) {
        try {
            Constants.locks[playerId % Constants.LOCKS_LEN].lock();
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            BuildingService.autoUPMap.remove(playerId);
            doc.createElement("times", 5);
            doc.createElement("areaId", 0);
            doc.createElement("state", 0);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        finally {
            Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
        }
    }
    
    @Override
    public byte[] startAutoUpBuilding(final PlayerDto playerDto, final int type) {
        final int playerId = playerDto.playerId;
        final int playerLv = playerDto.playerLv;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[21] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final Player player = this.playerDao.read(playerId);
        if (player.getConsumeLv() < 1 && player.getPlayerLv() > 30) {
            doc.createElement("prompt", (Object)LocalMessages.T_BUILDING_10008);
            doc.createElement("url", PayUtil.getAbsolutePayUrl(playerDto.yx, player.getUserId(), player.getPlayerId()));
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        try {
            Constants.locks[playerId % Constants.LOCKS_LEN].lock();
            if (type <= 0 || type > 5) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            final List<PlayerBuilding> buildings = this.getPlayerBuildings(playerId, type);
            final List<PlayerBuildingWork> buildingWorks = this.playerBuildingWorkDao.getPlayerBuildingWork(playerId);
            final Set<Integer> set = new HashSet<Integer>();
            for (final PlayerBuildingWork pbw : buildingWorks) {
                if (pbw.getWorkState() == 1) {
                    set.add(pbw.getTargetBuildId());
                }
            }
            final int times = buildingWorks.size() * 5;
            final AutoBuildingTimes abt = new AutoBuildingTimes(type, times, times, System.currentTimeMillis());
            BuildingService.autoUPMap.put(playerId, abt);
            Tuple<Integer, byte[]> tuple = null;
            int bId = 0;
            int s_times = abt.exeTimes;
            int s_areaId = abt.type;
            int s_state = 1;
            while (abt.exeTimes > 0) {
                bId = this.getMinLv(buildings, set, playerLv);
                if (bId == 0) {
                    if (set.size() <= 0) {
                        s_times = times;
                        s_areaId = 0;
                        s_state = 0;
                        BuildingService.autoUPMap.remove(playerId);
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10007);
                    }
                    break;
                }
                else {
                    tuple = this.upgradeBuilding(playerId, bId, true);
                    if (tuple.left == 3) {
                        if (set.size() <= 0) {
                            s_times = times;
                            s_areaId = 0;
                            s_state = 0;
                            BuildingService.autoUPMap.remove(playerId);
                            return tuple.right;
                        }
                        s_times = 0;
                        break;
                    }
                    else {
                        if (tuple.left != 1) {
                            break;
                        }
                        s_state = 1;
                        --abt.exeTimes;
                        s_times = abt.exeTimes;
                        set.add(bId);
                    }
                }
            }
            bId = this.getMinLv(buildings, set, playerLv);
            if (s_times > 0) {
                doc.createElement("times", s_times);
                doc.createElement("areaId", s_areaId);
                doc.createElement("state", s_state);
            }
            else {
                doc.createElement("times", times);
                doc.createElement("areaId", 0);
                doc.createElement("state", 0);
                BuildingService.autoUPMap.remove(playerId);
            }
            doc.endObject();
            this.pushShakeFreeCons(playerId);
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        finally {
            Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
        }
    }
    
    @Override
    public byte[] getAutoUpbuilding(final int playerId) {
        try {
            Constants.locks[playerId % Constants.LOCKS_LEN].lock();
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            final AutoBuildingTimes abt = BuildingService.autoUPMap.get(playerId);
            if (abt != null) {
                doc.createElement("times", abt.exeTimes);
                doc.createElement("areaId", abt.type);
                doc.createElement("state", 1);
            }
            else {
                doc.createElement("times", 5);
                doc.createElement("areaId", 0);
                doc.createElement("state", 0);
            }
            doc.endObject();
            return doc.toByte();
        }
        finally {
            Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
        }
    }
    
    @Override
    public Tuple<Integer, byte[]> upgradeBuilding(final int playerId, final int buildingId, final boolean isAuto) {
        try {
            Constants.locks[playerId % Constants.LOCKS_LEN].lock();
            final Tuple<Integer, byte[]> tuple = new Tuple();
            tuple.left = 0;
            final Player player = this.playerDao.read(playerId);
            final PlayerBuilding pb = this.getPlayerBuilding(playerId, buildingId);
            if (pb == null) {
                tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
                return tuple;
            }
            if (pb.getLv() >= player.getPlayerLv()) {
                tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10001);
                return tuple;
            }
            final Date nowDate = new Date();
            PlayerBuildingWork pbw = null;
            final List<PlayerBuildingWork> buildingWorks = this.playerBuildingWorkDao.getPlayerBuildingWork(playerId);
            for (int i = buildingWorks.size() - 1; i >= 0; --i) {
                final PlayerBuildingWork tpbw = buildingWorks.get(i);
                if (tpbw.getTargetBuildId() == buildingId) {
                    tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10006);
                    return tuple;
                }
                if (tpbw.getWorkState() == 0) {
                    pbw = tpbw;
                }
            }
            if (pbw == null) {
                tuple.left = 2;
                tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10002);
                return tuple;
            }
            final Building building = (Building)this.buildingCache.get((Object)buildingId);
            final int copper = this.getBuildingUpradeCopperCost(building, pb.getLv() + 1);
            final int wood = this.getBuildingUpradeWoodCost(building, pb.getLv() + 1);
            final PlayerResource playerResource = this.playerResourceDao.read(playerId);
            if (playerResource.getCopper() < copper) {
                tuple.left = 3;
                tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
                return tuple;
            }
            if (playerResource.getWood() < wood) {
                tuple.left = 3;
                tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10008);
                return tuple;
            }
            if (!this.playerResourceDao.consumeCopper(playerId, copper, "\u5efa\u7b51\u5347\u7ea7")) {
                tuple.left = 3;
                tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
                return tuple;
            }
            if (!this.playerResourceDao.consumeWood(playerId, wood, "\u5efa\u7b51\u5347\u7ea7")) {
                tuple.left = 3;
                tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10008);
                return tuple;
            }
            final int t = this.getBuildingUpgradeTime(building, pb.getLv() + 1);
            this.upgradeBuildingState(playerId, buildingId, 1, this.serialCache.get(building.getTimeT(), pb.getLv()), new Date(nowDate.getTime() + t));
            this.resetSpeedNums(playerId, buildingId);
            pbw.setEndTime(new Date(nowDate.getTime() + t));
            pbw.setStartTime(nowDate);
            pbw.setTargetBuildId(buildingId);
            pbw.setWorkState(1);
            final int taskId = this.jobService.addJob("buildingService", "doUpgrade", getParams(playerId, buildingId, pbw.getWorkId(), isAuto, pb.getLv()), pbw.getEndTime().getTime());
            pbw.setTaskId(taskId);
            this.playerBuildingWorkDao.assignedWork(pbw);
            tuple.left = 1;
            if (isAuto) {
                tuple.right = JsonBuilder.getJson(State.SUCCESS, "");
            }
            else {
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("type", pb.getAreaId());
                doc.endObject();
                tuple.right = JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
            this.pushShakeFreeCons(playerId);
            if (this.dataGetter.getPlayerAttributeDao().getFunctionId(playerId).toCharArray()[32] == '1') {
                final PlayerTech pt = this.dataGetter.getPlayerTechDao().getPlayerTech(playerId, 502);
                if (pt != null && pt.getStatus() == 5) {
                    final int feat = this.calFeat(pb.getLv());
                    if (feat > 0) {
                        this.dataGetter.getFeatBuildingDao().addFeat(playerId, feat);
                    }
                }
            }
            return tuple;
        }
        finally {
            Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
        }
    }
    
    private int calFeat(final int bLv) {
        if (bLv > 30) {
            return bLv / 5;
        }
        if (bLv % 5 == 0) {
            return 5;
        }
        return 0;
    }
    
    @Override
    public CallBack doUpgrade(final String param) {
        final Object[] params = parseParams(param, 2);
        final int playerId = (int)params[0];
        final int buildingId = (int)params[1];
        final int workId = (int)params[2];
        final int lv = (int)params[4];
        try {
            Constants.locks[playerId % Constants.LOCKS_LEN].lock();
            final Player player = this.playerDao.read(playerId);
            final Building building = (Building)this.buildingCache.get((Object)buildingId);
            final PlayerBuilding pb = this.getPlayerBuilding(playerId, buildingId);
            if (pb.getLv() <= lv && player.getPlayerLv() > pb.getLv()) {
                pb.setLv(pb.getLv() + 1);
                final int addNum = (int)Math.floor(building.getChiefExpE() * this.serialCache.get(building.getChiefExpS(), pb.getLv()));
                final AddExpInfo aei = this.playerService.updateExpAndPlayerLevel(playerId, addNum, "\u5efa\u7b51\u5347\u7ea7\u589e\u52a0\u7ecf\u9a8c");
                final int res = this.upgradeBuilding(playerId, buildingId, 1, 0, player.getPlayerLv());
                if (res < 1) {
                    BuildingService.log.error("buildingService building Lv update DB fail over playerLv");
                    pb.setLv(player.getPlayerLv());
                }
                this.setSpeedNums(playerId, buildingId);
                final int num = aei.addExp;
                this.playerBuildingWorkDao.resetBuildingWork(playerId, workId);
                final int outputValue = this.buildingOutputCache.getBuildingOutput(playerId, buildingId);
                final JsonDocument buildingDoc = new JsonDocument();
                final AutoBuildingTimes abt = BuildingService.autoUPMap.get(playerId);
                int s_areaId = 0;
                int s_state = 0;
                int s_times = 5;
                if (abt != null) {
                    if (abt.exeTimes <= 0) {
                        BuildingService.autoUPMap.remove(playerId);
                    }
                    else {
                        final List<PlayerBuildingWork> buildingWorks = this.playerBuildingWorkDao.getPlayerBuildingWork(playerId);
                        s_areaId = abt.type;
                        s_times = abt.exeTimes;
                        s_state = 1;
                        if (Players.getSession(Integer.valueOf(playerId)) == null) {
                            BuildingService.autoUPMap.remove(playerId);
                            return null;
                        }
                        Tuple<Integer, byte[]> tuple = null;
                        if (s_areaId == 0) {
                            BuildingService.log.error("BuildingService--doUpgrade playerId " + playerId + ", abt.type " + abt.type + " abt.exeTimes " + abt.exeTimes);
                            BuildingService.autoUPMap.remove(playerId);
                            return null;
                        }
                        final List<PlayerBuilding> buildings = this.getPlayerBuildings(playerId, s_areaId);
                        final Set<Integer> set = new HashSet<Integer>();
                        for (final PlayerBuildingWork pbw : buildingWorks) {
                            if (pbw.getTargetBuildId() > 0) {
                                set.add(pbw.getTargetBuildId());
                            }
                        }
                        while (abt.exeTimes > 0) {
                            final int bId = this.getMinLv(buildings, set, player.getPlayerLv());
                            if (bId == 0) {
                                if (set.size() <= 0) {
                                    s_areaId = 0;
                                    s_state = 0;
                                    s_times = buildingWorks.size() * 5;
                                    BuildingService.autoUPMap.remove(playerId);
                                    break;
                                }
                                break;
                            }
                            else {
                                tuple = this.upgradeBuilding(playerId, bId, true);
                                if (tuple.left == 3) {
                                    if (set.size() <= 0) {
                                        s_areaId = 0;
                                        s_state = 0;
                                        s_times = buildingWorks.size() * 5;
                                        BuildingService.autoUPMap.remove(playerId);
                                        break;
                                    }
                                    break;
                                }
                                else {
                                    if (tuple.left != 1) {
                                        break;
                                    }
                                    s_state = 1;
                                    --abt.exeTimes;
                                    s_times = abt.exeTimes;
                                    set.add(bId);
                                }
                            }
                        }
                    }
                    buildingDoc.startObject("autoUpbuilding");
                    buildingDoc.createElement("times", s_times);
                    buildingDoc.createElement("areaId", s_areaId);
                    buildingDoc.createElement("state", s_state);
                    buildingDoc.endObject();
                }
                final CallBack callBack = new CallBack() {
                    @Override
                    public void call() {
                        final JsonDocument doc = new JsonDocument();
                        doc.startObject();
                        doc.createElement("id", buildingId);
                        doc.createElement("lv", pb.getLv());
                        doc.createElement("areaId", pb.getAreaId());
                        doc.createElement("exp", num);
                        final int newOutput = BuildingService.this.buildingOutputCache.getBuildingOutput(playerId, buildingId);
                        boolean add = false;
                        doc.startObject("player");
                        if (pb.getBuildingId() == 16) {
                            doc.createElement("copperMax", BuildingService.this.buildingOutputCache.getBuildingOutput(playerId, 16));
                        }
                        else if (pb.getBuildingId() == 48) {
                            doc.createElement("foodMax", BuildingService.this.buildingOutputCache.getBuildingOutput(playerId, 48));
                        }
                        else if (pb.getBuildingId() == 32) {
                            doc.createElement("woodMax", BuildingService.this.buildingOutputCache.getBuildingOutput(playerId, 32));
                        }
                        else if (pb.getBuildingId() == 64) {
                            doc.createElement("ironMax", BuildingService.this.buildingOutputCache.getBuildingOutput(playerId, 64));
                        }
                        else {
                            add = true;
                        }
                        if (pb.getOutputType() == 1) {
                            doc.createElement("copperOutput", BuildingService.this.buildingOutputCache.getBuildingsOutput(playerId, 1));
                        }
                        else if (pb.getOutputType() == 2) {
                            doc.createElement("foodOutput", BuildingService.this.buildingOutputCache.getBuildingsOutput(playerId, 2));
                        }
                        else if (pb.getOutputType() == 3) {
                            doc.createElement("woodOutput", BuildingService.this.buildingOutputCache.getBuildingsOutput(playerId, 3));
                        }
                        else if (pb.getOutputType() == 4) {
                            doc.createElement("ironOutput", BuildingService.this.buildingOutputCache.getBuildingsOutput(playerId, 4));
                        }
                        doc.endObject();
                        if (add) {
                            doc.createElement("addOutput", newOutput - outputValue);
                        }
                        else {
                            doc.createElement("addOutput", 0);
                        }
                        final int output = BuildingService.this.buildingOutputCache.getBuildingsOutput(playerId, pb.getOutputType());
                        if (pb.getOutputType() == 5) {
                            BuildingService.this.generalService.sendGeneralMilitaryRecruitInfo(playerId, true);
                        }
                        TaskMessageHelper.sendResourceTaskMessage(playerId, pb.getOutputType(), output);
                        if (!buildingDoc.toString().equals("")) {
                            doc.appendJson(buildingDoc.toByte());
                        }
                        doc.endObject();
                        Players.push(playerId, PushCommand.PUSH_BUILDING_UPGRADE, doc.toByte());
                        BuildingService.this.resourceService.pushOutput(playerId);
                    }
                };
                TaskMessageHelper.sendBuildingTaskMessage(playerId, buildingId, pb.getLv());
                this.buildingOutputCache.clearBase(playerId, buildingId);
                final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
                if (cs[22] == '1') {
                    final double rate = Math.random();
                    final double dropTech = ((C)this.cCache.get((Object)"EventDaily.Base.Prob")).getValue() + this.techEffectCache.getTechEffect3(playerId, 29);
                    if (rate <= dropTech && this.playerPoliticsEventDao.getEventNum(playerId) < 24) {
                        final List<PlayerBuilding> list = this.playerBuildingDao.getPlayerBuildingWithoutEvent2(playerId, pb.getAreaId());
                        if (list.size() > 0) {
                            final PlayerBuilding pb2 = list.get(WebUtil.nextInt(list.size()));
                            if (pb2 != null) {
                                final List<EventDaily> eventList = this.eventDailyCache.getEventDailyMap(pb2.getAreaId());
                                if (eventList != null) {
                                    final int eventIndex = WebUtil.nextInt(eventList.size());
                                    final int eventId = eventList.get(eventIndex).getId();
                                    this.updateEventId(pb2, eventId);
                                    this.playerPoliticsEventDao.addPoliticEventNum(playerId, 1, new Date());
                                }
                            }
                        }
                    }
                }
                return callBack;
            }
            return null;
        }
        catch (Exception e) {
            e.printStackTrace();
            final int result = this.playerBuildingWorkDao.resetBuildingWork(playerId, workId);
            BuildingService.log.debug("doExeUpgrade building exception" + result);
        }
        finally {
            Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
        }
        return null;
    }
    
    @Override
    public byte[] getBuildingInfo(final int playerId, final int areaId) {
        if (areaId <= 0 || areaId > 5) {
            BuildingService.log.error("BuildingService--getBuildingInfo playerId " + playerId + ", areaId " + areaId);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final List<PlayerBuilding> buildings = this.getPlayerBuildings(playerId, areaId);
        final Player player = this.playerDao.read(playerId);
        TaskMessageHelper.sendVisitAreaTaskMessage(playerId, areaId);
        this.sendFirstEnterEvent(player.getPlayerId(), areaId);
        return JsonBuilder.getJson(State.SUCCESS, this.getResult(player, buildings, areaId));
    }
    
    private void sendFirstEnterEvent(final int playerId, final int areaId) {
        switch (areaId) {
            case 1: {
                EventListener.fireEvent(new CommonEvent(15, playerId));
                break;
            }
            case 2: {
                EventListener.fireEvent(new CommonEvent(18, playerId));
                break;
            }
            case 3: {
                EventListener.fireEvent(new CommonEvent(19, playerId));
                break;
            }
            case 4: {
                EventListener.fireEvent(new CommonEvent(20, playerId));
                break;
            }
            case 5: {
                EventListener.fireEvent(new CommonEvent(21, playerId));
                break;
            }
        }
    }
    
    @Override
    public byte[] getMainCity(final int playerId) {
        final Date nowDate = new Date();
        TaskMessageHelper.sendVisitAreaTaskMessage(playerId, 0);
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        final char[] ns = pa.getIsAreaNew().toCharArray();
        final Map<Integer, Integer> map = this.politicsService.getBuildingTypeWithEvent(playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("areas");
        boolean isOpen = false;
        boolean isNew = false;
        int state = 0;
        boolean update = false;
        final List<PlayerResourceAddition> list = this.playerResourceAdditionDao.getListByPlayerId(playerId);
        for (int i = 1; i <= 5; ++i) {
            doc.startObject();
            doc.createElement("id", i);
            state = this.getState(i);
            isOpen = (cs[state] == '1');
            doc.createElement("isOpen", isOpen);
            if (isOpen) {
                isNew = (ns[i - 1] == '0');
                if (isNew) {
                    doc.createElement("isNew", isNew);
                    ns[i - 1] = '1';
                    update = true;
                }
            }
            if (map.containsKey(i)) {
                doc.createElement("hasEvent", true);
                doc.createElement("eventNum", map.get(i));
            }
            else {
                doc.createElement("hasEvent", false);
            }
            PlayerResourceAddition pra = null;
            for (final PlayerResourceAddition temp : list) {
                if (temp.getResourceType() == i) {
                    pra = temp;
                    break;
                }
            }
            if (pra != null && pra.getEndTime().after(nowDate)) {
                doc.createElement("additionMode", pra.getAdditionMode());
                doc.createElement("additionRate", ((Chargeitem)this.chargeItemCache.get((Object)this.getId(i, pra.getAdditionMode()))).getParam());
                doc.createElement("additionCd", CDUtil.getCD(pra.getEndTime(), nowDate));
            }
            else {
                doc.createElement("additionMode", 0);
                doc.createElement("additionRate", 1);
                doc.createElement("additionCd", 0);
            }
            doc.startArray("totalOutput");
            doc.startObject();
            doc.createElement("type", i);
            int output = this.buildingOutputCache.getBuildingsOutput(playerId, i);
            if (i == 5) {
                output = (int)Math.ceil(output / 60.0);
            }
            doc.createElement("output", output);
            if (3 == i) {
                if (output >= 800) {
                    EventListener.fireEvent(new CommonEvent(29, playerId));
                }
            }
            else if (5 == i) {
                if (output >= 60000) {
                    EventListener.fireEvent(new CommonEvent(33, playerId));
                }
                else if (output >= 52000) {
                    EventListener.fireEvent(new CommonEvent(30, playerId));
                }
            }
            doc.endObject();
            doc.endArray();
            doc.endObject();
        }
        doc.startObject();
        doc.createElement("id", 6);
        state = this.getState(6);
        isOpen = (cs[state] == '1');
        doc.createElement("isOpen", isOpen);
        if (isOpen) {
            isNew = (ns[5] == '0');
            if (isNew) {
                doc.createElement("isNew", isNew);
                ns[5] = '1';
                update = true;
            }
        }
        final PlayerOfficeRelative por = this.dataGetter.getPlayerOfficeRelativeDao().read(playerId);
        final int officerId = (por == null) ? 37 : por.getOfficerId();
        final Halls mineHalls = (Halls)this.hallsCache.get((Object)officerId);
        if (mineHalls == null) {
            doc.createElement("addition", 0);
        }
        else {
            final double techEffect = this.techEffectCache.getTechEffect(playerId, 22) / 100.0;
            doc.createElement("addition", (int)Math.ceil(mineHalls.getOutput() * (1.0 + techEffect)));
        }
        doc.startArray("officerOutput");
        for (int j = 1; j <= 4; ++j) {
            doc.startObject();
            doc.createElement("type", j);
            doc.createElement("value", this.buildingOutputCache.getOfficersOutput(playerId, j));
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        if (update) {
            this.playerAttributeDao.setIsNewArea(playerId, new String(ns));
        }
        doc.endArray();
        EventListener.fireEvent(new CommonEvent(16, playerId));
        doc.createElement("troopLv", this.techEffectCache.getTechEffect(playerId, 28) + 1);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private int getState(final int type) {
        switch (type) {
            case 1: {
                return 0;
            }
            case 2: {
                return 5;
            }
            case 3: {
                return 6;
            }
            case 4: {
                return 7;
            }
            case 5: {
                return 8;
            }
            case 6: {
                return 14;
            }
            default: {
                return 0;
            }
        }
    }
    
    @Override
    public byte[] cdRecover(final int playerId, final int workId) {
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)5);
        final Player player = this.playerDao.read(playerId);
        if (ci.getLv() > player.getConsumeLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        int gold = 0;
        final Date nowDate = new Date();
        final PlayerBuildingWork pbw = this.playerBuildingWorkDao.getPlayerBuildingWork(playerId, workId);
        if (pbw == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        if (pbw.getWorkState() == 1) {
            gold = this.getCDRecoverCost(ci, pbw, nowDate);
        }
        return JsonBuilder.getJson(State.SUCCESS, "gold", (Object)gold);
    }
    
    @Override
    public Tuple<Boolean, Object> cdRecoverConfirm(final int playerId, final int workId) {
        final Tuple<Boolean, Object> tuple = new Tuple();
        tuple.left = false;
        try {
            Constants.locks[playerId % Constants.LOCKS_LEN].lock();
            final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)5);
            final Player player = this.playerDao.read(playerId);
            if (ci.getLv() > player.getConsumeLv()) {
                tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
                return tuple;
            }
            int gold = 0;
            final Date nowDate = new Date();
            final PlayerBuildingWork pbw = this.playerBuildingWorkDao.getPlayerBuildingWork(playerId, workId);
            if (pbw == null) {
                tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
                return tuple;
            }
            if (pbw.getWorkState() != 1) {
                tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10005);
                return tuple;
            }
            final PlayerBuilding pb = this.getPlayerBuilding(playerId, pbw.getTargetBuildId());
            if (pb != null && player.getPlayerLv() <= pb.getLv()) {
                tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10001);
                return tuple;
            }
            gold = this.getCDRecoverCost(ci, pbw, nowDate);
            if (!this.playerDao.canConsumeMoney(player, gold)) {
                tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
                return tuple;
            }
            final PlayerJob playerJob = this.playerJobDao.read(pbw.getTaskId());
            if (playerJob != null) {
                this.playerDao.consumeGold(player, gold, ci.getName());
                boolean isAuto = false;
                try {
                    final Object[] params = parseParams(playerJob.getParams(), 2);
                    isAuto = (Integer.valueOf(params[3].toString()) == 1);
                }
                catch (Exception e) {
                    BuildingService.log.error("building Work error: jobId :" + playerJob.getId() + ",workId:" + pbw.getWorkId() + ", param:" + playerJob.getParams());
                    BuildingService.log.error("buidling Work exception: " + e.getMessage());
                }
                final CallBack cBack = this.doUpgrade(getParams(playerId, pbw.getTargetBuildId(), pbw.getWorkId(), isAuto, pb.getLv()));
                tuple.left = true;
                tuple.right = cBack;
                return tuple;
            }
            final CallBack cBack2 = this.doUpgrade(getParams(playerId, pbw.getTargetBuildId(), pbw.getWorkId(), true, pb.getLv()));
            tuple.left = true;
            tuple.right = cBack2;
            return tuple;
        }
        finally {
            Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
        }
    }
    
    @Override
    public byte[] cdRecoverConfirmCallBack(final CallBack cBack) {
        if (cBack != null) {
            cBack.call();
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    public static String getParams(final int playerId, final int type) {
        return new StringBuilder(20).append(playerId).append("-").append(type).toString();
    }
    
    private int getCDRecoverCost(final Chargeitem ci, final PlayerBuildingWork pbw, final Date nowDate) {
        return (int)Math.ceil(CDUtil.getCD(pbw.getEndTime(), nowDate) * 1.0 / (ci.getParam() * 60000.0) * ci.getCost());
    }
    
    private int getBuildingUpgradeTime(final Building building, final int lv) {
        return (int)(building.getTimeE() * (building.getTimeBase() + this.serialCache.get(building.getTimeS(), lv) + this.serialCache.get(building.getTimeR(), lv) * this.serialCache.get(building.getTimeT(), lv)) * 1000.0);
    }
    
    private int getBuildingUpradeCopperCost(final Building building, final int lv) {
        return (int)(building.getCopperE() * this.serialCache.get(building.getCopperS(), lv));
    }
    
    private int getBuildingUpradeWoodCost(final Building building, final int lv) {
        return (int)(building.getLumberE() * this.serialCache.get(building.getLumberS(), lv));
    }
    
    private void copyProperties(final PlayerBuilding pb, final Building building, final int isNew) {
        pb.setBuildingId(building.getId());
        pb.setState(0);
        pb.setOutputType(building.getType());
        pb.setAreaId(building.getType());
        pb.setSpeedUpNum(this.serialCache.get(building.getTimeT(), 1));
        pb.setLv(1);
        if (17 == building.getId() || 18 == building.getId() || 8 == building.getId() || 9 == building.getId() || 3 == building.getId() || 4 == building.getId() || 5 == building.getId() || 10 == building.getId() || 16 == building.getId()) {
            pb.setIsNew(0);
        }
        else {
            pb.setIsNew(isNew);
        }
        pb.setEventId(0);
    }
    
    public static Object[] parseParams(final String param, final int type) {
        Object[] result = null;
        String[] strs = null;
        switch (type) {
            case 1: {
                result = new Object[2];
                strs = param.split("-");
                result[0] = Integer.valueOf(strs[0]);
                result[1] = Integer.valueOf(strs[1]);
                break;
            }
            case 2: {
                result = new Object[5];
                strs = param.split("-");
                result[0] = Integer.valueOf(strs[0]);
                result[1] = Integer.valueOf(strs[1]);
                result[2] = Integer.valueOf(strs[2]);
                result[3] = Integer.valueOf(strs[3]);
                result[4] = Integer.valueOf(strs[4]);
                break;
            }
        }
        return result;
    }
    
    public static String getParams(final int playerId, final int buildingId, final int workId, final boolean isAuto, final int pLv) {
        if (isAuto) {
            return new StringBuilder(20).append(playerId).append("-").append(buildingId).append("-").append(workId).append("-").append(1).append("-").append(pLv).toString();
        }
        return new StringBuilder(20).append(playerId).append("-").append(buildingId).append("-").append(workId).append("-").append(0).append("-").append(pLv).toString();
    }
    
    private byte[] getResult(final Player player, final List<PlayerBuilding> buildings, final int areaId) {
        final Date nowDate = new Date();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final int playerId = player.getPlayerId();
        doc.startArray("buildings");
        final List<PlayerBuildingWork> buildingWorks = this.playerBuildingWorkDao.getPlayerBuildingWork(playerId);
        final List<String> buldingIdList = this.getBuldingIdListByareaId(areaId);
        boolean isTip = false;
        for (final PlayerBuilding pb : buildings) {
            buldingIdList.remove(new StringBuilder().append(pb.getBuildingId()).toString());
            final Building building = (Building)this.buildingCache.get((Object)pb.getBuildingId());
            doc.startObject();
            doc.createElement("type", 0);
            doc.createElement("id", building.getId());
            doc.createElement("name", building.getName());
            doc.createElement("speedUpNum", pb.getSpeedUpNum());
            doc.createElement("totalUpNum", this.serialCache.get(building.getTimeT(), pb.getLv()));
            doc.createElement("lv", pb.getLv());
            if (pb.getLv() < player.getPlayerLv()) {
                isTip = true;
            }
            doc.createElement("intro", building.getIntro());
            doc.createElement("pos", building.getPos());
            doc.createElement("outputType", building.getType());
            final int output = this.buildingOutputCache.getBuildingOutput(playerId, building.getId());
            if (building.getOutputType() == 4) {
                doc.createElement("resType", 2);
            }
            else {
                doc.createElement("resType", 1);
            }
            doc.createElement("output", output);
            doc.createElement("isNew", pb.getIsNew() == 1);
            if (pb.getIsNew() == 1) {
                this.updateBuildingNewState(playerId, pb.getBuildingId(), 0);
            }
            doc.createElement("hasEvent", pb.getEventId() != 0);
            doc.startObject("upgrade");
            final boolean upgradeEnable = pb.getLv() < player.getPlayerLv();
            doc.createElement("upgradeEnable", upgradeEnable);
            doc.createElement("time", this.getBuildingUpgradeTime(building, pb.getLv() + 1));
            doc.startArray("cost");
            doc.startObject();
            doc.createElement("type", 1);
            doc.createElement("value", this.getBuildingUpradeCopperCost(building, pb.getLv() + 1));
            doc.endObject();
            doc.startObject();
            doc.createElement("type", 2);
            doc.createElement("value", this.getBuildingUpradeWoodCost(building, pb.getLv() + 1));
            doc.endObject();
            doc.endArray();
            doc.endObject();
            doc.endObject();
        }
        for (final PlayerBuildingWork pbw : buildingWorks) {
            if (pbw.getWorkState() == 1) {
                isTip = false;
            }
        }
        for (final String idStr : buldingIdList) {
            final Integer id = Integer.parseInt(idStr);
            final BluePrint bp = this.bluePrintDao.getByPlayerIdAndIndex(playerId, id);
            if (bp == null) {
                continue;
            }
            doc.startObject();
            final int state = bp.getState();
            doc.createElement("id", id);
            doc.createElement("type", state);
            final Building bd = (Building)this.buildingCache.get((Object)id);
            doc.createElement("pos", bd.getPos());
            doc.createElement("name", bd.getName());
            doc.createElement("isNew", false);
            if (1 == state) {
                if (((BuildingDrawing)this.buildingDrawingCache.get((Object)id)).getGet() != 0) {
                    doc.createElement("copper", 100);
                }
            }
            else if (2 == state) {
                doc.createElement("pic", this.buildingDrawingCache.get((Object)id));
                doc.createElement("tips", ((Building)this.buildingCache.get((Object)id)).getDrawingIntro());
            }
            else if (3 == state) {
                doc.createElement("pic", this.buildingDrawingCache.get((Object)id));
                doc.createElement("cd", 30);
            }
            else if (4 == state) {
                doc.createElement("cd", (Object)TimeUtil.now2specMs(bp.getCd().getTime()));
                doc.createElement("totalCd", 1800000);
            }
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("tip", isTip);
        doc.startArray("totalOutput");
        doc.startObject();
        int output2 = this.buildingOutputCache.getBuildingsOutput(playerId, areaId);
        if (areaId == 5) {
            output2 = (int)Math.ceil(output2 / 60.0);
        }
        if (3 == areaId) {
            if (output2 >= 800) {
                EventListener.fireEvent(new CommonEvent(29, playerId));
            }
        }
        else if (5 == areaId) {
            if (output2 >= 60000) {
                EventListener.fireEvent(new CommonEvent(33, playerId));
            }
            else if (output2 >= 52000) {
                EventListener.fireEvent(new CommonEvent(30, playerId));
            }
        }
        doc.createElement("type", areaId);
        doc.createElement("output", output2);
        doc.endObject();
        doc.endArray();
        doc.appendJson("autoUpbuilding", this.getAutoUpbuilding(playerId));
        final PlayerResourceAddition pra = this.playerResourceAdditionDao.getByPlayerIdAndType(playerId, areaId);
        if (pra != null && pra.getEndTime().after(nowDate)) {
            doc.createElement("additionMode", pra.getAdditionMode());
            doc.createElement("additionRate", ((Chargeitem)this.chargeItemCache.get((Object)this.getId(areaId, pra.getAdditionMode()))).getParam());
            doc.createElement("additionCd", CDUtil.getCD(pra.getEndTime(), nowDate));
            doc.createElement("currentTimeType", pra.getTimeType());
        }
        else {
            doc.createElement("additionMode", 0);
            doc.createElement("additionRate", 1);
            doc.createElement("additionCd", 0);
        }
        this.getBuildingWorks(player, doc, nowDate, buildings, buildingWorks);
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        doc.createElement("freeConsNum", pa.getFreeConstructionNum());
        if (5 == areaId) {
            doc.createElement("troopLv", this.techEffectCache.getTechEffect(playerId, 28) + 1);
        }
        else if (2 == areaId) {
            doc.createElement("hasBandit", pa.getHasBandit());
        }
        else if (1 == areaId) {
            doc.createElement("kidnapper", pa.getKidnapper());
        }
        if (this.dataGetter.getPlayerAttributeDao().getFunctionId(playerId).toCharArray()[32] == '1') {
            final PlayerTech pt = this.dataGetter.getPlayerTechDao().getPlayerTech(playerId, 502);
            if (pt != null && pt.getStatus() == 5) {
                doc.createElement("feat", this.dataGetter.getFeatBuildingDao().getFeat(playerId));
            }
        }
        doc.endObject();
        return doc.toByte();
    }
    
    private void getBuildingWorks(final Player player, final JsonDocument doc, final Date nowDate, final List<PlayerBuilding> buildings, final List<PlayerBuildingWork> buildingWorks) {
        doc.startArray("buildingWorks");
        for (final PlayerBuildingWork pbw : buildingWorks) {
            pbw.getTargetBuildId();
            final Building building = (Building)this.buildingCache.get((Object)pbw.getTargetBuildId());
            doc.startObject();
            doc.createElement("workId", pbw.getWorkId());
            int state = pbw.getWorkState();
            if (pbw.getWorkState() == 1) {
                final PlayerBuilding pb = this.getPlayerBuilding(player.getPlayerId(), pbw.getTargetBuildId());
                doc.createElement("areaId", pb.getAreaId());
                doc.createElement("buildingId", pbw.getTargetBuildId());
                final long cd = CDUtil.getCD(pb.getUpdateTime(), nowDate);
                if (cd <= 0L) {
                    state = 0;
                    final int result = this.playerBuildingWorkDao.resetBuildingWork(player.getPlayerId(), pbw.getWorkId());
                    BuildingService.log.debug("doExeUpgrade building exception result:" + result + " playerId:" + player.getPlayerId() + " workId:" + pbw.getWorkId());
                }
                doc.createElement("cd", cd);
                doc.createElement("totalTime", this.getBuildingUpgradeTime(building, pb.getLv() + 1));
            }
            doc.createElement("state", state);
            doc.endObject();
        }
        doc.endArray();
    }
    
    private static String getAreaName(final int buildingType) {
        switch (buildingType) {
            case 1: {
                return LocalMessages.T_COMM_10004;
            }
            case 2: {
                return LocalMessages.T_COMM_10005;
            }
            case 3: {
                return LocalMessages.T_COMM_10017;
            }
            case 4: {
                return LocalMessages.T_COMM_10018;
            }
            case 5: {
                return LocalMessages.T_COMM_10040;
            }
            default: {
                return "";
            }
        }
    }
    
    @Transactional
    @Override
    public Tuple<Boolean, byte[]> addBuildingAddition(final PlayerDto playerDto, final int buildingType, final int additionMode, final int timeType) {
        final Tuple<Boolean, byte[]> result = new Tuple();
        result.left = false;
        if (buildingType < 1 || buildingType > 5 || additionMode < 1 || additionMode > 3 || timeType < 1 || timeType > 3) {
            result.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            return result;
        }
        final int playerId = playerDto.playerId;
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[41] != '1') {
            result.right = JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
            return result;
        }
        final Tuple<Integer, Chargeitem> tuple = this.getResourceAddition(buildingType, timeType, additionMode);
        final int price = tuple.left;
        final Chargeitem chargeItem = tuple.right;
        final Player player = this.playerDao.read(playerDto.playerId);
        if (player.getConsumeLv() < chargeItem.getLv()) {
            result.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
            return result;
        }
        if (!this.playerDao.consumeGold(player, price, String.valueOf(getAreaName(buildingType)) + chargeItem.getName())) {
            result.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
            return result;
        }
        Date baseTime = new Date();
        PlayerResourceAddition pra = this.playerResourceAdditionDao.getByPlayerIdAndType(playerId, buildingType);
        if (pra != null && additionMode == pra.getAdditionMode() && pra.getEndTime().after(baseTime)) {
            baseTime = pra.getEndTime();
        }
        Date endTime = new Date();
        switch (timeType) {
            case 1: {
                endTime = new Date(baseTime.getTime() + 86400000L);
                break;
            }
            case 2: {
                endTime = new Date(baseTime.getTime() + 604800000L);
                break;
            }
            case 3: {
                endTime = new Date(baseTime.getTime() + 2592000000L);
                break;
            }
        }
        if (3 == additionMode) {
            final String msg = MessageFormatter.format(LocalMessages.BROADCAST_ADDTION_3, new Object[] { ColorUtil.getGreenMsg(player.getPlayerName()) });
            this.chatService.sendBigNotice("COUNTRY", playerDto, msg, "_1");
        }
        final StringBuilder param = new StringBuilder(20);
        param.append(playerId);
        param.append(",");
        param.append(buildingType);
        this.jobService.addJob("buildingService", "clearBuildingsOutputAddition", param.toString(), endTime.getTime(), false);
        if (pra == null) {
            pra = new PlayerResourceAddition();
            pra.setPlayerId(playerId);
            pra.setResourceType(buildingType);
            pra.setAdditionMode(additionMode);
            pra.setEndTime(endTime);
            pra.setTimeType(timeType);
            pra.setTaskId(0);
            this.playerResourceAdditionDao.create(pra);
        }
        else {
            this.playerResourceAdditionDao.update(pra.getVId(), endTime, timeType, additionMode, 0);
        }
        this.buildingOutputCache.clearOutputAddition(playerId, buildingType);
        final int output = this.buildingOutputCache.getBuildingsOutput(playerId, buildingType);
        TaskMessageHelper.sendResourceTaskMessage(playerId, buildingType, output);
        if (buildingType < 5) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement(this.getType(buildingType), output);
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
        }
        result.left = true;
        result.right = JsonBuilder.getJson(State.SUCCESS, "");
        return result;
    }
    
    @Override
    public byte[] getBuildingAdditionPrice(final int buildingType, final int additionMode, final int timeType) {
        if (buildingType < 1 || buildingType > 5 || additionMode < 1 || additionMode > 3 || timeType < 1 || timeType > 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Tuple<Integer, Chargeitem> tuple = this.getResourceAddition(buildingType, timeType, additionMode);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("price", tuple.left);
        doc.startArray("additions");
        for (int i = 1; i <= 3; ++i) {
            doc.startObject();
            doc.createElement("additionMode", i);
            doc.createElement("additionRate", ((Chargeitem)this.chargeItemCache.get((Object)this.getId(buildingType, i))).getParam());
            doc.endObject();
        }
        doc.endArray();
        doc.startArray("days");
        for (int i = 1; i <= 3; ++i) {
            doc.startObject();
            doc.createElement("timeType", i);
            doc.createElement("timeValue", this.getDay(i));
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public Tuple<Integer, Chargeitem> getResourceAddition(final int buildingType, final int timeType, final int additionMode) {
        final float discount = this.getDisCount(timeType);
        final int days = this.getDay(timeType);
        final Tuple<Integer, Chargeitem> tuple = new Tuple();
        final int id = this.getId(buildingType, additionMode);
        final Chargeitem item = (Chargeitem)this.chargeItemCache.get((Object)id);
        final int price = item.getCost();
        final int resultPrice = (int)Math.floor(price * days * discount);
        tuple.left = resultPrice;
        tuple.right = item;
        return tuple;
    }
    
    @Override
    public void clearBuildingsOutputAddition(final String param) {
        final String[] s = param.split(",");
        final int playerId = Integer.valueOf(s[0]);
        final int buildingType = Integer.valueOf(s[1]);
        final PlayerResourceAddition pra = this.playerResourceAdditionDao.getByPlayerIdAndType(playerId, buildingType);
        if (pra != null && pra.getEndTime().after(new Date())) {
            this.jobService.addJob("buildingService", "clearBuildingsOutputAddition", param.toString(), pra.getEndTime().getTime(), false);
            return;
        }
        this.buildingOutputCache.clearOutputAddition(playerId, buildingType);
    }
    
    @Override
    public void initResourceAdditionTimeJob() {
        final List<PlayerResourceAddition> list = this.playerResourceAdditionDao.getListByTime(new Date());
        for (final PlayerResourceAddition pra : list) {
            try {
                if (!pra.getEndTime().after(new Date())) {
                    continue;
                }
                final StringBuilder param = new StringBuilder(20);
                param.append(pra.getPlayerId());
                param.append(",");
                param.append(pra.getResourceType());
                this.jobService.addJob("buildingService", "clearBuildingsOutputAddition", param.toString(), pra.getEndTime().getTime(), false);
            }
            catch (Exception e) {
                BuildingService.log.error("buildingService initResourceAdditionTimeJob", e);
            }
        }
    }
    
    @Override
    public byte[] useFeatBuilding(final PlayerDto playerDto) {
        if (playerDto.cs[32] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final int playerId = playerDto.playerId;
        final int feat = this.dataGetter.getFeatBuildingDao().getFeat(playerId);
        if (feat <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FEAT_RANK_NO_FEAT_BUILING);
        }
        this.dataGetter.getFeatBuildingDao().resetFeat(playerId);
        this.dataGetter.getRankService().addFeat(playerId, feat);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public Tuple<Integer, byte[]> cdSpeedUp(final int playerId, final int workId) {
        try {
            Constants.locks[playerId % Constants.LOCKS_LEN].lock();
            final Tuple<Integer, byte[]> tuple = new Tuple();
            final PlayerBuildingWork pbw = this.playerBuildingWorkDao.getPlayerBuildingWork(playerId, workId);
            if (pbw == null || pbw.getWorkState() != 1) {
                tuple.left = 0;
                tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10005);
                return tuple;
            }
            final PlayerJob playerJob = this.playerJobDao.read(pbw.getTaskId());
            final PlayerBuilding pb = this.getPlayerBuilding(playerId, pbw.getTargetBuildId());
            if (playerJob == null) {
                tuple.left = 0;
                tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10005);
                return tuple;
            }
            long endtime = playerJob.getExecutionTime();
            final Building building = (Building)this.buildingCache.get((Object)pb.getBuildingId());
            final int reduceTime = (int)(this.serialCache.get(building.getTimeR(), pb.getLv()) * building.getTimeE());
            endtime = pb.getUpdateTime().getTime() - reduceTime * 1000;
            int nextState = 1;
            if (endtime <= System.currentTimeMillis()) {
                nextState = 2;
            }
            if (pb.getSpeedUpNum() == 1) {
                nextState = 3;
            }
            if (!this.reduceSpeedNums(playerId, pb.getBuildingId())) {
                tuple.left = 0;
                tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10009);
                return tuple;
            }
            this.upgradeBuilding(pb.getPlayerId(), pb.getBuildingId(), new Date(endtime));
            this.jobService.reAddJob(playerJob.getId(), playerJob.getClassName(), playerJob.getMethodName(), playerJob.getParams(), endtime);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("nextState", nextState);
            doc.createElement("areaId", pb.getAreaId());
            doc.createElement("buildingId", pbw.getTargetBuildId());
            doc.createElement("cd", CDUtil.getCD(endtime, new Date()));
            doc.createElement("reduceTime", reduceTime);
            doc.createElement("totalTime", this.getBuildingUpgradeTime(building, pb.getLv() + 1));
            doc.endObject();
            tuple.left = pb.getBuildingId();
            tuple.right = JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            return tuple;
        }
        finally {
            Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
        }
    }
    
    private String getType(final int buildingType) {
        switch (buildingType) {
            case 1: {
                return "copperOutput";
            }
            case 2: {
                return "woodOutput";
            }
            case 3: {
                return "foodOutput";
            }
            case 4: {
                return "ironOutput";
            }
            default: {
                return "";
            }
        }
    }
    
    @Override
    public void constructionComplete(final int playerId) {
        final Tuple<Boolean, Object> tuple = new Tuple();
        tuple.left = false;
        try {
            Constants.locks[playerId % Constants.LOCKS_LEN].lock();
            final List<PlayerBuildingWork> pbwList = this.playerBuildingWorkDao.getBusyWorkList(playerId);
            PlayerBuilding pb = null;
            for (final PlayerBuildingWork pbw : pbwList) {
                final PlayerJob playerJob = this.playerJobDao.read(pbw.getTaskId());
                pb = this.getPlayerBuilding(playerId, pbw.getTargetBuildId());
                if (playerJob != null) {
                    final Object[] params = parseParams(playerJob.getParams(), 2);
                    final boolean isAuto = Integer.valueOf(params[3].toString()) == 1;
                    if (pb == null) {
                        continue;
                    }
                    final CallBack cBack = this.doUpgrade(getParams(playerId, pbw.getTargetBuildId(), pbw.getWorkId(), isAuto, pb.getLv()));
                    if (cBack == null) {
                        continue;
                    }
                    cBack.call();
                }
                else {
                    final CallBack cBack2 = this.doUpgrade(getParams(playerId, pbw.getTargetBuildId(), pbw.getWorkId(), false, pb.getLv()));
                    if (cBack2 == null) {
                        continue;
                    }
                    cBack2.call();
                }
            }
        }
        finally {
            Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
        }
        Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
    }
    
    @Override
    public byte[] freeCdRecoverConfirm(final int playerId) {
        final char[] cs = this.playerAttributeDao.read(playerId).getFunctionId().toCharArray();
        if (cs[56] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final int count = this.playerBuildingWorkDao.getBusyWorkNum(playerId);
        if (count <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10011);
        }
        final int num = this.playerAttributeDao.getFreeConstructionNum(playerId);
        if (num <= 0) {
            final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)43);
            final Player player = this.playerDao.read(playerId);
            if (ci.getLv() > player.getConsumeLv()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
            }
            if (!this.playerDao.consumeGold(player, ci)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
            }
            this.constructionComplete(playerId);
        }
        else {
            this.constructionComplete(playerId);
            this.playerAttributeDao.consumeFreeConstructionNum(playerId, "\u5347\u7ea7\u5efa\u7b51\u4f7f\u7528\u9ec4\u91d1\u5efa\u7b51\u961f");
        }
        TaskMessageHelper.sendUseFreeConsTaskMessage(playerId);
        return JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("freeConsNum", this.playerAttributeDao.getFreeConstructionNum(playerId)));
    }
    
    @Override
    public byte[] freeCdRecover(final int playerId) {
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)43);
        final Player player = this.playerDao.read(playerId);
        if (ci.getLv() > player.getConsumeLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        return JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("gold", ci.getCost()));
    }
    
    @Transactional
    @Override
    public void openFreeConstruction(final int playerId) {
        EventListener.fireEvent(new CommonEvent(34, playerId));
    }
    
    private void pushShakeFreeCons(final int playerId) {
        final char[] cs = this.playerAttributeDao.read(playerId).getFunctionId().toCharArray();
        if (cs[56] != '1') {
            return;
        }
        final int count = this.playerBuildingWorkDao.getFreeWorkNum(playerId);
        if (count > 0) {
            return;
        }
        final int num = this.playerAttributeDao.getFreeConstructionNum(playerId);
        if (num > 0) {
            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("shakeFreeCons", 1));
        }
    }
    
    @Override
    public void openLumberArea(final int playerId) {
        this.playerAttributeDao.setHasBandit(playerId, 3);
    }
    
    @Override
    public void openCopperArea(final int playerId) {
        this.playerAttributeDao.setKidnapper(playerId, 3);
    }
    
    @Override
    public boolean dropBluePrintById(final int playerId, final int id) {
        if (this.buildingDrawingCache.get((Object)id) == null) {
            return false;
        }
        final BluePrint bp = this.bluePrintDao.getByPlayerIdAndIndex(playerId, id);
        if (bp == null || 2 != bp.getState()) {
            return false;
        }
        if (this.buildingDrawingCache.getByIdAndLvAndRate(id, this.playerDao.getPlayerLv(playerId), WebUtil.nextDouble()) == null) {
            return false;
        }
        this.bluePrintDao.updateState(bp.getVId(), 3);
        return true;
    }
    
    @Override
    public BuildingDrawing dropBluePrintByType(final int playerId, final int type) {
        final List<BuildingDrawing> bdList = this.buildingDrawingCache.getListByTypeAndLv(type, this.playerDao.getPlayerLv(playerId));
        if (bdList == null || bdList.size() <= 0) {
            return null;
        }
        final List<BuildingDrawing> openList = new ArrayList<BuildingDrawing>();
        for (final BuildingDrawing bd : bdList) {
            if (this.playerBuildingDao.getPlayerBuilding(playerId, bd.getId()) != null) {
                continue;
            }
            final BluePrint bp = this.bluePrintDao.getByPlayerIdAndIndex(playerId, bd.getId());
            if (bp == null || 2 != bp.getState()) {
                continue;
            }
            openList.add(bd);
        }
        double rate = WebUtil.nextDouble();
        while (openList.size() > 0) {
            final BuildingDrawing bd2 = openList.get(0);
            if (rate <= bd2.getProb()) {
                if (2 == type) {
                    this.bluePrintDao.updateStateByPlayerIdAndIndex(playerId, bd2.getId(), 3);
                }
                return bd2;
            }
            rate -= bd2.getProb();
            openList.remove(0);
        }
        return null;
    }
    
    @Override
    public boolean buyBluePrintById(final int playerId, final int id) {
        final BluePrint bp = this.bluePrintDao.getByPlayerIdAndIndex(playerId, id);
        if (bp == null || 2 != bp.getState()) {
            return false;
        }
        this.bluePrintDao.updateState(bp.getVId(), 3);
        Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("refreshBuilding", ((Building)this.buildingCache.get((Object)id)).getType()));
        return true;
    }
    
    @Override
    public byte[] killBandit(final int playerId, final int banditId) {
        int alive = 1;
        if (1 == banditId) {
            alive = 2;
        }
        this.playerAttributeDao.killBandit(playerId, alive);
        TaskMessageHelper.sendKillBanditTaskMessage(playerId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] killKidnapper(final int playerId, final int kidnapperId) {
        this.playerAttributeDao.killKidnapper(playerId, 0);
        TaskMessageHelper.sendKillBanditTaskMessage(playerId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private List<String> getBuldingIdListByareaId(final int areaId) {
        final List<String> result = new ArrayList<String>();
        for (int begin = (areaId - 1) * 16 + 1, end = areaId * 16; begin <= end; ++begin) {
            result.add(new StringBuilder(String.valueOf(begin)).toString());
        }
        return result;
    }
    
    @Transactional
    @Override
    public byte[] openBluePrint(final int playerId, final int buildingId) {
        final BluePrint bp = this.bluePrintDao.getByPlayerIdAndIndex(playerId, buildingId);
        if (bp == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10021);
        }
        if (1 != bp.getState()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10023);
        }
        if (!this.playerResourceDao.consumeCopper(playerId, 100, "\u5f00\u53d1\u7a7a\u5730\u6d88\u8017\u94f6\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
        }
        this.bluePrintDao.updateState(bp.getVId(), 2);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final Building building = (Building)this.buildingCache.get((Object)buildingId);
        doc.createElement("type", 2);
        doc.createElement("name", building.getName());
        doc.createElement("pos", building.getPos());
        doc.createElement("pic", ((BuildingDrawing)this.buildingDrawingCache.get((Object)buildingId)).getPic());
        doc.createElement("tips", building.getDrawingIntro());
        doc.endObject();
        TaskMessageHelper.sendOpenBluePrintTaskMessage(playerId, buildingId);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] consBluePrint(final int playerId, final int buildingId) {
        final BluePrint bp = this.bluePrintDao.getByPlayerIdAndIndex(playerId, buildingId);
        if (bp == null || 3 > bp.getState()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_BLUE_PRINT);
        }
        if (4 == bp.getState()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BLUE_IN_CD);
        }
        if (5 <= bp.getState()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BLUE_PRINT_CONS_FINISH);
        }
        final Date cd = TimeUtil.nowAddMinutes(30);
        final int jobId = this.jobService.addJob("buildingService", "finishConsBluePrint", new StringBuilder().append(bp.getVId()).toString(), cd.getTime());
        this.bluePrintDao.cons(bp.getVId(), 4, cd, jobId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("pos", ((Building)this.buildingCache.get((Object)buildingId)).getPos());
        doc.createElement("cd", 1800000);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] consCdRecover(final int playerId, final int buildingId) {
        final BluePrint bp = this.bluePrintDao.getByPlayerIdAndIndex(playerId, buildingId);
        if (bp == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (4 != bp.getState()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10022);
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)44);
        final Player player = this.playerDao.read(playerId);
        if (ci.getLv() > player.getConsumeLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        final int gold = (int)Math.ceil(TimeUtil.now2specMs(bp.getCd().getTime()) / 60000.0 / ci.getParam()) * ci.getCost();
        return JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("gold", gold));
    }
    
    @Transactional
    @Override
    public byte[] consCdRecoverConfirm(final int playerId, final int buildingId) {
        final BluePrint bp = this.bluePrintDao.getByPlayerIdAndIndex(playerId, buildingId);
        if (bp == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (4 != bp.getState()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10022);
        }
        final PlayerJob job = this.playerJobDao.read(bp.getJobId());
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)44);
        final Player player = this.playerDao.read(playerId);
        if (ci.getLv() > player.getConsumeLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        final int gold = (int)Math.ceil(TimeUtil.now2specMs(bp.getCd().getTime()) / 60000.0 / ci.getParam()) * ci.getCost();
        if (!this.playerDao.canConsumeMoney(player, gold)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        if (job != null && this.jobService.cancelJob(bp.getJobId(), true)) {
            this.playerDao.consumeGold(player, gold, ci.getName());
            this.finishConsBluePrint(new StringBuilder().append(bp.getVId()).toString());
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_BUILDING_10022);
    }
    
    @Transactional
    @Override
    public void finishConsBluePrint(final String param) {
        final int vId = Integer.parseInt(param);
        final BluePrint bp = this.bluePrintDao.read(vId);
        if (bp == null) {
            return;
        }
        this.bluePrintDao.cons(vId, 5, new Date(), 0);
        this.createBuilding(bp.getPlayerId(), bp.getIndex(), 0);
    }
    
    @Override
    public int getId(final int buildingType, final int additionMode) {
        int id = 0;
        if (1 == additionMode) {
            id = ((5 == buildingType) ? 49 : 16);
        }
        else if (2 == additionMode) {
            id = ((5 == buildingType) ? 50 : 17);
        }
        else if (3 == additionMode) {
            id = ((5 == buildingType) ? 51 : 18);
        }
        return id;
    }
    
    private int getDay(final int timeType) {
        int day = 0;
        if (1 == timeType) {
            day = 1;
        }
        else if (2 == timeType) {
            day = 7;
        }
        else if (3 == timeType) {
            day = 30;
        }
        return day;
    }
    
    private float getDisCount(final int timeType) {
        float discount = 1.0f;
        if (2 == timeType) {
            discount = ((C)this.cCache.get((Object)"Resource.Muti.Sale.Weekly")).getValue();
        }
        else if (3 == timeType) {
            discount = ((C)this.cCache.get((Object)"Resource.Muti.Sale.Monthly")).getValue();
        }
        return discount;
    }
    
    @Override
    public Tuple<Boolean, String> addBuildingAdditionForFree(final PlayerDto playerDto, final int buildingType, final int additionMode, final int timeType) {
        final Tuple<Boolean, String> result = new Tuple(false, (Object)null);
        if (buildingType < 1 || buildingType > 5 || additionMode < 1 || additionMode > 3 || timeType < 1 || timeType > 3) {
            result.right = LocalMessages.T_COMM_10011;
            return result;
        }
        final int playerId = playerDto.playerId;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final boolean isOpen = this.checkBuildingTypeIsOpen(buildingType, pa);
        if (!isOpen) {
            if (4 == buildingType) {
                result.right = LocalMessages.OPRN_CONDITION_IRON;
            }
            else {
                result.right = LocalMessages.OPRN_CONDITION;
            }
            return result;
        }
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[41] != '1') {
            result.right = LocalMessages.OPRN_CONDITION;
            return result;
        }
        Date baseTime = new Date();
        PlayerResourceAddition pra = this.playerResourceAdditionDao.getByPlayerIdAndType(playerId, buildingType);
        boolean isExpired = false;
        if (pra != null && pra.getEndTime().before(baseTime)) {
            isExpired = true;
        }
        if (pra != null && additionMode == pra.getAdditionMode() && pra.getEndTime().after(baseTime)) {
            baseTime = pra.getEndTime();
        }
        Date endTime = new Date();
        switch (timeType) {
            case 1: {
                endTime = new Date(baseTime.getTime() + 86400000L);
                break;
            }
            case 2: {
                endTime = new Date(baseTime.getTime() + 604800000L);
                break;
            }
            case 3: {
                endTime = new Date(baseTime.getTime() + 2592000000L);
                break;
            }
        }
        if (3 == additionMode) {
            final String msg = MessageFormatter.format(LocalMessages.BROADCAST_ADDTION_3, new Object[] { ColorUtil.getGreenMsg(playerDto.playerName) });
            this.chatService.sendBigNotice("COUNTRY", playerDto, msg, "_1");
        }
        final StringBuilder param = new StringBuilder(20);
        param.append(playerId);
        param.append(",");
        param.append(buildingType);
        this.jobService.addJob("buildingService", "clearBuildingsOutputAddition", param.toString(), endTime.getTime(), false);
        if (pra == null) {
            pra = new PlayerResourceAddition();
            pra.setPlayerId(playerId);
            pra.setResourceType(buildingType);
            pra.setAdditionMode(additionMode);
            pra.setEndTime(endTime);
            pra.setTimeType(timeType);
            pra.setTaskId(0);
            this.playerResourceAdditionDao.create(pra);
        }
        else {
            if (!isExpired && pra.getAdditionMode() != additionMode) {
                result.right = LocalMessages.CAN_NOT_USE_VIP_PRIVILEGE;
                return result;
            }
            this.playerResourceAdditionDao.update(pra.getVId(), endTime, timeType, additionMode, 0);
        }
        this.buildingOutputCache.clearOutputAddition(playerId, buildingType);
        final int output = this.buildingOutputCache.getBuildingsOutput(playerId, buildingType);
        TaskMessageHelper.sendResourceTaskMessage(playerId, buildingType, output);
        if (buildingType < 5) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement(this.getType(buildingType), output);
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
        }
        result.left = true;
        return result;
    }
    
    public boolean checkBuildingTypeIsOpen(final int buildingType, final PlayerAttribute pa) {
        int type = 0;
        switch (buildingType) {
            case 1: {
                return true;
            }
            case 2: {
                type = 5;
                break;
            }
            case 3: {
                type = 6;
                break;
            }
            case 4: {
                type = 7;
                break;
            }
            case 5: {
                type = 8;
                break;
            }
            default: {
                return false;
            }
        }
        return RankComm.functionIsOpen(type, 0, pa);
    }
}
