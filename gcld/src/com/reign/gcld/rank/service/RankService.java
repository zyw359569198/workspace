package com.reign.gcld.rank.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.mail.service.*;
import com.reign.gcld.job.service.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.duel.service.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.kfgz.service.*;
import com.reign.gcld.activity.dao.*;
import com.reign.gcld.score.dao.*;
import com.reign.gcld.nation.dao.*;
import com.reign.gcld.feat.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.world.dao.*;
import com.reign.gcld.system.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.rank.dao.*;
import com.reign.gcld.common.log.*;
import org.apache.commons.lang.*;
import com.reign.gcld.battle.service.*;
import org.springframework.transaction.annotation.*;
import com.reign.framework.exception.*;
import com.reign.framework.json.*;
import com.reign.gcld.world.common.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.battle.reward.*;
import com.reign.gcld.rank.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.chat.service.*;
import com.reign.framework.netty.util.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.log.*;
import com.reign.util.*;
import com.reign.gcld.chat.common.*;
import java.text.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.world.domain.*;
import com.reign.gcld.task.reward.*;
import com.reign.gcld.world.util.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.general.domain.*;
import com.reign.gcld.rank.common.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.feat.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.world.service.*;
import com.reign.gcld.event.util.*;
import com.reign.gcld.event.common.*;
import com.reign.gcld.event.domain.*;
import java.io.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;
import java.lang.reflect.*;
import sun.misc.*;
import java.util.*;

@Component("rankService")
public class RankService implements IRankService, InitializingBean
{
    private static final Logger timerLog;
    private static final Logger errorLog;
    public static DoubleLinkedList<RankData> levelRankList;
    public static DoubleLinkedList<RankData> levelRankListA;
    public static DoubleLinkedList<RankData> levelRankListB;
    public static DoubleLinkedList<RankData> levelRankListC;
    private static ConcurrentMap<Integer, PlayerRank> cacheMap;
    private static ConcurrentMap<Integer, PlayerRank> cacheMapA;
    private static ConcurrentMap<Integer, PlayerRank> cacheMapB;
    private static ConcurrentMap<Integer, PlayerRank> cacheMapC;
    private static ReentrantLock levelRankLock;
    private static List<MultiRankData> positionRankListA;
    private static List<MultiRankData> positionRankListB;
    private static List<MultiRankData> positionRankListC;
    private static Map<Integer, PlayerRankInfo> mapA;
    private static Map<Integer, PlayerRankInfo> mapB;
    private static Map<Integer, PlayerRankInfo> mapC;
    private static List<MultiRankData> playerAttributes;
    public static Lock lock;
    public static String[] TIME_DIVISION1;
    public static String[] TIME_DIVISION2;
    public static String[] TIME_DIVISION3;
    public static String[] TIME_DIVISION4;
    public static TimeForNationTask[] TASK_TIME1;
    public static TimeForNationTask[] TASK_TIME2;
    public static TimeForNationTask[] TASK_TIME3;
    public static BarbarainRanker barbarainRanker;
    public static HuiZhanForceRanker huiZhanForceRanker;
    public static HuiZhanKillRanker HuiZhanKillRanker;
    public static NationTaskRankerNew nationTaskKillRanker;
    public static int nowDays;
    public static boolean hasDoubleReward;
    public static WholeKillRank wholeKillRank;
    public static ScoreRank scoreRank;
    public static TryRank tryRank;
    public static PRank pRank;
    public static FeatRank featRank;
    public static LanternRank lanternRank;
    public static MultiRanker occupyCityRanker;
    public static MultiRanker challegeRanker;
    public static int[] barCity;
    public static int[] chooseCities;
    public SdThreadCheck thread;
    String[] infos;
    String[] attInfos;
    public static ConcurrentHashMap<Integer, Integer> worldMap;
    public static Map<Integer, Integer> nationLv;
    private static String FESTIVAL_TIME;
    public static ReentrantLock[] locks;
    public static final int LOCKS_LEN;
    public static volatile NationTaskInfo taskInfo;
    public static NationFestival festival;
    public static NationMiracle nationMiracle;
    public static boolean isYuanXiaoNight;
    public static boolean isYuanXiao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private HallsCache hallsCache;
    @Autowired
    private INationTaskDao nationTaskDao;
    @Autowired
    private IForceInfoDao forceInfoDao;
    @Autowired
    private ICityDataCache cityDataCache;
    @Autowired
    private KingdomTaskRoadCache kingdomTaskRoadCache;
    @Autowired
    private ITaskKillInfoDao taskKillInfoDao;
    @Autowired
    private KingdomLvCache kingdomLvCache;
    @Autowired
    private WorldCityCache worldCityCache;
    @Autowired
    private BarbariansKillInfoDao barbariansKillInfoDao;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IMailService mailService;
    @Autowired
    private BarbarainCache barbarainCache;
    @Autowired
    private IJobService jobService;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private KingdomTaskRankingCache kingdomTaskRankingCache;
    @Autowired
    private BarbarianRankingCache barbarianRankingCache;
    @Autowired
    private IChatService chatService;
    @Autowired
    private IBattleService battleService;
    @Autowired
    private GeneralCache generalCache;
    @Autowired
    private KindomTaskCityCache kindomTaskCityCache;
    @Autowired
    private KtInitCache ktInitCache;
    @Autowired
    private KtTypeCache ktTypeCache;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private KtTzEvCache ktTzEvCache;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private CCache cCache;
    @Autowired
    private ITimerBattleService timerBattleService;
    @Autowired
    private KtMzSCache ktMzSCache;
    @Autowired
    private DataPushCenterUtil dataPushCenterUtil;
    @Autowired
    private IPlayerOccupyCityDao playerOccupyCityDao;
    @Autowired
    private IPlayerChallengeInfoDao playerChallengeInfoDao;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private RankingCache rankingCache;
    @Autowired
    private IPlayerRankingRewardDao playerRankingRewardDao;
    @Autowired
    private IPlayerGeneralDao playerGeneralDao;
    @Autowired
    private IDuelService duelService;
    @Autowired
    private ITaskInitDao taskInitDao;
    @Autowired
    private KtSCache ktSCache;
    @Autowired
    private KtKjSCache ktKjSCache;
    @Autowired
    private IPlayerExpandInfoDao playerExpandInfoDao;
    @Autowired
    private KtKjRelativeCache ktKjRelativeCache;
    @Autowired
    private IPlayerCouponDao playerCouponDao;
    @Autowired
    private IWholeKillDao wholeKillDao;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private KtSdmzSCache ktSdmzSCache;
    @Autowired
    private IKfgzSeasonService kfgzSeasonService;
    @Autowired
    private FstDbNumCache fstDbNumCache;
    @Autowired
    private IPlayerDragonDao playerDragonDao;
    @Autowired
    private KtBjCache ktBjCache;
    @Autowired
    private IPlayerScoreRankDao playerScoreRankDao;
    @Autowired
    private TpCoTnumCache tpCoTnumCache;
    @Autowired
    private IPlayerTryRankDao playerTryRankDao;
    @Autowired
    private CdExamsCache cdExamsCache;
    @Autowired
    private IPlayerPRankDao playerPRankDao;
    @Autowired
    private IPlayerFeatRankDao playerFeatRankDao;
    @Autowired
    private TpFtTnumCache tpFtTnumCache;
    @Autowired
    private TpFtRankingCache tpFtRankingCache;
    @Autowired
    private IPlayerOfficeRelativeDao playerOfficeRelativeDao;
    @Autowired
    private KtHjCache ktHjCache;
    @Autowired
    private ICityDao cityDao;
    @Autowired
    private IDbVersionDao dbVersionDao;
    @Autowired
    private KtMrCache ktMrCache;
    @Autowired
    private NationIndivTaskCache nationIndivTaskCache;
    @Autowired
    private IIndividualTaskService individualTaskService;
    @Autowired
    private KtNfCache ktNfCache;
    @Autowired
    private IPlayerHungerDao playerHungerDao;
    private static boolean[] needShowNextInvade;
    static int[] lastSerial;
    private static Object[] object;
    public static int[] taskRelative;
    public static int[] armiesSerial;
    public static long[] nextLegion;
    public static long[] nextDT;
    public static boolean[] nextLegionMessage;
    
    static {
        timerLog = new TimerLogger();
        errorLog = CommonLog.getLog(RankService.class);
        RankService.levelRankList = new DoubleLinkedList<RankData>();
        RankService.levelRankListA = new DoubleLinkedList<RankData>();
        RankService.levelRankListB = new DoubleLinkedList<RankData>();
        RankService.levelRankListC = new DoubleLinkedList<RankData>();
        RankService.cacheMap = new ConcurrentHashMap<Integer, PlayerRank>();
        RankService.cacheMapA = new ConcurrentHashMap<Integer, PlayerRank>();
        RankService.cacheMapB = new ConcurrentHashMap<Integer, PlayerRank>();
        RankService.cacheMapC = new ConcurrentHashMap<Integer, PlayerRank>();
        RankService.levelRankLock = new ReentrantLock(false);
        RankService.positionRankListA = new ArrayList<MultiRankData>();
        RankService.positionRankListB = new ArrayList<MultiRankData>();
        RankService.positionRankListC = new ArrayList<MultiRankData>();
        RankService.mapA = new ConcurrentHashMap<Integer, PlayerRankInfo>();
        RankService.mapB = new ConcurrentHashMap<Integer, PlayerRankInfo>();
        RankService.mapC = new ConcurrentHashMap<Integer, PlayerRankInfo>();
        RankService.playerAttributes = new ArrayList<MultiRankData>();
        RankService.lock = new ReentrantLock();
        RankService.TIME_DIVISION1 = new String[] { "10:00", "11:30" };
        RankService.TIME_DIVISION2 = new String[] { "15:00", "16:30" };
        RankService.TIME_DIVISION3 = new String[] { "20:00", "21:30" };
        RankService.TIME_DIVISION4 = new String[] { "21:00", "21:30" };
        RankService.TASK_TIME1 = null;
        RankService.TASK_TIME2 = null;
        RankService.TASK_TIME3 = null;
        RankService.barbarainRanker = null;
        RankService.huiZhanForceRanker = null;
        RankService.HuiZhanKillRanker = null;
        RankService.nationTaskKillRanker = null;
        RankService.nowDays = 0;
        RankService.hasDoubleReward = false;
        RankService.wholeKillRank = null;
        RankService.scoreRank = null;
        RankService.tryRank = null;
        RankService.pRank = null;
        RankService.featRank = null;
        RankService.lanternRank = null;
        RankService.occupyCityRanker = null;
        RankService.challegeRanker = null;
        RankService.barCity = new int[] { 251, 250, 252 };
        RankService.chooseCities = new int[2];
        RankService.worldMap = new ConcurrentHashMap<Integer, Integer>();
        RankService.nationLv = new ConcurrentHashMap<Integer, Integer>();
        RankService.FESTIVAL_TIME = "2014-02-14";
        RankService.locks = new ReentrantLock[10240];
        LOCKS_LEN = RankService.locks.length;
        for (int i = 0; i < RankService.LOCKS_LEN; ++i) {
            RankService.locks[i] = new ReentrantLock(false);
        }
        RankService.taskInfo = new NationTaskInfo();
        RankService.festival = null;
        RankService.nationMiracle = null;
        RankService.isYuanXiaoNight = false;
        RankService.isYuanXiao = false;
        RankService.needShowNextInvade = new boolean[3];
        RankService.lastSerial = new int[3];
        (RankService.object = new Object[2])[0] = new Object();
        RankService.object[1] = new Object();
        RankService.taskRelative = new int[3];
        RankService.armiesSerial = new int[3];
        RankService.nextLegion = new long[3];
        RankService.nextDT = new long[3];
        RankService.nextLegionMessage = new boolean[3];
    }
    
    public RankService() {
        this.thread = null;
        this.infos = new String[] { "gcld.nation.cityInfo.wei", "gcld.nation.cityInfo.shu", "gcld.nation.cityInfo.wu" };
        this.attInfos = new String[] { "gcld.nation.attInfo.wei", "gcld.nation.attInfo.shu", "gcld.nation.attInfo.wu" };
        RankService.TASK_TIME1 = new TimeForNationTask[2];
        RankService.TASK_TIME2 = new TimeForNationTask[2];
        RankService.TASK_TIME3 = new TimeForNationTask[2];
        RankService.TASK_TIME1[0] = new TimeForNationTask(RankService.TIME_DIVISION1[0]);
        RankService.TASK_TIME1[1] = new TimeForNationTask(RankService.TIME_DIVISION1[1]);
        RankService.TASK_TIME2[0] = new TimeForNationTask(RankService.TIME_DIVISION2[0]);
        RankService.TASK_TIME2[1] = new TimeForNationTask(RankService.TIME_DIVISION2[1]);
        RankService.TASK_TIME3[0] = new TimeForNationTask(RankService.TIME_DIVISION3[0]);
        RankService.TASK_TIME3[1] = new TimeForNationTask(RankService.TIME_DIVISION3[1]);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.initPlayerAttributes();
        this.initPositionListMap(1);
        this.initPositionListMap(2);
        this.initPositionListMap(3);
        this.initLevelRankList();
        this.initLevelRankListA();
        this.initLevelRankListB();
        this.initLevelRankListC();
    }
    
    @Override
    public void initNationLv() {
        final List<ForceInfo> list = this.forceInfoDao.getModels();
        if (list != null) {
            for (final ForceInfo fi : list) {
                RankService.nationLv.put(fi.getForceId(), fi.getForceLv());
            }
        }
    }
    
    private void cacheNationLv(final int forceId) {
        final ForceInfo fi = this.forceInfoDao.read(forceId);
        if (fi != null) {
            RankService.nationLv.put(fi.getForceId(), fi.getForceLv());
        }
    }
    
    @Override
    public void initRankerAndRelativeInfo() {
        try {
            (RankService.barbarainRanker = new BarbarainRanker(this.dataGetter)).init();
            this.initBarKillNum();
            this.initChooseCities();
            this.initNationTaskRanker();
            (RankService.occupyCityRanker = new PlayerOccupyCityRanker(this.dataGetter)).init();
            (RankService.challegeRanker = new PlayerChallengeRanker(this.dataGetter)).init();
            (RankService.wholeKillRank = new WholeKillRank(this.dataGetter, this.wholeKillDao.getRankList())).init();
            (RankService.scoreRank = new ScoreRank(this.dataGetter, this.playerScoreRankDao.getRankList())).init();
            (RankService.tryRank = new TryRank(this.dataGetter, this.playerTryRankDao.getRankList())).init();
            (RankService.pRank = new PRank(this.dataGetter, this.playerPRankDao.getRankList())).init();
            (RankService.featRank = new FeatRank(this.dataGetter, this.playerFeatRankDao.getRankList())).init();
            RankService.lanternRank = new LanternRank(this.dataGetter, this.dataGetter.getPlayerEventDao().getLanternRankList(21));
            (RankService.huiZhanForceRanker = new HuiZhanForceRanker(this.dataGetter)).init();
            (RankService.HuiZhanKillRanker = new HuiZhanKillRanker(this.dataGetter)).init();
        }
        catch (Exception e) {
            RankService.errorLog.error("init initRankerAndRelativeInfo", e);
        }
    }
    
    @Override
    public void initTaskRelativeInfo() {
        try {
            final int timeDivision = getTimeDevision();
            if (timeDivision == 1 || timeDivision == 3 || timeDivision == 5) {
                final int taskType = this.hasNationTasks(1);
                if (taskType == 7 || taskType == 8 || taskType == 9 || taskType == 10 || taskType == 12) {
                    (this.thread = new SdThreadCheck()).start();
                }
                if (taskType == 8) {
                    final List<NationTask> tasks = this.nationTaskDao.getModels();
                    if (tasks == null || tasks.isEmpty() || tasks.size() > 3) {
                        return;
                    }
                    NationTask task = null;
                    for (int i = 0; i < tasks.size(); ++i) {
                        task = tasks.get(i);
                        final String serialString = task.getTaskRelateInfo();
                        if (!StringUtils.isBlank(serialString)) {
                            final String[] single = serialString.split(";");
                            final int forceId = task.getForceid();
                            int armiesIndex = 0;
                            try {
                                armiesIndex = Integer.parseInt(single[0]);
                            }
                            catch (Exception e) {
                                RankService.errorLog.error(this, e);
                            }
                            RankService.armiesSerial[forceId - 1] = armiesIndex;
                        }
                        if (task.getAttType() != 0) {
                            RankService.taskRelative[task.getForceid() - 1] = task.getAttType();
                        }
                        final int taskId = task.getNationTaskId();
                        final int serial = taskId % 100 / 10;
                        if (serial <= 3 && task.getIswin() == 0) {
                            RankService.needShowNextInvade[task.getForceid() - 1] = true;
                        }
                    }
                }
            }
            RankService.nowDays = this.nowDays();
            RankService.hasDoubleReward = this.hasDoubleReward();
            final List<NationTask> tasks2 = this.nationTaskDao.getModels();
            if (tasks2 == null || tasks2.isEmpty()) {
                return;
            }
            final NationTask temp = tasks2.get(0);
            if (temp == null) {
                return;
            }
            final int taskType2 = getTaskTypeById(temp.getNationTaskId());
            if (taskType2 == 9) {
                if (RankService.taskInfo == null) {
                    RankService.taskInfo = new NationTaskInfo();
                }
                final String taskRelativeInfo = temp.getTaskRelateInfo();
                final String[] array = SymbolUtil.StringtoArray(taskRelativeInfo, ";");
                final int taskId = temp.getNationTaskId();
                final int serial = taskId % 100 / 10;
                RankService.taskInfo.setSerial(taskId % 100 / 10);
                if (serial == 2) {
                    RankService.taskInfo.setCanAtt(true);
                    final int[] arrayReal = NationTaskInfo.cityArray2;
                    final int[] templeBelong = new int[arrayReal.length];
                    for (int j = 0; j < arrayReal.length; ++j) {
                        final City city = this.cityDao.read(arrayReal[j]);
                        templeBelong[j] = city.getForceId();
                    }
                    RankService.taskInfo.setTempleBelong(templeBelong);
                }
                if (array != null) {
                    final int[] cityBelong = new int[3];
                    for (int k = 0; k < cityBelong.length; ++k) {
                        cityBelong[k] = Integer.parseInt(array[k]);
                    }
                    RankService.taskInfo.setCityBelong(cityBelong);
                    final int canAttCityForce = Integer.parseInt(array[3]);
                    RankService.taskInfo.setAttackCityForceId(canAttCityForce);
                }
                RankService.taskInfo.setEndTimeDate(temp.getEndtime());
                RankService.taskInfo.setStartTimeDate(this.getStartTime(getTimeDevision(temp.getEndtime())));
                final int state = (temp.getIswin() == 0) ? 1 : 2;
                RankService.taskInfo.setState(state);
                if (serial == 2 && RankService.taskInfo.getState() == 1) {
                    this.battleService.addXiangYangPhantomForTimer("");
                }
            }
            else if (taskType2 == 10) {
                RankService.taskInfo = null;
                final Date endDate = temp.getEndtime();
                final long startTime = endDate.getTime() - 5400000L;
                final Date startDate = new Date(startTime);
                final int interVal = this.ktMrCache.getInterValTime();
                final List<KtMrTroop> troops = this.ktMrCache.getTroopList();
                final List<Integer> defaultTimeList = this.ktMrCache.getDefaultTimeList();
                final List<KtMrTarget> targets = new ArrayList<KtMrTarget>();
                final boolean[] taskIsOver = new boolean[3];
                boolean allIsOver = true;
                final int[] curSerial = new int[3];
                for (final NationTask task2 : tasks2) {
                    final int taskId2 = task2.getNationTaskId();
                    final int serial2 = taskId2 % 1000 / 100;
                    final int forceId2 = task2.getForceid();
                    final ForceInfo forceInfo = this.forceInfoDao.read(forceId2);
                    if (forceInfo != null) {
                        final KtMrTarget one = this.ktMrCache.getTargetByNationAndLv(forceId2, forceInfo.getForceLv(), serial2);
                        if (one != null) {
                            targets.add(one);
                        }
                    }
                    taskIsOver[forceId2 - 1] = (task2.getIswin() != 0);
                    if (task2.getIswin() == 0) {
                        allIsOver = false;
                    }
                    curSerial[forceId2 - 1] = serial2;
                }
                (RankService.nationMiracle = new NationMiracle(startDate, interVal, troops, this.dataGetter, targets, defaultTimeList)).setNationTaskIsOver(taskIsOver);
                RankService.nationMiracle.setReboot(true);
                RankService.nationMiracle.curSerial = curSerial;
                RankService.nationMiracle.restoreMiracle(temp.getTaskRelateInfo(), allIsOver);
                if (!allIsOver && getTimeDevision() > 0) {
                    RankService.nationMiracle.start();
                }
            }
            else if (taskType2 == 12) {
                RankService.taskInfo = null;
                final Date endDate = temp.getEndtime();
                final long startTime = endDate.getTime() - 5400000L;
                final Date startDate = new Date(startTime);
                final int interVal = this.ktNfCache.getInterValTime();
                final List<KtMrTroop> troops = this.ktNfCache.getTroopList();
                final List<Integer> defaultTimeList = this.ktNfCache.getDefaultTimeList();
                final List<KtMrTarget> targets = new ArrayList<KtMrTarget>();
                final boolean[] taskIsOver = new boolean[3];
                boolean allIsOver = true;
                final int[] curSerial = new int[3];
                for (final NationTask task2 : tasks2) {
                    final int taskId2 = task2.getNationTaskId();
                    final int serial2 = taskId2 % 1000 / 100;
                    final int forceId2 = task2.getForceid();
                    final ForceInfo forceInfo = this.forceInfoDao.read(forceId2);
                    if (forceInfo != null) {
                        final KtMrTarget one = this.ktNfCache.getTargetByNationAndLv(forceId2, forceInfo.getForceLv(), serial2);
                        if (one != null) {
                            targets.add(one);
                        }
                    }
                    taskIsOver[forceId2 - 1] = (task2.getIswin() != 0);
                    if (task2.getIswin() == 0) {
                        allIsOver = false;
                    }
                    curSerial[forceId2 - 1] = serial2;
                }
                (RankService.festival = new NationFestival(startDate, interVal, troops, this.dataGetter, targets, defaultTimeList)).setNationTaskIsOver(taskIsOver);
                RankService.festival.setReboot(true);
                RankService.festival.curSerial = curSerial;
                RankService.festival.restoreMiracle(temp.getTaskRelateInfo(), allIsOver);
                final List<PlayerHunger> list = this.playerHungerDao.getModels();
                RankService.festival.restorePlayerMap(list);
                if (!allIsOver && getTimeDevision() > 0) {
                    RankService.festival.start();
                }
            }
            else {
                RankService.taskInfo = null;
                RankService.nationMiracle = null;
                RankService.festival = null;
            }
        }
        catch (Exception e2) {
            RankService.errorLog.error(this, e2);
        }
    }
    
    @Override
    public void addFeat(final int playerId, final int feat) {
        try {
            RankService.locks[playerId % RankService.LOCKS_LEN].lock();
            if (this.playerAttributeDao.getFunctionId(playerId).toCharArray()[32] != '1' || RankService.featRank == null) {
                return;
            }
            final int before = this.playerFeatRankDao.getTotalFeat(playerId);
            this.playerFeatRankDao.addTotalFeat(playerId, feat);
            final int after = before + feat;
            this.calFeatBox(playerId, before, after);
            RankService.featRank.fireRankEvent(1, new com.reign.gcld.world.common.RankData(playerId, after));
        }
        catch (Exception e) {
            RankService.errorLog.error("#class:RankService#method:addFeat#playerId:" + playerId + "#feat:" + feat, e);
            return;
        }
        finally {
            RankService.locks[playerId % RankService.LOCKS_LEN].unlock();
        }
        RankService.locks[playerId % RankService.LOCKS_LEN].unlock();
    }
    
    @Override
    public void initForceInfo() {
        try {
            ForceInfo forceInfo = null;
            for (int i = 1; i <= 3; ++i) {
                forceInfo = this.forceInfoDao.read(i);
                if (forceInfo == null) {
                    forceInfo = new ForceInfo();
                    forceInfo.setForceId(i);
                    forceInfo.setForceLv(1);
                    forceInfo.setForceExp(0);
                    forceInfo.setIswin(0);
                    forceInfo.setBeidiQinmidu(0);
                    forceInfo.setBeidiShoumaiCount(WorldCityCommon.MANZU_SHOUMAI_COUNT_PER_DAY);
                    forceInfo.setBeidiShoumaiSum(0);
                    forceInfo.setXirongQinmidu(0);
                    forceInfo.setXirongShoumaiCount(WorldCityCommon.MANZU_SHOUMAI_COUNT_PER_DAY);
                    forceInfo.setXirongShoumaiSum(0);
                    forceInfo.setDongyiQinmidu(0);
                    forceInfo.setDongyiShoumaiCount(WorldCityCommon.MANZU_SHOUMAI_COUNT_PER_DAY);
                    forceInfo.setDongyiShoumaiSum(0);
                    forceInfo.setIds(null);
                    forceInfo.setId(0);
                    forceInfo.setTryEndTime(null);
                    forceInfo.setStage(4);
                    forceInfo.setTryWin(0);
                    forceInfo.setGeneralNum(0);
                    forceInfo.setPWin(0);
                    forceInfo.setPForceId(0);
                    forceInfo.setPCityId(0);
                    forceInfo.setPId(0);
                    forceInfo.setFarmInvestSum(0L);
                    forceInfo.setLv(1);
                    this.forceInfoDao.create(forceInfo);
                }
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("initForceInfo fail...");
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
            throw new RuntimeException("initForceInfo fail" + e.getMessage());
        }
    }
    
    private void initNationTaskRanker() {
        int type = 1;
        if (RankService.chooseCities[0] == 0 || RankService.chooseCities[1] == 0) {
            type = 0;
        }
        RankService.nationTaskKillRanker = new NationTaskRankerNew(type, this.dataGetter);
    }
    
    private void initBarKillNum() {
        try {
            for (int i = 1; i <= 3; ++i) {
                final ForceInfo forceInfo = this.forceInfoDao.read(i);
                final int isWin = (forceInfo.getIswin() == null) ? 0 : forceInfo.getIswin();
                final Date nowDate = new Date();
                final Date enDate = (forceInfo.getEndtime() == null) ? nowDate : forceInfo.getEndtime();
                final int sum = this.barbariansKillInfoDao.getKillSumByForceId(i);
                if (nowDate.getTime() >= enDate.getTime() && isWin == 0 && sum != 0) {
                    this.forceInfoDao.updateIsWin(i, 1);
                }
                NewBattleManager.getInstance().addBarbarainKill(i, sum);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("initBarKillNum fail ...", e);
        }
    }
    
    private void initChooseCities() {
        final List<NationTask> tasks = this.nationTaskDao.getModels();
        if (tasks == null || tasks.isEmpty()) {
            return;
        }
        if (tasks.size() != 6) {
            return;
        }
        Collections.sort(tasks);
        for (int i = 0; i < RankService.chooseCities.length; ++i) {
            RankService.chooseCities[i] = tasks.get(i).getTarget();
        }
    }
    
    private void initPlayerAttributes() {
        final List<PlayerOfficeRelative> attributes = this.playerOfficeRelativeDao.getModels();
        for (final PlayerOfficeRelative attribute : attributes) {
            final int playerId = attribute.getPlayerId();
            final int positionId = ((Halls)this.hallsCache.get((Object)attribute.getOfficerId())).getOfficialId();
            if (positionId == 13) {
                continue;
            }
            final Player player = this.playerDao.read(playerId);
            if (player == null) {
                continue;
            }
            final int playerLv = player.getPlayerLv();
            final ComparableFactor[] arrays = MultiRankData.orgnizeValue(positionId, 1, playerLv, 0);
            final MultiRankData multiRankData = new MultiRankData(playerId, arrays);
            RankService.playerAttributes.add(multiRankData);
        }
    }
    
    private void initPositionListMap(final int i) {
        List<MultiRankData> rankList = null;
        Map<Integer, PlayerRankInfo> map = null;
        switch (i) {
            case 1: {
                RankService.positionRankListA = this.getPlayerListByForce(i);
                rankList = RankService.positionRankListA;
                map = RankService.mapA;
                break;
            }
            case 2: {
                RankService.positionRankListB = this.getPlayerListByForce(i);
                rankList = RankService.positionRankListB;
                map = RankService.mapB;
                break;
            }
            case 3: {
                RankService.positionRankListC = this.getPlayerListByForce(i);
                rankList = RankService.positionRankListC;
                map = RankService.mapC;
                break;
            }
        }
        int index = 1;
        for (final MultiRankData data : rankList) {
            final int playerId = data.playerId;
            final PlayerRankInfo rankInfo = new PlayerRankInfo();
            rankInfo.setPlayerId(playerId);
            rankInfo.setRankData(data);
            rankInfo.setRank(index++);
            map.put(playerId, rankInfo);
        }
    }
    
    @Override
    public void firePositionRank(final int forceId, final MultiRankData rankData) {
        RankService.lock.lock();
        try {
            List<MultiRankData> list = null;
            Map<Integer, PlayerRankInfo> map = null;
            switch (forceId) {
                case 1: {
                    list = RankService.positionRankListA;
                    map = RankService.mapA;
                    break;
                }
                case 2: {
                    list = RankService.positionRankListB;
                    map = RankService.mapB;
                    break;
                }
                case 3: {
                    list = RankService.positionRankListC;
                    map = RankService.mapC;
                    break;
                }
            }
            final PlayerRankInfo rankInfo = map.get(rankData.playerId);
            if (rankInfo == null) {
                final List<ComparableFactor> rankList = rankData.value;
                if (rankList != null && !rankList.isEmpty() && rankList.get(0).getValue() != 13) {
                    list.add(rankData);
                    Collections.sort(list);
                    this.resetMap(map, forceId);
                }
            }
            else {
                final List<ComparableFactor> rankList = rankData.value;
                if (rankList != null && !rankList.isEmpty() && rankList.get(0).getValue() == 13) {
                    list.remove(rankInfo.getRankData());
                    Collections.sort(list);
                    map.remove(rankData.playerId);
                }
                else {
                    list.get(rankInfo.getRank() - 1).value = rankData.value;
                    Collections.sort(list);
                }
                this.resetMap(map, forceId);
            }
        }
        finally {
            RankService.lock.unlock();
        }
        RankService.lock.unlock();
    }
    
    private void resetMap(final Map<Integer, PlayerRankInfo> map, final int forceId) {
        map.clear();
        List<MultiRankData> rankList = null;
        switch (forceId) {
            case 1: {
                rankList = RankService.positionRankListA;
                break;
            }
            case 2: {
                rankList = RankService.positionRankListB;
                break;
            }
            case 3: {
                rankList = RankService.positionRankListC;
                break;
            }
        }
        int index = 1;
        for (final MultiRankData data : rankList) {
            final int playerId = data.playerId;
            final PlayerRankInfo rankInfo = new PlayerRankInfo();
            rankInfo.setPlayerId(playerId);
            rankInfo.setRankData(data);
            rankInfo.setRank(index++);
            map.put(playerId, rankInfo);
        }
    }
    
    @Override
    public List<Integer> getRankInfo(final int forceId, final int startRank, final int count) {
        final List<Integer> result = new ArrayList<Integer>();
        List<MultiRankData> rankDatas = null;
        switch (forceId) {
            case 1: {
                rankDatas = RankService.positionRankListA;
                break;
            }
            case 2: {
                rankDatas = RankService.positionRankListB;
                break;
            }
            case 3: {
                rankDatas = RankService.positionRankListC;
                break;
            }
        }
        for (int i = 0; i < count; ++i) {
            result.add(rankDatas.get(i + startRank - 1).playerId);
        }
        return result;
    }
    
    @Override
    public int getPlayerPositionRank(final int playerId, final int forceId) {
        Map<Integer, PlayerRankInfo> map = null;
        switch (forceId) {
            case 1: {
                map = RankService.mapA;
                break;
            }
            case 2: {
                map = RankService.mapB;
                break;
            }
            case 3: {
                map = RankService.mapC;
                break;
            }
        }
        final PlayerRankInfo rankInfo = map.get(playerId);
        if (rankInfo == null) {
            return -1;
        }
        return (rankInfo.getRank() > 200) ? -1 : rankInfo.getRank();
    }
    
    private List<MultiRankData> getPlayerListByForce(final int forceId) {
        List<MultiRankData> rankDatas = null;
        switch (forceId) {
            case 1: {
                rankDatas = RankService.positionRankListA;
                break;
            }
            case 2: {
                rankDatas = RankService.positionRankListB;
                break;
            }
            case 3: {
                rankDatas = RankService.positionRankListC;
                break;
            }
        }
        for (final MultiRankData data : RankService.playerAttributes) {
            if (this.playerDao.read(data.playerId).getForceId() == forceId) {
                rankDatas.add(data);
            }
        }
        Collections.sort(rankDatas);
        return rankDatas;
    }
    
    @Override
    public List<Integer> getForcePositionRankList(final int forceId, final int startRank, final int count) {
        final List<Integer> result = new ArrayList<Integer>();
        int seq = 0;
        int num = 0;
        Iterator<MultiRankData> di = null;
        switch (forceId) {
            case 1: {
                di = RankService.positionRankListA.iterator();
                break;
            }
            case 2: {
                di = RankService.positionRankListB.iterator();
                break;
            }
            case 3: {
                di = RankService.positionRankListC.iterator();
                break;
            }
        }
        if (di == null) {
            return result;
        }
        while (di.hasNext()) {
            final MultiRankData node = di.next();
            if (++seq >= startRank) {
                result.add(node.playerId);
                ++num;
            }
            if (num >= count) {
                break;
            }
        }
        return result;
    }
    
    @Transactional
    private void initLevelRankList() {
        final List<Player> resultList = this.playerDao.getPlayerLevelRankList(200);
        int index = 1;
        for (final Player player : resultList) {
            final Node<RankData> node = RankService.levelRankList.addWithReturn(new RankData(player.getPlayerId(), player.getPlayerLv(), 0));
            final PlayerRank pr = new PlayerRank();
            pr.levelData = node;
            pr.levelRank = index++;
            final int playerId = player.getPlayerId();
            RankService.cacheMap.put(playerId, pr);
        }
    }
    
    private void initLevelRankListA() {
        final List<Player> resultList = this.playerDao.getPlayerLevelRankList(1, 200);
        int index = 1;
        for (final Player player : resultList) {
            final Node<RankData> node = RankService.levelRankListA.addWithReturn(new RankData(player.getPlayerId(), player.getPlayerLv(), 0));
            final PlayerRank pr = new PlayerRank();
            pr.levelData = node;
            pr.levelRank = index++;
            final int playerId = player.getPlayerId();
            RankService.cacheMapA.put(playerId, pr);
        }
    }
    
    private void initLevelRankListB() {
        final List<Player> resultList = this.playerDao.getPlayerLevelRankList(2, 200);
        int index = 1;
        for (final Player player : resultList) {
            final Node<RankData> node = RankService.levelRankListB.addWithReturn(new RankData(player.getPlayerId(), player.getPlayerLv(), 0));
            final PlayerRank pr = new PlayerRank();
            pr.levelData = node;
            pr.levelRank = index++;
            final int playerId = player.getPlayerId();
            RankService.cacheMapB.put(playerId, pr);
        }
    }
    
    private void initLevelRankListC() {
        final List<Player> resultList = this.playerDao.getPlayerLevelRankList(3, 200);
        int index = 1;
        for (final Player player : resultList) {
            final Node<RankData> node = RankService.levelRankListC.addWithReturn(new RankData(player.getPlayerId(), player.getPlayerLv(), 0));
            final PlayerRank pr = new PlayerRank();
            pr.levelData = node;
            pr.levelRank = index++;
            final int playerId = player.getPlayerId();
            RankService.cacheMapC.put(playerId, pr);
        }
    }
    
    @Override
    public void fireRankEvent(final int rankId, final RankData data) {
        switch (rankId) {
            case 1: {
                this.fireLevelRankEvent(data);
                this.fireLevelRankEvent(this.playerDao.read(data.playerId).getForceId(), data);
                break;
            }
        }
    }
    
    @Override
    public int getRank(final int rankId, final int playerId) {
        switch (rankId) {
            case 1: {
                return this.getLevelRank(playerId);
            }
            default: {
                throw new InternalException("unknow rank type [type:" + rankId + "]");
            }
        }
    }
    
    @Override
    public int getRank(final int rankId, final int playerId, final int forceId) {
        switch (rankId) {
            case 1: {
                return this.getLevelRank(playerId, forceId);
            }
            default: {
                throw new InternalException("unknow rank type [type:" + rankId + "]");
            }
        }
    }
    
    @Override
    public byte[] getRankList(final int rankId) {
        switch (rankId) {
            case 1: {
                return this.getLevelRankList();
            }
            default: {
                throw new InternalException("unknow rank type [type:" + rankId + "]");
            }
        }
    }
    
    @Override
    public List<RankData> getRankDataList(final int rankId) {
        switch (rankId) {
            case 1: {
                return this.getLevelRankDataList();
            }
            default: {
                throw new InternalException("unknow rank type [type:" + rankId + "]");
            }
        }
    }
    
    private List<RankData> getLevelRankDataList() {
        final List<RankData> resultList = new ArrayList<RankData>(200);
        final DoubleIterator<Node<RankData>> di = RankService.levelRankList.iterator(false);
        while (di.hasNext()) {
            resultList.add((RankData)di.next().e);
        }
        return resultList;
    }
    
    public int getLegionNumInRankList(final int legionId, final int scope) {
        int rtn = 0;
        int seq = 1;
        for (DoubleIterator<Node<RankData>> di = RankService.levelRankList.iterator(false); di.hasNext() && seq <= scope; ++seq) {
            final Node<RankData> node = di.next();
            if (node.e.legionId == legionId) {
                ++rtn;
            }
        }
        return rtn;
    }
    
    @Override
    public List<Integer> getLevelRankList(final int startRank, final int count) {
        final List<Integer> result = new ArrayList<Integer>();
        int seq = 0;
        int num = 0;
        final DoubleIterator<Node<RankData>> di = RankService.levelRankList.iterator(false);
        while (di.hasNext()) {
            final Node<RankData> node = di.next();
            if (++seq >= startRank) {
                result.add(node.e.playerId);
                ++num;
            }
            if (num >= count) {
                break;
            }
        }
        return result;
    }
    
    @Override
    public List<Integer> getForceLevelRankList(final int forceId, final int startRank, final int count) {
        final List<Integer> result = new ArrayList<Integer>();
        int seq = 0;
        int num = 0;
        DoubleIterator<Node<RankData>> di = null;
        switch (forceId) {
            case 1: {
                di = RankService.levelRankListA.iterator(false);
                break;
            }
            case 2: {
                di = RankService.levelRankListB.iterator(false);
                break;
            }
            case 3: {
                di = RankService.levelRankListC.iterator(false);
                break;
            }
        }
        if (di == null) {
            return result;
        }
        while (di.hasNext()) {
            final Node<RankData> node = di.next();
            if (++seq >= startRank) {
                result.add(node.e.playerId);
                ++num;
            }
            if (num >= count) {
                break;
            }
        }
        return result;
    }
    
    @Override
    public List<RankData> getRankDataList(final int startRank, final int count) {
        final List<RankData> result = new ArrayList<RankData>();
        int seq = 0;
        int num = 0;
        final DoubleIterator<Node<RankData>> di = RankService.levelRankList.iterator(false);
        while (di.hasNext()) {
            final Node<RankData> node = di.next();
            if (++seq >= startRank) {
                result.add(node.e);
                ++num;
            }
            if (num >= count) {
                break;
            }
        }
        return result;
    }
    
    private byte[] getLevelRankList() {
        final JsonDocument doc = new JsonDocument();
        doc.startArray("rankList");
        final DoubleIterator<Node<RankData>> di = RankService.levelRankList.iterator(false);
        while (di.hasNext()) {
            final Node<RankData> node = di.next();
            PlayerRank pr = RankService.cacheMap.get(node.e.playerId);
            if (pr == null || pr.levelRankByte == null) {
                pr = new PlayerRank();
                pr.levelRankByte = this.getResult(node.e);
                final PlayerRank temp = RankService.cacheMap.putIfAbsent(node.e.playerId, pr);
                if (temp != null) {
                    temp.levelRankByte = pr.levelRankByte;
                    pr = temp;
                }
            }
            doc.appendJson(pr.levelRankByte);
        }
        doc.endArray();
        return JsonBuilder.getObjectJson(State.SUCCESS, doc.toByte());
    }
    
    private int getLevelRank(final int playerId) {
        PlayerRank pr = RankService.cacheMap.get(playerId);
        if (pr == null || !this.isValidate(pr, 1)) {
            final DoubleIterator<Node<RankData>> di = RankService.levelRankList.iterator(false);
            int index = 1;
            boolean find = false;
            while (di.hasNext()) {
                final Node<RankData> node = di.next();
                if (node.e.playerId == playerId) {
                    this.updatePlayerRank(playerId, index, 1);
                    this.updatePlayerRank(playerId, node, 1);
                    pr = RankService.cacheMap.get(playerId);
                    find = true;
                    break;
                }
                ++index;
            }
            if (!find) {
                this.updatePlayerRank(playerId, 0, 1);
                this.updatePlayerRank(playerId, null, 1);
            }
        }
        return this.getPlayerRank(pr, 1);
    }
    
    private int getLevelRank(final int playerId, final int forceId) {
        PlayerRank pr = null;
        DoubleIterator<Node<RankData>> di = null;
        if (1 == forceId) {
            pr = RankService.cacheMapA.get(playerId);
            if (pr == null || !this.isValidate(pr, 1)) {
                di = RankService.levelRankListA.iterator(false);
                int index = 1;
                boolean find = false;
                while (di.hasNext()) {
                    final Node<RankData> node = di.next();
                    if (node.e.playerId == playerId) {
                        this.updatePlayerRank(playerId, index, 1, forceId);
                        this.updatePlayerRank(playerId, node, 1, forceId);
                        pr = RankService.cacheMapA.get(playerId);
                        find = true;
                        break;
                    }
                    ++index;
                }
                if (!find) {
                    this.updatePlayerRank(playerId, 0, 1, forceId);
                    this.updatePlayerRank(playerId, null, 1, forceId);
                }
            }
        }
        else if (2 == forceId) {
            pr = RankService.cacheMapB.get(playerId);
            if (pr == null || !this.isValidate(pr, 1)) {
                di = RankService.levelRankListB.iterator(false);
                int index = 1;
                boolean find = false;
                while (di.hasNext()) {
                    final Node<RankData> node = di.next();
                    if (node.e.playerId == playerId) {
                        this.updatePlayerRank(playerId, index, 1, forceId);
                        this.updatePlayerRank(playerId, node, 1, forceId);
                        pr = RankService.cacheMapB.get(playerId);
                        find = true;
                        break;
                    }
                    ++index;
                }
                if (!find) {
                    this.updatePlayerRank(playerId, 0, 1, forceId);
                    this.updatePlayerRank(playerId, null, 1, forceId);
                }
            }
        }
        else {
            pr = RankService.cacheMapC.get(playerId);
            if (pr == null || !this.isValidate(pr, 1)) {
                di = RankService.levelRankListC.iterator(false);
                int index = 1;
                boolean find = false;
                while (di.hasNext()) {
                    final Node<RankData> node = di.next();
                    if (node.e.playerId == playerId) {
                        this.updatePlayerRank(playerId, index, 1, forceId);
                        this.updatePlayerRank(playerId, node, 1, forceId);
                        pr = RankService.cacheMapC.get(playerId);
                        find = true;
                        break;
                    }
                    ++index;
                }
                if (!find) {
                    this.updatePlayerRank(playerId, 0, 1, forceId);
                    this.updatePlayerRank(playerId, null, 1, forceId);
                }
            }
        }
        return this.getPlayerRank(pr, 1);
    }
    
    private void fireLevelRankEvent(final RankData data) {
        try {
            RankService.levelRankLock.lock();
            this.setRanking(RankService.levelRankList, data, 200, 1, RankService.cacheMap);
            final PlayerRank pr = RankService.cacheMap.get(data.playerId);
            if (pr != null) {
                pr.levelRankByte = this.getResult(data);
            }
        }
        finally {
            RankService.levelRankLock.unlock();
        }
        RankService.levelRankLock.unlock();
    }
    
    private void fireLevelRankEvent(final int forceId, final RankData data) {
        try {
            RankService.levelRankLock.lock();
            PlayerRank pr = null;
            if (1 == forceId) {
                this.setRanking(RankService.levelRankListA, data, 200, 1, RankService.cacheMapA, forceId);
                pr = RankService.cacheMapA.get(data.playerId);
            }
            else if (2 == forceId) {
                this.setRanking(RankService.levelRankListB, data, 200, 1, RankService.cacheMapB, forceId);
                pr = RankService.cacheMapB.get(data.playerId);
            }
            else {
                this.setRanking(RankService.levelRankListC, data, 200, 1, RankService.cacheMapC, forceId);
                pr = RankService.cacheMapC.get(data.playerId);
            }
            if (pr != null) {
                pr.levelRankByte = this.getResult(data);
            }
        }
        finally {
            RankService.levelRankLock.unlock();
        }
        RankService.levelRankLock.unlock();
    }
    
    private byte[] getResult(final RankData data) {
        final Player player = this.playerDao.read(data.playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("playerId", (Object)data.playerId);
        doc.createElement("playerName", player.getPlayerName());
        doc.createElement("playerLv", (Object)data.value);
        doc.endObject();
        return doc.toByte();
    }
    
    public boolean setRanking(final DoubleLinkedList<RankData> rankList, final RankData rankData, final int max, final int type, final ConcurrentMap<Integer, PlayerRank> concurrentMap) {
        boolean change = false;
        final PlayerRank pr = concurrentMap.get(rankData.playerId);
        if (pr == null || !this.isValidateNode(pr, type)) {
            if (rankList.size() == 0) {
                final Node<RankData> node = rankList.addWithReturn(rankData);
                this.updatePlayerRank(rankData.playerId, 1, type);
                this.updatePlayerRank(rankData.playerId, node, type);
                change = true;
                return change;
            }
            boolean insert = false;
            final DoubleIterator<Node<RankData>> di = rankList.iterator(true);
            while (di.hasPrev()) {
                final Node<RankData> node2 = di.prev();
                final RankData data = node2.e;
                if (rankData.value < data.value) {
                    final Node<RankData> temp = rankList.addWithReturn(rankData, node2);
                    this.updatePlayerRank(rankData.playerId, temp, type);
                    if (rankList.size() > max) {
                        final RankData popData = rankList.pop();
                        this.updatePlayerRank(popData.playerId, null, type);
                    }
                    this.clearCache(type);
                    insert = true;
                    change = true;
                    break;
                }
            }
            if (!insert && rankList.size() < max) {
                final Node<RankData> temp2 = rankList.addBeforeWithReturn(rankData);
                this.updatePlayerRank(rankData.playerId, temp2, type);
                this.clearCache(type);
                change = true;
            }
        }
        else {
            final Node<RankData> currentNode = this.getPlayerRankNode(pr, type);
            if (currentNode.e.legionId != rankData.legionId) {
                currentNode.e.legionId = rankData.legionId;
                pr.levelRankByte = null;
            }
            if (currentNode.e.value == rankData.value) {
                return change;
            }
            boolean insert2 = false;
            if (currentNode.e.value < rankData.value) {
                Node<RankData> tempNode = currentNode;
                while ((tempNode = tempNode.prev) != null) {
                    if (tempNode.e == null) {
                        final Node<RankData> temp3 = rankList.addBeforeWithReturn(rankData);
                        rankList.remove(currentNode);
                        this.updatePlayerRank(rankData.playerId, temp3, type);
                        if (rankList.size() > max) {
                            final RankData popData2 = rankList.pop();
                            this.updatePlayerRank(popData2.playerId, null, type);
                        }
                        this.clearCache(type);
                        insert2 = true;
                        change = true;
                        break;
                    }
                    if (rankData.value < tempNode.e.value) {
                        final Node<RankData> temp3 = rankList.addWithReturn(rankData, tempNode);
                        rankList.remove(currentNode);
                        this.updatePlayerRank(rankData.playerId, temp3, type);
                        if (rankList.size() > max) {
                            final RankData popData2 = rankList.pop();
                            this.updatePlayerRank(popData2.playerId, null, type);
                        }
                        this.clearCache(type);
                        insert2 = true;
                        change = true;
                        break;
                    }
                }
            }
            else {
                Node<RankData> tempNode = currentNode;
                while ((tempNode = tempNode.next) != null) {
                    if (tempNode.e == null) {
                        continue;
                    }
                    if (rankData.value > tempNode.e.value) {
                        final Node<RankData> temp3 = rankList.addBeforeWithReturn(rankData, tempNode);
                        rankList.remove(currentNode);
                        this.updatePlayerRank(rankData.playerId, temp3, type);
                        if (rankList.size() > max) {
                            final RankData popData2 = rankList.pop();
                            this.updatePlayerRank(popData2.playerId, null, type);
                        }
                        this.clearCache(type);
                        insert2 = true;
                        change = true;
                        break;
                    }
                }
            }
            if (!insert2) {
                rankList.remove(currentNode);
                if (rankList.size() < max) {
                    final Node<RankData> temp2 = rankList.addWithReturn(rankData);
                    this.updatePlayerRank(rankData.playerId, temp2, type);
                    change = true;
                }
                this.clearCache(type);
            }
        }
        return change;
    }
    
    public boolean setRanking(final DoubleLinkedList<RankData> rankList, final RankData rankData, final int max, final int type, final ConcurrentMap<Integer, PlayerRank> concurrentMap, final int forecId) {
        boolean change = false;
        final PlayerRank pr = concurrentMap.get(rankData.playerId);
        if (pr == null || !this.isValidateNode(pr, type)) {
            if (rankList.size() == 0) {
                final Node<RankData> node = rankList.addWithReturn(rankData);
                this.updatePlayerRank(rankData.playerId, 1, type, forecId);
                this.updatePlayerRank(rankData.playerId, node, type, forecId);
                change = true;
                return change;
            }
            boolean insert = false;
            final DoubleIterator<Node<RankData>> di = rankList.iterator(true);
            while (di.hasPrev()) {
                final Node<RankData> node2 = di.prev();
                final RankData data = node2.e;
                if (rankData.value < data.value) {
                    final Node<RankData> temp = rankList.addWithReturn(rankData, node2);
                    this.updatePlayerRank(rankData.playerId, temp, type, forecId);
                    if (rankList.size() > max) {
                        final RankData popData = rankList.pop();
                        this.updatePlayerRank(popData.playerId, null, type, forecId);
                    }
                    this.clearCache(type);
                    insert = true;
                    change = true;
                    break;
                }
            }
            if (!insert && rankList.size() < max) {
                final Node<RankData> temp2 = rankList.addBeforeWithReturn(rankData);
                this.updatePlayerRank(rankData.playerId, temp2, type, forecId);
                this.clearCache(type);
                change = true;
            }
        }
        else {
            final Node<RankData> currentNode = this.getPlayerRankNode(pr, type);
            if (currentNode.e.legionId != rankData.legionId) {
                currentNode.e.legionId = rankData.legionId;
                pr.levelRankByte = null;
            }
            if (currentNode.e.value == rankData.value) {
                return change;
            }
            boolean insert2 = false;
            if (currentNode.e.value < rankData.value) {
                Node<RankData> tempNode = currentNode;
                while ((tempNode = tempNode.prev) != null) {
                    if (tempNode.e == null) {
                        final Node<RankData> temp3 = rankList.addBeforeWithReturn(rankData);
                        rankList.remove(currentNode);
                        this.updatePlayerRank(rankData.playerId, temp3, type, forecId);
                        if (rankList.size() > max) {
                            final RankData popData2 = rankList.pop();
                            this.updatePlayerRank(popData2.playerId, null, type, forecId);
                        }
                        this.clearCache(type);
                        insert2 = true;
                        change = true;
                        break;
                    }
                    if (rankData.value < tempNode.e.value) {
                        final Node<RankData> temp3 = rankList.addWithReturn(rankData, tempNode);
                        rankList.remove(currentNode);
                        this.updatePlayerRank(rankData.playerId, temp3, type, forecId);
                        if (rankList.size() > max) {
                            final RankData popData2 = rankList.pop();
                            this.updatePlayerRank(popData2.playerId, null, type, forecId);
                        }
                        this.clearCache(type);
                        insert2 = true;
                        change = true;
                        break;
                    }
                }
            }
            else {
                Node<RankData> tempNode = currentNode;
                while ((tempNode = tempNode.next) != null) {
                    if (tempNode.e == null) {
                        continue;
                    }
                    if (rankData.value > tempNode.e.value) {
                        final Node<RankData> temp3 = rankList.addBeforeWithReturn(rankData, tempNode);
                        rankList.remove(currentNode);
                        this.updatePlayerRank(rankData.playerId, temp3, type, forecId);
                        if (rankList.size() > max) {
                            final RankData popData2 = rankList.pop();
                            this.updatePlayerRank(popData2.playerId, null, type);
                        }
                        this.clearCache(type);
                        insert2 = true;
                        change = true;
                        break;
                    }
                }
            }
            if (!insert2) {
                rankList.remove(currentNode);
                if (rankList.size() < max) {
                    final Node<RankData> temp2 = rankList.addWithReturn(rankData);
                    this.updatePlayerRank(rankData.playerId, temp2, type, forecId);
                    change = true;
                }
                this.clearCache(type);
            }
        }
        return change;
    }
    
    private int getPlayerRank(final PlayerRank pr, final int type) {
        if (pr == null) {
            return 0;
        }
        return pr.levelRank;
    }
    
    private Node<RankData> getPlayerRankNode(final PlayerRank pr, final int type) {
        if (pr == null) {
            return null;
        }
        return pr.levelData;
    }
    
    private void updatePlayerRank(final int playerId, final int value, final int type) {
        PlayerRank pr = RankService.cacheMap.get(playerId);
        if (pr == null) {
            pr = new PlayerRank();
        }
        pr.levelRank = value;
        final PlayerRank temp = RankService.cacheMap.putIfAbsent(playerId, pr);
        if (temp != null) {
            temp.levelRank = value;
        }
    }
    
    private void updatePlayerRank(final int playerId, final int value, final int type, final int forceId) {
        if (1 == forceId) {
            PlayerRank pr = RankService.cacheMapA.get(playerId);
            if (pr == null) {
                pr = new PlayerRank();
            }
            pr.levelRank = value;
            final PlayerRank temp = RankService.cacheMapA.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelRank = value;
            }
        }
        else if (2 == forceId) {
            PlayerRank pr = RankService.cacheMapB.get(playerId);
            if (pr == null) {
                pr = new PlayerRank();
            }
            pr.levelRank = value;
            final PlayerRank temp = RankService.cacheMapB.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelRank = value;
            }
        }
        else {
            PlayerRank pr = RankService.cacheMapC.get(playerId);
            if (pr == null) {
                pr = new PlayerRank();
            }
            pr.levelRank = value;
            final PlayerRank temp = RankService.cacheMapC.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelRank = value;
            }
        }
    }
    
    private void updatePlayerRank(final int playerId, final Node<RankData> value, final int type) {
        PlayerRank pr = RankService.cacheMap.get(playerId);
        if (pr == null) {
            pr = new PlayerRank();
        }
        pr.levelData = value;
        final PlayerRank temp = RankService.cacheMap.putIfAbsent(playerId, pr);
        if (temp != null) {
            temp.levelData = value;
        }
    }
    
    private void updatePlayerRank(final int playerId, final Node<RankData> value, final int type, final int forceId) {
        if (1 == forceId) {
            PlayerRank pr = RankService.cacheMapA.get(playerId);
            if (pr == null) {
                pr = new PlayerRank();
            }
            pr.levelData = value;
            final PlayerRank temp = RankService.cacheMapA.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelData = value;
            }
        }
        else if (2 == forceId) {
            PlayerRank pr = RankService.cacheMapB.get(playerId);
            if (pr == null) {
                pr = new PlayerRank();
            }
            pr.levelData = value;
            final PlayerRank temp = RankService.cacheMapB.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelData = value;
            }
        }
        else {
            PlayerRank pr = RankService.cacheMapC.get(playerId);
            if (pr == null) {
                pr = new PlayerRank();
            }
            pr.levelData = value;
            final PlayerRank temp = RankService.cacheMapC.putIfAbsent(playerId, pr);
            if (temp != null) {
                temp.levelData = value;
            }
        }
    }
    
    private void clearCache(final int type) {
        final Set<Map.Entry<Integer, PlayerRank>> entrySet = RankService.cacheMap.entrySet();
        final Set<Map.Entry<Integer, PlayerRank>> entrySetA = RankService.cacheMapA.entrySet();
        final Set<Map.Entry<Integer, PlayerRank>> entrySetB = RankService.cacheMapB.entrySet();
        final Set<Map.Entry<Integer, PlayerRank>> entrySetC = RankService.cacheMapC.entrySet();
        for (final Map.Entry<Integer, PlayerRank> entry : entrySet) {
            final PlayerRank pr = entry.getValue();
            pr.levelRank = -1;
        }
        for (final Map.Entry<Integer, PlayerRank> entry : entrySetA) {
            final PlayerRank pr = entry.getValue();
            pr.levelRank = -1;
        }
        for (final Map.Entry<Integer, PlayerRank> entry : entrySetB) {
            final PlayerRank pr = entry.getValue();
            pr.levelRank = -1;
        }
        for (final Map.Entry<Integer, PlayerRank> entry : entrySetC) {
            final PlayerRank pr = entry.getValue();
            pr.levelRank = -1;
        }
    }
    
    private boolean isValidate(final PlayerRank pr, final int type) {
        return pr.levelRank != -1;
    }
    
    private boolean isValidateNode(final PlayerRank pr, final int type) {
        return pr.levelData != null;
    }
    
    @Override
    public int getTotalRankNumByForceId(final int forceId) {
        if (forceId == 1) {
            return RankService.levelRankListA.size();
        }
        if (forceId == 2) {
            return RankService.levelRankListB.size();
        }
        if (forceId == 3) {
            return RankService.levelRankListC.size();
        }
        return 200;
    }
    
    @Override
    public int getTotalPostionRankNumByForceId(final int forceId) {
        int result = 0;
        if (forceId == 1) {
            result = RankService.positionRankListA.size();
        }
        else if (forceId == 2) {
            result = RankService.positionRankListB.size();
        }
        else if (forceId == 3) {
            result = RankService.positionRankListC.size();
        }
        else {
            result = 200;
        }
        result = ((result > 200) ? 200 : result);
        return result;
    }
    
    public static void main(final String[] args) throws InterruptedException {
    }
    
    @Override
    public void updatePlayerLv(final int playerId, final int playerLv) {
        final Player player = this.playerDao.read(playerId);
        final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerId);
        final Integer officerId = por.getOfficerId();
        int realOfficerId = 13;
        if (officerId != null) {
            final Halls halls = (Halls)this.hallsCache.get((Object)officerId);
            realOfficerId = halls.getOfficialId();
        }
        final ComparableFactor[] arrays = MultiRankData.orgnizeValue(realOfficerId, 1, playerLv, 0);
        this.firePositionRank(player.getForceId(), new MultiRankData(playerId, arrays));
        this.playerDao.updatePlayerLv(playerId, playerLv);
        final int count1 = this.playerChallengeInfoDao.getByPlayerId(playerId);
        if (count1 > 0) {
            final ComparableFactor[] arrays2 = MultiRankData.orgnizeValue(0, 0, playerLv, 0);
            if (RankService.challegeRanker == null) {
                RankService.errorLog.info("rankService challegeRanker null");
                return;
            }
            RankService.challegeRanker.firePositionRank(player.getForceId(), new MultiRankData(playerId, arrays2));
        }
        final int count2 = this.playerOccupyCityDao.getByPlayerId(playerId);
        if (count2 > 0) {
            final ComparableFactor[] arrays3 = MultiRankData.orgnizeValue(0, 0, playerLv, 0);
            if (RankService.occupyCityRanker == null) {
                RankService.errorLog.info("rankService occupyCityRanker null");
                return;
            }
            RankService.occupyCityRanker.firePositionRank(player.getForceId(), new MultiRankData(playerId, arrays3));
        }
        if (playerLv == 30) {
            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("isFirstLianbing", true));
            final boolean isFirst = CityEventManager.getInstance().addPlayerEvent(playerId, 6);
            if (isFirst) {
                CityEventManager.getInstance().bobaoOnePlayerEvent(playerId, 6);
            }
        }
    }
    
    @Override
    public byte[] getCurRankInfo(final PlayerDto playerDto, final int type) {
        if (type < 0 || type > 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10015);
        }
        final int forceId = playerDto.forceId;
        final List<NationTaskAnd> taskAnds = this.getNationTaskAndsByType(forceId, type);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
        final int nationLv = forceInfo.getForceLv();
        final int curExp = forceInfo.getForceExp();
        final int isWin = (forceInfo.getIswin() == null) ? 0 : forceInfo.getIswin();
        final boolean resultForHJ = this.getCurTaskInfo(taskAnds, doc, playerDto, nationLv);
        if ((RankService.taskInfo != null && RankService.taskInfo.getSerial() == 1) || resultForHJ) {
            doc.createElement("cities", this.getHJCities());
        }
        else {
            doc.createElement("cities", this.getCurCities(forceId));
        }
        doc.createElement("nationLv", nationLv);
        doc.createElement("nationExp", curExp);
        final KindomLv lv = (KindomLv)this.kingdomLvCache.get((Object)nationLv);
        final int maxExp = lv.getExpUpgrade();
        doc.createElement("maxNationExp", maxExp);
        final int officerId = this.playerOfficeRelativeDao.getOfficerId(playerDto.playerId);
        if (officerId == 1 || officerId == 3) {
            doc.createElement("isOfficerProper", true);
        }
        else {
            doc.createElement("isOfficerProper", false);
        }
        if (this.isTimeProper()) {
            doc.createElement("isTimeProper", true);
        }
        else {
            doc.createElement("isTimeProper", false);
        }
        if (maxExp <= curExp && (officerId == 1 || officerId == 3) && forceInfo.getForceLv() < this.kingdomLvCache.maxLv) {
            final long now = System.currentTimeMillis();
            final Date endDate = forceInfo.getEndtime();
            final long endTime = (endDate == null) ? now : endDate.getTime();
            if (endDate == null) {
                doc.createElement("canUpgrade", true);
            }
            else if (endTime + 86400000L - 14400000L <= now) {
                doc.createElement("canUpgrade", true);
            }
            else {
                doc.createElement("canUpgrade", false);
                if (endDate != null && isWin == 1) {
                    doc.createElement("nextUpgradeCd", endTime + 86400000L - 14400000L - now);
                }
            }
        }
        else {
            doc.createElement("canUpgrade", false);
        }
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private String getHJCities() {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < BattleConstant.NT_YELLOW_TURBANS_CLEAR_BATTLE_CITY_PHASE_1.length; ++i) {
            final int cityId = BattleConstant.NT_YELLOW_TURBANS_CLEAR_BATTLE_CITY_PHASE_1[i];
            final City city = CityDataCache.cityArray[cityId];
            if (city != null) {
                sb.append(city.getId()).append(",").append(city.getForceId()).append("#");
            }
        }
        return sb.toString();
    }
    
    private boolean isTimeProper() {
        final Calendar calendar1 = Calendar.getInstance();
        calendar1.set(11, 0);
        calendar1.set(12, 0);
        calendar1.set(13, 0);
        final long time1 = calendar1.getTimeInMillis();
        final Calendar calendar2 = Calendar.getInstance();
        calendar2.set(11, 8);
        calendar2.set(12, 0);
        calendar2.set(13, 0);
        final long time2 = calendar2.getTimeInMillis();
        final long now = System.currentTimeMillis();
        return now >= time1 && now <= time2;
    }
    
    private List<NationTaskAnd> getNationTaskAndsByType(final int forceId, final int type) {
        final List<NationTaskAnd> result = new ArrayList<NationTaskAnd>();
        WorldCity worldCity = null;
        if (type == 0) {
            final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
            final int isWin = (forceInfo == null || forceInfo.getIswin() == null) ? 0 : forceInfo.getIswin();
            final NationTaskAnd taskAnd = new NationTaskAnd();
            taskAnd.setTaskId(0);
            final int cityId = this.getBarbCity(forceId);
            taskAnd.setCityId(cityId);
            worldCity = (WorldCity)this.worldCityCache.get((Object)cityId);
            taskAnd.setCityName(worldCity.getName());
            taskAnd.setEndTime(forceInfo.getEndtime());
            taskAnd.setCanGetReward(0);
            taskAnd.setTaskIntros("");
            taskAnd.setForceId(forceId);
            taskAnd.setIsWin(isWin);
            taskAnd.setState(isWin);
            result.add(taskAnd);
        }
        else {
            NationTask task = null;
            final List<NationTask> tasks = this.nationTaskDao.getListByForce(forceId);
            for (int size = tasks.size(), i = 0; i < size; ++i) {
                if (size == 1) {
                    task = this.nationTaskDao.getByForce(forceId);
                }
                else {
                    task = this.nationTaskDao.getByForceAndTarget(forceId, RankService.chooseCities[i]);
                }
                if (task != null) {
                    final int isWin2 = (task.getIswin() == null) ? 0 : task.getIswin();
                    final NationTaskAnd taskAnd2 = new NationTaskAnd();
                    final int taskId = task.getNationTaskId();
                    final int taskType = getTaskTypeById(taskId);
                    taskAnd2.setTaskId(task.getNationTaskId());
                    taskAnd2.setTaskType(taskType);
                    taskAnd2.setEndTime(task.getEndtime());
                    if (size == 2) {
                        taskAnd2.setCityId(task.getTarget());
                        worldCity = (WorldCity)this.worldCityCache.get((Object)task.getTarget());
                        taskAnd2.setCityName(worldCity.getName());
                        taskAnd2.setAttType(task.getAttType());
                    }
                    else if (taskType == 8) {
                        taskAnd2.setAttType((task.getAttType() == null) ? 0 : ((int)task.getAttType()));
                    }
                    taskAnd2.setTaskIntros("");
                    taskAnd2.setForceId(forceId);
                    taskAnd2.setCanGetReward(0);
                    taskAnd2.setState(isWin2);
                    taskAnd2.setIsWin(isWin2);
                    taskAnd2.setTarget(task.getTarget());
                    taskAnd2.setTaskRelative(task.getTaskRelateInfo());
                    result.add(taskAnd2);
                }
            }
        }
        return result;
    }
    
    private String getCurCities(final int forceId) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < CityDataCache.cityArray.length; ++i) {
            final City city = CityDataCache.cityArray[i];
            if (city != null) {
                if (city.getForceId() == forceId) {
                    sb.append(city.getId()).append("#");
                }
            }
        }
        return sb.toString();
    }
    
    private boolean getCurTaskInfo(final List<NationTaskAnd> taskAnds, final JsonDocument doc, final PlayerDto playerDto, int nationLv) {
        boolean resultForHJCity = false;
        final int timeDivision = getTimeDevision();
        int realState = 0;
        final int PRE = 0;
        final int CURRENT = 1;
        doc.startArray("tasks");
        int isRewarded = 0;
        TaskKillInfo taskKillInfo = null;
        final int playerId = playerDto.playerId;
        KindomLv kindomLv = (KindomLv)this.kingdomLvCache.get((Object)nationLv);
        final long result = this.getNextTime();
        BaseRanker baseRanker = null;
        for (final NationTaskAnd and : taskAnds) {
            int showPre = CURRENT;
            final int taskType = and.getTaskType();
            int taskState = and.getState();
            PlayerExpandInfo playerExpandInfo = null;
            if (taskType == 6 || taskType == 7 || taskType == 8 || taskType == 9 || taskType == 10 || taskType == 12) {
                playerExpandInfo = this.playerExpandInfoDao.read(playerId);
                if (playerExpandInfo != null && playerExpandInfo.getIsrewarded() == 0) {
                    showPre = PRE;
                }
                if (taskType == 6) {
                    showPre = ((and.getIsWin() != 0) ? PRE : showPre);
                }
                else if ((taskType == 7 || taskType == 8 || taskType == 9 || taskType == 10 || taskType == 12) && timeDivision <= 0) {
                    showPre = PRE;
                }
            }
            int killNum = 0;
            if (and.getTaskId() == 0) {
                final BarbariansKillInfo barbariansKillInfo = this.barbariansKillInfoDao.read(playerId);
                isRewarded = ((barbariansKillInfo == null) ? 0 : barbariansKillInfo.getIsrewarder());
                baseRanker = RankService.barbarainRanker;
                killNum = ((barbariansKillInfo == null) ? 0 : barbariansKillInfo.getKillnum());
            }
            else {
                taskKillInfo = this.taskKillInfoDao.getTaskKillInfoByPAndT(playerId, and.getTaskId());
                baseRanker = RankService.nationTaskKillRanker.getRanker(and.getTaskId());
                killNum = this.getTaskKillNum(playerExpandInfo, taskKillInfo, and.getTaskType(), playerId, showPre);
                isRewarded = this.getTaskIsRewarded(playerExpandInfo, taskKillInfo, taskType, playerId, showPre);
            }
            final long now = System.currentTimeMillis();
            final long endDate = (and.getEndTime() == null) ? now : and.getEndTime().getTime();
            final long endTime = endDate - now;
            final int rankNum = this.getNationTaskPersonnelRank(playerExpandInfo, baseRanker, and, playerId, playerDto.forceId, showPre);
            final boolean rankProper = rankNum >= 1;
            doc.startObject();
            doc.createElement("taskId", and.getTaskId());
            doc.createElement("taskType", and.getTaskType());
            doc.createElement("isRewarded", isRewarded);
            if (and.getTaskType() == 1 || and.getTaskType() == 0) {
                doc.createElement("cityName", and.getCityName());
                final City city = CityDataCache.cityArray[and.getCityId()];
                if (city != null) {
                    int forceId = city.getForceId();
                    if (and.getIsWin() != 0) {
                        forceId = this.getTaskAttType(and.getForceId(), and.getTaskId());
                    }
                    doc.createElement("cityForceId", (forceId == -1) ? city.getForceId() : forceId);
                }
                doc.createElement("taskIntro", and.getTaskIntros());
                doc.createElement("cityId", and.getCityId());
                doc.createElement("attType", and.getAttType());
            }
            if (and.getTaskType() == 6) {
                this.getExpandTaskInfo(and, doc, showPre);
            }
            else if (taskType == 7) {
                final int taskSerial = and.getTaskId() % 100 / 10;
                int serial = (showPre == PRE) ? (taskSerial - 1) : taskSerial;
                serial = ((timeDivision <= 0) ? taskSerial : serial);
                taskState = ((showPre == PRE && timeDivision > 0) ? 2 : and.getState());
                doc.createElement("taskState", taskState);
                doc.createElement("serial", serial);
                final InvestInfo nationKill = baseRanker.getKillTotalByForceId(playerDto.forceId);
                doc.createElement("nationKill", (nationKill == null) ? 0L : nationKill.investNum);
                if (showPre == CURRENT && and.getIsWin() == 0) {
                    this.appendSdCityInfo(doc, and.getForceId(), serial);
                }
            }
            else if (taskType == 8) {
                final int taskSerial = and.getTaskId() % 100 / 10;
                int serial = 0;
                taskState = 0;
                int showInfoForce = and.getForceId();
                if (taskSerial > 3) {
                    if (timeDivision <= 0) {
                        serial = taskSerial;
                        taskState = and.getState();
                    }
                    else {
                        serial = ((showPre == PRE) ? (taskSerial - 3) : taskSerial);
                        taskState = ((showPre == PRE) ? 1 : and.getState());
                    }
                    showInfoForce = and.getAttType();
                }
                else {
                    serial = ((showPre == PRE) ? (taskSerial - 1) : taskSerial);
                    serial = ((timeDivision <= 0) ? taskSerial : serial);
                    serial = ((serial <= 0) ? 1 : serial);
                    taskState = ((showPre == PRE) ? 2 : and.getState());
                    taskState = ((timeDivision <= 0) ? and.getIsWin() : taskState);
                }
                doc.createElement("taskState", taskState);
                doc.createElement("serial", serial);
                final InvestInfo nationKill2 = baseRanker.getKillTotalByForceId(playerDto.forceId);
                doc.createElement("nationKill", (nationKill2 == null) ? 0L : nationKill2.investNum);
                if (showPre == CURRENT && and.getIsWin() == 0) {
                    this.appendJsCityInfo(doc, showInfoForce, serial);
                }
            }
            else if (taskType == 9) {
                final int taskSerial = RankService.taskInfo.getSerial();
                int serial = (showPre == PRE) ? (taskSerial - 1) : taskSerial;
                serial = ((timeDivision <= 0) ? taskSerial : serial);
                taskState = ((showPre == PRE && timeDivision > 0) ? 2 : and.getState());
                final int[] array = (serial == 1) ? RankService.taskInfo.getCityBelong() : RankService.taskInfo.getTempleBelong();
                final int[] arrayReal = (serial == 1) ? NationTaskInfo.cityArray1 : NationTaskInfo.cityArray2;
                long nationKill3 = 0L;
                if (taskSerial == 2 && serial == 1) {
                    nationKill3 = RankService.taskInfo.getLastNationKill()[playerDto.forceId - 1];
                    resultForHJCity = true;
                }
                else {
                    final InvestInfo nationKillInfo = baseRanker.getKillTotalByForceId(playerDto.forceId);
                    nationKill3 = ((nationKillInfo == null) ? 0L : nationKillInfo.investNum);
                }
                doc.createElement("nationKill", nationKill3);
                this.appendHJCityInfo(doc, serial, array, arrayReal, and.getForceId());
                doc.createElement("taskState", taskState);
                doc.createElement("serial", serial);
            }
            else if (taskType == 10) {
                final int taskId = and.getTaskId();
                final int taskSerial2 = taskId % 1000 / 100;
                int serial2 = (showPre == PRE) ? (taskSerial2 - 1) : taskSerial2;
                serial2 = ((timeDivision <= 0) ? taskSerial2 : serial2);
                final int forceId2 = and.getForceId();
                taskState = and.getState();
                if (showPre == PRE && timeDivision > 0) {
                    taskState = ((RankService.nationMiracle.serialWinner[serial2 - 1] == forceId2) ? 2 : 1);
                }
                doc.createElement("taskState", taskState);
                doc.createElement("serial", serial2);
                this.appendMiracleInfo(doc, forceId2, taskSerial2 != serial2, serial2);
                realState = taskState;
            }
            else if (taskType == 12) {
                final int taskId = and.getTaskId();
                final int taskSerial2 = taskId % 1000 / 100;
                int serial2 = (showPre == PRE) ? (taskSerial2 - 1) : taskSerial2;
                serial2 = ((timeDivision <= 0) ? taskSerial2 : serial2);
                final int forceId2 = and.getForceId();
                taskState = and.getState();
                if (showPre == PRE && timeDivision > 0) {
                    taskState = ((RankService.festival.serialWinner[serial2 - 1] == forceId2) ? 2 : 1);
                }
                doc.createElement("taskState", taskState);
                doc.createElement("serial", serial2);
                this.appendFestivalInfo(doc, forceId2, taskSerial2 != serial2, serial2);
                realState = taskState;
                doc.createElement("hunger", RankService.festival.getplayerHunger(playerId));
            }
            else if (taskType == 4) {
                this.appendInvestInfo(doc, and, taskKillInfo, playerId);
                doc.createElement("taskState", and.getState());
            }
            else {
                doc.createElement("target", and.getTarget());
                doc.createElement("taskState", and.getState());
            }
            doc.createElement("deadTime", endTime);
            if (and.getIsWin() != 0) {
                doc.createElement("nextTaskTime", result);
            }
            final ForceInfo forceInfo = this.forceInfoDao.read(and.getForceId());
            if (rankProper) {
                int nationRank = this.getNationRank(and.getForceId(), taskType);
                if (taskType == 5) {
                    nationRank = 1;
                }
                int nextRank = this.kingdomTaskRankingCache.getNextRank(rankNum, nationRank, and.getTaskType(), forceInfo.getForceLv());
                if (nextRank != -1) {
                    if (nextRank == 0) {
                        doc.createElement("isFirstClass", true);
                        nextRank = rankNum - 2;
                    }
                    else {
                        --nextRank;
                    }
                    if (rankNum != 1) {
                        final int nextKillNum = this.getNationTaskNextKillNum(baseRanker, playerDto.forceId, nextRank, and.getTaskType(), showPre);
                        doc.createElement("nextKillNum", (nextKillNum - killNum == 0) ? 1 : (nextKillNum - killNum));
                    }
                }
                final int titleLv = this.kingdomTaskRankingCache.getTaskRankingLv(rankNum, taskType);
                doc.createElement("titleLv", this.kingdomTaskRankingCache.getTitleQuality(titleLv, taskType));
                doc.createElement("title", this.kingdomTaskRankingCache.getTitleString(titleLv, taskType));
                doc.createElement("titlePic", this.kingdomTaskRankingCache.getTitlePic(titleLv, taskType));
            }
            if (and.getTaskType() == 0) {
                long totalKillNum = NewBattleManager.getInstance().getBarbarainKillByForceId(playerDto.forceId);
                totalKillNum = ((totalKillNum == Long.MIN_VALUE) ? 0L : totalKillNum);
                doc.createElement("totalKillNum", totalKillNum);
                if (and.getIsWin() == 2) {
                    nationLv = ((nationLv <= 1) ? 1 : (nationLv - 1));
                    kindomLv = (KindomLv)this.kingdomLvCache.get((Object)nationLv);
                    ++nationLv;
                }
                final Barbarain barbarain = (Barbarain)this.barbarainCache.get((Object)kindomLv.getBarbarainLv());
                final long requestNum = barbarain.getTarget();
                doc.createElement("requestKillNum", requestNum);
                doc.createElement("percentage", Math.min((int)(Object)Double.valueOf(totalKillNum / (requestNum * 1.0) * 100.0), 100));
            }
            else if (and.getTaskType() != 1 && taskType != 7 && taskType != 8 && taskType != 9) {
                final List<InvestInfo> list = this.getNationRankList(taskType, baseRanker, showPre, rankNum);
                this.getNationRankInfo(doc, list, taskType);
                if (taskType == 5) {
                    this.getFightingChampionInfo(doc, baseRanker, playerDto.forceId);
                }
                else if (taskType != 6) {
                    final int addExp = this.getAddExp(taskType, 0, and.getTarget(), forceInfo, kindomLv);
                    doc.createElement("nationExp", addExp);
                }
            }
            else if (taskType == 7) {
                final int taskSerial2 = and.getTaskId() % 100 / 10;
                final int maxSerial = this.ktSdmzSCache.getMaxRound();
                final int serial3 = (showPre == PRE) ? ((taskSerial2 == maxSerial && and.getIsWin() != 0) ? taskSerial2 : (taskSerial2 - 1)) : taskSerial2;
                if (serial3 % 2 == 0) {
                    doc.createElement("nationExp", 1);
                }
            }
            else {
                final int addExp2 = this.getAddExp(taskType, and.getAttType(), and.getTarget(), forceInfo, kindomLv);
                doc.createElement("nationExp", addExp2);
            }
            doc.createElement("rankNum", rankNum);
            doc.createElement("killNum", killNum);
            boolean flag = false;
            if (isRewarded == 0) {
                if (and.getTaskId() == 0) {
                    flag = (and.getIsWin() == 2);
                }
                else if (and.getTaskType() == 1) {
                    flag = (and.getIsWin() != 0 && taskKillInfo != null);
                }
                else if (taskType == 6 || taskType == 7 || taskType == 8 || taskType == 9 || taskType == 10 || taskType == 12) {
                    flag = ((showPre == 0) ? (playerExpandInfo != null) : (and.getIsWin() != 0 && taskKillInfo != null));
                }
                else {
                    flag = (and.getIsWin() == 2);
                }
            }
            boolean giveFailFlag = true;
            final Player player = this.playerDao.read(playerId);
            final String rewards = this.getNationTaskPersonalReward(and.getTaskId(), player, nationLv, rankNum, and.getIsWin(), showPre);
            if (taskType != 8) {
                RewardType.rewardDoc(rewards, doc);
            }
            else {
                final int taskSerial3 = and.getTaskId() % 100 / 10;
                if (taskSerial3 <= 3) {
                    RewardType.rewardDoc(rewards, doc);
                }
                else if (showPre == PRE && timeDivision > 0) {
                    RewardType.rewardDoc(rewards, doc);
                }
            }
            boolean hasExtra = false;
            String extra = "";
            if (taskType > 1 && taskType < 5) {
                final InvestInfo nationKill4 = baseRanker.getKillTotalByForceId(playerDto.forceId);
                doc.createElement("nationKill", (nationKill4 == null) ? 0L : nationKill4.investNum);
                final float extraRewards = this.getExtraTaskReward(playerDto.forceId);
                final C e = (C)this.cCache.get((Object)"KindomTask.Extra.e");
                extra = RewardType.rewardByTimees(rewards, e.getValue());
                hasExtra = (extraRewards != 0.0f);
                doc.createElement("hasExtra", hasExtra);
                doc.appendJson(RewardType.rewards(extra, "extraRewards"));
            }
            else {
                extra = this.getVicTaskReward(and.getTaskId(), taskType, kindomLv, showPre);
                boolean showExtra = true;
                boolean hasVReward = false;
                int isWin = and.getIsWin();
                if (taskType == 7 || taskType == 9) {
                    isWin = ((showPre == PRE && timeDivision > 0) ? 2 : and.getState());
                    hasVReward = this.checkExcept(isWin, taskType, taskKillInfo, playerExpandInfo);
                    hasVReward = ((showPre == CURRENT) ? (and.getIsWin() == 2 && hasVReward) : hasVReward);
                    hasVReward = (!StringUtils.isBlank(rewards) && hasVReward);
                }
                else if (taskType == 8) {
                    final int taskSerial4 = and.getTaskId() % 100 / 10;
                    taskState = 0;
                    if (taskSerial4 > 3) {
                        if (showPre == PRE && timeDivision > 0) {
                            taskState = 1;
                            showExtra = false;
                        }
                        else {
                            if (!StringUtils.isBlank(rewards)) {
                                RewardType.rewardDoc(extra, doc);
                            }
                            taskState = and.getState();
                            showExtra = false;
                            if (taskState == 1) {
                                giveFailFlag = false;
                            }
                        }
                    }
                    else {
                        taskState = ((showPre == PRE) ? 2 : and.getState());
                        taskState = ((timeDivision <= 0) ? and.getState() : taskState);
                        if (taskSerial4 <= 1 && isWin != 0) {
                            taskState = and.getState();
                        }
                        if (taskState == 1) {
                            showExtra = false;
                        }
                    }
                    hasVReward = this.checkExcept(taskState, taskType, taskKillInfo, playerExpandInfo);
                    hasVReward = ((showPre == CURRENT) ? (and.getIsWin() == 2 && hasVReward) : hasVReward);
                    hasVReward = (!StringUtils.isBlank(rewards) && hasVReward && taskSerial4 <= 3);
                }
                else if (taskType == 10 || taskType == 12) {
                    isWin = ((showPre == PRE && timeDivision > 0) ? realState : and.getState());
                    hasVReward = this.checkExcept(isWin, taskType, taskKillInfo, playerExpandInfo);
                    hasVReward = ((showPre == CURRENT) ? (and.getIsWin() == 2 && hasVReward) : hasVReward);
                    hasVReward = (!StringUtils.isBlank(rewards) && hasVReward);
                }
                else {
                    hasVReward = this.checkExcept(isWin, taskType, taskKillInfo, playerExpandInfo);
                }
                if (hasVReward) {
                    doc.createElement("hasExtra", true);
                    hasExtra = true;
                }
                if (showExtra) {
                    doc.appendJson(RewardType.rewards(extra, "extraRewards"));
                }
                if (!hasVReward) {
                    extra = "";
                }
            }
            if (taskType == 7) {
                final int taskSerial5 = and.getTaskId() % 100 / 10;
                final int nextSerial = (showPre == PRE) ? taskSerial5 : (taskSerial5 + 1);
                if (nextSerial <= this.ktSdmzSCache.getMaxRound()) {
                    final KtSdmzS next = this.ktSdmzSCache.getKtSdmzSByRound(nextSerial);
                    final int exp = next.getRewardExp();
                    final int iron = next.getRewardIron();
                    final String nextReward = this.orgReward(exp, iron, 0);
                    doc.appendJson(RewardType.rewards(nextReward, "nextRewards"));
                }
            }
            else if (taskType == 8) {
                final int taskSerial5 = and.getTaskId() % 100 / 10;
                int serial4 = 0;
                int id = 0;
                if (taskSerial5 <= 3) {
                    final int arimiesS = RankService.armiesSerial[and.getForceId() - 1];
                    serial4 = ((showPre == PRE) ? arimiesS : (arimiesS + 1));
                    id = this.ktBjCache.getIdByFAndI(forceInfo.getForceLv(), serial4);
                    final Tuple<Integer, Integer> nextTuple = this.ktBjCache.getRewardTuple(id);
                    int exp2 = 0;
                    int iron2 = 0;
                    if (nextTuple != null) {
                        iron2 = nextTuple.left;
                        exp2 = nextTuple.right;
                    }
                    final String nextReward2 = this.orgReward(exp2, iron2, 0);
                    doc.appendJson(RewardType.rewards(nextReward2, "nextRewards"));
                }
            }
            doc.createElement("is14th", RankService.nowDays >= 14 && result == RankService.TASK_TIME1[0].getHour());
            doc.createElement("canGetReward", giveFailFlag && flag && (!StringUtils.isBlank(rewards) || !StringUtils.isBlank(extra)));
            if (taskState == 0) {
                final byte[] bytes = this.individualTaskService.getIndiviInfo(playerDto.playerId, playerDto.forceId);
                if (bytes != null) {
                    doc.appendJson(bytes);
                }
            }
            doc.endObject();
        }
        doc.endArray();
        return resultForHJCity;
    }
    
    private void appendFestivalInfo(final JsonDocument doc, final int playerForceId, final boolean isLast, final int curSerial) {
        try {
            if (RankService.festival == null) {
                return;
            }
            doc.startArray("warInfo");
            for (int i = 1; i <= 3; ++i) {
                final int serial = RankService.festival.curSerial[i - 1];
                doc.startObject();
                doc.createElement("forceId", i);
                int totalPct = 0;
                if (playerForceId == i) {
                    if (isLast) {
                        doc.createElement("serial", serial - 1);
                        doc.createElement("Pct1", 100);
                        doc.createElement("Pct2", 100);
                        doc.createElement("Pct3", 100);
                        doc.createElement("totalPct", 100);
                    }
                    else {
                        doc.createElement("serial", serial);
                        final int soilTotal = RankService.festival.getResourcePct(i, 0);
                        doc.createElement("Pct1", soilTotal);
                        final int stoneTotal = RankService.festival.getResourcePct(i, 1);
                        doc.createElement("Pct2", stoneTotal);
                        final int lumberTotal = RankService.festival.getResourcePct(i, 2);
                        doc.createElement("Pct3", lumberTotal);
                        totalPct = (soilTotal + stoneTotal + lumberTotal) / 3;
                        doc.createElement("totalPct", totalPct);
                    }
                    doc.createElement("player", true);
                }
                else {
                    doc.createElement("serial", serial);
                    doc.createElement("player", false);
                }
                doc.endObject();
            }
            doc.endArray();
            final int serialWinner = RankService.festival.serialWinner[curSerial - 1];
            if (isLast || serialWinner != 0) {
                doc.createElement("force1st", serialWinner);
                doc.createElement("pct1st", 100);
            }
            else {
                final int firstForce = RankService.festival.getTheFirst(curSerial);
                doc.createElement("force1st", firstForce);
                doc.createElement("pct1st", RankService.festival.getTheTotalPct(firstForce));
            }
            doc.startArray("investGolds");
            MultiResult[] playerConsumeArray;
            for (int length = (playerConsumeArray = RankService.festival.getPlayerConsumeArray()).length, j = 0; j < length; ++j) {
                final MultiResult result = playerConsumeArray[j];
                if (result != null) {
                    final String name = (String)result.result1;
                    final int type = (int)result.result2;
                    doc.startObject();
                    doc.createElement("name", name);
                    doc.createElement("gold", 1);
                    doc.createElement("type", type);
                    doc.endObject();
                }
            }
            doc.endArray();
            doc.createElement("yxGold", this.getCostGold(curSerial));
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
        }
    }
    
    private void appendInvestInfo(final JsonDocument doc, final NationTaskAnd and, final TaskKillInfo taskKillInfo, final int playerId) {
        final NationTask task = this.nationTaskDao.read(and.getTaskId());
        if (task == null) {
            return;
        }
        final long total = RankService.nationTaskKillRanker.getRanker1().getKillTotalByForceId(and.getForceId()).investNum;
        final long now = System.currentTimeMillis();
        long cd = (taskKillInfo == null) ? now : taskKillInfo.getUpdatetime();
        cd = ((cd - now < 0L) ? 0L : (cd - now));
        final int killNum = (taskKillInfo == null) ? 0 : taskKillInfo.getKillnum();
        final int taskId = task.getNationTaskId();
        final int event = taskId % 100 / 10;
        final Date date = task.getEndtime();
        final long startTime = date.getTime() - 5400000L;
        final long lastTime = System.currentTimeMillis() - startTime;
        KtTzEv ktTzEv = null;
        boolean isOver = false;
        if (task.getIswin() == 0) {
            ktTzEv = this.ktTzEvCache.getCurSerial(lastTime, event);
        }
        else if (task.getIswin() == 2) {
            ktTzEv = this.ktTzEvCache.getLastSerial(event);
            isOver = true;
        }
        if (ktTzEv == null) {
            return;
        }
        long nextTime = 0L;
        if (!isOver) {
            final KtTzEv nextEv = this.ktTzEvCache.getNextSerial(ktTzEv.getId());
            if (nextEv != null) {
                final int nextMinutes = nextEv.getT();
                nextTime = this.getNextSerialTime(nextMinutes, lastTime);
            }
        }
        final int copper = ktTzEv.getCc();
        int exp = ktTzEv.getEr();
        int couponNum = 0;
        List<StoreHouse> shList = null;
        try {
            shList = this.storeHouseDao.getByItemId(playerId, 401, 5);
        }
        catch (Exception e) {
            RankService.errorLog.error("getPersonalInvestmentInfo exception...playerId:" + playerId, e);
        }
        final List<KtTzEv> list = this.ktTzEvCache.getEvListByEvent(event);
        if (list != null && !list.isEmpty()) {
            doc.startArray("serials");
            for (final KtTzEv cell : list) {
                doc.startObject();
                doc.createElement("serial", cell.getI());
                doc.createElement("target", cell.getCm());
                doc.endObject();
            }
            doc.endArray();
        }
        couponNum = ((shList == null || shList.size() <= 0) ? 0 : shList.get(0).getNum());
        if (couponNum > 0) {
            exp *= 2;
        }
        doc.createElement("goal", task.getTarget());
        doc.createElement("curNum", total);
        doc.createElement("selfInvestment", killNum);
        doc.createElement("percentage", (int)Math.min(100.0, total * 1.0 / task.getTarget() * 100.0));
        doc.createElement("cd", cd);
        doc.createElement("nextTime", nextTime);
        doc.createElement("copper", copper);
        doc.createElement("investExp", exp);
        doc.createElement("cdToUnable", ktTzEv.getCdMax() * 1000L);
        doc.createElement("name", ktTzEv.getName());
        doc.createElement("pic", ktTzEv.getPic());
        doc.createElement("couponNum", couponNum);
        doc.createElement("intro", ktTzEv.getIntro());
        doc.createElement("nationRank", this.getNationRank(and.getForceId(), 6));
        doc.createElement("isLast", this.ktTzEvCache.isLastSerial(ktTzEv, event));
        doc.createElement("curSerial", ktTzEv.getI());
    }
    
    private void appendMiracleInfo(final JsonDocument doc, final int playerForceId, final boolean isLast, final int curSerial) {
        try {
            if (RankService.nationMiracle == null) {
                return;
            }
            doc.startArray("warInfo");
            for (int i = 1; i <= 3; ++i) {
                final int serial = RankService.nationMiracle.curSerial[i - 1];
                doc.startObject();
                doc.createElement("forceId", i);
                int totalPct = 0;
                if (playerForceId == i) {
                    if (isLast) {
                        doc.createElement("serial", serial - 1);
                        doc.createElement("soil", 100);
                        doc.createElement("stone", 100);
                        doc.createElement("lumber", 100);
                        doc.createElement("totalPct", 100);
                    }
                    else {
                        doc.createElement("serial", serial);
                        final int soilTotal = RankService.nationMiracle.getResourcePct(i, 0);
                        doc.createElement("soil", soilTotal);
                        final int stoneTotal = RankService.nationMiracle.getResourcePct(i, 1);
                        doc.createElement("stone", stoneTotal);
                        final int lumberTotal = RankService.nationMiracle.getResourcePct(i, 2);
                        doc.createElement("lumber", lumberTotal);
                        totalPct = (soilTotal + stoneTotal + lumberTotal) / 3;
                        doc.createElement("totalPct", totalPct);
                    }
                    doc.createElement("player", true);
                }
                else {
                    doc.createElement("serial", serial);
                    doc.createElement("player", false);
                }
                doc.endObject();
            }
            doc.endArray();
            final int serialWinner = RankService.nationMiracle.serialWinner[curSerial - 1];
            if (isLast || serialWinner != 0) {
                doc.createElement("force1st", serialWinner);
                doc.createElement("pct1st", 100);
            }
            else {
                final int firstForce = RankService.nationMiracle.getTheFirst(curSerial);
                doc.createElement("force1st", firstForce);
                doc.createElement("pct1st", RankService.nationMiracle.getTheTotalPct(firstForce));
            }
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
        }
    }
    
    private void appendHJCityInfo(final JsonDocument doc, final int serial, final int[] array, final int[] arrayReal, final int playerForceId) {
        try {
            if (RankService.taskInfo == null) {
                return;
            }
            doc.startArray("warInfo");
            for (int i = 0; i < array.length; ++i) {
                doc.startObject();
                final int forceId = array[i];
                doc.createElement("cityId", arrayReal[i]);
                doc.createElement("forceId", forceId);
                final WorldCity worldCity = (WorldCity)this.worldCityCache.get((Object)arrayReal[i]);
                if (worldCity != null) {
                    doc.createElement("cityName", worldCity.getName());
                }
                doc.createElement("rewardType", forceId != 104);
                if (serial == 1) {
                    final ForceInfo forceInfo = this.forceInfoDao.read(playerForceId);
                    if (forceInfo != null) {
                        final Tuple<Integer, Integer> reward = this.ktHjCache.getReward(forceInfo.getForceLv(), serial, 1);
                        if (reward != null) {
                            doc.createElement("exp", reward.left);
                            doc.createElement("iron", reward.right);
                        }
                    }
                }
                doc.endObject();
            }
            if (serial == 2) {
                doc.startObject();
                doc.createElement("cityId", 105);
                final WorldCity worldCity2 = (WorldCity)this.worldCityCache.get((Object)105);
                if (worldCity2 != null) {
                    doc.createElement("cityName", worldCity2.getName());
                }
                doc.createElement("forceId", RankService.taskInfo.getAttackCityForceId());
                doc.endObject();
            }
            doc.endArray();
            if (serial == 2) {
                final int num = RankService.taskInfo.getThreeCityInfo(playerForceId);
                doc.createElement("CityInfo3", num);
                if (num > 0) {
                    final ForceInfo forceInfo2 = this.forceInfoDao.read(playerForceId);
                    if (forceInfo2 != null) {
                        final Tuple<Integer, Integer> reward2 = this.ktHjCache.getPeriod2Reward(forceInfo2.getForceLv());
                        if (reward2 != null) {
                            doc.createElement("exp", reward2.left);
                            doc.createElement("iron", reward2.right);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
        }
    }
    
    private void appendHJCitySimpleInfo(final JsonDocument doc, final int serial, final int[] array, final int[] arrayReal, final int playerForceId) {
        try {
            if (RankService.taskInfo == null) {
                return;
            }
            doc.startArray("warInfo");
            for (int i = 0; i < array.length; ++i) {
                doc.startObject();
                final int forceId = array[i];
                doc.createElement("cityId", arrayReal[i]);
                doc.createElement("forceId", forceId);
                final WorldCity worldCity = (WorldCity)this.worldCityCache.get((Object)arrayReal[i]);
                if (worldCity != null) {
                    doc.createElement("cityName", worldCity.getName());
                }
                doc.createElement("isCenter", false);
                doc.endObject();
            }
            if (serial == 2) {
                doc.startObject();
                doc.createElement("cityId", 105);
                final WorldCity worldCity2 = (WorldCity)this.worldCityCache.get((Object)105);
                if (worldCity2 != null) {
                    doc.createElement("cityName", worldCity2.getName());
                }
                doc.createElement("forceId", RankService.taskInfo.getAttackCityForceId());
                doc.createElement("isCenter", true);
                doc.endObject();
            }
            doc.endArray();
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
        }
    }
    
    private void appendJsCityInfo(final JsonDocument doc, final int forceId, final int serial) {
        doc.startArray("warInfo");
        NationTask task = null;
        for (int i = 1; i <= 3; ++i) {
            task = this.nationTaskDao.getByForce(i);
            if (task != null) {
                final String tasksString = task.getTaskRelateInfo();
                if (!StringUtils.isBlank(tasksString)) {
                    final String[] single = tasksString.split(";");
                    Set<Integer> sdCitySet = null;
                    if (single.length > 1) {
                        sdCitySet = SymbolUtil.stringToSet(single[1], ",");
                    }
                    if (sdCitySet != null) {
                        if (!sdCitySet.isEmpty()) {
                            final StringBuffer sb = new StringBuffer();
                            final StringBuffer nameSb = new StringBuffer();
                            int citySize = 0;
                            for (final Integer city : sdCitySet) {
                                final City c = CityDataCache.cityArray[city];
                                if (c == null) {
                                    continue;
                                }
                                if (c.getForceId() == i && c.getState() == 0) {
                                    continue;
                                }
                                ++citySize;
                                sb.append(city).append("#");
                                final WorldCity nameCity = (WorldCity)this.worldCityCache.get((Object)city);
                                nameSb.append(nameCity.getName()).append("#");
                            }
                            SymbolUtil.removeTheLast(sb);
                            SymbolUtil.removeTheLast(nameSb);
                            doc.startObject();
                            doc.createElement("sdCities", sb);
                            doc.createElement("sdCitySize", citySize);
                            doc.createElement("names", nameSb);
                            doc.createElement("forceId", i);
                            doc.createElement("us", i == forceId);
                            doc.createElement("isJs", task.getAttType());
                            doc.endObject();
                        }
                    }
                }
            }
        }
        doc.endArray();
    }
    
    private void appendSdCityInfo(final JsonDocument doc, final int forceId, final int serial) {
        doc.startArray("warInfo");
        NationTask task = null;
        for (int i = 1; i <= 3; ++i) {
            final Set<Integer> sdCitySet = this.ktSdmzSCache.getCitySet(serial, i);
            if (sdCitySet == null || sdCitySet.isEmpty()) {
                return;
            }
            final StringBuffer sb = new StringBuffer();
            final StringBuffer nameSb = new StringBuffer();
            int citySize = 0;
            for (final Integer city : sdCitySet) {
                final City c = CityDataCache.cityArray[city];
                if (c == null) {
                    continue;
                }
                if (c.getForceId() == i && c.getState() == 0) {
                    continue;
                }
                ++citySize;
                sb.append(city).append("#");
                final WorldCity nameCity = (WorldCity)this.worldCityCache.get((Object)city);
                nameSb.append(nameCity.getName()).append("#");
            }
            SymbolUtil.removeTheLast(sb);
            SymbolUtil.removeTheLast(nameSb);
            doc.startObject();
            doc.createElement("sdCities", sb);
            doc.createElement("sdCitySize", citySize);
            doc.createElement("names", nameSb);
            doc.createElement("forceId", i);
            doc.createElement("us", i == forceId);
            task = this.nationTaskDao.getByForce(i);
            if (task != null) {
                final int taskId = task.getNationTaskId();
                final int taskSerial = taskId % 100 / 10;
                doc.createElement("taskBarSerial", taskSerial);
            }
            doc.endObject();
        }
        doc.endArray();
    }
    
    private int getTaskAttType(final int forceId, final int nationTaskid) {
        final String key = this.attInfos[forceId - 1];
        final String taskInfo = Configuration.getProperty(key);
        if (StringUtils.isBlank(taskInfo)) {
            return -1;
        }
        final String[] taskInfos = taskInfo.split(";");
        if (this.infos.length < 2) {
            return -1;
        }
        for (int i = 0; i < taskInfos.length; ++i) {
            final String infoI = taskInfos[i];
            if (!StringUtils.isBlank(infoI)) {
                final String[] infString = infoI.split(",");
                final int taskId = Integer.parseInt(infString[0]);
                if (taskId == nationTaskid) {
                    return Integer.parseInt(infString[1]);
                }
            }
        }
        return -1;
    }
    
    private long getNextTime() {
        int nextHour = 0;
        final int timeDivison = getTimeDevision();
        if (timeDivison == 1 || timeDivison == 3 || timeDivison == 5) {
            final TimeForNationTask tfnt = (timeDivison == 1) ? RankService.TASK_TIME2[0] : ((timeDivison == 3) ? RankService.TASK_TIME3[0] : RankService.TASK_TIME1[0]);
            nextHour = tfnt.getHour();
        }
        else if (TimeForNationTask.isInTime(RankService.TASK_TIME1[1], RankService.TASK_TIME2[0])) {
            nextHour = RankService.TASK_TIME2[0].getHour();
        }
        else if (TimeForNationTask.isInTime(RankService.TASK_TIME2[1], RankService.TASK_TIME3[0])) {
            nextHour = RankService.TASK_TIME3[0].getHour();
        }
        else {
            nextHour = RankService.TASK_TIME1[0].getHour();
        }
        return nextHour;
    }
    
    private void getFightingChampionInfo(final JsonDocument doc, final BaseRanker baseRanker, final int forceId) {
        if (baseRanker == null) {
            return;
        }
        com.reign.gcld.world.common.RankData rankData = null;
        Player player = null;
        int nationRank = 0;
        for (int i = 0; i < 3; ++i) {
            rankData = baseRanker.getRankNum(0, i);
            if (rankData != null) {
                final int playerId = rankData.playerId;
                player = this.playerDao.read(playerId);
                if (player.getForceId() == forceId) {
                    nationRank = i + 1;
                    break;
                }
            }
        }
        if (nationRank == 0) {
            return;
        }
        final int addExp = this.getAddExp(5, 0, nationRank, this.forceInfoDao.read(forceId), null);
        doc.createElement("playerName", player.getPlayerName());
        doc.createElement("nationRank", nationRank);
        doc.createElement("forceId", forceId);
        doc.createElement("nationExp", addExp);
    }
    
    private void getNationRankInfo(final JsonDocument doc, final List<InvestInfo> list, final int taskType) {
        if (list == null || list.isEmpty()) {
            return;
        }
        final int size = Math.min(list.size(), 3);
        doc.startArray("nationsort");
        for (int i = 1; i <= size; ++i) {
            final InvestInfo info = list.get(i - 1);
            doc.startObject();
            doc.createElement("forceId", info.forceId);
            doc.createElement("value", info.investNum);
            doc.createElement("playerName", info.name);
            doc.endObject();
        }
        doc.endArray();
    }
    
    private List<InvestInfo> getNationRankList(final int taskType, final BaseRanker baseRanker, final int showPre, final int rankNum) {
        final List<InvestInfo> list = new ArrayList<InvestInfo>();
        if (taskType != 6 && taskType != 5) {
            for (int i = 1; i <= 3; ++i) {
                final InvestInfo info = baseRanker.getKillTotalByForceId(i);
                list.add(info);
            }
            Collections.sort(list);
        }
        else if (taskType == 5) {
            if (rankNum < 0 || rankNum > 999) {
                return list;
            }
            for (int i = rankNum - 2; i < rankNum + 1; ++i) {
                final com.reign.gcld.world.common.RankData highRank = baseRanker.getRankNum(0, i);
                if (highRank != null) {
                    final int playerId = highRank.playerId;
                    final Player player = this.playerDao.read(playerId);
                    final InvestInfo info2 = new InvestInfo(player.getForceId());
                    info2.investNum = highRank.value;
                    info2.name = player.getPlayerName();
                    list.add(info2);
                }
            }
        }
        else {
            if (showPre == 1) {
                for (int i = 1; i <= 3; ++i) {
                    final InvestInfo info = new InvestInfo(i);
                    info.investNum = this.cityDataCache.getCityNum(i);
                    list.add(info);
                }
            }
            else {
                for (int i = 1; i <= 3; ++i) {
                    final InvestInfo info = new InvestInfo(i);
                    final String taskInfo = this.getTaskSavedInfo(i);
                    if (!StringUtils.isBlank(taskInfo)) {
                        final String[] values = taskInfo.split("#");
                        int cityNum = 0;
                        if (values.length == 4) {
                            cityNum = Integer.parseInt(values[1]);
                        }
                        info.investNum = cityNum;
                        list.add(info);
                    }
                }
            }
            Collections.sort(list);
        }
        return list;
    }
    
    private int getTaskIsRewarded(final PlayerExpandInfo playerExpandInfo, final TaskKillInfo taskKillInfo, final int taskType, final int playerId, final int showPre) {
        if (taskType != 6 && taskType != 7 && taskType != 8 && taskType != 9 && taskType != 10 && taskType != 12) {
            return (taskKillInfo == null) ? 0 : taskKillInfo.getIsrewarded();
        }
        if (showPre == 0) {
            return (playerExpandInfo == null) ? 0 : playerExpandInfo.getIsrewarded();
        }
        return (taskKillInfo == null) ? 0 : taskKillInfo.getIsrewarded();
    }
    
    private int getTaskKillNum(final PlayerExpandInfo playerExpandInfo, final TaskKillInfo taskKillInfo, final int taskType, final int playerId, final int showPre) {
        if (taskType != 6 && taskType != 7 && taskType != 8 && taskType != 9 && taskType != 10 && taskType != 12) {
            return (taskKillInfo == null) ? 0 : taskKillInfo.getKillnum();
        }
        if (showPre == 0) {
            return (playerExpandInfo == null) ? 0 : playerExpandInfo.getKillnum();
        }
        return (taskKillInfo == null) ? 0 : taskKillInfo.getKillnum();
    }
    
    private void getExpandTaskInfo(final NationTaskAnd and, final JsonDocument doc, final int showPre) {
        int target = 0;
        int curCityNum = 0;
        int winTimes = 0;
        int taskState = 0;
        int serial = 0;
        if (showPre == 1) {
            target = and.getTarget();
            winTimes = and.getAttType();
            curCityNum = this.cityDataCache.getCityNum(and.getForceId());
            taskState = and.getIsWin();
            serial = this.curSerial();
        }
        else {
            final String taskInfo = this.getTaskSavedInfo(and.getForceId());
            if (StringUtils.isBlank(taskInfo)) {
                return;
            }
            final String[] single = taskInfo.split("#");
            if (single.length >= 4) {
                target = Integer.parseInt(single[0]);
                curCityNum = Integer.parseInt(single[1]);
                winTimes = Integer.parseInt(single[2]);
                taskState = Integer.parseInt(single[3]);
            }
            final int taskId = and.getTaskId();
            serial = taskId % 100 / 10;
            if (and.getIsWin() != 0) {
                serial = this.ktKjSCache.getLastSerial().getId();
            }
            else {
                --serial;
            }
        }
        doc.createElement("serial", serial);
        doc.createElement("taskState", taskState);
        doc.createElement("target", target);
        doc.createElement("winTimes", winTimes);
        doc.createElement("curCityNum", curCityNum);
    }
    
    private String getTaskSavedInfo(final int forceId) {
        final String info = this.infos[forceId - 1];
        return Configuration.getProperty(info);
    }
    
    private String getVicTaskReward(final int taskId, final int taskType, final KindomLv kindomLv, final int showPre) {
        try {
            int exp = 0;
            int iron = 0;
            try {
                if (taskType == 6) {
                    final double rate = this.getExpandRewardRate(taskId, 1, showPre);
                    final KtKjTr ktKjTr = this.ktKjRelativeCache.getKtKjTrByKindomLv(kindomLv.getLv());
                    exp = (int)(Object)Double.valueOf(ktKjTr.getRewardExp() * rate);
                    iron = (int)(Object)Double.valueOf(ktKjTr.getRewardIron() * rate);
                }
                else if (taskType == 1) {
                    exp = kindomLv.getRewardChiefExp();
                    iron = kindomLv.getRewardIron();
                }
                else if (taskType == 7) {
                    int serial = taskId % 100 / 10;
                    final int timeDivision = getTimeDevision();
                    if (showPre == 0 && timeDivision > 0) {
                        --serial;
                    }
                    else if (showPre == -1) {
                        final NationTask task = this.nationTaskDao.read(taskId);
                        if (task != null) {
                            final Date endTimeDate = task.getEndtime();
                            final int lastDivision = getTimeDevision(endTimeDate);
                            if (timeDivision < 0 || (lastDivision > 0 && lastDivision == timeDivision)) {
                                --serial;
                            }
                        }
                    }
                    final KtSdmzS s = this.ktSdmzSCache.getKtSdmzSByRound(serial);
                    if (s == null) {
                        RankService.errorLog.error("ktsdmz gained fail....id:" + serial);
                    }
                    else {
                        exp = s.getRewardExp();
                        iron = s.getRewardIron();
                    }
                }
                else if (taskType == 8) {
                    final int forceId = taskId / 100;
                    int curSerial = RankService.armiesSerial[forceId - 1];
                    if (showPre == 0) {
                        curSerial = this.getJSBJLastSerial(forceId);
                    }
                    final Tuple<Integer, Integer> rewards = this.ktBjCache.getRewardTuple(kindomLv.getLv(), curSerial);
                    if (rewards != null) {
                        exp = rewards.right;
                        iron = rewards.left;
                    }
                }
                else if (taskType == 9) {
                    final int forceId = taskId / 100;
                    int serialPar = RankService.taskInfo.getSerial();
                    final int timeDivision2 = getTimeDevision();
                    if (showPre == 0 && timeDivision2 > 0) {
                        --serialPar;
                    }
                    else if (showPre == -1) {
                        serialPar = ((timeDivision2 > 0) ? serialPar : (serialPar - 1));
                    }
                    final int cityNum = RankService.taskInfo.getRewardCityNum(forceId, serialPar);
                    final Tuple<Integer, Integer> resultTuple = this.ktHjCache.getReward(kindomLv.getLv(), serialPar, cityNum);
                    if (resultTuple != null) {
                        exp = resultTuple.left;
                        iron = resultTuple.right;
                    }
                    if (showPre == -1 && serialPar == 2 && RankService.taskInfo.getAttackCityForceId() != forceId) {
                        exp = 0;
                        iron = 0;
                    }
                }
                else if (taskType == 10) {
                    final int forceId = taskId / 1000;
                    int serialPar2;
                    final int taskSerial = serialPar2 = taskId % 1000 / 100;
                    final int timeDivision3 = getTimeDevision();
                    if (showPre == 0 && timeDivision3 > 0) {
                        --serialPar2;
                    }
                    else if (showPre == -1) {
                        final NationTask task2 = this.nationTaskDao.read(taskId);
                        if (task2 != null) {
                            final Date endTimeDate2 = task2.getEndtime();
                            final int lastDivision2 = getTimeDevision(endTimeDate2);
                            if (timeDivision3 < 0 || (lastDivision2 > 0 && lastDivision2 == timeDivision3)) {
                                --serialPar2;
                            }
                        }
                    }
                    final int realSerial = (serialPar2 - 1 < 0) ? 0 : (serialPar2 - 1);
                    if (showPre == -1 && RankService.nationMiracle.serialWinner[realSerial] != forceId) {
                        exp = 0;
                        iron = 0;
                    }
                    else {
                        final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
                        if (forceInfo != null) {
                            final Tuple<Integer, Integer> result = this.ktMrCache.getExtraReward(forceId, forceInfo.getForceLv(), serialPar2);
                            if (result != null) {
                                exp = result.left;
                                iron = result.right;
                            }
                        }
                    }
                }
            }
            catch (Exception e) {
                RankService.errorLog.error("getVicTaskReward fail....taskId:" + taskId + "showPre:" + showPre);
                RankService.errorLog.error(e.getMessage());
                RankService.errorLog.error(this, e);
            }
            if (RankService.hasDoubleReward) {
                exp *= 2;
            }
            return this.orgReward(exp, iron, 0);
        }
        catch (Exception e2) {
            RankService.errorLog.error(this, e2);
            return "";
        }
    }
    
    private int getJSBJLastSerial(final int forceId) {
        final String value = this.infos[forceId - 1];
        final String serial = Configuration.getProperty(value);
        if (StringUtils.isBlank(serial)) {
            return 0;
        }
        return Integer.parseInt(serial);
    }
    
    @Override
    public int getNationTaskNextKillNum(final BaseRanker baseRanker, final int forceId, final int nextRank, final int taskType, final int showPre) {
        if (taskType != 6 && taskType != 7 && taskType != 8 && taskType != 9 && taskType != 10) {
            int parForceId = forceId;
            if (taskType == 5) {
                parForceId = 0;
            }
            final com.reign.gcld.world.common.RankData nextRankData = baseRanker.getRankNum(parForceId, nextRank);
            final int nextKillNum = (nextRankData == null) ? 0 : nextRankData.value;
            return nextKillNum;
        }
        if (showPre == 1) {
            final com.reign.gcld.world.common.RankData nextRankData2 = baseRanker.getRankNum(forceId, nextRank);
            final int nextKillNum2 = (nextRankData2 == null) ? 0 : nextRankData2.value;
            return nextKillNum2;
        }
        final PlayerExpandInfo playerExpandInfo = this.playerExpandInfoDao.getByForceAndRank(forceId, nextRank);
        if (playerExpandInfo == null) {
            return 0;
        }
        return playerExpandInfo.getKillnum();
    }
    
    private int getNationTaskPersonnelRank(final PlayerExpandInfo playerExpandInfo, final BaseRanker baseRanker, final NationTaskAnd and, final int playerId, final int forceId, final int showPre) {
        if (baseRanker == null) {
            return 0;
        }
        final int taskType = and.getTaskType();
        if (taskType == 5) {
            final int rankNum = baseRanker.getRank(1, playerId, 0);
            return rankNum;
        }
        if (taskType != 6 && taskType != 7 && taskType != 8 && taskType != 9 && taskType != 10 && taskType != 12) {
            final int rankNum = baseRanker.getRank(1, playerId, forceId);
            return rankNum;
        }
        if (showPre == 1) {
            final int rankNum = baseRanker.getRank(1, playerId, forceId);
            return rankNum;
        }
        return (playerExpandInfo == null) ? 0 : playerExpandInfo.getRank();
    }
    
    private float getExtraTaskReward(final int forceId) {
        final List<InvestInfo> infos = new ArrayList<InvestInfo>();
        try {
            for (int i = 1; i <= 3; ++i) {
                final InvestInfo info = RankService.nationTaskKillRanker.getRanker1().getKillTotalByForceId(i);
                infos.add(info);
            }
            Collections.sort(infos);
        }
        catch (Exception e) {
            RankService.errorLog.error(e);
            RankService.errorLog.error("rankService .. getExtraTaskReward..");
            return 0.0f;
        }
        if (infos.size() == 0) {
            return 0.0f;
        }
        if (infos.get(0).forceId != forceId) {
            return 0.0f;
        }
        final long num1 = infos.get(0).investNum;
        final long num2 = infos.get(1).investNum;
        final C multi = (C)this.cCache.get((Object)"KindomTask.Extra.Muti");
        final C e2 = (C)this.cCache.get((Object)"KindomTask.Extra.e");
        if (num1 > multi.getValue() * num2) {
            return e2.getValue();
        }
        return 0.0f;
    }
    
    private String orgReward(final int expSum, final int ironSum, final int tokenSum) {
        final StringBuffer sb = new StringBuffer();
        if (expSum > 0) {
            sb.append("chief_exp").append(",").append(expSum);
            if (sb.length() > 0) {
                sb.append(";");
            }
        }
        if (ironSum > 0) {
            sb.append("iron").append(",").append(ironSum);
            if (sb.length() > 0) {
                sb.append(";");
            }
        }
        if (tokenSum > 0) {
            sb.append("recruit_token").append(",").append(tokenSum);
        }
        if (sb.length() > 0) {
            sb.append(";");
        }
        return sb.toString();
    }
    
    @Override
    public byte[] getCurNationTaskSimpleInfo(final int playerId, final List<NationTaskAnd> taskAnds) {
        try {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.startArray("tasks");
            int isRewarded = 0;
            final int timeDivision = getTimeDevision();
            TaskKillInfo taskKillInfo = null;
            for (final NationTaskAnd and : taskAnds) {
                final boolean isKtSdTaskInTime = and.getTaskType() == 7 && timeDivision > 0;
                final BaseRanker ranker = (and.getTaskId() == 0) ? RankService.barbarainRanker : RankService.nationTaskKillRanker.getRanker(and.getTaskId());
                boolean hasReward = true;
                if (and.getTaskType() == 0) {
                    final BarbariansKillInfo barbariansKillInfo = this.barbariansKillInfoDao.read(playerId);
                    isRewarded = ((barbariansKillInfo == null) ? 0 : barbariansKillInfo.getIsrewarder());
                }
                else {
                    taskKillInfo = this.taskKillInfoDao.getTaskKillInfoByPAndT(playerId, and.getTaskId());
                    final int ir = (taskKillInfo == null) ? 0 : taskKillInfo.getIsrewarded();
                    if (and.getTaskType() == 6) {
                        final PlayerExpandInfo playerExpandInfo = this.playerExpandInfoDao.read(playerId);
                        isRewarded = ((playerExpandInfo == null) ? 0 : playerExpandInfo.getIsrewarded());
                    }
                    else {
                        isRewarded = ir;
                        if ((and.getTaskType() == 7 || and.getTaskType() == 8 || and.getTaskType() == 9 || and.getTaskType() == 10 || and.getTaskType() == 12) && timeDivision <= 0) {
                            final PlayerExpandInfo playerExpandInfo = this.playerExpandInfoDao.read(playerId);
                            isRewarded = ((playerExpandInfo == null) ? 1 : playerExpandInfo.getIsrewarded());
                        }
                    }
                }
                if (and.getIsWin() == 1) {
                    if (and.getTaskType() != 1 && and.getTaskType() != 6 && and.getTaskType() != 9 && and.getTaskType() != 10) {
                        continue;
                    }
                    final int rank = (ranker == null) ? 0 : ranker.getRank(1, playerId, and.getForceId());
                    if (rank < 1) {
                        continue;
                    }
                    if (rank > 999) {
                        continue;
                    }
                    hasReward = true;
                }
                else if (and.getIsWin() == 2) {
                    int rank = 0;
                    int bakForceId = and.getForceId();
                    if (and.getTaskType() == 5) {
                        bakForceId = 0;
                    }
                    if (ranker != null) {
                        rank = ranker.getRank(1, playerId, bakForceId);
                    }
                    hasReward = ((rank >= 1 && rank <= 999) || (and.getTaskType() == 1 && taskKillInfo != null));
                    if (and.getTaskType() == 6) {
                        final PlayerExpandInfo playerExpandInfo2 = this.playerExpandInfoDao.read(playerId);
                        hasReward = (playerExpandInfo2 != null);
                    }
                }
                if (isRewarded == 1 && and.getIsWin() != 0 && !isKtSdTaskInTime) {
                    continue;
                }
                final long now = System.currentTimeMillis();
                final long endTime = (and.getEndTime() == null) ? now : and.getEndTime().getTime();
                if (and.getIsWin() != 0 && !hasReward && !isKtSdTaskInTime) {
                    continue;
                }
                doc.startObject();
                if (isKtSdTaskInTime) {
                    hasReward = (hasReward && isRewarded != 1);
                }
                doc.createElement("hasReward", hasReward);
                doc.createElement("taskType", and.getTaskType());
                doc.createElement("target", and.getTarget());
                if (and.getTaskType() == 1 || and.getTaskType() == 0) {
                    doc.createElement("attType", and.getAttType());
                    doc.createElement("cityName", and.getCityName());
                    doc.createElement("cityId", and.getCityId());
                    if (and.getTaskType() == 1) {
                        doc.createElement("pages", 2 - and.getTaskId() % 2);
                    }
                }
                else if (and.getTaskType() == 4) {
                    final int taskId = and.getTaskId();
                    final int event = taskId % 100 / 10;
                    final Date date = and.getEndTime();
                    final long startTime = date.getTime() - 5400000L;
                    final long lastTime = System.currentTimeMillis() - startTime;
                    final KtTzEv ktTzEv = this.ktTzEvCache.getCurSerial(lastTime, event);
                    KtTzEv nextEv = null;
                    long nextTime = 0L;
                    if (ktTzEv != null) {
                        nextEv = this.ktTzEvCache.getNextSerial(ktTzEv.getId());
                        if (nextEv != null) {
                            final int nextMinutes = nextEv.getT();
                            nextTime = this.getNextSerialTime(nextMinutes, lastTime);
                        }
                    }
                    doc.createElement("nextTime", nextTime);
                }
                else if (and.getTaskType() == 3) {
                    final long startTime2 = and.getEndTime().getTime() - 5400000L;
                    final Tuple<Integer, Long> nextTime2 = this.ktMzSCache.getNextInvadeInfo(startTime2);
                    if (nextTime2 != null) {
                        doc.createElement("curSerial", nextTime2.left);
                        doc.createElement("nextTime", nextTime2.right);
                    }
                }
                else if (and.getTaskType() == 6) {
                    doc.createElement("curCityNum", this.cityDataCache.getCityNum(and.getForceId()));
                }
                else if (and.getTaskType() == 8) {
                    final int taskId = and.getTaskId();
                    final int serial = taskId % 100 / 10;
                    if (serial <= 3) {
                        final long startTime3 = this.getStartTime();
                        final Tuple<Integer, Long> nextTime3 = this.ktBjCache.getNextInvadeInfo(startTime3);
                        if (nextTime3 != null) {
                            doc.createElement("curSerial", nextTime3.left);
                            doc.createElement("nextTime", nextTime3.right);
                        }
                        this.appendJsbjDoubleFlagInfo(and, doc);
                    }
                    doc.createElement("serial", serial);
                }
                else if (and.getTaskType() == 9) {
                    final int taskId = and.getTaskId();
                    final int serial = taskId % 100 / 10;
                    doc.createElement("serial", serial);
                    final int[] array = (serial == 1) ? RankService.taskInfo.getCityBelong() : RankService.taskInfo.getTempleBelong();
                    final int[] arrayReal = (serial == 1) ? NationTaskInfo.cityArray1 : NationTaskInfo.cityArray2;
                    this.appendHJCitySimpleInfo(doc, serial, array, arrayReal, and.getForceId());
                }
                else if (and.getTaskType() == 10) {
                    final int taskId = and.getTaskId();
                    final int serial = taskId % 1000 / 10;
                    doc.createElement("serial", serial);
                    this.appendMiralWorkerInfo(doc);
                }
                else if (and.getTaskType() == 12) {
                    final int taskId = and.getTaskId();
                    final int serial = taskId % 1000 / 100;
                    doc.createElement("serial", serial);
                    this.appendFestivalWorkerInfo(doc);
                }
                doc.createElement("taskState", and.getState());
                if (and.getTaskType() == 0) {
                    final int forceId = and.getForceId();
                    long killNum = NewBattleManager.getInstance().getBarbarainKillByForceId(forceId);
                    killNum = ((killNum == Long.MIN_VALUE) ? 0L : killNum);
                    final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
                    KindomLv lv = null;
                    if (and.getIsWin() == 2) {
                        lv = (KindomLv)this.kingdomLvCache.get((Object)(forceInfo.getForceLv() - 1));
                    }
                    else {
                        lv = (KindomLv)this.kingdomLvCache.get((Object)forceInfo.getForceLv());
                    }
                    final Barbarain barbarain = (Barbarain)this.barbarainCache.get((Object)lv.getBarbarainLv());
                    final long requestNum = barbarain.getTarget();
                    doc.createElement("killNum", killNum);
                    doc.createElement("requestKillNum", requestNum);
                    doc.createElement("percentage", Math.min((int)(Object)Double.valueOf(killNum / (requestNum * 1.0) * 100.0), 100));
                    final int armyId = barbarain.getArmyId();
                    final General general = (General)this.generalCache.get((Object)armyId);
                    doc.createElement("pic", (general == null) ? "" : general.getPic());
                }
                doc.createElement("endTime", endTime - now);
                doc.createElement("nowDate", new Date());
                doc.createElement("endTimeDate", and.getEndTime());
                doc.endObject();
            }
            doc.endArray();
            doc.endObject();
            return doc.toByte();
        }
        catch (Exception e) {
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
            final JsonDocument doc2 = new JsonDocument();
            doc2.startObject();
            doc2.endObject();
            return doc2.toByte();
        }
    }
    
    private void appendFestivalWorkerInfo(final JsonDocument doc) {
        try {
            if (RankService.festival == null || RankService.festival.map == null || RankService.festival.miracleState != 0) {
                return;
            }
            doc.startArray("workerInfo");
            for (final NationFestival.SupplyWorker worker : RankService.festival.map.values()) {
                if (worker != null && worker.workerState == 0) {
                    if (worker.curPosition >= worker.path.length - 1) {
                        continue;
                    }
                    doc.startObject();
                    NationFestival.getWorkerMarchingInfo(doc, this.dataGetter, worker);
                    doc.endObject();
                }
            }
            doc.endArray();
            if (RankService.festival.miracleState == 0) {
                RankService.festival.getNationMiracleInfo(doc, 0);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
        }
    }
    
    private void appendMiralWorkerInfo(final JsonDocument doc) {
        try {
            if (RankService.nationMiracle == null || RankService.nationMiracle.map == null || RankService.nationMiracle.miracleState != 0) {
                return;
            }
            doc.startArray("workerInfo");
            for (final NationMiracle.SupplyWorker worker : RankService.nationMiracle.map.values()) {
                if (worker != null && worker.workerState == 0) {
                    if (worker.curPosition >= worker.path.length - 1) {
                        continue;
                    }
                    doc.startObject();
                    NationMiracle.getWorkerMarchingInfo(doc, this.dataGetter, worker);
                    doc.endObject();
                }
            }
            doc.endArray();
            if (RankService.nationMiracle.miracleState == 0) {
                RankService.nationMiracle.getNationMiracleInfo(doc, 0);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
        }
    }
    
    private void appendJsbjDoubleFlagInfo(final NationTaskAnd and, final JsonDocument doc) {
        try {
            if (and.getIsWin() != 0 || StringUtils.isBlank(and.getTaskRelative())) {
                return;
            }
            final String[] single = and.getTaskRelative().split(";");
            if (single.length < 2) {
                return;
            }
            doc.createElement("forceId", and.getForceId());
            doc.createElement("cities", single[1]);
        }
        catch (Exception e) {
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
        }
    }
    
    @Override
    public List<NationTaskAnd> getNationTaskAnds(final int forceId) {
        final List<NationTaskAnd> result = new ArrayList<NationTaskAnd>();
        final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
        int isWin = (forceInfo == null || forceInfo.getIswin() == null) ? 0 : forceInfo.getIswin();
        WorldCity worldCity = null;
        if (this.hasBarTask(forceId)) {
            final NationTaskAnd taskAnd = new NationTaskAnd();
            taskAnd.setTaskId(0);
            final int cityId = this.getBarbCity(forceId);
            taskAnd.setCityId(cityId);
            worldCity = (WorldCity)this.worldCityCache.get((Object)cityId);
            if (worldCity == null) {
                RankService.errorLog.error("BarCity Not Exist!!!!  forceId:" + forceId);
            }
            else {
                taskAnd.setCityName(worldCity.getName());
            }
            taskAnd.setEndTime(forceInfo.getEndtime());
            taskAnd.setState(isWin);
            taskAnd.setTaskType(0);
            taskAnd.setCanGetReward(0);
            taskAnd.setForceId(forceId);
            taskAnd.setTaskIntros("");
            taskAnd.setIsWin(isWin);
            result.add(taskAnd);
        }
        NationTask task = null;
        final List<NationTask> tasks = this.nationTaskDao.getListByForce(forceId);
        for (int size = tasks.size(), i = 0; i < size; ++i) {
            if (size == 1) {
                task = tasks.get(0);
            }
            else {
                task = this.nationTaskDao.getByForceAndTarget(forceId, RankService.chooseCities[i]);
            }
            if (task != null) {
                isWin = ((task.getIswin() == null) ? 0 : task.getIswin());
                final NationTaskAnd taskAnd2 = new NationTaskAnd();
                final int taskId = task.getNationTaskId();
                final int taskType = getTaskTypeById(taskId);
                taskAnd2.setTaskType(taskType);
                taskAnd2.setTaskId(taskId);
                taskAnd2.setCityId(task.getTarget());
                taskAnd2.setEndTime(task.getEndtime());
                if (taskType == 1) {
                    worldCity = (WorldCity)this.worldCityCache.get((Object)task.getTarget());
                    if (worldCity == null) {
                        RankService.errorLog.equals("Nation Task City is Not Exist!!!! cityId:" + task.getTarget());
                    }
                    else {
                        taskAnd2.setCityName(worldCity.getName());
                    }
                    taskAnd2.setAttType(task.getAttType());
                }
                taskAnd2.setTaskIntros("");
                taskAnd2.setForceId(forceId);
                taskAnd2.setCanGetReward((isWin == 2) ? 1 : 0);
                taskAnd2.setIsWin(isWin);
                taskAnd2.setState(isWin);
                taskAnd2.setTarget(task.getTarget());
                taskAnd2.setTaskRelative(task.getTaskRelateInfo());
                result.add(taskAnd2);
            }
        }
        return result;
    }
    
    @Override
    public int getBarbCity(final int forceId) {
        if (forceId != 0) {
            return RankService.barCity[forceId - 1];
        }
        return 0;
    }
    
    public void pushNationTaskInfo(final PushTaskOverInfo info) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("lv", info.lv);
        doc.createElement("state", info.state);
        doc.createElement("pages", 2 - info.page);
        doc.createElement("taskType", info.taskType);
        doc.createElement("rank", info.rank);
        doc.createElement("serial", info.serial);
        doc.createElement("city", info.city);
        doc.createElement("attType", info.attType);
        doc.endObject();
        this.pushToForce(info.force, PushCommand.PUSH_NATION_TASK_STATE_CHANGE, doc.toByte());
    }
    
    public void pushBarTaskInfo(final int forceId, final int lv, final int state) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("foceId", forceId);
        doc.createElement("lv", lv);
        doc.createElement("state", state);
        doc.endObject();
        this.pushToForce(forceId, PushCommand.PUSH_BAR_TASK_STATE_CHANGE, doc.toByte());
    }
    
    public void pushToForce(final int forceId, final PushCommand pushCommand, final byte[] doc) {
        final Group group = GroupManager.getInstance().getGroup(String.valueOf(ChatType.COUNTRY.toString()) + forceId);
        if (group != null) {
            final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, pushCommand.getModule(), doc));
            group.notify(WrapperUtil.wrapper(pushCommand.getCommand(), 0, bytes));
        }
    }
    
    public static void pushToAll(final PushCommand pushCommand, final byte[] doc, final Group group) {
        if (group == null) {
            return;
        }
        final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, pushCommand.getModule(), doc));
        group.notify(WrapperUtil.wrapper(pushCommand.getCommand(), 0, bytes));
    }
    
    @Override
    public void startNationTasks() {
        final long start = System.currentTimeMillis();
        final boolean isInKf = this.checkIsInKf();
        if (isInKf) {
            return;
        }
        final String startServer = Configuration.getProperty("gcld.server.time");
        if (this.playerService.isSecond(startServer) > 0L) {
            return;
        }
        RankService.errorLog.error("before time...");
        final int timeDivision = getTimeDevision();
        RankService.errorLog.error("before time...timeDivision.." + timeDivision);
        if (timeDivision == 1 || timeDivision == 3 || timeDivision == 5) {
            int taskType = this.getTaskType(timeDivision, startServer);
            this.isYuanXiaoFestival();
            if (RankService.isYuanXiao) {
                taskType = 12;
            }
            if (taskType == 12) {
                RankService.isYuanXiao = true;
            }
            this.checkIsYuanXiaoNight(RankService.isYuanXiao, taskType);
            final long timeToCount1 = System.currentTimeMillis();
            this.nationNextTaskStart();
            final long timeToCount2 = System.currentTimeMillis();
            RankService.errorLog.error("nationNextTaskStart time:" + (timeToCount2 - timeToCount1));
            final boolean isSuc = this.startNationTaskByType(taskType, timeDivision);
            if (!isSuc) {
                taskType = 4;
                RankService.isYuanXiao = false;
                RankService.isYuanXiaoNight = false;
                this.startNationTaskByType(taskType, timeDivision);
            }
            final long timeToCount3 = System.currentTimeMillis();
            RankService.errorLog.error("startNationTaskByType time:" + (timeToCount3 - timeToCount2));
            if (taskType == 4) {
                this.timerBattleService.triggerAddTicketArmyTask();
            }
            RankService.nowDays = this.nowDays();
            RankService.hasDoubleReward = this.hasDoubleReward();
            RankService.errorLog.error("start nation task : taskType" + taskType + "time " + new Date());
            final long timeToCount4 = System.currentTimeMillis();
            RankService.errorLog.error("others time:" + (timeToCount4 - timeToCount3));
        }
        RankService.timerLog.info(LogUtil.formatThreadLog("RankService", "startNationTasks", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Override
    public void checkIsYuanXiaoNight(final boolean isYuanXiao, final int taskType) {
        final int timeDivision = getTimeDevision();
        if (isYuanXiao && taskType == 12 && timeDivision == 5) {
            RankService.isYuanXiaoNight = true;
        }
        else {
            RankService.isYuanXiaoNight = false;
        }
    }
    
    @Override
    public boolean startNationTaskByType(final int taskType, final int timeDivision) {
        Date endtime = this.getEndTime(timeDivision);
        final long nextLegionTime = endtime.getTime() - 5400000L + this.ktBjCache.getLegionStartMinutes() * 60000L - 10000L;
        final InMemmoryIndivTaskManager inMemmoryIndivTaskManager = InMemmoryIndivTaskManager.getInstance();
        if (taskType == 1) {
            final int defForceId = this.cityDataCache.getStrongestForce();
            RankService.chooseCities = this.chooseCities(defForceId);
            if (RankService.chooseCities[0] == 0 || RankService.chooseCities[1] == 0) {
                return false;
            }
            ForceInfo forceInfo = null;
            for (int i = 1; i <= 3; ++i) {
                final StringBuffer sBuffer = new StringBuffer();
                for (int j = 0; j < 2; ++j) {
                    if (RankService.chooseCities[j] != 0) {
                        final City city = CityDataCache.cityArray[RankService.chooseCities[j]];
                        if (city != null) {
                            final NationTask task = new NationTask();
                            task.setForceid(i);
                            task.setTarget(RankService.chooseCities[j]);
                            task.setEndtime(endtime);
                            task.setAttType((city.getForceId() == i) ? 1 : 0);
                            task.setIswin(0);
                            final int taskId = this.computeTaskId(timeDivision, i, j);
                            task.setNationTaskId(taskId);
                            this.nationTaskDao.create(task);
                            sBuffer.append(taskId).append(",").append(city.getForceId()).append(";");
                        }
                    }
                }
                SymbolUtil.removeTheLast(sBuffer);
                this.saveAttDefInfo(sBuffer.toString(), i);
                RankService.needShowNextInvade[i - 1] = false;
                forceInfo = this.forceInfoDao.read(i);
                inMemmoryIndivTaskManager.initDefaultTasks(i, forceInfo.getForceLv(), taskType);
            }
        }
        else {
            RankService.chooseCities[0] = 0;
            RankService.chooseCities[1] = 0;
            final int barSerial = 1;
            final int serial = this.getCurSerial();
            final KtTzEv ktTzEv = this.ktTzEvCache.getRandomEvent();
            List<KtMrTarget> targets = null;
            ForceInfo forceInfo2 = null;
            for (int k = 1; k <= 3; ++k) {
                final NationTask task = new NationTask();
                forceInfo2 = this.forceInfoDao.read(k);
                task.setForceid(k);
                if (taskType == 4) {
                    final int s = ktTzEv.getS();
                    final int target = ktTzEv.getCm();
                    task.setTarget(target);
                    task.setNationTaskId(k * 100 + taskType + s * 10);
                }
                else {
                    int target2 = 0;
                    task.setNationTaskId(k * 100 + taskType);
                    if (taskType == 6) {
                        final int winTimes = this.getWinTimes(task.getForceid(), serial);
                        final KtKjT ktKjT = this.ktKjRelativeCache.getKtKjTByWinTimes(winTimes);
                        target2 = this.cityDataCache.getCityNum(k) + ktKjT.getTc();
                        target2 = ((target2 >= 246) ? 246 : target2);
                        endtime = this.getExpandEndTime(this.getStartTime(timeDivision));
                        task.setAttType(winTimes);
                        task.setNationTaskId(k * 100 + taskType + serial * 10);
                    }
                    else if (taskType == 7) {
                        task.setNationTaskId(k * 100 + taskType + barSerial * 10);
                        task.setAttType(0);
                        final String name = (k == 1) ? LocalMessages.T_FORCE_BEIDI : ((k == 2) ? LocalMessages.T_FORCE_XIRONG : LocalMessages.T_FORCE_DONGYI);
                        final String msg = MessageFormatter.format(LocalMessages.SD_HAS_COMED, new Object[] { name });
                        this.dataGetter.getChatService().sendBigNotice("COUNTRY", new PlayerDto(0, k), msg, null);
                    }
                    else if (taskType == 8) {
                        task.setNationTaskId(k * 100 + taskType + barSerial * 10);
                        task.setAttType(0);
                        endtime = this.getJsbjEndTime(barSerial, timeDivision);
                        RankService.taskRelative[0] = 0;
                        RankService.taskRelative[1] = 0;
                        RankService.taskRelative[2] = 0;
                        RankService.nextLegion[k - 1] = nextLegionTime;
                        RankService.errorLog.error(String.valueOf(k) + " init nextLegion:" + new Date(nextLegionTime));
                    }
                    else if (taskType == 9) {
                        task.setNationTaskId(k * 100 + taskType + 10);
                        task.setTaskRelateInfo("");
                    }
                    else if (taskType == 10) {
                        task.setNationTaskId(k * 1000 + 100 + taskType);
                        task.setTaskRelateInfo("");
                        if (targets == null) {
                            targets = new ArrayList<KtMrTarget>();
                        }
                        final KtMrTarget one = this.ktMrCache.getTargetByNationAndLv(k, forceInfo2.getForceLv(), 1);
                        targets.add(one);
                    }
                    else if (taskType == 12) {
                        int taskId2 = k * 1000 + 100 + taskType;
                        if (!RankService.isYuanXiaoNight) {
                            task.setNationTaskId(taskId2);
                            task.setTaskRelateInfo("");
                            if (targets == null) {
                                targets = new ArrayList<KtMrTarget>();
                            }
                            final KtMrTarget one2 = this.ktNfCache.getTargetByNationAndLv(k, forceInfo2.getForceLv(), 1);
                            targets.add(one2);
                        }
                        else {
                            final int n;
                            final int curSerial = n = RankService.festival.curSerial[k - 1];
                            RankService.festival.getClass();
                            boolean b = false;
                            Label_1109: {
                                if (n >= 5) {
                                    final int n2 = curSerial;
                                    RankService.festival.getClass();
                                    if (n2 != 5 || RankService.festival.nationResourceFull(k)) {
                                        b = false;
                                        break Label_1109;
                                    }
                                }
                                b = true;
                            }
                            final boolean flag = b;
                            if (flag) {
                                taskId2 = curSerial * 100 + k * 1000 + taskType;
                                final int res = this.nationTaskDao.updateIsWinAndEndTime(taskId2, 0, endtime);
                                if (res <= 0) {
                                    RankService.errorLog.error("update YuanXiaoNight task error" + taskId2);
                                }
                                RankService.festival.setNationTaskIsOver(k, false);
                            }
                        }
                    }
                    task.setTarget(target2);
                }
                task.setEndtime(endtime);
                task.setIswin(0);
                if (!RankService.isYuanXiaoNight) {
                    this.nationTaskDao.create(task);
                }
                if (taskType == 7) {
                    this.timerBattleService.addRoundSaoDangManZu(k, barSerial);
                    RankService.needShowNextInvade[k - 1] = false;
                }
                else if (taskType == 8) {
                    this.doClearBianJiangManzu(k);
                    RankService.needShowNextInvade[k - 1] = true;
                }
                else {
                    RankService.needShowNextInvade[k - 1] = false;
                }
                inMemmoryIndivTaskManager.initDefaultTasks(k, forceInfo2.getForceLv(), taskType);
            }
            this.startThread(taskType);
            this.startMiracleThread(taskType, targets);
        }
        this.initNationTaskRanker();
        if (taskType == 9) {
            if (RankService.taskInfo == null) {
                RankService.taskInfo = new NationTaskInfo();
            }
            RankService.taskInfo.reload(endtime, this.getStartTime(timeDivision));
            this.chatService.sendSystemChat("GLOBAL", 0, 0, LocalMessages.NATION_TASK_HJ_START, null);
        }
        else {
            RankService.taskInfo = null;
        }
        if (taskType == 9) {
            this.battleService.clearBattleForNTYellowTurbans(1);
            RankService.taskInfo.setState(1);
        }
        inMemmoryIndivTaskManager.initialOver();
        for (int l = 1; l <= 3; ++l) {
            this.pushCurNationTaskInfoToPlayer(l);
        }
        return true;
    }
    
    private void startMiracleThread(final int taskType, final List<KtMrTarget> targets) {
        try {
            if (RankService.nationMiracle != null) {
                if (!RankService.nationMiracle.executor.isShutdown()) {
                    RankService.nationMiracle.executor.shutdownNow();
                    RankService.nationMiracle.map.clear();
                }
                RankService.nationMiracle = null;
            }
            if (RankService.festival != null && !RankService.isYuanXiaoNight) {
                if (!RankService.festival.executor.isShutdown()) {
                    RankService.festival.executor.shutdownNow();
                    RankService.festival.map.clear();
                }
                RankService.festival = null;
            }
            if (taskType == 10) {
                RankService.festival = null;
                final int interVal = this.ktMrCache.getInterValTime();
                final Date startDate = this.getStartTime(getTimeDevision());
                final List<KtMrTroop> troops = this.ktMrCache.getTroopList();
                final List<Integer> defaultTimeList = this.ktMrCache.getDefaultTimeList();
                (RankService.nationMiracle = new NationMiracle(startDate, interVal, troops, this.dataGetter, targets, defaultTimeList)).start();
            }
            else if (taskType == 12) {
                RankService.nationMiracle = null;
                final int interVal = this.ktNfCache.getInterValTime();
                final Date startDate = this.getStartTime(getTimeDevision());
                final List<KtMrTroop> troops = this.ktNfCache.getTroopList();
                final List<Integer> defaultTimeList = this.ktNfCache.getDefaultTimeList();
                if (!RankService.isYuanXiaoNight) {
                    RankService.festival = new NationFestival(startDate, interVal, troops, this.dataGetter, targets, defaultTimeList);
                    if (!RankService.festival.isAlive()) {
                        RankService.festival.start();
                    }
                }
                else {
                    final NationFestival temp = new NationFestival(new Date(), interVal, troops, this.dataGetter, targets, defaultTimeList);
                    RankService.festival = temp.copyAttributes(RankService.festival);
                    if (!RankService.festival.isAlive()) {
                        RankService.festival.start();
                    }
                }
            }
            else {
                RankService.nationMiracle = null;
                RankService.festival = null;
            }
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
        }
    }
    
    private void startThread(final int taskType) {
        if (taskType == 3) {
            this.timerBattleService.BarbarainInvade();
        }
        else if (taskType == 7 || taskType == 8 || taskType == 9 || taskType == 10 || taskType == 12) {
            (this.thread = new SdThreadCheck()).start();
        }
    }
    
    private void doClearBianJiangManzu(final int forceId) {
        try {
            final Set<Integer> cities = this.ktBjCache.getCitiesByForceId(forceId);
            if (cities == null || cities.isEmpty()) {
                return;
            }
            Set<Integer> lastCities = null;
            for (final Integer cityId : cities) {
                final City city = CityDataCache.cityArray[cityId];
                if (city == null) {
                    continue;
                }
                final int cityForceId = city.getForceId();
                if (cityForceId >= 0 && cityForceId <= 3) {
                    continue;
                }
                if (lastCities == null) {
                    lastCities = new HashSet<Integer>();
                }
                lastCities.add(cityId);
            }
            if (lastCities != null && !lastCities.isEmpty()) {
                this.timerBattleService.removeManZuBeforeNationTask(forceId, lastCities);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("doClearBianJiangManzu");
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
        }
    }
    
    private Date getJsbjEndTime(final int barSerial, final int timeDivision) {
        Date startDate = this.getStartTime(timeDivision);
        if (startDate == null) {
            RankService.errorLog.error("getJsbjEndTime fail....barSerial:" + barSerial + "timeDivision:" + timeDivision);
            startDate = new Date();
        }
        final long result = startDate.getTime() + barSerial * 60000L * 30L;
        final Date resultDate = new Date(result);
        return resultDate;
    }
    
    private void saveAttDefInfo(final String string, final int forceId) {
        Configuration.saveProperties(this.attInfos[forceId - 1], string, "serverstate.properties");
    }
    
    private int getWinTimes(final int forceId, final int serial) {
        int winTimes = 0;
        if (serial > 1) {
            winTimes = this.getWinTimesBySavedInfoAndIsWin(forceId);
            return winTimes;
        }
        return winTimes;
    }
    
    private Date getExpandEndTime(final Date startTime) {
        final long now = System.currentTimeMillis();
        final long start = startTime.getTime();
        final long lastTime = now - start;
        final Date resultDate = new Date();
        final KtKjS ktKjS = this.ktKjSCache.getCurSerial(lastTime);
        if (ktKjS == null) {
            RankService.errorLog.equals("wrong time" + new Date());
            return null;
        }
        final long end = start + 5400000L;
        resultDate.setTime(this.ktKjSCache.getEndTime(ktKjS, end));
        return resultDate;
    }
    
    private int getTaskType(final int timeDivision, final String startServer) {
        final long startLong = Long.parseLong(startServer);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(startLong);
        final int startYear = calendar.get(1);
        final int startDay = calendar.get(6);
        final long nowLong = System.currentTimeMillis();
        calendar.setTimeInMillis(nowLong);
        final int nowYear = calendar.get(1);
        final int nowDay = calendar.get(6);
        final int day = (nowYear - startYear) * 365 + (nowDay - startDay);
        final int composeId = day * 100 + (timeDivision + 1) / 2;
        final KtInit ktInit = (KtInit)this.ktInitCache.get((Object)composeId);
        if (ktInit != null) {
            final KtType ktType = (KtType)this.ktTypeCache.get((Object)ktInit.getKtType());
            return ktType.getId();
        }
        final int taskType = this.getFromDatabase(composeId);
        if (taskType != 0) {
            return taskType;
        }
        return this.ktTypeCache.getKtType();
    }
    
    private void isYuanXiaoFestival() {
        final String time = Configuration.getProperty("gcld.task.festival.time");
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final Date date = new Date();
        final String value = format.format(date);
        boolean result = false;
        if (!StringUtils.isBlank(time)) {
            final String[] single = time.split(",");
            String[] array;
            for (int length = (array = single).length, i = 0; i < length; ++i) {
                final String t = array[i];
                if (!StringUtils.isBlank(t)) {
                    if (t.equalsIgnoreCase(value)) {
                        result = true;
                        break;
                    }
                }
            }
        }
        else {
            result = value.equalsIgnoreCase(RankService.FESTIVAL_TIME);
        }
        final int timeDivision = getTimeDevision();
        RankService.isYuanXiao = (result && timeDivision >= 3);
    }
    
    @Override
    public int getFromDatabase(final int composeId) {
        final TaskInit init = this.taskInitDao.read(composeId);
        if (init != null) {
            return init.getType();
        }
        this.taskInitDao.deleteAlls();
        final int day = composeId / 100;
        final List<TaskInit> list = this.ktSCache.getInits(day);
        if (list == null || list.isEmpty()) {
            RankService.errorLog.error("getFromDatabase\tlist is null or empty");
            return 0;
        }
        this.taskInitDao.batchCreate(list);
        return list.get(0).getType();
    }
    
    @Override
    public byte[] startNationTask(final PlayerDto playerDto, final int taskType) {
        if (taskType != 0) {
            if (taskType != 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            if (this.isTimeProper()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.TIME_IS_NOT_PROPER);
            }
            final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerDto.playerId);
            final int officerId = (por == null) ? 37 : por.getOfficerId();
            if (officerId != 1 && officerId != 3) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.ONLY_SPECIFIC_OFFICER_CAN_USE);
            }
            final int forceId = playerDto.forceId;
            synchronized (Constants.lock) {
                final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
                final long now = System.currentTimeMillis();
                final long endTime = (forceInfo.getEndtime() == null) ? 0L : forceInfo.getEndtime().getTime();
                if (endTime > now) {
                    // monitorexit(Constants.lock)
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.NATION_TASK_STARTED);
                }
                if (endTime + 86400000L - 14400000L > now) {
                    // monitorexit(Constants.lock)
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.NATION_TASK_CD);
                }
                if (forceInfo.getForceLv() >= this.kingdomLvCache.maxLv) {
                    // monitorexit(Constants.lock)
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.MAX_LV);
                }
                final KindomLv kindomLv = (KindomLv)this.kingdomLvCache.get((Object)forceInfo.getForceLv());
                if (forceInfo.getForceExp() < kindomLv.getExpUpgrade()) {
                    // monitorexit(Constants.lock)
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.EXP_IS_NOT_FUL);
                }
                if (forceInfo.getStage() < 4) {
                    // monitorexit(Constants.lock)
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.TRY_CAN_NOT_OPEN_NATION_TASK);
                }
                this.nextBarTaskStart(forceInfo);
                this.forceInfoDao.updateEndTimeAndIsWin(new Date(now + 14400000L), playerDto.forceId, 0);
            }
            // monitorexit(Constants.lock)
            final String msg = MessageFormatter.format(LocalMessages.BAR_TASK_START_BROADCAST, new Object[] { ColorUtil.getForceMsg(forceId, WorldCityCommon.nationIdNameMap.get(forceId)) });
            this.dataGetter.getChatService().sendBigNotice("GLOBAL", null, msg, null);
            this.pushCurNationTaskInfoToPlayer(playerDto.forceId);
            this.jobService.addJob("rankService", "barTaskTimeIsOver", String.valueOf(playerDto.forceId), System.currentTimeMillis() + 14400000L);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private void nextBarTaskStart(final ForceInfo forceInfo) {
        final int isWin = (forceInfo.getIswin() == null) ? 0 : forceInfo.getIswin();
        final int forceId = forceInfo.getForceId();
        if (isWin == 2) {
            final List<BarbariansKillInfo> list = this.barbariansKillInfoDao.getByforceId(forceId);
            Player player = null;
            for (final BarbariansKillInfo info : list) {
                if (RankService.barbarainRanker == null) {
                    break;
                }
                if (info.getIsrewarder() != 0) {
                    continue;
                }
                player = this.playerDao.read(info.getPlayerid());
                final int playerId = player.getPlayerId();
                final int rank = RankService.barbarainRanker.getRank(1, playerId, forceId);
                final String rewards = this.getNationTaskPersonalReward(0, player, forceInfo.getForceLv(), rank, isWin, 0);
                RewardType.reward(rewards, this.dataGetter, playerId, 4);
                this.sendTaskMail(0, rewards, playerId);
            }
        }
        if (RankService.barbarainRanker != null) {
            RankService.barbarainRanker.clear();
        }
        NewBattleManager.getInstance().clearBarbarainKill(forceId);
        this.barbariansKillInfoDao.deleteByForceId(forceId);
    }
    
    private void sendTaskMail(final int i, final String rewards, final int playerId) {
        if (StringUtils.isBlank(rewards)) {
            return;
        }
        final int taskType = getTaskTypeById(i);
        final String header = (i == 0) ? LocalMessages.NATION_TASK_MAIL_HEADER_0 : LocalMessages.NATION_TASK_MAIL_HEADER_1;
        final String rewarString = RewardType.getRewardsString(rewards);
        final String rewardPart = (taskType == 4) ? LocalMessages.NATION_TASK_MAIL_PART1_2 : LocalMessages.NATION_TASK_MAIL_PART1_1;
        final String mailContents = MessageFormatter.format(LocalMessages.NATION_TASK_MAIL_CONTENT, new Object[] { header, rewardPart, rewarString });
        this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, header, mailContents, 1, playerId, new Date());
    }
    
    private void pushCurNationTaskInfoToPlayer(final int forceId) {
        final List<NationTaskAnd> taskAnds = this.getNationTaskAnds(forceId);
        for (final PlayerDto dto : Players.getAllPlayerByForceId(forceId)) {
            if (dto.playerLv <= 30) {
                continue;
            }
            Players.push(dto.playerId, PushCommand.PUSH_NATION_TASK_SIMPLE, this.getCurNationTaskSimpleInfo(dto.playerId, taskAnds));
        }
    }
    
    private int[] chooseCities(final int defForceId) {
        final ForceInfo deForceInfo = this.forceInfoDao.read(defForceId);
        final int defForLv = deForceInfo.getForceLv();
        final int[] cities = new int[2];
        List<KindomTaskRoad> roads = null;
        int target = 0;
        final List<Integer> tIntegers = new ArrayList<Integer>();
        ForceInfo forceInfo = null;
        int forceLv = 0;
        int count = WebUtil.nextInt(2);
        for (int i = 1; i <= 3; ++i) {
            if (defForceId != i) {
                forceInfo = this.forceInfoDao.read(i);
                forceLv = forceInfo.getForceLv();
                if (count == 1) {
                    for (int j = cities.length; j > 0; --j) {
                        roads = this.kingdomTaskRoadCache.getRoadsByType(j);
                        final int biggerLvForce = (defForLv > forceLv) ? defForceId : ((defForLv == forceLv) ? 0 : i);
                        target = this.getTarget(roads, defForceId, i, biggerLvForce);
                        if (this.timerBattleService.addNationTaskExpeditionArmy(target) && target != 0 && !tIntegers.contains(target)) {
                            tIntegers.add(target);
                            break;
                        }
                    }
                }
                else {
                    for (int j = 1; j <= cities.length; ++j) {
                        roads = this.kingdomTaskRoadCache.getRoadsByType(j);
                        final int biggerLvForce = (defForLv > forceLv) ? defForceId : ((defForLv == forceLv) ? 0 : i);
                        target = this.getTarget(roads, defForceId, i, biggerLvForce);
                        if (this.timerBattleService.addNationTaskExpeditionArmy(target) && target != 0 && !tIntegers.contains(target)) {
                            tIntegers.add(target);
                            break;
                        }
                    }
                }
                ++count;
            }
        }
        for (int length = Math.min(tIntegers.size(), cities.length), k = 0; k < length; ++k) {
            cities[k] = tIntegers.get(k);
        }
        return cities;
    }
    
    private boolean isForceConcerned(final int roadId, final int defForceId, final int id) {
        final int ab = roadId % 100;
        final String abs = String.valueOf(ab);
        final boolean firstHas = abs.indexOf(String.valueOf(defForceId)) != -1;
        final boolean secondHas = abs.indexOf(String.valueOf(id)) != -1;
        return firstHas && secondHas;
    }
    
    private Integer computeTaskId(final int timeDivision, final int forceId, final int j) {
        return forceId * 10 + timeDivision + j;
    }
    
    public static Integer getTimeDevision() {
        if (TimeForNationTask.isInTime(RankService.TASK_TIME1[0], RankService.TASK_TIME1[1])) {
            return 1;
        }
        if (TimeForNationTask.isInTime(RankService.TASK_TIME2[0], RankService.TASK_TIME2[1])) {
            return 3;
        }
        if (TimeForNationTask.isInTime(RankService.TASK_TIME3[0], RankService.TASK_TIME3[1])) {
            return 5;
        }
        return -1;
    }
    
    public static Integer getTimeDevision(final Date date) {
        if (TimeForNationTask.isInTime(RankService.TASK_TIME1[0], RankService.TASK_TIME1[1], date)) {
            return 1;
        }
        if (TimeForNationTask.isInTime(RankService.TASK_TIME2[0], RankService.TASK_TIME2[1], date)) {
            return 3;
        }
        if (TimeForNationTask.isInTime(RankService.TASK_TIME3[0], RankService.TASK_TIME3[1], date)) {
            return 5;
        }
        return -1;
    }
    
    private Date getEndTime(final int timeDivision) {
        Date date = null;
        switch (timeDivision) {
            case 1: {
                date = RankService.TASK_TIME1[1].getDate();
                break;
            }
            case 3: {
                date = RankService.TASK_TIME2[1].getDate();
                break;
            }
            case 5: {
                date = RankService.TASK_TIME3[1].getDate();
                break;
            }
            default: {
                RankService.errorLog.error("getEndTime exception...timeDivision:" + timeDivision);
                date = new Date();
                date.setTime(date.getTime() + 5400000L);
                break;
            }
        }
        return date;
    }
    
    private Date getStartTime(final int timeDivision) {
        Date date = null;
        switch (timeDivision) {
            case 1: {
                date = RankService.TASK_TIME1[0].getDate();
                break;
            }
            case 3: {
                date = RankService.TASK_TIME2[0].getDate();
                break;
            }
            case 5: {
                date = RankService.TASK_TIME3[0].getDate();
                break;
            }
        }
        return date;
    }
    
    public static Date getNationTaskStartTime(final int timeDivision) {
        Date date = null;
        switch (timeDivision) {
            case 1: {
                date = RankService.TASK_TIME1[0].getDate();
                break;
            }
            case 3: {
                date = RankService.TASK_TIME2[0].getDate();
                break;
            }
            case 5: {
                date = RankService.TASK_TIME3[0].getDate();
                break;
            }
            default: {
                RankService.errorLog.error("getNationTaskStartTime exception...timeDivision:" + timeDivision);
                date = new Date();
                break;
            }
        }
        return date;
    }
    
    private int getTarget(final List<KindomTaskRoad> roads, final int defForceId, final int i, final int biggerLvForce) {
        String city = null;
        String[] cityS = null;
        int cityId1 = 0;
        int cityId2 = 0;
        String string = null;
        String string2 = null;
        for (final KindomTaskRoad road : roads) {
            final int id = road.getId();
            if (this.isForceConcerned(id, defForceId, i)) {
                city = road.getCities();
                cityS = city.split(";");
                final City[] cities = new City[2];
                final int[] index = new int[2];
                for (int j = 0; j < cityS.length - 1; ++j) {
                    string = cityS[j];
                    string2 = cityS[j + 1];
                    if (!StringUtils.isBlank(string)) {
                        if (!StringUtils.isBlank(string2)) {
                            cityId1 = Integer.parseInt(string);
                            cityId2 = Integer.parseInt(string2);
                            final City city2 = CityDataCache.cityArray[cityId1];
                            final City city3 = CityDataCache.cityArray[cityId2];
                            if (city2 != null) {
                                if (city3 != null) {
                                    if (city2.getForceId() == defForceId && city3.getForceId() != defForceId) {
                                        cities[0] = city2;
                                        cities[1] = city3;
                                        index[1] = (index[0] = j) + 1;
                                        break;
                                    }
                                    if (city2.getForceId() != defForceId && city3.getForceId() == defForceId) {
                                        cities[0] = city3;
                                        cities[1] = city2;
                                        index[0] = j + 1;
                                        index[1] = j;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if (cities[0] == null) {
                    continue;
                }
                if (cities[1] == null) {
                    continue;
                }
                int random = WebUtil.nextInt(cities.length);
                City cityI = null;
                if (random == cities.length - 1) {
                    cityI = cities[random];
                    if (cityI.getForceId() != i || cityI.getId() == WorldCityCommon.nationMainCityIdMap.get(cityI.getForceId())) {
                        random = 0;
                    }
                }
                cityI = cities[random];
                final int indexI = index[random];
                if (biggerLvForce != 0 && WebUtil.nextDouble() <= 0.3) {
                    final int backForceId = (defForceId == biggerLvForce) ? i : defForceId;
                    final int index2 = indexI - 1;
                    if (index2 >= 0 && index2 < cityS.length) {
                        final int cityIdf = Integer.parseInt(cityS[index2]);
                        final City cityF = CityDataCache.cityArray[cityIdf];
                        if (cityF != null && cityF.getForceId() == backForceId && cityF.getId() != WorldCityCommon.nationMainCityIdMap.get(cityF.getForceId()) && this.isCityFreeToBattle(cityF.getId())) {
                            return cityF.getId();
                        }
                    }
                    final int index3 = indexI + 1;
                    if (index3 >= 0 && index3 < cityS.length) {
                        final int cityIdS = Integer.parseInt(cityS[index3]);
                        final City citySe = CityDataCache.cityArray[cityIdS];
                        if (citySe != null && citySe.getForceId() == backForceId && citySe.getId() != WorldCityCommon.nationMainCityIdMap.get(citySe.getForceId()) && this.isCityFreeToBattle(citySe.getId())) {
                            return citySe.getId();
                        }
                    }
                }
                if (this.isCityFreeToBattle(cityI.getId())) {
                    return cityI.getId();
                }
                continue;
            }
        }
        return 0;
    }
    
    public boolean isCityFreeToBattle(final int cityId) {
        final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
        return battle == null || (battle.getAttList().size() >= 2 && battle.getDefList().size() >= 2);
    }
    
    @Override
    public byte[] getNationTaskReward(final PlayerDto playerDto, final int taskId) {
        final JsonDocument doc = new JsonDocument();
        if (taskId == 0) {
            final ForceInfo forceInfo = this.forceInfoDao.read(playerDto.forceId);
            if (forceInfo == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
            }
            if (forceInfo.getIswin() == null || forceInfo.getIswin() == 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
            }
            final BarbariansKillInfo barbariansKillInfo = this.barbariansKillInfoDao.read(playerDto.playerId);
            if (barbariansKillInfo == null || barbariansKillInfo.getIsrewarder() == 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
            }
            doc.startObject();
            final int playerId = playerDto.playerId;
            final int forceId = playerDto.forceId;
            final Player player = this.playerDao.read(playerId);
            final int isWin = (forceInfo.getIswin() == null) ? 0 : forceInfo.getIswin();
            final int rankNum = RankService.barbarainRanker.getRank(1, playerId, forceId);
            final String rewards = this.getNationTaskPersonalReward(taskId, player, forceInfo.getForceLv(), rankNum, isWin, 0);
            if (StringUtils.isBlank(rewards)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
            }
            RewardType.rewardDoc(rewards, doc);
            RewardType.reward(rewards, this.dataGetter, playerId, 4);
            doc.endObject();
            this.barbariansKillInfoDao.updateIsRewarded(playerDto.playerId, 1);
        }
        else {
            final int timeDivision = getTimeDevision();
            final long now = System.currentTimeMillis();
            final NationTask task = this.nationTaskDao.read(taskId);
            if (task == null) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
            }
            final int taskType = getTaskTypeById(task.getNationTaskId());
            if (task.getIswin() != 2 && taskType != 1 && taskType != 6 && taskType != 7 && taskType != 8 && taskType != 9 && taskType != 10 && taskType != 12) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
            }
            final ForceInfo forceInfo2 = this.forceInfoDao.read(playerDto.forceId);
            final TaskKillInfo killInfo = this.taskKillInfoDao.getTaskKillInfoByPAndT(playerDto.playerId, taskId);
            final PlayerExpandInfo playerExpandInfo = this.playerExpandInfoDao.read(playerDto.playerId);
            int ir = 0;
            int showPre = (playerExpandInfo == null || playerExpandInfo.getIsrewarded() != 0) ? 1 : 0;
            if (taskType != 6 && taskType != 7 && taskType != 8 && taskType != 9 && taskType != 10 && taskType != 12) {
                if (killInfo == null) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
                }
                ir = ((killInfo == null) ? 0 : killInfo.getIsrewarded());
            }
            else if (taskType == 7 || taskType == 8 || taskType == 9 || taskType == 10 || taskType == 12) {
                showPre = ((timeDivision <= 0) ? 0 : showPre);
                if (showPre == 0) {
                    if (playerExpandInfo == null) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
                    }
                    ir = playerExpandInfo.getIsrewarded();
                }
                else {
                    if (killInfo == null) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
                    }
                    ir = killInfo.getIsrewarded();
                }
            }
            else {
                if (playerExpandInfo == null) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
                }
                ir = ((playerExpandInfo == null) ? 0 : playerExpandInfo.getIsrewarded());
            }
            final BaseRanker ranker = RankService.nationTaskKillRanker.getRanker(taskId);
            int isRewarded = 0;
            isRewarded = ir;
            if (isRewarded == 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
            }
            final int playerId2 = playerDto.playerId;
            final int forceId2 = playerDto.forceId;
            final Player player2 = this.playerDao.read(playerId2);
            int rankNum2 = 0;
            if (taskType == 6 || taskType == 7 || taskType == 8 || taskType == 9 || taskType == 10 || taskType == 12) {
                rankNum2 = ((showPre == 0) ? playerExpandInfo.getRank() : ranker.getRank(1, playerId2, forceId2));
            }
            else {
                int rankForceID = forceId2;
                if (taskType == 5) {
                    rankForceID = 0;
                }
                rankNum2 = ranker.getRank(1, playerId2, rankForceID);
            }
            int isWin2 = (task.getIswin() == null) ? 0 : task.getIswin();
            if (taskType == 7 || taskType == 9) {
                isWin2 = ((isWin2 == 0 && now <= task.getEndtime().getTime()) ? 2 : isWin2);
            }
            else if (taskType == 8) {
                final int serial = taskId % 100 / 10;
                if (serial > 3) {
                    if (showPre == 0) {
                        isWin2 = ((timeDivision > 0) ? 1 : isWin2);
                    }
                }
                else if (showPre == 0) {
                    isWin2 = ((timeDivision > 0) ? 2 : isWin2);
                    if (serial <= 1 && task.getIswin() != 0) {
                        isWin2 = task.getIswin();
                    }
                }
            }
            else if (taskType == 10) {
                final int taskSerial = taskId % 1000 / 100;
                int serial2 = (showPre == 0) ? (taskSerial - 1) : taskSerial;
                serial2 = ((timeDivision <= 0) ? taskSerial : serial2);
                int taskState = task.getIswin();
                if (showPre == 0 && timeDivision > 0) {
                    taskState = ((RankService.nationMiracle.serialWinner[serial2 - 1] == forceId2) ? 2 : 1);
                }
                isWin2 = taskState;
            }
            else if (taskType == 12) {
                final int taskSerial = taskId % 1000 / 100;
                int serial2 = (showPre == 0) ? (taskSerial - 1) : taskSerial;
                serial2 = ((timeDivision <= 0) ? taskSerial : serial2);
                int taskState = task.getIswin();
                if (showPre == 0 && timeDivision > 0) {
                    taskState = ((RankService.festival.serialWinner[serial2 - 1] == forceId2) ? 2 : 1);
                }
                isWin2 = taskState;
            }
            String rewards2 = this.getNationTaskPersonalReward(taskId, player2, forceInfo2.getForceLv(), rankNum2, isWin2, 0);
            boolean except = this.checkExcept(isWin2, taskType, killInfo, playerExpandInfo);
            if (StringUtils.isBlank(rewards2) && !except) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
            }
            doc.startObject();
            boolean showExtraFlag = true;
            boolean giveFailFlag = true;
            if (taskType != 8) {
                RewardType.rewardDoc(rewards2, doc);
            }
            else {
                final int serial3 = taskId % 100 / 10;
                if (serial3 <= 3) {
                    RewardType.rewardDoc(rewards2, doc);
                    if (isWin2 != 2) {
                        showExtraFlag = false;
                    }
                }
                else {
                    if (showPre == 0 && timeDivision > 0) {
                        RewardType.rewardDoc(rewards2, doc);
                    }
                    else {
                        giveFailFlag = except;
                    }
                    showExtraFlag = false;
                }
            }
            if (taskType > 1 && taskType < 5) {
                final float extraRewards = this.getExtraTaskReward(forceId2);
                doc.createElement("hasExtra", extraRewards != 0.0f);
                final String extra = RewardType.rewardByTimees(rewards2, extraRewards);
                doc.appendJson(RewardType.rewards(extra, "extraRewards"));
                rewards2 = RewardType.mergeRewards(rewards2, extra);
            }
            else if (taskType == 1 || taskType == 6 || taskType == 7 || taskType == 8 || taskType == 9 || taskType == 10 || taskType == 12) {
                except = (!StringUtils.isBlank(rewards2) && except);
                if (except) {
                    final KindomLv kindomLv = (KindomLv)this.kingdomLvCache.get((Object)forceInfo2.getForceLv());
                    final String extra = this.getVicTaskReward(taskId, taskType, kindomLv, showPre);
                    if (showExtraFlag) {
                        doc.appendJson(RewardType.rewards(extra, "extraRewards"));
                        rewards2 = RewardType.mergeRewards(rewards2, extra);
                        doc.createElement("hasExtra", true);
                    }
                    else {
                        RewardType.rewardDoc(extra, doc);
                        rewards2 = extra;
                    }
                }
            }
            if (StringUtils.isBlank(rewards2) || !giveFailFlag) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
            }
            RewardType.reward(rewards2, this.dataGetter, playerId2, 3);
            final Tuple<byte[], String> indivRewards = this.individualTaskService.getReward(playerDto);
            if (indivRewards != null && indivRewards.left != null) {
                doc.appendJson(indivRewards.left);
            }
            doc.endObject();
            this.updateTaskRewardInfo(playerId2, taskId, showPre);
        }
        final List<NationTaskAnd> list = this.getNationTaskAnds(playerDto.forceId);
        Players.push(playerDto.playerId, PushCommand.PUSH_NATION_TASK_SIMPLE, this.getCurNationTaskSimpleInfo(playerDto.playerId, list));
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] getRewardWholePointKill(final PlayerDto playerDto) {
        final JsonDocument doc = new JsonDocument();
        final WholeKill wk = this.wholeKillDao.read(playerDto.playerId);
        if (wk == null || 1 == wk.getReceivedReward() || wk.getLastRank() == 0) {
            if (wk != null) {
                if (wk.getReceivedReward() == 0 && wk.getLastRank() < 1) {
                    this.wholeKillDao.received(playerDto.playerId);
                }
                Players.push(playerDto.playerId, PushCommand.PUSH_WORLD, JsonBuilder.getSimpleJson("mask", true));
                RankService.errorLog.error("RankService getRewardWholePointKill playerId:" + playerDto.playerId + " getReceivedReward:" + wk.getReceivedReward() + " getLastRank:" + wk.getLastRank());
            }
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
        }
        this.wholeKillDao.received(playerDto.playerId);
        final Player player = this.playerDao.read(playerDto.playerId);
        final MultiResult result = this.kingdomTaskRankingCache.getTaskRanking(wk.getLastRank(), this.forceInfoDao.read(player.getForceId()).getForceLv(), 999, -1);
        String rewards = "food,";
        rewards = String.valueOf(rewards) + ((result == null || result.result1 == null) ? "0" : result.result1.toString());
        final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(rewards);
        reward.rewardPlayer(playerDto, this.dataGetter, "\u6574\u70b9\u6740\u654c\u699c\u5956\u52b1", null);
        doc.startObject();
        RewardType.rewardDoc(rewards, doc);
        this.dataGetter.getCityService().getWholeKillTitle(playerDto.playerId, playerDto.forceId, doc, true);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private void updateTaskRewardInfo(final int playerId, final int taskId, final int showPre) {
        final int taskType = getTaskTypeById(taskId);
        if (taskType < 6) {
            this.taskKillInfoDao.updateIsRewardedTask(playerId, taskId, 1);
        }
        else if (taskType == 7 || taskType == 8 || taskType == 9 || taskType == 10 || taskType == 12) {
            if (showPre == 0) {
                this.playerExpandInfoDao.updateIsRewardedTask(playerId, 1);
            }
            else {
                this.taskKillInfoDao.updateIsRewardedTask(playerId, taskId, 1);
            }
        }
        else {
            this.playerExpandInfoDao.updateIsRewardedTask(playerId, 1);
        }
    }
    
    private boolean checkExcept(final int isWin, final int taskType, final TaskKillInfo killInfo, final PlayerExpandInfo playerExpandInfo) {
        if (taskType == 1) {
            return isWin == 2 && killInfo != null;
        }
        if (taskType != 6) {
            return (taskType == 7 || taskType == 8 || taskType == 9 || taskType == 10 || taskType == 12) && isWin == 2;
        }
        if (playerExpandInfo == null) {
            return false;
        }
        final int playerId = playerExpandInfo.getPlayerId();
        final Player player = this.playerDao.read(playerId);
        final String taskInfo = this.getTaskSavedInfo(player.getForceId());
        int taskState = 0;
        if (!StringUtils.isBlank(taskInfo)) {
            final String[] single = taskInfo.split("#");
            if (single.length >= 4) {
                taskState = Integer.parseInt(single[3]);
            }
        }
        return taskState == 2;
    }
    
    @Override
    public void updateKillNum(final int type, int killTotal, final int playerId, final long updateTime) {
        try {
            final long start = System.currentTimeMillis();
            BaseRanker ranker = null;
            final Player player = this.playerDao.read(playerId);
            final int forceId = player.getForceId();
            int orignTotal = killTotal;
            boolean needToPushTaskInfo = false;
            if (type == 0) {
                final int hasBatTask = this.hasBarTasks(player.getForceId());
                if (hasBatTask == 0) {
                    return;
                }
                ranker = RankService.barbarainRanker;
                int killTotalAll = 0;
                synchronized (this) {
                    BarbariansKillInfo killInfo = this.barbariansKillInfoDao.read(playerId);
                    if (killInfo == null) {
                        killInfo = new BarbariansKillInfo();
                        killInfo.setForceid(player.getForceId());
                        killInfo.setIsrewarder(0);
                        killInfo.setKillnum(killTotal);
                        killInfo.setPlayerid(playerId);
                        this.barbariansKillInfoDao.create(killInfo);
                        killTotalAll = killTotal;
                    }
                    else {
                        final int killNum = killInfo.getKillnum();
                        killTotalAll = killNum + killTotal;
                        this.barbariansKillInfoDao.updateKillNum(playerId, killTotalAll);
                    }
                }
                long curKillTotal = NewBattleManager.getInstance().getBarbarainKillByForceId(player.getForceId());
                curKillTotal = ((curKillTotal == Long.MIN_VALUE) ? 0L : curKillTotal);
                final ForceInfo forceInfo = this.forceInfoDao.read(player.getForceId());
                final KindomLv kindomLv = (KindomLv)this.kingdomLvCache.get((Object)forceInfo.getForceLv());
                final Barbarain barbarain = (Barbarain)this.barbarainCache.get((Object)kindomLv.getBarbarainLv());
                final int requestNum = barbarain.getTarget();
                if (curKillTotal >= requestNum) {
                    final int res = this.forceInfoDao.updateIsWinAndLvAndExp(player.getForceId(), 2, kindomLv.getExpUpgrade());
                    if (res > 0) {
                        this.cacheNationLv(forceId);
                        this.dataGetter.getCityService().initCountryPrivilege();
                        final int lv = forceInfo.getForceLv() + 1;
                        final String msg = MessageFormatter.format(LocalMessages.BAR_TASK_V_BROADCAST, new Object[] { ColorUtil.getForceMsg(forceId, WorldCityCommon.nationIdNameMap.get(forceId)), lv });
                        this.dataGetter.getChatService().sendBigNotice("GLOBAL", null, msg, null);
                        needToPushTaskInfo = true;
                        this.pushBarTaskInfo(forceId, lv, 1);
                    }
                }
                killTotal = killTotalAll;
                if (ranker != null && killTotal > 0) {
                    ranker.fireRankEvent(1, new com.reign.gcld.world.common.RankData(playerId, killTotal));
                    final int rankBefore = ranker.getRank(1, playerId, forceId);
                    final List<com.reign.gcld.world.common.RankData> listBefore = ranker.getRankListDatas(1, forceId);
                    final int rankAfter = ranker.getRank(1, playerId, forceId);
                    final List<com.reign.gcld.world.common.RankData> listAfter = ranker.getRankListDatas(1, forceId);
                    this.checkTitleIsChange(listBefore, listAfter, 0, playerId, ranker, killTotal, forceId);
                    this.checkTitleIsChange(rankBefore, rankAfter, 0, playerId, ranker, killTotal, forceId);
                }
            }
            else {
                if (RankService.nationTaskKillRanker == null) {
                    return;
                }
                final List<NationTask> tasks = this.nationTaskDao.getListByForce(forceId);
                final int backTotal = killTotal;
                for (final NationTask task : tasks) {
                    killTotal = backTotal;
                    final int taskId = task.getNationTaskId();
                    final int taskType = getTaskTypeById(task.getNationTaskId());
                    if (taskType == 0) {
                        continue;
                    }
                    if (task.getIswin() != 0) {
                        continue;
                    }
                    if (taskType == 3 || taskType == 4 || taskType == 5 || taskType == 7) {
                        if (taskType != type) {
                            continue;
                        }
                    }
                    else if ((taskType == 1 || taskType == 2 || taskType == 6) && type == 3) {
                        continue;
                    }
                    ranker = RankService.nationTaskKillRanker.getRanker(taskId);
                    int times = 0;
                    try {
                        TaskKillInfo killInfo2 = this.taskKillInfoDao.getTaskKillInfoByPAndT(playerId, taskId);
                        boolean isUpdate = false;
                        if (killInfo2 == null) {
                            if (killTotal > 0) {
                                killInfo2 = new TaskKillInfo();
                                killInfo2.setIsrewarded(0);
                                killInfo2.setKillnum(killTotal);
                                killInfo2.setPlayerId(playerId);
                                killInfo2.setUpdatetime(updateTime);
                                killInfo2.setTaskId(taskId);
                                final int res2 = this.taskKillInfoDao.create(killInfo2);
                                if (res2 == 0) {
                                    isUpdate = true;
                                }
                            }
                            else {
                                orignTotal = 0;
                            }
                        }
                        else {
                            isUpdate = true;
                        }
                        if (isUpdate) {
                            long forceTotal = ranker.getKillTotalByForceId(forceId).investNum;
                            forceTotal += killTotal;
                            killTotal += killInfo2.getKillnum();
                            if (killTotal < 0) {
                                forceTotal -= killTotal;
                                orignTotal -= killTotal;
                                killTotal = 0;
                            }
                            if (taskType == 4) {
                                final boolean canAdd = this.checkInvestTaskIsOver(forceId, forceTotal);
                                if (!canAdd) {
                                    this.taskKillInfoDao.updateKillNumTaskId(playerId, taskId, killTotal, updateTime);
                                }
                            }
                            else {
                                this.taskKillInfoDao.updateKillNumTaskId(playerId, taskId, killTotal, updateTime);
                            }
                        }
                    }
                    catch (Exception e) {
                        RankService.errorLog.error("lock exception...playerId:" + playerId + "times:" + times);
                        RankService.errorLog.error(e.getMessage());
                        RankService.errorLog.error(this, e);
                        ++times;
                    }
                    if (ranker != null && killTotal > 0) {
                        int parForceId = forceId;
                        if (taskType == 5) {
                            parForceId = 0;
                        }
                        final int rankBefore = ranker.getRank(1, playerId, parForceId);
                        final List<com.reign.gcld.world.common.RankData> listBefore = ranker.getRankListDatas(1, parForceId);
                        ranker.fireRankEvent(1, new com.reign.gcld.world.common.RankData(playerId, killTotal));
                        final int rankAfter = ranker.getRank(1, playerId, parForceId);
                        final List<com.reign.gcld.world.common.RankData> listAfter = ranker.getRankListDatas(1, parForceId);
                        this.checkTitleIsChange(listBefore, listAfter, taskId, playerId, ranker, killTotal, parForceId);
                        this.checkTitleIsChange(rankBefore, rankAfter, taskId, playerId, ranker, killTotal, parForceId);
                    }
                    if (ranker == null) {
                        continue;
                    }
                    ranker.fireTotalChange(forceId, orignTotal, System.currentTimeMillis());
                }
            }
            if (needToPushTaskInfo) {
                this.pushCurNationTaskInfoToPlayer(forceId);
            }
            RankService.timerLog.info(LogUtil.formatThreadLog("RankService", "updateKillNum", 2, System.currentTimeMillis() - start, "type:" + type + "|killTotal:" + killTotal + "|playerId:" + playerId));
        }
        catch (Exception e2) {
            RankService.errorLog.error("updateKillNum playerId:" + playerId + "killNum:" + killTotal, e2);
        }
    }
    
    @Override
    public void updateWholeKillNum(final int playerId, final int killNum) {
        try {
            if (!WorldUtil.isWholePointKill() || RankService.wholeKillRank == null) {
                return;
            }
            final WholeKill wk = this.wholeKillDao.read(playerId);
            if (wk == null || wk.getReceivedReward() == 0) {
                return;
            }
            final int killTotal = wk.getKillNum() + killNum;
            this.wholeKillDao.updateKillNum(playerId, killNum);
            final int forceId = this.playerDao.getForceId(playerId);
            final int rankBefore = RankService.wholeKillRank.getRank(1, playerId, forceId);
            final List<com.reign.gcld.world.common.RankData> listBefore = RankService.wholeKillRank.getRankListDatas(1, forceId);
            RankService.wholeKillRank.fireRankEvent(1, new com.reign.gcld.world.common.RankData(playerId, killTotal));
            final int rankAfter = RankService.wholeKillRank.getRank(1, playerId, forceId);
            final List<com.reign.gcld.world.common.RankData> listAfter = RankService.wholeKillRank.getRankListDatas(1, forceId);
            this.checkTitleIsChange(listBefore, listAfter, 999, playerId, RankService.wholeKillRank, killTotal, forceId);
            this.checkTitleIsChange(rankBefore, rankAfter, 999, playerId, RankService.wholeKillRank, killTotal, forceId);
        }
        catch (Exception e) {
            RankService.errorLog.error("updateWholeKillNum playerId:" + playerId + "killNum:" + killNum, e);
        }
    }
    
    @Override
    public void updateScoreRank(final int playerId, final int type, final int cityId) {
        try {
            if (this.playerAttributeDao.getFunctionId(playerId).toCharArray()[63] != '1' || RankService.scoreRank == null) {
                return;
            }
            final int before = this.playerScoreRankDao.getScore2(playerId);
            int score = 0;
            String displayMsg = "";
            if (1 == type) {
                this.playerScoreRankDao.updateOccupyAndScore(playerId, 5);
                displayMsg = LocalMessages.SCORE_RANK_OCCUPY_CITY;
                score = 5;
            }
            else if (2 == type) {
                this.playerScoreRankDao.updateAssistAndScore(playerId, 2);
                displayMsg = LocalMessages.SCORE_RANK_ASSIST_CITY;
                score = 2;
            }
            else {
                this.playerScoreRankDao.updateCheerAndScore(playerId, 1);
                displayMsg = LocalMessages.SCORE_RANK_CHEER_CITY;
                score = 1;
            }
            final int after = this.playerScoreRankDao.getScore2(playerId);
            final int boxNum = this.tpCoTnumCache.getReward(before, after);
            if (boxNum > 0) {
                this.playerDragonDao.addBoxNumByPlayerId(playerId, boxNum, 100);
            }
            RankService.scoreRank.fireRankEvent(1, new com.reign.gcld.world.common.RankData(playerId, after));
            final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
            if (wc == null) {
                RankService.errorLog.error("#class:RankService#method:updateScoreRank#cityId:" + cityId);
            }
            else if (boxNum > 0) {
                final String msg = MessageFormatter.format(displayMsg, new Object[] { wc.getName(), score, after });
                this.dataGetter.getChatService().sendSystemChat("SYS2ONE", playerId, this.playerDao.getForceId(playerId), msg, null);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("#class:RankService#method:updateScoreRank#playerId:" + playerId + "#type" + type + "#cityId" + cityId, e);
        }
    }
    
    private void checkTitleIsChange(final List<com.reign.gcld.world.common.RankData> listBefore, final List<com.reign.gcld.world.common.RankData> listAfter, final int taskId, final int playerId, final BaseRanker ranker, int killTotal, final int parForceId) {
        final int sizeBefore = listBefore.size();
        final int sizeAfter = listAfter.size();
        final int minSize = Math.min(sizeBefore, sizeAfter);
        final int maxSize = Math.max(sizeBefore, sizeAfter);
        for (int i = 0; i < minSize; ++i) {
            final com.reign.gcld.world.common.RankData rankData1 = listBefore.get(i);
            final com.reign.gcld.world.common.RankData rankData2 = listAfter.get(i);
            if (rankData1.playerId != rankData2.playerId) {
                killTotal = rankData2.value;
                this.checkTitleIsChange(-1, i + 1, taskId, rankData2.playerId, ranker, killTotal, parForceId);
            }
        }
        final List<com.reign.gcld.world.common.RankData> list = (sizeBefore == maxSize) ? listBefore : listAfter;
        for (int size = Math.min(maxSize, 999), j = minSize; j < size; ++j) {
            final com.reign.gcld.world.common.RankData rankData3 = list.get(j);
            killTotal = rankData3.value;
            this.checkTitleIsChange(-1, j + 1, taskId, rankData3.playerId, ranker, killTotal, parForceId);
        }
    }
    
    private void checkTitleIsChange(final int rankBefore, final int rankAfter, final int taskId, final int playerId, final BaseRanker ranker, final int killTotal, final int forceId) {
        if (ranker == null) {
            return;
        }
        final int taskType = getTaskTypeById(taskId);
        final int lvBefore = this.kingdomTaskRankingCache.getTaskRankingLv(rankBefore, taskType);
        final int lvAfter = this.kingdomTaskRankingCache.getTaskRankingLv(rankAfter, taskType);
        boolean haveRank = true;
        if (lvAfter != lvBefore) {
            final String titlePic = this.kingdomTaskRankingCache.getTitlePic(lvAfter, taskType);
            if (!StringUtils.isBlank(titlePic)) {
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("playerId", playerId);
                this.titleInfoByCurTitle(lvAfter, taskType, doc, rankAfter);
                doc.endObject();
                Players.push(playerId, PushCommand.PUSH_KILL_ADD, doc.toByte());
                haveRank = false;
            }
        }
        int nextRank = this.kingdomTaskRankingCache.getNextRank(rankAfter, 1, taskType, 1);
        final int lvNext = this.kingdomTaskRankingCache.getTaskRankingLv(nextRank, taskType);
        int type = 0;
        if (lvAfter == lvNext || nextRank == 0) {
            type = 1;
        }
        nextRank = ((nextRank == 0) ? (rankAfter - 2) : (nextRank - 1));
        final int nextKillNum = this.getNationTaskNextKillNum(ranker, forceId, nextRank, taskType, 1);
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        if (type == 0) {
            doc2.createElement("lvNext", this.kingdomTaskRankingCache.getTitleQuality(lvNext, taskType));
            doc2.createElement("nextTitle", this.kingdomTaskRankingCache.getTitleString(lvNext, taskType));
        }
        doc2.createElement("playerId", playerId);
        doc2.createElement("nextKillNum", (nextKillNum - killTotal < 0) ? 0 : (nextKillNum - killTotal));
        doc2.createElement("killTotal", killTotal);
        doc2.createElement("type", type);
        doc2.createElement("taskType", taskType);
        doc2.createElement("lv", this.kingdomTaskRankingCache.getTitleQuality(lvAfter, taskType));
        if (haveRank) {
            doc2.createElement("rank", rankAfter);
            doc2.createElement("titleName", this.kingdomTaskRankingCache.getTitlePicName(lvAfter, taskType));
        }
        doc2.endObject();
        Players.push(playerId, PushCommand.PUSH_KILL_ADD, doc2.toByte());
    }
    
    private boolean checkInvestTaskIsOver(final int forceId, final long forceTotal) {
        final NationTask task = this.nationTaskDao.getByForce(forceId);
        if (task == null) {
            return false;
        }
        if (task.getIswin() != 0) {
            return false;
        }
        final int target = task.getTarget();
        final int taskId = task.getNationTaskId();
        final int event = taskId % 100 / 10;
        final Date date = task.getEndtime();
        final long startTime = date.getTime() - 5400000L;
        final long now = System.currentTimeMillis();
        final long lastTime = now - startTime;
        final KtTzEv ktTzEv = this.ktTzEvCache.getCurSerial(lastTime, event);
        if (ktTzEv == null) {
            return false;
        }
        if (this.ktTzEvCache.isLastSerial(ktTzEv, event)) {
            if (forceTotal >= target) {
                this.nationTaskDao.updateIsWinAndFinishTime(task.getNationTaskId(), 2, now);
                final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
                final KindomLv kindomLv = (KindomLv)this.kingdomLvCache.get((Object)forceInfo.getForceLv());
                final int nowExp = forceInfo.getForceExp();
                final int maxExp = kindomLv.getExpUpgrade();
                if (nowExp < maxExp && forceInfo.getForceLv() < this.kingdomLvCache.maxLv) {
                    final int addExp = this.getAddExp(4, task.getAttType(), task.getTarget(), forceInfo, kindomLv);
                    final int finalExp = (addExp + nowExp > maxExp) ? maxExp : (addExp + nowExp);
                    this.forceInfoDao.updateNationExp(forceId, finalExp);
                    final String msg = MessageFormatter.format(LocalMessages.NATION_TASK_SERAIL_FINISH, new Object[] { ColorUtil.getForceMsg(forceId, WorldCityCommon.nationIdNameMap.get(forceId)), addExp });
                    this.chatService.sendBigNotice("GLOBAL", null, msg, null);
                }
                final PushTaskOverInfo overInfo = new PushTaskOverInfo();
                overInfo.force = forceId;
                overInfo.lv = forceInfo.getForceLv();
                overInfo.state = 2;
                overInfo.page = 1;
                overInfo.taskType = 4;
                overInfo.rank = this.getNationRank(forceId, 4);
                this.pushNationTaskInfo(overInfo);
                InMemmoryIndivTaskManager.getInstance().clearNationIndivTask(forceId);
            }
        }
        else if (forceTotal == target && ktTzEv.getI() % 3 == 0) {
            final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
            final KindomLv kindomLv = (KindomLv)this.kingdomLvCache.get((Object)forceInfo.getForceLv());
            final int nowExp = forceInfo.getForceExp();
            final int maxExp = kindomLv.getExpUpgrade();
            final int addExp = 1;
            if (nowExp < maxExp && forceInfo.getForceLv() < this.kingdomLvCache.maxLv) {
                final int finalExp = (addExp + nowExp > maxExp) ? maxExp : (addExp + nowExp);
                this.forceInfoDao.updateNationExp(forceId, finalExp);
            }
            final String msg2 = MessageFormatter.format(LocalMessages.NATION_TASK_SERAIL_FINISH, new Object[] { ColorUtil.getForceMsg(forceId, WorldCityCommon.nationIdNameMap.get(forceId)), addExp });
            this.dataGetter.getChatService().sendBigNotice("GLOBAL", null, msg2, null);
        }
        return forceTotal > target;
    }
    
    @Override
    public void barTaskTimeIsOver(final String params) {
        try {
            final int forceId = Integer.parseInt(params);
            final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
            if (forceInfo.getIswin() == null || forceInfo.getIswin() == 0) {
                long totalKillNum = NewBattleManager.getInstance().getBarbarainKillByForceId(forceId);
                totalKillNum = ((totalKillNum == Long.MIN_VALUE) ? 0L : totalKillNum);
                final KindomLv kindomLv = (KindomLv)this.kingdomLvCache.get((Object)forceInfo.getForceLv());
                final Barbarain barbarain = (Barbarain)this.barbarainCache.get((Object)kindomLv.getBarbarainLv());
                final int requestNum = barbarain.getTarget();
                String msg = "";
                if (totalKillNum >= requestNum) {
                    final int lv = forceInfo.getForceLv() + 1;
                    this.forceInfoDao.updateIsWinAndLvAndExp(forceId, 2, kindomLv.getExpUpgrade());
                    this.cacheNationLv(forceId);
                    this.dataGetter.getCityService().initCountryPrivilege();
                    this.pushBarTaskInfo(forceId, lv, 1);
                    msg = MessageFormatter.format(LocalMessages.BAR_TASK_V_BROADCAST, new Object[] { ColorUtil.getForceMsg(forceId, WorldCityCommon.nationIdNameMap.get(forceId)) });
                }
                else {
                    msg = MessageFormatter.format(LocalMessages.BAR_TASK_F_BROADCAST, new Object[] { ColorUtil.getForceMsg(forceId, WorldCityCommon.nationIdNameMap.get(forceId)) });
                    this.forceInfoDao.updateIsWin(forceId, 1);
                    this.pushBarTaskInfo(forceId, forceInfo.getForceLv() + 1, 0);
                }
                this.dataGetter.getChatService().sendBigNotice("GLOBAL", null, msg, null);
                this.pushCurNationTaskInfoToPlayer(forceId);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("barTaskTimeIsOver fail ..forceId = " + params, e);
        }
    }
    
    @Override
    public void nationTaskIsOver(final int forceId, final int target) {
        try {
            boolean flag = false;
            int nextCity = 0;
            for (int i = 0; i < RankService.chooseCities.length; ++i) {
                if (RankService.chooseCities[i] == target) {
                    flag = true;
                }
                else {
                    nextCity = RankService.chooseCities[i];
                }
            }
            if (!flag) {
                return;
            }
            final NationTask task = this.nationTaskDao.getByForceAndTarget(forceId, target);
            if (task == null) {
                return;
            }
            final int taskType = getTaskTypeById(task.getNationTaskId());
            if (taskType != 1) {
                return;
            }
            final int isWin = task.getIswin();
            if (isWin == 1 || isWin == 2) {
                return;
            }
            if (isWin != 0 || task.getAttType() != 0) {
                return;
            }
            RankService.errorLog.error("nationTaskIsOver" + forceId + "time" + new Date());
            this.nationTaskDao.resetTaskIsWin(task.getNationTaskId(), forceId);
            ForceInfo forceInfo = null;
            PushTaskOverInfo overInfo = null;
            for (int j = 1; j <= 3; ++j) {
                this.pushCurNationTaskInfoToPlayer(j);
                forceInfo = this.forceInfoDao.read(j);
                overInfo = new PushTaskOverInfo();
                overInfo.force = j;
                overInfo.city = target;
                overInfo.lv = forceInfo.getForceLv();
                overInfo.page = task.getNationTaskId() % 2;
                if (j == forceId) {
                    overInfo.state = 2;
                    overInfo.attType = 0;
                    this.pushNationTaskInfo(overInfo);
                }
                else {
                    overInfo.state = 1;
                    this.pushNationTaskInfo(overInfo);
                }
            }
            forceInfo = this.forceInfoDao.read(forceId);
            final KindomLv kindomLv = (KindomLv)this.kingdomLvCache.get((Object)forceInfo.getForceLv());
            final int nationExp = forceInfo.getForceExp();
            final int nationLv = forceInfo.getForceLv();
            final int addExp = this.getAddExp(taskType, task.getAttType(), task.getTarget(), forceInfo, kindomLv);
            final int maxExp = kindomLv.getExpUpgrade();
            if (nationLv < this.kingdomLvCache.maxLv) {
                if (nationExp != maxExp) {
                    if (nationExp + addExp >= maxExp) {
                        this.forceInfoDao.updateNationExp(forceId, maxExp);
                    }
                    else {
                        this.forceInfoDao.updateNationExp(forceId, nationExp + addExp);
                    }
                }
            }
            final NationTask nextTask = this.nationTaskDao.getByForceAndTarget(forceId, nextCity);
            if (nextTask != null && nextTask.getIswin() != 0) {
                InMemmoryIndivTaskManager.getInstance().clearAfterTaskIsOver();
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("RankService, modify nation task is over exception.", e);
        }
    }
    
    private int getAddExp(final int taskType, final Integer attType, final Integer target, final ForceInfo forceInfo, final KindomLv kindomLv) {
        int result = 0;
        if (taskType == 1) {
            final int baseExp = kindomLv.getExpPerTask();
            final KindomTaskCity road = (KindomTaskCity)this.kindomTaskCityCache.get((Object)target);
            if (road == null) {
                return 1;
            }
            final int forceId = forceInfo.getForceId();
            int degree = 1;
            if (forceId == 1) {
                degree = road.getWeiDegree();
            }
            else if (forceId == 2) {
                degree = road.getShuDegree();
            }
            else {
                degree = road.getWuDegree();
            }
            result = baseExp * degree;
            if (attType == 1) {
                result += baseExp;
            }
            return result;
        }
        else {
            if (taskType == 6) {
                final KtKjT ktKjT = this.ktKjRelativeCache.getKtKjTByWinTimes(attType);
                return (ktKjT == null) ? 0 : ktKjT.getExpC();
            }
            if (taskType == 5) {
                final int nationRank = this.getNationRank(forceInfo.getForceId(), taskType);
                final int grade = this.ktTypeCache.getRankGrade(taskType, nationRank);
                return grade;
            }
            if (taskType == 7 || taskType == 8) {
                if (taskType == 8 && attType > 0) {
                    return 0;
                }
                return 1;
            }
            else {
                if (taskType == 9) {
                    final int serial = RankService.taskInfo.getSerial();
                    return this.getHJAddExp(serial, forceInfo.getForceId());
                }
                if (taskType == 10 || taskType == 12) {
                    return 1;
                }
                final int forceId2 = forceInfo.getForceId();
                final int nationRank2 = this.getNationRank(forceId2, taskType);
                final int grade2 = this.ktTypeCache.getRankGrade(taskType, nationRank2);
                return grade2;
            }
        }
    }
    
    private int getHJAddExp(final int serial, final int forceId) {
        try {
            if (RankService.taskInfo == null) {
                return 0;
            }
            if (serial == 1) {
                final int num = RankService.taskInfo.getCityNum(forceId, serial);
                return num;
            }
            final int attackCity = RankService.taskInfo.getAttackCityForceId();
            if (attackCity == forceId) {
                return 3;
            }
            return 0;
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
            return 0;
        }
    }
    
    public void nationNextTaskStart() {
        final List<NationTask> list = this.nationTaskDao.getModels();
        this.sendRewardToAll(list);
        this.playerExpandInfoDao.deleteAll();
        RankService.lastSerial[0] = 0;
        RankService.lastSerial[1] = 0;
        RankService.lastSerial[2] = 0;
        if (RankService.isYuanXiaoNight) {
            return;
        }
        this.clearTaskRelative();
        final InMemmoryIndivTaskManager manager = InMemmoryIndivTaskManager.getInstance();
        manager.clearNextTaskStart();
    }
    
    private void sendRewardToAll(final List<NationTask> list) {
        try {
            final List<PlayerExpandInfo> playerList = this.playerExpandInfoDao.getModels();
            final List<TaskKillInfo> taskKillInfos = this.taskKillInfoDao.getModels();
            for (final NationTask task : list) {
                if (task == null) {
                    continue;
                }
                final int isWin = (task.getIswin() == null) ? 0 : task.getIswin();
                final int taskId = task.getNationTaskId();
                final int taskType = getTaskTypeById(taskId);
                if (taskType == 6 || taskType == 7 || taskType == 8 || taskType == 9 || taskType == 10 || taskType == 12) {
                    this.sendReward(task, isWin, playerList);
                }
                else {
                    this.sendReward(task, isWin, taskId, taskType, taskKillInfos);
                }
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("nation task Reward fail...." + e.getMessage());
            RankService.errorLog.error(e.getStackTrace());
        }
    }
    
    private void sendReward(final NationTask task, final int isWin, final List<PlayerExpandInfo> list) {
        try {
            if (list == null || list.isEmpty() || task == null) {
                return;
            }
            final ForceInfo forceInfo = this.forceInfoDao.read(task.getForceid());
            final int taskType = getTaskTypeById(task.getNationTaskId());
            int taskState = this.getTaskSavedState(task.getForceid());
            int showPre = 0;
            if (taskType == 8) {
                taskState = task.getIswin();
            }
            else if (taskType == 7) {
                taskState = isWin;
                showPre = -1;
            }
            else if (taskType == 9 || taskType == 10 || taskType == 12) {
                taskState = 2;
                showPre = -1;
            }
            final int taskId = task.getNationTaskId();
            final KindomLv kindomLv = (KindomLv)this.kingdomLvCache.get((Object)forceInfo.getForceLv());
            final String extra = this.getVicTaskReward(taskId, taskType, kindomLv, showPre);
            for (final PlayerExpandInfo playerExpandInfo : list) {
                if (playerExpandInfo == null) {
                    continue;
                }
                final int taskForceId = task.getForceid();
                if (playerExpandInfo.getIsrewarded() != 0) {
                    continue;
                }
                final int playerId = playerExpandInfo.getPlayerId();
                final Player player = this.playerDao.read(playerId);
                final int playerForceId = player.getForceId();
                if (playerForceId != taskForceId) {
                    continue;
                }
                final int rank = playerExpandInfo.getRank();
                String rewards = this.getNationTaskPersonalReward(task.getNationTaskId(), player, forceInfo.getForceLv(), rank, isWin, 0);
                if (taskState == 2 && !StringUtils.isBlank(extra)) {
                    rewards = RewardType.mergeRewards(rewards, extra);
                }
                if (StringUtils.isBlank(rewards)) {
                    continue;
                }
                RewardType.reward(rewards, this.dataGetter, playerId, 3);
                this.sendTaskMail(task.getNationTaskId(), rewards, playerId);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("sendReward exception");
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
        }
    }
    
    private int getTaskSavedState(final Integer forceid) {
        try {
            final String tasksString = this.getTaskSavedInfo(forceid);
            if (StringUtils.isBlank(tasksString)) {
                return 1;
            }
            final String[] spl = tasksString.split("#");
            if (spl.length < 4) {
                return 1;
            }
            final int taskState = Integer.parseInt(spl[3]);
            return taskState;
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
            return 1;
        }
    }
    
    private void sendReward(final NationTask task, final int isWin, final int taskId, final int taskType, final List<TaskKillInfo> taskKillInfos) {
        try {
            if (taskKillInfos == null || taskKillInfos.isEmpty()) {
                return;
            }
            final boolean except = isWin == 1 && taskType == 1;
            if (isWin == 2 || except) {
                final int forceId = task.getForceid();
                final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
                String extra = "";
                if (isWin == 2 && taskType == 1) {
                    final KindomLv kindomLv = (KindomLv)this.kingdomLvCache.get((Object)forceInfo.getForceLv());
                    final int exp = kindomLv.getRewardChiefExp();
                    final int iron = kindomLv.getRewardIron();
                    extra = this.orgReward(exp, iron, 0);
                }
                for (final TaskKillInfo taskKillInfo : taskKillInfos) {
                    if (taskKillInfo == null) {
                        continue;
                    }
                    final int playerId = taskKillInfo.getPlayerId();
                    final Player player = this.playerDao.read(playerId);
                    if (player == null) {
                        continue;
                    }
                    if (player.getForceId() != forceId) {
                        continue;
                    }
                    if (taskKillInfo.getTaskId() != taskId) {
                        continue;
                    }
                    final int ir = (taskKillInfo == null) ? 0 : taskKillInfo.getIsrewarded();
                    if (ir == 1) {
                        continue;
                    }
                    int parForceId = forceId;
                    if (taskType == 5) {
                        parForceId = 0;
                    }
                    final int rank = RankService.nationTaskKillRanker.getRanker(taskKillInfo.getTaskId()).getRank(1, player.getPlayerId(), parForceId);
                    String rewards = this.getNationTaskPersonalReward(task.getNationTaskId(), player, forceInfo.getForceLv(), rank, isWin, 0);
                    if (taskType != 1) {
                        if (taskType != 5) {
                            final float extraRewards = this.getExtraTaskReward(forceId);
                            if (extraRewards != 0.0f) {
                                final String extras = RewardType.rewardByTimees(rewards, extraRewards);
                                rewards = RewardType.mergeRewards(extras, rewards);
                            }
                        }
                    }
                    else if (isWin == 2) {
                        rewards = RewardType.mergeRewards(rewards, extra);
                    }
                    if (StringUtils.isBlank(rewards)) {
                        continue;
                    }
                    RewardType.reward(rewards, this.dataGetter, playerId, 3);
                    this.sendTaskMail(task.getNationTaskId(), rewards, playerId);
                }
            }
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
        }
    }
    
    public static int getTaskTypeById(final int taskId) {
        if (999 == taskId) {
            return 999;
        }
        if (taskId == 0) {
            return 0;
        }
        return (taskId > 1000) ? (taskId % 100) : ((taskId > 100) ? (taskId % 10) : 1);
    }
    
    @Override
    public void nationTaskTimeIsOver() {
        final long start = System.currentTimeMillis();
        final List<NationTask> list = this.nationTaskDao.getModels();
        if (list == null || list.isEmpty()) {
            return;
        }
        City city = null;
        final boolean[] hasTaskFinish = new boolean[3];
        boolean isAllOver = true;
        final int[] cityNum = new int[3];
        PushTaskOverInfo overInfo = null;
        for (final NationTask task : list) {
            if (task == null) {
                continue;
            }
            if (task.getIswin() == 0) {
                isAllOver = false;
            }
            final int isWin = (task.getIswin() == null) ? 0 : task.getIswin();
            final int forceId = task.getForceid();
            final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
            final int taskId = task.getNationTaskId();
            final int taskType = getTaskTypeById(taskId);
            if (isWin == 0) {
                overInfo = new PushTaskOverInfo();
                boolean isFinish = false;
                if (taskType == 1) {
                    city = CityDataCache.cityArray[task.getTarget()];
                    if (city == null) {
                        continue;
                    }
                    isFinish = city.getForceId().equals(task.getForceid());
                    overInfo.city = task.getTarget();
                    overInfo.attType = task.getAttType();
                }
                else if (taskType == 6) {
                    final int target = task.getTarget();
                    final int curNum = this.cityDataCache.getCityNum(task.getForceid());
                    int winTimes = task.getAttType();
                    if (curNum >= target) {
                        isFinish = true;
                        ++winTimes;
                    }
                    else {
                        --winTimes;
                    }
                    winTimes = ((winTimes < 0) ? -1 : winTimes);
                    this.nationTaskDao.updateAttType(taskId, winTimes);
                    cityNum[forceId - 1] = curNum;
                }
                else if (taskType == 7) {
                    isFinish = (isWin != 0);
                }
                else if (taskType == 8) {
                    final Set<Integer> cities = this.getCurrentTaskCities(task);
                    final boolean hasFinish = this.checkJSBJTaskIsWin(task, forceInfo.getForceLv(), cities);
                    isFinish = ((isWin == 0) ? hasFinish : (isWin == 2));
                }
                else if (taskType == 9) {
                    isFinish = (RankService.taskInfo.getAttackCityForceId() == forceId);
                }
                else if (taskType == 10) {
                    final int serial = taskId % 1000 / 100;
                    isFinish = this.checkNationTaskMiracleIsFinish(serial, forceId);
                    if (RankService.nationMiracle != null) {
                        RankService.nationMiracle.setNationTaskIsOver(forceId, true);
                    }
                }
                else if (taskType == 12) {
                    final int serial = taskId % 1000 / 100;
                    isFinish = this.checkNationTaskFestivalIsFinish(serial, forceId);
                    if (RankService.festival != null) {
                        RankService.festival.setNationTaskIsOver(forceId, true);
                    }
                }
                else {
                    isFinish = true;
                }
                overInfo.force = forceId;
                overInfo.lv = forceInfo.getForceLv();
                overInfo.page = task.getNationTaskId() % 2;
                overInfo.taskType = taskType;
                overInfo.rank = this.getNationRank(forceId, taskType);
                overInfo.serial = taskId % 100 / 10;
                if (isFinish) {
                    overInfo.state = 2;
                    task.setIswin(2);
                    hasTaskFinish[task.getForceid() - 1] = true;
                    this.nationTaskDao.updateIsWin(task.getNationTaskId(), 2);
                    final KindomLv kindomLv = (KindomLv)this.kingdomLvCache.get((Object)forceInfo.getForceLv());
                    final int nowExp = forceInfo.getForceExp();
                    final int maxExp = kindomLv.getExpUpgrade();
                    if (nowExp < maxExp && forceInfo.getForceLv() < this.kingdomLvCache.maxLv) {
                        final int addExp = this.getAddExp(taskType, task.getAttType(), task.getTarget(), forceInfo, kindomLv);
                        final int finalExp = (addExp + nowExp > maxExp) ? maxExp : (addExp + nowExp);
                        if (finalExp > nowExp) {
                            this.forceInfoDao.updateNationExp(forceId, finalExp);
                        }
                    }
                    this.pushNationTaskInfo(overInfo);
                }
                else {
                    overInfo.state = 1;
                    task.setIswin(1);
                    this.nationTaskDao.updateIsWin(task.getNationTaskId(), 1);
                    if (!hasTaskFinish[task.getForceid() - 1]) {
                        this.pushNationTaskInfo(overInfo);
                    }
                }
                if (taskType != 6 && taskType != 8) {
                    this.pushCurNationTaskInfoToPlayer(forceId);
                }
            }
            if (taskType == 7) {
                this.nationtaskSDNextStart(task);
            }
            else if (taskType == 8) {
                if (isWin == 0) {
                    this.nationTaskJSBJOver(task, true);
                }
                else {
                    this.autoRewardAndSave(task, true);
                }
                RankService.needShowNextInvade[forceId - 1] = false;
            }
            else if (taskType == 9) {
                if (isWin == 0) {
                    RankService.errorLog.error("nationtasktimeisover nationTaskHJOver...");
                    this.nationTaskHJOver(task, true);
                }
                else {
                    this.autoRewardAndSave(task, true);
                }
            }
            else if (taskType == 10) {
                if (isWin == 0) {
                    this.nationTaskMiracleOver(task, true);
                }
                else {
                    this.autoRewardAndSave(task, true);
                }
            }
            else {
                if (taskType != 12) {
                    continue;
                }
                if (isWin == 0) {
                    this.nationTaskFestivalOver(task, true);
                }
                else {
                    this.autoRewardAndSave(task, true);
                }
            }
        }
        final NationTask temp = list.get(0);
        if (temp == null) {
            return;
        }
        final int taskType2 = getTaskTypeById(temp.getNationTaskId());
        if (!isAllOver) {
            if (taskType2 == 3 || taskType2 == 7) {
                this.boBaoWhenBarbarainInvadeIsOver();
            }
            final BaseRanker ranker = (RankService.nationTaskKillRanker == null) ? null : RankService.nationTaskKillRanker.getRanker1();
            if (ranker != null && taskType2 != 1 && taskType2 != 6 && taskType2 != 7 && taskType2 != 8 && taskType2 != 9) {
                com.reign.gcld.world.common.RankData rankData = null;
                int first = 0;
                if (taskType2 != 5) {
                    final List<InvestInfo> sortList = new ArrayList<InvestInfo>();
                    for (int i = 1; i <= 3; ++i) {
                        final InvestInfo info = ranker.getKillTotalByForceId(i);
                        sortList.add(info);
                    }
                    Collections.sort(sortList);
                    first = sortList.get(0).forceId;
                    rankData = ranker.getRankNum(first, 0);
                }
                else {
                    rankData = ranker.getRankNum(0, 0);
                }
                if (rankData != null) {
                    final int playerId = rankData.playerId;
                    final Player player = this.playerDao.read(playerId);
                    first = ((taskType2 == 5) ? player.getForceId() : first);
                    final ForceInfo forceInfo2 = this.forceInfoDao.read(first);
                    final NationTask task2 = this.nationTaskDao.getByForce(first);
                    String rewards = this.getNationTaskPersonalReward(task2.getNationTaskId(), player, forceInfo2.getForceLv(), 1, 0, 1);
                    if (taskType2 != 6 && taskType2 != 5) {
                        final float extraRewards = this.getExtraTaskReward(first);
                        if (extraRewards != 0.0f) {
                            final String extra = RewardType.rewardByTimees(rewards, extraRewards);
                            rewards = RewardType.mergeRewards(rewards, extra);
                        }
                    }
                    else {
                        final String extra2 = this.getVicTaskReward(task2.getNationTaskId(), taskType2, (KindomLv)this.kingdomLvCache.get((Object)forceInfo2.getForceLv()), 1);
                        if (!StringUtils.isBlank(extra2)) {
                            rewards = RewardType.mergeRewards(rewards, extra2);
                        }
                    }
                    final String rewarString = RewardType.getRewardsString(rewards);
                    final String taskString = this.getTaskBroadCastTitle(taskType2);
                    final String forceName = WorldCityCommon.nationIdNameMap.get(first);
                    final String msg = MessageFormatter.format(LocalMessages.NATION_TASK_BROADCAST, new Object[] { taskString, ColorUtil.getForceMsg(player.getForceId(), forceName), ColorUtil.getForceMsg(player.getForceId(), player.getPlayerName()), rewarString });
                    this.dataGetter.getChatService().sendBigNotice("GLOBAL", null, msg, null);
                }
            }
        }
        if (taskType2 == 6 && !isAllOver) {
            this.dealExpandTask(taskType2, list, cityNum);
        }
        else if (taskType2 == 7) {
            this.thread = null;
        }
        else if (taskType2 == 4) {
            this.dataGetter.getTimerBattleService().deleteAllTicketArmyAfterNationTaskEnded();
        }
        else if (taskType2 == 9) {
            RankService.taskInfo.setState(2);
            this.thread = null;
        }
        else if (taskType2 == 10) {
            this.thread = null;
            RankService.nationMiracle.changeMiracleState(1);
        }
        else if (taskType2 == 12) {
            this.thread = null;
            RankService.festival.changeMiracleState(1);
            if (RankService.isYuanXiaoNight) {
                RankService.festival.transferPlayerHunger();
            }
        }
        if (getTimeDevision() <= 0) {
            InMemmoryIndivTaskManager.getInstance().clearAfterTaskIsOver();
        }
        RankService.timerLog.info(LogUtil.formatThreadLog("RankService", "nationTaskTimeIsOver", 2, System.currentTimeMillis() - start, ""));
    }
    
    private void nationTaskFestivalOver(final NationTask task, final boolean isTimeOver) {
        try {
            if (task == null || RankService.festival == null) {
                RankService.errorLog.error("nationTaskFestivalOver error...Festival is null ?" + RankService.festival == null);
                return;
            }
            if (isTimeOver) {
                this.autoRewardAndSave(task, isTimeOver);
            }
            final int forceId = task.getForceid();
            final int taskId = task.getNationTaskId();
            final int serial = taskId % 1000 / 100;
            final int attType = (task.getAttType() == null) ? 0 : task.getAttType();
            if (attType == 1) {
                final int addExp = 1;
                this.addNationExp(addExp, forceId);
            }
            final PushTaskOverInfo overInfo = new PushTaskOverInfo();
            overInfo.force = forceId;
            overInfo.taskType = 12;
            overInfo.serial = serial;
            if (task.getIswin() == 2) {
                overInfo.state = 2;
                this.pushNationTaskInfo(overInfo);
            }
            else {
                overInfo.state = 1;
                this.pushNationTaskInfo(overInfo);
            }
            RankService.errorLog.error("festival task is over...forceId:" + forceId + "isFirst:" + (task.getIswin() == 2));
            final int n = serial;
            RankService.festival.getClass();
            if (n < 5 && !isTimeOver) {
                this.autoRewardAndSave(task, false);
                this.nationTaskDao.deleteById(taskId);
                this.taskKillInfoDao.eraseByTaskId(taskId);
                if (RankService.nationTaskKillRanker != null) {
                    RankService.nationTaskKillRanker.clearByForceId(forceId);
                }
                this.duelService.clear();
                final NationTask newTask = new NationTask();
                newTask.setEndtime(task.getEndtime());
                newTask.setForceid(forceId);
                newTask.setIswin(0);
                newTask.setNationTaskId(taskId + 100);
                newTask.setTarget(0);
                newTask.setTaskRelateInfo(task.getTaskRelateInfo());
                this.nationTaskDao.create(newTask);
                RankService.festival.curSerial[forceId - 1] = serial + 1;
                final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
                final KtMrTarget target = this.ktNfCache.getTargetByNationAndLv(forceId, forceInfo.getForceLv(), serial + 1);
                if (RankService.festival != null) {
                    RankService.festival.clearResource(forceId, target);
                }
                else {
                    RankService.errorLog.error("nationMiracle is null");
                }
            }
            else {
                RankService.festival.setNationTaskIsOver(forceId, true);
                InMemmoryIndivTaskManager.getInstance().clearNationIndivTask(forceId);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
        }
    }
    
    private boolean checkNationTaskFestivalIsFinish(final int serial, final int forceId) {
        try {
            if (RankService.festival == null) {
                return false;
            }
            final boolean isFull = RankService.festival.nationResourceFull(forceId);
            if (!isFull) {
                return false;
            }
            final int firstNation = RankService.festival.serialWinner[serial - 1];
            return firstNation == 0;
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
            return false;
        }
    }
    
    private boolean checkNationTaskMiracleIsFinish(final int serial, final int forceId) {
        try {
            if (RankService.nationMiracle == null) {
                return false;
            }
            final boolean isFull = RankService.nationMiracle.nationResourceFull(forceId);
            if (!isFull) {
                return false;
            }
            final int firstNation = RankService.nationMiracle.serialWinner[serial - 1];
            return firstNation == 0;
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
            return false;
        }
    }
    
    private boolean checkJSBJTaskIsWin(final NationTask task, final Integer forceLv, final Set<Integer> cities) {
        try {
            final int timeDivision = getTimeDevision();
            final int taskId = task.getNationTaskId();
            final int taskSerial = taskId % 100 / 10;
            final int forceId = task.getForceid();
            if (taskSerial > 3) {
                return false;
            }
            if (timeDivision <= 0) {
                return true;
            }
            final int armies = RankService.armiesSerial[forceId - 1];
            return armies < 1 || this.isAllForceIdCities(cities, forceId);
        }
        catch (Exception e) {
            RankService.errorLog.error("checkJSBJTaskIsWin ...");
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
            return false;
        }
    }
    
    private Set<Integer> getCurrentTaskCities(final NationTask task) {
        try {
            if (task == null || task.getTaskRelateInfo() == null) {
                return null;
            }
            Set<Integer> result = null;
            final String[] single = task.getTaskRelateInfo().split(";");
            if (single.length > 1) {
                final String[] cell = single[1].split(",");
                for (int i = 0; i < cell.length; ++i) {
                    if (result == null) {
                        result = new HashSet<Integer>();
                    }
                    result.add(Integer.parseInt(cell[i]));
                }
            }
            return result;
        }
        catch (Exception e) {
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
            return null;
        }
    }
    
    private boolean isAllForceIdCities(final Set<Integer> cities, final int forceId) {
        if (cities == null || cities.isEmpty()) {
            return true;
        }
        for (final Integer id : cities) {
            final City city = CityDataCache.cityArray[id];
            if (city == null) {
                continue;
            }
            if (city.getForceId() != forceId) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public void boBaoWhenBarbarainInvadeIsOver() {
        try {
            this.dataGetter.getChatService().sendBigNotice("GLOBAL", null, LocalMessages.MANZU_INVADE_OVER_BOBAO_MSG_FORMAT, null);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("RankService.boBaoWhenBarbarainInvadeIsOver catch exception", e);
        }
    }
    
    private void dealExpandTask(final int taskType, final List<NationTask> list, final int[] cityNum) {
        if (taskType == 6) {
            final long startTime = this.getStartTime();
            final long lastTime = System.currentTimeMillis() - startTime;
            final KtKjS curSerial = this.ktKjSCache.getCurSerial(lastTime);
            if (curSerial == null) {
                return;
            }
            if (curSerial.getId() > 2) {
                this.sendRewardToAll(list);
            }
            this.savePlayerInfo();
            this.saveCityInfo(list, cityNum);
            if (lastTime < 5400000L) {
                this.clearTaskRelative();
                this.startNationTaskByType(6, getTimeDevision());
            }
        }
    }
    
    private String getTaskBroadCastTitle(final int taskType) {
        switch (taskType) {
            case 1: {
                return LocalMessages.NATION_TASK_1;
            }
            case 2: {
                return LocalMessages.NATION_TASK_2;
            }
            case 3: {
                return LocalMessages.NATION_TASK_3;
            }
            case 4: {
                return LocalMessages.NATION_TASK_4;
            }
            case 5: {
                return LocalMessages.NATION_TASK_5;
            }
            case 6: {
                return LocalMessages.NATION_TASK_6;
            }
            default: {
                return "";
            }
        }
    }
    
    private long getStartTime() {
        final int timeDevision = getTimeDevision();
        if (timeDevision == 1 || timeDevision == 3 || timeDevision == 5) {
            return this.getStartTime(timeDevision).getTime();
        }
        return -1L;
    }
    
    private void clearTaskRelative() {
        this.nationTaskDao.deleteAllTasks();
        this.taskKillInfoDao.deleteAllInfos();
        if (RankService.nationTaskKillRanker != null) {
            RankService.nationTaskKillRanker.clear();
        }
        this.duelService.clear();
    }
    
    @Override
    public void clearWholeKill() {
        final long start = System.currentTimeMillis();
        if (RankService.wholeKillRank == null) {
            return;
        }
        this.wholeKillDao.updateWholeKill();
        for (final com.reign.gcld.world.common.RankData rd : RankService.wholeKillRank.getRankList(1)) {
            final int rank = RankService.wholeKillRank.getRank(1, rd.playerId, 1);
            if (rank > 0) {
                this.wholeKillDao.updateKillRank(rd.playerId, rank);
            }
        }
        for (final com.reign.gcld.world.common.RankData rd : RankService.wholeKillRank.getRankList(2)) {
            final int rank = RankService.wholeKillRank.getRank(1, rd.playerId, 2);
            if (rank > 0) {
                this.wholeKillDao.updateKillRank(rd.playerId, rank);
            }
        }
        for (final com.reign.gcld.world.common.RankData rd : RankService.wholeKillRank.getRankList(3)) {
            final int rank = RankService.wholeKillRank.getRank(1, rd.playerId, 3);
            if (rank > 0) {
                this.wholeKillDao.updateKillRank(rd.playerId, rank);
            }
        }
        for (int i = 1; i <= 3; ++i) {
            final com.reign.gcld.world.common.RankData rd2 = RankService.wholeKillRank.getRankNum(i, 0);
            if (rd2 != null) {
                final Object object = this.kingdomTaskRankingCache.getTaskRanking(1, this.forceInfoDao.read(i).getForceLv(), 999, -1).result1;
                if (object != null) {
                    final int food = (int)object;
                    final String msg = MessageFormatter.format(LocalMessages.WHOLE_KILL_FIRST, new Object[] { ColorUtil.getSpecialColorMsg(this.playerDao.getPlayerName(rd2.playerId)), rd2.value, food });
                    this.chatService.sendSystemChat("GLOBAL", 0, i, msg, null);
                }
            }
        }
        RankService.wholeKillRank.clear();
        RankService.timerLog.info(LogUtil.formatThreadLog("RankService", "clearWholeKill", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Override
    public void clearScoreRank() {
    }
    
    private void saveCityInfo(final List<NationTask> list, final int[] cityNum) {
        try {
            for (final NationTask task : list) {
                final int forceId = task.getForceid();
                final String taskInfo = this.orgLastTaskInfo(task.getTarget(), cityNum[forceId - 1], task.getAttType(), task.getIswin());
                final String value = this.infos[forceId - 1];
                Configuration.saveProperties(value, taskInfo, "serverstate.properties");
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("saveCityInfo exception..", e);
        }
    }
    
    private String orgLastTaskInfo(final Integer target, final int cityNum, final Integer winTimes, final int isWin) {
        return target + "#" + cityNum + "#" + winTimes + "#" + isWin;
    }
    
    private void savePlayerInfo() {
        try {
            this.playerExpandInfoDao.deleteAll();
            final List<TaskKillInfo> taskKillInfos = this.taskKillInfoDao.getModels();
            final List<PlayerExpandInfo> listToRestore = new ArrayList<PlayerExpandInfo>();
            final Set<Integer> idSet = new HashSet<Integer>();
            for (final TaskKillInfo taskKillInfo : taskKillInfos) {
                final int playerId = taskKillInfo.getPlayerId();
                if (idSet.contains(playerId)) {
                    continue;
                }
                final Player player = this.playerDao.read(playerId);
                final PlayerExpandInfo playerExpandInfo = new PlayerExpandInfo();
                playerExpandInfo.setIsrewarded(taskKillInfo.getIsrewarded());
                playerExpandInfo.setForceid(player.getForceId());
                playerExpandInfo.setKillnum(taskKillInfo.getKillnum());
                playerExpandInfo.setPlayerId(playerId);
                playerExpandInfo.setRank(RankService.nationTaskKillRanker.getRanker1().getRank(1, playerId, taskKillInfo.getTaskId() / 100));
                listToRestore.add(playerExpandInfo);
                idSet.add(playerId);
            }
            if (!listToRestore.isEmpty()) {
                this.playerExpandInfoDao.batchCreate(listToRestore);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
        }
    }
    
    private String getNationTaskPersonalReward(final Integer nationTaskId, final Player player, Integer forceLv, final int rank, final int isWin, final int showPre) {
        try {
            if (nationTaskId == 0) {
                if (isWin == 2) {
                    forceLv = ((forceLv <= 1) ? 1 : (forceLv - 1));
                }
                final BarbarainRanking ranking = this.barbarianRankingCache.getTaskRanking(rank, forceLv);
                if (ranking == null) {
                    return "";
                }
                final int iron = ranking.getRewardIron();
                final int exp = ranking.getRewardExp();
                return this.orgReward(exp, iron, 0);
            }
            else {
                final int taskType = getTaskTypeById(nationTaskId);
                int nationRank = 0;
                if (taskType != 1) {
                    nationRank = this.getNationRank(player.getForceId(), taskType);
                }
                if (taskType == 5) {
                    forceLv = this.getChampionForceLv(nationTaskId);
                }
                else if (taskType == 9) {
                    nationRank = ((RankService.taskInfo == null) ? 1 : RankService.taskInfo.getSerial());
                }
                else if (taskType == 10 || taskType == 12) {
                    final int taskSerial = nationTaskId % 1000 / 100;
                    final int timeDivision = getTimeDevision();
                    int serial = (showPre == 0) ? (taskSerial - 1) : taskSerial;
                    serial = (nationRank = ((timeDivision <= 0) ? taskSerial : serial));
                }
                final MultiResult result = this.kingdomTaskRankingCache.getTaskRanking(rank, forceLv, taskType, nationRank);
                if (result == null) {
                    return "";
                }
                double rate = 0.0;
                int iron2 = 0;
                int exp2 = 0;
                iron2 = (int)((result.result1 == null) ? 0 : result.result1);
                exp2 = (int)((result.result2 == null) ? 0 : result.result2);
                if (taskType == 6) {
                    rate = this.getExpandRewardRate(nationTaskId, 0, showPre);
                    iron2 = (int)(Object)Double.valueOf(iron2 * rate);
                    exp2 = (int)(Object)Double.valueOf(exp2 * rate);
                }
                if (RankService.hasDoubleReward) {
                    exp2 *= 2;
                }
                final String normalReward = this.orgReward(exp2, iron2, 0);
                final int ItemId = (int)((result.result3 == null) ? 0 : result.result3);
                String diamondReward = "";
                if (ItemId > 0 && this.dataGetter.getDiamondShopService().canRecvDropProps(player.getPlayerId(), ItemId)) {
                    diamondReward = ((result.result3 == null) ? "" : (result.result3 + "," + result.result4));
                }
                return String.valueOf(normalReward) + ";" + diamondReward;
            }
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
            return "";
        }
    }
    
    private Integer getChampionForceLv(final Integer nationTaskId) {
        final int maxLv = this.forceInfoDao.getMaxLv();
        return (maxLv <= 0) ? 1 : maxLv;
    }
    
    private double getExpandRewardRate(final Integer nationTaskId, final int type, final int showPre) {
        final NationTask task = this.nationTaskDao.read(nationTaskId);
        if (task == null) {
            return 1.0;
        }
        final int winTimes = this.getWintimesByShowPre(showPre, task);
        KtKjS ktKjS = null;
        if (!this.isDuringNationTask()) {
            ktKjS = this.ktKjSCache.getLastSerial();
        }
        else if (showPre == 0) {
            final int serial = task.getNationTaskId() % 100 / 10;
            ktKjS = (KtKjS)this.ktKjSCache.get((Object)(serial - 1));
        }
        else {
            final int serial = nationTaskId % 100 / 10;
            ktKjS = (KtKjS)this.ktKjSCache.get((Object)serial);
        }
        final KtKjT ktKjT = this.ktKjRelativeCache.getKtKjTByWinTimes(winTimes);
        double sRate = 0.0;
        double tRate = 0.0;
        if (type == 0) {
            sRate = ((ktKjS == null) ? 1.0 : ktKjS.getReR());
            tRate = ((ktKjT == null) ? 1.0 : ktKjT.getReR());
        }
        else {
            sRate = ((ktKjS == null) ? 1.0 : ktKjS.getReT());
            tRate = ((ktKjT == null) ? 1.0 : ktKjT.getReT());
        }
        return sRate * tRate;
    }
    
    private boolean isDuringNationTask() {
        final int timeDivision = getTimeDevision();
        return timeDivision == 1 || timeDivision == 3 || timeDivision == 5;
    }
    
    private int getWintimesByShowPre(final int showPre, final NationTask task) {
        if (showPre == 0) {
            final int winTimes = this.getWinTimesBySavedInfo(task.getForceid());
            return winTimes;
        }
        return task.getAttType();
    }
    
    private int getWinTimesBySavedInfo(final Integer forceid) {
        final String taskInfos = this.getTaskSavedInfo(forceid);
        if (StringUtils.isBlank(taskInfos)) {
            return 0;
        }
        final String[] infos = taskInfos.split("#");
        int result = 0;
        if (infos.length >= 3) {
            result = Integer.parseInt(infos[2]);
        }
        return result;
    }
    
    private int getWinTimesBySavedInfoAndIsWin(final Integer forceid) {
        final String taskInfos = this.getTaskSavedInfo(forceid);
        if (StringUtils.isBlank(taskInfos)) {
            return 0;
        }
        final String[] infos = taskInfos.split("#");
        int winTimes = 0;
        int isWin = 0;
        if (infos.length >= 4) {
            winTimes = Integer.parseInt(infos[2]);
            isWin = Integer.parseInt(infos[3]);
        }
        winTimes = ((isWin == 2) ? (winTimes + 1) : (winTimes - 1));
        return (winTimes < 0) ? -1 : winTimes;
    }
    
    private int getNationRank(final Integer forceId, final int nationTaskType) {
        if (RankService.nationTaskKillRanker == null) {
            return 0;
        }
        final BaseRanker baseRanker = RankService.nationTaskKillRanker.getRanker1();
        if (baseRanker == null) {
            return 0;
        }
        if (nationTaskType != 1 && nationTaskType != 5 && nationTaskType != 7) {
            final List<InvestInfo> infos = new ArrayList<InvestInfo>();
            for (int i = 1; i <= 3; ++i) {
                final InvestInfo info = RankService.nationTaskKillRanker.getRanker1().getKillTotalByForceId(i);
                infos.add(info);
            }
            Collections.sort(infos);
            for (int i = 1; i <= 3; ++i) {
                final InvestInfo info = infos.get(i - 1);
                if (info.forceId == forceId) {
                    return i;
                }
            }
            return 0;
        }
        if (nationTaskType == 5) {
            com.reign.gcld.world.common.RankData rankData = null;
            Player player = null;
            int nationRank = 0;
            for (int j = 0; j < 3; ++j) {
                rankData = baseRanker.getRankNum(0, j);
                if (rankData != null) {
                    final int playerId = rankData.playerId;
                    player = this.playerDao.read(playerId);
                    if (player.getForceId() == forceId) {
                        nationRank = j + 1;
                        break;
                    }
                }
            }
            return nationRank;
        }
        return 0;
    }
    
    @Override
    public void scanNationTask() {
        final long start = System.currentTimeMillis();
        final boolean isInKf = this.checkIsInKf();
        if (isInKf) {
            return;
        }
        final int timeDivision = getTimeDevision();
        final List<NationTask> tasks = this.nationTaskDao.getModels();
        if (timeDivision == 1 || timeDivision == 3 || timeDivision == 5) {
            if (tasks == null || tasks.size() == 0) {
                RankService.errorLog.error("NATIONTASK-------startNationtasks coz tasks is null");
                this.startNationTasks();
            }
            else {
                for (final NationTask task : tasks) {
                    final Date enDate = task.getEndtime();
                    if (timeDivision != getTimeDevision(enDate)) {
                        RankService.errorLog.error("NATIONTASK-------startNationtasks coz timeDivision is NOT RIGHT");
                        this.startNationTasks();
                        break;
                    }
                }
            }
        }
        else {
            this.isHalfHourBeforeNationTask();
            if (tasks != null && !tasks.isEmpty()) {
                final NationTask temp = tasks.get(0);
                final int taskId = temp.getNationTaskId();
                final int taskType = getTaskTypeById(taskId);
                if (taskType != 7 && taskType != 8 && taskType != 9 && taskType != 10 && taskType != 12) {
                    this.nationTaskTimeIsOver();
                }
            }
        }
        RankService.timerLog.info(LogUtil.formatThreadLog("RankService", "scanNationTask", 2, System.currentTimeMillis() - start, ""));
    }
    
    private boolean checkIsInKf() {
        try {
            final Date now = new Date();
            final boolean isKfGz = this.kfgzSeasonService.isInBattleDay(now);
            return isKfGz;
        }
        catch (Exception e) {
            RankService.errorLog.error("checkIsInKf fail...");
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
            return false;
        }
    }
    
    @Override
    public void checkInvestSerialChange() {
        final long start = System.currentTimeMillis();
        final boolean isInKf = this.checkIsInKf();
        if (isInKf) {
            return;
        }
        final int taskType = this.hasNationTasks(1);
        if (taskType == 4) {
            final NationTask task = this.nationTaskDao.getByForce(1);
            final int taskId = task.getNationTaskId();
            final int event = taskId % 100 / 10;
            final Date date = task.getEndtime();
            final long startTime = date.getTime() - 5400000L;
            final long lastTime = System.currentTimeMillis() - startTime;
            final KtTzEv ktTzEv = this.ktTzEvCache.getCurSerial(lastTime, event);
            if (ktTzEv == null) {
                return;
            }
            if (!task.getTarget().equals(ktTzEv.getCm())) {
                for (int i = 1; i <= 3; ++i) {
                    this.nationTaskDao.updateTarget(i, ktTzEv.getCm());
                }
                for (int i = 1; i <= 3; ++i) {
                    this.pushCurNationTaskInfoToPlayer(i);
                }
            }
        }
        else if (taskType == 3) {
            final NationTask task = this.nationTaskDao.getByForce(1);
            if (task == null) {
                return;
            }
            final long startTime2 = task.getEndtime().getTime() - 5400000L;
            final Tuple<Integer, Long> nextTime = this.ktMzSCache.getNextInvadeInfo(startTime2);
            if (nextTime == null) {
                return;
            }
            for (int j = 1; j <= 3; ++j) {
                this.pushCurNationTaskInfoToPlayer(j);
            }
        }
        else if (taskType == 6) {
            final NationTask task = this.nationTaskDao.getByForce(1);
            if (task == null) {
                return;
            }
            final int cur = this.curSerial();
            final int serial = task.getNationTaskId() % 100 / 10;
            if (serial < cur) {
                this.nationTaskTimeIsOver();
            }
        }
        else if (taskType == 8) {
            final List<NationTask> tasks = this.nationTaskDao.getModels();
            final long now = System.currentTimeMillis();
            for (final NationTask task2 : tasks) {
                if (task2 != null) {
                    if (task2.getIswin() != 0) {
                        continue;
                    }
                    final int taskId2 = task2.getNationTaskId();
                    final int serial2 = taskId2 % 100 / 10;
                    final int forceId = task2.getForceid();
                    if (serial2 > 3) {
                        continue;
                    }
                    this.pushCurNationTaskInfoToPlayer(forceId);
                    if (now >= RankService.nextLegion[forceId - 1] - 60000L && now < RankService.nextLegion[forceId - 1]) {
                        RankService.nextLegionMessage[forceId - 1] = true;
                        RankService.errorLog.error("nextLegion True: " + new Date());
                    }
                    final Tuple<Integer, Long> result = this.ktBjCache.getNextInvadeInfo(this.getStartTime());
                    if (result == null) {
                        continue;
                    }
                    final long nextTime2 = result.right;
                    if (nextTime2 > 60000L) {
                        continue;
                    }
                    final String msg = LocalMessages.CHAT_JSBJ_BARBARAIN;
                    this.chatService.sendSystemChat("GLOBAL", 0, forceId, msg, null);
                }
            }
        }
        RankService.timerLog.info(LogUtil.formatThreadLog("RankService", "checkInvestSerialChange", 2, System.currentTimeMillis() - start, ""));
    }
    
    private boolean checkHasFinished(final int serial) {
        boolean tempResult = true;
        try {
            if (serial == 1) {
                final int[] array = RankService.taskInfo.getCityBelong();
                for (int i = 0; i < array.length; ++i) {
                    if (array[i] == 104) {
                        tempResult = false;
                    }
                }
            }
            else if (serial == 2) {
                tempResult = (RankService.taskInfo.getAttackCityForceId() != 104);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
            tempResult = false;
        }
        return tempResult;
    }
    
    private void nationtaskSDNextStart(final NationTask task) {
        try {
            if (task == null) {
                return;
            }
            final int serial = task.getNationTaskId() % 100 / 10;
            final int forceId = task.getForceid();
            if (RankService.lastSerial[forceId - 1] == serial) {
                RankService.errorLog.error("same serial do nationtaskSDNextStart again...+serial:" + serial);
                return;
            }
            if (serial >= 2) {
                final List<PlayerExpandInfo> list = this.playerExpandInfoDao.getByForceId(forceId);
                this.sendReward(task, 2, list);
            }
            this.savePlayerInfo(task);
            RankService.lastSerial[forceId - 1] = serial;
            final long now = System.currentTimeMillis();
            final long endTime = task.getEndtime().getTime();
            if (serial < this.ktSdmzSCache.getMaxRound() && now <= endTime) {
                final int taskId = task.getNationTaskId();
                this.nationTaskDao.deleteById(task.getNationTaskId());
                this.taskKillInfoDao.eraseByTaskId(taskId);
                if (RankService.nationTaskKillRanker != null) {
                    RankService.nationTaskKillRanker.clearByForceId(forceId);
                }
                this.duelService.clear();
                this.startNextSDTask(task);
            }
            else {
                InMemmoryIndivTaskManager.getInstance().clearNationIndivTask(forceId);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
        }
    }
    
    private void savePlayerInfo(final NationTask task) {
        this.playerExpandInfoDao.eraseByForceId(task.getForceid());
        if (RankService.nationTaskKillRanker == null) {
            RankService.errorLog.error("save playerInfo fail...taskId:" + task.getNationTaskId());
            return;
        }
        final int forceId = task.getForceid();
        final List<TaskKillInfo> taskKillInfos = this.taskKillInfoDao.getByTaskId(task.getNationTaskId());
        final List<PlayerExpandInfo> toStore = new ArrayList<PlayerExpandInfo>();
        PlayerExpandInfo playerExpandInfo = null;
        List<PlayerHunger> toUpdate = null;
        List<PlayerHunger> toCreate = null;
        PlayerHunger playerHunger = null;
        for (final TaskKillInfo killInfo : taskKillInfos) {
            if (killInfo == null) {
                continue;
            }
            final int playerId = killInfo.getPlayerId();
            playerExpandInfo = new PlayerExpandInfo();
            playerExpandInfo.setForceid(forceId);
            playerExpandInfo.setIsrewarded(killInfo.getIsrewarded());
            playerExpandInfo.setKillnum(killInfo.getKillnum());
            playerExpandInfo.setPlayerId(playerId);
            playerExpandInfo.setRank(RankService.nationTaskKillRanker.getRanker1().getRank(1, playerId, forceId));
            toStore.add(playerExpandInfo);
            if (!RankService.isYuanXiao || RankService.festival == null) {
                continue;
            }
            toCreate = new ArrayList<PlayerHunger>();
            toUpdate = new ArrayList<PlayerHunger>();
            playerHunger = new PlayerHunger();
            playerHunger.setPlayerId(playerId);
            playerHunger.setHunger(1);
            if (RankService.festival.getPlayerHungerMap().containsKey(playerId)) {
                toUpdate.add(playerHunger);
            }
            else {
                toCreate.add(playerHunger);
            }
        }
        try {
            if (toStore != null && !toStore.isEmpty()) {
                this.playerExpandInfoDao.batchCreate(toStore);
            }
            if (toUpdate != null && !toUpdate.isEmpty()) {
                final int number = this.playerHungerDao.batchUpdate(toUpdate);
                RankService.festival.updatePlayerHunger(number, toUpdate);
            }
            if (toCreate != null && !toCreate.isEmpty()) {
                final int number = this.playerHungerDao.batchCreate(toCreate);
                RankService.festival.updatePlayerHunger(number, toCreate);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
        }
    }
    
    private void startNextSDTask(final NationTask task) {
        if (task == null) {
            return;
        }
        int serial = task.getNationTaskId() % 100 / 10;
        final int forceId = task.getForceid();
        if (serial >= this.ktSdmzSCache.getMaxRound()) {
            return;
        }
        final NationTask toCreate = new NationTask();
        ++serial;
        toCreate.setNationTaskId(forceId * 100 + serial * 10 + 7);
        toCreate.setForceid(forceId);
        toCreate.setIswin(0);
        toCreate.setTarget(0);
        final Date endTime = this.getEndTime(getTimeDevision());
        toCreate.setEndtime(endTime);
        toCreate.setAttType(0);
        this.nationTaskDao.create(toCreate);
        this.pushCurNationTaskInfoToPlayer(forceId);
        this.timerBattleService.addRoundSaoDangManZu(forceId, serial);
        final String name = (forceId == 1) ? LocalMessages.T_FORCE_BEIDI : ((forceId == 2) ? LocalMessages.T_FORCE_XIRONG : LocalMessages.T_FORCE_DONGYI);
        final String msg = MessageFormatter.format(LocalMessages.SD_HAS_COMED, new Object[] { name });
        this.dataGetter.getChatService().sendBigNotice("COUNTRY", new PlayerDto(1, forceId), msg, null);
    }
    
    private int curSerial() {
        final long now = System.currentTimeMillis();
        final int timeDivision = getTimeDevision();
        if (timeDivision != 1 && timeDivision != 3 && timeDivision != 5) {
            return this.ktKjSCache.getLastSerial().getId();
        }
        final long startTime = this.getStartTime(timeDivision).getTime();
        final long lastTime = now - startTime;
        final KtKjS kjS = this.ktKjSCache.getCurSerial(lastTime);
        if (kjS == null) {
            return 0;
        }
        return kjS.getId();
    }
    
    private int getCurSerial() {
        final long now = System.currentTimeMillis();
        final int timeDivision = getTimeDevision();
        if (timeDivision != 1 && timeDivision != 3 && timeDivision != 5) {
            RankService.errorLog.error("startNationTasks wrong timeDivision..." + timeDivision);
            return 0;
        }
        final long startTime = this.getStartTime(timeDivision).getTime();
        final long lastTime = now - startTime;
        final KtKjS kjS = this.ktKjSCache.getCurSerial(lastTime);
        if (kjS == null) {
            RankService.errorLog.error("getCurSerial  serial wrong kjs is null..." + lastTime);
            return 1;
        }
        RankService.errorLog.error("getCurSerial log for check...lastTime:" + lastTime + "id:" + kjS.getId());
        return kjS.getId();
    }
    
    private void isHalfHourBeforeNationTask() {
        final String startServer = Configuration.getProperty("gcld.server.time");
        if (this.playerService.isSecond(startServer) > 0L) {
            return;
        }
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        final int hour = calendar.get(11);
        final int minute = calendar.get(12);
        if (minute >= 30) {
            String msg = "";
            if (hour == RankService.TASK_TIME1[0].getHour() - 1) {
                msg = MessageFormatter.format(LocalMessages.BEFORE_NATION_TASK_BROADCAST, new Object[] { hour + 1 });
                this.chatService.sendSystemChat("GLOBAL", 0, 0, msg, null);
            }
            else if (hour == RankService.TASK_TIME2[0].getHour() - 1) {
                msg = MessageFormatter.format(LocalMessages.BEFORE_NATION_TASK_BROADCAST, new Object[] { hour + 1 });
                this.chatService.sendSystemChat("GLOBAL", 0, 0, msg, null);
            }
            else if (hour == RankService.TASK_TIME3[0].getHour() - 1) {
                msg = MessageFormatter.format(LocalMessages.BEFORE_NATION_TASK_BROADCAST, new Object[] { hour + 1 });
                this.chatService.sendSystemChat("GLOBAL", 0, 0, msg, null);
            }
        }
    }
    
    @Override
    public int hasBarTasks(final int forceId) {
        try {
            final ForceInfo info = this.forceInfoDao.read(forceId);
            if (info == null) {
                return 0;
            }
            final long now = System.currentTimeMillis();
            final long endTime = (info.getEndtime() == null) ? now : info.getEndtime().getTime();
            if (endTime <= now) {
                return 0;
            }
            final int isWin = (info.getIswin() == null) ? 0 : info.getIswin();
            if (isWin != 0) {
                return 0;
            }
            return info.getForceLv();
        }
        catch (Exception e) {
            RankService.errorLog.error("hasBarTasks error ..forceId " + forceId, e);
            return 0;
        }
    }
    
    public boolean hasBarTask(final int forceId) {
        final ForceInfo info = this.forceInfoDao.read(forceId);
        return info != null && info.getEndtime() != null;
    }
    
    @Override
    public int hasNationTasks(final int forceId) {
        try {
            final List<NationTask> list = this.nationTaskDao.getListByForce(forceId);
            if (list == null || list.size() == 0) {
                return 0;
            }
            if (list.size() == 2) {
                for (final NationTask task : list) {
                    if (task != null && task.getIswin() == 0) {
                        return 1;
                    }
                }
                return 0;
            }
            NationTask task = list.get(0);
            if (task == null) {
                return 0;
            }
            final int taskId = task.getNationTaskId();
            if (taskId <= 100) {
                RankService.errorLog.error("NationTaskId is Wrong !!! " + taskId);
                return 0;
            }
            int taskType = getTaskTypeById(taskId);
            if (taskType == 7 || taskType == 8 || taskType == 9 || taskType == 10 || taskType == 12) {
                final int timeDivision = getTimeDevision();
                if (timeDivision == 1 || timeDivision == 3 || timeDivision == 5) {
                    return taskType;
                }
                return 0;
            }
            else {
                if (task.getIswin() == 0) {
                    taskType = getTaskTypeById(taskId);
                    return taskType;
                }
                return 0;
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("hasNationTasks error forceId:" + forceId, e);
            return 0;
        }
    }
    
    public boolean hasInvestNationTaskNotFailure(final int forceId) {
        final List<NationTask> tasks = this.nationTaskDao.getModels();
        if (tasks.size() == 6) {
            return false;
        }
        for (final NationTask temp : tasks) {
            if (temp.getForceid() == forceId) {
                return temp.getIswin() != 1;
            }
        }
        return false;
    }
    
    public static boolean isBarCity(final int cityId) {
        for (int i = 0; i < RankService.barCity.length; ++i) {
            if (cityId == RankService.barCity[i]) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public byte[] getPersonalInvestmentInfo(final PlayerDto playerDto) {
        final boolean hasInvestNationTask = this.hasInvestNationTaskNotFailure(playerDto.forceId);
        if (!hasInvestNationTask) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_INVEST_TASK);
        }
        if (playerDto.playerLv < 30) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        final NationTask task = this.nationTaskDao.getByForce(playerDto.forceId);
        if (task == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_INVEST_TASK);
        }
        final long total = RankService.nationTaskKillRanker.getRanker1().getKillTotalByForceId(playerDto.forceId).investNum;
        final long now = System.currentTimeMillis();
        final TaskKillInfo taskKillInfo = this.taskKillInfoDao.getTaskKillInfo(playerDto.playerId);
        long cd = (taskKillInfo == null) ? now : taskKillInfo.getUpdatetime();
        cd = ((cd - now < 0L) ? 0L : (cd - now));
        final int killNum = (taskKillInfo == null) ? 0 : taskKillInfo.getKillnum();
        final int taskId = task.getNationTaskId();
        final int event = taskId % 100 / 10;
        final Date date = task.getEndtime();
        final long startTime = date.getTime() - 5400000L;
        final long lastTime = System.currentTimeMillis() - startTime;
        KtTzEv ktTzEv = null;
        boolean isOver = false;
        if (task.getIswin() == 0) {
            ktTzEv = this.ktTzEvCache.getCurSerial(lastTime, event);
        }
        else if (task.getIswin() == 2) {
            ktTzEv = this.ktTzEvCache.getLastSerial(event);
            isOver = true;
        }
        if (ktTzEv == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_INVEST_TASK);
        }
        long nextTime = 0L;
        if (!isOver) {
            final KtTzEv nextEv = this.ktTzEvCache.getNextSerial(ktTzEv.getId());
            if (nextEv != null) {
                final int nextMinutes = nextEv.getT();
                nextTime = this.getNextSerialTime(nextMinutes, lastTime);
            }
        }
        final int copper = ktTzEv.getCc();
        final int exp = ktTzEv.getEr();
        int couponNum = 0;
        List<StoreHouse> shList = null;
        try {
            shList = this.storeHouseDao.getByItemId(playerDto.playerId, 401, 5);
        }
        catch (Exception e) {
            RankService.errorLog.error("getPersonalInvestmentInfo exception...playerId:" + playerDto.playerId);
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
        }
        couponNum = ((shList == null || shList.size() <= 0) ? 0 : shList.get(0).getNum());
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("goal", task.getTarget());
        doc.createElement("curNum", total);
        doc.createElement("selfInvestment", killNum);
        doc.createElement("percentage", (int)Math.min(100.0, total * 1.0 / task.getTarget() * 100.0));
        doc.createElement("cd", cd);
        doc.createElement("nextTime", nextTime);
        doc.createElement("copper", copper);
        doc.createElement("exp", exp);
        doc.createElement("cdToUnable", ktTzEv.getCdMax() * 1000L);
        doc.createElement("name", ktTzEv.getName());
        doc.createElement("pic", ktTzEv.getPic());
        doc.createElement("couponNum", couponNum);
        doc.createElement("intro", ktTzEv.getIntro());
        doc.createElement("nationRank", this.getNationRank(playerDto.forceId, 6));
        doc.createElement("isLast", this.ktTzEvCache.isLastSerial(ktTzEv, event));
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private long getNextSerialTime(final int nextMinutes, final long lastTime) {
        return nextMinutes * 60000L - lastTime;
    }
    
    @Override
    public byte[] investCopper(final PlayerDto playerDto) {
        final int forceId = playerDto.forceId;
        final int taskType = this.hasNationTasks(forceId);
        if (playerDto.playerLv < 30) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        if (taskType != 4) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_INVEST_TASK);
        }
        final TaskKillInfo taskKillInfo = this.taskKillInfoDao.getTaskKillInfo(playerDto.playerId);
        final long now = System.currentTimeMillis();
        long cd = (taskKillInfo == null) ? now : taskKillInfo.getUpdatetime();
        cd = Math.max(now, cd);
        final NationTask task = this.nationTaskDao.getByForce(forceId);
        final int taskId = task.getNationTaskId();
        final int event = taskId % 100 / 10;
        final Date date = task.getEndtime();
        final long startTime = date.getTime() - 5400000L;
        final long lastTime = System.currentTimeMillis() - startTime;
        final KtTzEv ktTzEv = this.ktTzEvCache.getCurSerial(lastTime, event);
        if (ktTzEv == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_INVEST_TASK);
        }
        if (cd > now + ktTzEv.getCdMax() * 1000L) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.INVEST_IS_IN_CD);
        }
        final int copper = ktTzEv.getCc();
        final int exp = ktTzEv.getEr();
        long delay = 60000L;
        final List<StoreHouse> shList = this.storeHouseDao.getByItemId(playerDto.playerId, 401, 5);
        final StoreHouse storeHouse = (shList == null || shList.size() <= 0) ? null : shList.get(0);
        int inUse = (storeHouse == null) ? 0 : storeHouse.getNum();
        int copperExtra = (inUse > 0) ? copper : 0;
        synchronized (this) {
            final long curTotal = RankService.nationTaskKillRanker.getRanker1().getKillTotalByForceId(forceId).investNum;
            if (curTotal + copper + copperExtra > task.getTarget()) {
                if (curTotal + copper > task.getTarget()) {
                    // monitorexit(this)
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.CUR_TOTAL_IS_FULL);
                }
                inUse = 0;
                copperExtra = 0;
            }
            delay = ktTzEv.getCd() * 1000L;
            if (!this.playerResourceDao.consumeCopper(playerDto.playerId, copper, "\u5bcc\u7532\u5929\u4e0b\u6295\u8d44\u82b1\u8d39\u94f6\u5e01")) {
                // monitorexit(this)
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10001);
            }
            this.updateKillNum(4, copper + copperExtra, playerDto.playerId, cd + delay);
        }
        if (inUse > 0) {
            if (inUse > 1) {
                this.storeHouseDao.reduceNum(storeHouse.getVId(), 1);
            }
            else {
                this.storeHouseDao.deleteById(storeHouse.getVId());
            }
        }
        this.dataPushCenterUtil.addInvestCd(playerDto.playerId, cd + delay);
        this.dataGetter.getIndividualTaskService().sendTaskMessage(playerDto, 1, "tznum");
        this.dataGetter.getIndividualTaskService().sendTaskMessage(playerDto, copper + copperExtra, "tzyb");
        final int expExtra = (inUse > 0) ? exp : 0;
        this.playerService.updateExpAndPlayerLevel(playerDto.playerId, exp + expExtra, "\u5bcc\u7532\u5929\u4e0b\u6295\u8d44\u589e\u52a0\u7ecf\u9a8c");
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("copper", copper);
        doc.createElement("copperExtra", copperExtra);
        doc.createElement("exp", exp);
        doc.createElement("expExtra", expExtra);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] investCdRecoverConfirm(final PlayerDto playerDto) {
        final int forceId = playerDto.forceId;
        final int taskType = this.hasNationTasks(forceId);
        if (playerDto.playerLv < 30) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.FUNCTION_NOT_OPEN);
        }
        if (taskType != 4) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_INVEST_TASK);
        }
        final TaskKillInfo taskKillInfo = this.taskKillInfoDao.getTaskKillInfo(playerDto.playerId);
        final long now = System.currentTimeMillis();
        final long cd = (taskKillInfo == null) ? 0L : taskKillInfo.getUpdatetime();
        if (cd <= now) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10015);
        }
        final double recoverCd = Math.ceil((cd - now) / 60000.0);
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)62);
        final int gold = (int)(Object)Double.valueOf(ci.getCost() * recoverCd);
        if (!this.playerDao.consumeGold(this.playerDao.read(playerDto.playerId), gold, "\u6295\u8d44\u79d2cd\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        this.taskKillInfoDao.updateTime(playerDto.playerId, System.currentTimeMillis());
        this.dataPushCenterUtil.addInvestCd(playerDto.playerId, System.currentTimeMillis());
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("gold", gold);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public long getBarbarianNationTaskEnd() {
        final int forceId = 1;
        final List<NationTask> tasks = this.nationTaskDao.getListByForce(forceId);
        final int size = tasks.size();
        if (size <= 0) {
            return 0L;
        }
        if (size >= 2) {
            return 0L;
        }
        final NationTask task = tasks.get(0);
        if (task == null) {
            return 0L;
        }
        final int taskType = getTaskTypeById(task.getNationTaskId());
        if (taskType == 3) {
            final long end = (task.getEndtime() == null) ? 0L : task.getEndtime().getTime();
            return end;
        }
        return 0L;
    }
    
    @Override
    public byte[] investCdRecover(final PlayerDto playerDto) {
        final int forceId = playerDto.forceId;
        if (playerDto.playerLv < 30) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)62);
        if (this.playerDao.getConsumeLv(playerDto.playerId) < ci.getLv()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10002);
        }
        final int taskType = this.hasNationTasks(forceId);
        if (taskType != 4) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_INVEST_TASK);
        }
        final TaskKillInfo taskKillInfo = this.taskKillInfoDao.getTaskKillInfo(playerDto.playerId);
        final long now = System.currentTimeMillis();
        final long cd = (taskKillInfo == null) ? now : taskKillInfo.getUpdatetime();
        if (cd <= now) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10015);
        }
        final double recoverCd = Math.ceil((cd - now) / 60000.0);
        final int gold = (int)(Object)Double.valueOf(ci.getCost() * recoverCd);
        return JsonBuilder.getJson(State.SUCCESS, "gold", (Object)gold);
    }
    
    @Override
    public void updatePlayerChallengeInfo(final int playerId, final int generalId, final int times) {
        if (times <= 0) {
            return;
        }
        final PlayerAttribute pAttribute = this.playerAttributeDao.read(playerId);
        if (pAttribute == null) {
            RankService.errorLog.error("updatePlayerChallengeInfo fail pa is null:  playerId " + playerId);
            return;
        }
        if (!RankComm.functionIsOpen(32, playerId, pAttribute)) {
            return;
        }
        final Player player = this.playerDao.read(playerId);
        if (player == null) {
            RankService.errorLog.error("rankService + updatePlayerChallengeInfo :  +  playerId");
            return;
        }
        int suc = 0;
        PlayerChallengeInfo playerChallengeInfo = this.playerChallengeInfoDao.getInfoByPAndG(playerId, generalId);
        if (playerChallengeInfo == null) {
            playerChallengeInfo = new PlayerChallengeInfo();
            playerChallengeInfo.setGeneralId(generalId);
            playerChallengeInfo.setPlayerId(playerId);
            playerChallengeInfo.setVTimes(times);
            suc = this.playerChallengeInfoDao.create(playerChallengeInfo);
        }
        else {
            suc = this.playerChallengeInfoDao.updateVtimes(playerId, generalId, times);
        }
        if (suc > 0) {
            final ComparableFactor[] arrays = MultiRankData.orgnizeValue(times, 0, player.getPlayerLv(), 0);
            RankService.challegeRanker.firePositionRank(player.getForceId(), new MultiRankData(player.getPlayerId(), arrays));
            final int value = RankService.challegeRanker.getRankByPlayerId(player.getForceId(), player.getPlayerId());
            this.checkHasReward(value - times, value, player, 1);
        }
    }
    
    private void checkHasReward(final int preNum, final int value, final Player player, final int type) {
        try {
            if (type != 1 && type != 2) {
                return;
            }
            synchronized (RankService.object[type - 1]) {
                final PlayerRankingReward reward = this.playerRankingRewardDao.getByTypeAndPlayerId(player.getPlayerId(), type);
                final String par = (reward == null) ? "" : reward.getRewardStr();
                final String rewardString = this.rankingCache.getRewardIdStr(preNum, value, par, type);
                if (reward == null) {
                    final PlayerRankingReward toCreate = new PlayerRankingReward();
                    toCreate.setPlayerid(player.getPlayerId());
                    toCreate.setRewardStr(rewardString);
                    toCreate.setType(type);
                    this.playerRankingRewardDao.create(toCreate);
                }
                else if (!par.equals(rewardString)) {
                    this.playerRankingRewardDao.updateReward(reward.getVid(), rewardString);
                }
            }
            // monitorexit(RankService.object[type - 1])
        }
        catch (Exception e) {
            RankService.errorLog.error("checkHasReward exception...", e);
        }
    }
    
    @Override
    public void updatePlayerOccupyCItyInfo(final int playerId, final int generalId, int times) {
        if (times <= 0) {
            return;
        }
        final PlayerAttribute pAttribute = this.playerAttributeDao.read(playerId);
        if (pAttribute == null) {
            RankService.errorLog.error("updatePlayerChallengeInfo fail pa is null:  playerId " + playerId);
            return;
        }
        if (!RankComm.functionIsOpen(32, playerId, pAttribute)) {
            return;
        }
        final Player player = this.playerDao.read(playerId);
        if (player == null) {
            RankService.errorLog.error("rankService + updatePlayerOccupyCItyInfo :  +  playerId");
            return;
        }
        PlayerOccupyCity playerOccupyCity = this.playerOccupyCityDao.getInfoByPAndG(playerId, generalId);
        final int occupyCityNum = this.playerOccupyCityDao.getByPlayerId(playerId);
        final int checkCanAdd = this.rankingCache.checkCanAdd(player.getPlayerLv(), occupyCityNum, times);
        if (checkCanAdd == 0) {
            return;
        }
        times = checkCanAdd;
        int suc = 0;
        if (playerOccupyCity == null) {
            playerOccupyCity = new PlayerOccupyCity();
            playerOccupyCity.setGeneralId(generalId);
            playerOccupyCity.setPlayerId(playerId);
            playerOccupyCity.setOccupyCityNum(times);
            suc = this.playerOccupyCityDao.create(playerOccupyCity);
        }
        else {
            suc = this.playerOccupyCityDao.updateVtimes(playerId, generalId, times);
        }
        if (suc > 0) {
            final ComparableFactor[] arrays = MultiRankData.orgnizeValue(times, 0, player.getPlayerLv(), 0);
            RankService.occupyCityRanker.firePositionRank(player.getForceId(), new MultiRankData(player.getPlayerId(), arrays));
            final int value = RankService.occupyCityRanker.getRankByPlayerId(player.getForceId(), player.getPlayerId());
            this.checkHasReward(value - times, value, player, 2);
        }
    }
    
    @Override
    public byte[] getOccupyRankInfo(final int page, final PlayerDto playerDto, final int type) {
        if (type < 1 || type > 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final MultiRanker ranker = (type == 2) ? RankService.occupyCityRanker : RankService.challegeRanker;
        final int playerId = playerDto.playerId;
        final int forceId = playerDto.forceId;
        int startRank = 1;
        final int count = 6;
        final int nowRank = ranker.getPlayerPositionRank(playerId, forceId);
        final int totalNum = ranker.getTotalPostionRankNumByForceId(forceId);
        List<MultiRankData> list = new ArrayList<MultiRankData>();
        if (page == 0) {
            if (nowRank == -1) {
                startRank = 1;
            }
            else {
                final int isDevided = (nowRank % count == 0) ? -1 : 0;
                startRank = nowRank / count * count + isDevided * 6 + 1;
            }
        }
        else {
            if (page > totalNum / count + 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            startRank = (page - 1) * count + 1;
        }
        final int currentPage = startRank / 6 + 1;
        int value = 0;
        if (type == 2) {
            value = this.playerOccupyCityDao.getByPlayerId(playerId);
        }
        else if (type == 1) {
            value = this.playerChallengeInfoDao.getByPlayerId(playerId);
        }
        list = ranker.getForcePositionRankList(forceId, startRank, count);
        final int totalPage = (int)Math.ceil(totalNum / Double.valueOf(count));
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("rankList");
        for (final MultiRankData pId : list) {
            if (pId == null) {
                continue;
            }
            doc.startObject();
            final Player player = this.playerDao.read(pId.playerId);
            doc.createElement("rank", startRank);
            doc.createElement("playerId", pId.playerId);
            doc.createElement("playerLv", player.getPlayerLv());
            doc.createElement("playerName", player.getPlayerName());
            doc.createElement("forceId", player.getForceId());
            doc.createElement("value", pId.value.get(0).getValue());
            ++startRank;
            doc.endObject();
        }
        doc.endArray();
        final Player currPlayer = this.playerDao.read(playerId);
        doc.createElement("currentPage", currentPage);
        doc.createElement("totalPage", totalPage);
        doc.createElement("playerId", playerId);
        doc.createElement("playerName", currPlayer.getPlayerName());
        doc.createElement("playerLv", currPlayer.getPlayerLv());
        doc.createElement("pic", currPlayer.getPic());
        doc.createElement("value", value);
        doc.createElement("rank", nowRank);
        doc.createElement("forceId", playerDto.forceId);
        if (nowRank > 1) {
            final MultiRankData nextRankData = ranker.getRankNum(playerDto.forceId, nowRank - 2);
            final int nextKillNum = (nextRankData == null) ? 0 : nextRankData.value.get(0).getValue();
            doc.createElement("nextKillNum", (nextKillNum - value == 0) ? 1 : (nextKillNum - value));
        }
        final List<PlayerGeneralMilitary> glist = this.playerGeneralMilitaryDao.getMilitaryList(playerId);
        if (type == 1) {
            final List<PlayerChallengeInfo> lists = this.playerChallengeInfoDao.getListByPlayerIdOrderByNum(playerId);
            this.fulfillist1(lists, glist);
            if (lists != null && !lists.isEmpty()) {
                doc.startArray("gInfos");
                for (final PlayerChallengeInfo info : lists) {
                    if (info == null) {
                        continue;
                    }
                    doc.startObject();
                    doc.createElement("generalId", info.getGeneralId());
                    final General general = (General)this.generalCache.get((Object)info.getGeneralId());
                    doc.createElement("generalName", general.getName());
                    doc.createElement("value", info.getVTimes());
                    doc.createElement("gLv", this.getGeneralLv(info.getPlayerId(), info.getGeneralId(), glist));
                    doc.createElement("pic", general.getPic());
                    doc.createElement("quality", general.getQuality());
                    doc.endObject();
                }
                doc.endArray();
            }
            final PlayerRankingReward rankingReward = this.playerRankingRewardDao.getByTypeAndPlayerId(playerId, type);
            int num = 0;
            int rewardExp = 0;
            if (rankingReward != null) {
                num = SymbolUtil.getSplitNum(rankingReward.getRewardStr(), ",");
            }
            doc.createElement("boxNum", num);
            final List<RankingReward> division = this.rankingCache.getRankDivision(type);
            int title = 0;
            int i = 0;
            int nextId = 0;
            int nextNum = 0;
            for (final RankingReward reward : division) {
                ++i;
                if (value < reward.getCount()) {
                    nextNum = i;
                    break;
                }
                title = i;
                nextId = i;
                nextNum = i;
            }
            RankingReward nextRankingReward = null;
            if (num > 0) {
                final String rewardString = (rankingReward.getRewardStr() == null) ? "" : rankingReward.getRewardStr();
                final List<Integer> rewardList = SymbolUtil.stringToList(rewardString, ",");
                rewardExp = this.getRewardExp(rewardList, playerDto.playerLv, type);
                final RankingReward raReward = division.get(nextNum - 2);
                doc.createElement("nextNum", (raReward == null) ? 0 : raReward.getCount());
            }
            else {
                if (nextId <= division.size() - 1) {
                    nextRankingReward = division.get(nextId);
                }
                rewardExp = ((nextRankingReward == null) ? 0 : nextRankingReward.getRewardExp());
                doc.createElement("nextNum", (nextRankingReward == null) ? 0 : nextRankingReward.getCount());
            }
            if (rewardExp >= 0) {
                doc.createElement("reward", rewardExp);
            }
            doc.createElement("title", title + 1);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        if (type == 2) {
            final List<PlayerOccupyCity> lists2 = this.playerOccupyCityDao.getListByPlayerIdOrderByNum(playerId);
            this.fulfillist2(lists2, glist);
            if (lists2 != null && !lists2.isEmpty()) {
                doc.startArray("gInfos");
                for (final PlayerOccupyCity info2 : lists2) {
                    if (info2 == null) {
                        continue;
                    }
                    doc.startObject();
                    doc.createElement("generalId", info2.getGeneralId());
                    final General general = (General)this.generalCache.get((Object)info2.getGeneralId());
                    doc.createElement("generalName", general.getName());
                    doc.createElement("value", info2.getOccupyCityNum());
                    doc.createElement("gLv", this.getGeneralLv(info2.getPlayerId(), info2.getGeneralId(), glist));
                    doc.createElement("pic", general.getPic());
                    doc.createElement("quality", general.getQuality());
                    doc.endObject();
                }
                doc.endArray();
            }
            final PlayerRankingReward rankingReward = this.playerRankingRewardDao.getByTypeAndPlayerId(playerId, type);
            int num = 0;
            int rewardExp = 0;
            int index = 0;
            if (rankingReward != null) {
                final String rewardString2 = (rankingReward.getRewardStr() == null) ? "" : rankingReward.getRewardStr();
                final List<Integer> rewardList2 = SymbolUtil.stringToList(rewardString2, ",");
                if (rewardList2 == null || rewardList2.isEmpty()) {
                    num = 0;
                }
                else {
                    for (final Integer reward2 : rewardList2) {
                        final RankingReward reward3 = (RankingReward)this.rankingCache.get((Object)reward2);
                        if (playerDto.playerLv >= reward3.getLv()) {
                            rewardExp = reward3.getRewardExp();
                            index = reward2;
                            break;
                        }
                    }
                    num = rewardExp;
                }
            }
            doc.createElement("boxNum", num);
            final List<RankingReward> division2 = this.rankingCache.getRankDivision(type);
            int title2 = 0;
            int j = 0;
            int nextId2 = 0;
            int nextNum2 = 0;
            for (final RankingReward reward4 : division2) {
                ++j;
                if (num > 0) {
                    nextNum2 = j;
                    if (reward4.getId() >= index) {
                        break;
                    }
                    title2 = j;
                }
                else {
                    if (value < reward4.getCount() || playerDto.playerLv < reward4.getLv()) {
                        nextNum2 = j;
                        break;
                    }
                    title2 = j;
                    nextId2 = j;
                    nextNum2 = j;
                }
            }
            RankingReward nextRankingReward2 = null;
            if (num > 0) {
                rewardExp = num;
                final RankingReward raReward2 = division2.get(nextNum2 - 1);
                doc.createElement("nextNum", (raReward2 == null) ? 0 : raReward2.getCount());
            }
            else {
                if (nextId2 <= division2.size() - 1) {
                    nextRankingReward2 = division2.get(nextId2);
                }
                if (nextRankingReward2 != null) {
                    doc.createElement("nextLv", nextRankingReward2.getLv());
                }
                rewardExp = ((nextRankingReward2 == null) ? 0 : nextRankingReward2.getRewardExp());
                doc.createElement("nextNum", (nextRankingReward2 == null) ? 0 : nextRankingReward2.getCount());
            }
            if (rewardExp >= 0) {
                doc.createElement("reward", rewardExp);
            }
            doc.createElement("title", title2 + 1);
            this.rankingCache.checkIsFull(playerDto.playerLv, value, doc);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
    }
    
    private int getGeneralLv(final Integer playerId, final Integer generalId, final List<PlayerGeneralMilitary> glist) {
        for (final PlayerGeneralMilitary pgm : glist) {
            if (pgm.getPlayerId().equals(playerId) && pgm.getGeneralId().equals(generalId)) {
                return pgm.getLv();
            }
        }
        final PlayerGeneral playerGeneral = this.playerGeneralDao.getPlayerGeneral(playerId, generalId);
        if (playerGeneral != null) {
            return playerGeneral.getLv();
        }
        return 0;
    }
    
    private void fulfillist2(final List<PlayerOccupyCity> lists, final List<PlayerGeneralMilitary> glist) {
        final List<Integer> gLists = new ArrayList<Integer>();
        for (final PlayerOccupyCity info : lists) {
            gLists.add(info.getGeneralId());
        }
        for (final PlayerGeneralMilitary pgm : glist) {
            if (!gLists.contains(pgm.getGeneralId())) {
                final PlayerOccupyCity info2 = new PlayerOccupyCity();
                info2.setGeneralId(pgm.getGeneralId());
                info2.setOccupyCityNum(0);
                info2.setPlayerId(pgm.getPlayerId());
                lists.add(info2);
            }
        }
    }
    
    private void fulfillist1(final List<PlayerChallengeInfo> lists, final List<PlayerGeneralMilitary> glist) {
        final List<Integer> gLists = new ArrayList<Integer>();
        for (final PlayerChallengeInfo info : lists) {
            gLists.add(info.getGeneralId());
        }
        for (final PlayerGeneralMilitary pgm : glist) {
            if (!gLists.contains(pgm.getGeneralId())) {
                final PlayerChallengeInfo info2 = new PlayerChallengeInfo();
                info2.setGeneralId(pgm.getGeneralId());
                info2.setVTimes(0);
                info2.setPlayerId(pgm.getPlayerId());
                lists.add(info2);
            }
        }
    }
    
    @Override
    public byte[] getRankerReward(final PlayerDto playerDto, final int type) {
        if (type < 1 || type > 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final PlayerRankingReward reward = this.playerRankingRewardDao.getByTypeAndPlayerId(playerDto.playerId, type);
        final String rewarString = (reward == null) ? "" : reward.getRewardStr();
        if (StringUtils.isBlank(rewarString)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
        }
        final List<Integer> rewardList = SymbolUtil.stringToList(rewarString, ",");
        if (rewardList == null || rewardList.isEmpty()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
        }
        final int rewardExp = this.getRewardExp(rewardList, playerDto.playerLv, type);
        if (rewardExp <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_AVAILABLE_REWARD);
        }
        final String realReward = this.orgReward(rewardExp, 0, 0);
        final int reason = (type == 2) ? 6 : 5;
        RewardType.reward(realReward, this.dataGetter, playerDto.playerId, reason);
        if (type == 2) {
            this.playerRankingRewardDao.updateReward(reward.getVid(), SymbolUtil.listToString(rewardList, ","));
        }
        else {
            this.playerRankingRewardDao.updateReward(reward.getVid(), "");
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        RewardType.rewardDoc(realReward, doc);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private int getRewardExp(final List<Integer> rewardList, final int playerLv, final int type) {
        if (rewardList == null || rewardList.isEmpty()) {
            return 0;
        }
        int rewardExp = 0;
        int toRemove = 0;
        for (final Integer index : rewardList) {
            if (type == 2) {
                final RankingReward rankingReward = (RankingReward)this.rankingCache.get((Object)index);
                if (playerLv >= rankingReward.getLv()) {
                    rewardExp = rankingReward.getRewardExp();
                    break;
                }
                ++toRemove;
            }
            else {
                final RankingReward rankingReward = (RankingReward)this.rankingCache.get((Object)index);
                if (rankingReward == null) {
                    continue;
                }
                rewardExp += rankingReward.getRewardExp();
            }
        }
        if (type == 2 && rewardExp > 0) {
            rewardList.remove(toRemove);
        }
        return rewardExp;
    }
    
    @Override
    public void titleInfoByCurTitle(final int titleMax, final int taskType, final JsonDocument doc, final int rank) {
        final String title = this.kingdomTaskRankingCache.getTitlePic(titleMax, taskType);
        doc.createElement("title", StringUtils.isBlank(title) ? "" : title);
        doc.createElement("taskType", taskType);
        doc.createElement("rank", rank);
        if (taskType == 999) {
            doc.createElement("titleName", this.kingdomTaskRankingCache.getTitlePicName(titleMax, taskType));
        }
    }
    
    @Override
    public boolean duringTaskByTarget(final int target) {
        try {
            final City city = CityDataCache.cityArray[target];
            if (city == null) {
                return false;
            }
            final NationTask task = this.nationTaskDao.getByForceAndTarget(city.getForceId(), target);
            return task != null && task.getIswin() == 0;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().appendErrorMsg("Exception!").append("target", target).appendClassName("RankService").appendMethodName("duringTaskByTarget").flush();
            return false;
        }
    }
    
    @Override
    public List<Tuple<Integer, Long>> getAttDefNationTaskInfos() {
        try {
            final List<Tuple<Integer, Long>> result = new ArrayList<Tuple<Integer, Long>>();
            if (RankService.chooseCities[0] == 0 || RankService.chooseCities[1] == 0) {
                return null;
            }
            NationTask task = null;
            for (int i = 0; i < RankService.chooseCities.length; ++i) {
                task = this.nationTaskDao.getByForceAndTarget(1, RankService.chooseCities[i]);
                if (task != null) {
                    if (task.getIswin() == 0) {
                        final Date endDate = task.getEndtime();
                        final long startTime = endDate.getTime() - 7200000L;
                        final Tuple<Integer, Long> tuple = new Tuple();
                        tuple.left = RankService.chooseCities[i];
                        tuple.right = startTime;
                        result.add(tuple);
                    }
                }
            }
            return result;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().appendErrorMsg("Exception!").appendClassName("RankService").appendMethodName("getAttDefNationTaskInfos").flush();
            return null;
        }
    }
    
    @Deprecated
    @Override
    public byte[] useInvestCoupon(final PlayerDto playerDto, final int type) {
        if (type < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final PlayerCoupon playerCoupon = this.playerCouponDao.getPlayerCouponByPT(playerDto.playerId, 1);
        int couponNum = (playerCoupon == null) ? 0 : playerCoupon.getCouponNum();
        final int num = PlayerCouponUseRecord.inUseNum(playerDto.playerId, type);
        couponNum -= num;
        if (couponNum <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_COUPON_LEFT);
        }
        final int hasNationTask = this.hasNationTasks(playerDto.forceId);
        if (hasNationTask != 4) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_INVEST_TASK);
        }
        PlayerCouponUseRecord.useCoupon(playerDto.playerId, playerCoupon.getVid(), type);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public List<Integer> getLastAttDefChooseCities() {
        List<Integer> cities = null;
        for (int i = 0; i < RankService.chooseCities.length; ++i) {
            final int cityId = RankService.chooseCities[i];
            if (cityId != 0) {
                if (cities == null) {
                    cities = new ArrayList<Integer>();
                }
                cities.add(cityId);
            }
        }
        return cities;
    }
    
    @Override
    public void pushWholeKill() {
        final long start = System.currentTimeMillis();
        for (final Integer playerId : RankService.worldMap.keySet()) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            this.dataGetter.getCityService().getWholeKillTitle(playerId, RankService.worldMap.get(playerId), doc, false);
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_WHOLE_KILL, doc.toByte());
        }
        RankService.timerLog.info(LogUtil.formatThreadLog("RankService", "pushWholeKill", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Override
    public void addWorld(final int playerId, final int forceId) {
        RankService.worldMap.put(playerId, forceId);
    }
    
    @Override
    public void leaveWorld(final int playerId) {
        RankService.worldMap.remove(playerId);
    }
    
    public void doCheckSdTask() {
        try {
            final int taskType = this.hasNationTasks(1);
            PushTaskOverInfo overInfo = null;
            if (taskType == 7) {
                final List<NationTask> taskList = this.nationTaskDao.getModels();
                if (taskList == null || taskList.isEmpty() || taskList.get(0) == null) {
                    return;
                }
                long finishTime;
                final long now = finishTime = System.currentTimeMillis();
                for (int i = 0; i < taskList.size(); ++i) {
                    final NationTask task = taskList.get(i);
                    if (task != null) {
                        final int isWin = task.getIswin();
                        final int taskId = task.getNationTaskId();
                        final int serial = task.getNationTaskId() % 100 / 10;
                        final int forceId = task.getForceid();
                        final int maxSerial = this.ktSdmzSCache.getMaxRound();
                        if (isWin == 2 && serial < maxSerial) {
                            finishTime = task.getFinishtime();
                            final long nextTaskTime = finishTime + 60000L;
                            final long taskEndTime = task.getEndtime().getTime();
                            if (nextTaskTime <= now && nextTaskTime < taskEndTime) {
                                this.nationtaskSDNextStart(task);
                            }
                        }
                        else if (isWin == 0) {
                            final boolean checkIsOver = this.checkSdIsOver(serial, forceId);
                            if (checkIsOver) {
                                this.nationTaskDao.updateIsWinAndFinishTime(taskId, 2, now);
                                overInfo = new PushTaskOverInfo();
                                overInfo.force = forceId;
                                overInfo.taskType = taskType;
                                overInfo.state = 2;
                                overInfo.lv = 0;
                                overInfo.serial = serial;
                                this.pushNationTaskInfo(overInfo);
                                this.pushCurNationTaskInfoToPlayer(forceId);
                                if (now + 60000L < task.getEndtime().getTime() && serial < 6) {
                                    final String name = (forceId == 1) ? LocalMessages.T_FORCE_BEIDI : ((forceId == 2) ? LocalMessages.T_FORCE_XIRONG : LocalMessages.T_FORCE_DONGYI);
                                    final String msg = MessageFormatter.format(LocalMessages.NEXT_SD_IS_COMING, new Object[] { name });
                                    this.chatService.sendSystemChat("GLOBAL", 0, forceId, msg, null);
                                }
                                if (serial % 2 == 0) {
                                    final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
                                    final KindomLv kindomLv = (KindomLv)this.kingdomLvCache.get((Object)forceInfo.getForceLv());
                                    final int nowExp = forceInfo.getForceExp();
                                    final int maxExp = kindomLv.getExpUpgrade();
                                    if (nowExp < maxExp && forceInfo.getForceLv() < this.kingdomLvCache.maxLv) {
                                        final int addExp = this.getAddExp(7, task.getAttType(), task.getTarget(), forceInfo, kindomLv);
                                        final int finalExp = (addExp + nowExp > maxExp) ? maxExp : (addExp + nowExp);
                                        this.forceInfoDao.updateNationExp(forceId, finalExp);
                                    }
                                }
                                final String msg2 = MessageFormatter.format(LocalMessages.NATION_SDMZ_TASK_SERIAL_VIC, new Object[] { ColorUtil.getForceMsg(forceId, WorldCityCommon.nationIdNameMap.get(forceId)), serial });
                                this.chatService.sendBigNotice("GLOBAL", null, msg2, null);
                            }
                        }
                    }
                }
            }
            else if (taskType == 8) {
                final List<NationTask> taskList = this.nationTaskDao.getModels();
                if (taskList == null || taskList.isEmpty() || taskList.get(0) == null) {
                    return;
                }
                NationTask task2 = null;
                ForceInfo forceInfo2 = null;
                final long now2 = System.currentTimeMillis();
                for (int i = 0; i < taskList.size(); ++i) {
                    task2 = taskList.get(i);
                    if (task2 != null) {
                        final int isWin2 = task2.getIswin();
                        if (isWin2 == 0) {
                            forceInfo2 = this.forceInfoDao.read(task2.getForceid());
                            final int forceLv = forceInfo2.getForceLv();
                            final Set<Integer> cities = this.getCurrentTaskCities(task2);
                            final boolean checkIsWin = this.checkJSBJTaskIsWin(task2, forceLv, cities);
                            this.doSupplyArmies(task2);
                            this.doLegionTeamWork(task2, now2, cities, forceLv);
                            final int taskId2 = task2.getNationTaskId();
                            final int serial2 = taskId2 % 100 / 10;
                            if (serial2 <= 3) {
                                final Date date = task2.getEndtime();
                                overInfo = new PushTaskOverInfo();
                                overInfo.force = task2.getForceid();
                                overInfo.serial = serial2;
                                overInfo.taskType = taskType;
                                if (checkIsWin) {
                                    overInfo.state = 2;
                                    if (now2 >= date.getTime()) {
                                        task2.setIswin(checkIsWin ? 2 : 1);
                                        this.nationTaskDao.updateIsWin(taskId2, task2.getIswin());
                                        this.nationTaskJSBJOver(task2, false);
                                        this.pushCurNationTaskInfoToPlayer(task2.getForceid());
                                        this.pushNationTaskInfo(overInfo);
                                    }
                                }
                                else {
                                    overInfo.state = task2.getIswin();
                                    task2.setIswin(checkIsWin ? 2 : 1);
                                    this.nationTaskDao.updateIsWin(taskId2, task2.getIswin());
                                    this.nationTaskJSBJOver(task2, false);
                                    this.pushCurNationTaskInfoToPlayer(task2.getForceid());
                                    this.pushNationTaskInfo(overInfo);
                                }
                            }
                        }
                    }
                }
            }
            else if (taskType == 9) {
                if (RankService.taskInfo == null) {
                    return;
                }
                final Date enDate = RankService.taskInfo.getEndTimeDate();
                if (enDate == null || enDate.before(new Date()) || RankService.taskInfo.getState() != 1) {
                    return;
                }
                final int serial3 = RankService.taskInfo.getSerial();
                final boolean hasFinished = this.checkHasFinished(serial3);
                if (!hasFinished) {
                    return;
                }
                final List<NationTask> taskList2 = this.nationTaskDao.getModels();
                if (taskList2 == null || taskList2.isEmpty()) {
                    return;
                }
                for (final NationTask task3 : taskList2) {
                    if (task3 == null) {
                        continue;
                    }
                    if (serial3 == 1) {
                        task3.setIswin(2);
                    }
                    else {
                        final int forceId2 = task3.getForceid();
                        final int setIsWin = (RankService.taskInfo.getAttackCityForceId() == forceId2) ? 2 : 1;
                        task3.setIswin(setIsWin);
                    }
                    RankService.errorLog.error("doCheckSdTask nationTaskHJOver...");
                    this.nationTaskHJOver(task3, false);
                }
                for (final NationTask task3 : taskList2) {
                    final int forceId2 = task3.getForceid();
                    this.pushCurNationTaskInfoToPlayer(forceId2);
                }
                if (serial3 <= 1) {
                    RankService.taskInfo.setSerial(2);
                    RankService.taskInfo.setCanAtt(true);
                    this.battleService.clearBattleForNTYellowTurbans(2);
                    this.battleService.addXiangYangPhantomForTimer("");
                    this.chatService.sendSystemChat("GLOBAL", 0, 0, LocalMessages.NATION_TASK_HJ_SECOND_MESSAGE_1, null);
                    this.chatService.sendSystemChat("GLOBAL", 0, 0, LocalMessages.NATION_TASK_HJ_SECOND_MESSAGE_2, null);
                    final Group group = GroupManager.getInstance().getGroup(ChatType.WORLD.toString());
                    if (group != null) {
                        final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_NATION_TASK_STATE_CHANGE.getModule(), JsonBuilder.getJson(JsonBuilder.getSimpleJson("recover", 4))));
                        group.notify(WrapperUtil.wrapper(PushCommand.PUSH_NATION_TASK_STATE_CHANGE.getCommand(), 0, bytes));
                    }
                }
                else {
                    RankService.taskInfo.setState(2);
                }
                this.pushCurNationTaskInfoToPlayer(1);
                this.pushCurNationTaskInfoToPlayer(2);
                this.pushCurNationTaskInfoToPlayer(3);
            }
            else if (taskType == 10) {
                if (RankService.nationMiracle == null || RankService.nationMiracle.miracleState != 0) {
                    return;
                }
                final List<NationTask> tasks = this.nationTaskDao.getModels();
                if (tasks == null || tasks.isEmpty()) {
                    return;
                }
                boolean allIsOver = true;
                for (final NationTask task4 : tasks) {
                    final int taskId3 = task4.getNationTaskId();
                    final int serial4 = taskId3 % 1000 / 100;
                    if (task4.getIswin() == 0) {
                        final boolean hasFull = RankService.nationMiracle.nationResourceFull(task4.getForceid());
                        final int forceId3 = task4.getForceid();
                        if (hasFull) {
                            final int curSerialFirst = RankService.nationMiracle.serialWinner[serial4 - 1];
                            if (curSerialFirst == 0) {
                                task4.setIswin(2);
                                RankService.nationMiracle.serialWinner[serial4 - 1] = forceId3;
                            }
                            else {
                                task4.setIswin(1);
                            }
                            task4.setAttType(1);
                            this.nationTaskDao.updateIsWin(taskId3, task4.getIswin());
                            this.nationTaskMiracleOver(task4, false);
                            this.pushCurNationTaskInfoToPlayer(forceId3);
                        }
                    }
                    if (serial4 < 3 || task4.getIswin() == 0) {
                        allIsOver = false;
                    }
                }
                if (allIsOver) {
                    RankService.nationMiracle.changeMiracleState(1);
                }
            }
            else if (taskType == 12) {
                if (RankService.festival == null || RankService.festival.miracleState != 0) {
                    return;
                }
                final List<NationTask> tasks = this.nationTaskDao.getModels();
                if (tasks == null || tasks.isEmpty()) {
                    return;
                }
                boolean allIsOver = true;
                for (final NationTask task4 : tasks) {
                    final int taskId3 = task4.getNationTaskId();
                    final int serial4 = taskId3 % 1000 / 100;
                    if (task4.getIswin() == 0) {
                        final boolean hasFull = RankService.festival.nationResourceFull(task4.getForceid());
                        final int forceId3 = task4.getForceid();
                        if (hasFull) {
                            final int curSerialFirst = RankService.festival.serialWinner[serial4 - 1];
                            if (curSerialFirst == 0) {
                                task4.setIswin(2);
                                RankService.festival.serialWinner[serial4 - 1] = forceId3;
                            }
                            else {
                                task4.setIswin(1);
                            }
                            task4.setAttType(1);
                            this.nationTaskDao.updateIsWin(taskId3, task4.getIswin());
                            this.nationTaskFestivalOver(task4, false);
                            this.pushCurNationTaskInfoToPlayer(forceId3);
                        }
                    }
                    final int n = serial4;
                    RankService.festival.getClass();
                    if (n < 5 || task4.getIswin() == 0) {
                        allIsOver = false;
                    }
                }
                if (allIsOver) {
                    RankService.festival.changeMiracleState(1);
                }
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("doCheckSdTask fail...", e);
        }
    }
    
    private void nationTaskMiracleOver(final NationTask task, final boolean isTimeOver) {
        try {
            if (task == null || RankService.nationMiracle == null) {
                RankService.errorLog.error("nationTaskMiracleOver error...Miracle is null ?" + RankService.nationMiracle == null);
                return;
            }
            if (isTimeOver) {
                this.autoRewardAndSave(task, isTimeOver);
            }
            final int forceId = task.getForceid();
            final int taskId = task.getNationTaskId();
            final int serial = taskId % 1000 / 100;
            final int attType = (task.getAttType() == null) ? 0 : task.getAttType();
            if (attType == 1) {
                final int addExp = this.getMiracleAddExp(serial, forceId);
                this.addNationExp(addExp, forceId);
            }
            final PushTaskOverInfo overInfo = new PushTaskOverInfo();
            overInfo.force = forceId;
            overInfo.taskType = 10;
            overInfo.serial = serial;
            if (task.getIswin() == 2) {
                overInfo.state = 2;
                this.pushNationTaskInfo(overInfo);
            }
            else {
                overInfo.state = 1;
                this.pushNationTaskInfo(overInfo);
            }
            RankService.errorLog.error("miracle task is over...forceId:" + forceId + "isFirst:" + (task.getIswin() == 2));
            if (serial < 3 && !isTimeOver) {
                this.autoRewardAndSave(task, false);
                this.nationTaskDao.deleteById(taskId);
                this.taskKillInfoDao.eraseByTaskId(taskId);
                if (RankService.nationTaskKillRanker != null) {
                    RankService.nationTaskKillRanker.clearByForceId(forceId);
                }
                this.duelService.clear();
                final NationTask newTask = new NationTask();
                newTask.setEndtime(task.getEndtime());
                newTask.setForceid(forceId);
                newTask.setIswin(0);
                newTask.setNationTaskId(taskId + 100);
                newTask.setTarget(0);
                newTask.setTaskRelateInfo(task.getTaskRelateInfo());
                this.nationTaskDao.create(newTask);
                RankService.nationMiracle.curSerial[forceId - 1] = serial + 1;
                final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
                final KtMrTarget target = this.ktMrCache.getTargetByNationAndLv(forceId, forceInfo.getForceLv(), serial + 1);
                if (RankService.nationMiracle != null) {
                    RankService.nationMiracle.clearResource(forceId, target);
                }
                else {
                    RankService.errorLog.error("nationMiracle is null");
                }
            }
            else {
                RankService.nationMiracle.setNationTaskIsOver(forceId, true);
                InMemmoryIndivTaskManager.getInstance().clearNationIndivTask(forceId);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
        }
    }
    
    private int getMiracleAddExp(final int serial, final int forceId) {
        return (serial < 3) ? 1 : 2;
    }
    
    private void nationTaskHJOver(final NationTask task, final boolean isTimeOver) {
        try {
            RankService.errorLog.error("nationTaskHJOver..." + isTimeOver);
            final int serial = RankService.taskInfo.getSerial();
            final int forceId = task.getForceid();
            if (isTimeOver) {
                this.autoRewardAndSave(task, isTimeOver);
            }
            final PushTaskOverInfo overInfo = new PushTaskOverInfo();
            overInfo.force = forceId;
            overInfo.serial = serial;
            overInfo.taskType = 9;
            if (task.getIswin() == 2) {
                overInfo.state = 2;
                final int addExp = this.getHJAddExp(serial, forceId);
                this.addNationExp(addExp, forceId);
                this.pushNationTaskInfo(overInfo);
            }
            else {
                overInfo.state = 1;
                this.pushNationTaskInfo(overInfo);
            }
            if (serial <= 1 && !isTimeOver) {
                this.autoRewardAndSave(task, true);
                final int taskId = task.getNationTaskId();
                this.nationTaskDao.deleteById(taskId);
                this.taskKillInfoDao.eraseByTaskId(taskId);
                if (RankService.nationTaskKillRanker != null) {
                    this.saveHJLastNationKill(RankService.nationTaskKillRanker, forceId);
                    RankService.nationTaskKillRanker.clearByForceId(forceId);
                }
                this.duelService.clear();
                final NationTask newTask = new NationTask();
                newTask.setEndtime(task.getEndtime());
                newTask.setForceid(forceId);
                newTask.setIswin(0);
                newTask.setNationTaskId(taskId + 10);
                newTask.setTarget(0);
                newTask.setTaskRelateInfo(task.getTaskRelateInfo());
                this.nationTaskDao.create(newTask);
            }
            else {
                InMemmoryIndivTaskManager.getInstance().clearNationIndivTask(forceId);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
        }
    }
    
    private void saveHJLastNationKill(final NationTaskRankerNew nationTaskKillRanker2, final int forceId) {
        try {
            if (nationTaskKillRanker2 == null || RankService.taskInfo == null) {
                return;
            }
            final BaseRanker ranker = nationTaskKillRanker2.getRanker1();
            if (ranker == null) {
                return;
            }
            final InvestInfo nationKill = ranker.getKillTotalByForceId(forceId);
            RankService.taskInfo.getLastNationKill()[forceId - 1] = ((nationKill == null) ? 0L : nationKill.investNum);
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
        }
    }
    
    private void addNationExp(final int addExp, final int forceId) {
        try {
            if (addExp > 0) {
                final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
                if (forceInfo != null) {
                    final int exp = forceInfo.getForceExp();
                    final KindomLv kindomLv = (KindomLv)this.kingdomLvCache.get((Object)forceInfo.getForceLv());
                    if (kindomLv != null) {
                        final int expUpgrade = kindomLv.getExpUpgrade();
                        final int resultExp = Math.min(exp + addExp, expUpgrade);
                        if (resultExp > exp) {
                            this.forceInfoDao.updateNationExp(forceId, resultExp);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            RankService.errorLog.error(this, e);
        }
    }
    
    private void doLegionTeamWork(final NationTask task, final long now, final Set<Integer> cities, final int forceLv) {
        try {
            if (task == null || cities == null || cities.isEmpty()) {
                return;
            }
            final int taskId = task.getNationTaskId();
            final int serial = taskId % 100 / 10;
            if (serial > 3) {
                return;
            }
            final int forceId = task.getForceid();
            final int index = RankService.armiesSerial[forceId - 1];
            if (index <= 0) {
                return;
            }
            final int id = this.ktBjCache.getIdByFAndI(forceLv, index);
            final KtBjS ktBjS = (KtBjS)this.ktBjCache.get((Object)id);
            if (RankService.nextLegionMessage[forceId - 1]) {
                final String msg = LocalMessages.NATION_JSBJ_NEXT_LEGION;
                this.chatService.sendSystemChat("GLOBAL", 0, forceId, msg, null);
                RankService.nextLegionMessage[forceId - 1] = false;
                RankService.errorLog.error("legion Message: " + new Date());
            }
            if (ktBjS == null || ktBjS.getTb() <= 0) {
                return;
            }
            if (now > RankService.nextLegion[forceId - 1] && index > 0) {
                final Tuple<Integer, Battle> tuple = this.getRandomCityFromBJTask(cities);
                if (tuple != null) {
                    this.timerBattleService.fireJiTuanJunForBianJiang(tuple.right, forceId, tuple.left);
                    RankService.nextLegion[forceId - 1] = this.ktBjCache.getNextLegion(ktBjS, now);
                    RankService.errorLog.error(String.valueOf(forceId) + " last legion: " + new Date() + "next: " + new Date(RankService.nextLegion[forceId - 1]));
                }
            }
            if (now > RankService.nextDT[forceId - 1] && index > 0) {
                this.doJSTaskSinglePK(cities);
                RankService.nextDT[forceId - 1] = this.ktBjCache.getNextDT(ktBjS, now);
                RankService.errorLog.error("last DT: " + new Date() + "next: " + new Date(RankService.nextDT[forceId - 1]));
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("doLegionTeamWork fail...forceLv:" + forceLv + "nationtaskId:" + task.getNationTaskId());
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
        }
    }
    
    private void doJSTaskSinglePK(final Set<Integer> cities) {
        try {
            for (final Integer cityId : cities) {
                final Battle battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
                if (battle != null) {
                    this.jobService.addJob("timerBattleService", "fireASinglePK", battle.getBattleId(), System.currentTimeMillis());
                }
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("doJSTaskSinglePK");
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
        }
    }
    
    private Tuple<Integer, Battle> getRandomCityFromBJTask(final Set<Integer> cities) {
        if (cities == null || cities.isEmpty()) {
            return null;
        }
        final List<Integer> list = new ArrayList<Integer>(cities);
        Collections.shuffle(list);
        Battle battle = null;
        for (final Integer cityId : list) {
            battle = NewBattleManager.getInstance().getBattleByDefId(3, cityId);
            if (battle != null) {
                return new Tuple(cityId, battle);
            }
        }
        return null;
    }
    
    private void doSupplyArmies(final NationTask task) {
        try {
            final long startTime = this.getStartTime();
            if (startTime == -1L) {
                return;
            }
            if (task == null) {
                return;
            }
            final int taskId = task.getNationTaskId();
            final int forceId = task.getForceid();
            final int serial = taskId % 100 / 10;
            if (serial > 3) {
                return;
            }
            final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
            int number = 0;
            final int index = this.getJSBJIndex(startTime);
            final String numberInfo = task.getTaskRelateInfo();
            if (!StringUtils.isBlank(numberInfo)) {
                final String[] single = numberInfo.split(";");
                number = Integer.parseInt(single[0]);
            }
            if (index > number) {
                final int id = this.ktBjCache.getIdByFAndI(forceInfo.getForceLv(), index);
                final KtBjS ktBjS = (KtBjS)this.ktBjCache.get((Object)id);
                if (ktBjS == null) {
                    RankService.errorLog.error("doSupplyArmies ktbjs is null....index:" + index);
                }
                final Set<Integer> cities = (ktBjS == null) ? null : ktBjS.getCitiesSetByForceId(forceId);
                final Set<Integer> stateCities = this.getCitiesByForceId(cities, forceId);
                if (stateCities != null && !stateCities.isEmpty()) {
                    final int manzuForceId = WorldCityCommon.playerManZuForceMap.get(forceId);
                    this.timerBattleService.addRoundManZuForBianJiang(manzuForceId, ktBjS.getN(), stateCities);
                    RankService.errorLog.error("supplyCities size:" + stateCities.size());
                }
                RankService.armiesSerial[forceId - 1] = index;
                final String taskRelativeInfo = this.getTaskRelativeInfo(stateCities, index);
                this.nationTaskDao.updateManZuSaoDangTaskRelateInfo(forceId, taskRelativeInfo);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("doSupplyArmies fail....");
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
        }
    }
    
    private String getTaskRelativeInfo(final Set<Integer> stateCities, final int index) {
        final StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(index);
        if (stateCities != null && !stateCities.isEmpty()) {
            stringBuffer.append(";").append(SymbolUtil.CollectionToString(stateCities, ","));
        }
        return stringBuffer.toString();
    }
    
    private Set<Integer> getCitiesByForceId(final Set<Integer> cities, final int forceId) {
        if (cities == null) {
            return null;
        }
        final Set<Integer> result = new HashSet<Integer>();
        for (final Integer cityId : cities) {
            final City city = CityDataCache.cityArray[cityId];
            if (city == null) {
                continue;
            }
            if (city.getForceId() != forceId) {
                continue;
            }
            result.add(cityId);
        }
        return result;
    }
    
    private int getJSBJIndex(final long startTime) {
        final long now = System.currentTimeMillis();
        final long minutes = (now - startTime) / 60000L;
        final int index = this.ktBjCache.getIndexByMinutes(minutes);
        return index;
    }
    
    private void nationTaskJSBJOver(final NationTask task, final boolean timeIsOver) {
        try {
            final int taskId = task.getNationTaskId();
            final int serial = taskId % 100 / 10;
            final int forceId = task.getForceid();
            final int isWin = task.getIswin();
            if (timeIsOver) {
                this.autoRewardAndSave(task, timeIsOver);
            }
            this.saveJSBJInfo(task);
            final int timeDivision = getTimeDevision();
            if (isWin == 2 && serial <= 3) {
                this.addNationExpForJSBJ(forceId);
            }
            if (isWin == 1 && serial <= 3) {
                this.notifyRelativeNation(task, isWin);
                RankService.needShowNextInvade[forceId - 1] = false;
            }
            final int num = this.getZMSDTaskNumber();
            if (serial <= 3 && timeDivision > 0 && ((isWin == 1 && num < 2) || isWin == 2)) {
                this.autoRewardAndSave(task, false);
                RankService.errorLog.error("nationTaskJSBJOver delete tasks....serial:" + serial + " num:" + num);
                this.nationTaskDao.deleteById(task.getNationTaskId());
                this.taskKillInfoDao.eraseByTaskId(taskId);
                if (RankService.nationTaskKillRanker != null) {
                    RankService.nationTaskKillRanker.clearByForceId(forceId);
                }
                this.duelService.clear();
                this.startNextJSBJTask(task);
            }
            else {
                InMemmoryIndivTaskManager.getInstance().clearNationIndivTask(forceId);
                if (num >= 2) {
                    RankService.needShowNextInvade[forceId - 1] = false;
                }
            }
            if (timeDivision <= 0) {
                this.pushCurNationTaskInfoToPlayer(forceId);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("nationTaskJSBJOver  exception...");
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
        }
    }
    
    private void autoRewardAndSave(final NationTask task, final boolean timeIsOver) {
        if (task == null) {
            return;
        }
        RankService.errorLog.error("autoRewardAndSave..." + task.getNationTaskId() + "   over:" + timeIsOver);
        final int taskId = task.getNationTaskId();
        final int serial = taskId % 1000 / 100;
        final int forceId = task.getForceid();
        if (serial >= 2 || timeIsOver) {
            final List<PlayerExpandInfo> list = this.playerExpandInfoDao.getByForceId(forceId);
            this.sendReward(task, task.getIswin(), list);
        }
        this.savePlayerInfo(task);
    }
    
    private void addNationExpForJSBJ(final int forceId) {
        try {
            final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
            if (forceInfo != null) {
                final int exp = forceInfo.getForceExp();
                final KindomLv kindomLv = (KindomLv)this.kingdomLvCache.get((Object)forceInfo.getForceLv());
                if (kindomLv != null) {
                    final int expUpgrade = kindomLv.getExpUpgrade();
                    final int real = Math.min(exp + 1, expUpgrade);
                    if (real > exp) {
                        this.forceInfoDao.updateNationExp(forceId, real);
                    }
                }
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("addNationExpForJSBJ exception...forceId:" + forceId);
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
        }
    }
    
    private int getZMSDTaskNumber() {
        try {
            final List<NationTask> tasks = this.nationTaskDao.getModels();
            int size = 0;
            for (final NationTask task : tasks) {
                if (task == null) {
                    continue;
                }
                final int taskId = task.getNationTaskId();
                final int serial = taskId % 100 / 10;
                if (serial <= 3) {
                    continue;
                }
                ++size;
            }
            return size;
        }
        catch (Exception e) {
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
            return 0;
        }
    }
    
    private void saveJSBJInfo(final NationTask task) {
        if (task == null || task.getTaskRelateInfo() == null) {
            return;
        }
        final int forceId = task.getForceid();
        final String taskInfo = String.valueOf(RankService.armiesSerial[forceId - 1]);
        final String value = this.infos[forceId - 1];
        Configuration.saveProperties(value, taskInfo, "serverstate.properties");
    }
    
    private void notifyRelativeNation(final NationTask task, final int isWin) {
        try {
            if (task == null) {
                return;
            }
            if (isWin == 0) {
                RankService.errorLog.error("notifyRelativeNation exception....taskId:" + task.getNationTaskId());
                return;
            }
            final List<Integer> relativeTask = this.getRelativeTask(task.getForceid());
            if (relativeTask == null) {
                return;
            }
            NationTask relative = null;
            for (final Integer key : relativeTask) {
                try {
                    relative = this.nationTaskDao.getByForce(key);
                }
                catch (Exception e) {
                    RankService.errorLog.error("notifyRelativeNation");
                    RankService.errorLog.error(this, e);
                }
                if (relative != null) {
                    if (relative.getIswin() != 0) {
                        continue;
                    }
                    final int taskId = relative.getNationTaskId();
                    final int serial = taskId % 100 / 10;
                    if (serial <= 3) {
                        RankService.errorLog.error("notifyRelativeNation exception...taskId:" + taskId);
                    }
                    else {
                        this.nationTaskDao.updateIsWin(relative.getNationTaskId(), 3 - isWin);
                        this.pushCurNationTaskInfoToPlayer(relative.getForceid());
                        final PushTaskOverInfo overInfo = new PushTaskOverInfo();
                        overInfo.force = relative.getForceid();
                        overInfo.taskType = 8;
                        overInfo.serial = serial;
                        overInfo.state = 3 - isWin;
                        this.pushNationTaskInfo(overInfo);
                        InMemmoryIndivTaskManager.getInstance().clearNationIndivTask(relative.getForceid());
                    }
                }
            }
        }
        catch (Exception e2) {
            RankService.errorLog.error("notifyRelativeNation exception....isWin:" + isWin + "taskId:" + task.getNationTaskId());
            RankService.errorLog.error(e2.getMessage());
            RankService.errorLog.error(this, e2);
        }
    }
    
    private List<Integer> getRelativeTask(final Integer forceid) {
        List<Integer> result = null;
        for (int i = 0; i < RankService.taskRelative.length; ++i) {
            final int re = RankService.taskRelative[i];
            if (re == forceid) {
                if (result == null) {
                    result = new ArrayList<Integer>();
                }
                result.add(i + 1);
            }
        }
        return result;
    }
    
    private void startNextJSBJTask(final NationTask task) {
        try {
            final int taskId = task.getNationTaskId();
            final int serial = taskId % 100 / 10;
            if (serial > 3) {
                return;
            }
            final int isWin = task.getIswin();
            final int timeDivision = getTimeDevision();
            final int forceId = task.getForceid();
            NationTask nexTask = new NationTask();
            Date endTime = null;
            if (isWin == 2) {
                if (serial >= 3) {
                    return;
                }
                nexTask.setNationTaskId(taskId + 10);
                final Date lastEndTime = task.getEndtime();
                endTime = new Date(lastEndTime.getTime() + 1800000L);
                nexTask.setAttType(0);
            }
            else if (isWin == 1) {
                nexTask.setNationTaskId(taskId + 30);
                endTime = this.getEndTime(timeDivision);
                final int chooseForceId = this.chooseRelativeForceId(forceId);
                if (chooseForceId == 0) {
                    nexTask = null;
                    RankService.errorLog.error("startNextJSBJTask  iswin 1 exception...taskid:" + taskId);
                    return;
                }
                this.doFireManWangLing(chooseForceId, forceId);
                nexTask.setAttType(chooseForceId);
                RankService.taskRelative[forceId - 1] = chooseForceId;
            }
            else if (isWin == 0) {
                nexTask = null;
                RankService.errorLog.error("startNextJSBJTask  iswin 0  exception...taskid:" + taskId);
                return;
            }
            nexTask.setEndtime(endTime);
            nexTask.setForceid(forceId);
            nexTask.setIswin(0);
            nexTask.setTarget(0);
            nexTask.setTaskRelateInfo(task.getTaskRelateInfo());
            this.nationTaskDao.create(nexTask);
        }
        catch (Exception e) {
            RankService.errorLog.error("startNextJSBJTask exception...");
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
        }
    }
    
    private void doFireManWangLing(final int chooseForceId, final int forceId) {
        try {
            final String forceName = WorldCityCommon.nationIdNameMap.get(forceId);
            final String chooseName = WorldCityCommon.nationIdNameMap.get(chooseForceId);
            final String manzuName = (chooseForceId == 1) ? LocalMessages.T_FORCE_BEIDI : ((chooseForceId == 2) ? LocalMessages.T_FORCE_XIRONG : LocalMessages.T_FORCE_DONGYI);
            final String msg1 = MessageFormatter.format(LocalMessages.MANGWANGLING_CAST_TO_1, new Object[] { manzuName, chooseName });
            final String msg2 = MessageFormatter.format(LocalMessages.MANGWANGLING_CAST_TO_2, new Object[] { manzuName, forceName });
            this.chatService.sendSystemChat("GLOBAL", 0, forceId, msg1, null);
            this.chatService.sendSystemChat("GLOBAL", 0, chooseForceId, msg2, null);
            this.jobService.addJob("rankService", "fireMangWangLing", String.valueOf(chooseForceId) + "," + forceId, System.currentTimeMillis() + 60000L);
        }
        catch (Exception e) {
            RankService.errorLog.error("doFireManWangLing exception...choose:" + chooseForceId + " forceId:" + forceId);
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
        }
    }
    
    @Override
    public void fireMangWangLing(final String params) {
        try {
            final String[] single = params.split(",");
            final int chooseForceId = Integer.parseInt(single[0]);
            final int forceId = Integer.parseInt(single[1]);
            final NationTask task = this.nationTaskDao.getByForce(chooseForceId);
            if (task == null) {
                return;
            }
            final int taskId = task.getNationTaskId();
            final int serial = taskId % 100 / 10;
            if (serial <= 3 && task.getIswin() == 0) {
                this.battleService.fireManWangLing(chooseForceId, forceId);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("fireMangWangLing exception...params:" + params);
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
        }
    }
    
    @Override
    public void updateTodayKillNum(final int playerId, final int killNum) {
        try {
            RankService.locks[playerId % RankService.LOCKS_LEN].lock();
            if (this.playerAttributeDao.getFunctionId(playerId).toCharArray()[32] != '1' || RankService.featRank == null) {
                return;
            }
            PlayerFeatRank pfr = this.playerFeatRankDao.read(playerId);
            if (pfr == null) {
                this.dataGetter.getFeatService().openFeatRecord(playerId);
                pfr = this.playerFeatRankDao.read(playerId);
            }
            final int before = pfr.getTotalFeat();
            final int feat = this.calFeat(pfr.getKillFeat(), pfr.getKillNum() + killNum);
            this.playerFeatRankDao.addKillNumAndFeat(playerId, killNum, feat);
            final int after = before + feat;
            this.calFeatBox(playerId, before, after);
            if (after > 0) {
                RankService.featRank.fireRankEvent(1, new com.reign.gcld.world.common.RankData(playerId, after));
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("#class:RankService#method:updateTodayKillNum#playerId:" + playerId + "#killNum:" + killNum, e);
            return;
        }
        finally {
            RankService.locks[playerId % RankService.LOCKS_LEN].unlock();
        }
        RankService.locks[playerId % RankService.LOCKS_LEN].unlock();
    }
    
    @Override
    public void clearFeatRank() {
        if (RankService.featRank == null) {
            return;
        }
        final long start = System.currentTimeMillis();
        int playerId = 0;
        int lastRank = 0;
        final List<PlayerFeatRank> pfrList = this.playerFeatRankDao.getRewardRankList();
        for (final PlayerFeatRank pfr : pfrList) {
            try {
                playerId = pfr.getPlayerId();
                lastRank = pfr.getLastRank();
                final int copper = this.tpFtRankingCache.getRewardCopper(lastRank);
                if (copper > 0) {
                    this.playerResourceDao.addCopperIgnoreMax(playerId, copper, "\u529f\u52cb\u699c\u6392\u540d\u5956\u52b1\u94f6\u5e01", true);
                }
                else {
                    RankService.errorLog.error("class:RankService#method:clearFeatRank#playerId:" + playerId + "#lastRank" + lastRank + "#copper" + copper);
                }
                int iron = this.tpFtRankingCache.getRewardIron(lastRank);
                iron *= (int)(this.dataGetter.getTechEffectCache().getTechEffect(playerId, 33) / 100.0);
                if (iron > 0) {
                    this.playerResourceDao.addIronIgnoreMax(playerId, iron, "\u529f\u52cb\u699c\u6392\u540d\u83b7\u53d6\u9554\u94c1", true);
                }
                int exp = this.tpFtRankingCache.getRewardExp(lastRank);
                exp *= (int)(this.dataGetter.getTechEffectCache().getTechEffect(playerId, 51) / 100.0);
                if (exp > 0) {
                    this.playerService.updateExpAndPlayerLevel(playerId, exp, "\u529f\u52cb\u699c\u6392\u540d\u5956\u52b1\u7ecf\u9a8c");
                }
                String mailContent = "";
                if (iron > 0 && exp > 0) {
                    mailContent = MessageFormatter.format(LocalMessages.FEAT_RANK_MAIL_ALL, new Object[] { TimeUtil.getBeforeMonth(1), TimeUtil.getBeforeDay(1), copper, iron, exp });
                }
                else if (iron > 0) {
                    mailContent = MessageFormatter.format(LocalMessages.FEAT_RANK_MAIL_IRON, new Object[] { TimeUtil.getBeforeMonth(1), TimeUtil.getBeforeDay(1), copper, iron });
                }
                else if (exp > 0) {
                    mailContent = MessageFormatter.format(LocalMessages.FEAT_RANK_MAIL_EXP, new Object[] { TimeUtil.getBeforeMonth(1), TimeUtil.getBeforeDay(1), copper, exp });
                }
                else {
                    mailContent = MessageFormatter.format(LocalMessages.FEAT_RANK_MAIL_COPPER, new Object[] { TimeUtil.getBeforeMonth(1), TimeUtil.getBeforeDay(1), copper });
                }
                this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.FEAT_RANK_TITLE, mailContent, 1, playerId, 0);
            }
            catch (Exception e) {
                RankService.errorLog.error("class:RankService#method:clearFeatRank#playerId:" + playerId + "#lastRank" + lastRank, e);
            }
        }
        this.playerFeatRankDao.clearLastRankReced();
        final byte[] send = JsonBuilder.getSimpleJson("batReward", true);
        for (final com.reign.gcld.world.common.RankData rd : RankService.featRank.getRankList(1)) {
            final int rank = RankService.featRank.getRank(1, rd.playerId, 1);
            if (rank > 0) {
                this.playerFeatRankDao.updateLastRank(rd.playerId, rank);
                Players.push(rd.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        for (final com.reign.gcld.world.common.RankData rd : RankService.featRank.getRankList(2)) {
            final int rank = RankService.featRank.getRank(1, rd.playerId, 2);
            if (rank > 0) {
                this.playerFeatRankDao.updateLastRank(rd.playerId, rank);
                Players.push(rd.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        for (final com.reign.gcld.world.common.RankData rd : RankService.featRank.getRankList(3)) {
            final int rank = RankService.featRank.getRank(1, rd.playerId, 3);
            if (rank > 0) {
                this.playerFeatRankDao.updateLastRank(rd.playerId, rank);
                Players.push(rd.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        this.playerFeatRankDao.clearAll();
        RankService.featRank.clear();
        RankService.timerLog.info(LogUtil.formatThreadLog("RankService", "clearFeatRank", 2, System.currentTimeMillis() - start, ""));
    }
    
    private int chooseRelativeForceId(final int forceId) {
        try {
            NationTask task = null;
            int result = 0;
            for (int i = 1; i <= 3; ++i) {
                if (i != forceId) {
                    task = this.nationTaskDao.getByForce(i);
                    if (task != null && task.getIswin() == 0) {
                        if (task.getAttType() == 0) {
                            result = i;
                            if (result != 0 && WebUtil.nextBoolean()) {
                                break;
                            }
                        }
                    }
                }
            }
            return result;
        }
        catch (Exception e) {
            RankService.errorLog.error("chooseRelativeForceId fail...forceId:" + forceId);
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
            return 0;
        }
    }
    
    private boolean checkSdIsOver(final int serial, final int forceId) {
        final Set<Integer> citySet = this.ktSdmzSCache.getCitySet(serial, forceId);
        if (citySet == null || citySet.isEmpty()) {
            return false;
        }
        City city = null;
        for (final Integer key : citySet) {
            city = CityDataCache.cityArray[key];
            if (city == null) {
                continue;
            }
            if (city.getForceId() != forceId || city.getState() != 0) {
                return false;
            }
        }
        return true;
    }
    
    public boolean isTheXthDayNight() {
        if (RankService.nowDays >= 14) {
            final long result = this.getNextTime();
            if (result == RankService.TASK_TIME1[0].getHour()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int nowDays() {
        try {
            final String startServer = Configuration.getProperty("gcld.server.time");
            final long start = Long.parseLong(startServer);
            final Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTimeInMillis(start);
            final int startYear = startCalendar.get(1);
            final int startDay = startCalendar.get(6);
            final long now = System.currentTimeMillis();
            final Calendar calendar1 = Calendar.getInstance();
            calendar1.setTimeInMillis(now);
            final int nowYear = calendar1.get(1);
            final int nowDay = calendar1.get(6);
            return (nowYear - startYear) * 365 + nowDay - startDay;
        }
        catch (Exception e) {
            RankService.errorLog.error("nowdays fail....");
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
            return 0;
        }
    }
    
    @Override
    public int getCountryNpcDefDays() {
        return this.nowDays() - this.dbVersionDao.getDiffDay();
    }
    
    private boolean hasDoubleReward() {
        return this.isTheXthDayNight();
    }
    
    @Override
    public void updateTodayScoreRank(final String param) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(param)) {
                RankService.errorLog.error("#class:RankService#method:updateTodayScoreRank#param:" + param);
                return;
            }
            final String[] tempArry = param.split("#");
            if (tempArry == null || tempArry.length != 3) {
                RankService.errorLog.error("#class:RankService#method:updateTodayScoreRank#param:" + param + "#tempArry:" + tempArry);
                return;
            }
            final int playerId = Integer.parseInt(tempArry[0]);
            final int cityId = Integer.parseInt(tempArry[1]);
            final int type = Integer.parseInt(tempArry[2]);
            if (playerId <= 0 || type < 1 || type > 3) {
                RankService.errorLog.error("#class:RankService#method:updateTodayScoreRank#param:" + param + "#playerId:" + playerId + "#type" + type);
                return;
            }
            this.updateScore(playerId, type, cityId);
            if (this.dataGetter.getTechEffectCache().getTechEffect(playerId, 52) > 0) {
                this.updateFeat(playerId, type, cityId);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("#class:RankService#method:updateTodayScoreRank#param:" + param, e);
        }
        RankService.timerLog.info(LogUtil.formatThreadLog("RankService", "updateTodayScoreRank", 2, System.currentTimeMillis() - start, "param:" + param));
    }
    
    @Override
    public void updateTryRank(final String param) {
        final long start = System.currentTimeMillis();
        try {
            if (StringUtils.isBlank(param)) {
                RankService.errorLog.error("#class:RankService#method:updateTryRank#param:" + param);
                return;
            }
            final String[] tempArry = param.split("#");
            if (tempArry == null || tempArry.length != 2) {
                RankService.errorLog.error("#class:RankService#method:updateTryRank#param:" + param + "#tempArry:" + tempArry);
                return;
            }
            final int playerId = Integer.parseInt(tempArry[0]);
            final int killNum = Integer.parseInt(tempArry[1]);
            if (playerId <= 0 || killNum <= 0) {
                RankService.errorLog.error("#class:RankService#method:updateTryRank#param:" + param + "#playerId:" + playerId + "#killNum" + killNum);
                return;
            }
            final int forceId = this.playerDao.getForceId(playerId);
            if (this.dataGetter.getNationService().getStageByForceId(forceId) < 4 && RankService.tryRank != null) {
                this.playerTryRankDao.addKillNum(playerId, killNum);
                final int beforeNum = RankService.tryRank.getValue(forceId, playerId);
                RankService.tryRank.fireRankEvent(1, new com.reign.gcld.world.common.RankData(playerId, beforeNum + killNum));
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("#class:RankService#method:updateTryRank#param:" + param, e);
        }
        RankService.timerLog.info(LogUtil.formatThreadLog("RankService", "updateTryRank", 2, System.currentTimeMillis() - start, "param:" + param));
    }
    
    @Override
    public void updatePRank(final String param) {
        final long start = System.currentTimeMillis();
        Label_0583: {
            try {
                if (StringUtils.isBlank(param)) {
                    RankService.errorLog.error("#class:RankService#method:updatePRank#param:" + param);
                    return;
                }
                final String[] tempArry = param.split("#");
                if (tempArry == null || tempArry.length != 4) {
                    RankService.errorLog.error("#class:RankService#method:updatePRank#param:" + param + "#tempArry:" + tempArry);
                    return;
                }
                final int playerId = Integer.parseInt(tempArry[0]);
                final int defForceId = Integer.parseInt(tempArry[1]);
                final int killNum = Integer.parseInt(tempArry[2]);
                if (playerId <= 0 || killNum <= 0 || defForceId < 1 || defForceId > 3) {
                    return;
                }
                final Tuple<Integer, Date> res = this.dataGetter.getNationService().getTryMap().get(defForceId);
                final boolean hasProtectManWang = res.left == 3;
                final int forceId = this.playerDao.getForceId(playerId);
                final ForceInfo fi = this.forceInfoDao.read(forceId);
                if (RankService.pRank != null) {
                    if (!hasProtectManWang || fi.getPWin() != 0) {
                        break Label_0583;
                    }
                    synchronized (this) {
                        this.playerPRankDao.addKillNum(playerId, killNum);
                        final int beforeNum = RankService.pRank.getValue(forceId, playerId);
                        RankService.pRank.fireRankEvent(1, new com.reign.gcld.world.common.RankData(playerId, beforeNum + killNum));
                        RankService.pRank.fireTotalChange(forceId, killNum, System.currentTimeMillis());
                        final long total = RankService.pRank.getTotalNum(forceId);
                        final int id = fi.getPId();
                        final int needKill = ((CdExams)this.dataGetter.getCdExamsCache().get((Object)id)).getWinConP();
                        if (total >= needKill) {
                            ManWangLingManager.getInstance().clearProtectManWangLingByToForceId(forceId);
                            ManWangLingManager.getInstance().removeManWangLingObj(forceId, 2);
                            this.forceInfoDao.updatePWin(forceId, 1);
                            this.dataGetter.getProtectService().pushPTaskResult(forceId, true);
                            for (final PlayerDto dto : Players.getAllPlayerByForceId(forceId)) {
                                final JsonDocument doc = new JsonDocument();
                                doc.startObject();
                                doc.appendJson(this.dataGetter.getProtectService().getProtectTaskInfo(dto.playerId));
                                doc.endObject();
                                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
                            }
                        }
                        break Label_0583;
                    }
                }
                RankService.errorLog.error("#class:RankService#method:updatePRank#param:" + param + "#system_start#pRank_is_null");
            }
            catch (Exception e) {
                RankService.errorLog.error("#class:RankService#method:updatePRank#param:" + param, e);
            }
        }
        RankService.timerLog.info(LogUtil.formatThreadLog("RankService", "updatePRank", 2, System.currentTimeMillis() - start, "param:" + param));
    }
    
    private void updateScore(final int playerId, final int type, final int cityId) {
        if (this.dataGetter.getActivityService().inDragonBoatFestival()) {
            final int before = this.playerScoreRankDao.getScore(playerId);
            if (1 == type) {
                this.playerScoreRankDao.updateOccupyNumAndScore(playerId, 5);
            }
            else if (2 == type) {
                this.playerScoreRankDao.updateAssistNumAndScore(playerId, 2);
            }
            else {
                this.playerScoreRankDao.updateCheerNumAndScore(playerId, 1);
            }
            final int after = this.playerScoreRankDao.getScore(playerId);
            final int dragonNum = this.fstDbNumCache.getReward(before, after);
            if (dragonNum > 0) {
                this.playerDragonDao.addDragonNumByPlayerId(playerId, dragonNum);
            }
            final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
            if (wc == null) {
                RankService.errorLog.error("#class:RankService#method:updateTodayScoreRank#cityId:" + cityId);
            }
            else if (before < this.fstDbNumCache.getMaxNum() && dragonNum > 0) {
                String displayMsg = "";
                int score = 0;
                if (1 == type) {
                    displayMsg = LocalMessages.DRAGON_OCCUPY_CITY;
                    score = 5;
                }
                else if (2 == type) {
                    displayMsg = LocalMessages.DRAGON_ASSIST_CITY;
                    score = 2;
                }
                else {
                    displayMsg = LocalMessages.DRAGON_CHEER_CITY;
                    score = 1;
                }
                final String msg = MessageFormatter.format(displayMsg, new Object[] { wc.getName(), score, after });
                this.dataGetter.getChatService().sendSystemChat("SYS2ONE", playerId, this.playerDao.getForceId(playerId), msg, null);
            }
        }
    }
    
    private void updateFeat(final int playerId, final int type, final int cityId) {
        try {
            RankService.locks[playerId % RankService.LOCKS_LEN].lock();
            if (this.playerAttributeDao.getFunctionId(playerId).toCharArray()[32] != '1' || RankService.featRank == null) {
                return;
            }
            final int before = this.playerFeatRankDao.getTotalFeat(playerId);
            int feat = 0;
            String displayMsg = "";
            if (1 == type) {
                this.playerFeatRankDao.addOccupyAndFeat(playerId, 100);
                displayMsg = LocalMessages.FEAT_RANK_OCCUPY_CITY;
                feat = 100;
            }
            else if (2 == type) {
                this.playerFeatRankDao.addAssistAndFeat(playerId, 40);
                displayMsg = LocalMessages.FEAT_RANK_ASSIST_CITY;
                feat = 40;
                final Player player = this.playerDao.read(playerId);
                if (player != null) {
                    this.dataGetter.getIndividualTaskService().sendTaskMessage(new PlayerDto(playerId, player.getForceId()), 1, "zhugong");
                }
            }
            else {
                this.playerFeatRankDao.addCheerAndFeat(playerId, 20);
                displayMsg = LocalMessages.FEAT_RANK_CHEER_CITY;
                feat = 20;
            }
            final int after = before + feat;
            final boolean flag = this.calFeatBox(playerId, before, after);
            RankService.featRank.fireRankEvent(1, new com.reign.gcld.world.common.RankData(playerId, after));
            final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
            if (wc == null) {
                RankService.errorLog.error("#class:RankService#method:updateScoreRank#cityId:" + cityId);
            }
            else if (flag) {
                final String msg = MessageFormatter.format(displayMsg, new Object[] { wc.getName(), feat, after });
                this.dataGetter.getChatService().sendSystemChat("SYS2ONE", playerId, this.playerDao.getForceId(playerId), msg, null);
            }
        }
        catch (Exception e) {
            RankService.errorLog.error("#class:RankService#method:updateFeat#playerId:" + playerId + "#type:" + type + "#cityId" + cityId, e);
            return;
        }
        finally {
            RankService.locks[playerId % RankService.LOCKS_LEN].unlock();
        }
        RankService.locks[playerId % RankService.LOCKS_LEN].unlock();
    }
    
    private int calFeat(final int beforeFeat, final int totalKillNum) {
        if (totalKillNum <= 0 || beforeFeat < 0) {
            return 0;
        }
        int beforeNeedKillNum = 0;
        for (int i = 0; i < beforeFeat; i += 10) {
            beforeNeedKillNum += i * 40 + 10000;
        }
        int remainKillNum = totalKillNum - beforeNeedKillNum;
        if (remainKillNum <= 0) {
            return 0;
        }
        int addedFeat = 0;
        while (remainKillNum > 0) {
            final int needKillNum = (beforeFeat + addedFeat) * 40 + 10000;
            if (needKillNum > remainKillNum) {
                break;
            }
            addedFeat += 10;
            remainKillNum -= needKillNum;
        }
        return addedFeat;
    }
    
    private boolean calFeatBox(final int playerId, final int before, final int after) {
        if (after <= before) {
            return false;
        }
        final double rate = 1.0 + this.dataGetter.getTechEffectCache().getTechEffect2(playerId, 34) / 100.0;
        final int featBoxNum = this.tpFtTnumCache.getReward(before, after, rate);
        if (featBoxNum > 0) {
            this.playerDragonDao.addFeatBoxNum(playerId, featBoxNum, 100, "\u589e\u52a0\u529f\u52cb\u699c\u5b9d\u7bb1");
            return true;
        }
        return false;
    }
    
    @Override
    public ScoreRank getScoreRank() {
        return RankService.scoreRank;
    }
    
    @Override
    public TryRank getTryRank() {
        return RankService.tryRank;
    }
    
    @Override
    public PRank getPRank() {
        return RankService.pRank;
    }
    
    @Override
    public void clearTryRank(final int forceId) {
        if (RankService.tryRank == null) {
            return;
        }
        final ForceInfo fi = this.dataGetter.getForceInfoDao().read(forceId);
        if (fi.getTryWin() == 1) {
            final int id = fi.getId();
            for (final com.reign.gcld.world.common.RankData rd : RankService.tryRank.getRankList(forceId)) {
                try {
                    final int rank = RankService.tryRank.getRank(1, rd.playerId, forceId);
                    if (rank <= 0 || !this.playerTryRankDao.hasReward(rd.playerId)) {
                        continue;
                    }
                    final int playerId = rd.playerId;
                    final CdExams ce = (CdExams)this.cdExamsCache.get((Object)id);
                    final int winExp = ce.getWinRExp();
                    final int winIron = ce.getWinRIron();
                    final int rankExp = this.cdExamsCache.getRankingExp(id, rank);
                    final int rankIron = this.cdExamsCache.getRankingIron(id, rank);
                    this.playerService.updateExpAndPlayerLevel(playerId, winExp, "\u56fd\u5bb6\u8bd5\u70bc\u80dc\u5229\u5956\u52b1\u7ecf\u9a8c");
                    this.playerService.updateExpAndPlayerLevel(playerId, rankExp, "\u56fd\u5bb6\u8bd5\u70bc\u6392\u540d\u5956\u52b1\u7ecf\u9a8c");
                    this.playerResourceDao.addIronIgnoreMax(playerId, winIron, "\u56fd\u5bb6\u8bd5\u70bc\u80dc\u5229\u83b7\u53d6\u9554\u94c1", true);
                    this.playerResourceDao.addIronIgnoreMax(playerId, rankIron, "\u56fd\u5bb6\u8bd5\u70bc\u6392\u540d\u83b7\u53d6\u9554\u94c1", true);
                    final String mailMsg = MessageFormatter.format(LocalMessages.TRY_MAIL_CONTENT, new Object[] { winExp + rankExp, winIron + rankIron });
                    this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.TRY_MAIL_HEAD, mailMsg, 1, playerId, 0);
                }
                catch (Exception e) {
                    RankService.errorLog.error("class:rankService#method:clearTryRank#have_exception", e);
                }
            }
        }
        RankService.tryRank.clearByForceId(forceId);
        this.playerTryRankDao.clear(forceId);
    }
    
    @Override
    public void clearPRank(final int forceId) {
        if (RankService.pRank == null) {
            return;
        }
        final ForceInfo fi = this.dataGetter.getForceInfoDao().read(forceId);
        if (fi.getPWin() == 1) {
            final int id = fi.getPId();
            for (final com.reign.gcld.world.common.RankData rd : RankService.pRank.getRankList(forceId)) {
                try {
                    final int rank = RankService.pRank.getRank(1, rd.playerId, forceId);
                    if (rank <= 0 && !this.playerTryRankDao.hasReward(rd.playerId)) {
                        continue;
                    }
                    final int playerId = rd.playerId;
                    final int rankExp = this.cdExamsCache.getPRankingExp(id, rank);
                    final int rankIron = this.cdExamsCache.getPRankingIron(id, rank);
                    this.playerService.updateExpAndPlayerLevel(playerId, rankExp, "\u4fdd\u62a4\u86ee\u738b\u6392\u540d\u5956\u52b1\u7ecf\u9a8c");
                    this.playerResourceDao.addIronIgnoreMax(playerId, rankIron, "\u4fdd\u62a4\u86ee\u738b\u6392\u540d\u83b7\u53d6\u9554\u94c1", true);
                    final String mailMsg = MessageFormatter.format(LocalMessages.PROTECT_TASK_MAIL_CONTENT, new Object[] { rankExp, rankIron });
                    this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.PROTECT_TASK_MAIL_HEAD, mailMsg, 1, playerId, 0);
                }
                catch (Exception e) {
                    RankService.errorLog.error("class:rankService#method:clearPRank#have_exception", e);
                }
            }
        }
        RankService.pRank.clearByForceId(forceId);
        this.playerPRankDao.clear(forceId);
    }
    
    @Override
    public Tuple<Integer, Long> getNextInvadeInfo(final int forceId) {
        try {
            if (forceId <= 0 || forceId > 3) {
                return null;
            }
            final int timeDivision = getTimeDevision();
            if (timeDivision <= 0) {
                return null;
            }
            if (RankService.needShowNextInvade[forceId - 1]) {
                final long startTime = this.getStartTime();
                return this.ktBjCache.getNextInvadeInfo(startTime);
            }
            return null;
        }
        catch (Exception e) {
            RankService.errorLog.error("getNextInvadeInfo exception...forceId:" + forceId);
            RankService.errorLog.error(e.getMessage());
            RankService.errorLog.error(this, e);
            return null;
        }
    }
    
    @Override
    public FeatRank getFeatRank() {
        return RankService.featRank;
    }
    
    @Override
    public void nationTaskHJCityOccupy(final int forceId, final int cityId) {
        try {
            boolean cityFlag = false;
            if (cityId == NationTaskInfo.cityArray1[0] || cityId == NationTaskInfo.cityArray1[1] || cityId == NationTaskInfo.cityArray1[2] || cityId == 105) {
                cityFlag = true;
                RankService.errorLog.error("nationTaskHJCityOccupy.... cityId:" + cityId + "forceId:" + forceId);
            }
            boolean shouldUpdate = false;
            if (RankService.taskInfo == null || RankService.taskInfo.getState() != 1) {
                return;
            }
            final int serial = RankService.taskInfo.getSerial();
            if (serial == 1) {
                boolean flag = false;
                int index = 0;
                final int[] array = NationTaskInfo.cityArray1;
                for (int i = 0; i < array.length; ++i) {
                    if (array[i] == cityId) {
                        flag = true;
                        index = i;
                        break;
                    }
                }
                if (cityFlag) {
                    RankService.errorLog.error("nationTaskHJCityOccupy.... index:" + index + "infoForceId:" + RankService.taskInfo.getCityBelong()[index]);
                }
                if (!flag || forceId == 104 || RankService.taskInfo.getCityBelong()[index] != 104) {
                    return;
                }
                final Tuple<Integer, Integer> rewardsTuple = this.sendRewardForCityOccupiers(forceId);
                RankService.taskInfo.getCityBelong()[index] = forceId;
                shouldUpdate = true;
                try {
                    final WorldCity worldCity = (WorldCity)this.worldCityCache.get((Object)cityId);
                    if (worldCity != null) {
                        final String forceName = WorldCityCommon.nationIdNameMap.get(forceId);
                        final String msg = MessageFormatter.format(LocalMessages.NATION_TASK_HJ_OCCUPY_CITY, new Object[] { worldCity.getName(), forceName, forceName });
                        this.chatService.sendSystemChat("GLOBAL", 0, 0, msg, null);
                        final Group group = GroupManager.getInstance().getGroup(ChatType.WORLD.toString());
                        if (group != null) {
                            final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_NATION_TASK_STATE_CHANGE.getModule(), JsonBuilder.getJson(JsonBuilder.getSimpleJson("recover", index))));
                            group.notify(WrapperUtil.wrapper(PushCommand.PUSH_NATION_TASK_STATE_CHANGE.getCommand(), 0, bytes));
                        }
                        if (rewardsTuple != null) {
                            String rewardMsg = null;
                            int exp = rewardsTuple.left;
                            final int iron = rewardsTuple.right;
                            if (RankService.hasDoubleReward) {
                                exp *= 2;
                            }
                            if (iron > 0) {
                                rewardMsg = MessageFormatter.format(LocalMessages.NATION_TASK_CITY_REWARD, new Object[] { worldCity.getName(), exp, iron });
                            }
                            else {
                                rewardMsg = MessageFormatter.format(LocalMessages.NATION_TASK_CITY_REWARDEXP, new Object[] { worldCity.getName(), exp });
                            }
                            this.chatService.sendSystemChat("COUNTRY", 0, forceId, rewardMsg, null);
                        }
                    }
                }
                catch (Exception e) {
                    RankService.errorLog.error(this, e);
                }
                RankService.errorLog.error("nationTaskHJCityOccupy ....forceId:" + forceId + " cityId:" + cityId);
            }
            else if (cityId == 105) {
                final int nowId = RankService.taskInfo.getAttackCityForceId();
                if (nowId == 104 && forceId != 104) {
                    this.nationTaskDao.resetTaskIsWinByForceId(forceId);
                    RankService.taskInfo.setAttackCityForceId(forceId);
                    shouldUpdate = true;
                    RankService.errorLog.error("nationTaskHJCityOccupy ....forceId:" + forceId + " cityId:" + cityId);
                }
            }
            else if (cityId == 193 || cityId == 132 || cityId == 60) {
                boolean flag = false;
                int index = 0;
                final int[] array = NationTaskInfo.cityArray2;
                for (int i = 0; i < array.length; ++i) {
                    if (array[i] == cityId) {
                        flag = true;
                        index = i;
                        break;
                    }
                }
                if (!flag || forceId == 104 || RankService.taskInfo.getTempleBelong()[index] != 104) {
                    return;
                }
                RankService.taskInfo.getTempleBelong()[index] = forceId;
                RankService.errorLog.error("nationTaskHJCityOccupy ....forceId:" + forceId + " cityId:" + cityId);
                try {
                    final JsonDocument doc = new JsonDocument();
                    doc.startObject();
                    doc.createElement("recover", index + 5);
                    if (RankService.taskInfo != null && RankService.taskInfo.isCanAtt() && RankService.taskInfo.getState() == 1) {
                        final List<Integer> cityIds = RankService.taskInfo.getUnOccupiedCity();
                        final int jitanNum = RankService.taskInfo.getTempleNum();
                        if (cityIds != null && jitanNum == cityIds.size()) {
                            doc.createElement("jitanNum", jitanNum);
                            doc.startArray("jitan");
                            for (int j = 0; j < jitanNum; ++j) {
                                doc.startObject();
                                doc.createElement("cityId", cityIds.get(j));
                                doc.endObject();
                            }
                            doc.endArray();
                        }
                    }
                    doc.endObject();
                    final Group group2 = GroupManager.getInstance().getGroup(ChatType.WORLD.toString());
                    if (group2 != null) {
                        final byte[] bytes2 = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_NATION_TASK_STATE_CHANGE.getModule(), JsonBuilder.getJson(doc.toByte())));
                        group2.notify(WrapperUtil.wrapper(PushCommand.PUSH_NATION_TASK_STATE_CHANGE.getCommand(), 0, bytes2));
                    }
                }
                catch (Exception e2) {
                    RankService.errorLog.error(this, e2);
                }
            }
            else if (cityId == 135 || cityId == 103 || cityId == 139) {
                boolean flag = false;
                int index = 0;
                final int[] array = NationTaskInfo.cityArray1;
                for (int i = 0; i < array.length; ++i) {
                    if (array[i] == cityId) {
                        flag = true;
                        index = i;
                        break;
                    }
                }
                if (cityFlag) {
                    RankService.errorLog.error("nationTaskHJCityOccupy.... index:" + index + "infoForceId:" + RankService.taskInfo.getCityBelong()[index]);
                }
                if (!flag) {
                    return;
                }
                RankService.taskInfo.getCityBelong()[index] = forceId;
                shouldUpdate = true;
            }
            if (shouldUpdate) {
                String info = SymbolUtil.toString(RankService.taskInfo.getCityBelong(), ";");
                info = String.valueOf(info) + ";" + RankService.taskInfo.getAttackCityForceId();
                this.nationTaskDao.updateTaskRelativeInfo(info);
            }
        }
        catch (Exception e3) {
            RankService.errorLog.error(this, e3);
        }
    }
    
    private Tuple<Integer, Integer> sendRewardForCityOccupiers(final int forceId) {
        Tuple<Integer, Integer> rewardsTuple = null;
        final ForceInfo forceInfo = this.forceInfoDao.read(forceId);
        try {
            if (forceInfo != null) {
                rewardsTuple = this.ktHjCache.getReward(forceInfo.getForceLv(), 1, 1);
                if (rewardsTuple != null) {
                    final List<TaskKillInfo> rewardPlayers = this.taskKillInfoDao.getByForceId(forceId);
                    if (rewardPlayers != null && !rewardPlayers.isEmpty()) {
                        final JsonDocument doc = new JsonDocument();
                        doc.startObject();
                        if (rewardsTuple.left > 0) {
                            doc.createElement("exp", rewardsTuple.left);
                        }
                        if (rewardsTuple.right > 0) {
                            doc.createElement("iron", rewardsTuple.right);
                        }
                        doc.endObject();
                        int exp = rewardsTuple.left;
                        final int iron = rewardsTuple.right;
                        if (RankService.hasDoubleReward) {
                            exp *= 2;
                        }
                        for (final TaskKillInfo info : rewardPlayers) {
                            try {
                                RewardType.reward(this.dataGetter, 10, exp, info.getPlayerId(), 3);
                                RewardType.reward(this.dataGetter, 4, iron, info.getPlayerId(), 3);
                                Players.push(info.getPlayerId(), PushCommand.PUSH_HJ_REWARD, doc.toByte());
                            }
                            catch (Exception e) {
                                RankService.errorLog.error(this, e);
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e2) {
            RankService.errorLog.error(this, e2);
        }
        return rewardsTuple;
    }
    
    public static boolean canEnterHJCenterCity(final int cityId) {
        try {
            if (RankService.taskInfo == null || RankService.taskInfo.getState() == 2) {
                return true;
            }
            final int serial = RankService.taskInfo.getSerial();
            final int state = RankService.taskInfo.getState();
            if (cityId != 105) {
                final int flag = CityService.getCityFlag(cityId);
                return flag != 1;
            }
            if (serial == 1 && state == 0) {
                final int flag = CityService.getCityFlag(cityId);
                return flag != 1;
            }
            return RankService.taskInfo.isCanAtt();
        }
        catch (Exception e) {
            RankService.errorLog.error(RankService.class, e);
            return true;
        }
    }
    
    @Override
    public byte[] investYx(final PlayerDto playerDto, final int type) {
        if (type > 2 || type < 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final TimeForNationTask time1 = new TimeForNationTask(RankService.TIME_DIVISION4[0]);
        final TimeForNationTask time2 = new TimeForNationTask(RankService.TIME_DIVISION4[1]);
        if (!TimeForNationTask.isInTime(time1, time2)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NATION_TASK_YX_TIME_IS_NOT_PROPER);
        }
        final int forceId = playerDto.forceId;
        if (RankService.festival == null || RankService.festival.miracleState != 0 || RankService.festival.getNationTaskIsOver(forceId)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NATION_TASK_YX_NO_TASK);
        }
        final int serial = RankService.festival.curSerial[forceId - 1];
        final int percentage = RankService.festival.getResourcePct(forceId, type);
        if (percentage >= 100) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.NATION_TASK_YX_NO_TASK);
        }
        final int gold = this.getCostGold(serial, percentage);
        if (gold <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Player player = this.playerDao.read(playerDto.playerId);
        if (!this.playerDao.consumeGold(player, gold, "\u9f13\u821e\u5143\u5bb5\u8f66\u961f\u6d88\u8017\u91d1\u5e01")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10003);
        }
        final boolean update = RankService.festival.updateResourceByGold(type, forceId);
        if (!update) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        RankService.festival.addPlayerInArray(playerDto.playerName, type);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private int getCostGold(final int serial, final int percentage) {
        final int p = 100 - percentage;
        return p * this.getCostGold(serial);
    }
    
    private int getCostGold(final int serial) {
        if (serial <= 4) {
            return 1;
        }
        return 5;
    }
    
    @Override
    public byte[] eatLantern(final PlayerDto playerDto) {
        if (!EventUtil.isEventTime(21)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_NO_ACTIVITY);
        }
        final int playerId = playerDto.playerId;
        final int forceId = playerDto.forceId;
        final PlayerEvent pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 21);
        if (pe == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_NO_TARK_PART_IN);
        }
        final long now = System.currentTimeMillis();
        final long startTime = TimeUtil.getSpecialTime((int)LanternEvent.timeTuple.left, (int)LanternEvent.timeTuple.right);
        if (now < startTime) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_NOT_START);
        }
        if (LanternTask.state != LanternTask.State.START) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_WAIT_NEXT_ROUND);
        }
        final int beforeNum = RankService.lanternRank.getValue(forceId, playerId);
        if (beforeNum <= 0) {
            if (pe.getParam1() <= 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.LANTERN_NO_ROUND);
            }
            this.dataGetter.getPlayerEventDao().reduceParam1(playerId, 21, 1);
        }
        if (beforeNum < 100) {
            final int afterNum = beforeNum + 1;
            this.dataGetter.getPlayerEventDao().addParam3(playerId, 21, 1);
            RankService.lanternRank.fireRankEvent(1, new com.reign.gcld.world.common.RankData(playerId, afterNum));
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public LanternRank getLanternRank() {
        return RankService.lanternRank;
    }
    
    @Override
    public NationFestival getNationFestival() {
        return RankService.festival;
    }
    
    class PlayerRank
    {
        public int levelRank;
        public Node<RankData> levelData;
        public byte[] levelRankByte;
        
        PlayerRank() {
            this.levelRank = -1;
            this.levelData = null;
            this.levelRankByte = null;
        }
    }
    
    class SdThreadCheck extends Thread
    {
        public long sleepTime;
        
        SdThreadCheck() {
            this.sleepTime = 1000L;
        }
        
        @Override
        public void run() {
            while (true) {
                Label_0078: {
                    try {
                        Thread.sleep(60000L);
                        break Label_0078;
                    }
                    catch (Exception e) {
                        RankService.errorLog.error(this, e);
                        break Label_0078;
                    }
                    try {
                        final long start = System.currentTimeMillis();
                        RankService.this.doCheckSdTask();
                        RankService.timerLog.info(LogUtil.formatThreadLog("SdThreadCheck", "run", 2, System.currentTimeMillis() - start, "param:"));
                        Thread.sleep(this.sleepTime);
                    }
                    catch (Exception e) {
                        RankService.errorLog.error(this, e);
                    }
                }
                if (RankService.getTimeDevision() <= 0) {
                    return;
                }
                continue;
            }
        }
    }
    
    private final class PushTaskOverInfo
    {
        int taskType;
        int state;
        int page;
        int lv;
        int rank;
        int serial;
        int city;
        int force;
        int attType;
        
        public PushTaskOverInfo() {
            this.attType = 1;
        }
    }
}
