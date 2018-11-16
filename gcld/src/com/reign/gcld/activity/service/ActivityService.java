package com.reign.gcld.activity.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.gcld.battle.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.job.service.*;
import com.reign.gcld.player.service.*;
import com.reign.gcld.score.dao.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.store.service.*;
import com.reign.gcld.activity.dao.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.activity.common.*;
import java.util.concurrent.locks.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.framework.json.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.battle.domain.*;
import com.reign.util.*;
import com.reign.gcld.log.*;
import org.apache.commons.lang.*;
import java.math.*;
import com.reign.gcld.score.domain.*;
import com.reign.gcld.pay.service.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.common.*;
import com.reign.plugin.yx.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.activity.domain.*;
import java.util.*;
import com.reign.gcld.store.domain.*;
import com.reign.gcld.kfwd.service.*;
import com.reign.gcld.player.domain.*;
import com.reign.framework.netty.util.*;
import com.reign.util.codec.*;
import java.text.*;

@Component("activityService")
public class ActivityService implements IActivityService, InitializingBean
{
    private static final Logger timerLog;
    private static final Logger errorLog;
    private static final int TOKEN_1 = 50;
    private static final int TOKEN_2 = 25;
    private static final int FREE_CONS = 15;
    private static final int RESOURCE_1 = 150000;
    private static final int RESOURCE_2 = 100000;
    private static final int RESOURCE_3 = 50000;
    @Autowired
    private IPlayerBattleAttributeDao playerBattleAttributeDao;
    @Autowired
    private IActivityDao activityDao;
    @Autowired
    private IPlayerLvExpDao playerLvExpDao;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private SerialCache serialCache;
    @Autowired
    private CCache cCache;
    @Autowired
    private IJobService jobService;
    @Autowired
    private IPlayerService playerService;
    @Autowired
    private IPlayerDragonDao playerDragonDao;
    @Autowired
    private FstDbNumCache fstDbNumCache;
    @Autowired
    private FstDbRewardCache fstDbRewardCache;
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private FstDbLveCache fstDbLveCache;
    @Autowired
    private IPlayerScoreRankDao playerScoreRankDao;
    @Autowired
    private IChatService chatService;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IPlayerIronDao playerIronDao;
    @Autowired
    private HallsCache hallsCache;
    @Autowired
    private IPlayerOfficeRelativeDao playerOfficeRelativeDao;
    @Autowired
    private IBonusActivityDao bonusActivityDao;
    @Autowired
    private IStoreHouseService storeHouseService;
    @Autowired
    private IPrivilege360Dao privilege360Dao;
    @Autowired
    private Gift360Cache gift360Cache;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private EquipCache equipCache;
    public static List<ActivityDto> acitvityDtoList;
    public static Long clearEndDate;
    public static Set<Integer> setActivityLv;
    public static Integer lvExpJoinActivityLv;
    public static Integer lvExpStartActivityLv;
    public static List<LvExp> lvExpList;
    public static boolean isLvExpDoing;
    public static Float lvExpTarget;
    public static ActivityDto lvExpactivityDto;
    public static boolean init;
    public static boolean inDragon;
    public static boolean inQuenching;
    public static boolean initIron;
    public static boolean inIron;
    public static Map<Integer, Tuple<Integer, Integer>> ironMap;
    public static ReentrantLock lock;
    public static ReentrantLock box_lock;
    public static final int DSTQ_TYPE_1 = 1;
    public static final int DSTQ_TYPE_2 = 2;
    public static final int DSTQ_ITEM_ID_1 = 106;
    public static final int DSTQ_ITEM_ID_2 = 107;
    public static boolean initDstq;
    public static boolean inDstq;
    public static Map<Integer, ThreeTuple<Integer, Integer, Integer>> dstqMap;
    
    static {
        timerLog = new TimerLogger();
        errorLog = CommonLog.getLog(ActivityService.class);
        ActivityService.acitvityDtoList = new ArrayList<ActivityDto>();
        ActivityService.clearEndDate = 0L;
        ActivityService.setActivityLv = new HashSet<Integer>();
        ActivityService.lvExpJoinActivityLv = 50;
        ActivityService.lvExpStartActivityLv = 60;
        ActivityService.lvExpList = new ArrayList<LvExp>();
        ActivityService.isLvExpDoing = false;
        ActivityService.lvExpTarget = 0.0f;
        ActivityService.lvExpactivityDto = new ActivityDto();
        ActivityService.init = false;
        ActivityService.inDragon = false;
        ActivityService.inQuenching = false;
        ActivityService.initIron = false;
        ActivityService.inIron = false;
        ActivityService.ironMap = new ConcurrentHashMap<Integer, Tuple<Integer, Integer>>();
        ActivityService.lock = new ReentrantLock(false);
        ActivityService.box_lock = new ReentrantLock(false);
        ActivityService.initDstq = false;
        ActivityService.inDstq = false;
        ActivityService.dstqMap = new ConcurrentHashMap<Integer, ThreeTuple<Integer, Integer, Integer>>();
    }
    
    @Transactional
    @Override
    public byte[] get51activity(final PlayerDto playerDto) {
        if (!this.isTodayInBatExpActivity()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_BATTLE_EXP_TIP2);
        }
        String condition = null;
        int addExp = 0;
        if (playerDto.playerLv < 55) {
            condition = "55-69";
        }
        else if (playerDto.playerLv <= 69) {
            condition = "55-69";
            addExp = 1;
        }
        else if (playerDto.playerLv <= 79) {
            condition = "70-79";
            addExp = 2;
        }
        else {
            condition = String.valueOf(80) + LocalMessages.ACTIVITY_BATTLE_EXP_TIP;
            addExp = 3;
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("condition", condition);
        doc.createElement("addExp", addExp);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public float getAddBatValue(final int playerId, final int playerLv) {
        float addExp = 0.0f;
        if (!this.isTodayInBatExpActivity()) {
            return addExp;
        }
        if (playerId <= 0) {
            return addExp;
        }
        final PlayerBattleAttribute pba = this.playerBattleAttributeDao.read(playerId);
        if (pba != null && pba.getActivityBatExp() > 0 && playerLv >= 55) {
            if (playerLv <= 69) {
                addExp = 0.1f;
            }
            else if (playerLv <= 79) {
                addExp = 0.2f;
            }
            else {
                addExp = 0.3f;
            }
        }
        return addExp;
    }
    
    @Transactional
    @Override
    public byte[] reward51Activity(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        if (playerDto.playerLv < 55) {
            return JsonBuilder.getJson(State.FAIL, MessageFormatter.format(LocalMessages.ACTIVITY_BATTLE_EXP_ADD_LOWER_LV, new Object[] { 55 }));
        }
        final PlayerBattleAttribute pba = this.playerBattleAttributeDao.read(playerId);
        if (pba.getActivityBatExp() > 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_BATTLE_EXP_TIP3);
        }
        if (!this.isTodayInBatExpActivity()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_BATTLE_EXP_TIP2);
        }
        this.playerBattleAttributeDao.updateBatExpActivity(playerId, 1);
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Override
    public void sendBatExpActivity() {
        final long start = System.currentTimeMillis();
        if (this.isTodayInBatExpActivity()) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("activity51", true);
            doc.endObject();
            final Collection<PlayerDto> playerDtos = Players.getAllPlayer();
            for (final PlayerDto playerDto : playerDtos) {
                Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
            }
        }
        final long end = System.currentTimeMillis();
        ActivityService.timerLog.info(LogUtil.formatTimerLog("activityService", "sendBatExpActivity", end - start));
    }
    
    @Override
    public void clearBatExpActivity(final String params) {
        this.clearBatExpActivity();
    }
    
    @Override
    public void sendBatExpActivity(final String params) {
        final long start = System.currentTimeMillis();
        this.sendBatExpActivity();
        ActivityService.timerLog.info(LogUtil.formatThreadLog("ActivityService", "sendBatExpActivity", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Transactional
    @Override
    public void clearBatExpActivity() {
        final long start = System.currentTimeMillis();
        final long curTime = System.currentTimeMillis();
        if (curTime < ActivityService.clearEndDate) {
            this.playerBattleAttributeDao.clearBatExpActivity();
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("activity51", false);
            doc.endObject();
            final Collection<PlayerDto> playerDtos = Players.getAllPlayer();
            for (final PlayerDto playerDto : playerDtos) {
                Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
            }
        }
        final long end = System.currentTimeMillis();
        ActivityService.timerLog.info(LogUtil.formatTimerLog("activityService", "clearBatExpActivity", end - start));
    }
    
    @Override
    public boolean isTodayInBatExpActivity() {
        final long curTime = System.currentTimeMillis();
        for (final ActivityDto ad : ActivityService.acitvityDtoList) {
            if (curTime >= ad.getStartTime() && curTime <= ad.getEndTime()) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isInLvExpActivity() {
        final long curTime = System.currentTimeMillis();
        return curTime >= ActivityService.lvExpactivityDto.getStartTime() && curTime <= ActivityService.lvExpactivityDto.getEndTime();
    }
    
    @Override
    public byte[] initLvExpActivity(final int type, final long startTime, final long endTime, final String paramsInfo, final boolean isReboot) {
        Activity activity = this.activityDao.read(type);
        if (StringUtils.isBlank(paramsInfo)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final String[] paramsInfos = paramsInfo.split(";");
        final Map<Float, Float> map = new HashMap<Float, Float>();
        String[] array;
        for (int length = (array = paramsInfos).length, i = 0; i < length; ++i) {
            final String ss = array[i];
            final String[] infos = ss.split(",");
            map.put(Float.valueOf(infos[0]), Float.valueOf(infos[1]));
        }
        final Long curTime = System.currentTimeMillis();
        if (!isReboot && curTime + 60000L > startTime) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_START_TIME_SET);
        }
        if (activity != null) {
            if (!isReboot) {
                if (activity.getStartTime().getTime() <= curTime && activity.getEndTime().getTime() >= curTime) {
                    ActivityService.isLvExpDoing = true;
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_DOING_NOT_RESET);
                }
                this.activityDao.updateInfo(activity.getVId(), new Date(startTime), new Date(endTime), paramsInfo);
            }
        }
        else {
            activity = new Activity();
            activity.setEndTime(new Date(endTime));
            activity.setStartTime(new Date(startTime));
            activity.setVId(type);
            activity.setName(LocalMessages.ACTIVITY_NAME_3);
            activity.setParamsInfo(paramsInfo);
            this.activityDao.create(activity);
        }
        if (activity.getStartTime().getTime() <= curTime && activity.getEndTime().getTime() >= curTime) {
            ActivityService.isLvExpDoing = true;
        }
        ActivityService.lvExpList.clear();
        ActivityService.lvExpTarget = 0.0f;
        String[] array2;
        for (int length2 = (array2 = paramsInfos).length, j = 0; j < length2; ++j) {
            final String ss2 = array2[j];
            final String[] infos2 = ss2.split(",");
            final float key = Float.valueOf(infos2[0]);
            final LvExp lvExp = new LvExp(key, Float.valueOf(infos2[1]));
            ActivityService.lvExpList.add(lvExp);
            if (key > ActivityService.lvExpTarget) {
                ActivityService.lvExpTarget = key;
            }
        }
        ActivityService.lvExpactivityDto.setStartTime(startTime);
        ActivityService.lvExpactivityDto.setEndTime(endTime);
        if (!isReboot) {
            this.playerLvExpDao.deleteAll();
        }
        if (startTime > System.currentTimeMillis()) {
            this.jobService.addJob("activityService", "initLvExpActivityStart", "", startTime, false);
        }
        if (endTime > System.currentTimeMillis()) {
            this.jobService.addJob("activityService", "initLvExpActivityEnd", "", endTime, false);
        }
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    @Override
    public byte[] initDragonActivity(final int type, final long startTime, final long endTime) {
        Activity activity = this.activityDao.read(type);
        final Long curTime = System.currentTimeMillis();
        if (curTime + 60000L > startTime) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_START_TIME_SET);
        }
        if (activity != null) {
            if (activity.getStartTime().getTime() <= curTime && activity.getEndTime().getTime() >= curTime) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_DOING_NOT_RESET);
            }
            this.activityDao.updateInfo(activity.getVId(), new Date(startTime), new Date(endTime), "");
        }
        else {
            activity = new Activity();
            activity.setEndTime(new Date(endTime));
            activity.setStartTime(new Date(startTime));
            activity.setVId(type);
            activity.setName(LocalMessages.ACTIVITY_NAME_4);
            activity.setParamsInfo("");
            this.activityDao.create(activity);
        }
        if (startTime > System.currentTimeMillis()) {
            this.jobService.addJob("activityService", "initDragonActivityStart", "", startTime, false);
        }
        if (endTime > System.currentTimeMillis()) {
            this.jobService.addJob("activityService", "initDragonActivityEnd", "", endTime, false);
        }
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    @Override
    public byte[] initIronActivity(final int type, final long startTime, final long endTime, final String paramsInfo) {
        ActivityService.errorLog.error("class:ActivityService#method:initIronActivity#begin");
        final Activity activity = this.activityDao.read(type);
        if (activity != null && new Date().after(activity.getStartTime()) && new Date().before(activity.getEndTime())) {
            ActivityService.errorLog.error("class:ActivityService#method:initIronActivity#not_reset");
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_DOING_NOT_RESET);
        }
        Tuple<Boolean, String> result = new Tuple(false, "");
        result = this.checkIronActivity(startTime, endTime, paramsInfo);
        if (!(boolean)result.left) {
            ActivityService.errorLog.error("class:ActivityService#method:initIronActivity#error:" + result.right);
            return JsonBuilder.getJson(State.FAIL, result.right);
        }
        final Long curTime = System.currentTimeMillis();
        if (curTime + 60000L > startTime) {
            ActivityService.errorLog.error("class:ActivityService#method:initIronActivity#error_time");
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_START_TIME_SET);
        }
        if (activity == null) {
            final Activity newActivity = new Activity();
            newActivity.setVId(type);
            newActivity.setStartTime(new Date(startTime));
            newActivity.setEndTime(new Date(endTime));
            newActivity.setParamsInfo(paramsInfo);
            newActivity.setName(LocalMessages.ACTIVITY_NAME_5);
            this.activityDao.create(newActivity);
        }
        else {
            this.activityDao.updateInfo(type, new Date(startTime), new Date(endTime), paramsInfo);
        }
        if (startTime > System.currentTimeMillis()) {
            this.jobService.addJob("activityService", "initIronActivityBegin", "", startTime, false);
        }
        if (endTime > System.currentTimeMillis()) {
            this.jobService.addJob("activityService", "initIronActivityEnd", "", endTime, false);
        }
        ActivityService.errorLog.error("class:ActivityService#method:initIronActivity#success");
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    @Override
    public byte[] initDstqActivity(final int type, final long startTime, final long endTime, final String paramsInfo) {
        ActivityService.errorLog.error("class:ActivityService#method:initDstqActivity#begin");
        final Activity activity = this.activityDao.read(type);
        if (activity != null && new Date().after(activity.getStartTime()) && new Date().before(activity.getEndTime())) {
            ActivityService.errorLog.error("class:ActivityService#method:initDstqActivity#not_reset");
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_DOING_NOT_RESET);
        }
        final Tuple<Boolean, String> result = this.checkDstqActivity(startTime, endTime, paramsInfo);
        if (!(boolean)result.left) {
            ActivityService.errorLog.error("class:ActivityService#method:initDstqActivity#error:" + result.right);
            return JsonBuilder.getJson(State.FAIL, result.right);
        }
        final Long curTime = System.currentTimeMillis();
        if (curTime + 60000L > startTime) {
            ActivityService.errorLog.error("class:ActivityService#method:initDstqActivity#error_time");
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_START_TIME_SET);
        }
        if (activity == null) {
            final Activity newActivity = new Activity();
            newActivity.setVId(type);
            newActivity.setStartTime(new Date(startTime));
            newActivity.setEndTime(new Date(endTime));
            newActivity.setParamsInfo(paramsInfo);
            newActivity.setName(LocalMessages.ACTIVITY_NAME_8);
            this.activityDao.create(newActivity);
        }
        else {
            this.activityDao.updateInfo(type, new Date(startTime), new Date(endTime), paramsInfo);
        }
        this.bonusActivityDao.clearAll();
        if (startTime > System.currentTimeMillis()) {
            this.jobService.addJob("activityService", "initDstqActivityBegin", "", startTime, false);
        }
        if (endTime > System.currentTimeMillis()) {
            this.jobService.addJob("activityService", "initDstqActivityEnd", "", endTime, false);
        }
        ActivityService.errorLog.error("class:ActivityService#method:initDstqActivity#success");
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    private Tuple<Boolean, String> checkIronActivity(final long startTime, final long endTime, final String paramsInfo) {
        final Tuple<Boolean, String> result = new Tuple(false, "");
        final long curTime = System.currentTimeMillis();
        if (startTime <= 0L || endTime <= 0L || StringUtils.isBlank(paramsInfo)) {
            result.right = LocalMessages.IRON_ACTIVITY_NULL_VALUE;
            return result;
        }
        if (startTime >= endTime || startTime <= curTime || endTime <= curTime) {
            result.right = LocalMessages.IRON_ACTIVITY_INVALID_TIME;
            return result;
        }
        int maxNIron = 0;
        int maxRIron = 0;
        final String[] rules = paramsInfo.trim().split(";");
        String[] array;
        for (int length = (array = rules).length, i = 0; i < length; ++i) {
            final String str = array[i];
            if (str.split(",").length != 2) {
                result.right = LocalMessages.IRON_ACTIVITY_INVALID_RULES;
                return result;
            }
            try {
                final int needIron = Integer.parseInt(str.split(",")[0]);
                final int rewardIron = Integer.parseInt(str.split(",")[1]);
                if (rewardIron * 1.0 / needIron > 0.5) {
                    result.right = LocalMessages.IRON_ACTIVITY_PROTECT;
                    return result;
                }
                if (needIron <= maxNIron || rewardIron <= maxRIron) {
                    result.right = LocalMessages.IRON_ACTIVITY_INVALID_RULES;
                    return result;
                }
                maxNIron = needIron;
                maxRIron = rewardIron;
            }
            catch (NumberFormatException e) {
                result.right = LocalMessages.IRON_ACTIVITY_NUMBER_FORMAT_EXCEPTION;
                return result;
            }
        }
        result.left = true;
        return result;
    }
    
    @Override
    public void reachJoinLvExpActivity(final int playerId) {
        if (!isInLvExpActivity()) {
            return;
        }
        PlayerLvExp playerLvExp = this.playerLvExpDao.read(playerId);
        if (playerLvExp != null) {
            return;
        }
        final Player player = this.playerDao.read(playerId);
        final PlayerResource playerResource = this.playerResourceDao.read(playerId);
        final int cAxis = (int)(Object)((C)this.cCache.get((Object)"Chief.UpgradeExpS")).getValue();
        final int upNum = this.serialCache.get(cAxis, player.getPlayerLv());
        double curLvPoint = playerResource.getExp() / 1.0 / upNum;
        final BigDecimal bd = new BigDecimal(curLvPoint);
        curLvPoint = bd.setScale(3, 4).doubleValue();
        playerLvExp = new PlayerLvExp();
        playerLvExp.setPlayerId(playerId);
        playerLvExp.setNewExp(0L);
        playerLvExp.setNewLv(0);
        playerLvExp.setReward(0);
        final boolean canUpdate = player.getPlayerLv() >= ActivityService.lvExpStartActivityLv;
        if (canUpdate) {
            playerLvExp.setLv(player.getPlayerLv());
            playerLvExp.setExp((long)playerResource.getExp());
        }
        else {
            playerLvExp.setExp(0L);
            playerLvExp.setLv(ActivityService.lvExpStartActivityLv);
        }
        this.playerLvExpDao.create(playerLvExp);
    }
    
    @Transactional
    @Override
    public byte[] rewardLvExpActivity(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final PlayerLvExp playerLvExp = this.playerLvExpDao.read(playerId);
        if (playerLvExp == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        if (isInLvExpActivity()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_DOING_NOT_RESET);
        }
        if (playerLvExp.getReward() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_DOING_REWARD_CALCULATE);
        }
        if (playerLvExp.getReward() == 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10044);
        }
        this.playerLvExpDao.updateReward(playerId);
        final int cAxis = (int)(Object)((C)this.cCache.get((Object)"Chief.UpgradeExpS")).getValue();
        int upNum = this.serialCache.get(cAxis, playerLvExp.getLv());
        double startLvPoint = playerLvExp.getExp() * 1.0 / upNum;
        BigDecimal startBd = new BigDecimal(startLvPoint);
        startLvPoint = startBd.setScale(3, 4).doubleValue();
        upNum = this.serialCache.get(cAxis, playerLvExp.getNewLv());
        if (upNum == 0) {
            upNum = 1;
        }
        double curLvPoint = playerLvExp.getNewExp() * 1.0 / upNum;
        startBd = new BigDecimal(curLvPoint);
        curLvPoint = startBd.setScale(3, 4).doubleValue();
        float addLv = 0.0f;
        double addPoint = playerLvExp.getNewLv() - playerLvExp.getLv() + curLvPoint - startLvPoint;
        startBd = new BigDecimal(addPoint);
        addPoint = startBd.setScale(3, 4).doubleValue();
        for (final LvExp lvExp : ActivityService.lvExpList) {
            if (lvExp.lv <= addPoint && addLv < lvExp.exp) {
                addLv = lvExp.exp;
            }
        }
        final int addLvExp = this.getAddLvExp(playerLvExp.getNewLv(), curLvPoint, addLv);
        if (addLvExp > 0) {
            this.playerService.updateExpAndPlayerLevel(playerId, addLvExp, "\u51b2\u7ea7\u9001\u7ecf\u9a8c\u6d3b\u52a8\u589e\u52a0\u7ecf\u9a8c");
        }
        doc.createElement("addLvExp", addLvExp);
        doc.endObject();
        final JsonDocument doc2 = new JsonDocument();
        doc2.startObject();
        doc2.createElement("activityLvExp", false);
        doc2.endObject();
        Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc2.toByte());
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] getLvExpActivity(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        if (player.getPlayerLv() < ActivityService.lvExpJoinActivityLv) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final PlayerResource playerResource = this.playerResourceDao.read(playerId);
        PlayerLvExp playerLvExp = this.playerLvExpDao.read(playerId);
        final int cAxis = (int)(Object)((C)this.cCache.get((Object)"Chief.UpgradeExpS")).getValue();
        final boolean isInActivity = isInLvExpActivity();
        if (playerLvExp != null && playerLvExp.getReward() == 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10044);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        if (playerLvExp != null && !isInActivity) {
            int upNum = this.serialCache.get(cAxis, playerLvExp.getLv());
            double startLvPoint = playerLvExp.getExp() * 1.0 / upNum;
            BigDecimal startBd = new BigDecimal(startLvPoint);
            startLvPoint = startBd.setScale(3, 4).doubleValue();
            upNum = this.serialCache.get(cAxis, playerLvExp.getNewLv());
            if (upNum == 0) {
                upNum = 1;
            }
            double curLvPoint = playerLvExp.getNewExp() * 1.0 / upNum;
            startBd = new BigDecimal(curLvPoint);
            curLvPoint = startBd.setScale(3, 4).doubleValue();
            float addLv = 0.0f;
            double addPoint = playerLvExp.getNewLv() - playerLvExp.getLv() + curLvPoint - startLvPoint;
            startBd = new BigDecimal(addPoint);
            addPoint = startBd.setScale(3, 4).doubleValue();
            for (final LvExp lvExp : ActivityService.lvExpList) {
                if (lvExp.lv <= addPoint && addLv < lvExp.exp) {
                    addLv = lvExp.exp;
                }
            }
            doc.createElement("reward", true);
            doc.createElement("startLv", playerLvExp.getLv() + startLvPoint);
            doc.createElement("curLv", playerLvExp.getNewLv() + curLvPoint);
            doc.createElement("addLvExp", this.getAddLvExp(playerLvExp.getNewLv(), curLvPoint, addLv));
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        if (!isInActivity && (playerLvExp == null || playerLvExp.getReward() == 2)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_BATTLE_EXP_TIP2);
        }
        double startLvPoint2 = 0.0;
        if (playerLvExp != null) {
            final int upNum2 = this.serialCache.get(cAxis, playerLvExp.getLv());
            startLvPoint2 = playerLvExp.getExp() * 1.0 / upNum2;
            final BigDecimal startBd = new BigDecimal(startLvPoint2);
            startLvPoint2 = startBd.setScale(3, 4).doubleValue();
        }
        else {
            final int upNum2 = this.serialCache.get(cAxis, player.getPlayerLv());
            startLvPoint2 = playerResource.getExp() / 1.0 / upNum2;
            final BigDecimal startBd = new BigDecimal(startLvPoint2);
            startLvPoint2 = startBd.setScale(3, 4).doubleValue();
        }
        if (isInActivity && playerLvExp == null) {
            final boolean canUpdate = player.getPlayerLv() >= ActivityService.lvExpStartActivityLv;
            playerLvExp = new PlayerLvExp();
            playerLvExp.setPlayerId(playerId);
            if (canUpdate) {
                playerLvExp.setLv(player.getPlayerLv());
                playerLvExp.setExp((long)playerResource.getExp());
            }
            else {
                playerLvExp.setExp(0L);
                playerLvExp.setLv(ActivityService.lvExpStartActivityLv);
            }
            playerLvExp.setNewExp(0L);
            playerLvExp.setNewLv(0);
            playerLvExp.setReward(0);
            this.playerLvExpDao.create(playerLvExp);
        }
        final long leftTime = ActivityService.lvExpactivityDto.getEndTime() - System.currentTimeMillis();
        doc.createElement("reward", false);
        doc.createElement("leftTime", (leftTime > 0L) ? (leftTime + 5000L) : 0L);
        final int upNum3 = this.serialCache.get(cAxis, player.getPlayerLv());
        double curLvPoint2 = playerResource.getExp() / 1.0 / upNum3;
        final BigDecimal curBd = new BigDecimal(curLvPoint2);
        curLvPoint2 = curBd.setScale(3, 4).doubleValue();
        doc.createElement("startLv", playerLvExp.getLv() + startLvPoint2);
        doc.createElement("curLv", player.getPlayerLv() + curLvPoint2);
        int aa = (int)((playerLvExp.getLv() + startLvPoint2 + ActivityService.lvExpTarget) * 10000.0);
        aa = (int)Math.ceil(aa / 10.0);
        doc.createElement("targetLv", aa / 1000.0);
        final double curAddLv = player.getPlayerLv() - playerLvExp.getLv() + curLvPoint2 - startLvPoint2;
        float addLv2 = 0.0f;
        doc.startArray("lvExps");
        int id = 1;
        for (final LvExp lvExp2 : ActivityService.lvExpList) {
            doc.startObject();
            doc.createElement("id", (id++));
            if (lvExp2.lv <= curAddLv) {
                addLv2 = lvExp2.exp;
                doc.createElement("dis", 0);
            }
            else {
                doc.createElement("dis", lvExp2.lv + playerLvExp.getLv() - player.getPlayerLv() + startLvPoint2 - curLvPoint2);
            }
            aa = (int)((playerLvExp.getLv() + startLvPoint2 + lvExp2.lv) * 10000.0);
            aa = (int)Math.ceil(aa / 10.0);
            doc.createElement("targetLv", aa / 1000.0);
            doc.createElement("addLvExp", this.getAddLvExp(player.getPlayerLv(), curLvPoint2, lvExp2.exp));
            doc.endObject();
        }
        doc.endArray();
        final int addLvExp = this.getAddLvExp(player.getPlayerLv(), curLvPoint2, addLv2);
        doc.createElement("addLvExp", addLvExp);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private int getAddLvExp(final int playerLv, double point, double addLv) {
        final int cAxis = (int)(Object)((C)this.cCache.get((Object)"Chief.UpgradeExpS")).getValue();
        final int firstLvExp = this.serialCache.get(cAxis, playerLv);
        int addExp = 0;
        if (point + addLv > 1.0) {
            addExp += (int)(firstLvExp * (1.0 - point));
            addLv = point + addLv - 1.0;
        }
        else {
            addExp += (int)(firstLvExp * addLv);
            addLv = 0.0;
        }
        int lv;
        int i;
        int nextLvExp;
        for (lv = (int)addLv, i = 1, i = 1; i <= lv; ++i) {
            nextLvExp = this.serialCache.get(cAxis, playerLv + i);
            addExp += nextLvExp;
        }
        point = addLv - lv;
        if (point > 0.0) {
            final int lastLvExp = this.serialCache.get(cAxis, playerLv + i);
            addExp += (int)(lastLvExp * point);
        }
        return addExp;
    }
    
    @Transactional
    @Override
    public byte[] initLvExp(final PlayerDto playerDto) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("addLvExp", 10000);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getDragonInfo(final PlayerDto playerDto) {
        if (playerDto.cs[10] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DRAGON_NO_FUNCTION);
        }
        final int playerId = playerDto.playerId;
        final int dragonNum = this.playerDragonDao.getDragonNumByPlayerId(playerId);
        final Activity activity = this.activityDao.read(4);
        if (!this.inDragonBoatFestival() && dragonNum <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DRAGON_NO_ACTIVITY);
        }
        PlayerScoreRank psr = this.playerScoreRankDao.read(playerId);
        if (psr == null) {
            this.openPlayerScoreRank(playerId);
            psr = this.playerScoreRankDao.read(playerId);
        }
        final int todayNum = psr.getScore();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("lvs");
        int lv = 0;
        int count = 0;
        int needNum = 0;
        boolean flag_needGold = false;
        for (int size = this.fstDbNumCache.getModels().size(), i = 1; i <= size; ++i) {
            final FstDbNum fdn = (FstDbNum)this.fstDbNumCache.get((Object)i);
            final int nNum = fdn.getCityOcc();
            final int rNum = fdn.getDNum();
            doc.startObject();
            doc.createElement("num", nNum);
            doc.createElement("rewardNum", rNum);
            doc.endObject();
            ++count;
            if (todayNum >= nNum) {
                lv = count;
            }
            if (!flag_needGold && todayNum < nNum) {
                needNum = nNum - todayNum;
                flag_needGold = true;
            }
        }
        doc.endArray();
        doc.createElement("cd", TimeUtil.now2specMs(activity.getEndTime().getTime()));
        doc.createElement("todayNum", todayNum);
        doc.createElement("dragonNum", dragonNum);
        doc.createElement("lv", lv + 1);
        doc.createElement("needNum", needNum);
        doc.createElement("maxNum", this.fstDbNumCache.getMaxNum());
        doc.createElement("occupyNum", psr.getOccupyNum());
        doc.createElement("assistNum", psr.getAssistNum());
        doc.createElement("cheerNum", psr.getCheerNum());
        doc.createElement("occupyScore", 5);
        doc.createElement("assistScore", 2);
        doc.createElement("cheerScore", 1);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] useDragon(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final int dragonNum = this.playerDragonDao.getDragonNumByPlayerId(playerId);
        if (dragonNum <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DRAGON_NO_DRAGON);
        }
        final int res = this.playerDragonDao.useDragon(playerId);
        if (res < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DRAGON_NO_DRAGON);
        }
        final FstDbReward fdr = this.fstDbRewardCache.getFstDbReward();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final int baoji = this.getBaoji(fdr.getQuality());
        doc.createElement("baoji", baoji);
        int num = fdr.getNum();
        String displayMsg = "";
        final int type = fdr.getType();
        if (1 == type) {
            final double rate = this.fstDbLveCache.getRate(playerDto.playerLv);
            num *= (int)rate;
            this.playerService.updateExpAndPlayerLevel(playerId, num, "\u653b\u57ce\u6d3b\u52a8\u5b9d\u7bb1\u589e\u52a0\u7ecf\u9a8c");
            displayMsg = LocalMessages.DRAGON_EAT_DRAGON_BAOJI_10_1;
        }
        else if (2 == type) {
            this.playerBattleAttributeDao.addVip3PhantomCount(playerId, num, "\u653b\u57ce\u6d3b\u52a8\u5b9d\u7bb1\u83b7\u5f97\u514d\u8d39\u501f\u5175\u6b21\u6570");
            displayMsg = LocalMessages.DRAGON_EAT_DRAGON_BAOJI_10_2;
        }
        else if (3 == type) {
            this.playerAttributeDao.addRecruitToken(playerId, num, "\u653b\u57ce\u6d3b\u52a8\u5b9d\u7bb1\u83b7\u5f97\u52df\u5175\u4ee4");
            displayMsg = LocalMessages.DRAGON_EAT_DRAGON_BAOJI_10_3;
        }
        else {
            final double rate = this.fstDbLveCache.getRate(playerDto.playerLv);
            num *= (int)rate;
            this.playerResourceDao.addFoodIgnoreMax(playerId, num, "\u653b\u57ce\u6d3b\u52a8\u5b9d\u7bb1\u83b7\u53d6\u7cae\u98df");
            displayMsg = LocalMessages.DRAGON_EAT_DRAGON_BAOJI_10_4;
        }
        doc.createElement("type", this.getDisplayType(type));
        doc.createElement("num", num);
        doc.endObject();
        if (10 == baoji) {
            final String msg = MessageFormatter.format(displayMsg, new Object[] { this.playerDao.getPlayerName(playerId), num });
            this.chatService.sendBigNotice("GLOBAL", null, msg, null);
        }
        if ((!this.inDragonBoatFestival() || this.fstDbNumCache.getMaxNum() <= this.playerScoreRankDao.getScore(playerId)) && this.playerDragonDao.getDragonNumByPlayerId(playerId) <= 0) {
            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("haveDragonActivity", 0));
        }
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private int getBaoji(final int quality) {
        if (4 == quality) {
            return 2;
        }
        if (5 == quality) {
            return 4;
        }
        if (6 == quality) {
            return 10;
        }
        return 0;
    }
    
    private int getDisplayType(final int type) {
        if (1 == type) {
            return 6;
        }
        if (2 == type) {
            return 12;
        }
        if (3 == type) {
            return 9;
        }
        return 3;
    }
    
    @Override
    public void initLvExpActivityStart(final String param) {
        final Activity activity = this.activityDao.read(3);
        final Long time = System.currentTimeMillis();
        if (time >= activity.getStartTime().getTime() && time <= activity.getEndTime().getTime()) {
            ActivityService.isLvExpDoing = true;
            this.playerLvExpDao.initActivity();
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("activityLvExp", true);
            doc.endObject();
            final Collection<PlayerDto> playerDtos = Players.getAllPlayer();
            for (final PlayerDto playerDto : playerDtos) {
                if (playerDto.playerLv > ActivityService.lvExpJoinActivityLv) {
                    Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
                }
            }
        }
    }
    
    @Override
    public void initDragonActivityStart(final String param) {
        final Activity activity = this.activityDao.read(4);
        if (activity == null) {
            return;
        }
        final Long time = System.currentTimeMillis();
        if (time >= activity.getStartTime().getTime() && time <= activity.getEndTime().getTime()) {
            ActivityService.inDragon = true;
            final byte[] send = JsonBuilder.getSimpleJson("haveDragonActivity", 1);
            final Collection<PlayerDto> playerDtos = Players.getAllPlayer();
            for (final PlayerDto playerDto : playerDtos) {
                if (playerDto.cs[10] == '1') {
                    Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, send);
                }
            }
        }
    }
    
    @Override
    public void initIronActivityBegin(final String param) {
        final Activity activity = this.activityDao.read(5);
        if (activity == null) {
            return;
        }
        final Long time = System.currentTimeMillis();
        if (time >= activity.getStartTime().getTime() && time <= activity.getEndTime().getTime()) {
            final String[] rules = activity.getParamsInfo().trim().split(";");
            int index = 1;
            String[] array;
            for (int length = (array = rules).length, i = 0; i < length; ++i) {
                final String str = array[i];
                final String[] temp = str.split(",");
                ActivityService.ironMap.put(index++, new Tuple(Integer.parseInt(temp[0]), Integer.parseInt(temp[1])));
            }
            ActivityService.inIron = true;
            final byte[] send = JsonBuilder.getSimpleJson("haveIronActivity", 1);
            final Collection<PlayerDto> playerDtos = Players.getAllPlayer();
            for (final PlayerDto playerDto : playerDtos) {
                Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
    }
    
    @Override
    public void initDstqActivityBegin(final String param) {
        final Activity activity = this.activityDao.read(8);
        if (activity == null) {
            return;
        }
        final Long time = System.currentTimeMillis();
        if (time >= activity.getStartTime().getTime() && time <= activity.getEndTime().getTime()) {
            final String[] rules = activity.getParamsInfo().trim().split(";");
            int index = 1;
            String[] array;
            for (int length = (array = rules).length, i = 0; i < length; ++i) {
                final String str = array[i];
                final String[] temp = str.split(",");
                ActivityService.dstqMap.put(index++, new ThreeTuple<Integer, Integer, Integer>(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2])));
            }
            ActivityService.inDstq = true;
            final byte[] send = JsonBuilder.getSimpleJson("haveDstqActivity", 1);
            final Collection<PlayerDto> playerDtos = Players.getAllPlayer();
            for (final PlayerDto playerDto : playerDtos) {
                Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
    }
    
    @Override
    public void initDstqActivityEnd(final String param) {
        final Activity activity = this.activityDao.read(8);
        final Long time = System.currentTimeMillis();
        if (activity == null || time >= activity.getEndTime().getTime()) {
            ActivityService.inDstq = false;
            final byte[] send = JsonBuilder.getSimpleJson("haveDstqActivity", 0);
            final Collection<PlayerDto> playerDtos = Players.getAllPlayer();
            for (final PlayerDto playerDto : playerDtos) {
                Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, send);
            }
            ActivityService.dstqMap.clear();
        }
    }
    
    @Override
    public void initDragonActivityEnd(final String param) {
        final Activity activity = this.activityDao.read(4);
        final Long time = System.currentTimeMillis();
        try {
            ActivityService.box_lock.lock();
            if (ActivityService.inDragon && (activity == null || time >= activity.getEndTime().getTime())) {
                ActivityService.inDragon = false;
                final byte[] send = JsonBuilder.getSimpleJson("haveDragonActivity", 0);
                final Collection<PlayerDto> playerDtos = Players.getAllPlayer();
                for (final PlayerDto playerDto : playerDtos) {
                    if (playerDto.cs[10] == '1') {
                        Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, send);
                    }
                }
                final List<PlayerDragon> pdList = this.playerDragonDao.getDragonNumList();
                for (final PlayerDragon pd : pdList) {
                    try {
                        final int boxNum = pd.getDragonNum();
                        int exp = 0;
                        int photom = 0;
                        int token = 0;
                        int food = 0;
                        final int playerId = pd.getPlayerId();
                        final Player player = this.playerDao.read(playerId);
                        for (int i = 0; i < boxNum; ++i) {
                            final FstDbReward fdr = this.fstDbRewardCache.getFstDbReward();
                            final int num = fdr.getNum();
                            final int type = fdr.getType();
                            if (1 == type) {
                                final double rate = this.fstDbLveCache.getRate(player.getPlayerLv());
                                exp += (int)(num * rate);
                            }
                            else if (2 == type) {
                                photom += num;
                            }
                            else if (3 == type) {
                                token += num;
                            }
                            else {
                                final double rate = this.fstDbLveCache.getRate(player.getPlayerLv());
                                food += (int)(num * rate);
                            }
                        }
                        String mailMsg = MessageFormatter.format(LocalMessages.DRAGON_MAIL_TITAL, new Object[] { boxNum });
                        if (exp > 0) {
                            this.playerService.updateExpAndPlayerLevel(playerId, exp, "\u653b\u57ce\u6d3b\u52a8\u5b9d\u7bb1\u589e\u52a0\u7ecf\u9a8c");
                            mailMsg = String.valueOf(mailMsg) + LocalMessages.T_COMM_10039 + "+" + exp;
                        }
                        if (photom > 0) {
                            this.playerBattleAttributeDao.addVip3PhantomCount(playerId, photom, "\u653b\u57ce\u6d3b\u52a8\u5b9d\u7bb1\u83b7\u5f97\u514d\u8d39\u501f\u5175\u6b21\u6570");
                            mailMsg = String.valueOf(mailMsg) + LocalMessages.DRAGON_MAIL_PHOTOM + "+" + photom;
                        }
                        if (token > 0) {
                            this.playerAttributeDao.addRecruitToken(playerId, token, "\u653b\u57ce\u6d3b\u52a8\u5b9d\u7bb1\u83b7\u5f97\u52df\u5175\u4ee4");
                            mailMsg = String.valueOf(mailMsg) + LocalMessages.T_COMM_10022 + "+" + token;
                        }
                        if (food > 0) {
                            this.playerResourceDao.addFoodIgnoreMax(playerId, food, "\u653b\u57ce\u6d3b\u52a8\u5b9d\u7bb1\u83b7\u53d6\u7cae\u98df");
                            mailMsg = String.valueOf(mailMsg) + LocalMessages.T_COMM_10017 + "+" + food;
                        }
                        this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.DRAGON_MAIL_HEAD, mailMsg, 1, playerId, 0);
                    }
                    catch (Exception e) {
                        ActivityService.errorLog.error("class:ActivityService#method:initDragonActivityEnd#exception:", e);
                    }
                }
                this.playerDragonDao.clearDragon();
            }
        }
        finally {
            ActivityService.box_lock.unlock();
        }
        ActivityService.box_lock.unlock();
    }
    
    @Override
    public void initIronActivityEnd(final String param) {
        final Activity activity = this.activityDao.read(5);
        final Long time = System.currentTimeMillis();
        try {
            ActivityService.lock.lock();
            if (ActivityService.inIron && (activity == null || time >= activity.getEndTime().getTime())) {
                ActivityService.inIron = false;
                final byte[] send = JsonBuilder.getSimpleJson("haveIronActivity", 0);
                final Collection<PlayerDto> playerDtos = Players.getAllPlayer();
                for (final PlayerDto playerDto : playerDtos) {
                    Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, send);
                }
                final List<PlayerIron> piList = this.playerIronDao.getReceivedList();
                for (final PlayerIron pi : piList) {
                    try {
                        final int iron = this.getIron(pi.getReward(), pi.getReceived());
                        final int playerId = pi.getPlayerId();
                        this.playerResourceDao.addIronIgnoreMax(playerId, iron, "\u9554\u94c1\u5927\u56de\u9988\u6d3b\u52a8\u83b7\u53d6\u9554\u94c1", false);
                        final String mailMsg = MessageFormatter.format(LocalMessages.IRON_ACTIVITY_MAIL_CONTENT, new Object[] { TimeUtil.getMonth(), TimeUtil.getHour(), iron });
                        this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.IRON_ACTIVITY_MAIL_TITLE, mailMsg, 1, playerId, 0);
                    }
                    catch (Exception e) {
                        ActivityService.errorLog.error("class:ActivityService#method:initIronActivityEnd#exception:", e);
                    }
                }
                this.playerIronDao.clearAll();
                ActivityService.ironMap.clear();
            }
        }
        finally {
            ActivityService.lock.unlock();
        }
        ActivityService.lock.unlock();
    }
    
    private int getIron(final int reward, final int received) {
        int iron = 0;
        for (int i = received + 1; i <= reward; ++i) {
            iron += ActivityService.ironMap.get(i).right;
        }
        return iron;
    }
    
    @Override
    public void initLvExpActivityEnd(final String param) {
        final Activity activity = this.activityDao.read(3);
        final Long time = System.currentTimeMillis();
        if (time >= activity.getEndTime().getTime() && ActivityService.isLvExpDoing) {
            ActivityService.isLvExpDoing = false;
            this.playerLvExpDao.endActivity();
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("lvExpReward", true);
            doc.endObject();
            final Collection<PlayerDto> playerDtos = Players.getAllPlayer();
            for (final PlayerDto playerDto : playerDtos) {
                if (playerDto.playerLv > ActivityService.lvExpJoinActivityLv) {
                    Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, doc.toByte());
                }
            }
        }
    }
    
    @Override
    public byte[] initActivity(final int type, final long startTime, final long endTime, final String paramsInfo, final boolean isReboot) {
        Activity activity = this.activityDao.read(type);
        final Long curTime = System.currentTimeMillis();
        if (!isReboot && curTime + 60000L > startTime) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_START_TIME_SET);
        }
        if (activity != null) {
            if (!isReboot) {
                if (activity.getStartTime().getTime() <= curTime && activity.getEndTime().getTime() >= curTime) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_DOING_NOT_RESET);
                }
                this.activityDao.updateInfo(activity.getVId(), new Date(startTime), new Date(endTime), "");
            }
        }
        else {
            activity = new Activity();
            activity.setEndTime(new Date(endTime));
            activity.setStartTime(new Date(startTime));
            activity.setVId(type);
            activity.setName(LocalMessages.ACTIVITY_NAME_1);
            this.activityDao.create(activity);
        }
        ActivityService.setActivityLv.clear();
        ActivityService.acitvityDtoList.clear();
        Date startDate = new Date(startTime);
        int days = 0;
        do {
            final Calendar calendar = Calendar.getInstance();
            calendar.setTime(startDate);
            calendar.set(11, 23);
            calendar.set(12, 59);
            calendar.set(13, 0);
            calendar.set(14, 0);
            final ActivityDto ad = new ActivityDto();
            ad.setStartTime(startDate.getTime());
            ad.setEndTime(calendar.getTimeInMillis());
            ActivityService.acitvityDtoList.add(ad);
            ++days;
            calendar.add(11, 1);
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            startDate = calendar.getTime();
            if (startDate.getTime() + 60000L >= endTime) {
                break;
            }
        } while (days <= 30);
        ActivityService.setActivityLv.add(55);
        ActivityService.setActivityLv.add(70);
        ActivityService.setActivityLv.add(80);
        ActivityService.clearEndDate = endTime + 259200000L;
        this.jobService.addJob("activityService", "clearBatExpActivity", "endTime", endTime + 2000L, false);
        this.jobService.addJob("activityService", "sendBatExpActivity", "startTime", startTime + 1000L, false);
        this.sendBatExpActivity();
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
    }
    
    @Override
    public boolean inDragonBoatFestival() {
        if (!ActivityService.init) {
            synchronized (this) {
                if (!ActivityService.init) {
                    final Activity activity = this.activityDao.read(4);
                    if (activity == null) {
                        ActivityService.inDragon = false;
                    }
                    else {
                        final Date now = new Date();
                        ActivityService.inDragon = (activity.getStartTime().before(now) && activity.getEndTime().after(now));
                    }
                    ActivityService.init = true;
                }
            }
        }
        return ActivityService.inDragon;
    }
    
    @Override
    public boolean inIronActivity() {
        if (!ActivityService.initIron) {
            synchronized (this) {
                if (!ActivityService.initIron) {
                    final Activity activity = this.activityDao.read(5);
                    if (activity == null) {
                        ActivityService.inIron = false;
                    }
                    else {
                        final Date now = new Date();
                        ActivityService.inIron = (activity.getStartTime().before(now) && activity.getEndTime().after(now));
                        if (ActivityService.inIron) {
                            final String[] rules = activity.getParamsInfo().trim().split(";");
                            int index = 1;
                            String[] array;
                            for (int length = (array = rules).length, i = 0; i < length; ++i) {
                                final String str = array[i];
                                final String[] temp = str.split(",");
                                ActivityService.ironMap.put(index++, new Tuple(Integer.parseInt(temp[0]), Integer.parseInt(temp[1])));
                            }
                        }
                    }
                    ActivityService.initIron = true;
                }
            }
        }
        return ActivityService.inIron;
    }
    
    @Override
    public boolean inTicketActivity() {
        if (!PayService.initTicket) {
            synchronized (this) {
                if (!PayService.initTicket) {
                    final Activity activity = this.activityDao.read(7);
                    if (activity == null) {
                        PayService.inTicket = false;
                    }
                    else {
                        final Date now = new Date();
                        PayService.inTicket = (activity.getStartTime().before(now) && activity.getEndTime().after(now));
                        if (PayService.inTicket) {
                            final String[] rules = activity.getParamsInfo().trim().split(";");
                            int index = 1;
                            String[] array;
                            for (int length = (array = rules).length, i = 0; i < length; ++i) {
                                final String str = array[i];
                                final String[] temp = str.split(",");
                                PayService.ticketMap.put(index++, new Tuple(Integer.parseInt(temp[0]), Integer.parseInt(temp[1])));
                            }
                        }
                    }
                    PayService.initTicket = true;
                }
            }
        }
        return PayService.inTicket;
    }
    
    @Override
    public boolean inDstqActivity() {
        if (!ActivityService.initDstq) {
            synchronized (this) {
                if (!ActivityService.initDstq) {
                    final Activity activity = this.activityDao.read(8);
                    if (activity == null) {
                        ActivityService.inDstq = false;
                    }
                    else {
                        final Date now = new Date();
                        ActivityService.inDstq = (activity.getStartTime().before(now) && activity.getEndTime().after(now));
                        if (ActivityService.inDstq) {
                            final String[] rules = activity.getParamsInfo().trim().split(";");
                            int index = 1;
                            String[] array;
                            for (int length = (array = rules).length, i = 0; i < length; ++i) {
                                final String str = array[i];
                                final String[] temp = str.split(",");
                                ActivityService.dstqMap.put(index++, new ThreeTuple<Integer, Integer, Integer>(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]), Integer.parseInt(temp[2])));
                            }
                        }
                    }
                    ActivityService.initDstq = true;
                }
            }
        }
        return ActivityService.inDstq;
    }
    
    @Override
    public void openPlayerScoreRank(final int playerId) {
        PlayerScoreRank psr = this.playerScoreRankDao.read(playerId);
        if (psr == null) {
            psr = new PlayerScoreRank();
            psr.setPlayerId(playerId);
            psr.setOccupyNum(0);
            psr.setOccupy(0);
            psr.setAssistNum(0);
            psr.setAssist(0);
            psr.setCheerNum(0);
            psr.setCheer(0);
            psr.setScore(0);
            psr.setScore2(0);
            psr.setLastRank(0);
            psr.setReceived(0);
            this.playerScoreRankDao.create(psr);
        }
    }
    
    @Override
    public void openDragonRecord(final int playerId) {
        PlayerDragon pd = this.playerDragonDao.read(playerId);
        if (pd == null) {
            pd = new PlayerDragon();
            pd.setPlayerId(playerId);
            pd.setDragonNum(0);
            pd.setBoxNum(0);
            pd.setFeatBoxNum(0);
            this.playerDragonDao.create(pd);
        }
    }
    
    @Override
    public byte[] initQuenchingActivity(final int type, final long startTime, final long endTime) {
        Activity activity = this.activityDao.read(type);
        final Long curTime = System.currentTimeMillis();
        if (curTime + 60000L > startTime) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_START_TIME_SET);
        }
        if (activity != null) {
            if (activity.getStartTime().getTime() <= curTime && activity.getEndTime().getTime() >= curTime) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_DOING_NOT_RESET);
            }
            this.activityDao.updateInfo(activity.getVId(), new Date(startTime), new Date(endTime), "");
        }
        else {
            activity = new Activity();
            activity.setEndTime(new Date(endTime));
            activity.setStartTime(new Date(startTime));
            activity.setVId(type);
            activity.setName(LocalMessages.ACTIVITY_NAME_6);
            activity.setParamsInfo("");
            this.activityDao.create(activity);
        }
        if (startTime > System.currentTimeMillis()) {
            this.jobService.addJob("activityService", "initQuenchingActivityStart", "", startTime, false);
        }
        if (endTime > System.currentTimeMillis()) {
            this.jobService.addJob("activityService", "initQuenchingActivityEnd", "", endTime, false);
        }
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    @Override
    public void initQuenchingActivityStart(final String params) {
        final Activity activity = this.activityDao.read(6);
        if (activity == null) {
            return;
        }
        final Long time = System.currentTimeMillis();
        if (time >= activity.getStartTime().getTime() && time <= activity.getEndTime().getTime()) {
            ActivityService.inQuenching = true;
            final byte[] send = JsonBuilder.getSimpleJson("haveQuenchingActivity", 1);
            final Collection<PlayerDto> playerDtos = Players.getAllPlayer();
            for (final PlayerDto playerDto : playerDtos) {
                if (playerDto.cs[51] == '1') {
                    Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, send);
                }
            }
        }
    }
    
    @Override
    public void initQuenchingActivityEnd(final String params) {
        final Activity activity = this.activityDao.read(6);
        final Long time = System.currentTimeMillis();
        if (activity == null || time >= activity.getEndTime().getTime()) {
            ActivityService.inQuenching = false;
            final byte[] send = JsonBuilder.getSimpleJson("haveQuenchingActivity", 0);
            final Collection<PlayerDto> playerDtos = Players.getAllPlayer();
            for (final PlayerDto playerDto : playerDtos) {
                if (playerDto.cs[51] == '1') {
                    Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, send);
                }
            }
            if (activity != null) {
                this.activityDao.deleteById(activity.getVId());
            }
        }
    }
    
    @Override
    public byte[] getQuenching(final PlayerDto dto) {
        if (!ActivityService.inQuenching) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (dto.cs[51] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Activity activity = this.activityDao.read(6);
        if (activity == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Date date = new Date();
        final Date startTime = activity.getStartTime();
        final Date endDate = activity.getEndTime();
        if (startTime.after(date) || endDate.before(date)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("leftTime", endDate.getTime() - date.getTime());
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void clearIronActivity() {
        final Activity activity = this.activityDao.read(5);
        final long start = System.currentTimeMillis();
        try {
            ActivityService.lock.lock();
            if (ActivityService.inIron && activity != null && start < activity.getEndTime().getTime()) {
                final List<PlayerIron> piList = this.playerIronDao.getReceivedList();
                for (final PlayerIron pi : piList) {
                    try {
                        final int iron = this.getIron(pi.getReward(), pi.getReceived());
                        final int playerId = pi.getPlayerId();
                        this.playerResourceDao.addIronIgnoreMax(playerId, iron, "\u9554\u94c1\u5927\u56de\u9988\u6d3b\u52a8\u83b7\u53d6\u9554\u94c1", false);
                        final String mailMsg = MessageFormatter.format(LocalMessages.IRON_ACTIVITY_MAIL_CONTENT, new Object[] { TimeUtil.getBeforeMonth(1), TimeUtil.getBeforeDay(1), iron });
                        this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.IRON_ACTIVITY_MAIL_TITLE, mailMsg, 1, playerId, 0);
                    }
                    catch (Exception e) {
                        ActivityService.errorLog.error("class:ActivityService#method:clearIronActivity#exception:", e);
                    }
                }
                this.playerIronDao.clearAll();
            }
        }
        finally {
            ActivityService.lock.unlock();
        }
        ActivityService.lock.unlock();
        ActivityService.timerLog.info(LogUtil.formatThreadLog("ActivityService", "clearIronActivity", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Override
    public void updateIron(final String param) {
        try {
            if (!ActivityService.inIron) {
                return;
            }
            if (StringUtils.isBlank(param)) {
                ActivityService.errorLog.error("class:ActivityService#method:updateIron#param:" + param);
                return;
            }
            final String[] temp = param.trim().split("#");
            if (temp.length != 2) {
                ActivityService.errorLog.error("class:ActivityService#method:updateIron#param:" + param);
                return;
            }
            final int playerId = Integer.parseInt(temp[0]);
            final int iron = Integer.parseInt(temp[1]);
            this.playerIronDao.addIron(playerId, iron);
            PlayerIron pi = this.playerIronDao.read(playerId);
            if (pi == null) {
                this.openPlayerIron(playerId);
                pi = this.playerIronDao.read(playerId);
            }
            final int after = pi.getIron();
            final int reward = pi.getReward();
            final int newReward = this.getReward(after);
            if (newReward > reward) {
                this.playerIronDao.updateReward(playerId, newReward);
            }
        }
        catch (Exception e) {
            ActivityService.errorLog.error("class:ActivityService#method:updateIron#param:" + param + "#exception:", e);
        }
    }
    
    @Override
    public void addDstqGold(final int playerId, final int gold) {
        try {
            if (!ActivityService.inDstq) {
                return;
            }
            final int before = this.bonusActivityDao.getConsumeGold(playerId);
            this.bonusActivityDao.addConsumeGold(playerId, gold);
            final int after = this.bonusActivityDao.getConsumeGold(playerId);
            final Tuple<Integer, Integer> beforeTuple = this.getDstqReward(before);
            final Tuple<Integer, Integer> afterTuple = this.getDstqReward(after);
            final int beforeCount = beforeTuple.left + beforeTuple.right;
            final int afterCount = afterTuple.left + afterTuple.right;
            final int diff_L = afterTuple.left - beforeTuple.left;
            final int diff_R = afterTuple.right - beforeTuple.right;
            if (afterCount > beforeCount) {
                if (diff_L > 0) {
                    this.storeHouseService.gainSearchItems(106, diff_L, PlayerDtoUtil.getPlayerDto(this.playerDao.read(playerId), this.playerAttributeDao.read(playerId)), "\u4e39\u4e66\u94c1\u5238\u6d3b\u52a8\u83b7\u5f97\u4e39\u4e66\u94c1\u5238");
                }
                if (diff_R > 0) {
                    this.storeHouseService.gainSearchItems(107, diff_R, PlayerDtoUtil.getPlayerDto(this.playerDao.read(playerId), this.playerAttributeDao.read(playerId)), "\u4e39\u4e66\u94c1\u5238\u6d3b\u52a8\u83b7\u5f97\u4e39\u4e66\u94c1\u5238");
                }
                String mailMsg = "";
                if (diff_L > 0 && diff_R > 0) {
                    mailMsg = MessageFormatter.format(LocalMessages.DSTQ_ACTIVITY_MAIL_CONTENT_ALL, new Object[] { after, diff_L, diff_R });
                }
                else if (diff_L > 0) {
                    mailMsg = MessageFormatter.format(LocalMessages.DSTQ_ACTIVITY_MAIL_CONTENT_L, new Object[] { after, diff_L });
                }
                else {
                    mailMsg = MessageFormatter.format(LocalMessages.DSTQ_ACTIVITY_MAIL_CONTENT_R, new Object[] { after, diff_R });
                }
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.DSTQ_ACTIVITY_MAIL_TITLE, mailMsg, 1, playerId, 0);
            }
        }
        catch (Exception e) {
            ActivityService.errorLog.error("class:ActivityService#method:addDstqGold#playerId:" + playerId + "#gold:" + gold + "#exception:", e);
        }
    }
    
    private int getReward(final int iron) {
        int reward = 0;
        for (int size = ActivityService.ironMap.size(), i = 1; i <= size; ++i) {
            final Tuple<Integer, Integer> tuple = ActivityService.ironMap.get(i);
            if (iron < tuple.left) {
                break;
            }
            ++reward;
        }
        return reward;
    }
    
    private Tuple<Integer, Integer> getDstqReward(final int gold) {
        final Tuple<Integer, Integer> result = new Tuple(0, 0);
        for (int size = ActivityService.dstqMap.size(), i = 1; i <= size; ++i) {
            final ThreeTuple<Integer, Integer, Integer> tuple = ActivityService.dstqMap.get(i);
            if (gold < tuple.left) {
                break;
            }
            if (1 == tuple.right) {
                final Tuple<Integer, Integer> tuple2 = result;
                tuple2.left = tuple2.left + tuple.middle;
            }
            else if (2 == tuple.right) {
                final Tuple<Integer, Integer> tuple3 = result;
                tuple3.right = tuple3.right + tuple.middle;
            }
        }
        return result;
    }
    
    @Override
    public byte[] getIronInfo(final PlayerDto playerDto) {
        if (!ActivityService.inIron) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.IRON_ACTIVITY_NO_ACTIVITY);
        }
        final int playerId = playerDto.playerId;
        final Activity activity = this.activityDao.read(5);
        PlayerIron pi = this.playerIronDao.read(playerId);
        if (pi == null) {
            this.openPlayerIron(playerId);
            pi = this.playerIronDao.read(playerId);
        }
        final int todayIron = pi.getIron();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("lvs");
        int lv = 0;
        int count = 0;
        int needIron = 0;
        boolean flag_needIron = false;
        final int size = ActivityService.ironMap.size();
        int remainIron = 0;
        final int reward = pi.getReward();
        final int received = pi.getReceived();
        int rewardValues = 0;
        int maxIron = 0;
        for (int i = 1; i <= size; ++i) {
            final Tuple<Integer, Integer> tuple = ActivityService.ironMap.get(i);
            final int iron = tuple.left;
            final int rewardIron = tuple.right;
            doc.startObject();
            doc.createElement("iron", iron);
            doc.createElement("rewardIron", rewardIron);
            doc.endObject();
            ++count;
            if (todayIron >= iron) {
                lv = count;
            }
            if (!flag_needIron && todayIron < iron) {
                needIron = iron - todayIron;
                flag_needIron = true;
            }
            if (received < i) {
                remainIron += rewardIron;
            }
            if (received + 1 == i && reward > received) {
                rewardValues = rewardIron;
            }
            if (i == size) {
                maxIron = iron;
            }
        }
        doc.endArray();
        doc.createElement("cd", TimeUtil.now2specMs(activity.getEndTime().getTime()));
        doc.createElement("todayIron", todayIron);
        doc.createElement("remainIron", remainIron);
        doc.createElement("rewardTimes", reward - received);
        doc.createElement("rewardValues", rewardValues);
        doc.createElement("lv", lv + 1);
        doc.createElement("needIron", needIron);
        doc.createElement("maxIron", maxIron);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] useIron(final PlayerDto playerDto) {
        if (!ActivityService.inIron) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final int playerId = playerDto.playerId;
        PlayerIron pi = this.playerIronDao.read(playerId);
        if (pi == null) {
            this.openPlayerIron(playerId);
            pi = this.playerIronDao.read(playerId);
        }
        final int reward = pi.getReward();
        final int received = pi.getReceived();
        if (received >= reward) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.IRON_ACTIVITY_NO_REWARD);
        }
        this.playerIronDao.useIron(playerId);
        final int iron = ActivityService.ironMap.get(received + 1).right;
        this.playerResourceDao.addIronIgnoreMax(playerId, iron, "\u9554\u94c1\u5927\u56de\u9988\u6d3b\u52a8\u83b7\u53d6\u9554\u94c1", false);
        if (pi.getReceived() + 1 >= ActivityService.ironMap.size()) {
            Players.push(playerId, PushCommand.PUSH_UPDATE, JsonBuilder.getSimpleJson("haveIronActivity", 0));
        }
        return JsonBuilder.getJson(State.SUCCESS, JsonBuilder.getSimpleJson("iron", iron));
    }
    
    private void openPlayerIron(final int playerId) {
        PlayerIron pi = this.playerIronDao.read(playerId);
        if (pi == null) {
            pi = new PlayerIron();
            pi.setPlayerId(playerId);
            pi.setIron(0);
            pi.setReward(0);
            pi.setReceived(0);
        }
        this.playerIronDao.create(pi);
    }
    
    @Override
    public void innerActivity5(final String param) {
        try {
            ActivityService.timerLog.error("class:ActivityService#method:innerActivity5#begin...");
            final List<PlayerOfficeRelative> list = this.playerOfficeRelativeDao.getListOtherOfficerId(35, 36, 37);
            final Set<Integer> set0 = this.hallsCache.getPris(1);
            final Set<Integer> set2 = this.hallsCache.getPris(2);
            final Set<Integer> set3 = this.hallsCache.getPris(3);
            final Set<Integer> set4 = this.hallsCache.getPris(4);
            final Set<Integer> set5 = this.hallsCache.getPris(5);
            final List<Integer> list2 = new ArrayList<Integer>();
            final List<Integer> list3 = new ArrayList<Integer>();
            final List<Integer> list4 = new ArrayList<Integer>();
            final List<Integer> list5 = new ArrayList<Integer>();
            final List<Integer> list6 = new ArrayList<Integer>();
            for (final PlayerOfficeRelative pa : list) {
                if (set0.contains(pa.getOfficerId())) {
                    list2.add(pa.getPlayerId());
                }
                else if (set2.contains(pa.getOfficerId())) {
                    list3.add(pa.getPlayerId());
                }
                else if (set3.contains(pa.getOfficerId())) {
                    list4.add(pa.getPlayerId());
                }
                else if (set4.contains(pa.getOfficerId())) {
                    list5.add(pa.getPlayerId());
                }
                else {
                    if (!set5.contains(pa.getOfficerId())) {
                        continue;
                    }
                    list6.add(pa.getPlayerId());
                }
            }
            for (final int playerId : list2) {
                try {
                    this.playerAttributeDao.addRecruitToken(playerId, 50, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u5f97\u52df\u5175\u4ee4");
                    this.playerAttributeDao.addFreeConstructionNum(playerId, 15, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u5f97\u9ec4\u91d1\u5efa\u7b51\u961f");
                    this.playerResourceDao.addCopperIgnoreMax(playerId, 150000.0, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u5f97\u94f6\u5e01", true);
                    this.playerResourceDao.addWoodIgnoreMax(playerId, 150000.0, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u5f97\u6728\u6750", true);
                    this.playerResourceDao.addFoodIgnoreMax(playerId, 150000.0, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u53d6\u7cae\u98df");
                    this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.INNER_ACTIVITY_5_TITLE, MessageFormatter.format(LocalMessages.INNER_ACTIVITY_5_CONTENT_0, new Object[] { 50, 15, 150000 }), 1, playerId, new Date());
                }
                catch (Exception e) {
                    ActivityService.timerLog.error("class:ActivityService#method:innerActivity5#exception0:", e);
                }
            }
            for (final int playerId : list3) {
                try {
                    this.playerAttributeDao.addRecruitToken(playerId, 50, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u5f97\u52df\u5175\u4ee4");
                    this.playerResourceDao.addCopperIgnoreMax(playerId, 100000.0, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u5f97\u94f6\u5e01", true);
                    this.playerResourceDao.addWoodIgnoreMax(playerId, 100000.0, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u5f97\u6728\u6750", true);
                    this.playerResourceDao.addFoodIgnoreMax(playerId, 100000.0, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u53d6\u7cae\u98df");
                    this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.INNER_ACTIVITY_5_TITLE, MessageFormatter.format(LocalMessages.INNER_ACTIVITY_5_CONTENT_1, new Object[] { 50, 100000 }), 1, playerId, new Date());
                }
                catch (Exception e) {
                    ActivityService.timerLog.error("class:ActivityService#method:innerActivity5#exception1:", e);
                }
            }
            for (final int playerId : list4) {
                try {
                    this.playerAttributeDao.addRecruitToken(playerId, 50, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u5f97\u52df\u5175\u4ee4");
                    this.playerResourceDao.addCopperIgnoreMax(playerId, 100000.0, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u5f97\u94f6\u5e01", true);
                    this.playerResourceDao.addWoodIgnoreMax(playerId, 100000.0, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u5f97\u6728\u6750", true);
                    this.playerResourceDao.addFoodIgnoreMax(playerId, 100000.0, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u53d6\u7cae\u98df");
                    this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.INNER_ACTIVITY_5_TITLE, MessageFormatter.format(LocalMessages.INNER_ACTIVITY_5_CONTENT_2, new Object[] { 50, 100000 }), 1, playerId, new Date());
                }
                catch (Exception e) {
                    ActivityService.timerLog.error("class:ActivityService#method:innerActivity5#exception2:", e);
                }
            }
            for (final int playerId : list5) {
                try {
                    this.playerAttributeDao.addRecruitToken(playerId, 25, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u5f97\u52df\u5175\u4ee4");
                    this.playerResourceDao.addCopperIgnoreMax(playerId, 50000.0, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u5f97\u94f6\u5e01", true);
                    this.playerResourceDao.addWoodIgnoreMax(playerId, 50000.0, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u5f97\u6728\u6750", true);
                    this.playerResourceDao.addFoodIgnoreMax(playerId, 50000.0, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u53d6\u7cae\u98df");
                    this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.INNER_ACTIVITY_5_TITLE, MessageFormatter.format(LocalMessages.INNER_ACTIVITY_5_CONTENT_3, new Object[] { 25, 50000 }), 1, playerId, new Date());
                }
                catch (Exception e) {
                    ActivityService.timerLog.error("class:ActivityService#method:innerActivity5#exception3:", e);
                }
            }
            for (final int playerId : list6) {
                try {
                    this.playerAttributeDao.addRecruitToken(playerId, 25, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u5f97\u52df\u5175\u4ee4");
                    this.playerResourceDao.addCopperIgnoreMax(playerId, 50000.0, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u5f97\u94f6\u5e01", true);
                    this.playerResourceDao.addWoodIgnoreMax(playerId, 50000.0, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u5f97\u6728\u6750", true);
                    this.playerResourceDao.addFoodIgnoreMax(playerId, 50000.0, "\u4e89\u5b98\u7235\u6388\u597d\u793c\u5185\u7f6e\u6d3b\u52a8\u83b7\u53d6\u7cae\u98df");
                    this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.INNER_ACTIVITY_5_TITLE, MessageFormatter.format(LocalMessages.INNER_ACTIVITY_5_CONTENT_4, new Object[] { 25, 50000 }), 1, playerId, new Date());
                }
                catch (Exception e) {
                    ActivityService.timerLog.error("class:ActivityService#method:innerActivity5#exception4:", e);
                }
            }
            ActivityService.timerLog.error("class:ActivityService#method:innerActivity5#end...");
        }
        catch (Exception e2) {
            ActivityService.timerLog.error("class:ActivityService#method:innerActivity5#exception:", e2);
        }
    }
    
    @Override
    public void initInnerActivity() {
        final String serverTime = Configuration.getProperty("gcld.server.time");
        final long start = Long.parseLong(serverTime);
        final long now = System.currentTimeMillis();
        if (start + 4 * Constants.ONE_DAY_MS >= now) {
            this.jobService.addJob("activityService", "innerActivity5", "", start + 4 * Constants.ONE_DAY_MS, false);
        }
    }
    
    private Tuple<Boolean, String> checkDstqActivity(final long startTime, final long endTime, final String paramsInfo) {
        final Tuple<Boolean, String> result = new Tuple(false, "");
        final long curTime = System.currentTimeMillis();
        if (startTime <= 0L || endTime <= 0L || StringUtils.isBlank(paramsInfo)) {
            result.right = LocalMessages.DSTQ_ACTIVITY_NULL_VALUE;
            return result;
        }
        if (startTime >= endTime || startTime <= curTime || endTime <= curTime) {
            result.right = LocalMessages.DSTQ_ACTIVITY_INVALID_TIME;
            return result;
        }
        int maxNGold = 0;
        int maxRDSTQ = 0;
        int maxDstqType = 0;
        final String[] rules = paramsInfo.trim().split(";");
        String[] array;
        for (int length = (array = rules).length, i = 0; i < length; ++i) {
            final String str = array[i];
            if (str.split(",").length != 3) {
                result.right = LocalMessages.DSTQ_ACTIVITY_INVALID_RULES;
                return result;
            }
            try {
                final int needGold = Integer.parseInt(str.split(",")[0]);
                final int rewardDstq = Integer.parseInt(str.split(",")[1]);
                final int dstqType = Integer.parseInt(str.split(",")[2]);
                if (rewardDstq * 1.0 / needGold > 0.3) {
                    result.right = LocalMessages.DSTQ_ACTIVITY_PROTECT;
                    return result;
                }
                if (needGold <= maxNGold || rewardDstq < maxRDSTQ || (1 != dstqType && 2 != dstqType) || dstqType < maxDstqType) {
                    result.right = LocalMessages.DSTQ_ACTIVITY_INVALID_RULES;
                    return result;
                }
                maxNGold = needGold;
                maxRDSTQ = rewardDstq;
                maxDstqType = dstqType;
            }
            catch (NumberFormatException e) {
                result.right = LocalMessages.DSTQ_ACTIVITY_NUMBER_FORMAT_EXCEPTION;
                return result;
            }
        }
        result.left = true;
        return result;
    }
    
    @Override
    public byte[] getDstqInfo(final PlayerDto playerDto) {
        if (!ActivityService.inDstq) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.DSTQ_ACTIVITY_NO_ACTIVITY);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("lvs");
        int totalTicket_1 = 0;
        int totalTicket_2 = 0;
        int haveTicket_1 = 0;
        int haveTicket_2 = 0;
        int lv = 0;
        int count = 0;
        final int playerId = playerDto.playerId;
        final int gold = this.bonusActivityDao.getConsumeGold(playerId);
        int needGold = 0;
        boolean flag_needGold = false;
        for (int size = ActivityService.dstqMap.size(), i = 1; i <= size; ++i) {
            final ThreeTuple<Integer, Integer, Integer> tuple = ActivityService.dstqMap.get(i);
            final int nGold = tuple.left;
            final int rDstq = tuple.middle;
            final int type = tuple.right;
            doc.startObject();
            doc.createElement("gold", nGold);
            doc.createElement("rewardDstq", rDstq);
            doc.createElement("type", type);
            doc.endObject();
            ++count;
            if (gold >= nGold) {
                lv = count;
                if (1 == type) {
                    haveTicket_1 += rDstq;
                }
                else if (2 == type) {
                    haveTicket_2 += rDstq;
                }
            }
            if (!flag_needGold && gold < nGold) {
                needGold = nGold - gold;
                flag_needGold = true;
            }
            if (1 == type) {
                totalTicket_1 += rDstq;
            }
            else if (2 == type) {
                totalTicket_2 += rDstq;
            }
        }
        doc.endArray();
        final Activity activity = this.activityDao.read(8);
        doc.createElement("day", TimeUtil.now2specMs(activity.getEndTime().getTime()));
        doc.createElement("remainingDstq1", totalTicket_1 - haveTicket_1);
        doc.createElement("remainingDstq2", totalTicket_2 - haveTicket_2);
        doc.createElement("gold", gold);
        doc.createElement("lv", lv + 1);
        doc.createElement("needGold", needGold);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] get360PrivilegeInfo(final PlayerDto playerDto) {
        final Date now = new Date();
        final Tuple<Date, Date> res = this.get360PrivilegeTime();
        if (now.before(res.left) || now.after(res.right)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PRIVILEGE_360_NOT_IN_PROGRESS);
        }
        if (!playerDto.yx.equals("360")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PRIVILEGE_360_INVALID_YX);
        }
        final Privilege360 privilege360 = this.privilege360Dao.read(playerDto.playerId);
        final String status = privilege360.getStatus();
        final int size = this.gift360Cache.getCacheMap().size();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("gUrl", PluginContext.configuration.get360PrivilegePageUrl("360"));
        doc.startArray("privilegeList");
        for (int i = 1; i <= size; ++i) {
            boolean canRecvReward = true;
            final Gift360 gift360 = (Gift360)this.gift360Cache.get((Object)i);
            doc.startObject();
            doc.createElement("privilegeName", gift360.getGiftname());
            if (status.charAt(i - 1) == '1') {
                canRecvReward = false;
            }
            doc.createElement("canRecvReward", canRecvReward);
            final int cooper = gift360.getCopper();
            final int food = gift360.getFood();
            final int wood = gift360.getLumber();
            final int iron = gift360.getIron();
            final int recruitToken = gift360.getRecruitToken();
            final int freeCons = gift360.getFreeCons();
            final int equipId = gift360.getEquip();
            final String title = gift360.getName();
            if (cooper > 0) {
                doc.createElement("cooper", cooper);
            }
            if (food > 0) {
                doc.createElement("food", food);
            }
            if (wood > 0) {
                doc.createElement("wood", wood);
            }
            if (iron > 0) {
                doc.createElement("iron", iron);
            }
            if (recruitToken > 0) {
                doc.createElement("recruitToken", recruitToken);
            }
            if (freeCons > 0) {
                doc.createElement("freeCons", freeCons);
            }
            if (equipId > 0) {
                final Equip item = (Equip)this.equipCache.get((Object)equipId);
                doc.createElement("equipName", item.getName());
                doc.createElement("equipNum", 1);
            }
            if (!StringUtils.isBlank(title)) {
                doc.createElement("title", title);
            }
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] recv360Privilege(final PlayerDto playerDto, final int level) {
        final Date now = new Date();
        final Tuple<Date, Date> res = this.get360PrivilegeTime();
        if (now.before(res.left) || now.after(res.right)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PRIVILEGE_360_NOT_IN_PROGRESS);
        }
        if (!playerDto.yx.equals("360")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PRIVILEGE_360_INVALID_YX);
        }
        if (level < 1 || level > 5) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PRIVILEGE_360_INVALID_PRIVILEGE);
        }
        final Privilege360 privilege360 = this.privilege360Dao.read(playerDto.playerId);
        if (privilege360 == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PRIVILEGE_360_IS_NULL);
        }
        final String status = privilege360.getStatus();
        if (status.charAt(level - 1) == '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PRIVILEGE_360_IS_RECEIVED);
        }
        int level2 = 0;
        if (level == 1) {
            level2 = 5;
        }
        else if (level == 2) {
            level2 = 6;
        }
        else if (level == 3) {
            level2 = 7;
        }
        else if (level == 4) {
            level2 = 8;
        }
        else {
            level2 = 15;
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final String echo = this.get360PrivilegeReturnCode(playerDto, level2);
        if (echo.equals("1")) {
            final Gift360 gift360 = (Gift360)this.gift360Cache.get((Object)level);
            final int cooper = gift360.getCopper();
            final int food = gift360.getFood();
            final int wood = gift360.getLumber();
            final int iron = gift360.getIron();
            final int recruitToken = gift360.getRecruitToken();
            final int freeCons = gift360.getFreeCons();
            final int equipId = gift360.getEquip();
            final String title = gift360.getName();
            if (equipId > 0) {
                final Equip item = (Equip)this.equipCache.get((Object)equipId);
                final int usedStoreNum = this.storeHouseDao.getCountByPlayerId(playerDto.playerId);
                final PlayerAttribute pa = this.playerAttributeDao.read(playerDto.playerId);
                final int maxStoreNum = pa.getMaxStoreNum();
                if (usedStoreNum >= maxStoreNum) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.NO_ENOUGH_STORE_NUM);
                }
                final StringBuffer sb = new StringBuffer();
                final Random random = new Random();
                final int skill = Math.abs(random.nextInt()) % 7 + 1;
                for (int i = 0; i < 4; ++i) {
                    sb.append(skill);
                    sb.append(":5;");
                }
                sb.deleteCharAt(sb.length() - 1);
                final StoreHouse sh = new StoreHouse();
                sh.setItemId(item.getId());
                sh.setPlayerId(playerDto.playerId);
                sh.setLv(item.getLevel());
                sh.setOwner(0);
                sh.setType(1);
                sh.setGoodsType(item.getType());
                sh.setAttribute(new StringBuilder().append(item.getAttribute()).toString());
                sh.setQuality(item.getQuality());
                sh.setGemId(0);
                sh.setNum(1);
                sh.setState(0);
                sh.setRefreshAttribute(sb.toString());
                sh.setSpecialSkillId(skill);
                sh.setQuenchingTimes(0);
                sh.setBindExpireTime(0L);
                sh.setMarkId(0);
                this.storeHouseDao.create(sh);
                doc.createElement("equipName", item.getName());
                doc.createElement("equipNum", 1);
            }
            if (cooper > 0) {
                this.playerResourceDao.addCopperIgnoreMax(playerDto.playerId, cooper, "360\u7279\u6743\u536b\u58eb\u83b7\u5f97\u94f6\u5e01", true);
                doc.createElement("cooper", cooper);
            }
            if (food > 0) {
                this.playerResourceDao.addFoodIgnoreMax(playerDto.playerId, food, "360\u7279\u6743\u536b\u58eb\u83b7\u5f97\u7cae\u98df");
                doc.createElement("food", food);
            }
            if (wood > 0) {
                this.playerResourceDao.addWoodIgnoreMax(playerDto.playerId, wood, "360\u7279\u6743\u536b\u58eb\u83b7\u5f97\u6728\u6750", true);
                doc.createElement("wood", wood);
            }
            if (iron > 0) {
                this.playerResourceDao.addIronIgnoreMax(playerDto.playerId, iron, "360\u7279\u6743\u536b\u58eb\u83b7\u5f97\u9554\u94c1", true);
                doc.createElement("iron", iron);
            }
            if (recruitToken > 0) {
                this.playerAttributeDao.addRecruitToken(playerDto.playerId, recruitToken, "360\u7279\u6743\u536b\u58eb\u83b7\u5f97\u52df\u5175\u4ee4");
                doc.createElement("recruitToken", recruitToken);
            }
            if (freeCons > 0) {
                this.playerAttributeDao.addFreeConstructionNum(playerDto.playerId, freeCons, "360\u7279\u6743\u536b\u58eb\u83b7\u5f97\u9ec4\u91d1\u9524");
                doc.createElement("freeCons", freeCons);
            }
            if (!StringUtils.isBlank(title)) {
                this.privilege360Dao.setTitleByPid(playerDto.playerId, LocalMessages.PRIVILEGE_360_TITLE);
                KfwdMatchService.privilege360TitleMap.put(playerDto.playerName, LocalMessages.PRIVILEGE_360_TITLE);
                final String content = MessageFormatter.format(LocalMessages.PRIVILEGE_360_CONTENT, new Object[] { LocalMessages.PRIVILEGE_360_TITLE });
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.PRIVILEGE_360_TITLE, content, 1, playerDto.playerId, new Date());
                doc.createElement("title", title);
            }
            doc.endObject();
            this.privilege360Dao.setStatusByPid(playerDto.playerId, level);
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        if (echo.equals("0")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PRIVILEGE_360_RETURN_CODE_0);
        }
        if (echo.equals("-1")) {
            doc.createElement("isPopUp", true);
            doc.createElement("url", PluginContext.configuration.get360PrivilegePageUrl("360"));
            doc.createElement("popUpWindowMsg", (Object)LocalMessages.PRIVILEGE_360_RETURN_CODE_NEGATIVE_1);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        if (echo.equals("-2")) {
            doc.createElement("isPopUp", true);
            doc.createElement("url", PluginContext.configuration.get360PrivilegePageUrl("360"));
            doc.createElement("popUpWindowMsg", (Object)LocalMessages.PRIVILEGE_360_RETURN_CODE_NEGATIVE_2);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        if (echo.equals("-3")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PRIVILEGE_360_RETURN_CODE_NEGATIVE_3);
        }
        if (echo.equals("-4")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PRIVILEGE_360_RETURN_CODE_NEGATIVE_4);
        }
        if (echo.equals("-5")) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.PRIVILEGE_360_RETURN_CODE_NEGATIVE_5);
        }
        return JsonBuilder.getJson(State.FAIL, LocalMessages.PRIVILEGE_360_RETURN_CODE_OTHERS);
    }
    
    private String get360PrivilegeReturnCode(final PlayerDto playerDto, final int level) {
        final long now = System.currentTimeMillis() / 1000L;
        final String aId = PluginContext.configuration.get360PrivilegeAid("360");
        final String gKey = PluginContext.configuration.get360PrivilegeGkey("360");
        final String type = PluginContext.configuration.get360PrivilegeType("360");
        final String checkUrl = PluginContext.configuration.get360PrivilegeCheckUrl("360");
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("aid", aId);
        paramMap.put("gkey", gKey);
        final String serverId = PluginContext.configuration.getServerId("360");
        paramMap.put("skey", "S" + serverId);
        paramMap.put("qid", playerDto.userId.toString());
        paramMap.put("type", type);
        paramMap.put("level", new StringBuilder(String.valueOf(level)).toString());
        paramMap.put("time", new StringBuilder(String.valueOf(now)).toString());
        final String sign = this.get360MD5Ticket(playerDto.userId, now);
        paramMap.put("sign", sign);
        String echo = "";
        try {
            echo = WebUtils.sendGetRequest(checkUrl, paramMap);
            echo = echo.trim();
        }
        catch (Exception e) {
            ActivityService.errorLog.error("#ClassName:activityService#MethodName:get360PrivilegeReturnCode#reason:", e);
        }
        return echo;
    }
    
    private String get360MD5Ticket(final String qid, final long time) {
        final String aid = PluginContext.configuration.get360PrivilegeAid("360");
        final String gkey = PluginContext.configuration.get360PrivilegeGkey("360");
        final String privKey = PluginContext.configuration.get360PrivilegePriviKey("360");
        final String type = PluginContext.configuration.get360PrivilegeType("360");
        final StringBuilder sb = new StringBuilder();
        sb.append(aid).append("|").append(gkey).append("|").append(qid).append("|").append(type).append("|").append(time).append("|").append(privKey);
        return CodecUtil.md5(sb.toString());
    }
    
    @Override
    public void handle360PrivilegeForLogin(final Player player, final JsonDocument doc) {
        try {
            if (!player.getYx().equals("360")) {
                return;
            }
            final Date now = new Date();
            final Tuple<Date, Date> res = this.get360PrivilegeTime();
            if (now.after(res.left) && now.before(res.right)) {
                Privilege360 privilege360 = this.privilege360Dao.read(player.getPlayerId());
                if (privilege360 == null) {
                    privilege360 = new Privilege360();
                    privilege360.setPlayerId(player.getPlayerId());
                    privilege360.setStatus("00000");
                    this.privilege360Dao.create(privilege360);
                }
                doc.createElement("has360PrivilegeIcon", true);
            }
        }
        catch (Exception e) {
            ActivityService.errorLog.error("#ActivityService#handle360PrivilegeForLogin#reason:", e);
        }
    }
    
    private Tuple<Date, Date> get360PrivilegeTime() {
        final Tuple<Date, Date> res = new Tuple();
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final String start = PluginContext.configuration.get360PrivilegeStartTime("360");
        final String end = PluginContext.configuration.get360PrivilegeEndTime("360");
        try {
            res.left = sdf.parse(start);
            res.right = sdf.parse(end);
        }
        catch (ParseException e) {
            ActivityService.errorLog.error("#ActivityService#get360PrivilegeTime#reason:", e);
        }
        return res;
    }
    
    @Override
    public int recv360PrivilegeForTest(final String userId, final int level) {
        int level2 = 0;
        if (level == 1) {
            level2 = 5;
        }
        else if (level == 2) {
            level2 = 6;
        }
        else if (level == 3) {
            level2 = 7;
        }
        else if (level == 4) {
            level2 = 8;
        }
        else {
            level2 = 15;
        }
        final String echo = this.get360PrivilegeReturnCodeForTest(userId, level2);
        if (echo.equals("1")) {
            return 200;
        }
        return -1;
    }
    
    private String get360PrivilegeReturnCodeForTest(final String uId, final int level) {
        final long now = System.currentTimeMillis() / 1000L;
        final String aId = PluginContext.configuration.get360PrivilegeAid("360");
        final String gKey = PluginContext.configuration.get360PrivilegeGkey("360");
        final String type = PluginContext.configuration.get360PrivilegeType("360");
        final String checkUrl = PluginContext.configuration.get360PrivilegeCheckUrl("360");
        final Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("aid", aId);
        paramMap.put("gkey", gKey);
        final String serverId = PluginContext.configuration.getServerId("360");
        paramMap.put("skey", "S" + serverId);
        paramMap.put("qid", uId);
        paramMap.put("type", type);
        paramMap.put("level", new StringBuilder(String.valueOf(level)).toString());
        paramMap.put("time", new StringBuilder(String.valueOf(now)).toString());
        final String sign = this.get360MD5Ticket(uId, now);
        paramMap.put("sign", sign);
        String echo = "";
        try {
            echo = WebUtils.sendGetRequest(checkUrl, paramMap);
            echo = echo.trim();
        }
        catch (Exception e) {
            ActivityService.errorLog.error("#ClassName:activityService#MethodName:get360PrivilegeReturnCode#reason:", e);
        }
        return echo;
    }
}
