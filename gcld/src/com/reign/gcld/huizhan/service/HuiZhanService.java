package com.reign.gcld.huizhan.service;

import org.springframework.stereotype.*;
import com.reign.gcld.rank.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.world.dao.*;
import com.reign.gcld.huizhan.dao.*;
import com.reign.gcld.job.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.mail.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.player.common.*;
import com.reign.gcld.common.log.*;
import com.reign.framework.json.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.huizhan.domain.*;
import com.reign.gcld.player.domain.*;
import java.util.*;
import java.text.*;
import com.reign.util.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.log.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.world.domain.*;

@Component("huiZhanService")
public class HuiZhanService implements IHuiZhanService
{
    @Autowired
    private IForceInfoDao forceInfoDao;
    @Autowired
    private ICityDao cityDao;
    @Autowired
    private IHuizhanHistoryDao huizhanHistoryDao;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IJobService jobService;
    @Autowired
    private BtGatherRankingCache btGatherRankingCache;
    @Autowired
    private KingdomTaskRankingCache kingdomTaskRankingCache;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IMailService mailService;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IResourceUpdateSynService resourceUpdateSynService;
    private static HuizhanHistory todayHuiZhan;
    private static int hzTotalNum;
    private static Map<Integer, Integer> hzWinNumMap;
    private static final Logger errorLog;
    private static final Logger timerLog;
    private static int initialDefForceId;
    public static long pushTime;
    
    static {
        HuiZhanService.hzWinNumMap = new HashMap<Integer, Integer>();
        errorLog = CommonLog.getLog(HuiZhanService.class);
        timerLog = new TimerLogger();
        HuiZhanService.initialDefForceId = 3;
        HuiZhanService.pushTime = 0L;
    }
    
    @Override
    public byte[] startHuizhan() {
        this.startHuiZhanForTimer("");
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("success", 1);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void startHuiZhanForTimer(final String param) {
        final Calendar calendar = this.getNextHzTime();
        this.jobService.addJob("huiZhanService", "startHuiZhanForTimer", "", calendar.getTimeInMillis(), false);
        HuiZhanService.errorLog.error("Timer: startHuiZhanForTimer has been set up successfully .....");
        this.jobService.addJob("huiZhanService", "pushHuiZhanIconForTimer", "1_0", calendar.getTimeInMillis() - 86400000L, false);
        this.jobService.addJob("huiZhanService", "pushHuiZhanIconForTimer", "1_3600000", calendar.getTimeInMillis() - 3600000L, false);
        final int maxLv = this.forceInfoDao.getMaxLv();
        if (maxLv < 5) {
            return;
        }
        this.createHuizhan(1, new Date());
        HuiZhanService.todayHuiZhan = this.huizhanHistoryDao.getLatestHuizhan();
        this.huiZhanInStatePreparation();
    }
    
    private void createHuizhan(final int hzState, final Date hzStartTime) {
        int defForceId = 0;
        final HuizhanHistory hh = this.huizhanHistoryDao.getLatestHuizhan();
        if (hh == null) {
            defForceId = HuiZhanService.initialDefForceId;
        }
        else {
            defForceId = ((hh.getAttForce1() > hh.getAttForce2()) ? hh.getAttForceId1() : hh.getAttForceId2());
        }
        final int cityId = WorldCityCommon.forceIdSpecialCityMap.get(defForceId);
        final int attForceId1 = WorldCityCommon.getOther2ForceIds(defForceId)[0];
        final int attForceId2 = WorldCityCommon.getOther2ForceIds(defForceId)[1];
        final HuizhanHistory newHh = new HuizhanHistory();
        newHh.setStartTime(hzStartTime);
        newHh.setCityId(cityId);
        newHh.setWinner(-1);
        newHh.setGatherFlag(0);
        newHh.setDefForce(0L);
        newHh.setDefForceId(defForceId);
        newHh.setAttForce1(0L);
        newHh.setAttForce2(0L);
        newHh.setAttForceId1(attForceId1);
        newHh.setAttForceId2(attForceId2);
        newHh.setState(hzState);
        this.huizhanHistoryDao.create(newHh);
    }
    
    @Override
    public void huiZhanInStatePreparation() {
        this.pushHuiZhanGatherIcon(true);
        this.handleUnReceivedReward();
        RankService.HuiZhanKillRanker.clear();
        RankService.huiZhanForceRanker.clear();
        this.dataGetter.getBattleService().clearBattleForHuiZhan(HuiZhanService.todayHuiZhan.getCityId(), HuiZhanService.todayHuiZhan.getDefForceId());
        final long time1 = HuiZhanService.todayHuiZhan.getStartTime().getTime() + 300000L;
        this.jobService.addJob("huiZhanService", "huiZhanInStateDoing", "", time1, false);
        HuiZhanService.errorLog.error("Timer: huiZhanInDoing has been set up successfully .....#hzId:" + HuiZhanService.todayHuiZhan.getVId());
        final long time2 = HuiZhanService.todayHuiZhan.getStartTime().getTime() + 3000000L;
        this.jobService.addJob("huiZhanService", "huiZhanInStateOver", "", time2, false);
        HuiZhanService.errorLog.error("Timer: huiZhanInStateOver has been set up successfully .....#hzId:" + HuiZhanService.todayHuiZhan.getVId());
        final long time3 = HuiZhanService.todayHuiZhan.getStartTime().getTime() + 300000L - System.currentTimeMillis();
        this.pushHuiZhanTaskInfo(1, (int)time3);
        this.pushHuiZhanIcon(true, 0);
        this.dataGetter.getBattleService().refreshWorld();
    }
    
    @Override
    public void huiZhanInStateDoing(final String key) {
        HuiZhanService.errorLog.error("Timer: huizhanInDoing starts.....#hzId:" + HuiZhanService.todayHuiZhan.getVId());
        this.updateHzStateById(2, HuiZhanService.todayHuiZhan.getVId());
        this.pushHuiZhanGatherIcon(false);
        final long time = HuiZhanService.todayHuiZhan.getStartTime().getTime() + 3000000L - System.currentTimeMillis();
        this.pushHuiZhanTaskInfo(2, (int)time);
    }
    
    @Override
    public void huiZhanInStateOver(final String key) {
        HuiZhanService.errorLog.error("Timer: huiZhanInStateOver starts.....#hzId:" + HuiZhanService.todayHuiZhan.getVId());
        if (HuiZhanService.todayHuiZhan.getState() == 2) {
            this.updateHzStateById(3, HuiZhanService.todayHuiZhan.getVId());
            this.huizhanHistoryDao.updateWinnerByVid(0, HuiZhanService.todayHuiZhan.getVId());
            this.huizhanHistoryDao.updateHzEndTimeById(new Date(), HuiZhanService.todayHuiZhan.getVId());
            this.pushHuiZhanTaskInfo(3, 0);
            this.resetTodayHuiZhan();
            this.pushHuiZhanIcon(false, 0);
            this.addHzTotalNum();
        }
    }
    
    private void pushHuiZhanGatherIcon(final boolean isShowUp) {
        try {
            final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
            for (final PlayerDto dto : onlinePlayerList) {
                if (dto == null) {
                    continue;
                }
                if (dto.cs[24] != '1') {
                    continue;
                }
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("hasHuiZhanGatherIcon", isShowUp);
                doc.endObject();
                Players.push(dto.playerId, PushCommand.PUSH_HUIZHAN_GATHER, doc.toByte());
            }
        }
        catch (Exception e) {
            HuiZhanService.errorLog.error("className:HuiZhanService#methodName:pushHuiZhanGatherIcon", e);
        }
    }
    
    @Override
    public void pushHuiZhanTaskInfo(final int state, final int countDown) {
        try {
            final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
            for (final PlayerDto dto : onlinePlayerList) {
                if (dto == null) {
                    continue;
                }
                if (dto.cs[24] != '1') {
                    continue;
                }
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                if (state == 3 || state == 5) {
                    final boolean hasHzReward = this.hasHzRewards(dto.playerId);
                    doc.createElement("hasHzReward", hasHzReward);
                }
                doc.createElement("hzState", state);
                doc.createElement("hzCountDown", countDown);
                doc.endObject();
                Players.push(dto.playerId, PushCommand.PUSH_HUIZHAN_TASK_INFO, doc.toByte());
            }
        }
        catch (Exception e) {
            HuiZhanService.errorLog.error("className:BattleService#methodName:pushHuiZhanTaskInfo", e);
        }
    }
    
    @Override
    public void pushHuiZhanTaskInfoForSinglePlayer(final int playerId, final int state) {
        try {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("hzState", state);
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_HUIZHAN_TASK_INFO, doc.toByte());
        }
        catch (Exception e) {
            HuiZhanService.errorLog.error("className:BattleService#methodName:pushHuiZhanTaskInfoForSinglePlayer", e);
        }
    }
    
    public void pushHuiZhanIconForTimer(final String key) {
        final String hasHzStr = key.split("_")[0];
        final String countDown = key.split("_")[1];
        boolean hasHz = false;
        if (hasHzStr.equalsIgnoreCase("1")) {
            hasHz = true;
        }
        final int maxLv = this.forceInfoDao.getMaxLv();
        if (maxLv < 5) {
            return;
        }
        this.pushHuiZhanIcon(hasHz, Integer.parseInt(countDown));
    }
    
    @Override
    public void pushHuiZhanIcon(final boolean hasHuiZhan, final int countDown) {
        try {
            final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
            for (final PlayerDto dto : onlinePlayerList) {
                if (dto == null) {
                    continue;
                }
                if (dto.cs[24] != '1') {
                    continue;
                }
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("hasHuiZhan", hasHuiZhan);
                doc.createElement("hzIconCountDown", countDown);
                doc.endObject();
                Players.push(dto.playerId, PushCommand.PUSH_HUIZHAN_ICON, doc.toByte());
            }
        }
        catch (Exception e) {
            HuiZhanService.errorLog.error("className:BattleService#methodName:pushHuiZhanTaskInfo", e);
        }
    }
    
    @Override
    public void getHuiZhanTaskInfoForLogin(final int playerId, final JsonDocument doc) {
        final HuizhanHistory hh = this.dataGetter.getHuizhanHistoryDao().getLatestHuizhan();
        if (hh != null) {
            int countDown = 0;
            int state = 0;
            if (hh.getState() == 1) {
                countDown = (int)(hh.getStartTime().getTime() + 300000L - System.currentTimeMillis());
                state = 1;
            }
            else if (hh.getState() == 2) {
                countDown = (int)(hh.getStartTime().getTime() + 3000000L - System.currentTimeMillis());
                state = 2;
            }
            else if (hh.getState() == 3) {
                if (!this.hasHzRewards(playerId)) {
                    return;
                }
                countDown = 0;
                state = 3;
            }
            doc.createElement("hzState", state);
            doc.createElement("hzCountDown", countDown);
        }
    }
    
    @Override
    public byte[] getHuiZhanGatherInfo(final PlayerDto playerDto) {
        if (HuiZhanService.todayHuiZhan == null || HuiZhanService.todayHuiZhan.getState() != 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.HUIZHAN_GATHER_ERROR_NO_HUIZHAN_IN_PREPARATION);
        }
        final HuizhanHistory hh = HuiZhanService.todayHuiZhan;
        final int cityId = hh.getCityId();
        final WorldCity worldCity = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
        final String cityName = worldCity.getName();
        final long selfForce = hh.getDefForce();
        final long otherSideForce = hh.getAttForce1() + hh.getAttForce2();
        final long minForce = (otherSideForce > selfForce) ? selfForce : otherSideForce;
        final BtLevel bl = this.btGatherRankingCache.getBtLevelByForce((int)minForce / 10000);
        String btLvName = "";
        if (bl != null) {
            btLvName = bl.getName();
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        this.dataGetter.getGeneralService().getGeneralInfoForGoldOrder(playerDto.playerId, doc);
        doc.createElement("cityName", cityName);
        doc.createElement("countDown", hh.getStartTime().getTime() + 300000L - System.currentTimeMillis());
        doc.createElement("level", btLvName);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] joinHuiZhan(final PlayerDto playerDto, final String gIds) {
        try {
            final int playerId = playerDto.playerId;
            final HuizhanHistory hh = HuiZhanService.todayHuiZhan;
            if (hh == null || hh.getState() != 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.HUIZHAN_GATHER_ERROR_NO_HUIZHAN_IN_PREPARATION);
            }
            final int cityId = hh.getCityId();
            if (playerDto.cs[24] != '1') {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_WORLD_NOT_OPEN);
            }
            if (!this.dataGetter.getBattleService().canAttackInMist(playerId, cityId)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_CITY_IN_MIST);
            }
            final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
            if (juBenDto != null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GOLDORDER_CAN_NOT_REPLY_WHILE_IN_JUBEN);
            }
            return this.dataGetter.getBattleService().battleStart(playerId, 3, cityId, gIds, 0);
        }
        catch (Exception e) {
            HuiZhanService.errorLog.error("className:HuiZhanService#methodName:joinHuiZhan", e);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.HUIZHAN_JOIN_BATTLE_EXCEPTION);
        }
    }
    
    @Override
    public boolean isHuiZhanInStatePreparation(final int cityId) {
        return HuiZhanService.todayHuiZhan != null && HuiZhanService.todayHuiZhan.getState() == 1 && cityId == HuiZhanService.todayHuiZhan.getCityId();
    }
    
    @Override
    public boolean isHuiZhanInProcess(final int cityId) {
        return HuiZhanService.todayHuiZhan != null && cityId == HuiZhanService.todayHuiZhan.getCityId() && (HuiZhanService.todayHuiZhan.getState() == 1 || HuiZhanService.todayHuiZhan.getState() == 2);
    }
    
    @Override
    public byte[] getHuiZhanInfo(final PlayerDto playerDto) {
        final HuizhanHistory hh = this.huizhanHistoryDao.getLatestHuizhan();
        if (hh == null) {
            return this.getHuiZhanHistoryBytes(playerDto);
        }
        if (hh.getGatherFlag() == 0) {
            if (hh.getState() != 2 && hh.getState() != 1) {
                this.pushHuiZhanTaskInfoForSinglePlayer(playerDto.playerId, 4);
                return this.getHuiZhanHistoryBytes(playerDto);
            }
            return this.getGatherPanelBytes(playerDto, false);
        }
        else {
            final PlayerHuizhan ph = this.dataGetter.getPlayerHuizhanDao().getByhzIdAndPlayerId(hh.getVId(), playerDto.playerId);
            if (ph == null) {
                if (hh.getState() == 3) {
                    this.pushHuiZhanTaskInfoForSinglePlayer(playerDto.playerId, 4);
                    return this.getHuiZhanHistoryBytes(playerDto);
                }
                return this.getUpgradePanelBytes(playerDto);
            }
            else {
                final int awardFlag = ph.getAwardFlag();
                if (awardFlag == 0) {
                    return this.getGatherPanelBytes(playerDto, true);
                }
                if (awardFlag == 1) {
                    return this.getUpgradePanelBytes(playerDto);
                }
                if (awardFlag == 2) {
                    return this.getHuiZhanHistoryBytes(playerDto);
                }
                return this.getHuiZhanHistoryBytes(playerDto);
            }
        }
    }
    
    private byte[] getGatherPanelBytes(final PlayerDto playerDto, boolean canGetReward) {
        final HuizhanHistory hh = this.dataGetter.getHuizhanHistoryDao().getLatestHuizhan();
        final int playerId = playerDto.playerId;
        final int hzId = hh.getVId();
        final Player player = this.dataGetter.getPlayerDao().read(playerId);
        final int attForceId1 = hh.getAttForceId1();
        final int attForceId2 = hh.getAttForceId2();
        final int defForceId = hh.getDefForceId();
        final long attForce1 = hh.getAttForce1();
        final long attForce2 = hh.getAttForce2();
        final long defForce = hh.getDefForce();
        final long attForce3 = attForce1 + attForce2;
        long selfForce = 0L;
        if (playerDto.forceId == attForceId1) {
            selfForce = attForce1;
        }
        else if (playerDto.forceId == attForceId2) {
            selfForce = attForce2;
        }
        else {
            selfForce = defForce;
        }
        final int cityId = hh.getCityId();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(hh.getStartTime());
        final int hour = calendar.get(11);
        long countDown = 0L;
        if (hh.getState() == 2) {
            countDown = hh.getStartTime().getTime() + 3000000L - System.currentTimeMillis();
        }
        else if (hh.getState() == 1) {
            countDown = hh.getStartTime().getTime() + 300000L - System.currentTimeMillis();
        }
        final WorldCity worldCity = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
        final String cityName = worldCity.getName();
        final int forceRank = RankService.huiZhanForceRanker.getRank(1, playerId, playerDto.forceId);
        int kindomLv = this.forceInfoDao.read(player.getForceId()).getForceLv();
        kindomLv = ((kindomLv <= 0) ? 1 : kindomLv);
        String titleName = "";
        String titlePic = "";
        int titleQuality = 0;
        int rankRewardFood = 0;
        List<BtLevel> hzLvList = new ArrayList<BtLevel>();
        hzLvList = this.btGatherRankingCache.getAllLvList();
        final BtGatherRanking bgr = this.btGatherRankingCache.getGatherRankByKindomLvAndRankLv(kindomLv, forceRank);
        if (bgr != null) {
            final int lv = bgr.getLv();
            titleName = this.kingdomTaskRankingCache.getTitleString(lv, 11);
            titlePic = this.kingdomTaskRankingCache.getTitlePic(lv, 11);
            titleQuality = this.kingdomTaskRankingCache.getTitleQuality(lv, 11);
            rankRewardFood = bgr.getRewardFood();
        }
        int phantomNum = 0;
        int phantomRewardExp = 0;
        long playerForces = 0L;
        final PlayerHuizhan ph = this.dataGetter.getPlayerHuizhanDao().getByhzIdAndPlayerId(hzId, playerId);
        if (ph != null) {
            phantomNum = ph.getPhantomNum();
            final BtHyReward bhr = this.btGatherRankingCache.getBtHyRewardByPhantomNum(phantomNum);
            if (bhr != null) {
                phantomRewardExp = bhr.getRewardExp();
            }
            playerForces = ph.getForces();
        }
        if (phantomRewardExp + rankRewardFood <= 0 && canGetReward) {
            canGetReward = false;
            this.dataGetter.getPlayerHuizhanDao().updateAwardFlagByVid(1, ph.getVId());
        }
        final BtLevel bl = this.btGatherRankingCache.getBtLv(1);
        final int forceNeed = bl.getNum();
        final long minForce = (attForce3 > defForce) ? defForce : attForce3;
        int btLv = 0;
        final BtLevel bl2 = this.btGatherRankingCache.getBtLevelByForce((int)minForce / 10000);
        if (bl2 != null) {
            btLv = bl2.getLv();
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("hzLvList");
        for (final BtLevel tempBl : hzLvList) {
            doc.startObject();
            doc.createElement("hzLv", tempBl.getLv());
            doc.createElement("hzLvNum", tempBl.getNum());
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("taskType", 1);
        doc.createElement("selfForceId", playerDto.forceId);
        doc.createElement("selfForce", selfForce);
        doc.createElement("attForceId1", attForceId1);
        doc.createElement("attForce1", attForce1);
        doc.createElement("attForceId2", attForceId2);
        doc.createElement("attForce2", attForce2);
        doc.createElement("defForceId", defForceId);
        doc.createElement("defForce", defForce);
        doc.createElement("cityId", cityId);
        doc.createElement("cityName", cityName);
        doc.createElement("hour", hour);
        doc.createElement("countDown", countDown);
        doc.createElement("canGetReward", false);
        doc.createElement("titleName", titleName);
        doc.createElement("rank", forceRank);
        doc.createElement("titlePic", titlePic);
        doc.createElement("titleQuality ", titleQuality);
        doc.createElement("rankRewardFood", rankRewardFood);
        doc.createElement("phantomRewardExp", phantomRewardExp);
        doc.createElement("phantomNum", phantomNum);
        doc.createElement("canGetReward", canGetReward);
        doc.createElement("forceNeed", forceNeed);
        doc.createElement("hzLv", btLv);
        doc.createElement("playerForces", playerForces);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private byte[] getUpgradePanelBytes(final PlayerDto playerDto) {
        final HuizhanHistory hh = this.dataGetter.getHuizhanHistoryDao().getLatestHuizhan();
        final int playerId = playerDto.playerId;
        final int attForceId1 = hh.getAttForceId1();
        final int attForceId2 = hh.getAttForceId2();
        final int defForceId = hh.getDefForceId();
        final long attForce1 = hh.getAttForce1();
        final long attForce2 = hh.getAttForce2();
        final long defForce = hh.getDefForce();
        final long attForce3 = attForce1 + attForce2;
        long selfForce = 0L;
        if (playerDto.forceId == attForceId1) {
            selfForce = attForce1;
        }
        else if (playerDto.forceId == attForceId2) {
            selfForce = attForce2;
        }
        else {
            selfForce = defForce;
        }
        boolean isAttack = true;
        if (playerDto.forceId == hh.getDefForceId()) {
            isAttack = false;
        }
        boolean canGetReward = false;
        final int cityId = hh.getCityId();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(hh.getStartTime());
        final int hour = calendar.get(11);
        long countDown = hh.getStartTime().getTime() + 3000000L - System.currentTimeMillis();
        final WorldCity worldCity = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
        final String cityName = worldCity.getName();
        final int killRank = RankService.HuiZhanKillRanker.getRank(1, playerId, playerDto.forceId);
        final long nationKillNum = RankService.HuiZhanKillRanker.getTotalNum(playerDto.forceId);
        final long playerKillNum = RankService.HuiZhanKillRanker.getValue(playerDto.forceId, playerId);
        String titleName = "";
        String titlePic = "";
        int titleQuality = 0;
        int rankRewardExp = 0;
        int rankRewardIron = 0;
        int battleRewardExp = 0;
        int battleRewardIron = 0;
        int hzLv = 0;
        List<BtLevel> hzLvList = new ArrayList<BtLevel>();
        hzLvList = this.btGatherRankingCache.getAllLvList();
        final long minForce = (attForce3 > defForce) ? defForce : attForce3;
        final BtLevel bl = this.btGatherRankingCache.getBtLevelByForce((int)minForce / 10000);
        int winOrLose = -1;
        if (bl != null) {
            hzLv = bl.getLv();
            final BtKillRanking bkr = this.btGatherRankingCache.getKillRankByhzLvAndRankLv(hzLv, killRank);
            if (bkr != null) {
                if (isAttack) {
                    rankRewardExp = bkr.getRewardExp2();
                    rankRewardIron = bkr.getRewardIron2();
                }
                else {
                    rankRewardExp = bkr.getRewardExp();
                    rankRewardIron = bkr.getRewardIron();
                }
                final int lv = bkr.getLv();
                titleName = this.kingdomTaskRankingCache.getTitleString(lv, 11);
                titlePic = this.kingdomTaskRankingCache.getTitlePic(lv, 11);
                titleQuality = this.kingdomTaskRankingCache.getTitleQuality(lv, 11);
            }
            if (hh.getState() == 3) {
                canGetReward = true;
                countDown = 0L;
                if (hh.getWinner() == 0) {
                    battleRewardExp = bl.getDrawExp();
                    battleRewardIron = bl.getDrawIron();
                    winOrLose = 0;
                }
                else if (hh.getWinner() == hh.getDefForceId()) {
                    if (!isAttack) {
                        battleRewardExp = bl.getWinExp();
                        battleRewardIron = bl.getWinIron();
                        winOrLose = 1;
                    }
                    else {
                        winOrLose = 2;
                    }
                }
                else if (isAttack) {
                    battleRewardExp = bl.getWinExp();
                    battleRewardIron = bl.getWinIron();
                    winOrLose = 1;
                }
                else {
                    winOrLose = 2;
                }
            }
            else if (playerKillNum > 0L) {
                battleRewardExp = bl.getWinExp();
                battleRewardIron = bl.getWinIron();
            }
        }
        final PlayerHuizhan ph1 = this.dataGetter.getPlayerHuizhanDao().getByhzIdAndPlayerId(hh.getVId(), playerDto.playerId);
        if (ph1 == null || rankRewardExp + rankRewardIron <= 0) {
            battleRewardExp = 0;
            battleRewardIron = 0;
        }
        if (hh.getState() == 3 && rankRewardExp + rankRewardIron + battleRewardExp + battleRewardIron <= 0) {
            canGetReward = false;
            final PlayerHuizhan ph2 = this.dataGetter.getPlayerHuizhanDao().getByhzIdAndPlayerId(hh.getVId(), playerDto.playerId);
            if (ph2 != null) {
                this.dataGetter.getPlayerHuizhanDao().updateAwardFlagByVid(2, ph2.getVId());
                this.pushHuiZhanTaskInfoForSinglePlayer(playerId, 4);
            }
        }
        int nextLvNum = 0;
        final BtLevel bl2 = this.btGatherRankingCache.getNextBtLv(hzLv);
        if (bl2 != null) {
            nextLvNum = bl2.getNum();
        }
        int attHzLv = 0;
        int defHzLv = 0;
        final BtLevel attBl = this.btGatherRankingCache.getBtLevelByForce((int)attForce3 / 10000);
        final BtLevel defBl = this.btGatherRankingCache.getBtLevelByForce((int)defForce / 10000);
        if (attBl != null) {
            attHzLv = attBl.getLv();
        }
        if (defBl != null) {
            defHzLv = defBl.getLv();
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("hzLvList");
        for (final BtLevel tempBl : hzLvList) {
            doc.startObject();
            doc.createElement("hzLv", tempBl.getLv());
            doc.createElement("hzLvNum", tempBl.getNum());
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("taskType", 2);
        doc.createElement("selfForceId", playerDto.forceId);
        doc.createElement("selfForce", selfForce);
        doc.createElement("attForceId1", attForceId1);
        doc.createElement("attForce1", attForce1);
        doc.createElement("attForceId2", attForceId2);
        doc.createElement("attForce2", attForce2);
        doc.createElement("defForceId", defForceId);
        doc.createElement("defForce", defForce);
        doc.createElement("cityId", cityId);
        doc.createElement("cityName", cityName);
        doc.createElement("hour", hour);
        doc.createElement("countDown", countDown);
        doc.createElement("titleName", titleName);
        doc.createElement("rank", killRank);
        doc.createElement("titlePic", titlePic);
        doc.createElement("titleQuality ", titleQuality);
        doc.createElement("rankRewardExp", rankRewardExp);
        doc.createElement("rankRewardIron", rankRewardIron);
        doc.createElement("battleRewardExp", battleRewardExp);
        doc.createElement("battleRewardIron", battleRewardIron);
        doc.createElement("canGetReward", canGetReward);
        doc.createElement("nextLvNum", nextLvNum);
        doc.createElement("hzLv", hzLv);
        doc.createElement("winOrLose", winOrLose);
        doc.createElement("nationKillNum", nationKillNum);
        doc.createElement("playerKillNum", playerKillNum);
        doc.createElement("attHzLv", attHzLv);
        doc.createElement("defHzLv", defHzLv);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private byte[] getHuiZhanHistoryBytes(final PlayerDto playerDto) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("taskType", 0);
        int defForceId = 0;
        final HuizhanHistory hh = this.huizhanHistoryDao.getLatestHuizhan();
        if (hh == null) {
            defForceId = this.dataGetter.getForceInfoDao().getMaxExp();
        }
        else {
            defForceId = ((hh.getAttForce1() > hh.getAttForce2()) ? hh.getAttForceId1() : hh.getAttForceId2());
        }
        final int cityId = WorldCityCommon.forceIdSpecialCityMap.get(defForceId);
        final int attForceId1 = WorldCityCommon.getOther2ForceIds(defForceId)[0];
        final int attForceId2 = WorldCityCommon.getOther2ForceIds(defForceId)[1];
        final int[] forceIdList = { attForceId1, attForceId2, defForceId };
        doc.startArray("winLose");
        for (int i = 0; i < 3; ++i) {
            final int forceId = forceIdList[i];
            final int totalNum = HuiZhanService.hzTotalNum;
            final int winNum = HuiZhanService.hzWinNumMap.get(forceId);
            int winningPercentage = 0;
            if (totalNum > 0) {
                winningPercentage = winNum * 100 / totalNum;
            }
            doc.startObject();
            doc.createElement("forceId", forceId);
            doc.createElement("winNum", winNum);
            doc.createElement("winningPercentage", winningPercentage);
            doc.endObject();
        }
        doc.endArray();
        final int maxLength = this.btGatherRankingCache.getBtLvSize();
        BtKillRanking topBkRanking = new BtKillRanking();
        doc.startArray("hzTopRewardTips");
        final List<BtKillRanking> bkrList = this.btGatherRankingCache.getKillRankByLv(1);
        for (final BtKillRanking bkr : bkrList) {
            final BtLevel bl = this.btGatherRankingCache.getBtLv(bkr.getBtLv());
            doc.startObject();
            doc.createElement("hzLvName", bl.getName());
            doc.createElement("titleRewardExp", bkr.getRewardExp());
            doc.createElement("titleRewardIron", bkr.getRewardIron());
            doc.createElement("winRewardExp", bl.getWinExp());
            doc.createElement("winRewardIron", bl.getWinIron());
            doc.endObject();
            if (bkr.getBtLv() == maxLength) {
                topBkRanking = bkr;
            }
        }
        doc.endArray();
        final BtLevel bl2 = this.btGatherRankingCache.getBtLv(maxLength);
        doc.createElement("attTitleRewardExp", topBkRanking.getRewardExp2());
        doc.createElement("attTitleRewardIron", topBkRanking.getRewardIron2());
        doc.createElement("attWinRewardExp", bl2.getWinExp());
        doc.createElement("attWinRewardIron", bl2.getWinIron());
        doc.createElement("defTitleRewardExp", topBkRanking.getRewardExp());
        doc.createElement("defTitleRewardIron", topBkRanking.getRewardIron());
        doc.createElement("defWinRewardExp", bl2.getWinExp());
        doc.createElement("defWinRewardIron", bl2.getWinIron());
        final Calendar calendar = this.getNextHzTime();
        final int month = calendar.get(2) + 1;
        final int day = calendar.get(5);
        final int hour = calendar.get(11);
        final int min = calendar.get(12);
        final String hzDate = MessageFormat.format(LocalMessages.HUIZHAN_START_DATE, month, day);
        final String hzTime = MessageFormat.format(LocalMessages.HUIZHAN_START_TIME, hour, min);
        final WorldCity worldCity = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
        doc.createElement("cityId", cityId);
        doc.createElement("cityName", worldCity.getName());
        doc.createElement("hzDate", hzDate);
        doc.createElement("hzTime", hzTime);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] receiveHuizhanRewards(final PlayerDto playerDto) {
        try {
            final HuizhanHistory hh = this.dataGetter.getHuizhanHistoryDao().getLatestHuizhan();
            if (hh == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.HUIZHAN_ERROR_NO_YOUR_COUNTRY);
            }
            final int hzId = hh.getVId();
            final int playerId = playerDto.playerId;
            final PlayerHuizhan ph = this.dataGetter.getPlayerHuizhanDao().getByhzIdAndPlayerId(hzId, playerId);
            final int gatherFlag = hh.getGatherFlag();
            final int awardFlag = ph.getAwardFlag();
            if (hh.getState() == 3) {
                if (awardFlag == 0) {
                    return this.receiveGatherRewards(ph, false);
                }
                if (awardFlag == 1) {
                    return this.receiveUpgradeRewards(ph, false);
                }
            }
            else if (gatherFlag == 1 && awardFlag == 0) {
                return this.receiveGatherRewards(ph, false);
            }
        }
        catch (Exception e) {
            HuiZhanService.errorLog.error("className:HuiZhanService#methodName:receiveHuizhanRewards", e);
        }
        return JsonBuilder.getJson(State.FAIL, "\u6ca1\u5956\u52b1\u53ef\u4ee5\u9886\u53d6");
    }
    
    private byte[] receiveGatherRewards(final PlayerHuizhan ph, final boolean isAutoRec) {
        final int playerId = ph.getPlayerId();
        try {
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            final int forceRank = RankService.huiZhanForceRanker.getRank(1, playerId, player.getForceId());
            int kindomLv = this.forceInfoDao.read(ph.getForceId()).getForceLv();
            kindomLv = ((kindomLv <= 0) ? 1 : kindomLv);
            int forceRankRewardFood = 0;
            final BtGatherRanking bgr = this.btGatherRankingCache.getGatherRankByKindomLvAndRankLv(kindomLv, forceRank);
            if (bgr != null) {
                forceRankRewardFood = bgr.getRewardFood();
            }
            final int phantomNum = ph.getPhantomNum();
            int phantomRewardExp = 0;
            final BtHyReward bhr = this.btGatherRankingCache.getBtHyRewardByPhantomNum(phantomNum);
            if (bhr != null) {
                phantomRewardExp = bhr.getRewardExp();
            }
            if (phantomRewardExp > 0) {
                this.dataGetter.getPlayerService().updateExpAndPlayerLevel(playerId, phantomRewardExp, "\u4f1a\u6218\u96c6\u7ed3\u4efb\u52a1\u83b7\u5f97\u501f\u5175\u5956\u52b1\u7ecf\u9a8c");
            }
            if (forceRankRewardFood > 0) {
                this.playerResourceDao.addFoodIgnoreMax(playerId, forceRankRewardFood, "\u4f1a\u6218\u96c6\u7ed3\u5956\u52b1\u83b7\u5f97\u7cae\u98df");
            }
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("rankRewardFood", forceRankRewardFood);
            doc.createElement("phantomRewardExp", phantomRewardExp);
            doc.endObject();
            this.dataGetter.getPlayerHuizhanDao().updateAwardFlagByVid(1, ph.getVId());
            if (isAutoRec && forceRankRewardFood + phantomRewardExp > 0) {
                final String msg = MessageFormatter.format(LocalMessages.HUIZHAN_AUTO_RECEIVE_REWARDS_MAIL_CONTENT_GATHER, new Object[] { forceRankRewardFood, phantomRewardExp });
                final String content = String.valueOf(LocalMessages.HUIZHAN_AUTO_RECEIVE_REWARDS_MAIL_CONTENT_COMMON) + msg;
                this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.HUIZHAN_AUTO_RECEIVE_REWARDS_MAIL_TILE, content, 1, playerId, new Date());
                HuiZhanService.errorLog.error("className:HuiZhanService#methodName:receiveGatherRewards#playerId:" + playerId + "#phantomRewardExp:" + phantomRewardExp + "#rankRewardFood:" + forceRankRewardFood + "#hzId:" + ph.getHzId());
            }
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        catch (Exception e) {
            HuiZhanService.errorLog.error("className:HuiZhanService#methodName:receiveGatherRewards#playerId:" + playerId + "#hzId:" + ph.getHzId(), e);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10035);
        }
    }
    
    private byte[] receiveUpgradeRewards(final PlayerHuizhan ph, final boolean isAutoRec) {
        final HuizhanHistory hh = this.dataGetter.getHuizhanHistoryDao().getLatestHuizhan();
        final int playerId = ph.getPlayerId();
        final Player player = this.playerDao.read(playerId);
        boolean isAttack = true;
        if (player.getForceId() == hh.getDefForceId()) {
            isAttack = false;
        }
        try {
            final int killRank = RankService.HuiZhanKillRanker.getRank(1, playerId, player.getForceId());
            int killRankRewardExp = 0;
            int killRankRewardIron = 0;
            int battleRewardExp = 0;
            int battleRewardIron = 0;
            int hzLv = 0;
            final long attForce = hh.getAttForce1() + hh.getAttForce2();
            final long defForce = hh.getDefForce();
            final long minForce = (attForce > defForce) ? defForce : attForce;
            final BtLevel bl = this.btGatherRankingCache.getBtLevelByForce((int)minForce / 10000);
            if (bl != null) {
                hzLv = bl.getLv();
                final BtKillRanking bkr = this.btGatherRankingCache.getKillRankByhzLvAndRankLv(hzLv, killRank);
                if (bkr != null) {
                    if (isAttack) {
                        killRankRewardExp = bkr.getRewardExp2();
                        killRankRewardIron = bkr.getRewardIron2();
                    }
                    else {
                        killRankRewardExp = bkr.getRewardExp();
                        killRankRewardIron = bkr.getRewardIron();
                    }
                }
                if (hh.getWinner() == 0) {
                    battleRewardExp = bl.getDrawExp();
                    battleRewardIron = bl.getDrawIron();
                }
                else if (hh.getWinner() == hh.getDefForceId()) {
                    if (!isAttack) {
                        battleRewardExp = bl.getWinExp();
                        battleRewardIron = bl.getWinIron();
                    }
                }
                else if (isAttack) {
                    battleRewardExp = bl.getWinExp();
                    battleRewardIron = bl.getWinIron();
                }
            }
            if (killRankRewardExp + killRankRewardIron <= 0) {
                battleRewardExp = 0;
                battleRewardIron = 0;
            }
            boolean hasReward = false;
            if (killRankRewardExp > 0) {
                hasReward = true;
                this.dataGetter.getPlayerService().updateExpAndPlayerLevel(playerId, killRankRewardExp, "\u4f1a\u6218\u5347\u7ea7\u4efb\u52a1\u83b7\u5f97\u6740\u654c\u6392\u540d\u7ecf\u9a8c");
            }
            if (battleRewardExp > 0) {
                hasReward = true;
                this.dataGetter.getPlayerService().updateExpAndPlayerLevel(playerId, battleRewardExp, "\u4f1a\u6218\u5347\u7ea7\u4efb\u52a1\u83b7\u5f97\u80dc\u8d1f\u5956\u52b1\u7ecf\u9a8c");
            }
            if (killRankRewardIron > 0) {
                hasReward = true;
                this.playerResourceDao.addIronIgnoreMax(playerId, killRankRewardIron, "\u4f1a\u6218\u5347\u7ea7\u4efb\u52a1\u83b7\u5f97\u6740\u654c\u6392\u540d\u5956\u52b1\u9554\u94c1", false);
            }
            if (battleRewardIron > 0) {
                hasReward = true;
                this.playerResourceDao.addIronIgnoreMax(playerId, battleRewardIron, "\u4f1a\u6218\u5347\u7ea7\u4efb\u52a1\u83b7\u5f97\u80dc\u8d1f\u5956\u52b1\u9554\u94c1", false);
            }
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("rankRewardExp", killRankRewardExp);
            doc.createElement("rankRewardIron", killRankRewardIron);
            doc.createElement("battleRewardExp", battleRewardExp);
            doc.createElement("battleRewardIron", battleRewardIron);
            doc.endObject();
            this.dataGetter.getPlayerHuizhanDao().updateAwardFlagByVid(2, ph.getVId());
            this.pushHuiZhanTaskInfoForSinglePlayer(playerId, 4);
            if (isAutoRec && hasReward) {
                final String msg = MessageFormatter.format(LocalMessages.HUIZHAN_AUTO_RECEIVE_REWARDS_MAIL_CONTENT_UPGRADE, new Object[] { killRankRewardExp, killRankRewardIron, battleRewardExp, battleRewardIron });
                final String content = String.valueOf(LocalMessages.HUIZHAN_AUTO_RECEIVE_REWARDS_MAIL_CONTENT_COMMON) + msg;
                this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.HUIZHAN_AUTO_RECEIVE_REWARDS_MAIL_TILE, content, 1, playerId, new Date());
                HuiZhanService.errorLog.error("className:HuiZhanService#methodName:receiveUpgradeRewards#playerId:" + playerId + "#rankRewardExp:" + killRankRewardExp + "#rankRewardIron:" + killRankRewardIron + "#battleRewardExp:" + battleRewardExp + "#battleRewardIron:" + battleRewardIron + "#hzId:" + ph.getHzId());
            }
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        catch (Exception e) {
            HuiZhanService.errorLog.error("className:HuiZhanService#methodName:receiveUpgradeRewards#playerId:" + playerId + "#hzId:" + ph.getHzId(), e);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10035);
        }
    }
    
    private void handleUnReceivedReward() {
        try {
            final List<PlayerHuizhan> phList = this.dataGetter.getPlayerHuizhanDao().getUnReceivedRewardPlayerHuizhan();
            if (phList == null) {
                return;
            }
            for (final PlayerHuizhan ph : phList) {
                final int hzId = ph.getHzId();
                final HuizhanHistory hh = this.dataGetter.getHuizhanHistoryDao().read(hzId);
                if (hh != null && hh.getState() == 3) {
                    if (hh.getGatherFlag() <= 0) {
                        continue;
                    }
                    HuiZhanService.errorLog.error("huizhanGetUnReceivedReward#playerId:" + ph.getPlayerId());
                    if (ph.getAwardFlag() == 0) {
                        this.receiveGatherRewards(ph, true);
                        this.receiveUpgradeRewards(ph, true);
                    }
                    else {
                        if (ph.getAwardFlag() != 1) {
                            continue;
                        }
                        this.receiveUpgradeRewards(ph, true);
                    }
                }
            }
        }
        catch (Exception e) {
            HuiZhanService.errorLog.error("className:HuiZhanService#methodName:handleUnReceivedReward", e);
        }
    }
    
    @Override
    public void dealHuiZhanPk(final CampArmy attCa, final CampArmy defCa, final Battle bat) {
        try {
            final int cityId = bat.getDefBaseInfo().getId();
            final HuizhanHistory hh = HuiZhanService.todayHuiZhan;
            if (hh != null && hh.getCityId() == cityId && hh.getState() == 2) {
                if (attCa.getPlayerId() > 0 && !attCa.isPhantom()) {
                    this.dataGetter.getPlayerHuizhanDao().addPKNumByhzIdAndPlayerId(hh.getVId(), attCa.getPlayerId());
                    this.dataGetter.getHuiZhanService().dealPkReward(bat, hh.getVId(), attCa.getPlayerId());
                }
                if (defCa.getPlayerId() > 0 && !defCa.isPhantom()) {
                    this.dataGetter.getPlayerHuizhanDao().addPKNumByhzIdAndPlayerId(hh.getVId(), defCa.getPlayerId());
                    this.dataGetter.getHuiZhanService().dealPkReward(bat, hh.getVId(), defCa.getPlayerId());
                }
            }
        }
        catch (Exception e) {
            HuiZhanService.errorLog.error("className:HuiZhanService#methodName:dealHuiZhanPk", e);
        }
    }
    
    @Override
    public void dealPkReward(final Battle bat, final int hzId, final int playerId) {
        try {
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            final int forceId = player.getForceId();
            final PlayerHuizhan ph = this.dataGetter.getPlayerHuizhanDao().getByhzIdAndPlayerId(hzId, playerId);
            if (ph == null) {
                return;
            }
            final int pkNum = ph.getPkTimes();
            final BtSoloReward bsr = this.btGatherRankingCache.getBtSoloRewardByNum(pkNum);
            if (bsr != null) {
                final int hzTokenNum = bsr.getRewardToken();
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("hzPkNum", pkNum);
                doc.createElement("hzTokenNum", hzTokenNum);
                doc.endObject();
                int armyId = 0;
                if (forceId == 1) {
                    armyId = bsr.getWeiNpc();
                }
                else if (forceId == 2) {
                    armyId = bsr.getShuNpc();
                }
                else {
                    armyId = bsr.getWuNpc();
                }
                if (hzTokenNum > 0) {
                    this.playerAttributeDao.addRecruitToken(playerId, hzTokenNum, "\u4f1a\u6218\u5355\u6311\u5956\u52b1\u83b7\u5f97\u52df\u5175\u4ee4");
                }
                Players.push(playerId, PushCommand.PUSH_HUIZHAN_PK_REWARD, doc.toByte());
                this.addHuizhanPkRewardNPC(bat, armyId, forceId, bsr.getNpcNum());
            }
        }
        catch (Exception e) {
            HuiZhanService.errorLog.error("className:HuiZhanService#methodName:dealPkReward", e);
        }
    }
    
    private void addHuizhanPkRewardNPC(final Battle battle, final int armyId, final int forceId, final int num) {
        try {
            HuiZhanService.errorLog.error("addHuizhanPkRewardNPC starts....");
            if (battle == null) {
                return;
            }
            int battleSide = 1;
            if (forceId == battle.getDefBaseInfo().getId()) {
                battleSide = 0;
            }
            final Builder builder = BuilderFactory.getInstance().getBuilder(3);
            for (int i = 0; i < num; ++i) {
                final CampArmy caArmy = builder.copyArmyfromHuizhanPkRewardNpc(this.dataGetter, battle, armyId, battleSide, forceId);
                battle.joinCampArmy(this.dataGetter, battleSide, caArmy);
            }
            HuiZhanService.errorLog.error("addHuizhanPkRewardNPC ends....");
        }
        catch (Exception e) {
            HuiZhanService.errorLog.error("className:HuiZhanService#methodName:addHuizhanPkRewardNPC", e);
        }
    }
    
    @Override
    public void getHzInfoInBattle(final JsonDocument doc, final int cityId) {
        final HuizhanHistory hh = this.huizhanHistoryDao.getLatestHuizhan();
        if (hh != null && (hh.getState() == 1 || hh.getState() == 2)) {
            final int hzCityId = hh.getCityId();
            final int attForceId1 = hh.getAttForceId1();
            final long attForce1 = hh.getAttForce1();
            final int attForceId2 = hh.getAttForceId2();
            final long attForce2 = hh.getAttForce2();
            final int defForceId = hh.getDefForceId();
            final long defForce = hh.getDefForce();
            final long attForce3 = attForce1 + attForce2;
            long countDown = 0L;
            if (hh.getState() == 1) {
                countDown = hh.getStartTime().getTime() - System.currentTimeMillis();
            }
            else if (hh.getState() == 2) {
                countDown = hh.getStartTime().getTime() + 3000000L - System.currentTimeMillis();
            }
            if (countDown <= 0L) {
                countDown = 0L;
            }
            int hzLv = 0;
            final long minForce = (attForce3 > defForce) ? defForce : attForce3;
            final BtLevel bl = this.btGatherRankingCache.getBtLevelByForce((int)minForce / 10000);
            if (bl != null) {
                hzLv = bl.getLv();
            }
            doc.createElement("hzAttForceId1", attForceId1);
            doc.createElement("hzAttForce1", attForce1 / 10000L);
            doc.createElement("hzAttForceId2", attForceId2);
            doc.createElement("hzAttForce2", attForce2 / 10000L);
            doc.createElement("hzAttForce", (attForce2 + attForce1) / 10000L);
            doc.createElement("hzDefForceId", defForceId);
            doc.createElement("hzDefForce", defForce / 10000L);
            doc.createElement("hzLv", hzLv);
            doc.createElement("hzCityId", hzCityId);
            doc.createElement("hzState", hh.getState());
            doc.createElement("countDown", countDown);
        }
    }
    
    @Override
    public void getHzInfoInCity(final JsonDocument doc) {
        final HuizhanHistory hh = this.huizhanHistoryDao.getLatestHuizhan();
        if (hh != null && (hh.getState() == 1 || hh.getState() == 2)) {
            final int hzCityId = hh.getCityId();
            final int attForceId1 = hh.getAttForceId1();
            final long attForce1 = hh.getAttForce1();
            final int attForceId2 = hh.getAttForceId2();
            final long attForce2 = hh.getAttForce2();
            final int defForceId = hh.getDefForceId();
            final long defForce = hh.getDefForce();
            final long attForce3 = attForce1 + attForce2;
            String hzName = "";
            int hzLv = 0;
            final long minForce = (attForce3 > defForce) ? defForce : attForce3;
            final BtLevel bl = this.btGatherRankingCache.getBtLevelByForce((int)minForce / 10000);
            if (bl != null) {
                hzName = bl.getName();
                hzLv = bl.getLv();
            }
            doc.createElement("hzAttForceId1", attForceId1);
            doc.createElement("hzAttForce1", attForce1 / 10000L);
            doc.createElement("hzAttForceId2", attForceId2);
            doc.createElement("hzAttForce2", attForce2 / 10000L);
            doc.createElement("hzDefForceId", defForceId);
            doc.createElement("hzAttForce", (attForce1 + attForce2) / 10000L);
            doc.createElement("hzDefForce", defForce / 10000L);
            doc.createElement("hzLv", hzLv);
            doc.createElement("hzLvName", hzName);
            doc.createElement("hzCityId", hzCityId);
        }
    }
    
    @Override
    public void clearPlayerHuizhan() {
        final long start = System.currentTimeMillis();
        HuiZhanService.timerLog.info(LogUtil.formatThreadLog("HuiZhanService", "clearPlayerHuizhan", 0, 0L, ""));
        try {
            final Calendar now = Calendar.getInstance();
            now.setTime(new Date());
            now.set(5, now.get(5) - 7);
            final int year = now.get(1);
            final int day = now.get(5);
            final int month = now.get(2);
            now.set(year, month, day, 0, 0, 0);
            final List<HuizhanHistory> hhList = this.dataGetter.getHuizhanHistoryDao().getHuizhanByDate(now.getTime());
            if (hhList != null && hhList.size() > 0) {
                for (final HuizhanHistory hh : hhList) {
                    this.dataGetter.getPlayerHuizhanDao().deleteByHzId(hh.getVId());
                    HuiZhanService.errorLog.error("#clearPlayerHuizhan doing...#hzid:" + hh.getVId());
                }
            }
        }
        catch (Exception e) {
            HuiZhanService.errorLog.error("clearPlayerHuizhan", e);
        }
        this.resourceUpdateSynService.clearPlayerResourceMap();
        HuiZhanService.timerLog.info(LogUtil.formatThreadLog("HuiZhanService", "clearPlayerHuizhan", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Override
    public boolean hasHzRewards(final int playerId) {
        final HuizhanHistory hh = this.dataGetter.getHuizhanHistoryDao().getLatestHuizhan();
        final int hzId = hh.getVId();
        final PlayerHuizhan ph = this.dataGetter.getPlayerHuizhanDao().getByhzIdAndPlayerId(hzId, playerId);
        if (hh.getGatherFlag() < 1 || ph == null) {
            return false;
        }
        if (ph.getAwardFlag() == 1) {
            final Player player = this.dataGetter.getPlayerDao().read(playerId);
            final int killRank = RankService.HuiZhanKillRanker.getRank(1, playerId, player.getForceId());
            int hzLv = 0;
            final long defForce = hh.getDefForce();
            final long attForce = hh.getAttForce1() + hh.getAttForce2();
            final long minForce = (attForce > defForce) ? defForce : attForce;
            final BtLevel bl = this.btGatherRankingCache.getBtLevelByForce((int)minForce / 10000);
            if (bl != null) {
                hzLv = bl.getLv();
                final BtKillRanking bkr = this.btGatherRankingCache.getKillRankByhzLvAndRankLv(hzLv, killRank);
                if (bkr == null) {
                    return false;
                }
            }
        }
        else if (ph.getAwardFlag() == 2) {
            return false;
        }
        return true;
    }
    
    @Override
    public void recoverHuizhan() {
        HuiZhanService.initialDefForceId = this.dataGetter.getForceInfoDao().getMaxExp();
        HuiZhanService.hzTotalNum = this.dataGetter.getHuizhanHistoryDao().getFinishedHzNum();
        for (int i = 0; i < 3; ++i) {
            final NationInfo nationInfo = this.dataGetter.getNationInfoDao().read(i + 1);
            HuiZhanService.hzWinNumMap.put(i + 1, nationInfo.getHzWinNum());
        }
        final Calendar calendar = this.getNextHzTime();
        this.jobService.addJob("huiZhanService", "startHuiZhanForTimer", "", calendar.getTimeInMillis(), false);
        HuiZhanService.errorLog.error("Timer: startHuiZhanForTimer has been set up successfully .....");
        if (calendar.getTimeInMillis() - 86400000L > System.currentTimeMillis()) {
            this.jobService.addJob("huiZhanService", "pushHuiZhanIconForTimer", "1_0", calendar.getTimeInMillis() - 86400000L, false);
            HuiZhanService.errorLog.error("Timer: pushHuiZhanIconForTimer has been set up successfully .....");
        }
        if (calendar.getTimeInMillis() - 3600000L > System.currentTimeMillis()) {
            this.jobService.addJob("huiZhanService", "pushHuiZhanIconForTimer", "1_3600000", calendar.getTimeInMillis() - 3600000L, false);
            HuiZhanService.errorLog.error("Timer: pushHuiZhanIconForTimer has been set up successfully .....");
        }
        final Calendar hzTime = Calendar.getInstance();
        final int year = hzTime.get(1);
        final int month = hzTime.get(2);
        final int day = hzTime.get(5);
        final int week = calendar.get(7);
        hzTime.set(year, month, day, 21, 30, 0);
        final HuizhanHistory temp = this.dataGetter.getHuizhanHistoryDao().getLatestHuizhan();
        if (week == 1 && temp != null && System.currentTimeMillis() - hzTime.getTimeInMillis() > 0L && System.currentTimeMillis() - temp.getStartTime().getTime() > 432000000L) {
            if (System.currentTimeMillis() - hzTime.getTimeInMillis() < 300000L) {
                this.createHuizhan(1, hzTime.getTime());
            }
            else if (System.currentTimeMillis() - hzTime.getTimeInMillis() < 3000000L) {
                this.createHuizhan(2, hzTime.getTime());
            }
        }
        final HuizhanHistory hh = this.dataGetter.getHuizhanHistoryDao().getLatestHuizhan();
        if (hh == null || hh.getState() == 3) {
            return;
        }
        HuiZhanService.todayHuiZhan = hh;
        final long now = System.currentTimeMillis();
        final long batStartTime = hh.getStartTime().getTime() + 300000L;
        final int cityId = hh.getCityId();
        final City city = this.cityDao.read(cityId);
        if (hh.getState() == 1) {
            if (now < batStartTime) {
                this.dataGetter.getCityDao().updateForceIdStateTitleBorder(cityId, city.getForceId(), 0, 0, 0);
                final long time1 = HuiZhanService.todayHuiZhan.getStartTime().getTime() + 300000L;
                this.jobService.addJob("huiZhanService", "huiZhanInStateDoing", "", time1, false);
                HuiZhanService.errorLog.error("Timer: huiZhanInDoing has been set up successfully .....#hzId:" + HuiZhanService.todayHuiZhan.getVId());
                final long time2 = HuiZhanService.todayHuiZhan.getStartTime().getTime() + 3000000L;
                this.jobService.addJob("huiZhanService", "huiZhanInStateOver", "", time2, false);
                HuiZhanService.errorLog.error("Timer: huiZhanInStateOver has been set up successfully .....#hzId:" + HuiZhanService.todayHuiZhan.getVId());
            }
            else if (now < batStartTime + 2700000L) {
                this.dataGetter.getCityDao().updateForceIdStateTitleBorder(cityId, city.getForceId(), 0, 0, 0);
                this.huiZhanInStateDoing("");
                final long time3 = HuiZhanService.todayHuiZhan.getStartTime().getTime() + 3000000L;
                this.jobService.addJob("huiZhanService", "huiZhanInStateOver", "", time3, false);
                HuiZhanService.errorLog.error("Timer: huiZhanInStateOver has been set up successfully .....#hzId:" + HuiZhanService.todayHuiZhan.getVId());
            }
            else {
                this.updateHzStateById(3, HuiZhanService.todayHuiZhan.getVId());
                this.huizhanHistoryDao.updateWinnerByVid(0, HuiZhanService.todayHuiZhan.getVId());
                this.huizhanHistoryDao.updateHzEndTimeById(new Date(), HuiZhanService.todayHuiZhan.getVId());
            }
        }
        else if (hh.getState() == 2) {
            if (now < batStartTime + 2700000L) {
                this.dataGetter.getCityDao().updateForceIdStateTitleBorder(cityId, city.getForceId(), 0, 0, 0);
                final long time3 = HuiZhanService.todayHuiZhan.getStartTime().getTime() + 3000000L;
                this.jobService.addJob("huiZhanService", "huiZhanInStateOver", "", time3, false);
                HuiZhanService.errorLog.error("Timer: huiZhanInStateOver has been set up successfully .....#hzId:" + HuiZhanService.todayHuiZhan.getVId());
            }
            else {
                this.updateHzStateById(3, HuiZhanService.todayHuiZhan.getVId());
                this.huizhanHistoryDao.updateWinnerByVid(0, HuiZhanService.todayHuiZhan.getVId());
                this.huizhanHistoryDao.updateHzEndTimeById(new Date(), HuiZhanService.todayHuiZhan.getVId());
            }
        }
    }
    
    @Override
    public Calendar getNextHzTime() {
        final Calendar calendar = Calendar.getInstance();
        final int week = calendar.get(7);
        final Calendar hzTime = Calendar.getInstance();
        final int year = hzTime.get(1);
        final int month = hzTime.get(2);
        final int day = hzTime.get(5);
        hzTime.set(year, month, day, 21, 30, 0);
        switch (week) {
            case 1: {
                if (calendar.getTimeInMillis() < hzTime.getTimeInMillis()) {
                    return hzTime;
                }
                hzTime.add(5, 7);
                return hzTime;
            }
            default: {
                hzTime.add(5, 7 - week + 1);
                return hzTime;
            }
        }
    }
    
    @Override
    public void addHzWinNumByForceId(final int forceId) {
        this.dataGetter.getNationInfoDao().addHzWinNumByForceId(forceId);
        final int winNum = HuiZhanService.hzWinNumMap.get(forceId);
        HuiZhanService.hzWinNumMap.put(forceId, winNum + 1);
    }
    
    @Override
    public void addHzTotalNum() {
        ++HuiZhanService.hzTotalNum;
    }
    
    @Override
    public void updateHzStateById(final int state, final int vId) {
        this.huizhanHistoryDao.updateHzStateById(state, vId);
        HuiZhanService.todayHuiZhan.setState(state);
    }
    
    @Override
    public HuizhanHistory getTodayHuizhanInProcess() {
        if (HuiZhanService.todayHuiZhan != null && (HuiZhanService.todayHuiZhan.getState() == 1 || HuiZhanService.todayHuiZhan.getState() == 2)) {
            return HuiZhanService.todayHuiZhan;
        }
        return null;
    }
    
    @Override
    public HuizhanHistory getTodayHuizhanBySate(final int state) {
        if (HuiZhanService.todayHuiZhan != null && state == HuiZhanService.todayHuiZhan.getState()) {
            return HuiZhanService.todayHuiZhan;
        }
        return null;
    }
    
    @Override
    public HuizhanHistory getTodayHuiZhan() {
        return HuiZhanService.todayHuiZhan;
    }
    
    @Override
    public void resetTodayHuiZhan() {
        HuiZhanService.todayHuiZhan = null;
    }
    
    @Override
    public void updateHzAttForce1ByVid(final int attForce, final int vId) {
        this.huizhanHistoryDao.updateHzAttForce1ByVid(attForce, vId);
        synchronized (this) {
            final long oldForce = HuiZhanService.todayHuiZhan.getAttForce1();
            HuiZhanService.todayHuiZhan.setAttForce1(oldForce + attForce);
        }
    }
    
    @Override
    public void updateHzAttForce2ByVid(final int attForce, final int vId) {
        this.huizhanHistoryDao.updateHzAttForce2ByVid(attForce, vId);
        synchronized (this) {
            final long oldForce = HuiZhanService.todayHuiZhan.getAttForce2();
            HuiZhanService.todayHuiZhan.setAttForce2(oldForce + attForce);
        }
    }
    
    @Override
    public void updateHzDefForceByVid(final int defForce, final int vId) {
        this.huizhanHistoryDao.updateHzDefForceByVid(defForce, vId);
        synchronized (this) {
            final long oldForce = HuiZhanService.todayHuiZhan.getDefForce();
            HuiZhanService.todayHuiZhan.setDefForce(oldForce + defForce);
        }
    }
    
    @Override
    public void updateGatherFlagByVid(final int gFlag, final int vId) {
        this.huizhanHistoryDao.updateGatherFlagByVid(gFlag, vId);
        HuiZhanService.todayHuiZhan.setGatherFlag(gFlag);
    }
}
