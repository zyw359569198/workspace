package com.reign.gcld.tech.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.gcld.tech.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.job.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.timer.dao.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.general.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.store.service.*;
import com.reign.gcld.incense.service.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.tech.domain.*;
import org.apache.commons.lang.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.task.message.*;
import com.reign.util.*;
import org.springframework.transaction.annotation.*;
import java.util.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.timer.domain.*;
import com.reign.gcld.task.reward.*;
import com.reign.gcld.team.service.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.common.*;

@Component("techService")
public class TechService implements ITechService, InitializingBean
{
    @Autowired
    private IPlayerTechDao playerTechDao;
    @Autowired
    private TechCache techCache;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IJobService jobService;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private ArmiesCache armiesCache;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IPlayerJobDao playerJobDao;
    @Autowired
    private ConsumeResource consumeResource;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private BuildingOutputCache buildingOutputCache;
    @Autowired
    private IGeneralService generalService;
    @Autowired
    private ArmiesRewardCache armiesRewardCache;
    @Autowired
    private IQuenchingService quenchingService;
    @Autowired
    private IIncenseService incenseService;
    private static final Logger errorLogger;
    
    static {
        errorLogger = CommonLog.getLog(TechService.class);
    }
    
    @Override
    public byte[] getTechInfo(final PlayerDto playerDto, int page) {
        final int playerId = playerDto.playerId;
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[19] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        if (page <= 0) {
            page = 1;
        }
        final List<PlayerTech> ptList = this.playerTechDao.getTechListByLimit(playerId, (page - 1) * 8, 8);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        Tech tech = null;
        int techId = 0;
        int isNew = 0;
        int finishNew = 0;
        doc.startArray("techs");
        for (final PlayerTech pt : ptList) {
            doc.startObject();
            techId = pt.getTechId();
            doc.createElement("techId", techId);
            tech = (Tech)this.techCache.get((Object)techId);
            doc.createElement("pic", tech.getPic());
            doc.createElement("name", tech.getName());
            if (pt.getStatus() == 0) {
                String condition = this.armiesCache.getTechCondition(techId);
                if (StringUtils.isBlank(condition)) {
                    condition = this.armiesRewardCache.getTechCondition(techId);
                }
                doc.createElement("effect", condition);
            }
            else {
                doc.createElement("effect", tech.getIntro());
            }
            doc.createElement("num", pt.getNum());
            doc.createElement("total", tech.getResourceTimes());
            if (pt.getStatus() == 4) {
                doc.createElement("cd", TimeUtil.now2specMs(pt.getCd().getTime()));
            }
            doc.createElement("status", pt.getStatus());
            isNew = pt.getIsNew();
            doc.createElement("isNew", isNew);
            finishNew = pt.getFinishNew();
            doc.createElement("finish_new", finishNew);
            if (isNew == 1 || finishNew == 1) {
                this.playerTechDao.setIsNewAndFinishNew(pt.getVId(), 0, 0);
            }
            doc.createElement("cdTotal", tech.getResearchTime() * 60 * 1000);
            if (pt.getStatus() == 2 || pt.getStatus() == 3) {
                doc.startArray("resources");
                if (StringUtils.isBlank(tech.getResource())) {
                    TechService.errorLogger.error("tech resource error: techId :" + tech.getId());
                }
                else {
                    final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(tech.getResource());
                    final Map<Integer, Reward> rewardMap = reward.getReward(playerDto, this.dataGetter, null);
                    for (final Reward temp : rewardMap.values()) {
                        doc.startObject();
                        doc.createElement("type", temp.getType());
                        doc.createElement("value", temp.getNum());
                        doc.endObject();
                    }
                }
                doc.endArray();
            }
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("currentPage", page);
        final int size = this.playerTechDao.getSizeByPlayerId(playerId);
        doc.createElement("totalPage", (size % 8 == 0) ? (size / 8) : (size / 8 + 1));
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)11);
        doc.createElement("vipLimit", ci.getLv());
        doc.endObject();
        if (pa.getTechOpen() == 1 || pa.getTechResearch() == 1) {
            this.playerAttributeDao.setTechOpenAndTechResearch(playerId, 0, 0);
        }
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] capitalInject(final PlayerDto playerDto, final int techId) {
        if (techId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[19] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final PlayerTech pt = this.playerTechDao.getPlayerTech(playerId, techId);
        if (pt == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_NO_SUCH_TECH);
        }
        if (pt.getStatus() != 2 && pt.getStatus() != 3) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TECH_NO_CAPITAL_INJECT);
        }
        final Tech tech = (Tech)this.techCache.get((Object)pt.getTechId());
        if (StringUtils.isBlank(tech.getResource())) {
            TechService.errorLogger.error("tech resource error: techId :" + tech.getId());
        }
        else {
            final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(tech.getResource());
            final Map<Integer, Reward> rewardMap = reward.getReward(playerDto, this.dataGetter, null);
            final int size = rewardMap.size();
            if (size == 1) {
                for (final Reward temp : rewardMap.values()) {
                    if (temp.getType() == 1) {
                        if (!this.playerResourceDao.consumeCopper(playerId, temp.getNum(), "\u79d1\u6280\u6ce8\u8d44\u6d88\u8017\u94f6\u5e01")) {
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
                        }
                        continue;
                    }
                    else if (temp.getType() == 2) {
                        if (!this.playerResourceDao.consumeWood(playerId, temp.getNum(), "\u79d1\u6280\u6ce8\u8d44\u6d88\u8017\u6728\u6750")) {
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10008);
                        }
                        continue;
                    }
                    else if (temp.getType() == 3) {
                        if (!this.playerResourceDao.consumeFood(playerId, temp.getNum(), "\u79d1\u6280\u6ce8\u8d44\u6d88\u8017\u7cae\u98df")) {
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10027);
                        }
                        continue;
                    }
                    else {
                        if (temp.getType() == 4 && !this.playerResourceDao.consumeIron(playerId, temp.getNum(), "\u79d1\u6280\u6ce8\u8d44\u6d88\u8017\u9554\u94c1")) {
                            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10026);
                        }
                        continue;
                    }
                }
            }
            else if (size > 1) {
                final Tuple<Boolean, String> tuple = this.consumeResource.consumeBase(playerId, rewardMap.values());
                if (!(boolean)tuple.left) {
                    return JsonBuilder.getJson(State.FAIL, tuple.right);
                }
            }
        }
        final int num = pt.getNum();
        final int total = tech.getResourceTimes();
        if (num + 1 == total) {
            this.playerTechDao.setNumAndStatus(pt.getVId(), total, 1);
        }
        else if (num == 0) {
            this.playerTechDao.setNumAndStatus(pt.getVId(), 1, 3);
        }
        else {
            this.playerTechDao.setNum(pt.getVId(), num + 1);
        }
        TaskMessageHelper.sendTechInjectTaskMessage(playerId, techId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("techId", pt.getTechId());
        doc.createElement("num", num + 1);
        doc.createElement("total", total);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] research(final PlayerDto playerDto, final int techId) {
        if (techId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[19] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final PlayerTech pt = this.playerTechDao.getPlayerTech(playerId, techId);
        if (pt == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_NO_SUCH_TECH);
        }
        if (pt.getStatus() != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TECH_CAN_NOT_RESEARCH);
        }
        final Tech tech = (Tech)this.techCache.get((Object)techId);
        final int cd = tech.getResearchTime();
        final Date cdDate = TimeUtil.nowAddMinutes(cd);
        final int jobId = this.jobService.addJob("techService", "finishResearch", pt.getVId().toString(), cdDate.getTime());
        this.playerTechDao.setCd(pt.getVId(), cdDate, 4, jobId);
        TaskMessageHelper.sendTechResearchTaskMessage(playerId);
        TaskMessageHelper.sendTechResearchBeginTaskMessage(playerId, techId);
        this.dataGetter.getCourtesyService().addPlayerEvent(playerId, 2, 0);
        final int displayNum = this.playerTechDao.getNumDisPlayButton(playerId);
        if (displayNum <= 0) {
            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("displayTech", 0));
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] cdRecover(final int playerId, final int techId) {
        if (techId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[19] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final PlayerTech pt = this.playerTechDao.getPlayerTech(playerId, techId);
        if (pt == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_NO_SUCH_TECH);
        }
        if (pt.getStatus() != 4) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TECH_NOT_IN_RESEARCH);
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)11);
        if (this.playerDao.getConsumeLv(playerId) < ci.getLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        final long cd = TimeUtil.now2specMs(pt.getCd().getTime());
        final int gold = (int)Math.ceil(cd / (ci.getParam() * 60000.0)) * ci.getCost();
        return JsonBuilder.getJson(State.SUCCESS, "gold", (Object)gold);
    }
    
    @Transactional
    @Override
    public byte[] cdRecoverConfirm(final int playerId, final int techId) {
        if (techId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final char[] cs = this.playerAttributeDao.getFunctionId(playerId).toCharArray();
        if (cs[19] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final PlayerTech pt = this.playerTechDao.getPlayerTech(playerId, techId);
        if (pt == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_NO_SUCH_TECH);
        }
        if (pt.getStatus() != 4) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TECH_NOT_IN_RESEARCH);
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)11);
        final Player player = this.playerDao.read(playerId);
        if (player.getConsumeLv() < ci.getLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        final long cd = TimeUtil.now2specMs(pt.getCd().getTime());
        final int gold = (int)Math.ceil(cd / (ci.getParam() * 60000.0)) * ci.getCost();
        final PlayerJob playerJob = this.playerJobDao.read(pt.getJobId());
        if (!this.playerDao.canConsumeMoney(player, gold)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        if (playerJob != null && this.jobService.cancelJob(pt.getJobId(), true)) {
            this.playerDao.consumeGold(player, gold, ci.getName());
            this.finishResearch(new StringBuilder().append(pt.getVId()).toString());
            return JsonBuilder.getJson(State.SUCCESS, "");
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_TECH_FINISH_RESEARCH);
    }
    
    @Override
    public void finishResearch(final String params) {
        final int vId = Integer.parseInt(params);
        final PlayerTech pt = this.playerTechDao.read(vId);
        if (pt == null) {
            return;
        }
        this.playerTechDao.setCd(vId, new Date(), 5, 0);
        final Tech tech = (Tech)this.techCache.get((Object)pt.getTechId());
        final int playerId = pt.getPlayerId();
        final Player player = this.playerDao.read(playerId);
        this.techEffectCache.refreshTechEffect(playerId, tech.getKey());
        if (tech.getKey() == 6) {
            this.buildingOutputCache.clearTech(playerId, 3);
            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("foodOutput", this.buildingOutputCache.getBuildingsOutput(playerId, 3)));
        }
        else if (tech.getKey() == 8) {
            this.buildingOutputCache.clearTech(playerId, 5);
            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("forceOutput", this.buildingOutputCache.getBuildingsOutput(playerId, 5) / 60));
        }
        else if (tech.getKey() == 20) {
            this.buildingOutputCache.clearLimit(playerId);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("copperMax", this.buildingOutputCache.getBuildingOutput(playerId, 16));
            doc.createElement("woodMax", this.buildingOutputCache.getBuildingOutput(playerId, 32));
            doc.createElement("foodMax", this.buildingOutputCache.getBuildingOutput(playerId, 48));
            doc.createElement("ironMax", this.buildingOutputCache.getBuildingOutput(playerId, 64));
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
        }
        else if (tech.getKey() == 27) {
            this.generalService.sendGeneralMaxSizeInfo(playerId, player.getPlayerLv(), 2);
        }
        else if (tech.getKey() == 4) {
            this.generalService.sendGeneralMilitaryList(playerId);
        }
        else if (tech.getKey() == 22) {
            this.buildingOutputCache.clearTechGaoGuan(playerId);
        }
        else if (tech.getKey() == 28) {
            this.buildingOutputCache.clearTechBinZhong(playerId);
        }
        else if (tech.getKey() == 35) {
            this.buildingOutputCache.clearOfficer(playerId);
        }
        else if (tech.getKey() == 41) {
            final TaskRewardNewFunction trnf = new TaskRewardNewFunction(59);
            trnf.rewardPlayer(new PlayerDto(playerId), this.dataGetter, "\u96c6\u56e2\u519b\u79d1\u6280\u5f00\u653e\u96c6\u56e2\u519b\u521b\u5efa\u529f\u80fd", null);
            if (!TeamManager.leagueOpen(player.getForceId())) {
                TeamManager.setLeagueOpen(player.getForceId());
            }
        }
        else if (tech.getKey() == 45) {
            this.quenchingService.checkSpecialSkill(playerId);
        }
        else if (tech.getKey() == 46) {
            CityEventManager.getInstance().addPlayerEvent(player.getPlayerId(), 1);
            CityEventManager.getInstance().bobaoOnePlayerEvent(playerId, 1);
            try {
                Constants.locks[playerId % Constants.LOCKS_LEN].lock();
                if (this.techEffectCache.getTechEffect(playerId, 46) > 8) {
                    this.incenseService.addIncenseGod(playerId, 5);
                }
                final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward("functionId,28");
                final PlayerDto playerDto = new PlayerDto(playerId);
                playerDto.consumeLv = player.getConsumeLv();
                playerDto.cs = player.getForceId().toString().toCharArray();
                playerDto.forceId = player.getForceId();
                playerDto.loginTime = player.getLoginTime().getTime();
                playerDto.playerLv = player.getPlayerLv();
                playerDto.playerName = player.getPlayerName();
                playerDto.userId = player.getUserId();
                playerDto.yx = player.getYx();
                playerDto.yxSource = player.getYxSource();
                reward.rewardPlayer(playerDto, this.dataGetter, "\u5b9d\u77f3\u79d1\u6280\u5f00\u653e\u5b9d\u77f3\u796d\u7940\u529f\u80fd", null);
            }
            finally {
                Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
            }
            Constants.locks[playerId % Constants.LOCKS_LEN].unlock();
        }
        else if (tech.getKey() == 47) {
            CityEventManager.getInstance().addPlayerEvent(player.getPlayerId(), 2);
            CityEventManager.getInstance().bobaoOnePlayerEvent(playerId, 2);
        }
        else if (tech.getKey() == 59) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("autoBattleTechGain", true);
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_AUTO_BATTLE, doc.toByte());
        }
        else if (this.dataGetter.getWdSjpDramaCache().getDramaOpenList().contains(tech.getId())) {
            final boolean alreadyHas = CityEventManager.getInstance().alreadyHasWorldDramaEvent(playerId);
            final boolean isInWorldDrama = this.dataGetter.getJuBenService().isInWorldDrama(playerId);
            if (!alreadyHas && !isInWorldDrama) {
                final boolean add = CityEventManager.getInstance().addPlayerEvent(playerId, 5);
                if (add) {
                    CityEventManager.getInstance().bobaoOnePlayerEvent(playerId, 5);
                }
            }
        }
        TaskMessageHelper.sendTechResearchDoneTaskMessage(playerId, pt.getTechId());
        this.playerAttributeDao.setTechResearch(playerId, 1);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("techResearch", 1);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
    }
    
    @Override
    public void dropTech(final int playerId, final int techId) {
        int size = this.playerTechDao.getSizeByPlayerId(playerId);
        if (size < 2) {
            this.openTechFunction(playerId);
        }
        PlayerTech pt = this.playerTechDao.getPlayerTech(playerId, techId);
        Tech tech = null;
        if (pt != null) {
            if (pt.getStatus() != 0) {
                return;
            }
            final int result = this.playerTechDao.setStatusAndIsNew(playerId, techId, 2, 1);
            if (result >= 1) {
                this.playerAttributeDao.setTechOpen(playerId, 1);
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("displayTech", 1);
                doc.endObject();
                Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
            }
        }
        else {
            tech = (Tech)this.techCache.get((Object)techId);
            pt = new PlayerTech();
            pt.setCd(new Date());
            pt.setFinishNew(0);
            pt.setIsNew(1);
            pt.setJobId(0);
            pt.setKeyId(tech.getKey());
            pt.setNum(0);
            pt.setPlayerId(playerId);
            pt.setStatus(2);
            pt.setTechId(techId);
            this.playerTechDao.create(pt);
            this.playerAttributeDao.setTechOpen(playerId, 1);
            final JsonDocument doc2 = new JsonDocument();
            doc2.startObject();
            doc2.createElement("displayTech", 1);
            doc2.endObject();
            Players.push(playerId, PushCommand.PUSH_UPDATE, doc2.toByte());
        }
        size = this.playerTechDao.getSizeByPlayerId(playerId);
        tech = this.techCache.getDropTechByDropIndex(size);
        if (tech != null) {
            if (this.playerTechDao.getPlayerTech(playerId, tech.getId()) != null) {
                return;
            }
            pt = new PlayerTech();
            pt.setCd(new Date());
            pt.setFinishNew(0);
            pt.setIsNew(0);
            pt.setJobId(0);
            pt.setKeyId(tech.getKey());
            pt.setNum(0);
            pt.setPlayerId(playerId);
            pt.setStatus(0);
            pt.setTechId(tech.getId());
            this.playerTechDao.create(pt);
        }
    }
    
    @Override
    public int getTechEffect(final int playerId, final int techKey) {
        final List<PlayerTech> ptList = this.playerTechDao.getByTechKey1(playerId, techKey);
        if (ptList == null) {
            return 0;
        }
        int total = 0;
        for (final PlayerTech pt : ptList) {
            total += ((Tech)this.techCache.get((Object)pt.getTechId())).getPar1();
        }
        return total;
    }
    
    @Override
    public int getTechEffect2(final int playerId, final int techKey) {
        final List<PlayerTech> ptList = this.playerTechDao.getByTechKey1(playerId, techKey);
        if (ptList == null) {
            return 0;
        }
        int total = 0;
        for (final PlayerTech pt : ptList) {
            total += ((Tech)this.techCache.get((Object)pt.getTechId())).getPar2();
        }
        return total;
    }
    
    @Override
    public double getTechEffect3(final int playerId, final int techKey) {
        final List<PlayerTech> ptList = this.playerTechDao.getByTechKey1(playerId, techKey);
        if (ptList == null) {
            return 0.0;
        }
        double total = 0.0;
        for (final PlayerTech pt : ptList) {
            total += ((Tech)this.techCache.get((Object)pt.getTechId())).getPar3();
        }
        return total;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
    }
    
    @Override
    public void openTechFunction(final int playerId) {
        for (int i = 0; i < 2; ++i) {
            final Tech tech = this.techCache.getDropTechByDropIndex(i);
            if (tech != null) {
                if (this.playerTechDao.getPlayerTech(playerId, tech.getId()) == null) {
                    final PlayerTech pt = new PlayerTech();
                    pt.setCd(new Date());
                    pt.setFinishNew(0);
                    pt.setIsNew(0);
                    pt.setJobId(0);
                    pt.setKeyId(tech.getKey());
                    pt.setNum(0);
                    pt.setPlayerId(playerId);
                    pt.setStatus(0);
                    pt.setTechId(tech.getId());
                    this.playerTechDao.create(pt);
                }
            }
        }
    }
}
