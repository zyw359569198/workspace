package com.reign.gcld.player.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.building.service.*;
import com.reign.gcld.charge.service.*;
import com.reign.gcld.task.service.*;
import com.reign.gcld.task.dao.*;
import com.reign.gcld.tavern.service.*;
import com.reign.gcld.store.service.*;
import com.reign.gcld.politics.service.*;
import com.reign.gcld.market.service.*;
import com.reign.gcld.mail.service.*;
import com.reign.gcld.market.dao.*;
import com.reign.gcld.gift.service.*;
import com.reign.gcld.general.dao.*;
import com.reign.gcld.battle.dao.*;
import com.reign.gcld.slave.service.*;
import com.reign.gcld.slave.dao.*;
import com.reign.gcld.tech.dao.*;
import com.reign.gcld.building.dao.*;
import com.reign.gcld.world.dao.*;
import com.reign.gcld.kfwd.service.*;
import com.reign.gcld.user.dao.*;
import com.reign.gcld.kfgz.service.*;
import com.reign.gcld.kfwd.manager.*;
import com.reign.gcld.feat.dao.*;
import com.reign.gcld.gift.dao.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.score.dao.*;
import com.reign.gcld.activity.dao.*;
import com.reign.gcld.rank.dao.*;
import com.reign.gcld.kfzb.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.pay.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.general.domain.*;
import java.util.concurrent.locks.*;
import com.reign.gcld.common.log.*;
import org.apache.commons.logging.*;
import java.util.concurrent.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.util.*;
import com.reign.plugin.yx.*;
import com.reign.gcld.*;
import com.reign.gcld.user.dto.*;
import com.reign.gcld.task.domain.*;
import com.reign.gcld.log.*;
import java.text.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.battle.service.*;
import com.reign.util.*;
import com.reign.gcld.team.service.*;
import org.apache.commons.lang.*;
import com.reign.gcld.slave.domain.*;
import com.reign.gcld.juben.service.*;
import com.reign.gcld.activity.service.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.pay.service.*;
import com.reign.gcld.event.util.*;
import com.reign.gcld.slave.util.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.event.common.*;
import com.reign.gcld.courtesy.common.*;
import com.reign.gcld.rank.service.*;
import com.reign.gcld.pay.domain.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.battle.scene.*;
import com.reign.gcld.rank.domain.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.feat.domain.*;
import com.reign.gcld.market.domain.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.world.domain.*;
import com.reign.kf.comm.entity.kfwd.response.*;
import com.reign.gcld.huizhan.domain.*;
import com.reign.gcld.juben.common.*;
import com.reign.gcld.event.domain.*;
import com.reign.gcld.incense.domain.*;
import com.reign.gcld.task.reward.*;
import com.reign.gcld.player.common.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.rank.common.*;
import com.reign.gcld.task.message.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.common.util.characterFilter.*;
import com.reign.gcld.common.web.*;
import com.reign.gcld.system.domain.*;
import com.reign.gcld.world.common.*;
import java.util.*;
import com.reign.gcld.user.domain.*;
import com.reign.gcld.gift.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.building.domain.*;
import com.reign.gcld.activity.domain.*;
import com.alibaba.fastjson.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.plugin.yx.common.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.common.*;

@Component("playerService")
public class PlayerService implements IPlayerService, InitializingBean
{
    private static final Logger log;
    private static final Log opReport;
    private static final ErrorLogger error;
    private static final DayReportLogger dayReport;
    private static final String YX_360 = "360";
    private static final String G_KEY = "gcld";
    private static Map<String, Integer> goldMap;
    private static Map<Integer, String> rewardMap;
    private static int SIZE;
    private static final Logger timerLog;
    @Autowired
    private IPlayerNameDao playerNameDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private IBuildingService buildingService;
    @Autowired
    private IBuildingOutputCache buildingOutputCache;
    @Autowired
    private IPlayerConstantsDao playerConstantsDao;
    @Autowired
    private IChargeItemService chargeItemService;
    @Autowired
    private IPlayerTaskService playerTaskService;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private SerialCache serialCache;
    @Autowired
    private IPlayerTaskDao playerTaskDao;
    @Autowired
    private CCache cCache;
    @Autowired
    private ITavernService tavernService;
    @Autowired
    private IStoreService storeService;
    @Autowired
    private IPoliticsService politicsService;
    @Autowired
    private IRankService rankService;
    @Autowired
    private IMarketService marketService;
    @Autowired
    private IPlayerSearchDao playerSearchDao;
    @Autowired
    private IOccupyService occupyService;
    @Autowired
    private IPlayerBattleAutoDao playerBattleAutoDao;
    @Autowired
    private IMailService mailService;
    @Autowired
    private IPlayerMarketDao playerMarketDao;
    @Autowired
    private IGiftService giftServiceDao;
    @Autowired
    private TroopCache troopCache;
    @Autowired
    private UpdateRewardCache updateRewardCache;
    @Autowired
    private IPlayerBatRankDao playerBatRankDao;
    @Autowired
    private IPlayerGeneralMilitaryDao playerGeneralMilitaryDao;
    @Autowired
    private IDataGetter taskDataGetter;
    @Autowired
    private IPlayerBattleAttributeDao playerBattleAttributeDao;
    @Autowired
    private IPlayerStoreDao playerStoreDao;
    @Autowired
    private ChargeitemCache chargeitemCache;
    @Autowired
    private ISlaveService slaveService;
    @Autowired
    private PlayerNamesCache playerNamesCache;
    @Autowired
    private IPlayerSlaveDao playerSlaveDao;
    @Autowired
    private IPlayerTechDao playerTechDao;
    @Autowired
    private IUserRewardDao userRewardDao;
    @Autowired
    private IPlayerGiftDao playerGiftDao;
    @Autowired
    private IGiftInfoDao giftInfoDao;
    @Autowired
    private BuildingDrawingCache buildingDrawingCache;
    @Autowired
    private BluePrintDao bluePrintDao;
    @Autowired
    private IBattleService battleService;
    @Autowired
    private IPlayerWorldDao playerWorldDao;
    @Autowired
    private DataPushCenterUtil dataPushCenterUtil;
    @Autowired
    private ITaskKillInfoDao taskKillInfoDao;
    @Autowired
    private IKfwdMatchService kfwdMatchService;
    @Autowired
    private IPlayerLvExpDao playerLvExpDao;
    @Autowired
    private IActivityService activityService;
    @Autowired
    private IPowerService powerService;
    @Autowired
    private UserLoginInfoDao userLoginInfoDao;
    @Autowired
    private IChatService chatService;
    @Autowired
    private IKfgzSeasonService kfgzSeasonService;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IActivityDao activityDao;
    @Autowired
    private KfwdMatchManager kfwdMatchManager;
    @Autowired
    private IPlayerDragonDao playerDragonDao;
    @Autowired
    private HallsCache hallsCache;
    @Autowired
    private IPlayerIronDao playerIronDao;
    @Autowired
    private IPlayerFeatRankDao playerFeatRankDao;
    @Autowired
    private IPlayerOnlineRewardDao playerOnlineRewardDao;
    @Autowired
    private IPlayerQuenchingRelativeDao playerQuenchingRelativeDao;
    @Autowired
    private IPlayerVipDao playerVipDao;
    @Autowired
    private IPlayerOfficeRelativeDao playerOfficeRelativeDao;
    @Autowired
    private FstDbNumCache fstDbNumCache;
    @Autowired
    private IPlayerScoreRankDao playerScoreRankDao;
    @Autowired
    private IPayService payService;
    @Autowired
    private IBonusActivityDao bonusActivityDao;
    @Autowired
    private IForceInfoDao forceInfoDao;
    @Autowired
    IYxOperation yxOperation;
    @Autowired
    private IKfzbFeastService kfzbFeastService;
    @Autowired
    private GiftTxCache giftTxCache;
    @Autowired
    private PlayerVipTxDao playerVipTxDao;
    @Autowired
    private ILoginRewardDao loginRewardDao;
    private static final Logger rtLog;
    private static Set<String> NAME_SET;
    private static ConcurrentHashMap<Integer, Integer> forceMap;
    private static ConcurrentHashMap<String, Integer> forceRewardMap;
    public static ConcurrentHashMap<Integer, Integer> topLvMap;
    public static List<PlayerTrainningInfoDto> trainningInfo;
    private static Lock lock;
    private static ReentrantLock[] locks;
    private static final int LOCKS_LEN;
    
    static {
        log = CommonLog.getLog(PlayerService.class);
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
        error = new ErrorLogger();
        dayReport = new DayReportLogger();
        (PlayerService.goldMap = new HashMap<String, Integer>()).put("com.aoshitang.mjcshd.60gold", 60);
        PlayerService.goldMap.put("com.aoshitang.mjcshd.120gold", 120);
        PlayerService.goldMap.put("com.aoshitang.mjcshd.300gold", 300);
        PlayerService.goldMap.put("com.aoshitang.mjcshd.600gold", 600);
        PlayerService.goldMap.put("com.aoshitang.mjcshd.1280gold", 1280);
        PlayerService.goldMap.put("com.aoshitang.mjcshd.5680gold", 5680);
        (PlayerService.rewardMap = new HashMap<Integer, String>()).put(1, "lumber,5000");
        PlayerService.rewardMap.put(2, "gold,10");
        PlayerService.rewardMap.put(3, "food,10000");
        PlayerService.rewardMap.put(4, "food,10000;copper,10000;lumber,10000");
        PlayerService.rewardMap.put(5, "gold,15");
        PlayerService.rewardMap.put(6, "phantom,1");
        PlayerService.rewardMap.put(7, "gold,20");
        PlayerService.SIZE = PlayerService.rewardMap.size();
        timerLog = new TimerLogger();
        rtLog = new RTReportLogger();
        PlayerService.NAME_SET = new ConcurrentSkipListSet<String>();
        PlayerService.forceMap = new ConcurrentHashMap<Integer, Integer>();
        PlayerService.forceRewardMap = new ConcurrentHashMap<String, Integer>();
        PlayerService.topLvMap = new ConcurrentHashMap<Integer, Integer>();
        PlayerService.trainningInfo = new ArrayList<PlayerTrainningInfoDto>();
        PlayerService.lock = new ReentrantLock();
        PlayerService.locks = new ReentrantLock[10240];
        LOCKS_LEN = PlayerService.locks.length;
        for (int i = 0; i < PlayerService.LOCKS_LEN; ++i) {
            PlayerService.locks[i] = new ReentrantLock(false);
        }
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        PlayerService.log.info("::\u8f7d\u5165\u7528\u6237\u540d\u79f0\u5f00\u59cb::");
        PlayerService.NAME_SET.clear();
        final List<PlayerName> pnList = this.playerNameDao.getModels();
        for (final PlayerName pn : pnList) {
            PlayerService.NAME_SET.add(pn.getPlayerName().toLowerCase().trim());
        }
        PlayerService.log.info("::\u8f7d\u5165\u7528\u6237\u540d\u79f0\u7ed3\u675f::");
        PlayerService.forceMap.put(1, this.playerDao.getForceMemberCount(1));
        PlayerService.forceMap.put(2, this.playerDao.getForceMemberCount(2));
        PlayerService.forceMap.put(3, this.playerDao.getForceMemberCount(3));
    }
    
    @Override
    public boolean addPlayerName(final String playerName) {
        return playerName != null && PlayerService.NAME_SET.add(playerName.toLowerCase().trim());
    }
    
    @Override
    public boolean validatePlayerName(final String playerName) {
        return playerName != null && PlayerService.NAME_SET.contains(playerName.toLowerCase().trim());
    }
    
    @Transactional
    @Override
    public Tuple<byte[], Boolean> addNewPlayer(final String playerName, final String userId, final String yx, final String yxSource, final int forceId, final Request request) {
        final Tuple<byte[], Boolean> tuple = new Tuple();
        tuple.right = false;
        final Integer reward = PlayerService.forceRewardMap.get(String.valueOf(userId) + yx);
        if (reward == null) {
            final List<Player> list = this.playerDao.getPlayerByUserId(userId, yx);
            if (list != null && list.size() > 0) {
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                doc.createElement("playerId", list.get(0).getPlayerId());
                doc.endObject();
                tuple.left = JsonBuilder.getJson(State.SUCCESS, doc.toByte());
                return tuple;
            }
        }
        if (forceId > 3 || forceId < 0) {
            tuple.left = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            return tuple;
        }
        int count = this.playerDao.getRoleCount(userId, yx);
        if (count >= WebUtil.getMaxPlayerNum(yx)) {
            tuple.left = JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10007);
            return tuple;
        }
        final Player p = this.getNewPlayer(playerName, userId, yx, yxSource, forceId);
        final int res = this.playerDao.create(p);
        if (res >= 1) {
            ++count;
            PlayerService.rtLog.info(LogUtil.formatReportCreateLog(p.getPlayerId(), p.getPlayerName(), p.getPlayerLv(), p.getUserId(), p.getYx(), "c", count, p.getForceId(), p.getConsumeLv(), yxSource, request));
            tuple.right = true;
            this.playerTaskService.startFreshManTask(p);
            try {
                final String serverid = PluginContext.configuration.getServerId(yx);
                final String serverNameServeridPlayerid = String.valueOf(yx) + "_" + serverid + "_" + p.getPlayerId();
                this.playerDao.updatePlayerSSP(p.getPlayerId(), serverNameServeridPlayerid);
                PlayerService.dayReport.info(LogUtil.formatNewUserCreateLog(yx, userId, p.getPlayerId(), serverNameServeridPlayerid, Environment.getMainVersion()));
            }
            catch (Exception e) {
                PlayerService.log.error(this, e);
            }
        }
        final int playerId = p.getPlayerId();
        final PlayerAttribute playerAttribute = this.getPlayerAttribute(playerId, forceId);
        this.playerAttributeDao.create(playerAttribute);
        final LoginReward lr = new LoginReward();
        lr.setPlayerId(playerId);
        lr.setTodayFirstLoginTime(new Date());
        lr.setTotalDay(1);
        lr.setHaveReward(1);
        this.loginRewardDao.create(lr);
        final PlayerVip pv = new PlayerVip();
        pv.setPlayerId(playerId);
        pv.setVipStatus("1#00#00#10#00#00#0#0#0#0#0");
        pv.setVipRemainingTimes("");
        this.playerVipDao.create(pv);
        final int consumeLv = Integer.parseInt(Configuration.getProperty("gcld.player.consumeLv"));
        if (consumeLv > 0) {
            if (consumeLv >= 5) {
                final ArmiesReward armiesReward = (ArmiesReward)this.dataGetter.getArmiesRewardCache().get((Object)(-5));
                if (armiesReward == null) {
                    PlayerService.log.error("addNewPlayer armiesReward of vip 5 is null from ArmiesRewardCache. armyId=-5");
                }
                else {
                    final PlayerArmyReward playerArmyReward = new PlayerArmyReward();
                    playerArmyReward.setPlayerId(playerId);
                    playerArmyReward.setPowerId(armiesReward.getPowerId());
                    playerArmyReward.setArmyId(armiesReward.getId());
                    playerArmyReward.setFirst(0);
                    playerArmyReward.setExpireTime(new Date());
                    playerArmyReward.setNpcLost(null);
                    final int num = OneVsRewardNpcBuilder.getMaxHp(this.dataGetter, armiesReward.getId());
                    playerArmyReward.setHp(num);
                    playerArmyReward.setHpMax(num);
                    playerArmyReward.setState(0);
                    playerArmyReward.setBuyCount(0);
                    playerArmyReward.setFirstWin(0);
                    playerArmyReward.setWinCount(0);
                    this.dataGetter.getPlayerArmyRewardDao().create(playerArmyReward);
                }
            }
            final Tuple<Boolean, String> result = this.payService.updateVipTimes(0, consumeLv, playerId);
            if (result.left) {
                this.playerVipDao.setVipRemainingTimes(playerId, result.right);
            }
        }
        if (res >= 1) {
            final UserDto userDto = (UserDto)request.getSession().getAttribute("user");
            final PlayerDto dto = new PlayerDto();
            dto.playerId = p.getPlayerId();
            dto.playerName = p.getPlayerName();
            dto.playerLv = p.getPlayerLv();
            dto.loginTime = ((userDto != null) ? userDto.loginTime : System.currentTimeMillis());
            dto.userId = userId;
            dto.yx = yx;
            dto.forceId = p.getForceId();
            dto.consumeLv = p.getConsumeLv();
            dto.yxSource = p.getYxSource();
            final char[] cs = playerAttribute.getFunctionId().toCharArray();
            dto.cs = cs;
            dto.gm = p.getGm();
            request.getSession().setAttribute("PLAYER", dto);
        }
        final PlayerBattleAttribute pba = new PlayerBattleAttribute();
        pba.setPlayerId(playerId);
        pba.setSupportTime(new Date());
        pba.setType(0);
        pba.setArmiesAutoCount(0);
        pba.setArmiesBattleOrder("");
        pba.setYoudiTime(0L);
        pba.setChujiTime(0L);
        pba.setPhantomWorkShopLv(0);
        pba.setPhantomToday(0);
        pba.setVip3PhantomCount(0);
        pba.setAutoStrategy(0);
        pba.setShoumaiManzuTime(0L);
        pba.setWinTimes(0);
        pba.setFailTimes(0);
        pba.setTeamTimes(0);
        pba.setEventGemCount(0);
        pba.setEventGemCountToday(0);
        pba.setEventJiebingCountToday(0);
        pba.setEventXtysCountToday(0);
        pba.setEventSdlrCountToday(0);
        pba.setChangebat(0);
        pba.setActivityBatExp(0);
        pba.setEventNationalTreasureCountToday(0);
        pba.setEventWorldDramaCountToday(0);
        pba.setEventTrainningTokenCountToday(0);
        pba.setEventSlaveCountToday(0);
        this.playerBattleAttributeDao.create(pba);
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("playerId", playerId);
        if (reward != null && reward == forceId) {
            this.playerDao.addSysGold(p, 50, "\u9009\u62e9\u6307\u5b9a\u52bf\u529b\u5956\u52b1\u91d1\u5e01");
            doc2.startObject("reward");
            doc2.createElement("kind", (Object)LocalMessages.T_COMM_10009);
            doc2.createElement("value", 50);
            doc2.endObject();
            PlayerService.forceRewardMap.remove(String.valueOf(userId) + yx);
        }
        this.initPlayerConstants(playerId);
        this.initPlayerResource(playerId);
        this.initTavernAndStore(playerId);
        this.initPlayerOfficerRelative(playerId);
        this.buildingService.assignedBuildingWork(playerId);
        final PlayerTask playerTask = new PlayerTask();
        playerTask.setPlayerId(playerId);
        playerTask.setTaskId(1);
        playerTask.setState(0);
        playerTask.setProcess(0);
        playerTask.setType(1);
        playerTask.setGroupId(0);
        playerTask.setStartTime(System.currentTimeMillis());
        this.playerTaskDao.create(playerTask);
        final PlayerIron pi = new PlayerIron();
        pi.setPlayerId(playerId);
        pi.setIron(0);
        pi.setReward(0);
        pi.setReceived(0);
        this.playerIronDao.create(pi);
        final BonusActivity ba = new BonusActivity();
        ba.setPlayerId(playerId);
        ba.setBonusGold(0);
        this.bonusActivityDao.create(ba);
        PlayerService.forceMap.put(forceId, PlayerService.forceMap.get(forceId) + 1);
        doc2.endObject();
        tuple.left = JsonBuilder.getJson(State.SUCCESS, doc2.toByte());
        final Logger rewardUserLogger = new RewardUserLogger();
        final String USER_REWARD_OPEN = Configuration.getProperty(yx, "gcld.old.user.reward.open");
        if (Integer.parseInt(USER_REWARD_OPEN) == 1) {
            if (count == 1) {
                Tuple<Boolean, String> result2 = new Tuple();
                result2 = this.rewardUser(Integer.parseInt(userId), playerId, yx);
                rewardUserLogger.info(result2.right);
            }
            else {
                rewardUserLogger.info(MessageFormat.format(LocalMessages.REWARD_USER_FAILURE_6, userId));
            }
        }
        return tuple;
    }
    
    private void initPlayerOfficerRelative(final int playerId) {
        final PlayerOfficeRelative playerOfficeRelative = new PlayerOfficeRelative();
        playerOfficeRelative.setOfficerId(37);
        playerOfficeRelative.setOfficerNpc(19);
        playerOfficeRelative.setPlayerId(playerId);
        playerOfficeRelative.setReputationTime(new Date());
        playerOfficeRelative.setSalaryGotToday(0);
        playerOfficeRelative.setLastOfficerId(0);
        this.playerOfficeRelativeDao.create(playerOfficeRelative);
    }
    
    private void initTavernAndStore(final int playerId) {
        this.tavernService.refreshGeneral(playerId, 1, true, false);
        this.tavernService.refreshGeneral(playerId, 2, true, false);
        final Date nowDate = new Date();
        final PlayerStore playerStore = new PlayerStore();
        playerStore.setPlayerId(playerId);
        playerStore.setStoreState(1);
        playerStore.setEquipRefreshTime(0);
        playerStore.setToolRefreshTime(0);
        playerStore.setNextEquipDate(nowDate);
        playerStore.setNextToolDate(nowDate);
        playerStore.setUnrefreshedEquip("");
        this.playerStoreDao.create(playerStore);
        final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)9);
        this.storeService.refreshItem(playerId, 1, playerStore, ci);
        this.storeService.refreshItem(playerId, 2, playerStore, ci);
    }
    
    private void initPlayerResource(final int playerId) {
        final PlayerResource pr = new PlayerResource();
        pr.setPlayerId(playerId);
        pr.setCopper(Integer.valueOf(Configuration.getProperty("gcld.player.copper")));
        pr.setFood(Integer.valueOf(Configuration.getProperty("gcld.player.food")));
        pr.setIron(Integer.valueOf(Configuration.getProperty("gcld.player.iron")));
        pr.setWood(Integer.valueOf(Configuration.getProperty("gcld.player.wood")));
        pr.setExp(0);
        pr.setUpdateTime(new Date());
        this.playerResourceDao.create(pr);
    }
    
    private void initPlayerConstants(final int playerId) {
        final PlayerConstants pc = new PlayerConstants();
        pc.setPlayerId(playerId);
        pc.setExtraNum(1);
        this.playerConstantsDao.create(pc);
    }
    
    private Player getNewPlayer(final String playerName, final String userId, final String yx, final String yxSource, final int forceId) {
        final Player player = new Player();
        player.setPlayerName(playerName);
        player.setPlayerLv(Integer.parseInt(Configuration.getProperty("gcld.player.level")));
        player.setSysGold(Integer.parseInt(Configuration.getProperty("gcld.player.sysgold")));
        player.setUserGold(0);
        player.setConsumeLv(Integer.parseInt(Configuration.getProperty("gcld.player.consumeLv")));
        player.setUserId(userId);
        player.setYx(yx);
        player.setYxSource(yxSource);
        player.setForceId(forceId);
        player.setPic(0);
        player.setPowerId(1);
        final Date now = new Date();
        player.setLoginTime(now);
        player.setQuitTime(now);
        player.setDailyOnlineTime(0);
        player.setState(0);
        player.setCreateTime(now);
        player.setPlayerServerId(0);
        player.setMaxLv(10);
        player.setTotalUserGold(0);
        player.setTotalTicketGold(0);
        player.setGm(0);
        player.setDefaultPay(0);
        return player;
    }
    
    private PlayerAttribute getPlayerAttribute(final int playerId, final int forceId) {
        final PlayerAttribute pa = new PlayerAttribute();
        pa.setPlayerId(playerId);
        final char[] function = new char[128];
        for (int i = 0; i < 128; ++i) {
            function[i] = '0';
        }
        function[0] = '1';
        pa.setFunctionId(new String(function));
        pa.setMaxStoreNum(30);
        pa.setPayPoint(0);
        pa.setIsAreaNew("100000");
        pa.setFullRecruitNum(0);
        pa.setEnterCount(0);
        pa.setBattleRewardTimes(0);
        pa.setBattleWinTimes(0);
        pa.setRecruitToken((int)(Object)((C)this.cCache.get((Object)"Base.MuBingLing.Daily")).getValue());
        pa.setGuideId(2);
        pa.setIronConvert(0);
        pa.setIronDisplay(0);
        pa.setTechOpen(0);
        pa.setTechResearch(0);
        pa.setFreeConstructionNum(0);
        pa.setHasBandit(3);
        pa.setKidnapper(3);
        pa.setBlackMarketCd(new Date());
        return pa;
    }
    
    @Override
    public byte[] getPlayerSize(final String userId, final String yx) {
        final List<Player> playerList = this.playerDao.getPlayerByUserId(userId, yx);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("size", playerList.size());
        doc.createElement("serverName", Configuration.getProperty(yx, "gcld.servername"));
        doc.createElement("serverId", Configuration.getProperty(yx, "gcld.serverid"));
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getPlayerList(final String userId, final String yx) {
        final Date nowDate = new Date();
        List<Player> playerList = null;
        int roleNum = 0;
        if (this.yxOperation.checkTencentPf(yx)) {
            playerList = this.playerDao.getPlayerOnlyByUserId(userId);
            roleNum = this.playerDao.getRoleCountByUid(userId);
        }
        else {
            playerList = this.playerDao.getPlayerByUserId(userId, yx);
            roleNum = this.playerDao.getRoleCount(userId, yx);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("maxRoleNum", WebUtil.getMaxPlayerNum(yx));
        doc.startArray("playerList");
        for (final Player player : playerList) {
            if (player.getState() == 1 && player.getDeleteTime() != null && !CDUtil.isInCD(259200000L, player.getDeleteTime(), nowDate)) {
                this.playerDao.updateState(player.getPlayerId(), 2);
            }
            else {
                if (player.getState() == 2) {
                    continue;
                }
                doc.startObject();
                doc.createElement("playerId", player.getPlayerId());
                doc.createElement("playerLv", player.getPlayerLv());
                doc.createElement("playerName", player.getPlayerName());
                doc.createElement("lastLoginTime", player.getLoginTime());
                doc.createElement("pic", player.getPic());
                doc.createElement("consumLv", player.getConsumeLv());
                doc.createElement("isDelete", player.getState() == 1);
                doc.createElement("forceId", player.getForceId());
                doc.createElement("defaultPay", player.getDefaultPay());
                doc.endObject();
            }
        }
        doc.endArray();
        doc.createElement("singleRole", WebUtil.isSingleRole(yx));
        doc.createElement("maxRole", roleNum >= WebUtil.getMaxPlayerNum(yx));
        doc.createElement("serverName", Configuration.getProperty(yx, "gcld.servername"));
        doc.createElement("serverId", Configuration.getProperty(yx, "gcld.serverid"));
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public Tuple<Player, byte[]> getPlayerInfo(final int playerId, final String userId, final String yx) {
        final Tuple<Player, byte[]> tuple = new Tuple();
        final List<Player> playerList = this.playerDao.getPlayerByUserId(userId, yx);
        if (playerList.size() == 0) {
            tuple.left = null;
            tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10004);
        }
        return this.getPlayerInfo(playerList.get(0).getPlayerId(), userId);
    }
    
    @Transactional
    @Override
    public Tuple<Player, byte[]> getPlayerInfo(final int playerId, final String userId) {
        final Player player = this.playerDao.read(playerId);
        final Tuple<Player, byte[]> tuple = new Tuple();
        final UserDto userDto = Users.getUserDto(player.getUserId(), player.getYx());
        if (userDto == null) {
            tuple.left = null;
            tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_AUTH_10001);
            return tuple;
        }
        final PlayerResource pr = this.playerResourceDao.read(playerId);
        final PlayerAttribute pa = this.playerAttributeDao.read(playerId);
        if (!player.getUserId().equals(userId)) {
            tuple.left = null;
            tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
            return tuple;
        }
        if (player.getState() == 1) {
            tuple.left = null;
            tuple.right = JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10006);
            return tuple;
        }
        this.resetPlayer(pa);
        final boolean isTrainning = false;
        final boolean isTrainningOver = false;
        final int tExp = 0;
        CityEventManager.getInstance().addTuFeiSlaveEventWhenLogIn(playerId);
        return this.getResult(player, pr, isTrainning, isTrainningOver, tExp);
    }
    
    private Tuple<Player, byte[]> getResult(final Player player, final PlayerResource pr, final boolean isTrainning, final boolean isTrainningOver, final int tExp) {
        final PlayerAttribute pa = this.playerAttributeDao.read(player.getPlayerId());
        final Tuple<Player, byte[]> tuple = new Tuple();
        tuple.left = player;
        final int playerId = player.getPlayerId();
        final String[] name = this.chatService.initBlackList(playerId);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("blackNames");
        String[] array;
        for (int length = (array = name).length, l = 0; l < length; ++l) {
            final String str = array[l];
            doc.createElement(str);
        }
        doc.endArray();
        final long now = System.currentTimeMillis();
        if (player != null && player.getCreateTime() != null && this.powerService.bonusLiaoHuaDefeatedOrTimeOut(playerId)) {
            final long createTime = player.getCreateTime().getTime();
            final long endTime = createTime + 259200000L - now;
            doc.createElement("endTime", (endTime < 0L) ? 0L : endTime);
        }
        else {
            doc.createElement("endTime", 0);
        }
        doc.startObject("player");
        doc.createElement("playerId", playerId);
        doc.createElement("playerLv", player.getPlayerLv());
        doc.createElement("playerName", player.getPlayerName());
        doc.createElement("userId", player.getUserId());
        doc.createElement("yx", player.getYx());
        doc.createElement("forceId", player.getForceId());
        doc.createElement("pic", player.getPic());
        doc.createElement("gold", player.getGold());
        doc.createElement("vipLv", player.getConsumeLv());
        final String pIdName = MD5SecurityUtil.code(String.valueOf(playerId) + player.getPlayerName());
        doc.createElement("pkey", pIdName);
        doc.createElement("pkey2", String.valueOf(now) + "_" + pIdName);
        doc.createElement("copper", pr.getCopper());
        doc.createElement("copperMax", this.buildingOutputCache.getBuildingOutput(playerId, 16));
        doc.createElement("copperOutput", this.buildingOutputCache.getBuildingsOutput(playerId, 1));
        doc.createElement("food", pr.getFood());
        doc.createElement("foodMax", this.buildingOutputCache.getBuildingOutput(playerId, 48));
        doc.createElement("foodOutput", this.buildingOutputCache.getBuildingsOutput(playerId, 3));
        doc.createElement("wood", pr.getWood());
        doc.createElement("woodMax", this.buildingOutputCache.getBuildingOutput(playerId, 32));
        doc.createElement("woodOutput", this.buildingOutputCache.getBuildingsOutput(playerId, 2));
        doc.createElement("iron", pr.getIron());
        doc.createElement("ironMax", this.buildingOutputCache.getBuildingOutput(playerId, 64));
        doc.createElement("ironOutput", this.buildingOutputCache.getBuildingsOutput(playerId, 4));
        doc.createElement("exp", pr.getExp());
        doc.createElement("expNeed", this.serialCache.get((int)(Object)((C)this.cCache.get((Object)"Chief.UpgradeExpS")).getValue(), player.getPlayerLv()));
        doc.createElement("forces", 0);
        doc.createElement("forcesMax", 0);
        final Map<Integer, Battle> gMapBat = NewBattleManager.getInstance().getBattleByPid(playerId);
        doc.createElement("inPveBattle", gMapBat.get(1) != null || gMapBat.get(2) != null);
        doc.createElement("inOccupyBattle", gMapBat.get(4) != null);
        doc.endObject();
        doc.appendJson("chargeItems", this.chargeItemService.getConfigByPlayer(playerId).getBytes());
        doc.appendJson("curTask", this.playerTaskService.getCurTaskSimpleInfo(playerId));
        final List<NationTaskAnd> taskAnds = this.rankService.getNationTaskAnds(player.getForceId());
        if (player.getPlayerLv() >= 30) {
            doc.appendJson("curNationTask", this.rankService.getCurNationTaskSimpleInfo(playerId, taskAnds));
            final int nationTask = this.rankService.hasNationTasks(player.getForceId());
            if (nationTask == 4) {
                final TaskKillInfo taskKillInfo = this.taskKillInfoDao.getTaskKillInfo(playerId);
                final long updateTime = (taskKillInfo == null || taskKillInfo.getUpdatetime() == null) ? now : taskKillInfo.getUpdatetime();
                if (updateTime <= now) {
                    doc.createElement("displayInvest", 1);
                }
                else {
                    this.dataPushCenterUtil.addInvestCd(playerId, updateTime);
                }
            }
        }
        doc.appendJson(this.dataGetter.getNationService().getTryTaskInfo(playerId, player.getForceId(), this.dataGetter.getForceInfoDao().read(player.getForceId())));
        doc.appendJson(this.dataGetter.getProtectService().getProtectTaskInfo(playerId));
        doc.createElement("addictionURL", Configuration.getProperty(player.getYx(), "gcld.addiction.url"));
        doc.createElement("redirectUrl", MessageFormatter.format(Configuration.getProperty(player.getYx(), "gcld.unprelogin.redirect.url"), new Object[] { player.getUserId() }));
        doc.createElement("battleRewardTimes", pa.getBattleRewardTimes());
        final PlayerQuenchingRelative pqRelative = this.playerQuenchingRelativeDao.read(playerId);
        doc.createElement("freeQuechingTimes", (pqRelative == null) ? 0 : pqRelative.getFreeQuenchingTimes());
        doc.createElement("quechingTip", (pqRelative == null) ? 0 : pqRelative.getRemind());
        final int powerId = player.getPowerId();
        doc.createElement("powerId", powerId);
        final PlayerOfficeRelative por = this.playerOfficeRelativeDao.read(playerId);
        doc.createElement("hasSalary", por != null && por.getSalaryGotToday() == 0);
        final Tuple<Boolean, Boolean> applyTuple = this.occupyService.hasApply(playerId);
        doc.createElement("hasOfficialBuildingOthers", applyTuple.left);
        doc.createElement("hasOfficerBuildingApply", applyTuple.right);
        final PlayerBattleAuto pba = this.playerBattleAutoDao.read(playerId);
        doc.createElement("autoBat", pba != null);
        final PlayerFeatRank pfr = this.playerFeatRankDao.read(playerId);
        if (pfr != null && pfr.getLastRank() > 0 && pfr.getReceived() == 0) {
            doc.createElement("batReward", true);
        }
        else {
            doc.createElement("batReward", false);
        }
        final char[] cs = pa.getFunctionId().toCharArray();
        final List<OfficerTokenUseInfo> ingTokens = this.battleService.getCurrentInUseToken(player.getForceId());
        this.battleService.getCurrentOfficerTokenPushInfo(ingTokens, doc, playerId);
        if (cs[10] == '1' && TeamManager.leagueOpen(player.getForceId())) {
            doc.createElement("batTeamNum", TeamManager.getInstance().getTeamNumByForceId(player.getForceId()));
            doc.createElement("openLegion", 1);
        }
        else {
            doc.createElement("openLegion", 0);
        }
        String function = pa.getFunctionId();
        if (player.getPlayerLv() >= 30) {
            cs[57] = '1';
            function = String.valueOf(cs);
        }
        final String startServer = Configuration.getProperty("gcld.server.time");
        doc.createElement("is2th", this.isSecond(startServer));
        doc.createElement("function", new String(function));
        if (cs[38] != '1') {
            doc.createElement("haveDayGift", false);
        }
        else if (pa.getLastGiftTime() == null) {
            doc.createElement("haveDayGift", true);
        }
        else {
            final Date lastGiftTime = pa.getLastGiftTime();
            final long lastGiftTimeToMillsecond = lastGiftTime.getTime();
            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(1);
            final int month = calendar.get(2);
            final int day = calendar.get(5);
            calendar.set(year, month, day, 0, 0, 0);
            final long todayToMillsecond = calendar.getTimeInMillis();
            doc.createElement("haveDayGift", lastGiftTimeToMillsecond < todayToMillsecond);
        }
        if (cs[39] == '1') {
            doc.createElement("onlineNum", this.playerOnlineRewardDao.getOnlineNum(playerId));
        }
        if (cs[33] == '1') {
            doc.createElement("dinnerNum", this.dataGetter.getPlayerDinnerDao().getDinnerNum(playerId));
        }
        doc.createElement("hasNewMail", this.mailService.haveNewMail(playerId));
        final PlayerMarket pm = this.playerMarketDao.read(pa.getPlayerId());
        doc.createElement("marketCanBuyNum", (pm == null) ? 0.0 : pm.getCanbuyNum());
        doc.createElement("hasPurpleEquip", this.storeService.hasPurpleEquip(playerId));
        doc.createElement("hasGift", this.giftServiceDao.hasGift(player) ? "1" : "0");
        if (cs[46] == '1') {
            final PlayerBatRank playerBatRank = this.playerBatRankDao.read(playerId);
            if (playerBatRank != null) {
                doc.createElement("rankBat", !StringUtils.isBlank(playerBatRank.getReward()));
                doc.createElement("RankBatNum", playerBatRank.getRankBatNum());
            }
        }
        doc.createElement("guideId", pa.getGuideId());
        doc.createElement("displayTech", (this.playerTechDao.getNumDisPlayButton(playerId) > 0) ? 1 : 0);
        final PlayerSearch ps = this.playerSearchDao.read(playerId);
        if (ps != null) {
            doc.createElement("searchNumLeft", ps.getSearchNumLeft());
        }
        doc.createElement("isTrainning", isTrainning);
        doc.createElement("isTrainningOver", isTrainningOver);
        doc.createElement("totalExp", tExp);
        if (cs[52] == '1') {
            final List<PlayerSlave> playerSlaveList = this.playerSlaveDao.getListByPlayerId(playerId);
            final Date now2 = new Date();
            for (final PlayerSlave ps2 : playerSlaveList) {
                if ((ps2.getType() == 0 && TimeUtil.specialAddMinutes(ps2.getGrabTime(), 30).before(now2)) || (1 == ps2.getType() && ps2.getSlashTimes() > 0 && TimeUtil.specialAddDays(ps2.getGrabTime(), 3).before(now2))) {
                    this.playerSlaveDao.deleteById(ps2.getVId());
                }
            }
            doc.createElement("slaveNum", this.playerSlaveDao.getSizeByPlayerId(playerId));
        }
        int rewardNum = 0;
        final PlayerWorld pw = this.playerWorldDao.read(playerId);
        if (pw != null) {
            rewardNum = pw.getRewardNum();
        }
        doc.createElement("countryRewardNum", rewardNum);
        if (cs[43] == '1') {
            final Date cd = pa.getBlackMarketCd();
            if (cd != null) {
                if (cd.after(new Date())) {
                    this.dataPushCenterUtil.addBlackMarketCd(playerId, cd.getTime());
                }
                else if (this.dataPushCenterUtil.canPushBlackPrompt(playerId)) {
                    doc.createElement("displayBlack", 1);
                }
            }
        }
        if (player.getPlayerLv() >= 30 && player.getPlayerLv() >= player.getMaxLv()) {
            PlayerService.topLvMap.put(playerId, 1);
            doc.createElement("topLv", 1);
        }
        this.payService.checkVipForLogin(player, doc);
        final int state = this.kfwdMatchService.getWdState(playerId);
        if (state != 0) {
            if (state <= 20) {
                doc.createElement("inkfwd", 1);
            }
            else {
                doc.createElement("inkfwd", 2);
            }
            final KfwdSeasonInfo seasonInfo = this.kfwdMatchManager.getSeasonInfo();
            if (seasonInfo != null && seasonInfo.getZb() == 1) {
                doc.createElement("zb", 1);
            }
        }
        final int kfgzState = this.kfgzSeasonService.getMatchState();
        if (kfgzState != 0) {
            doc.createElement("kfgzState", kfgzState);
        }
        if (this.activityService.isTodayInBatExpActivity() && this.playerBattleAttributeDao.read(playerId).getActivityBatExp() <= 0) {
            doc.createElement("activity51", true);
        }
        else {
            doc.createElement("activity51", false);
        }
        this.dataGetter.getBattleService().getGoldOrderInfoForLogin(playerId, player.getForceId(), doc);
        if (cs[24] == '1') {
            HuizhanHistory hh = this.dataGetter.getHuiZhanService().getTodayHuizhanBySate(1);
            if (hh != null) {
                doc.createElement("hasHuiZhanGatherIcon", true);
            }
            final int maxLv = this.forceInfoDao.getMaxLv();
            if (maxLv >= 5) {
                this.dataGetter.getHuiZhanService().getHuiZhanTaskInfoForLogin(playerId, doc);
                hh = this.dataGetter.getHuizhanHistoryDao().getLatestHuizhan();
                if (hh == null || hh.getState() == 3) {
                    final Calendar hzTime = this.dataGetter.getHuiZhanService().getNextHzTime();
                    if (System.currentTimeMillis() > hzTime.getTimeInMillis() - 86400000L) {
                        doc.createElement("hasHuiZhan", true);
                    }
                    if (System.currentTimeMillis() > hzTime.getTimeInMillis() - 3600000L) {
                        doc.createElement("hzIconCountDown", hzTime.getTimeInMillis() - System.currentTimeMillis());
                    }
                }
                else {
                    doc.createElement("hasHuiZhan", true);
                }
            }
        }
        final JuBenDto juBenDto = JuBenManager.getInstance().getByPid(playerId);
        if (juBenDto != null) {
            doc.createElement("inJuBen", true);
            doc.createElement("soId", juBenDto.juBen_id);
            if (juBenDto.state == 0) {
                doc.createElement("juBenOver", true);
            }
        }
        final List<PlayerArmyReward> parList = this.dataGetter.getPlayerArmyRewardDao().getListByPlayerId(playerId);
        for (final PlayerArmyReward par : parList) {
            if (par.getState() == 0) {
                final ArmiesReward armiesReward = (ArmiesReward)this.dataGetter.getArmiesRewardCache().get((Object)par.getArmyId());
                final General general = (General)this.dataGetter.getGeneralCache().get((Object)armiesReward.getChief());
                final Troop troop = (Troop)this.dataGetter.getTroopCache().get((Object)general.getTroop());
                if (BattleDrop.getDropType(troop.getDrop()) == 5) {
                    doc.createElement("hasExp", true);
                    break;
                }
                continue;
            }
        }
        final UserDto userDto = Users.getUserDto(player.getUserId(), player.getYx());
        if (userDto.isNeedAntiAddiction()) {
            doc.createElement("antiaddiction", true);
            if (this.userLoginInfoDao.getUserLoginInfo(player.getYx(), player.getUserId()) != null) {
                doc.createElement("online_time", this.userLoginInfoDao.getUserLoginInfo(player.getYx(), player.getUserId()).getOnlineTime());
            }
            else {
                doc.createElement("online_time", 0);
            }
            doc.createElement("antiUrl", Configuration.getProperty(player.getYx(), "gcld.addiction.url"));
        }
        else {
            doc.createElement("antiaddiction", false);
        }
        ManWangLingManager.getInstance().caculateOnePlayer(player.getPlayerId(), player.getForceId(), doc);
        if (player.getPlayerLv() >= ActivityService.lvExpJoinActivityLv) {
            boolean activityLvExp = false;
            final PlayerLvExp playerLvExp = this.playerLvExpDao.read(playerId);
            if (playerLvExp != null && playerLvExp.getReward() != 2) {
                doc.createElement("lvExpReward", true);
                activityLvExp = true;
            }
            if (ActivityService.isInLvExpActivity()) {
                activityLvExp = true;
            }
            doc.createElement("activityLvExp", activityLvExp);
        }
        doc.createElement("havePayActivity", this.havePayActivity(playerId) ? 1 : 0);
        try {
            if (por != null) {
                final int priId = (por.getLastOfficerId() == null) ? 0 : por.getLastOfficerId();
                final long remainTime = (por.getReputationTime() == null) ? now : por.getReputationTime().getTime();
                if (remainTime - now > 0L) {
                    final Halls halls = (Halls)this.hallsCache.get((Object)priId);
                    doc.createElement("reputationTime", remainTime - now);
                    doc.createElement("officerId", halls.getOfficialId());
                }
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error(e.getMessage());
            errorSceneLog.error(this, e);
        }
        int haveDragonActivity = 0;
        if (cs[10] == '1' && ((this.activityService.inDragonBoatFestival() && this.fstDbNumCache.getMaxNum() > this.playerScoreRankDao.getScore(playerId)) || this.playerDragonDao.getDragonNumByPlayerId(playerId) > 0)) {
            haveDragonActivity = 1;
        }
        doc.createElement("haveDragonActivity", haveDragonActivity);
        if (this.activityService.inIronActivity()) {
            final PlayerIron pi = this.playerIronDao.read(playerId);
            if (pi != null && pi.getReceived() >= ActivityService.ironMap.size()) {
                doc.createElement("haveIronActivity", 0);
            }
            else {
                doc.createElement("haveIronActivity", 1);
            }
        }
        else {
            doc.createElement("haveIronActivity", 0);
        }
        if (this.activityService.inTicketActivity()) {
            final int totalTicketGold = player.getTotalTicketGold();
            if (totalTicketGold >= PayService.ticketMap.get(PayService.ticketMap.size()).left) {
                doc.createElement("haveTicketActivity", 0);
            }
            else {
                doc.createElement("haveTicketActivity", 1);
            }
        }
        else {
            doc.createElement("haveTicketActivity", 0);
        }
        if (this.activityService.inDstqActivity()) {
            final int consumeGold = this.bonusActivityDao.getConsumeGold(playerId);
            if (consumeGold >= ActivityService.dstqMap.get(ActivityService.dstqMap.size()).left) {
                doc.createElement("haveDstqActivity", 0);
            }
            else {
                doc.createElement("haveDstqActivity", 1);
            }
        }
        else {
            doc.createElement("haveDstqActivity", 0);
        }
        if (cs[52] == '1' && EventUtil.isEventTime(9)) {
            final PlayerEvent pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 9);
            if (pe == null || SlaveUtil.get1Num(pe.getParam3()) < 4) {
                doc.createElement("haveSlaveActivity", 1);
            }
            else {
                doc.createElement("haveSlaveActivity", 0);
            }
        }
        else {
            doc.createElement("haveSlaveActivity", 0);
        }
        doc.createElement("slaveActivityBuff", SlaveEvent.getAdditionExp(playerId));
        if (cs[10] == '1' && EventUtil.isEventTime(10)) {
            doc.createElement("haveMidAutumnActivity", 1);
        }
        else {
            doc.createElement("haveMidAutumnActivity", 0);
        }
        if (cs[10] == '1' && EventUtil.isEventTime(11)) {
            doc.createElement("haveNationalDayActivity", 1);
        }
        else {
            doc.createElement("haveNationalDayActivity", 0);
        }
        if (cs[41] == '1' && EventUtil.isEventTime(12)) {
            doc.createElement("haveResourceAddittionActivity", 1);
        }
        else {
            doc.createElement("haveResourceAddittionActivity", 0);
        }
        if (EventUtil.isEventTime(13)) {
            final PlayerIncense pi2 = this.dataGetter.getPlayerIncenseDao().read(playerId);
            if (pi2 != null) {
                if (SlaveUtil.hasReward(pi2.getOpenBit(), 4) == 1) {
                    doc.createElement("haveIronRewardActivity", 1);
                }
                else {
                    doc.createElement("haveIronRewardActivity", 0);
                }
            }
            else {
                doc.createElement("haveIronRewardActivity", 0);
            }
        }
        else {
            doc.createElement("haveIronRewardActivity", 0);
        }
        final PlayerIncenseWeaponEffect piwe = this.dataGetter.getPlayerIncenseWeaponEffectDao().read(playerId);
        if (piwe == null) {
            doc.createElement("ironIncenseEffect", 0);
            doc.createElement("weaponEffectCd", 0);
        }
        else {
            final int incneseId = piwe.getIncenseId();
            final int incenseLimit = piwe.getIncenseLimit();
            if (1 == incneseId && incenseLimit > 0) {
                doc.createElement("ironIncenseEffect", 1);
            }
            else {
                doc.createElement("ironIncenseEffect", 0);
            }
            final int weaponId = piwe.getWeaponId();
            if (5 == weaponId && now < piwe.getWeaponEndTime().getTime()) {
                doc.createElement("weaponEffectCd", (Object)TimeUtil.now2specMs(piwe.getWeaponEndTime().getTime()));
            }
            else {
                doc.createElement("weaponEffectCd", 0);
            }
        }
        doc.createElement("haveQuenchingActivity", cs[51] == '1' && ActivityService.inQuenching);
        if (cs[51] == '1' && EventUtil.isEventTime(14)) {
            doc.createElement("haveXiLianActivity", 1);
        }
        else {
            doc.createElement("haveXiLianActivity", 0);
        }
        final int day2 = -TimeUtil.specialToNowDays(player.getCreateTime());
        System.out.println("loginReward#playerId:" + player.getPlayerId() + "#playerName:" + player.getPlayerName() + "#createTime:" + player.getCreateTime() + "#now:" + new Date() + "#day:" + day2);
        if (day2 <= 7) {
            final LoginReward lr = this.loginRewardDao.read(playerId);
            if (lr == null) {
                doc.createElement("haveLoginRewardActivity", 0);
                System.out.println("loginReward#playerId:" + player.getPlayerId() + "#playerName:" + player.getPlayerName() + "#createTime:" + player.getCreateTime() + "#now:" + new Date() + "#day:" + day2 + "#haveReward:0");
            }
            else if (day2 > 6) {
                if (1 == lr.getHaveReward()) {
                    this.loginRewardDao.received(playerId, 0);
                    final int totalDay = lr.getTotalDay();
                    final String rewardStr = PlayerService.rewardMap.get(totalDay);
                    final ITaskReward itr = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
                    final Map<Integer, Reward> map = itr.rewardPlayer(PlayerDtoUtil.getPlayerDto(player, this.dataGetter.getPlayerAttributeDao().read(playerId)), this.taskDataGetter, "\u8fde\u7eed\u767b\u9646\u5956\u52b1\u6d3b\u52a8", null);
                    final StringBuffer sb = new StringBuffer();
                    sb.append(MessageFormatter.format(LocalMessages.LOGIN_REWARD_MAIL_CONTENT_HEAD, new Object[] { totalDay }));
                    for (final Reward reward : map.values()) {
                        sb.append(reward.getName());
                        sb.append(reward.getNum());
                        sb.append("\uff0c");
                    }
                    sb.setLength(sb.length() - 1);
                    this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.LOGIN_REWARD_MAIL_HEAD, sb.toString(), 1, playerId, 0);
                }
                this.loginRewardDao.deleteById(playerId);
                doc.createElement("haveLoginRewardActivity", 0);
                System.out.println("loginReward#playerId:" + player.getPlayerId() + "#playerName:" + player.getPlayerName() + "#createTime:" + player.getCreateTime() + "#now:" + new Date() + "#day:" + day2 + "#haveReward:0");
            }
            else {
                final int diff = -TimeUtil.specialToNowDays(lr.getTodayFirstLoginTime());
                System.out.println("loginReward#playerId:" + player.getPlayerId() + "#playerName:" + player.getPlayerName() + "#createTime:" + player.getCreateTime() + "#now:" + new Date() + "#day:" + day2 + "#todayFirstLoginTime:" + lr.getTodayFirstLoginTime() + "#now:" + new Date() + "#diff:" + diff);
                if (diff <= 0) {
                    doc.createElement("haveLoginRewardActivity", (lr.getHaveReward() == 1) ? 1 : 2);
                    System.out.println("loginReward#playerId:" + player.getPlayerId() + "#playerName:" + player.getPlayerName() + "#createTime:" + player.getCreateTime() + "#now:" + new Date() + "#day:" + day2 + "#todayFirstLoginTime:" + lr.getTodayFirstLoginTime() + "#now:" + new Date() + "#diff:" + diff + "#haveReward:" + lr.getHaveReward());
                }
                else {
                    if (1 == lr.getHaveReward()) {
                        final int totalDay2 = lr.getTotalDay();
                        final String rewardStr2 = PlayerService.rewardMap.get(totalDay2);
                        final ITaskReward itr2 = TaskRewardFactory.getInstance().getTaskReward(rewardStr2);
                        final Map<Integer, Reward> map2 = itr2.rewardPlayer(PlayerDtoUtil.getPlayerDto(player, this.dataGetter.getPlayerAttributeDao().read(playerId)), this.taskDataGetter, "\u8fde\u7eed\u767b\u9646\u5956\u52b1\u6d3b\u52a8", null);
                        final StringBuffer sb2 = new StringBuffer();
                        sb2.append(MessageFormatter.format(LocalMessages.LOGIN_REWARD_MAIL_CONTENT_HEAD, new Object[] { totalDay2 }));
                        for (final Reward reward2 : map2.values()) {
                            sb2.append(reward2.getName());
                            sb2.append(reward2.getNum());
                            sb2.append("\uff0c");
                        }
                        sb2.setLength(sb2.length() - 1);
                        this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.LOGIN_REWARD_MAIL_HEAD, sb2.toString(), 1, playerId, 0);
                    }
                    if (1 == diff) {
                        this.loginRewardDao.updateRecord(playerId, lr.getTotalDay() + 1, 1, new Date());
                    }
                    else {
                        this.loginRewardDao.updateRecord(playerId, 1, 1, new Date());
                    }
                    doc.createElement("haveLoginRewardActivity", 1);
                    System.out.println("loginReward#playerId:" + player.getPlayerId() + "#playerName:" + player.getPlayerName() + "#createTime:" + player.getCreateTime() + "#now:" + new Date() + "#day:" + day2 + "#todayFirstLoginTime:" + lr.getTodayFirstLoginTime() + "#now:" + new Date() + "#diff:" + diff + "#haveReward:1");
                }
            }
        }
        else {
            doc.createElement("haveLoginRewardActivity", 0);
        }
        if (EventUtil.isEventTime(15)) {
            final PlayerEvent pe2 = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 15);
            if (pe2 == null) {
                doc.createElement("haveIronGiveActivity", 1);
            }
            else {
                final int times = IronGiveEvent.getNum(1, pe2) + IronGiveEvent.getNum(2, pe2) + IronGiveEvent.getNum(3, pe2) + IronGiveEvent.getNum(4, pe2);
                if (times <= 0) {
                    doc.createElement("haveIronGiveActivity", 0);
                }
                else {
                    doc.createElement("haveIronGiveActivity", 1);
                }
            }
        }
        else {
            doc.createElement("haveIronGiveActivity", 0);
        }
        if (cs[10] == '1' && EventUtil.isEventTime(16)) {
            doc.createElement("haveChristmasDayActivity", 1);
        }
        else {
            doc.createElement("haveChristmasDayActivity", 0);
        }
        if (cs[10] == '1' && EventUtil.isEventTime(17)) {
            doc.createElement("haveWishActivity", 1);
        }
        else {
            doc.createElement("haveWishActivity", 0);
        }
        if (cs[10] == '1' && EventUtil.isEventTime(18)) {
            doc.createElement("haveBeastActivity", 1);
        }
        else {
            doc.createElement("haveBeastActivity", 0);
        }
        if (cs[10] == '1' && EventUtil.isEventTime(19)) {
            doc.createElement("haveBaiNianActivity", 1);
        }
        else {
            doc.createElement("haveBaiNianActivity", 0);
        }
        final int BUFF = BaiNianEvent.getBuff(playerId, this.dataGetter);
        doc.createElement("haveBaiNianBuff", (BUFF > 0) ? 1 : 0);
        long baiNianBuffCd = 0L;
        if (BUFF > 0) {
            final PlayerEvent pe3 = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 19);
            if (pe3 != null) {
                baiNianBuffCd = TimeUtil.getCd(pe3.getCd1());
            }
        }
        doc.createElement("baiNianBuffCd", baiNianBuffCd);
        if (EventUtil.isEventTime(20)) {
            doc.createElement("haveRedPaperActivity", 1);
        }
        else {
            doc.createElement("haveRedPaperActivity", 0);
        }
        if (cs[10] == '1' && EventUtil.isEventTime(21)) {
            doc.createElement("haveLanternActivity", 1);
        }
        else {
            doc.createElement("haveLanternActivity", 0);
        }
        if (cs[10] == '1') {
            if (this.kfzbFeastService.inFeast()) {
                doc.createElement("haveFeast", 1);
            }
            else {
                doc.createElement("haveFeast", 0);
            }
        }
        else {
            doc.createElement("haveFeast", 0);
        }
        final Integer wizardIconType = this.dataGetter.getPhantomService().getPahntomWorkShopIconType(playerId);
        if (wizardIconType != null) {
            doc.createElement("wizardIconType", wizardIconType);
        }
        doc.createElement("changeBat", this.playerBattleAttributeDao.getChangebat(playerId));
        if (cs[67] == '1') {
            final int forceId = player.getForceId();
            final ConcurrentHashMap<Integer, PlayerCourtesyObj> countrymap = CourtesyManager.getInstance().PCOContainer.get(forceId);
            final PlayerCourtesyObj playerCourtesyObj = countrymap.get(playerId);
            if (playerCourtesyObj == null) {
                CourtesyManager.getInstance().addPlayerToContainer(playerId, false);
            }
            doc.createElement("liShangWangLai", true);
        }
        else {
            doc.createElement("liShangWangLai", false);
        }
        if (RankService.taskInfo != null && RankService.taskInfo.isCanAtt() && RankService.taskInfo.getState() == 1) {
            final List<Integer> cityIds = RankService.taskInfo.getUnOccupiedCity();
            final int jitanNum = RankService.taskInfo.getTempleNum();
            if (cityIds != null && jitanNum == cityIds.size()) {
                doc.createElement("jitanNum", jitanNum);
                doc.startArray("jitan");
                for (int i = 0; i < jitanNum; ++i) {
                    doc.startObject();
                    doc.createElement("cityId", cityIds.get(i));
                    doc.endObject();
                }
                doc.endArray();
            }
        }
        final int maxNationLv = this.forceInfoDao.getMaxLv();
        doc.createElement("maxNationLv", maxNationLv);
        if (por != null) {
            final Halls halls2 = (Halls)this.hallsCache.get((Object)por.getOfficerId());
            if (halls2 != null) {
                doc.createElement("pin", halls2.getOfficialId() - 1);
            }
        }
        if (player.getYx().equals("360")) {
            this.activityService.handle360PrivilegeForLogin(player, doc);
        }
        this.dataGetter.getKfzbMatchService().appendKfzbSeasonInfo(player, doc);
        this.dataGetter.getAutoBattleService().appendAutoBattleInfo(player, doc);
        if (this.yxOperation.checkTencentPf(player.getYx())) {
            PlayerVipTx pvt = this.playerVipTxDao.getByPlayerId(playerId);
            if (pvt == null && userDto.getIsYellowVip() == 1) {
                pvt = new PlayerVipTx();
                pvt.setDailyStatus(0);
                pvt.setExtraStatus(0);
                pvt.setPlayerId(playerId);
                pvt.setRookieStatus(0);
                pvt.setIsYellowVip(userDto.getIsYellowVip());
                pvt.setIsYellowHighVip(userDto.getIsYellowHighVip());
                pvt.setIsYellowYearVip(userDto.getIsYellowYearVip());
                pvt.setYellowVipLv(userDto.getYellowVipLevel());
                final List<GiftTx> list = this.giftTxCache.getGiftByType(4);
                final int size = list.size();
                final StringBuilder sb3 = new StringBuilder();
                for (int j = 0; j < size; ++j) {
                    sb3.append("0");
                }
                pvt.setUpgradeStatus(sb3.toString());
                this.playerVipTxDao.create(pvt);
            }
            if (userDto.getIsYellowVip() == 1) {
                doc.createElement("isYellowVip", true);
                doc.createElement("yellowVipLv", userDto.getYellowVipLevel());
            }
            if (userDto.getIsYellowYearVip() == 1) {
                doc.createElement("isYellowYearVip", true);
            }
        }
        doc.endObject();
        tuple.right = JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        if (por.getOfficerId() == 1) {
            for (int k = 1; k <= 3; ++k) {
                if (k == player.getForceId()) {
                    final String msg = MessageFormatter.format(LocalMessages.KING_LOGIN_IN_OUR, new Object[] { "", ColorUtil.getSpecialColorMsg(player.getPlayerName()) });
                    this.chatService.sendSystemChat("GLOBAL", playerId, player.getForceId(), msg, null);
                }
                else {
                    final String msg = MessageFormatter.format(LocalMessages.KING_LOIN_IN_OTHER, new Object[] { "", ColorUtil.getSpecialColorMsg(WorldCityCommon.nationKingNameMap.get(player.getForceId())), ColorUtil.getSpecialColorMsg(player.getPlayerName()) });
                    this.chatService.sendSystemChat("GLOBAL", playerId, k, msg, null);
                }
            }
        }
        return tuple;
    }
    
    @Override
    public long isSecond(final String startServer) {
        final long start = Long.parseLong(startServer);
        final long now = System.currentTimeMillis();
        final Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(new Date(start));
        calendar1.add(6, 1);
        calendar1.set(11, 0);
        calendar1.set(12, 0);
        calendar1.set(13, 0);
        final long sec = calendar1.getTime().getTime();
        return sec - now;
    }
    
    @Transactional
    @Override
    public AddExpInfo updateExpAndPlayerLevel(final int playerId, int addExp, final Object attribute) {
        final AddExpInfo addExpInfo = new AddExpInfo();
        Label_1806: {
            try {
                PlayerService.locks[playerId % PlayerService.LOCKS_LEN].lock();
                if (addExp == 0) {
                    return addExpInfo;
                }
                final PlayerResource pr = this.playerResourceDao.read(playerId);
                final Player player = this.playerDao.read(playerId);
                final String sysTopLv = Configuration.getProperty("gcld.sys.player.lv");
                if (StringUtils.isNotBlank(sysTopLv) && player.getPlayerLv() >= Integer.valueOf(sysTopLv)) {
                    addExpInfo.state = AddExpInfo.STATE_TOP_LEVEL;
                    return addExpInfo;
                }
                final PlayerDto dto = Players.getPlayer(playerId);
                if (player.getPlayerLv() >= 30 && player.getPlayerLv() >= player.getMaxLv()) {
                    addExpInfo.state = AddExpInfo.STATE_NO_ADD;
                    if (dto != null && PlayerService.topLvMap.putIfAbsent(playerId, 1) == null) {
                        Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("topLv", 1));
                    }
                    return addExpInfo;
                }
                if (dto != null && PlayerService.topLvMap.remove(playerId) != null) {
                    Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("topLv", 0));
                }
                final int cAxis = (int)(Object)((C)this.cCache.get((Object)"Chief.UpgradeExpS")).getValue();
                final int upNum = this.serialCache.get(cAxis, player.getPlayerLv());
                final UserDto usrDto = Users.getUserDto(player.getUserId(), player.getYx());
                if (usrDto != null) {
                    addExp = (int)usrDto.getAntiAddictionStateMachine().getCurrentState().getIntDataAfterAntiAddiction(addExp);
                    if (addExp == 0) {
                        return addExpInfo;
                    }
                }
                final JsonDocument doc = new JsonDocument();
                doc.startObject();
                long curExp = pr.getExp();
                addExpInfo.addExp = addExp;
                if (curExp + addExp >= upNum) {
                    if (player.getPlayerLv() == player.getMaxLv() + 9) {
                        addExpInfo.addExp = (int)(upNum - curExp);
                        curExp = 0L;
                        addExpInfo.state = AddExpInfo.STATE_ADD_PART;
                    }
                    else {
                        curExp = curExp + addExp - upNum;
                    }
                    final int lv = player.getPlayerLv();
                    this.rankService.updatePlayerLv(playerId, lv + 1);
                    this.prepareBluePrint(playerId, lv + 1);
                    if (30 == lv) {
                        final Session session = Players.getSession(Integer.valueOf(playerId));
                        if (session != null) {
                            GroupManager.getInstance().getGroup(String.valueOf(ChatType.COUNTRY.toString()) + player.getForceId() + "_" + 1).leave(session.getId());
                            GroupManager.getInstance().getGroup(String.valueOf(ChatType.COUNTRY.toString()) + player.getForceId() + "_" + 2).join(session);
                        }
                    }
                    else if (50 == lv) {
                        final Session session = Players.getSession(Integer.valueOf(playerId));
                        if (session != null) {
                            GroupManager.getInstance().getGroup(String.valueOf(ChatType.COUNTRY.toString()) + player.getForceId() + "_" + 2).leave(session.getId());
                        }
                    }
                    this.rankService.fireRankEvent(1, new RankData(playerId, lv + 1, 0));
                    if (lv == 29) {
                        final List<NationTaskAnd> taskAnds = this.rankService.getNationTaskAnds(player.getForceId());
                        if (dto != null) {
                            Players.push(dto.playerId, PushCommand.PUSH_NATION_TASK_SIMPLE, this.rankService.getCurNationTaskSimpleInfo(dto.playerId, taskAnds));
                        }
                    }
                    this.playerResourceDao.setExp(playerId, curExp, attribute, addExp);
                    final UpdateReward updateReward = (UpdateReward)this.updateRewardCache.get((Object)(player.getPlayerLv() + 1));
                    final Session session2 = Players.getSession(Integer.valueOf(playerId));
                    PlayerDto playerDto = null;
                    if (session2 != null) {
                        playerDto = Players.getSession(session2.getId());
                    }
                    else {
                        playerDto = new PlayerDto();
                        playerDto.playerId = playerId;
                        playerDto.playerLv = player.getPlayerLv();
                        playerDto.forceId = player.getForceId();
                    }
                    final Map<Integer, Reward> map = updateReward.getTaskReward().rewardPlayer(playerDto, this.taskDataGetter, "\u73a9\u5bb6\u5347\u7ea7\u5956\u52b1", true);
                    final int nextOpenSuitLevel = this.storeService.checkSuitOpen(lv + 1, doc, playerId);
                    if (updateReward.getType() == 2 || nextOpenSuitLevel != 0) {
                        final int special = this.updateRewardCache.getNextSpecialLv(lv + 1);
                        doc.createElement("rewardType", 2);
                        doc.createElement("nextSpecialRewardLv", special);
                    }
                    else {
                        doc.createElement("rewardType", updateReward.getType());
                    }
                    doc.startArray("updateReward");
                    for (final Map.Entry<Integer, Reward> entry : map.entrySet()) {
                        final Reward rd = entry.getValue();
                        doc.startObject();
                        doc.createElement("type", rd.getType());
                        doc.createElement("name", rd.getName());
                        doc.createElement("num", rd.getNum());
                        doc.endObject();
                    }
                    doc.endArray();
                    doc.createElement("playerLv", lv + 1);
                    doc.createElement("expNeed", this.serialCache.get((int)(Object)((C)this.cCache.get((Object)"Chief.UpgradeExpS")).getValue(), lv + 1));
                    doc.createElement("exp", curExp);
                    final List<Troop> openLvList = this.troopCache.getOpenLvTroop(lv + 1);
                    if (openLvList != null) {
                        doc.createElement("troopUp", true);
                    }
                    TaskMessageHelper.sendChiefLvTaskMessage(playerId, lv + 1);
                    addExpInfo.upLv = true;
                    final PlayerMarket pm = this.playerMarketDao.read(playerId);
                    if (pm != null) {
                        this.marketService.refreshShowInfo(pm, lv + 1);
                    }
                    final PlayerDto playerDto2 = playerDto;
                    ++playerDto2.playerLv;
                    if (lv + 1 == 50 && player.getConsumeLv() >= 2 && player.getConsumeLv() <= 4) {
                        doc.createElement("promptVIP5", 252000);
                    }
                    if (playerDto.playerLv == 55 && this.activityService.isTodayInBatExpActivity()) {
                        doc.createElement("activity51", true);
                    }
                    if (playerDto.playerLv == ActivityService.lvExpJoinActivityLv && ActivityService.isInLvExpActivity()) {
                        doc.createElement("activityLvExp", true);
                        this.activityService.reachJoinLvExpActivity(playerId);
                    }
                    try {
                        if ("5211game".equals(playerDto.yx)) {
                            final String callback = Configuration.getProperty(playerDto.yx, "yx.5211game.callback");
                            if (StringUtils.isNotBlank(callback) && "1".equals(callback.trim())) {
                                String lvIds = Configuration.getProperty(playerDto.yx, "yx.5211game.lv.ids");
                                if (StringUtils.isNotBlank(lvIds)) {
                                    lvIds = lvIds.trim();
                                    final String[] lvs = lvIds.split(",");
                                    String[] array;
                                    for (int length = (array = lvs).length, j = 0; j < length; ++j) {
                                        final String temp = array[j];
                                        final String[] ids = temp.split(":");
                                        if (ids.length >= 2 && Integer.parseInt(ids[0]) == playerDto.playerLv) {
                                            for (int i = 1; i < ids.length; ++i) {
                                                ThreadUtil.executor.execute(new Send5211gameRunner(playerDto.userId, playerId, playerDto.playerName, playerDto.playerLv, Integer.parseInt(ids[i]), playerDto.yx));
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    catch (Exception e) {
                        PlayerService.error.error("class:PlayerService#method:updateExpAndPlayerLevel#5211game_exception:", e);
                    }
                }
                else {
                    doc.createElement("exp", pr.getExp() + addExp);
                    doc.createElement("expNeed", this.serialCache.get((int)(Object)((C)this.cCache.get((Object)"Chief.UpgradeExpS")).getValue(), player.getPlayerLv()));
                    this.playerResourceDao.addExp(playerId, addExp, attribute);
                }
                doc.endObject();
                Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
            }
            catch (Exception e2) {
                PlayerService.log.debug("updateExpAndPlayerLevel building exception", e2);
                break Label_1806;
            }
            finally {
                PlayerService.locks[playerId % PlayerService.LOCKS_LEN].unlock();
            }
            PlayerService.locks[playerId % PlayerService.LOCKS_LEN].unlock();
        }
        final Player player2 = this.playerDao.read(playerId);
        if (player2 != null && "360".equals(player2.getYx()) && addExpInfo.upLv) {
            ThreadUtil.executor.execute(new SendYx360Runner(null, player2.getUserId(), playerId, Configuration.getProperty("360", "gcld.serverids"), "gcld", player2.getPlayerLv(), player2.getPlayerName()));
        }
        return addExpInfo;
    }
    
    @Transactional
    @Override
    public byte[] getForceInfo(final UserDto userDto) {
        if (this.playerDao.getRoleCount(userDto.userId, userDto.yx) >= WebUtil.getMaxPlayerNum(userDto.yx)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10007);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("forceList");
        doc.startObject();
        doc.createElement("forceId", 1);
        doc.endObject();
        doc.startObject();
        doc.createElement("forceId", 2);
        doc.endObject();
        doc.startObject();
        doc.createElement("forceId", 3);
        doc.endObject();
        doc.endArray();
        doc.startObject("reward");
        final int rewardForceId = this.getBalancedForceId();
        doc.createElement("forceId", rewardForceId);
        doc.createElement("kind", (Object)LocalMessages.T_COMM_10009);
        doc.createElement("value", 50);
        doc.endObject();
        final String[] list = this.playerNamesCache.getPlayerNameList();
        doc.startArray("info");
        String[] array;
        for (int length = (array = list).length, i = 0; i < length; ++i) {
            final String tuple = array[i];
            if (!StringUtils.isBlank(tuple)) {
                doc.startObject();
                doc.createElement("playerName", tuple);
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        PlayerService.forceRewardMap.put(String.valueOf(userDto.userId) + userDto.yx, rewardForceId);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] setPlayerForce(final int playerId, final int forceId) {
        if (forceId > 3 && forceId < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Player player = this.playerDao.read(playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (forceId == this.getBalancedForceId()) {
            this.playerDao.addSysGold(player, 50, "\u9009\u62e9\u6307\u5b9a\u52bf\u529b\u5956\u52b1\u91d1\u5e01");
            doc.startObject("reward");
            doc.createElement("kind", (Object)LocalMessages.T_COMM_10009);
            doc.createElement("value", 50);
            doc.endObject();
        }
        doc.endObject();
        this.playerDao.updatePlayerForceId(playerId, forceId);
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public boolean checkName(final String playerName) {
        if (StringUtils.isBlank(playerName)) {
            return false;
        }
        final int result = WebUtil.validate(playerName, Configuration.getIntProperty("gcld.playername.len"), Configuration.getPatternProperty("gcld.character.pattern"), true);
        if (result != 0) {
            return false;
        }
        final ICharacterFilter characterFilter = CharacterFilterFactory.getInstance().getFilter("default");
        return (characterFilter == null || characterFilter.isValid(playerName)) && !WebUtil.containsPunctOrWhitespace(playerName);
    }
    
    @Transactional
    @Override
    public byte[] setPlayerNameAndPic(final PlayerDto playerDto, final String playerName, final int pic, final Request request) {
        final PlayerAttribute pa = this.playerAttributeDao.read(playerDto.playerId);
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[3] == '0') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (StringUtils.isBlank(playerName)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10001);
        }
        final int result = WebUtil.validate(playerName, Configuration.getIntProperty("gcld.playername.len"), Configuration.getPatternProperty("gcld.character.pattern"), true);
        if (result != 0) {
            return JsonBuilder.getJson(State.FAIL, WebUtil.getValidateMsg(result, Configuration.getIntProperty("gcld.playername.len")));
        }
        ICharacterFilter characterFilter = CharacterFilterFactory.getInstance().getFilter("default");
        if (characterFilter != null && !characterFilter.isValid(playerName)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10008);
        }
        characterFilter = CharacterFilterFactory.getInstance().getFilter("addition");
        if (characterFilter != null && !characterFilter.isValid(playerName)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10008);
        }
        if (WebUtil.containsPunctOrWhitespace(playerName)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10003);
        }
        if (this.addPlayerName(playerName)) {
            this.playerDao.updatePlayerNameAndPic(playerDto.playerId, playerName, pic);
            final PlayerName pn = new PlayerName();
            pn.setPlayerName(playerName);
            this.playerNameDao.create(pn);
            this.playerNamesCache.put(playerName);
            playerDto.playerName = playerName;
            cs[3] = '0';
            this.playerAttributeDao.updateFunction(playerDto.playerId, new String(cs));
            TaskMessageHelper.sendChangeNameMessage(playerDto.playerId);
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("playerName", playerName);
            doc.endObject();
            if ("360".equals(playerDto.yx)) {
                ThreadUtil.executor.execute(new SendYx360Runner(request, playerDto.userId, playerDto.playerId, Configuration.getProperty("360", "gcld.serverids"), "gcld", playerDto.playerLv, playerName));
            }
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10002);
    }
    
    @Override
    public int getForcePlayerNum(final int forceId) {
        return PlayerService.forceMap.get(forceId);
    }
    
    private int getBalancedForceId() {
        final int a = PlayerService.forceMap.get(1);
        final int b = PlayerService.forceMap.get(2);
        final int c = PlayerService.forceMap.get(3);
        if (a <= b && a <= c) {
            return 1;
        }
        if (b <= a && b <= c) {
            return 2;
        }
        return 3;
    }
    
    @Transactional
    @Override
    public byte[] deletePlayer(final int playerId, final String userId, final String yx) {
        final Date nowDate = new Date();
        final List<Player> playerList = this.playerDao.getPlayerByUserId(userId, yx);
        Player thePlayer = null;
        for (final Player player : playerList) {
            if (player.getPlayerId() == playerId) {
                thePlayer = player;
                break;
            }
        }
        if (thePlayer == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10005);
        }
        this.playerDao.deletePlayer(playerId, nowDate);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public byte[] retrievePlayer(final int playerId, final String userId, final String yx) {
        final List<Player> playerList = this.playerDao.getPlayerByUserId(userId, yx);
        Player thePlayer = null;
        for (final Player player : playerList) {
            if (player.getPlayerId() == playerId) {
                thePlayer = player;
                break;
            }
        }
        if (thePlayer == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10005);
        }
        this.playerDao.retrievePlayer(playerId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    private void resetPlayer(final PlayerAttribute pa) {
        final int playerId = pa.getPlayerId();
        final Date nowDate = new Date();
        if (pa.getLastResetTime() == null) {
            this.playerAttributeDao.updateLastResetTime(playerId, nowDate);
            return;
        }
        this.politicsService.assignPoliticsEvent(pa);
        this.marketService.supplyCanBuyNum(playerId);
        final Player player = this.playerDao.read(playerId);
        Date lastResetTime = pa.getLastResetTime();
        final boolean canSet = this.canReset(lastResetTime);
        if (canSet) {
            int token = (int)(Object)((C)this.cCache.get((Object)"Base.MuBingLing.Daily")).getValue();
            if (player.getConsumeLv() >= 4) {
                final Chargeitem ci = (Chargeitem)this.chargeitemCache.get((Object)57);
                final int additionalToken = (int)(Object)ci.getParam();
                token += additionalToken;
            }
            if (pa.getRecruitToken() + token > 100) {
                final String msg = MessageFormatter.format(LocalMessages.RECRUIT_TOKEN_FULL_TIP, new Object[] { 100, token });
                this.mailService.writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.RECRUIT_TOKEN_FULL_TITLE, msg, 1, playerId, 0);
            }
            this.playerAttributeDao.resetData(playerId, nowDate, token);
            this.playerOfficeRelativeDao.updateSalaryGot(playerId, 0);
            this.playerTaskDao.resetDailyTask(playerId);
            this.playerSearchDao.resetBuyNum(playerId);
            this.playerGeneralMilitaryDao.resetTacticEffect(playerId);
            this.slaveService.resetSlaveSystem(playerId);
        }
        try {
            lastResetTime = ((player.getLoginTime() == null) ? new Date() : player.getLoginTime());
            boolean isFirstLoginAfterReboot = false;
            final ServerTime serverTime = GcldInitManager.latestServerTime;
            if (serverTime != null && serverTime.getStartTime() != null) {
                isFirstLoginAfterReboot = (canSet || serverTime.getStartTime().after(lastResetTime));
            }
            if (isFirstLoginAfterReboot) {
                this.addFirstLianbinglingEvent(player);
            }
        }
        catch (Exception e) {
            PlayerService.error.error(this, e);
        }
    }
    
    private void addFirstLianbinglingEvent(final Player player) {
        try {
            if (player == null) {
                return;
            }
            final int playerId = player.getPlayerId();
            if (player.getPlayerLv() >= 30 && CityEventManager.getInstance().playerEventMap != null) {
                final ConcurrentHashMap<Integer, PlayerEventObj> map = CityEventManager.getInstance().playerEventMap.get(playerId);
                if (map == null || (map != null && map.get(6) == null)) {
                    final String params = player.getPlayerId() + "#" + player.getForceId() + "#" + 6;
                    AddJob.getInstance().addJob(JobClassMethondEnum.CITYSERVICE_ADDPLAYEREVENT, params, 10L);
                }
            }
        }
        catch (Exception e) {
            PlayerService.error.error(this, e);
        }
    }
    
    private boolean canReset(final Date lastResetTime) {
        if (lastResetTime == null) {
            return false;
        }
        final Calendar cg = Calendar.getInstance();
        cg.set(11, 0);
        cg.set(12, 0);
        cg.set(13, 0);
        cg.set(14, 0);
        final Date zeroHour = cg.getTime();
        return lastResetTime.before(zeroHour);
    }
    
    @Override
    public void firePlayerTrainningStart(final Player player, final int mode, final long time) {
        if (player == null || mode == 0) {
            return;
        }
        PlayerService.lock.lock();
        try {
            final List<PlayerTrainningInfoDto> list = new ArrayList<PlayerTrainningInfoDto>();
            final PlayerTrainningInfoDto dto = new PlayerTrainningInfoDto(player, mode, new Date(time), player.getPic());
            for (final PlayerTrainningInfoDto dtos : PlayerService.trainningInfo) {
                if (dtos == null) {
                    continue;
                }
                if (dtos.getPlayerId() != player.getPlayerId()) {
                    continue;
                }
                list.add(dtos);
            }
            for (final PlayerTrainningInfoDto single : list) {
                PlayerService.trainningInfo.remove(single);
            }
            PlayerService.trainningInfo.add(dto);
            Collections.sort(PlayerService.trainningInfo);
        }
        catch (Exception e) {
            PlayerService.log.error("firePlayerTrainningStart", e);
            return;
        }
        finally {
            PlayerService.lock.unlock();
        }
        PlayerService.lock.unlock();
    }
    
    @Override
    public void firePlayerTrainningOver(final String playerName) {
        PlayerService.lock.lock();
        try {
            if (PlayerService.trainningInfo == null || PlayerService.trainningInfo.isEmpty() || PlayerService.trainningInfo.size() == 0) {
                return;
            }
            final List<PlayerTrainningInfoDto> list = new ArrayList<PlayerTrainningInfoDto>();
            for (int i = 0; i < PlayerService.trainningInfo.size(); ++i) {
                final PlayerTrainningInfoDto dto = PlayerService.trainningInfo.get(i);
                if (dto.getPlayerName().equals(playerName)) {
                    list.add(dto);
                }
                else if (dto.getBeginTimeDate().getTime() + 28800000L <= System.currentTimeMillis()) {
                    list.add(dto);
                }
            }
            for (final PlayerTrainningInfoDto single : list) {
                PlayerService.trainningInfo.remove(single);
            }
        }
        catch (Exception e) {
            PlayerService.log.error("firePlayerTrainningOver", e);
            return;
        }
        finally {
            PlayerService.lock.unlock();
        }
        PlayerService.lock.unlock();
    }
    
    @Override
    public Tuple<Boolean, String> rewardUser(final int userId, final int playerId, final String yx) {
        final Tuple<Boolean, String> result = new Tuple();
        result.left = false;
        final List<UserReward> userRewardList = this.userRewardDao.getUserReward(userId, yx);
        final int count = userRewardList.size();
        if (count <= 0) {
            result.right = MessageFormatter.format(LocalMessages.REWARD_USER_FAILURE_1, new Object[] { userId });
            return result;
        }
        for (int i = 0; i < count; ++i) {
            final UserReward userReward = userRewardList.get(i);
            final int giftId = userReward.getRewardType();
            final GiftInfo giftInfo = this.giftInfoDao.read(giftId);
            if (giftInfo == null) {
                result.right = MessageFormatter.format(LocalMessages.REWARD_USER_FAILURE_3, new Object[] { giftId });
                return result;
            }
            final PlayerGift playerGift = new PlayerGift();
            playerGift.setGiftId(giftId);
            playerGift.setReceived(0);
            playerGift.setPlayerId(playerId);
            playerGift.setAllServer(0);
            playerGift.setReceivedTime(null);
            playerGift.setId(0);
            if (this.playerGiftDao.create(playerGift) <= 0) {
                result.right = LocalMessages.REWARD_USER_FAILURE_4;
                return result;
            }
        }
        result.left = true;
        result.right = MessageFormatter.format(LocalMessages.REWARD_USER_SUCCESS, new Object[] { userId, playerId, count });
        return result;
    }
    
    private void prepareBluePrint(final int playerId, final int playerLv) {
        final List<BuildingDrawing> openList = this.buildingDrawingCache.getOpenList();
        final List<Integer> bluePrintIndexList = this.bluePrintDao.getBluePrintIndexList(playerId);
        for (final BuildingDrawing bd : openList) {
            if (playerLv >= bd.getOpenLv()) {
                if (bluePrintIndexList.contains(bd.getId())) {
                    continue;
                }
                final BluePrint bp = new BluePrint();
                bp.setPlayerId(playerId);
                bp.setIndex(bd.getId());
                bp.setState(1);
                bp.setJobId(0);
                this.bluePrintDao.create(bp);
            }
        }
    }
    
    @Override
    public void removeTopLv(final int playerId) {
        PlayerService.topLvMap.remove(playerId);
    }
    
    @Override
    public int getYxRenrenForceId(final Request request, final String yx) {
        try {
            String cookieValue = request.getCookieValue("t");
            cookieValue = ((cookieValue == null) ? "" : cookieValue);
            final Map<String, Object> paramMap = new HashMap<String, Object>();
            paramMap.put("s", cookieValue);
            paramMap.put("trace", PluginContext.configuration.getServerId(yx));
            paramMap.put("code", PluginContext.configuration.getRenrenCode(yx));
            final StringBuilder sb = new StringBuilder();
            sb.append(PluginContext.configuration.getRenrenCode(yx));
            sb.append(PluginContext.configuration.getServerId(yx));
            sb.append(cookieValue);
            sb.append(PluginContext.configuration.getRenrenSecret(yx));
            final String md5Value = MD5SecurityUtil.code(sb.toString()).toLowerCase();
            paramMap.put("key", md5Value);
            paramMap.put("flag", "1");
            try {
                final String userId = WebUtils.sendGetRequest(PluginContext.configuration.getRenrenLoginUrl(yx), paramMap);
                if (StringUtils.isBlank(userId)) {
                    PlayerService.log.info("getYxRenrenForceId, userId:" + userId);
                    return -1;
                }
                final int forceId = this.getBalancedForceId();
                PlayerService.forceRewardMap.put(String.valueOf(userId) + yx, forceId);
                return forceId;
            }
            catch (Exception e) {
                PlayerService.log.error("send renren login query fail", e);
                return -1;
            }
        }
        catch (Exception e2) {
            PlayerService.log.error("unknow excepted exception:", e2);
            return -1;
        }
    }
    
    private boolean havePayActivity(final int playerId) {
        final Activity activity = this.activityDao.read(2);
        final Date now = new Date();
        if (activity == null || activity.getStartTime().after(now) || activity.getEndTime().before(now)) {
            return false;
        }
        final String content = activity.getParamsInfo();
        final String[] payArry = content.split(";");
        final String[] temp = payArry[payArry.length - 1].split(",");
        final int nGold = Integer.parseInt(temp[0]);
        return this.playerDao.getTotalUserGold(playerId) < nGold;
    }
    
    @Override
    public void resetDailyOnlineTime() {
        final long start = System.currentTimeMillis();
        this.playerDao.resetDailyOnlineTime();
        PlayerService.timerLog.info(LogUtil.formatThreadLog("PlayerService", "resetDailyOnlineTime", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Override
    public void afterOpenFunction(final int functionId, final int playerId) {
        try {
            if (functionId == 51) {
                synchronized (this) {
                    PlayerQuenchingRelative pqr = this.playerQuenchingRelativeDao.read(playerId);
                    if (pqr == null) {
                        pqr = new PlayerQuenchingRelative();
                        pqr.setPlayerId(playerId);
                        pqr.setFreeNiubiQuenchingTimes(0);
                        pqr.setFreeQuenchingTimes(0);
                        pqr.setRemind(0);
                        this.playerQuenchingRelativeDao.create(pqr);
                    }
                }
                if (EventUtil.isEventTime(14)) {
                    Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("haveXiLianActivity", 1));
                }
                this.dataGetter.getPlayerQuenchingRelativeDao().updateFreeQuenchingTimes(playerId, 5);
            }
        }
        catch (Exception e) {
            final ErrorSceneLog errorSceneLog = ErrorSceneLog.getInstance();
            errorSceneLog.error("afterOpenFunction  playerId:" + playerId + " functionId:" + functionId, e);
        }
    }
    
    @Override
    public byte[] setDefaultPay(final UserDto userDto, final int playerId) {
        if (playerId <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Player player = this.playerDao.read(playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10005);
        }
        if ((!player.getUserId().equals(userDto.userId) || !player.getYx().equals(userDto.yx)) && !this.yxOperation.checkTencentPf(userDto.yx)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10012);
        }
        this.playerDao.clearDefaultPay(userDto.userId, userDto.yx);
        this.playerDao.setDefaultPay(playerId);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public byte[] payByReceipt(final PlayerDto playerDto, final String receipt, final String testing, final Request request) {
        final long start = System.currentTimeMillis();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (StringUtils.isBlank(receipt)) {
            doc.createElement("result", 0);
            doc.createElement("msg", (Object)LocalMessages.T_COMM_10011);
            doc.createElement("echo", "");
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        final boolean test = "true".equals(testing);
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("receipt-data", receipt);
        doc2.endObject();
        String echo = null;
        try {
            final String requestUrl = test ? Configuration.getProperty("gcld.mobile.app.pay.testing.url") : Configuration.getProperty("gcld.mobile.app.pay.url");
            echo = WebUtils.sendRequestByPostWithJson(requestUrl, doc2.toString());
            if (StringUtils.isNotBlank(echo)) {
                echo = echo.trim();
            }
            PlayerService.log.error("request0#yx:" + playerDto.yx + "#userId:" + playerDto.userId + "#playerId:" + playerDto.playerId + "#playerName:" + playerDto.playerName + "#echo:" + echo);
            JSONObject json = (JSONObject)JSON.parse(echo);
            String status = json.getString("status");
            json = (JSONObject)JSON.parse(json.getString("receipt"));
            String unique_identifier = json.getString("unique_identifier");
            String quantity = json.getString("quantity");
            String product_id = json.getString("product_id");
            String transaction_id = json.getString("transaction_id");
            String original_transaction_id = json.getString("original_transaction_id");
            String purchase_date = json.getString("purchase_date");
            String bid = json.getString("bid");
            String bvrs = json.getString("bvrs");
            PlayerService.log.error("request1#yx:" + playerDto.yx + "#userId:" + playerDto.userId + "#playerId:" + playerDto.playerId + "#playerName:" + playerDto.playerName + "#echo:" + echo + "#json:" + json + "#unique_identifier:" + unique_identifier + "#quantity:" + quantity + "#status:" + status + "#product_id:" + product_id + "#transaction_id:" + transaction_id + "#original_transaction_id:" + original_transaction_id + "#purchase_date:" + purchase_date + "#bid:" + bid + "#bvrs:" + bvrs);
            if (!"0".equals(status)) {
                if (test || !"21007".equals(status)) {
                    doc.createElement("result", 2);
                    doc.createElement("msg", (Object)LocalMessages.MOBILE_PAY_CHECK_ERROR);
                    doc.createElement("echo", echo);
                    doc.endObject();
                    return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
                }
                echo = WebUtils.sendRequestByPostWithJson(Configuration.getProperty("gcld.mobile.app.pay.testing.url"), doc2.toString());
                if (StringUtils.isNotBlank(echo)) {
                    echo = echo.trim();
                }
                json = (JSONObject)JSON.parse(echo);
                status = json.getString("status");
                json = (JSONObject)JSON.parse(json.getString("receipt"));
                unique_identifier = json.getString("unique_identifier");
                quantity = json.getString("quantity");
                product_id = json.getString("product_id");
                transaction_id = json.getString("transaction_id");
                original_transaction_id = json.getString("original_transaction_id");
                purchase_date = json.getString("purchase_date");
                bid = json.getString("bid");
                bvrs = json.getString("bvrs");
                PlayerService.log.error("request2yx:" + playerDto.yx + "#userId:" + playerDto.userId + "#playerId:" + playerDto.playerId + "#playerName:" + playerDto.playerName + "#echo:" + echo + "#json:" + json + "#unique_identifier:" + unique_identifier + "#quantity:" + quantity + "#status:" + status + "#product_id:" + product_id + "#transaction_id:" + transaction_id + "#original_transaction_id:" + original_transaction_id + "#purchase_date:" + purchase_date + "#bid:" + bid + "#bvrs:" + bvrs);
                if (!"0".equals(status)) {
                    doc.createElement("result", 2);
                    doc.createElement("msg", (Object)LocalMessages.MOBILE_PAY_CHECK_ERROR);
                    doc.createElement("echo", echo);
                    doc.endObject();
                    return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
                }
            }
            final Tuple<Integer, Integer> result = this.yxOperation.pay(original_transaction_id, playerDto.playerId, playerDto.userId, playerDto.yx, PlayerService.goldMap.get(product_id), request);
            if (result.left == 1) {
                PlayerService.opReport.info((Object)OpLogUtil.formatOpInterfaceLog("pay", "pay_success_SUCCESS", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(1)), 1));
                doc.createElement("result", 1);
                doc.createElement("msg", (Object)LocalMessages.MOBILE_PAY_SUCCESS);
                doc.createElement("echo", echo);
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
            if (result.left == 5) {
                PlayerService.opReport.info((Object)OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_" + result.left, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(result.left)), (int)result.left));
                doc.createElement("result", 3);
                doc.createElement("msg", (Object)LocalMessages.MOBILE_PAY_ORDER_ID_EXISTS);
                doc.createElement("echo", echo);
                doc.endObject();
                return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
            }
            PlayerService.opReport.info((Object)OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_" + result.left, YxHelper.getIp(request), request.getParamterMap(), request.getContent(), false, System.currentTimeMillis() - start, new ByteResult(BackstageUtil.returnError(result.left)), (int)result.left));
            doc.createElement("result", 5);
            doc.createElement("msg", (Object)LocalMessages.MOBILE_PAY_ORDER_ID_EXISTS);
            doc.createElement("echo", echo);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        catch (Exception e) {
            PlayerService.log.error("pay_fail_EXCEPTION", e);
            PlayerService.opReport.info((Object)OpLogUtil.formatOpInterfaceLog("pay", "pay_fail_EXCEPTION", YxHelper.getIp(request), request.getParamterMap(), request.getContent(), true, System.currentTimeMillis() - start, new ByteResult(JsonBuilder.getJson(State.FAIL, JsonBuilder.getSimpleJson("result", 4))), 6));
            doc.createElement("result", 4);
            doc.createElement("msg", (Object)LocalMessages.MOBILE_PAY_INTERAL_EXCEPTION);
            doc.createElement("echo", echo);
            doc.endObject();
            return JsonBuilder.getJson(State.FAIL, doc.toByte());
        }
    }
    
    @Override
    public void loginReward() {
        final long start = System.currentTimeMillis();
        final List<Integer> list = new ArrayList<Integer>();
        final List<Integer> updateList = new ArrayList<Integer>();
        final byte[] send = JsonBuilder.getSimpleJson("haveLoginRewardActivity", 0);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.createTime == null) {
                System.out.println("loginreward2#update_error#playerId:" + dto.playerId + "#playerName:" + dto.playerName + "#createTime:" + dto.createTime + "#now:" + new Date(start));
            }
            else {
                final int day = -TimeUtil.specialToNowDays(dto.createTime);
                if (day <= 6) {
                    list.add(dto.playerId);
                    if (day == 6) {
                        continue;
                    }
                    updateList.add(dto.playerId);
                    System.out.println("loginreward2#update#playerId:" + dto.playerId + "#playerName:" + dto.playerName + "#createTime:" + dto.createTime + "#now:" + new Date(start) + "#day:" + day);
                }
                else {
                    if (day != 7) {
                        continue;
                    }
                    Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
                }
            }
        }
        if (list.size() > 0) {
            final List<PlayerTotalDay> totalDayList = this.loginRewardDao.getList(list);
            for (final PlayerTotalDay ptd : totalDayList) {
                final int playerId = ptd.getPlayerId();
                final int totalDay = ptd.getTotalDay();
                if (totalDay > 0) {
                    final String rewardStr = PlayerService.rewardMap.get(totalDay);
                    final ITaskReward itr = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
                    final Map<Integer, Reward> map = itr.rewardPlayer(PlayerDtoUtil.getPlayerDto(this.playerDao.read(playerId), this.dataGetter.getPlayerAttributeDao().read(playerId)), this.taskDataGetter, "\u8fde\u7eed\u767b\u9646\u5956\u52b1\u6d3b\u52a8", null);
                    final StringBuffer sb = new StringBuffer();
                    sb.append(MessageFormatter.format(LocalMessages.LOGIN_REWARD_MAIL_CONTENT_HEAD, new Object[] { totalDay }));
                    for (final Reward reward : map.values()) {
                        sb.append(reward.getName());
                        sb.append(reward.getNum());
                        sb.append("\uff0c");
                    }
                    sb.setLength(sb.length() - 1);
                    this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.LOGIN_REWARD_MAIL_HEAD, sb.toString(), 1, playerId, 0);
                }
            }
            this.loginRewardDao.deleteByTotalDay(7);
            this.loginRewardDao.receivedAll(0);
            if (updateList.size() > 0) {
                this.loginRewardDao.battchUpdate(updateList);
            }
        }
        PlayerService.timerLog.info(LogUtil.formatThreadLog("PlayerService", "loginReward", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Override
    public byte[] getLoginRewardInfo(final PlayerDto playerDto) {
        final int day = -TimeUtil.specialToNowDays(playerDto.createTime);
        if (day > 6) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LOGIN_REWARD_NO_REWARD_ACTIVITY);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("days");
        for (int i = 1; i <= PlayerService.SIZE; ++i) {
            doc.startObject();
            final String rewardStr = PlayerService.rewardMap.get(i);
            final ITaskReward itr = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
            final Map<Integer, Reward> map = itr.getReward(playerDto, this.taskDataGetter, null);
            doc.createElement("id", i);
            doc.startArray("rewards");
            for (final Reward reward : map.values()) {
                doc.startObject();
                doc.createElement("type", reward.getType());
                doc.createElement("value", reward.getNum());
                doc.endObject();
            }
            doc.endArray();
            doc.endObject();
        }
        doc.endArray();
        final LoginReward lr = this.loginRewardDao.read(playerDto.playerId);
        doc.createElement("haveReward", (lr.getHaveReward() == 1) ? 1 : 2);
        doc.createElement("totalDay", lr.getTotalDay());
        doc.createElement("createDay", day);
        final long cd = TimeUtil.getDay0ClackMS(playerDto.createTime) + 7 * Constants.ONE_DAY_MS - System.currentTimeMillis();
        doc.createElement("cd", cd);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] getLoginReward(final PlayerDto playerDto) {
        final int day = -TimeUtil.specialToNowDays(playerDto.createTime);
        if (day > 6) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LOGIN_REWARD_NO_REWARD_ACTIVITY);
        }
        final int playerId = playerDto.playerId;
        final LoginReward lr = this.loginRewardDao.read(playerId);
        if (lr.getHaveReward() == 0 || lr.getTotalDay() <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.LOGIN_REWARD_NO_REWARD);
        }
        this.loginRewardDao.received(playerId, 0);
        final String rewardStr = PlayerService.rewardMap.get(lr.getTotalDay());
        final ITaskReward itr = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
        final Map<Integer, Reward> map = itr.rewardPlayer(playerDto, this.taskDataGetter, "\u8fde\u7eed\u767b\u9646\u5956\u52b1\u6d3b\u52a8", null);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("rewards");
        for (final Reward reward : map.values()) {
            doc.startObject();
            doc.createElement("type", reward.getType());
            doc.createElement("value", reward.getNum());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    public static void main(final String[] args) {
        final String echo = "{\"receipt\":{\"original_purchase_date_pst\":\"2013-10-14 20:30:22 America/Los_Angeles\", \"purchase_date_ms\":\"1381807822000\", \"unique_identifier\":\"bd65ae81491ed9ffce3e0cf91bfa6ef2bf9627d8\", \"original_transaction_id\":\"1000000090028977\", \"bvrs\":\"1\", \"transaction_id\":\"1000000090028977\", \"quantity\":\"1\", \"unique_vendor_identifier\":\"1F8E5314-5981-418C-98D0-D59AAB69133D\", \"item_id\":\"665018128\", \"product_id\":\"com.aoshitang.mjcshd.5680gold\", \"purchase_date\":\"2013-10-15 03:30:22 Etc/GMT\", \"original_purchase_date\":\"2013-10-15 03:30:22 Etc/GMT\", \"purchase_date_pst\":\"2013-10-14 20:30:22 America/Los_Angeles\", \"bid\":\"com.aoshitang.mjcshd\", \"original_purchase_date_ms\":\"1381807822000\"},\"status\":0}";
        JSONObject json = (JSONObject)JSON.parse(echo);
        final String receipt = json.getString("receipt");
        json = (JSONObject)JSON.parse(receipt);
        System.out.println(json.getString("unique_identifier"));
    }
}
