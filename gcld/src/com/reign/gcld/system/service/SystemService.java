package com.reign.gcld.system.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.chat.dao.*;
import com.reign.gcld.user.dao.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.pay.dao.*;
import com.reign.gcld.store.dao.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.gift.dao.*;
import com.reign.gcld.mail.dao.*;
import com.reign.gcld.activity.dao.*;
import com.reign.gcld.job.service.*;
import com.reign.gcld.gm.service.*;
import com.reign.gcld.event.dao.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import org.apache.commons.lang.*;
import com.reign.gcld.chat.domain.*;
import com.reign.gcld.player.domain.*;
import org.springframework.transaction.annotation.*;
import com.reign.framework.json.*;
import com.reign.gcld.user.domain.*;
import com.reign.gcld.common.web.*;
import java.util.concurrent.*;
import com.reign.gcld.pay.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.common.*;
import com.reign.gcld.chat.service.*;
import com.reign.framework.netty.util.*;
import com.reign.gcld.gift.domain.*;
import com.alibaba.fastjson.*;
import java.util.*;
import com.reign.gcld.common.util.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.mail.domain.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.activity.domain.*;
import java.text.*;
import com.reign.gcld.event.util.*;
import com.reign.gcld.activity.service.*;
import com.reign.gcld.pay.service.*;
import com.reign.util.*;
import com.reign.gcld.chat.common.*;
import com.reign.gcld.common.plug.*;

@Component("systemService")
public class SystemService implements ISystemService
{
    private static final Logger rtLog;
    private static final Logger logger;
    private static final DayReportLogger drLog;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private ISilenceDao silenceDao;
    @Autowired
    private IUserBlockDao userBlockDao;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IPlayerPayDao playerPayDao;
    @Autowired
    private IStoreHouseDao storeHouseDao;
    @Autowired
    private GiftCache giftCache;
    @Autowired
    private IPlayerGiftDao playerGiftDao;
    @Autowired
    private IGiftInfoDao giftInfoDao;
    @Autowired
    private IPayService payService;
    @Autowired
    private IBakPlayerGiftDao bakPlayerGiftDao;
    @Autowired
    private IBakGiftInfoDao bakGiftInfoDao;
    @Autowired
    private IMailDao mailDao;
    @Autowired
    private IActivityService activityService;
    @Autowired
    private IActivityDao activityDao;
    @Autowired
    private IJobService jobService;
    @Autowired
    private IAutoPlayerTask autoPlayerTask;
    @Autowired
    private IPlayerEventDao playerEventDao;
    @Autowired
    private IChatService chatService;
    private static final Logger errorLogger;
    private static final Logger timerLog;
    
    static {
        rtLog = new RTReportLogger();
        logger = new InterfaceLogger();
        drLog = new DayReportLogger();
        errorLogger = CommonLog.getLog(SystemService.class);
        timerLog = new TimerLogger();
    }
    
    @Override
    public void printOnlineNum() {
        Players.printLog(SystemService.rtLog);
    }
    
    @Override
    public void statisticsGold() {
        final long start = System.currentTimeMillis();
        final Tuple<Integer, Integer> result = this.playerDao.getGoldStatistics(20, 7);
        SystemService.logger.info(LogUtil.formatGoldStatisticsLog("ugold", result.left));
        SystemService.logger.info(LogUtil.formatGoldStatisticsLog("sgold", result.right));
        SystemService.timerLog.info(LogUtil.formatThreadLog("SystemService", "statisticsGold", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Transactional
    @Override
    public byte[] banChat(final String playerNames, final String cause, final long duration, final String yx) {
        if (StringUtils.isBlank(playerNames) || StringUtils.isBlank(cause) || duration <= 0L || StringUtils.isBlank(yx)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final String[] names = playerNames.split(",");
        if (this.hasBanName(names)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_BAN_NAME_ERROR);
        }
        final Date nextSayTime = new Date(System.currentTimeMillis() + duration);
        int count = 0;
        final StringBuffer sb = new StringBuffer();
        String[] array;
        for (int length = (array = names).length, i = 0; i < length; ++i) {
            final String name = array[i];
            final Player player = this.playerDao.getPlayerByNameAndYx(name, yx);
            if (player != null) {
                final Date nowDate = new Date();
                final int playerId = player.getPlayerId();
                Silence silence = this.silenceDao.getByPlayerId(playerId);
                if (silence != null) {
                    silence.setNextSayTime(nextSayTime);
                    silence.setSilenceTime(nowDate);
                    silence.setReason(cause);
                    silence.setType(1);
                    this.silenceDao.update(silence);
                }
                else {
                    silence = new Silence();
                    silence.setNextSayTime(nextSayTime);
                    silence.setPlayerId(playerId);
                    silence.setReason(cause);
                    silence.setSilenceTime(nowDate);
                    silence.setUserId(player.getUserId());
                    silence.setYx(yx);
                    silence.setType(1);
                    this.silenceDao.create(silence);
                }
            }
            else {
                ++count;
                sb.append(String.valueOf(name) + ":" + LocalMessages.T_USER_10002 + "<" + "br" + "/>");
            }
        }
        if (count > 0) {
            return JsonBuilder.getJson(State.FAIL, sb.toString());
        }
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    @Transactional
    @Override
    public byte[] unbanChat(final String playerNames, final String yx) {
        if (StringUtils.isBlank(playerNames) || StringUtils.isBlank(yx)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Date nowDate = new Date();
        final String[] names = playerNames.split(",");
        if (this.hasBanName(names)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_BAN_NAME_ERROR);
        }
        String[] array;
        for (int length = (array = names).length, i = 0; i < length; ++i) {
            final String name = array[i];
            final Player player = this.playerDao.getPlayerByNameAndYx(name, yx);
            if (player != null) {
                final int playerId = player.getPlayerId();
                final Silence silence = this.silenceDao.getByPlayerId(playerId);
                if (silence.getType() != 2) {
                    silence.setNextSayTime(nowDate);
                    this.silenceDao.update(silence);
                }
            }
        }
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    @Override
    public byte[] getBanRecord(final String playerNames, final String yx) {
        final Date now = new Date();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("list");
        if (StringUtils.isBlank(playerNames)) {
            final List<Silence> silences = this.silenceDao.getByDateAndYx(now, yx, 1);
            for (final Silence silence : silences) {
                doc.startObject();
                doc.createElement("cause", silence.getReason());
                doc.createElement("name", this.playerDao.read(silence.getPlayerId()).getPlayerName());
                doc.createElement("until", silence.getNextSayTime().getTime());
                doc.endObject();
            }
        }
        else {
            final String[] names = playerNames.split(",");
            if (this.hasBanName(names)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_BAN_NAME_ERROR);
            }
            String[] array;
            for (int length = (array = names).length, i = 0; i < length; ++i) {
                final String name = array[i];
                final Player player = this.playerDao.getPlayerByNameAndYx(name, yx);
                if (player != null) {
                    final int playerId = player.getPlayerId();
                    final Silence silence2 = this.silenceDao.getByPlayerId(playerId);
                    if (silence2.getType() != 2) {
                        if (silence2 != null && now.before(silence2.getNextSayTime())) {
                            doc.startObject();
                            doc.createElement("cause", silence2.getReason());
                            doc.createElement("name", name);
                            doc.createElement("until", silence2.getNextSayTime().getTime());
                            doc.endObject();
                        }
                    }
                }
            }
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] banUser(final String playerNames, final String blockReason, final long interval, final String yx) {
        if ((StringUtils.isBlank(playerNames) && StringUtils.isBlank(blockReason)) || interval <= 0L) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final String[] names = playerNames.split(",");
        if (this.hasBanName(names)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_BAN_NAME_ERROR);
        }
        final Date endDate = new Date(System.currentTimeMillis() + interval * 1000L);
        String[] array;
        for (int length = (array = names).length, i = 0; i < length; ++i) {
            final String name = array[i];
            final Player player = this.playerDao.getPlayerByNameAndYx(name, yx);
            if (player != null) {
                final List<UserBlock> ubList = this.userBlockDao.getUserBlock(player.getUserId(), yx);
                if (ubList != null && ubList.size() > 0) {
                    for (final UserBlock ub : ubList) {
                        this.userBlockDao.update(ub.getVId(), blockReason, endDate);
                    }
                }
                else {
                    final UserBlock ub = new UserBlock();
                    ub.setBlockEndTime(endDate);
                    ub.setPlayerId(player.getPlayerId());
                    ub.setUserId(player.getUserId());
                    ub.setYx(yx);
                    ub.setReason(blockReason);
                    this.userBlockDao.create(ub);
                }
                AuthInterceptor.blockPlayer(player.getUserId(), yx, endDate.getTime(), blockReason);
            }
        }
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    @Transactional
    @Override
    public byte[] unbanUser(final String playerNames, final String yx) {
        if (StringUtils.isBlank(playerNames)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final String[] names = playerNames.split(",");
        if (this.hasBanName(names)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_BAN_NAME_ERROR);
        }
        String[] array;
        for (int length = (array = names).length, i = 0; i < length; ++i) {
            final String name = array[i];
            final Player player = this.playerDao.getPlayerByNameAndYx(name, yx);
            if (player != null) {
                final List<UserBlock> ubList = this.userBlockDao.getUserBlock(player.getUserId(), yx);
                if (ubList != null) {
                    for (final UserBlock ub : ubList) {
                        if (ub.getPlayerId() == player.getPlayerId()) {
                            this.userBlockDao.deleteById(ub.getVId());
                            AuthInterceptor.unblockPlayer(player.getUserId(), yx);
                        }
                    }
                }
            }
        }
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    @Override
    public byte[] getUserBanListByYx(final String yx) {
        if (StringUtils.isBlank(yx)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Date nowDate = new Date();
        final List<UserBlock> userBlockList = this.userBlockDao.getUserBanListByDateAndYx(nowDate, yx);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("list");
        for (final UserBlock ub : userBlockList) {
            if (ub.getBlockEndTime().after(nowDate)) {
                doc.startObject();
                doc.createElement("cause", ub.getReason());
                doc.createElement("id", ub.getVId());
                doc.createElement("playerId", ub.getPlayerId());
                doc.createElement("userId", ub.getUserId());
                doc.createElement("yx", ub.getYx());
                final Player player = this.playerDao.read(ub.getPlayerId());
                doc.createElement("playerName", player.getPlayerName());
                doc.createElement("until", ub.getBlockEndTime().getTime());
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void initIntercepterBlockMap() {
        final Date nowDate = new Date();
        final List<UserBlock> list = this.userBlockDao.getModels();
        for (final UserBlock ub : list) {
            if (ub.getBlockEndTime().after(nowDate)) {
                AuthInterceptor.blockPlayer(ub.getUserId(), ub.getYx(), ub.getBlockEndTime().getTime(), ub.getReason());
            }
        }
    }
    
    @Transactional
    @Override
    public byte[] getPlayerInfo(final String playerNames, final String userId, final String yx) {
        if ((StringUtils.isBlank(playerNames) && StringUtils.isBlank(userId)) || StringUtils.isBlank(yx)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Map<Integer, Player> playerMap = new ConcurrentHashMap<Integer, Player>();
        if (!StringUtils.isBlank(playerNames)) {
            final String[] names = playerNames.split(",");
            if (this.hasBanName(names)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_BAN_NAME_ERROR);
            }
            String[] array;
            for (int length = (array = names).length, i = 0; i < length; ++i) {
                final String name = array[i];
                final Player player = this.playerDao.getPlayerByNameAndYx(name, yx);
                if (player != null) {
                    playerMap.put(player.getPlayerId(), player);
                }
            }
        }
        if (!StringUtils.isBlank(userId)) {
            final String[] userIds = userId.split(",");
            String[] array2;
            for (int length2 = (array2 = userIds).length, j = 0; j < length2; ++j) {
                final String oneUserId = array2[j];
                final List<Player> playerList = this.playerDao.getPlayerByUserId(oneUserId, yx);
                for (final Player player2 : playerList) {
                    playerMap.put(player2.getPlayerId(), player2);
                }
            }
        }
        if (playerMap.size() == 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10002);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("list");
        for (final Integer playerId : playerMap.keySet()) {
            final Player player3 = playerMap.get(playerId);
            doc.startObject();
            doc.createElement("totalGem", this.storeHouseDao.getGemNumByPlayerId(playerId));
            doc.createElement("playerId", playerId);
            doc.createElement("playerName", player3.getPlayerName());
            doc.createElement("pic", player3.getPic());
            doc.createElement("yx", yx);
            doc.createElement("userId", player3.getUserId());
            final String guild = "";
            doc.createElement("guild", guild);
            doc.createElement("vip", player3.getConsumeLv());
            doc.createElement("country", getForceByForceId(player3.getForceId()));
            doc.createElement("level", player3.getPlayerLv());
            doc.createElement("sysGold", player3.getSysGold());
            doc.createElement("userGold", player3.getUserGold());
            doc.createElement("silver", this.playerResourceDao.read(playerId).getCopper());
            final List<PlayerPay> playerPayList = this.playerPayDao.getPlayerPayByPlayerId(playerId);
            doc.startArray("payHistory");
            for (final PlayerPay playerPay : playerPayList) {
                doc.startObject();
                doc.createElement("orderId", playerPay.getOrderId());
                doc.createElement("time", playerPay.getCreateTime());
                doc.createElement("count", playerPay.getGold());
                doc.endObject();
            }
            doc.endArray();
            doc.createElement("pay", this.playerPayDao.queryPaySum(playerId));
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    public static String getForceByForceId(final int forceId) {
        switch (forceId) {
            case 1: {
                return LocalMessages.T_FORCE_WEI;
            }
            case 2: {
                return LocalMessages.T_FORCE_SHU;
            }
            case 3: {
                return LocalMessages.T_FORCE_WU;
            }
            default: {
                return "";
            }
        }
    }
    
    @Transactional
    @Override
    public byte[] repay(final String orderId, final int gold, final String userId, final int playerId, final String yx, final Date createTime, final int type, final Request request) {
        if (StringUtils.isBlank(orderId) || gold <= 0 || StringUtils.isBlank(userId) || playerId <= 0 || StringUtils.isBlank(yx) || createTime == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Date now = new Date();
        if (createTime.after(now)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_DATE_ERROR);
        }
        final Player player = this.playerDao.read(playerId);
        if (player == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_PLAYER_10005);
        }
        if (!userId.equals(player.getUserId())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_ID_ERROR);
        }
        if (!yx.equals(player.getYx())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_YX_ERROR);
        }
        if (this.playerPayDao.containsOrderId(orderId, yx)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_REPAY_HAVE_RECORD);
        }
        if (!this.payService.commonPay(orderId, gold, userId, yx, type, "\u540e\u53f0\u5145\u503c\u8865\u5355\u589e\u52a0\u91d1\u5e01", player, createTime, "", request)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_REPAY_ERROR);
        }
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    @Transactional
    @Override
    public byte[] yxPayData(final Date startTime, final Date endTime, final String yx) {
        if (startTime == null || endTime == null || StringUtils.isBlank(yx)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("totalGold", this.playerPayDao.getTotalGold(startTime, endTime, yx));
        doc.createElement("playerCount", this.playerPayDao.getPlayerCount(startTime, endTime, yx));
        doc.createElement("orderCount", this.playerPayDao.getOrderCount(startTime, endTime, yx));
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] yxPayHistory(final Date startTime, final Date endTime, final String yx) {
        if (startTime == null || endTime == null || StringUtils.isBlank(yx)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("list");
        final List<PlayerPay> playerPayList = this.playerPayDao.getPlayerPayByDateAndYx(startTime, endTime, yx);
        for (final PlayerPay playerPay : playerPayList) {
            doc.startObject();
            doc.createElement("id", playerPay.getVId());
            doc.createElement("time", playerPay.getCreateTime());
            doc.createElement("yx", yx);
            doc.createElement("userId", playerPay.getUserId());
            doc.createElement("playerId", playerPay.getPlayerId());
            doc.createElement("playerName", this.playerDao.read(playerPay.getPlayerId()).getPlayerName());
            doc.createElement("count", playerPay.getGold());
            doc.createElement("orderId", playerPay.getOrderId());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] getGiftContent() {
        final Map<String, Gift> giftMap = this.giftCache.getCacheMap();
        final Map<String, Gift> resourceGiftMap = new ConcurrentHashMap<String, Gift>();
        final Map<String, Gift> gemGiftMap = new ConcurrentHashMap<String, Gift>();
        final Map<String, Gift> equipGiftMap = new ConcurrentHashMap<String, Gift>();
        final Map<String, Gift> toolGiftMap = new ConcurrentHashMap<String, Gift>();
        for (final String id : giftMap.keySet()) {
            final Gift gift = giftMap.get(id);
            if ("resource".equals(gift.getTypeId())) {
                resourceGiftMap.put(id, gift);
            }
            else if ("gem".equals(gift.getTypeId())) {
                gemGiftMap.put(id, gift);
            }
            else if ("equip".equals(gift.getTypeId())) {
                equipGiftMap.put(id, gift);
            }
            else {
                if (!"tool".equals(gift.getTypeId())) {
                    continue;
                }
                toolGiftMap.put(id, gift);
            }
        }
        final JsonDocument doc = new JsonDocument();
        doc.startArray();
        if (resourceGiftMap.size() > 0) {
            doc.startObject();
            doc.createElement("id", "resource");
            doc.createElement("text", (Object)LocalMessages.BACK_STAGE_RESOURCE);
            doc.startArray("child");
            for (final String id2 : resourceGiftMap.keySet()) {
                final Gift gift2 = resourceGiftMap.get(id2);
                doc.startObject();
                doc.createElement("id", gift2.getChildId());
                doc.createElement("text", gift2.getChildName());
                doc.endObject();
            }
            doc.endArray();
            doc.endObject();
        }
        if (gemGiftMap.size() > 0) {
            doc.startObject();
            doc.createElement("id", "gem");
            doc.createElement("text", (Object)LocalMessages.T_COMM_10023);
            doc.startArray("child");
            for (final String id2 : gemGiftMap.keySet()) {
                final Gift gift2 = gemGiftMap.get(id2);
                doc.startObject();
                doc.createElement("id", gift2.getChildId());
                doc.createElement("text", gift2.getChildName());
                doc.endObject();
            }
            doc.endArray();
            doc.endObject();
        }
        if (equipGiftMap.size() > 0) {
            doc.startObject();
            doc.createElement("id", "equip");
            doc.createElement("text", (Object)LocalMessages.BACK_STAGE_EQUIP);
            doc.startArray("child");
            for (final String id2 : equipGiftMap.keySet()) {
                final Gift gift2 = equipGiftMap.get(id2);
                doc.startObject();
                doc.createElement("id", gift2.getChildId());
                doc.createElement("text", gift2.getChildName());
                doc.endObject();
            }
            doc.endArray();
            doc.endObject();
        }
        if (toolGiftMap.size() > 0) {
            doc.startObject();
            doc.createElement("id", "tool");
            doc.createElement("text", (Object)LocalMessages.BACK_STAGE_TOOL);
            doc.startArray("child");
            for (final String id2 : toolGiftMap.keySet()) {
                final Gift gift2 = toolGiftMap.get(id2);
                doc.startObject();
                doc.createElement("id", gift2.getChildId());
                doc.createElement("text", gift2.getChildName());
                doc.endObject();
            }
            doc.endArray();
            doc.endObject();
        }
        doc.endArray();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] addGift(final JSONObject json) {
        try {
            final int allServer = json.getIntValue("allServer");
            final String giftName = json.getString("giftName");
            final long passDate = json.getLongValue("passDate");
            final String yx = json.getString("yx");
            final Date now = new Date();
            if (StringUtils.isBlank(giftName)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_GIFT_NAME_ERROR);
            }
            if (passDate < System.currentTimeMillis()) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_PASS_DATE_ERROR);
            }
            if (allServer == 0) {
                final String playerNames = json.getString("playerNames");
                final String userIds = json.getString("userIds");
                if (StringUtils.isBlank(playerNames) && StringUtils.isBlank(userIds)) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_NAMES_ERROR);
                }
                if (StringUtils.isBlank(yx)) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_NO_YX_ERROR);
                }
                final GiftInfo giftInfo = new GiftInfo();
                giftInfo.setGiftName(giftName);
                final Tuple<Boolean, String> tuple = this.getContents(json);
                if (!(boolean)tuple.left) {
                    return JsonBuilder.getJson(State.FAIL, tuple.right);
                }
                giftInfo.setYx(yx);
                giftInfo.setContents(tuple.right);
                giftInfo.setAllServer(0);
                giftInfo.setCurrentPlayer(0);
                giftInfo.setSendTime(now);
                giftInfo.setExpiredTime(new Date(passDate));
                final int result = this.giftInfoDao.create(giftInfo);
                if (result <= 0) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_INSERT_GIFT_ERROR);
                }
                final Set<Integer> s = Collections.synchronizedSet(new HashSet<Integer>());
                if (StringUtils.isNotBlank(playerNames)) {
                    final String[] names = playerNames.split(",");
                    if (this.hasBanName(names)) {
                        return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_BAN_NAME_ERROR);
                    }
                    String[] array;
                    for (int length = (array = names).length, i = 0; i < length; ++i) {
                        final String playerName = array[i];
                        final Player player = this.playerDao.getPlayerByNameAndYx(playerName, yx);
                        if (player != null) {
                            s.add(player.getPlayerId());
                        }
                    }
                }
                if (StringUtils.isNotBlank(userIds)) {
                    final String[] ids = userIds.split(",");
                    String[] array2;
                    for (int length2 = (array2 = ids).length, j = 0; j < length2; ++j) {
                        final String userId = array2[j];
                        final List<Integer> playerIdList = this.playerDao.getPlayerIdListByUserIdAndYx(userId, yx);
                        s.addAll(playerIdList);
                    }
                }
                if (s.size() == 0) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_PLAYER_USER_ERROR);
                }
                final PlayerGift playerGift = new PlayerGift();
                playerGift.setGiftId(giftInfo.getId());
                playerGift.setReceived(0);
                playerGift.setAllServer(0);
                int count = 0;
                int temp = 0;
                final byte[] send = JsonBuilder.getSimpleJson("hasGift", 1);
                for (final Integer playerId : s) {
                    playerGift.setPlayerId(playerId);
                    playerGift.setId(0);
                    temp = this.playerGiftDao.create(playerGift);
                    if (temp >= 1) {
                        ++count;
                    }
                    if (Players.getPlayer(playerId) != null) {
                        Players.push(playerId, PushCommand.PUSH_UPDATE, send);
                    }
                }
                if (count < s.size()) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.GIFT_SEND_FAIL);
                }
            }
            else {
                if (allServer != 1) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_ALLSERVER_ERROR);
                }
                final int allCurrentPlayers = Integer.parseInt(json.getString("allCurrentPlayers"));
                if (allCurrentPlayers != 0 && allCurrentPlayers != 1) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_ALL_CURRENT_PLAYERS_ERROR);
                }
                final Tuple<Boolean, String> tuple2 = this.getContents(json);
                if (!(boolean)tuple2.left) {
                    return JsonBuilder.getJson(State.FAIL, tuple2.right);
                }
                final String giftContents = tuple2.right;
                if (StringUtils.isBlank(giftContents)) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_GIFT_CONTENTS_ERROR);
                }
                final GiftInfo giftInfo2 = new GiftInfo();
                giftInfo2.setGiftName(giftName);
                giftInfo2.setYx(yx);
                giftInfo2.setContents(giftContents);
                giftInfo2.setAllServer(1);
                giftInfo2.setCurrentPlayer(allCurrentPlayers);
                giftInfo2.setSendTime(now);
                giftInfo2.setExpiredTime(new Date(passDate));
                final int result = this.giftInfoDao.create(giftInfo2);
                if (result <= 0) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_INSERT_GIFT_ERROR);
                }
                final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_UPDATE.getModule(), JsonBuilder.getSimpleJson("hasGift", 1)));
                GroupManager.getInstance().notifyAll(String.valueOf(ChatType.YX.toString()) + yx, WrapperUtil.wrapper(PushCommand.PUSH_UPDATE.getCommand(), 0, bytes));
            }
            this.deleteGiftRecord(now);
            return JsonBuilder.getJson(State.SUCCESS, "ok");
        }
        catch (Exception e) {
            SystemService.errorLogger.error("className:SystemService#methodName:addGift");
            SystemService.errorLogger.error(e.getMessage());
            SystemService.errorLogger.error(this, e);
            return JsonBuilder.getJson(State.FAIL, "add gift error : error message:" + e.getMessage());
        }
    }
    
    private void deleteGiftRecord(final Date now) {
        final List<GiftInfo> expiredGiftInfoList = this.giftInfoDao.getByDate(now);
        final BakPlayerGift bpg = new BakPlayerGift();
        final BakGiftInfo bgi = new BakGiftInfo();
        for (final GiftInfo gi : expiredGiftInfoList) {
            final int giftId = gi.getId();
            final List<PlayerGift> pgList = this.playerGiftDao.getByGiftId(giftId);
            for (final PlayerGift pg : pgList) {
                bpg.setBakId(null);
                bpg.setId(pg.getId());
                bpg.setPlayerId(pg.getPlayerId());
                bpg.setGiftId(giftId);
                bpg.setReceived(pg.getReceived());
                bpg.setReceivedTime(pg.getReceivedTime());
                bpg.setAllServer(pg.getAllServer());
                this.bakPlayerGiftDao.create(bpg);
            }
            bgi.setBakId(null);
            bgi.setId(gi.getId());
            bgi.setGiftName(gi.getGiftName());
            bgi.setYx(gi.getYx());
            bgi.setContents(gi.getContents());
            bgi.setAllServer(gi.getAllServer());
            bgi.setCurrentPlayer(gi.getCurrentPlayer());
            bgi.setSendTime(gi.getSendTime());
            bgi.setExpiredTime(gi.getExpiredTime());
            this.bakGiftInfoDao.create(bgi);
            this.playerGiftDao.deleteByGiftId(giftId);
            this.giftInfoDao.deleteById(giftId);
        }
    }
    
    private Tuple<Boolean, String> getContents(final JSONObject json) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = true;
        final StringBuffer sb = new StringBuffer();
        final String[] resources = { "gold", "silver", "wooden", "food", "iron" };
        final Set<String> goods = this.giftCache.getKeys();
        String itemNameback = null;
        try {
            String[] array;
            for (int length = (array = resources).length, i = 0; i < length; ++i) {
                final String itemName = itemNameback = array[i];
                this.parse(sb, json, itemName);
            }
            final JSONArray goodsArray = json.getJSONArray("goods");
            if (goodsArray != null && !goodsArray.isEmpty()) {
                final Iterator<String> iterator = goods.iterator();
                while (iterator.hasNext()) {
                    final String itemName2 = itemNameback = iterator.next();
                    this.parseGoods(sb, goodsArray, itemName2);
                }
            }
        }
        catch (Exception e) {
            SystemService.errorLogger.error("className:SystemService#methodName:addGift, itemName :" + itemNameback);
            SystemService.errorLogger.error(e.getMessage());
            SystemService.errorLogger.error(this, e);
            tuple.left = false;
            tuple.right = "addGift error, itemName :" + itemNameback + "reason: " + e.getMessage();
            return tuple;
        }
        String contents = StringUtils.replace(sb.toString(), "silver", "copper");
        contents = StringUtils.replace(contents, "wooden", "lumber");
        if (contents.length() <= 0) {
            tuple.left = false;
            tuple.right = "content  length is 0";
        }
        else {
            tuple.right = contents.substring(0, contents.length() - 1);
        }
        return tuple;
    }
    
    private void parse(final StringBuffer sb, final JSONObject json, final String itemName) throws Exception {
        final Object o = json.get(itemName);
        int count = 0;
        if (o != null && StringUtils.isNotBlank(o.toString())) {
            count = Integer.parseInt(o.toString());
            if (count > 0) {
                sb.append(itemName);
                sb.append(",");
                sb.append(count);
                sb.append(";");
            }
        }
    }
    
    private void parseGoods(final StringBuffer sb, final JSONArray goodsArray, final String itemName) throws Exception {
        for (int i = 0; i < goodsArray.size(); ++i) {
            final JSONObject json = goodsArray.getJSONObject(i);
            Object o = json.get("nameId");
            if (o != null && itemName.equals(o.toString())) {
                o = json.get("amount");
                int count = 0;
                if (o != null && StringUtils.isNotBlank(o.toString())) {
                    count = Integer.parseInt(o.toString());
                    if (count > 0) {
                        sb.append(itemName);
                        sb.append(",");
                        sb.append(count);
                        sb.append(";");
                    }
                }
            }
        }
    }
    
    private boolean hasBanName(final String[] names) {
        if (names != null && names.length > 0) {
            for (final String name : names) {
                if (LocalMessages.T_COMM_10010.equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public byte[] backpay(final String playerNames, final int gold, final String yx, final Request request) {
        if (StringUtils.isBlank(playerNames) || gold <= 0 || StringUtils.isBlank(yx)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final String[] names = WebUtil.getStringArray(playerNames);
        if (this.hasBanName(names)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_BAN_NAME_ERROR);
        }
        final Map<Integer, Player> existMap = new HashMap<Integer, Player>();
        final Set<String> noExistSet = new HashSet<String>();
        String[] array;
        for (int length = (array = names).length, i = 0; i < length; ++i) {
            final String name = array[i];
            if (StringUtils.isNotBlank(name)) {
                final Player player = this.playerDao.getPlayerByNameAndYx(name, yx);
                if (player != null) {
                    existMap.put(player.getPlayerId(), player);
                }
                else {
                    noExistSet.add(name);
                }
            }
        }
        boolean tag = false;
        final Date now = new Date();
        final long time = now.getTime();
        for (final Map.Entry<Integer, Player> temp : existMap.entrySet()) {
            if (!this.payService.commonPay("backpay" + time + temp.getKey(), gold, temp.getValue().getUserId(), yx, 1, "\u540e\u53f0\u5145\u503c\u589e\u52a0\u91d1\u5e01", temp.getValue(), now, "", request)) {
                noExistSet.add(temp.getValue().getPlayerName());
            }
            else {
                tag = true;
            }
        }
        if (!tag) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_BACK_PAY_ERROR);
        }
        if (noExistSet.size() == 0) {
            return JsonBuilder.getJson(State.SUCCESS, "ok");
        }
        final StringBuffer sb = new StringBuffer();
        sb.append(LocalMessages.BACK_STAGE_BACK_PAY_FAIL_ROLE);
        final Iterator<String> iter = noExistSet.iterator();
        while (iter.hasNext()) {
            sb.append(iter.next());
            sb.append(",");
        }
        return JsonBuilder.getJson(State.SUCCESS, sb.substring(0, sb.length() - 1));
    }
    
    @Transactional
    @Override
    public byte[] sendMail(final int noticeType, final int playerlv, final String countryIds, final String playerNames, final String title, final String content, final String yx) {
        if (noticeType < 0 || noticeType > 2 || StringUtils.isBlank(title) || StringUtils.isBlank(content) || StringUtils.isBlank(yx) || !YxUtil.isMatched(yx)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        if (1 == noticeType) {
            if (playerlv < 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            this.mailDao.sendMail(yx, LocalMessages.T_MAIL_ROLE_SYSTEM, title, content, playerlv);
            final Group group = GroupManager.getInstance().getGroup(String.valueOf(ChatType.YX.toString()) + yx);
            final byte[] bytes = JsonBuilder.getJson(JsonBuilder.getJson(State.PUSH, PushCommand.PUSH_UPDATE.getModule(), JsonBuilder.getSimpleJson("hasNewMail", 1)));
            group.notify((String)null, WrapperUtil.wrapper(PushCommand.PUSH_UPDATE.getCommand(), 0, bytes));
        }
        else if (2 == noticeType) {
            if (playerlv < 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            if (StringUtils.isBlank(countryIds)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            final String[] forceIds = countryIds.split(",");
            if (forceIds == null || forceIds.length < 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            final Set<Integer> forceIdSet = new HashSet<Integer>();
            String[] array;
            for (int length = (array = forceIds).length, i = 0; i < length; ++i) {
                final String forceId = array[i];
                final int temp = Integer.parseInt(forceId);
                if (temp < 1 || temp > 3) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
                }
                forceIdSet.add(temp);
            }
            final byte[] send = JsonBuilder.getSimpleJson("hasNewMail", 1);
            for (final int forceId2 : forceIdSet) {
                this.mailDao.sendMailByForceId(yx, LocalMessages.T_MAIL_ROLE_SYSTEM, title, content, forceId2, playerlv);
                for (final PlayerDto dto : Players.getAllPlayerByForceIdAndYx(forceId2, yx)) {
                    Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
                }
            }
        }
        else {
            if (noticeType != 0) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            if (StringUtils.isBlank(playerNames)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            final String[] playerNameArray = playerNames.split(",");
            if (playerNameArray == null || playerNameArray.length < 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            final Set<Integer> playerIdSet = new HashSet<Integer>();
            final Set<String> playerNameSet = new HashSet<String>();
            String[] array2;
            for (int length2 = (array2 = playerNameArray).length, j = 0; j < length2; ++j) {
                final String playerName = array2[j];
                if ("\u5c06\u519b".equals(playerName)) {
                    playerNameSet.add(playerName);
                }
                else {
                    final int playerId = this.playerDao.getPlayerIdByNameAndYx(playerName, yx);
                    if (playerId <= 0) {
                        playerNameSet.add(playerName);
                    }
                    else {
                        playerIdSet.add(playerId);
                    }
                }
            }
            final Mail mail = new Mail();
            mail.setFName(LocalMessages.T_MAIL_ROLE_SYSTEM);
            mail.setTitle(title);
            mail.setContent(content);
            mail.setSendtime(new Date());
            mail.setIsRead(0);
            mail.setIsDelete(0);
            mail.setMailType(1);
            mail.setLinkId(0);
            final byte[] send2 = JsonBuilder.getSimpleJson("hasNewMail", 1);
            for (final int playerId2 : playerIdSet) {
                mail.setId(null);
                mail.setFId(0);
                mail.setTId(playerId2);
                this.mailDao.saveSystemMail(mail);
                Players.push(playerId2, PushCommand.PUSH_UPDATE, send2);
            }
            if (playerNameSet.size() > 0) {
                return JsonBuilder.getJson(State.FAIL, String.valueOf(LocalMessages.T_MAIL_ROLE_NOT_EXIST) + playerNameSet.toString().substring(1, playerNameSet.toString().length() - 1));
            }
        }
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    @Transactional
    @Override
    public byte[] activityList() {
        final Map<Integer, Activity> map = this.activityDao.getActivityMap();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("activities");
        final Date date = new Date();
        for (int i = 1; i <= 21; ++i) {
            final Activity activity = map.get(i);
            doc.startObject();
            doc.createElement("type", i);
            if (activity != null) {
                doc.createElement("name", activity.getName());
                doc.createElement("startTime", activity.getStartTime());
                doc.createElement("endTime", activity.getEndTime());
                doc.createElement("content", activity.getParamsInfo());
            }
            else {
                doc.createElement("name", this.getActivityName(i));
                doc.createElement("startTime", date);
                doc.createElement("endTime", date);
                doc.createElement("content", this.getActivityParams(i));
            }
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return doc.toByte();
    }
    
    private String getActivityName(final int type) {
        if (type == 1) {
            return LocalMessages.ACTIVITY_NAME_1;
        }
        if (type == 2) {
            return LocalMessages.ACTIVITY_NAME_2;
        }
        if (type == 3) {
            return LocalMessages.ACTIVITY_NAME_3;
        }
        if (type == 4) {
            return LocalMessages.ACTIVITY_NAME_4;
        }
        if (type == 5) {
            return LocalMessages.ACTIVITY_NAME_5;
        }
        if (type == 6) {
            return LocalMessages.ACTIVITY_NAME_6;
        }
        if (type == 7) {
            return LocalMessages.ACTIVITY_NAME_7;
        }
        if (type == 8) {
            return LocalMessages.ACTIVITY_NAME_8;
        }
        if (type == 9) {
            return LocalMessages.ACTIVITY_NAME_9;
        }
        if (type == 10) {
            return LocalMessages.ACTIVITY_NAME_10;
        }
        if (type == 11) {
            return LocalMessages.ACTIVITY_NAME_11;
        }
        if (type == 12) {
            return LocalMessages.ACTIVITY_NAME_12;
        }
        if (type == 13) {
            return LocalMessages.ACTIVITY_NAME_13;
        }
        if (type == 14) {
            return LocalMessages.ACTIVITY_NAME_14;
        }
        if (type == 15) {
            return LocalMessages.ACTIVITY_NAME_15;
        }
        if (type == 16) {
            return LocalMessages.ACTIVITY_NAME_16;
        }
        if (type == 17) {
            return LocalMessages.ACTIVITY_NAME_17;
        }
        if (type == 18) {
            return LocalMessages.ACTIVITY_NAME_18;
        }
        if (type == 19) {
            return LocalMessages.ACTIVITY_NAME_19;
        }
        if (type == 20) {
            return LocalMessages.ACTIVITY_NAME_20;
        }
        if (type == 21) {
            return LocalMessages.ACTIVITY_NAME_21;
        }
        return "";
    }
    
    private String getActivityParams(final int type) {
        if (type == 1) {
            return "";
        }
        if (type == 2) {
            return "100,10;1000,70;2000,140;5000,400;10000,800;20000,1600;50000,5000";
        }
        if (type == 3) {
            return "1,0.2;1.5,0.3;2,0.5;3,1";
        }
        if (type == 5) {
            return "50000,20000;100000,30000;200000,40000;400000,50000;600000,60000";
        }
        if (type == 7) {
            return "100,1000;1000,3000;2000,5000;5000,12000;10000,20000;20000,20000;50000,20000";
        }
        if (type == 8) {
            return "100,30,1;200,60,1;500,120,1;1000,120,2;2000,200,2";
        }
        return "";
    }
    
    @Transactional
    @Override
    public byte[] activity(final int type, final String startTimeStr, final String endTimeStr, final String paramsInfo) {
        long startTime = 0L;
        long endTime = 0L;
        final long curTime = System.currentTimeMillis();
        try {
            final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            startTime = df.parse(startTimeStr).getTime();
            endTime = df.parse(endTimeStr).getTime();
        }
        catch (Exception e) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10042);
        }
        if (startTime >= endTime || startTime < curTime || endTime <= curTime) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10041);
        }
        if (curTime + 60000L > startTime) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_START_TIME_SET);
        }
        Activity activity = this.activityDao.read(type);
        if (type == 1) {
            return this.activityService.initActivity(type, startTime, endTime, paramsInfo, false);
        }
        if (type == 2) {
            return this.setPayActivity(type, startTime, endTime, paramsInfo);
        }
        if (type == 3) {
            return this.activityService.initLvExpActivity(type, startTime, endTime, paramsInfo, false);
        }
        if (type == 4) {
            return this.activityService.initDragonActivity(type, startTime, endTime);
        }
        if (type == 5) {
            return this.activityService.initIronActivity(type, startTime, endTime, paramsInfo);
        }
        if (type == 6) {
            return this.activityService.initQuenchingActivity(type, startTime, endTime);
        }
        if (type == 7) {
            return this.initTicketActivity(type, startTime, endTime, paramsInfo);
        }
        if (type == 8) {
            return this.activityService.initDstqActivity(type, startTime, endTime, paramsInfo);
        }
        if (type != 9 && type != 10 && type != 11 && type != 12 && type != 13 && type != 14 && type != 15 && type != 16 && type != 17 && type != 18 && type != 19 && type != 20 && type != 21) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10043);
        }
        if (EventUtil.getEventIdSet(0).contains(type)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_DOING_NOT_RESET);
        }
        EventUtil.removeEvent(type);
        this.playerEventDao.clearEvent(type);
        EventUtil.addEvent(type, new Date(startTime), new Date(endTime), paramsInfo);
        if (activity == null) {
            activity = new Activity();
            activity.setVId(type);
            activity.setStartTime(new Date(startTime));
            activity.setEndTime(new Date(endTime));
            activity.setParamsInfo(paramsInfo);
            activity.setName(this.getActivityName(type));
            this.activityDao.create(activity);
        }
        else {
            this.activityDao.updateInfo(type, new Date(startTime), new Date(endTime), paramsInfo);
        }
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    public byte[] setPayActivity(final int type, final long startTime, final long endTime, final String paramsInfo) {
        Tuple<Boolean, String> result = new Tuple(false, "");
        result = this.checkPayActivity(type, startTime, endTime, paramsInfo);
        if (!(boolean)result.left) {
            return JsonBuilder.getJson(State.FAIL, result.right);
        }
        final Activity activity = this.activityDao.read(type);
        if (activity == null) {
            final Activity newActivity = new Activity();
            newActivity.setVId(type);
            newActivity.setStartTime(new Date(startTime));
            newActivity.setEndTime(new Date(endTime));
            newActivity.setParamsInfo(paramsInfo);
            newActivity.setName(this.getActivityName(type));
            this.activityDao.create(newActivity);
            this.playerDao.resetTotalUserGold();
        }
        else {
            if (new Date().after(activity.getStartTime()) && new Date().before(activity.getEndTime())) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_DOING_NOT_RESET);
            }
            this.activityDao.updateInfo(type, new Date(startTime), new Date(endTime), paramsInfo);
            this.playerDao.resetTotalUserGold();
        }
        if (startTime > System.currentTimeMillis()) {
            this.jobService.addJob("payService", "pushPayActivityInfo", "", startTime, false);
        }
        if (endTime > System.currentTimeMillis()) {
            this.jobService.addJob("payService", "pushPayActivityInfoEnd", "", endTime, false);
        }
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    @Transactional
    @Override
    public Tuple<Boolean, String> checkPayActivity(final int type, final long startTime, final long endTime, final String paramsInfo) {
        final Tuple<Boolean, String> result = new Tuple(false, "");
        final long curTime = System.currentTimeMillis();
        if (type <= 0 || startTime <= 0L || endTime <= 0L || StringUtils.isBlank(paramsInfo)) {
            result.right = LocalMessages.PAY_ACTIVITY_NULL_VALUE;
            return result;
        }
        if (startTime >= endTime || startTime < curTime || endTime <= curTime) {
            result.right = LocalMessages.PAY_ACTIVITY_INVALID_TIME;
            return result;
        }
        final String[] rules = paramsInfo.trim().split(";");
        String[] array;
        for (int length = (array = rules).length, i = 0; i < length; ++i) {
            final String str = array[i];
            if (str.split(",").length != 2) {
                result.right = LocalMessages.PAY_ACTIVITY_INVALID_RULES;
                return result;
            }
            try {
                final int payGold = Integer.parseInt(str.split(",")[0]);
                final int additionalGold = Integer.parseInt(str.split(",")[1]);
                if (additionalGold * 1.0 / payGold > 0.3) {
                    result.right = LocalMessages.PAY_ACTIVITY_PROTECT;
                    return result;
                }
            }
            catch (NumberFormatException e) {
                result.right = LocalMessages.PAY_ACTIVITY_NUMBER_FORMAT_EXCEPTION;
                return result;
            }
        }
        result.left = true;
        return result;
    }
    
    @Override
    public void initAllActivity() {
        final List<Activity> list = this.activityDao.getModels();
        final Date now = new Date();
        if (list != null) {
            for (final Activity act : list) {
                if (act.getVId() == 1) {
                    this.activityService.initActivity(act.getVId(), act.getStartTime().getTime(), act.getEndTime().getTime(), act.getParamsInfo(), true);
                }
                else if (act.getVId() == 2) {
                    if (act.getStartTime().after(now)) {
                        this.jobService.addJob("payService", "pushPayActivityInfo", "", act.getStartTime().getTime(), false);
                    }
                    if (!act.getEndTime().after(now)) {
                        continue;
                    }
                    this.jobService.addJob("payService", "pushPayActivityInfoEnd", "", act.getEndTime().getTime(), false);
                }
                else if (act.getVId() == 3) {
                    this.activityService.initLvExpActivity(act.getVId(), act.getStartTime().getTime(), act.getEndTime().getTime(), act.getParamsInfo(), true);
                }
                else if (act.getVId() == 4) {
                    if (act.getStartTime().after(now)) {
                        this.jobService.addJob("activityService", "initDragonActivityStart", "", act.getStartTime().getTime(), false);
                    }
                    if (!act.getEndTime().after(now)) {
                        continue;
                    }
                    this.jobService.addJob("activityService", "initDragonActivityEnd", "", act.getEndTime().getTime(), false);
                }
                else if (act.getVId() == 5) {
                    if (act.getStartTime().after(now)) {
                        this.jobService.addJob("activityService", "initIronActivityBegin", "", act.getStartTime().getTime(), false);
                    }
                    if (!act.getEndTime().after(now)) {
                        continue;
                    }
                    this.jobService.addJob("activityService", "initIronActivityEnd", "", act.getEndTime().getTime(), false);
                }
                else if (act.getVId() == 6) {
                    if (act.getStartTime().after(now)) {
                        this.jobService.addJob("activityService", "initQuenchingActivityStart", "", act.getStartTime().getTime(), false);
                    }
                    if (act.getEndTime().after(now)) {
                        this.jobService.addJob("activityService", "initQuenchingActivityEnd", "", act.getEndTime().getTime(), false);
                    }
                    if (!act.getStartTime().before(now) || !act.getEndTime().after(now)) {
                        continue;
                    }
                    ActivityService.inQuenching = true;
                }
                else if (act.getVId() == 7) {
                    if (act.getStartTime().after(now)) {
                        this.jobService.addJob("systemService", "initTicketActivityBegin", "", act.getStartTime().getTime(), false);
                    }
                    if (!act.getEndTime().after(now)) {
                        continue;
                    }
                    this.jobService.addJob("systemService", "initTicketActivityEnd", "", act.getEndTime().getTime(), false);
                }
                else {
                    if (act.getVId() != 8) {
                        continue;
                    }
                    if (act.getStartTime().after(now)) {
                        this.jobService.addJob("activityService", "initDstqActivityBegin", "", act.getStartTime().getTime(), false);
                    }
                    if (!act.getEndTime().after(now)) {
                        continue;
                    }
                    this.jobService.addJob("activityService", "initDstqActivityEnd", "", act.getEndTime().getTime(), false);
                }
            }
        }
    }
    
    @Override
    public byte[] gmAuthority(final String playerNames, final int gm) {
        if (gm < 0 || gm > 2) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final String[] names = playerNames.split(",");
        if (this.hasBanName(names)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_BAN_NAME_ERROR);
        }
        final StringBuffer sb = new StringBuffer();
        int count = 0;
        String[] array;
        for (int length = (array = names).length, i = 0; i < length; ++i) {
            final String name = array[i];
            final Player player = this.playerDao.getPlayerByName(name);
            if (player != null) {
                if (gm == 2) {
                    final PlayerDto playerDto = new PlayerDto();
                    playerDto.playerId = player.getPlayerId();
                    playerDto.playerLv = player.getPlayerLv();
                    playerDto.playerName = player.getPlayerName();
                    this.autoPlayerTask.autoTask(playerDto, 121);
                }
                this.playerDao.setGm(player.getPlayerId(), gm);
            }
            else {
                ++count;
                sb.append(String.valueOf(name) + ":" + LocalMessages.T_USER_10002 + "<" + "br" + "/>");
            }
        }
        if (count > 0) {
            return JsonBuilder.getJson(State.FAIL, sb.toString());
        }
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    @Override
    public byte[] getGmInfo() {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("gms");
        final List<Player> players = this.playerDao.getByGm();
        for (final Player player : players) {
            doc.startObject();
            doc.createElement("playerName", player.getPlayerName());
            doc.createElement("gm", player.getGm());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] consumeGold(final String playerNames) {
        if (playerNames == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final String[] names = playerNames.split(",");
        if (this.hasBanName(names)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.BACK_STAGE_BAN_NAME_ERROR);
        }
        final StringBuffer sb = new StringBuffer();
        int count = 0;
        String[] array;
        for (int length = (array = names).length, i = 0; i < length; ++i) {
            final String name = array[i];
            final Player player = this.playerDao.getPlayerByName(name);
            if (player != null) {
                this.playerDao.consumeGold(player, player.getGold(), "\u5176\u5b83\u5145\u51b2\u91d1\u6d88\u8017");
            }
            else {
                ++count;
                sb.append(String.valueOf(name) + ":" + LocalMessages.T_USER_10002 + "<" + "br" + "/>");
            }
        }
        if (count > 0) {
            return JsonBuilder.getJson(State.FAIL, sb.toString());
        }
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    private byte[] initTicketActivity(final int type, final long startTime, final long endTime, final String paramsInfo) {
        SystemService.errorLogger.error("class:SystemService#method:initTicketActivity#begin");
        final Activity activity = this.activityDao.read(type);
        if (activity != null && new Date().after(activity.getStartTime()) && new Date().before(activity.getEndTime())) {
            SystemService.errorLogger.error("class:SystemService#method:initTicketActivity#not_reset");
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_DOING_NOT_RESET);
        }
        final Tuple<Boolean, String> result = this.checkTicketActivity(startTime, endTime, paramsInfo);
        if (!(boolean)result.left) {
            SystemService.errorLogger.error("class:SystemService#method:initTicketActivity#error:" + result.right);
            return JsonBuilder.getJson(State.FAIL, result.right);
        }
        final Long curTime = System.currentTimeMillis();
        if (curTime + 60000L > startTime) {
            SystemService.errorLogger.error("class:SystemService#method:initTicketActivity#error_time");
            return JsonBuilder.getJson(State.FAIL, LocalMessages.ACTIVITY_START_TIME_SET);
        }
        if (activity == null) {
            final Activity newActivity = new Activity();
            newActivity.setVId(type);
            newActivity.setStartTime(new Date(startTime));
            newActivity.setEndTime(new Date(endTime));
            newActivity.setParamsInfo(paramsInfo);
            newActivity.setName(LocalMessages.ACTIVITY_NAME_7);
            this.activityDao.create(newActivity);
        }
        else {
            this.activityDao.updateInfo(type, new Date(startTime), new Date(endTime), paramsInfo);
        }
        this.playerDao.resetTotalTicketGold();
        if (startTime > System.currentTimeMillis()) {
            this.jobService.addJob("systemService", "initTicketActivityBegin", "", startTime, false);
        }
        if (endTime > System.currentTimeMillis()) {
            this.jobService.addJob("systemService", "initTicketActivityEnd", "", endTime, false);
        }
        SystemService.errorLogger.error("class:SystemService#method:initTicketActivity#success");
        return JsonBuilder.getJson(State.SUCCESS, "ok");
    }
    
    private Tuple<Boolean, String> checkTicketActivity(final long startTime, final long endTime, final String paramsInfo) {
        final Tuple<Boolean, String> result = new Tuple(false, "");
        final long curTime = System.currentTimeMillis();
        if (startTime <= 0L || endTime <= 0L || StringUtils.isBlank(paramsInfo)) {
            result.right = LocalMessages.TICKET_ACTIVITY_NULL_VALUE;
            return result;
        }
        if (startTime >= endTime || startTime <= curTime || endTime <= curTime) {
            result.right = LocalMessages.TICKET_ACTIVITY_INVALID_TIME;
            return result;
        }
        int maxNGold = 0;
        int maxRTicket = 0;
        final String[] rules = paramsInfo.trim().split(";");
        String[] array;
        for (int length = (array = rules).length, i = 0; i < length; ++i) {
            final String str = array[i];
            if (str.split(",").length != 2) {
                result.right = LocalMessages.TICKET_ACTIVITY_INVALID_RULES;
                return result;
            }
            try {
                final int needGold = Integer.parseInt(str.split(",")[0]);
                final int rewardTicket = Integer.parseInt(str.split(",")[1]);
                if (rewardTicket * 1.0 / needGold > 10.0) {
                    result.right = LocalMessages.TICKET_ACTIVITY_PROTECT;
                    return result;
                }
                if (needGold <= maxNGold || rewardTicket < maxRTicket) {
                    result.right = LocalMessages.TICKET_ACTIVITY_INVALID_RULES;
                    return result;
                }
                maxNGold = needGold;
                maxRTicket = rewardTicket;
            }
            catch (NumberFormatException e) {
                result.right = LocalMessages.TICKET_ACTIVITY_NUMBER_FORMAT_EXCEPTION;
                return result;
            }
        }
        result.left = true;
        return result;
    }
    
    @Override
    public void initTicketActivityBegin(final String param) {
        final Activity activity = this.activityDao.read(7);
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
                PayService.ticketMap.put(index++, new Tuple(Integer.parseInt(temp[0]), Integer.parseInt(temp[1])));
            }
            PayService.inTicket = true;
            final byte[] send = JsonBuilder.getSimpleJson("haveTicketActivity", 1);
            final Collection<PlayerDto> playerDtos = Players.getAllPlayer();
            for (final PlayerDto playerDto : playerDtos) {
                Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
    }
    
    @Override
    public void initTicketActivityEnd(final String param) {
        final Activity activity = this.activityDao.read(7);
        final Long time = System.currentTimeMillis();
        if (activity == null || time >= activity.getEndTime().getTime()) {
            PayService.inTicket = false;
            final byte[] send = JsonBuilder.getSimpleJson("haveTicketActivity", 0);
            final Collection<PlayerDto> playerDtos = Players.getAllPlayer();
            for (final PlayerDto playerDto : playerDtos) {
                Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, send);
            }
            PayService.ticketMap.clear();
        }
    }
    
    @Transactional
    @Override
    public byte[] banChat2(final String uid, final String cause, final long duration, final String yx) {
        if (StringUtils.isBlank(uid) || StringUtils.isBlank(cause) || duration <= 0L || StringUtils.isBlank(yx)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final List<Player> list = this.playerDao.getPlayerByUserId(uid, yx);
        if (list == null || list.size() < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10002);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("list");
        final Date nextSayTime = new Date(System.currentTimeMillis() + duration);
        for (final Player player : list) {
            if (player != null) {
                final Date nowDate = new Date();
                final int playerId = player.getPlayerId();
                Silence silence = this.silenceDao.getByPlayerId(playerId);
                if (silence != null) {
                    silence.setNextSayTime(nextSayTime);
                    silence.setSilenceTime(nowDate);
                    silence.setReason(cause);
                    silence.setType(2);
                    this.silenceDao.update(silence);
                }
                else {
                    silence = new Silence();
                    silence.setNextSayTime(nextSayTime);
                    silence.setPlayerId(playerId);
                    silence.setReason(cause);
                    silence.setSilenceTime(nowDate);
                    silence.setUserId(player.getUserId());
                    silence.setYx(yx);
                    silence.setType(2);
                    this.silenceDao.create(silence);
                }
                doc.startObject();
                doc.createElement("uid", silence.getUserId());
                doc.createElement("playerId", silence.getPlayerId());
                doc.createElement("playerName", player.getPlayerName());
                doc.createElement("playerLv", player.getPlayerLv());
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] unbanChat2(final String uid, final String yx) {
        if (StringUtils.isBlank(uid) || StringUtils.isBlank(yx)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final Date nowDate = new Date();
        final List<Player> list = this.playerDao.getPlayerByUserId(uid, yx);
        if (list == null || list.size() < 1) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_USER_10002);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("list");
        for (final Player player : list) {
            if (player != null) {
                final int playerId = player.getPlayerId();
                final Silence silence = this.silenceDao.getByPlayerId(playerId);
                if (silence == null) {
                    continue;
                }
                silence.setNextSayTime(nowDate);
                this.silenceDao.update(silence);
                doc.startObject();
                doc.createElement("uid", silence.getUserId());
                doc.createElement("playerId", silence.getPlayerId());
                doc.createElement("playerName", player.getPlayerName());
                doc.createElement("playerLv", player.getPlayerLv());
                doc.endObject();
            }
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] getBanRecord2(final String yx) {
        final Date now = new Date();
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("list");
        final List<Silence> silences = this.silenceDao.getByDateAndYx(now, yx, 2);
        for (final Silence silence : silences) {
            doc.startObject();
            doc.createElement("uid", silence.getUserId());
            doc.createElement("playerId", silence.getPlayerId());
            final Player player = this.playerDao.read(silence.getPlayerId());
            doc.createElement("playerName", player.getPlayerName());
            doc.createElement("playerLv", player.getPlayerLv());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public void blockReward(final int playerId, final int value) {
        if (value >= 3) {
            return;
        }
        final Player player = this.playerDao.read(playerId);
        final int food = 5000;
        this.playerResourceDao.addFoodIgnoreMax(playerId, food, "\u8f93\u5165\u9a8c\u8bc1\u7801\u83b7\u5f97\u7cae\u98df");
        this.chatService.sendSystemChat("SYS2ONE", playerId, player.getForceId(), MessageFormatter.format(LocalMessages.BLOCK_TIPS_1, new Object[] { food }), null);
    }
    
    @Override
    public byte[] rtblockByIds(final String playerIds) {
        final String[] strs = playerIds.split(",");
        int playerId = 0;
        Player player = null;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("list");
        String[] array;
        for (int length = (array = strs).length, i = 0; i < length; ++i) {
            final String str = array[i];
            playerId = Integer.valueOf(str);
            player = this.playerDao.read(playerId);
            if (player != null) {
                if (TimeSlice.getInstance().rtBlock(player.getPlayerId(), 0)) {
                    doc.startObject();
                    doc.createElement("playerId", player.getPlayerId());
                    doc.createElement("playerName", player.getPlayerName());
                    doc.endObject();
                    SystemService.drLog.info(LogUtil.formatValidateCode(player));
                }
            }
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public byte[] rtblockByNames(final String playerNames) {
        final String[] strs = playerNames.split(",");
        String playerName = null;
        Player player = null;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("list");
        String[] array;
        for (int length = (array = strs).length, i = 0; i < length; ++i) {
            final String str = playerName = array[i];
            player = this.playerDao.getPlayerByName(playerName);
            if (player != null) {
                if (TimeSlice.getInstance().rtBlock(player.getPlayerId(), 0)) {
                    doc.startObject();
                    doc.createElement("playerId", player.getPlayerId());
                    doc.createElement("playerName", player.getPlayerName());
                    doc.endObject();
                    SystemService.drLog.info(LogUtil.formatValidateCode(player));
                }
            }
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
}
