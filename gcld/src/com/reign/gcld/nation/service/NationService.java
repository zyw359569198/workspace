package com.reign.gcld.nation.service;

import org.springframework.stereotype.*;
import com.reign.gcld.rank.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.nation.dao.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.chat.service.*;
import com.reign.framework.netty.util.*;
import com.reign.gcld.common.util.*;
import com.reign.util.*;
import com.reign.gcld.chat.common.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.nation.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.log.*;
import org.apache.commons.lang.*;
import java.util.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.battle.common.*;

@Component("nationService")
public class NationService implements INationService
{
    private static final Logger timerLog;
    private static final Logger errorLog;
    @Autowired
    private IForceInfoDao forceInfoDao;
    @Autowired
    private CdExamsCache cdExamsCache;
    @Autowired
    private KingdomLvCache kingdomLvCache;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private HallsCache hallsCache;
    @Autowired
    private IRankService rankService;
    @Autowired
    private IPlayerTryRankDao playerTryRankDao;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private KingdomTaskRankingCache kingdomTaskRankingCache;
    @Autowired
    private IChatService chatService;
    @Autowired
    private IPlayerOfficeRelativeDao playerOfficeRelativeDao;
    @Autowired
    private WnCitynpcLvCache wnCitynpcLvCache;
    private static ConcurrentMap<Integer, Tuple<Integer, Date>> tryMap;
    
    static {
        timerLog = new TimerLogger();
        errorLog = CommonLog.getLog(NationService.class);
        NationService.tryMap = new ConcurrentHashMap<Integer, Tuple<Integer, Date>>();
    }
    
    @Override
    public void initTryTask() {
        try {
            final List<ForceInfo> fiList = this.forceInfoDao.getModels();
            final Date now = new Date();
            if (fiList != null) {
                for (final ForceInfo fi : fiList) {
                    final Date date = fi.getTryEndTime();
                    if (date != null && date.after(now)) {
                        if (fi.getStage() >= 4) {
                            throw new RuntimeException("nation_try_stage_error#stage:" + fi.getStage() + "#tryEndTime:" + date + "#now:" + now);
                        }
                        NationService.tryMap.put(fi.getForceId(), new Tuple(fi.getStage(), fi.getTryEndTime()));
                        this.dataGetter.getJobService().addJob("nationService", "checkTryTask", String.valueOf(fi.getForceId()), date.getTime(), false);
                    }
                    else {
                        if (fi.getStage() >= 4) {
                            continue;
                        }
                        NationService.tryMap.put(fi.getForceId(), new Tuple(4, fi.getEndtime()));
                        this.forceInfoDao.updateStage(fi.getForceId(), 4);
                    }
                }
            }
            if (NationService.tryMap.size() < 3) {
                for (int i = 1; i <= 3; ++i) {
                    NationService.tryMap.putIfAbsent(i, new Tuple(4, (Object)null));
                }
            }
        }
        catch (Exception e) {
            NationService.errorLog.error("init tryTask fail", e);
        }
    }
    
    @Override
    public byte[] getNationInfo(final PlayerDto playerDto) {
        if (playerDto.cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final int forceId = playerDto.forceId;
        final ForceInfo fi = this.forceInfoDao.read(forceId);
        final int times = this.getTimes(fi.getIds());
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("times", times);
        final int totalNeedExp = this.cdExamsCache.getNeedExp(fi.getForceLv(), fi.getForceExp());
        final Tuple<Integer, Integer> tuple = this.cdExamsCache.getTuple(fi.getForceLv(), fi.getForceExp());
        int needExp = 0;
        int totalExp = 0;
        final int displayLv = this.cdExamsCache.getDisplayLv(fi.getForceLv(), fi.getForceExp());
        if (tuple != null) {
            needExp = tuple.left;
            totalExp = tuple.right;
        }
        doc.createElement("totalNeedExp", totalNeedExp);
        doc.createElement("displayLv", displayLv);
        doc.createElement("needExp", needExp);
        doc.createElement("totalExp", totalExp);
        doc.createElement("forceExp", fi.getForceExp());
        doc.createElement("forceLv", fi.getForceLv());
        if (times > 0) {
            doc.createElement("name", this.getName(fi.getIds()));
        }
        final long cd = this.getCd(fi.getTryEndTime());
        doc.createElement("cd", cd);
        doc.createElement("canUpgrade", this.canUpgrate(fi) ? 1 : 0);
        doc.createElement("cdUpgrade", this.getCdUpgrade(fi));
        doc.startArray("nations");
        final List<ForceInfo> fiList = this.forceInfoDao.getModels();
        for (final ForceInfo temp : fiList) {
            doc.startObject();
            doc.createElement("forceId", temp.getForceId());
            doc.createElement("forceLv", temp.getForceLv());
            doc.createElement("exp", temp.getForceExp());
            doc.createElement("maxExp", ((KindomLv)this.kingdomLvCache.get((Object)temp.getForceLv())).getExpUpgrade());
            doc.endObject();
        }
        doc.endArray();
        final int days = this.rankService.getCountryNpcDefDays();
        final WnCitynpcLv nowNpc = this.wnCitynpcLvCache.getWnCitynpcLvByDay(days);
        final WnCitynpcLv nextNpc = (WnCitynpcLv)this.wnCitynpcLvCache.get((Object)(nowNpc.getLv() + 1));
        final boolean isTopdefNpcLv = nextNpc == null;
        doc.createElement("defNpcLv", nowNpc.getGLv());
        doc.createElement("isTopdefNpcLv", isTopdefNpcLv);
        doc.createElement("currentPoint", isTopdefNpcLv ? 0 : (days - nowNpc.getDay()));
        doc.createElement("totalPoint", isTopdefNpcLv ? 0 : (nextNpc.getDay() - nowNpc.getDay()));
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] openTry(final PlayerDto playerDto) {
        if (playerDto.cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final int playerId = playerDto.playerId;
        final int officerId = this.playerOfficeRelativeDao.getOfficerId(playerId);
        if (((Halls)this.hallsCache.get((Object)officerId)).getOfficialId() > 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TRY_KING_AND_A_PRODUCT_OFFICIALS_CAN_OPEN);
        }
        final int forceId = playerDto.forceId;
        final ForceInfo fi = this.forceInfoDao.read(forceId);
        final int time = this.getTimes(fi.getIds());
        if (time <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TRY_NO_TIMES);
        }
        if (TimeUtil.in0To8()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TRY_0_TO_8_CLOCK);
        }
        if (this.rankService.hasNationTasks(forceId) > 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TRY_IN_NATION_TASK);
        }
        synchronized (Constants.lock) {
            final int stage = this.getStageByForceId(forceId);
            if (stage < 4) {
                // monitorexit(Constants.lock)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.TRY_IN_TRY);
            }
            long cd = this.getCd(fi.getTryEndTime());
            if (cd > 0L) {
                // monitorexit(Constants.lock)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.TRY_IN_CD);
            }
            final int degree = this.dataGetter.getRankService().hasBarTasks(playerDto.forceId);
            if (degree > 0) {
                // monitorexit(Constants.lock)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.TRY_IN_NATION_UPGRATE);
            }
            final List<ForceInfo> fiList = this.forceInfoDao.getModels();
            for (final ForceInfo temp : fiList) {
                if (temp.getForceId() != forceId) {
                    cd = this.getCd(temp.getTryEndTime());
                    final int stageOther = this.getStageByForceId(temp.getForceId());
                    if (cd > 0L && stageOther < 4) {
                        // monitorexit(Constants.lock)
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.TRY_OTHER_IN_CD);
                    }
                    continue;
                }
            }
            final int id = Integer.parseInt(fi.getIds().split(";")[0]);
            final CdExams cdExams = (CdExams)this.cdExamsCache.get((Object)id);
            this.dataGetter.getRankService().clearTryRank(forceId);
            final Date endTime = TimeUtil.nowAddHours(2);
            this.dataGetter.getBarbarainPhantomDao().removeAllInThisCity(WorldCityCommon.forcIdManzuCityIdMap.get(forceId));
            this.dataGetter.getBattleService().addBarbarainTryNpc(cdExams, 0, WorldCityCommon.playerManZuForceMap.get(forceId));
            NationService.tryMap.put(forceId, new Tuple(0, endTime));
            this.forceInfoDao.updateTryInfo(forceId, id, endTime, 0, 0, 0);
            this.dataGetter.getJobService().addJob("nationService", "checkTryTask", String.valueOf(forceId), endTime.getTime(), false);
        }
        // monitorexit(Constants.lock)
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.appendJson(this.getTryTaskInfo(playerId, forceId, this.forceInfoDao.read(forceId)));
        doc.createElement("tryTaskStage", 0);
        doc.endObject();
        Group g = null;
        if (1 == forceId) {
            g = GroupManager.getInstance().getGroup(ChatType.WORLD_OPENED_1.toString());
        }
        else if (2 == forceId) {
            g = GroupManager.getInstance().getGroup(ChatType.WORLD_OPENED_2.toString());
        }
        else if (3 == forceId) {
            g = GroupManager.getInstance().getGroup(ChatType.WORLD_OPENED_3.toString());
        }
        if (g != null) {
            final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_UPDATE.getModule(), doc.toByte()));
            g.notify((String)null, WrapperUtil.wrapper(PushCommand.PUSH_UPDATE.getCommand(), 0, bytes));
        }
        final String msg = MessageFormatter.format(LocalMessages.TRY_TASK_START, new Object[] { ColorUtil.getForceMsg(forceId, WebUtil.getForceName(forceId)) });
        this.chatService.sendManWangChat("GLOBAL", playerId, forceId, msg, null);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] getTryInfo(final PlayerDto playerDto) {
        if (playerDto.cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final int forceId = playerDto.forceId;
        final int stage = this.getStageByForceId(forceId);
        final ForceInfo fi = this.forceInfoDao.read(forceId);
        final int playerId = playerDto.playerId;
        final int killNum = this.rankService.getTryRank().getValue(forceId, playerId);
        if (stage >= 4 && (fi.getTryWin() != 1 || killNum <= 0)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TRY_NOT_IN_TRY);
        }
        final long cd = (fi.getTryEndTime() == null) ? 0L : TimeUtil.now2specMs(fi.getTryEndTime().getTime());
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("stage", stage);
        int id = 0;
        int rate = 0;
        if (stage >= 4) {
            id = fi.getId();
            rate = 100;
        }
        else {
            id = Integer.parseInt(fi.getIds().split(";")[0]);
            rate = (int)(fi.getGeneralNum() * 1.0 / this.cdExamsCache.getGeneralNum(id, 3) * 100.0);
        }
        final int manCityId = WorldCityCommon.forcIdManzuCityIdMap.get(forceId);
        final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)manCityId);
        doc.createElement("cityId", manCityId);
        doc.createElement("cityName", wc.getName());
        doc.createElement("name", ((CdExams)this.cdExamsCache.get((Object)id)).getName2());
        doc.createElement("rate", rate);
        doc.createElement("cd", cd);
        final int rank = this.rankService.getTryRank().getRank(1, playerId, forceId);
        doc.createElement("killNum", killNum);
        doc.createElement("rank", rank);
        final int lvBefore = this.kingdomTaskRankingCache.getTaskRankingLv(rank, 0);
        final String titlePic = this.kingdomTaskRankingCache.getTitlePic(lvBefore, 0);
        doc.createElement("title", titlePic);
        int isTopTitle = 0;
        if (1 == lvBefore) {
            isTopTitle = 1;
        }
        int needKillNum = 0;
        if (rank > 1) {
            final RankData rd = this.dataGetter.getRankService().getTryRank().getRankNum(forceId, rank - 2);
            if (rd != null) {
                needKillNum = rd.value - killNum;
            }
        }
        if (1 != rank && needKillNum <= 0) {
            needKillNum = 1;
        }
        doc.createElement("needKillNum", needKillNum);
        doc.createElement("isTopTitle", isTopTitle);
        final CdExams ce = (CdExams)this.cdExamsCache.get((Object)id);
        final int winExp = ce.getWinRExp();
        final int winIron = ce.getWinRIron();
        doc.createElement("winExp", winExp);
        doc.createElement("winIron", winIron);
        doc.createElement("exp", this.cdExamsCache.getRankingExp(id, rank));
        doc.createElement("iron", this.cdExamsCache.getRankingIron(id, rank));
        doc.startArray("stages");
        final List<Tuple<Integer, Integer>> tupleList = this.cdExamsCache.getKillList(id);
        for (final Tuple<Integer, Integer> tuple : tupleList) {
            doc.startObject();
            doc.createElement("stage", tuple.left);
            doc.createElement("num", tuple.right);
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void createPlayerTryRank(final int playerId) {
        PlayerTryRank ptr = this.playerTryRankDao.read(playerId);
        if (ptr == null) {
            ptr = new PlayerTryRank();
            ptr.setPlayerId(playerId);
            ptr.setNum(0);
            ptr.setReceived(1);
            this.playerTryRankDao.create(ptr);
        }
    }
    
    @Override
    public byte[] getReward(final PlayerDto playerDto) {
        if (playerDto.cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final int forceId = playerDto.forceId;
        final ForceInfo fi = this.forceInfoDao.read(forceId);
        if (fi.getTryWin() != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TRY_NOT_WIN);
        }
        final int playerId = playerDto.playerId;
        final PlayerTryRank ptr = this.playerTryRankDao.read(playerId);
        if (1 == ptr.getReceived()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TRY_RECEIVED);
        }
        if (ptr.getNum() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.TRY_NOT_JOIN);
        }
        this.playerTryRankDao.received(playerId);
        final int rank = this.rankService.getTryRank().getRank(1, playerId, forceId);
        final int id = fi.getId();
        final CdExams ce = (CdExams)this.cdExamsCache.get((Object)id);
        final int winExp = ce.getWinRExp();
        final int winIron = ce.getWinRIron();
        final int rankExp = this.cdExamsCache.getRankingExp(id, rank);
        final int rankIron = this.cdExamsCache.getRankingIron(id, rank);
        this.playerService.updateExpAndPlayerLevel(playerId, winExp, "\u56fd\u5bb6\u8bd5\u70bc\u80dc\u5229\u5956\u52b1\u7ecf\u9a8c");
        this.playerService.updateExpAndPlayerLevel(playerId, rankExp, "\u56fd\u5bb6\u8bd5\u70bc\u6392\u540d\u5956\u52b1\u7ecf\u9a8c");
        this.playerResourceDao.addIronIgnoreMax(playerId, winIron, "\u56fd\u5bb6\u8bd5\u70bc\u80dc\u5229\u83b7\u53d6\u9554\u94c1", true);
        this.playerResourceDao.addIronIgnoreMax(playerId, rankIron, "\u56fd\u5bb6\u8bd5\u70bc\u6392\u540d\u83b7\u53d6\u9554\u94c1", true);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("winExp", winExp);
        doc.createElement("winIron", winIron);
        doc.createElement("rankExp", rankExp);
        doc.createElement("rankIron", rankIron);
        doc.endObject();
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.appendJson(this.getTryTaskInfo(playerId, forceId, fi));
        doc2.endObject();
        Players.push(playerId, PushCommand.PUSH_UPDATE, doc2.toByte());
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public Map<Integer, Tuple<Integer, Date>> getTryMap() {
        return NationService.tryMap;
    }
    
    @Override
    public boolean haveTryReward(final int playerId, final int forceId) {
        final ForceInfo fi = this.forceInfoDao.read(forceId);
        if (fi.getTryWin() != 1) {
            return false;
        }
        final PlayerTryRank ptr = this.playerTryRankDao.read(playerId);
        return 1 != ptr.getReceived() && ptr.getNum() > 0;
    }
    
    @Override
    public byte[] getTryTaskInfo(final int playerId, final int forceId, final ForceInfo fi) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject("tryTasks");
        if (this.playerAttributeDao.getFunctionId(playerId).toCharArray()[10] == '1') {
            if (this.getStageByForceId(forceId) < 4) {
                doc.createElement("cd", (Object)TimeUtil.now2specMs(fi.getTryEndTime().getTime()));
                doc.createElement("state", 0);
            }
            else if (fi.getTryWin() == 1) {
                doc.createElement("state", 2);
            }
            else {
                doc.createElement("state", 1);
            }
            final int cityId = WorldCityCommon.forcIdManzuCityIdMap.get(forceId);
            final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
            doc.createElement("cityId", cityId);
            doc.createElement("cityName", wc.getName());
            doc.createElement("hasReward", this.haveTryReward(playerId, forceId));
        }
        doc.endObject();
        return doc.toByte();
    }
    
    @Override
    public byte[] getTryTaskInfo() {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("tryTasks");
        for (int i = 1; i <= 3; ++i) {
            final int stage = this.getStageByForceId(i);
            if (stage < 4) {
                final int manCityId = WorldCityCommon.forcIdManzuCityIdMap.get(i);
                doc.startObject();
                doc.createElement("cityId", manCityId);
                final ForceInfo fi = this.forceInfoDao.read(i);
                long endTime = this.getTryTaskCd(i);
                if (endTime > 0L) {
                    endTime = TimeUtil.now2specMs(endTime);
                }
                doc.createElement("endTime", endTime);
                doc.createElement("killNum", fi.getGeneralNum());
                doc.createElement("requestKillNum", this.dataGetter.getCdExamsCache().getGeneralNum(fi.getId(), 3));
                final CdExams cdExams = (CdExams)this.dataGetter.getCdExamsCache().get((Object)fi.getId());
                final CdExamsObj ceo = this.dataGetter.getBattleService().getCdExamsObjByStageAndForceId(cdExams, 0, i);
                final int armyId = Integer.parseInt(ceo.getArmyIds().split(";")[0]);
                doc.createElement("pic", ((General)this.dataGetter.getGeneralCache().get((Object)armyId)).getPic());
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        return doc.toByte();
    }
    
    @Override
    public int getStageByForceId(final int forceId) {
        if (NationService.tryMap.containsKey(forceId)) {
            return NationService.tryMap.get(forceId).left;
        }
        NationService.tryMap.put(forceId, new Tuple(4, (Object)null));
        return 4;
    }
    
    @Override
    public void checkTryTask(final String param) {
        final long start = System.currentTimeMillis();
        try {
            NationService.timerLog.info(LogUtil.formatThreadLog("RankService", "checkTryTask", 0, 0L, "param:" + param));
            if (StringUtils.isBlank(param)) {
                NationService.errorLog.error("#class:RankService#method:checkTryTask#param:" + param);
            }
            final int forceId = Integer.parseInt(param);
            if (forceId < 1 || forceId > 3) {
                NationService.errorLog.error("#class:RankService#method:checkTryTask#param:" + param + "#forceId:" + forceId);
                NationService.timerLog.info(LogUtil.formatThreadLog("RankService", "checkTryTask", 2, System.currentTimeMillis() - start, "param:" + param));
                return;
            }
            if (this.dataGetter.getNationService().getTryMap() != null) {
                final int stage = this.getStageByForceId(forceId);
                if (stage < 4) {
                    final ForceInfo fi = this.forceInfoDao.read(forceId);
                    boolean success = false;
                    if (stage >= 0 && stage < 2) {
                        success = false;
                    }
                    else if (stage == 3) {
                        final int id = fi.getId();
                        final int generalNum = this.dataGetter.getCdExamsCache().getGeneralNum(id, stage);
                        success = (fi.getGeneralNum() >= generalNum);
                    }
                    if (success) {
                        this.dataGetter.getForceInfoDao().updateTryWin(forceId, new Date(), 4);
                        this.pushTryTaskResult(forceId, true);
                    }
                    else {
                        this.dataGetter.getForceInfoDao().updateTryFail(forceId, 4);
                        this.pushTryTaskResult(forceId, false);
                        this.chatService.sendManWangChat("GLOBAL", 0, forceId, LocalMessages.TRY_TASK_FAIL, null);
                    }
                    this.dataGetter.getNationService().getTryMap().put(forceId, new Tuple(4, new Date()));
                    final ForceInfo fiNew = this.dataGetter.getForceInfoDao().read(forceId);
                    for (final PlayerDto dto : Players.getAllPlayerByForceId(forceId)) {
                        if (dto.cs[10] == '1') {
                            final JsonDocument doc = new JsonDocument();
                            doc.startObject();
                            doc.appendJson(this.dataGetter.getNationService().getTryTaskInfo(dto.playerId, dto.forceId, fiNew));
                            doc.endObject();
                            Players.push(dto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
                        }
                    }
                    ManWangLingManager.getInstance().clearProtectManWangLingByFromForceId(forceId);
                    final List<Integer> toForceIdList = ManWangLingManager.getInstance().getToForceId(forceId);
                    for (final int toForceId : toForceIdList) {
                        if (toForceId > 0 && toForceId < 4) {
                            ManWangLingManager.getInstance().removeManWangLingObj(toForceId, 2);
                            final ForceInfo forceInfoOther = this.forceInfoDao.read(toForceId);
                            if (forceInfoOther.getPWin() != 0) {
                                continue;
                            }
                            this.dataGetter.getProtectService().pushPTaskResult(toForceId, false);
                            for (final PlayerDto dto2 : Players.getAllPlayerByForceId(toForceId)) {
                                final JsonDocument doc2 = new JsonDocument();
                                doc2.startObject();
                                doc2.appendJson(this.dataGetter.getProtectService().getProtectTaskInfo(dto2.playerId));
                                doc2.endObject();
                                Players.push(dto2.playerId, PushCommand.PUSH_UPDATE, doc2.toByte());
                            }
                        }
                    }
                }
                else {
                    NationService.errorLog.error("#class:RankService#method:checkTryTask#param:" + param + "#tryTask_already_finished");
                }
            }
            else {
                NationService.errorLog.error("#class:RankService#method:checkTryTask#param:" + param + "#sytem_start#tryMap_is_null");
            }
        }
        catch (Exception e) {
            NationService.errorLog.error("#class:RankService#method:checkTryTask#param:" + param, e);
        }
        NationService.timerLog.info(LogUtil.formatThreadLog("RankService", "checkTryTask", 2, System.currentTimeMillis() - start, "param:" + param));
    }
    
    @Override
    public synchronized void addKillGeneralNum(final String param) {
        final long start = System.currentTimeMillis();
        try {
            NationService.timerLog.info(LogUtil.formatThreadLog("RankService", "addKillGeneralNum", 0, 0L, "param:" + param));
            if (StringUtils.isBlank(param)) {
                NationService.errorLog.error("#class:RankService#method:addKillGeneralNum#param:" + param);
            }
            final String[] tempArr = param.split("#");
            if (tempArr == null || tempArr.length != 2) {
                NationService.errorLog.error("#class:RankService#method:addKillGeneralNum#param:" + param + "#tempArr:" + tempArr);
                NationService.timerLog.info(LogUtil.formatThreadLog("RankService", "addKillGeneralNum", 2, System.currentTimeMillis() - start, "param:" + param));
                return;
            }
            final int forceId = Integer.parseInt(tempArr[0]);
            final int killGeneralNum = Integer.parseInt(tempArr[1]);
            if (forceId < 1 || forceId > 3 || killGeneralNum <= 0) {
                NationService.errorLog.error("#class:RankService#method:addKillGeneralNum#param:" + param + "#forceId:" + forceId + "#killGeneralNum" + killGeneralNum);
                NationService.timerLog.info(LogUtil.formatThreadLog("RankService", "addKillGeneralNum", 2, System.currentTimeMillis() - start, "param:" + param));
                return;
            }
            final int stage = this.getStageByForceId(forceId);
            if (stage < 4) {
                this.dataGetter.getForceInfoDao().addGeneralNum(forceId, killGeneralNum);
                final ForceInfo fi = this.forceInfoDao.read(forceId);
                final int lastGeneralNum = fi.getGeneralNum();
                final int id = fi.getId();
                final CdExams cdExams = (CdExams)this.dataGetter.getCdExamsCache().get((Object)id);
                final CdExamsObj ceo = this.dataGetter.getBattleService().getCdExamsObjByStageAndForceId(cdExams, stage, forceId);
                boolean push = false;
                if (stage == 0 || stage == 1) {
                    if (lastGeneralNum >= ceo.getOpenNextNum()) {
                        this.dataGetter.getBattleService().addBarbarainTryNpc(cdExams, stage + 1, WorldCityCommon.playerManZuForceMap.get(forceId));
                        this.forceInfoDao.updateStage(forceId, stage + 1);
                        NationService.tryMap.put(forceId, new Tuple(stage + 1, NationService.tryMap.get(forceId).right));
                        push = true;
                        if (stage == 0) {
                            this.chatService.sendManWangChat("GLOBAL", 0, forceId, LocalMessages.TRY_TASK_1, null);
                        }
                        else {
                            this.chatService.sendManWangChat("GLOBAL", 0, forceId, LocalMessages.TRY_TASK_2, null);
                        }
                    }
                }
                else if (stage == 2) {
                    if (lastGeneralNum >= ceo.getOpenNextNum()) {
                        this.dataGetter.getBattleService().addBarbarainTryNpc(cdExams, stage + 1, WorldCityCommon.playerManZuForceMap.get(forceId));
                        this.forceInfoDao.updateStage(forceId, stage + 1);
                        NationService.tryMap.put(forceId, new Tuple(stage + 1, NationService.tryMap.get(forceId).right));
                        final CdExamsObj ceo2 = this.dataGetter.getBattleService().getCdExamsObjByStageAndForceId(cdExams, stage + 1, forceId);
                        final String cityIds = ceo2.getCityIds();
                        if (StringUtils.isBlank(cityIds)) {
                            NationService.errorLog.error("#class:RankService#method:addKillGeneralNum#param:" + param + "#cityIds:" + cityIds);
                        }
                        else {
                            final List<Integer> cityIdList = new ArrayList<Integer>();
                            String[] split;
                            for (int length = (split = cityIds.split(";")).length, j = 0; j < length; ++j) {
                                final String temp = split[j];
                                if (StringUtils.isNotBlank(temp)) {
                                    final int cityId = Integer.parseInt(temp);
                                    final City city = this.dataGetter.getCityDao().read(cityId);
                                    if (city.getForceId() == forceId) {
                                        cityIdList.add(cityId);
                                    }
                                    else {
                                        NationService.errorLog.error("#class:RankService#method:addKillGeneralNum#param:" + param + "#cityIds:" + cityIds + "#removeCityId:" + cityId + "ordForceId:" + city.getForceId());
                                    }
                                }
                            }
                            if (cityIdList.size() > 0) {
                                final int randomId = WebUtil.nextInt(cityIdList.size());
                                final int cityId2 = cityIdList.get(randomId);
                                final int[] forceIds = this.getTokenForceId(forceId);
                                for (int i = 0; i < forceIds.length; ++i) {
                                    final boolean succ = this.dataGetter.getBattleService().fireManWangLing(forceId, forceIds[i], cityId2, fi.getTryEndTime().getTime());
                                    if (succ) {
                                        this.dataGetter.getRankService().clearPRank(forceIds[i]);
                                        this.forceInfoDao.startPTask(forceIds[i], forceId, cityId2, id);
                                        final ManWangLingObj obj = ManWangLingManager.getInstance().getProtectManWangLingByToForceId(forceIds[i]);
                                        if (obj != null) {
                                            final JsonDocument doc = new JsonDocument();
                                            doc.startObject();
                                            doc.startObject("protectTasks");
                                            doc.createElement("cd", (Object)TimeUtil.now2specMs(obj.expireTime));
                                            doc.createElement("state", 0);
                                            doc.endObject();
                                            doc.endObject();
                                            for (final PlayerDto dto : Players.getAllPlayerByForceId(forceIds[i])) {
                                                if (dto.cs[10] == '1') {
                                                    Players.push(dto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
                                                }
                                            }
                                        }
                                        this.chatService.sendManWangChat("GLOBAL", 0, forceIds[i], LocalMessages.TRY_TASK_3, null);
                                    }
                                }
                            }
                            else {
                                NationService.errorLog.error("#class:RankService#method:addKillGeneralNum#param:" + param + "#cityIds:" + cityIds + "#can't chooseCity:");
                            }
                        }
                        push = true;
                    }
                }
                else if (lastGeneralNum >= this.dataGetter.getCdExamsCache().getGeneralNum(id, stage)) {
                    this.dataGetter.getForceInfoDao().updateTryWin(forceId, new Date(), 4);
                    NationService.tryMap.put(forceId, new Tuple(stage + 1, new Date()));
                    this.dataGetter.getNationService().pushTryTaskResult(forceId, true);
                    final ForceInfo fiNew = this.dataGetter.getForceInfoDao().read(forceId);
                    for (final PlayerDto dto2 : Players.getAllPlayerByForceId(forceId)) {
                        if (dto2.cs[10] == '1') {
                            final JsonDocument doc2 = new JsonDocument();
                            doc2.startObject();
                            doc2.appendJson(this.dataGetter.getNationService().getTryTaskInfo(dto2.playerId, dto2.forceId, fiNew));
                            doc2.endObject();
                            Players.push(dto2.playerId, PushCommand.PUSH_UPDATE, doc2.toByte());
                        }
                    }
                    ManWangLingManager.getInstance().clearProtectManWangLingByFromForceId(forceId);
                    final List<Integer> toForceIdList = ManWangLingManager.getInstance().getToForceId(forceId);
                    for (final int toForceId : toForceIdList) {
                        if (toForceId > 0 && toForceId < 4) {
                            ManWangLingManager.getInstance().removeManWangLingObj(toForceId, 2);
                            final ForceInfo recv = this.dataGetter.getForceInfoDao().read(toForceId);
                            if (recv.getPWin() != 0) {
                                continue;
                            }
                            this.dataGetter.getProtectService().pushPTaskResult(toForceId, false);
                            for (final PlayerDto dto3 : Players.getAllPlayerByForceId(toForceId)) {
                                final JsonDocument doc3 = new JsonDocument();
                                doc3.startObject();
                                doc3.appendJson(this.dataGetter.getProtectService().getProtectTaskInfo(dto3.playerId));
                                doc3.endObject();
                                Players.push(dto3.playerId, PushCommand.PUSH_UPDATE, doc3.toByte());
                            }
                        }
                    }
                    this.chatService.sendManWangChat("GLOBAL", 0, forceId, LocalMessages.TRY_TASK_FINISH, null);
                }
                if (push) {
                    final byte[] send = JsonBuilder.getSimpleJson("tryTaskStage", stage + 1);
                    for (final PlayerDto dto2 : Players.getAllPlayerByForceId(forceId)) {
                        if (dto2.cs[10] == '1') {
                            Players.push(dto2.playerId, PushCommand.PUSH_UPDATE, send);
                        }
                    }
                }
            }
            else {
                NationService.errorLog.error("#class:RankService#method:addKillGeneralNum#param:" + param + "#forceId:" + forceId + "#try_stage_no_open");
            }
        }
        catch (Exception e) {
            NationService.errorLog.error("#class:RankService#method:addKillGeneralNum#param:" + param, e);
        }
        NationService.timerLog.info(LogUtil.formatThreadLog("RankService", "addKillGeneralNum", 2, System.currentTimeMillis() - start, "param:" + param));
    }
    
    private int getTimes(final String ids) {
        if (StringUtils.isBlank(ids)) {
            return 0;
        }
        return ids.split(";").length;
    }
    
    private String getName(final String ids) {
        if (StringUtils.isBlank(ids)) {
            return "";
        }
        final int id = Integer.parseInt(ids.split(";")[0]);
        return ((CdExams)this.cdExamsCache.get((Object)id)).getName();
    }
    
    private long getCd(final Date date) {
        if (date == null) {
            return 0L;
        }
        long result = TimeUtil.now2specMs(date.getTime());
        result += 10800000L;
        return (result <= 0L) ? 0L : result;
    }
    
    private long getCdUpgrade(final ForceInfo forceInfo) {
        final long now = System.currentTimeMillis();
        final long endTime = (forceInfo.getEndtime() == null) ? 0L : forceInfo.getEndtime().getTime();
        final long cd = endTime + 86400000L - 14400000L - now;
        return (cd > 0L) ? cd : 0L;
    }
    
    private boolean canUpgrate(final ForceInfo forceInfo) {
        final long now = System.currentTimeMillis();
        final long endTime = (forceInfo.getEndtime() == null) ? 0L : forceInfo.getEndtime().getTime();
        return endTime <= now && endTime + 86400000L - 14400000L <= now && forceInfo.getStage() >= 4 && forceInfo.getForceLv() < this.kingdomLvCache.maxLv && forceInfo.getForceExp() >= ((KindomLv)this.kingdomLvCache.get((Object)forceInfo.getForceLv())).getExpUpgrade();
    }
    
    private int[] getTokenForceId(final int forceId) {
        final int[] forceIds = new int[2];
        if (1 == forceId) {
            forceIds[0] = 2;
            forceIds[1] = 3;
        }
        else if (2 == forceId) {
            forceIds[0] = 3;
            forceIds[1] = 1;
        }
        else if (3 == forceId) {
            forceIds[forceIds[0] = 1] = 2;
        }
        return forceIds;
    }
    
    @Override
    public long getTryTaskCd(final int forceId) {
        try {
            final Tuple<Integer, Date> tuple = NationService.tryMap.get(forceId);
            if (tuple == null) {
                return 0L;
            }
            final Date date = tuple.right;
            return (date == null) ? 0L : date.getTime();
        }
        catch (Exception e) {
            NationService.errorLog.error("class:nationService#method:getTryTaskCd#forceId:" + forceId, e);
            return 0L;
        }
    }
    
    @Override
    public void pushTryTaskResult(final int forceId, final boolean result) {
        final byte[] send = JsonBuilder.getSimpleJson("tryTaskSuccess", result ? 1 : 0);
        for (final PlayerDto dto : Players.getAllPlayerByForceId(forceId)) {
            if (dto.cs[10] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
    }
}
