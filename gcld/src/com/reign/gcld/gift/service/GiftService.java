package com.reign.gcld.gift.service;

import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.chat.service.*;
import com.reign.gcld.player.dao.*;
import com.reign.gcld.tech.service.*;
import com.reign.gcld.sdata.cache.*;
import com.reign.gcld.gift.dao.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.util.*;
import org.apache.commons.lang.*;
import com.reign.gcld.world.common.*;
import org.springframework.transaction.annotation.*;
import com.reign.gcld.battle.domain.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.log.*;
import com.reign.gcld.player.domain.*;
import java.util.concurrent.*;
import java.util.*;
import com.reign.gcld.task.reward.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import java.net.*;
import com.reign.gcld.gift.domain.*;
import java.io.*;
import java.util.regex.*;
import com.alibaba.fastjson.*;

@Component("giftService")
public class GiftService implements IGiftService
{
    @Autowired
    private IPlayerAttributeDao playerAttributeDao;
    @Autowired
    private LoginRewardComboCache loginRewardComboCache;
    @Autowired
    private IChatService chatService;
    @Autowired
    private IPlayerDao playerDao;
    @Autowired
    private IPlayerResourceDao playerResourceDao;
    @Autowired
    private IDataGetter dataGetter;
    @Autowired
    private IGiftInfoDao giftInfoDao;
    @Autowired
    private IPlayerGiftDao playerGiftDao;
    @Autowired
    IGiftUuidDao giftUuidDao;
    @Autowired
    private LoginRewardBaseCache loginRewardBaseCache;
    @Autowired
    private HourlyRewardCache hourlyRewardCache;
    @Autowired
    private TechEffectCache techEffectCache;
    @Autowired
    private GiftCache giftCache;
    @Autowired
    private IPlayerOnlineRewardDao playerOnlineRewardDao;
    private static final Logger errorLogger;
    private static final Logger timerLog;
    
    static {
        errorLogger = CommonLog.getLog(GiftService.class);
        timerLog = new TimerLogger();
    }
    
    @Override
    public boolean haveDayGift(final PlayerAttribute playerAttribute) {
        if (playerAttribute.getLastGiftTime() == null) {
            return true;
        }
        final Date lastGiftTime = playerAttribute.getLastGiftTime();
        final long lastGiftTimeToMillsecond = lastGiftTime.getTime();
        final Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(1);
        final int month = calendar.get(2);
        final int day = calendar.get(5);
        calendar.set(year, month, day, 0, 0, 0);
        final long todayToMillsecond = calendar.getTimeInMillis();
        return lastGiftTimeToMillsecond < todayToMillsecond;
    }
    
    @Transactional
    @Override
    public byte[] getDayGift(final PlayerDto playerDto) {
        final PlayerAttribute pa = this.playerAttributeDao.read(playerDto.playerId);
        if (pa == null) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
        }
        final char[] cs = pa.getFunctionId().toCharArray();
        if (cs[38] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10020);
        }
        if (!this.haveDayGift(pa)) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        final LoginRewardCombo lrc = this.loginRewardComboCache.getProb(WebUtil.nextDouble());
        final String[] cardQuality = lrc.getCardQuality().split(",");
        doc.createElement("card_quality1", ((LoginRewardBase)this.loginRewardBaseCache.get((Object)Integer.parseInt(cardQuality[0]))).getPic());
        doc.createElement("card_quality2", ((LoginRewardBase)this.loginRewardBaseCache.get((Object)Integer.parseInt(cardQuality[1]))).getPic());
        doc.createElement("card_quality3", ((LoginRewardBase)this.loginRewardBaseCache.get((Object)Integer.parseInt(cardQuality[2]))).getPic());
        ITaskReward reward = lrc.getBaseReward();
        if (reward == null) {
            reward = lrc.getComboReward();
            if (StringUtils.isNotBlank(lrc.getPic()) && !lrc.equals("?")) {
                doc.createElement("pic", lrc.getPic());
                doc.createElement("special", 1);
            }
        }
        final Map<Integer, Reward> rewardMap = reward.rewardPlayer(playerDto, this.dataGetter, "\u767b\u9646\u5956\u52b1", false);
        for (final Integer key : rewardMap.keySet()) {
            final Reward reward2 = rewardMap.get(key);
            if (reward2.getType() == 19) {
                doc.createElement("gold", reward2.getNum());
            }
            else {
                if (reward2.getType() != 22) {
                    continue;
                }
                doc.createElement("worship", reward2.getNum());
            }
        }
        final int id = lrc.getId();
        if (id == 8 || id == 17 || id == 20 || id == 23 || id == 26) {
            final StringBuffer sb = new StringBuffer();
            for (final Integer key2 : rewardMap.keySet()) {
                final Reward reward3 = rewardMap.get(key2);
                sb.append(reward3.getName());
                sb.append("\u00d7");
                sb.append(reward3.getNum());
                sb.append("\uff0c");
            }
            final String msg = MessageFormatter.format(LocalMessages.DAY_GIFT_NOTICE, new Object[] { ColorUtil.getForceMsg(playerDto.forceId, String.valueOf(WorldCityCommon.nationIdNameMapDot.get(playerDto.forceId)) + playerDto.playerName), sb.substring(0, sb.length() - 1) });
            this.chatService.sendBigNotice("GLOBAL", null, msg, null);
        }
        doc.endObject();
        this.playerAttributeDao.updateLastGiftTime(playerDto.playerId, new Date());
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] getOnlineGiftNumber(final PlayerDto playerDto) {
        final JsonDocument doc = new JsonDocument();
        final Date now = new Date();
        if (playerDto.cs[39] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final int playerId = playerDto.playerId;
        final PlayerOnlineReward pa = this.playerOnlineRewardDao.read(playerId);
        final int beforeRemainOnlineNum = pa.getRemainOnlineNum();
        final int onlineNum = pa.getOnlineNum();
        if (beforeRemainOnlineNum == 0 && onlineNum == 0) {
            final long sNow = now.getTime();
            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(1);
            final int month = calendar.get(2);
            final int day = calendar.get(5);
            calendar.set(year, month, day, 23, 59, 59);
            final long sNext = calendar.getTimeInMillis() + 1000L + 32400000L;
            final long millseconds = sNext - sNow;
            doc.startObject();
            doc.createElement("remainNumber", 0);
            doc.createElement("number", 0);
            doc.createElement("millseconds", millseconds);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        if (onlineNum != 0) {
            doc.startObject();
            doc.createElement("number", onlineNum);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        final long sNow = now.getTime();
        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(11);
        if (23 == hour) {
            final int year2 = calendar.get(1);
            final int month2 = calendar.get(2);
            final int day2 = calendar.get(5);
            calendar.set(year2, month2, day2, 23, 59, 59);
            final long sNext2 = calendar.getTimeInMillis() + 1000L + 28800000L;
            final long millseconds2 = sNext2 - sNow;
            doc.startObject();
            doc.createElement("remainNumber", beforeRemainOnlineNum);
            doc.createElement("number", 0);
            doc.createElement("millseconds", millseconds2);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        if (hour < 8) {
            final int year2 = calendar.get(1);
            final int month2 = calendar.get(2);
            final int day2 = calendar.get(5);
            calendar.set(year2, month2, day2, 8, 59, 59);
            final long sNext2 = calendar.getTimeInMillis() + 1000L;
            final long millseconds2 = sNext2 - sNow;
            doc.startObject();
            doc.createElement("remainNumber", beforeRemainOnlineNum);
            doc.createElement("number", 0);
            doc.createElement("millseconds", millseconds2);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        final int year2 = calendar.get(1);
        final int month2 = calendar.get(2);
        final int day2 = calendar.get(5);
        calendar.set(year2, month2, day2, hour + 1, 0, 0);
        final long sNext2 = calendar.getTimeInMillis();
        final long millseconds2 = sNext2 - sNow;
        doc.startObject();
        doc.createElement("remainNumber", beforeRemainOnlineNum);
        doc.createElement("number", 0);
        doc.createElement("millseconds", millseconds2);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] getOnlineGift(final PlayerDto playerDto) {
        final int playerId = playerDto.playerId;
        final PlayerOnlineReward pa = this.playerOnlineRewardDao.read(playerId);
        if (playerDto.cs[39] != '1') {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final JsonDocument doc = new JsonDocument();
        final Date now = new Date();
        final int remainNum = pa.getRemainOnlineNum();
        final int onlineNum = pa.getOnlineNum();
        if (onlineNum <= 0) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10012);
        }
        final HourlyReward hr = this.hourlyRewardCache.getHourlyReward();
        final int type = hr.getId();
        final int food = (int)(hr.getRewardFood() * (1.0 + this.techEffectCache.getTechEffect(playerId, 42) / 100.0));
        this.playerResourceDao.addFoodIgnoreMax(playerId, food, "\u6574\u70b9\u5728\u7ebf\u5956\u52b1\u589e\u52a0\u7cae\u98df");
        this.playerOnlineRewardDao.useOnlineNum(playerId);
        if (4 == type) {
            final String msg = MessageFormatter.format(LocalMessages.ONLINE_GIFT_NOTICE, new Object[] { ColorUtil.getForceMsg(playerDto.forceId, String.valueOf(WorldCityCommon.nationIdNameMapDot.get(playerDto.forceId)) + this.playerDao.read(playerId).getPlayerName()), ColorUtil.getForceMsg(playerDto.forceId, String.valueOf(food) + LocalMessages.T_COMM_10017) });
            this.chatService.sendBigNotice("GLOBAL", null, msg, null);
        }
        if (remainNum == 0 && onlineNum - 1 == 0) {
            final long sNow = now.getTime();
            final Calendar calendar = Calendar.getInstance();
            final int year = calendar.get(1);
            final int month = calendar.get(2);
            final int day = calendar.get(5);
            calendar.set(year, month, day, 23, 59, 59);
            final long sNext = calendar.getTimeInMillis() + 1000L + 32400000L;
            final long millseconds = sNext - sNow;
            doc.startObject();
            doc.createElement("remainNumber", 0);
            doc.createElement("number", 0);
            doc.createElement("type", type);
            doc.createElement("rewardType", 3);
            doc.createElement("rewardNum", food);
            doc.createElement("millseconds", millseconds);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        if (onlineNum - 1 != 0) {
            doc.startObject();
            doc.createElement("number", onlineNum - 1);
            doc.createElement("type", type);
            doc.createElement("rewardType", 3);
            doc.createElement("rewardNum", food);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        final long sNow = now.getTime();
        final Calendar calendar = Calendar.getInstance();
        final int hour = calendar.get(11);
        if (23 == hour) {
            final int year2 = calendar.get(1);
            final int month2 = calendar.get(2);
            final int day2 = calendar.get(5);
            calendar.set(year2, month2, day2, 23, 59, 59);
            final long sNext2 = calendar.getTimeInMillis() + 1000L + 28800000L;
            final long millseconds2 = sNext2 - sNow;
            doc.startObject();
            doc.createElement("remainNumber", remainNum - 1);
            doc.createElement("number", 0);
            doc.createElement("type", type);
            doc.createElement("rewardType", 3);
            doc.createElement("rewardNum", food);
            doc.createElement("millseconds", millseconds2);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        if (hour < 8) {
            final int year2 = calendar.get(1);
            final int month2 = calendar.get(2);
            final int day2 = calendar.get(5);
            calendar.set(year2, month2, day2, 8, 59, 59);
            final long sNext2 = calendar.getTimeInMillis() + 1000L;
            final long millseconds2 = sNext2 - sNow;
            doc.startObject();
            doc.createElement("remainNumber", remainNum - 1);
            doc.createElement("number", 0);
            doc.createElement("type", type);
            doc.createElement("rewardType", 3);
            doc.createElement("rewardNum", food);
            doc.createElement("millseconds", millseconds2);
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        final int year2 = calendar.get(1);
        final int month2 = calendar.get(2);
        final int day2 = calendar.get(5);
        calendar.set(year2, month2, day2, hour + 1, 0, 0);
        final long sNext2 = calendar.getTimeInMillis();
        final long millseconds2 = sNext2 - sNow;
        doc.startObject();
        doc.createElement("remainNumber", remainNum - 1);
        doc.createElement("number", 0);
        doc.createElement("type", type);
        doc.createElement("rewardType", 3);
        doc.createElement("rewardNum", food);
        doc.createElement("millseconds", millseconds2);
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Override
    public void pushOnlineGift() {
        final long start = System.currentTimeMillis();
        final Collection<PlayerDto> onlinePlayerList = Players.getAllPlayer();
        JsonDocument doc = null;
        int playerId = 0;
        int onlineNum = 0;
        for (final PlayerDto onlinePlayer : onlinePlayerList) {
            try {
                playerId = onlinePlayer.playerId;
                doc = new JsonDocument();
                doc.startObject();
                boolean needPush = false;
                if (onlinePlayer.cs[39] == '1') {
                    this.playerOnlineRewardDao.addOnlineNum(playerId);
                    onlineNum = this.playerOnlineRewardDao.getOnlineNum(playerId);
                    if (onlineNum > 0) {
                        needPush = true;
                        doc.createElement("remainNumber", onlineNum);
                    }
                }
                final List<PlayerArmyReward> parList = this.dataGetter.getPlayerArmyRewardDao().getListByPlayerId(playerId);
                for (final PlayerArmyReward par : parList) {
                    if (par.getState() == 0) {
                        final ArmiesReward armiesReward = (ArmiesReward)this.dataGetter.getArmiesRewardCache().get((Object)par.getArmyId());
                        final General general = (General)this.dataGetter.getGeneralCache().get((Object)armiesReward.getChief());
                        final Troop troop = (Troop)this.dataGetter.getTroopCache().get((Object)general.getTroop());
                        if (BattleDrop.getDropType(troop.getDrop()) != 5) {
                            continue;
                        }
                        needPush = true;
                        doc.createElement("hasExp", true);
                    }
                }
                doc.endObject();
                if (!needPush) {
                    continue;
                }
                Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
            }
            catch (Exception e) {
                GiftService.errorLogger.error("#class:GiftService#method:pushOnlineGift#playerId:" + playerId + "#exception:", e);
            }
        }
        GiftService.timerLog.info(LogUtil.formatThreadLog("GiftService", "pushOnlineGift", 2, System.currentTimeMillis() - start, ""));
    }
    
    @Transactional
    @Override
    public void openOnlineGiftFunctin(final int playerId) {
        PlayerOnlineReward por = this.playerOnlineRewardDao.read(playerId);
        if (por == null) {
            por = new PlayerOnlineReward();
            por.setPlayerId(playerId);
            por.setRemainOnlineNum(8);
            por.setOnlineNum(1);
            this.playerOnlineRewardDao.create(por);
        }
        else {
            this.playerOnlineRewardDao.setOnlineGiftBaseData(playerId, 8, 1);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("onlineNum", 1);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_UPDATE, doc.toByte());
    }
    
    @Transactional
    @Override
    public boolean hasGift(final Player player) {
        try {
            final Date now = new Date();
            final int AllServerNum = this.giftInfoDao.getAllServerNum(player.getYx(), now, player.getCreateTime());
            final int receivedAllServerNum = this.playerGiftDao.getAllServerByPlayerId(player.getPlayerId());
            if (AllServerNum > receivedAllServerNum) {
                return true;
            }
            final List<PlayerGift> playerGiftList = this.playerGiftDao.getByPlayerId(player.getPlayerId());
            for (final PlayerGift playerGift : playerGiftList) {
                final Date expiredTime = this.giftInfoDao.read(playerGift.getGiftId()).getExpiredTime();
                if (playerGift.getReceived() == 0 && now.before(expiredTime)) {
                    return true;
                }
            }
            return false;
        }
        catch (Exception e) {
            GiftService.errorLogger.error("className:GiftService#methodName:hasGift");
            GiftService.errorLogger.error(e.getMessage());
            GiftService.errorLogger.error(this, e);
            return false;
        }
    }
    
    @Transactional
    @Override
    public byte[] getGiftInfo(final int playerId) {
        final Date now = new Date();
        final long nowMS = now.getTime();
        final ConcurrentMap<Integer, PlayerGift> allServerPlayerGift = new ConcurrentHashMap<Integer, PlayerGift>();
        final ConcurrentMap<Integer, PlayerGift> receivedPlayerGift = new ConcurrentHashMap<Integer, PlayerGift>();
        final ConcurrentMap<Integer, PlayerGift> playerGift = new ConcurrentHashMap<Integer, PlayerGift>();
        final ConcurrentMap<Integer, GiftInfo> playerGiftInfo = new ConcurrentHashMap<Integer, GiftInfo>();
        final List<PlayerGift> playerInfoList = this.playerGiftDao.getAllGiftByPlayerId(playerId);
        for (final PlayerGift pg : playerInfoList) {
            if (pg.getAllServer() == 1) {
                allServerPlayerGift.put(pg.getGiftId(), pg);
            }
            else if (pg.getReceived() == 1) {
                if (nowMS - pg.getReceivedTime().getTime() >= 259200000L) {
                    continue;
                }
                receivedPlayerGift.put(pg.getGiftId(), pg);
            }
            else {
                playerGift.put(pg.getGiftId(), pg);
            }
        }
        final Player player = this.playerDao.read(playerId);
        final Date createTime = player.getCreateTime();
        final List<GiftInfo> giftInfoList = this.giftInfoDao.getByYx(player.getYx());
        for (final GiftInfo pg2 : giftInfoList) {
            if (pg2.getAllServer() == 1) {
                if (!now.before(pg2.getExpiredTime()) || allServerPlayerGift.containsKey(pg2.getId())) {
                    continue;
                }
                if (pg2.getCurrentPlayer() == 1 && createTime.after(pg2.getSendTime())) {
                    continue;
                }
                playerGiftInfo.put(pg2.getId(), pg2);
            }
            else {
                if (!now.after(pg2.getExpiredTime()) || !playerGift.containsKey(pg2.getId())) {
                    continue;
                }
                playerGift.remove(pg2.getId());
            }
        }
        for (final Integer giftId : allServerPlayerGift.keySet()) {
            if (nowMS - allServerPlayerGift.get(giftId).getReceivedTime().getTime() > 259200000L) {
                allServerPlayerGift.remove(giftId);
            }
        }
        final Set<Integer> set = new HashSet<Integer>();
        set.addAll(playerGiftInfo.keySet());
        set.addAll(playerGift.keySet());
        final Set<Integer> receivedSet = new HashSet<Integer>();
        receivedSet.addAll(allServerPlayerGift.keySet());
        receivedSet.addAll(receivedPlayerGift.keySet());
        final ConcurrentMap<Integer, GiftInfo> allGift = new ConcurrentHashMap<Integer, GiftInfo>();
        for (final GiftInfo gi : giftInfoList) {
            allGift.put(gi.getId(), gi);
        }
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("gifts");
        int count = 0;
        for (final Integer giftId2 : set) {
            if (count >= 7) {
                break;
            }
            final GiftInfo gi2 = allGift.get(giftId2);
            if (gi2 == null) {
                continue;
            }
            doc.startObject();
            doc.createElement("id", giftId2);
            doc.createElement("giftName", gi2.getGiftName());
            doc.createElement("contents", this.getGiftContents(gi2.getContents()));
            doc.createElement("received", 0);
            doc.endObject();
            ++count;
        }
        for (final Integer giftId2 : receivedSet) {
            if (count >= 7) {
                break;
            }
            final GiftInfo gi2 = allGift.get(giftId2);
            if (gi2 == null) {
                continue;
            }
            doc.startObject();
            doc.createElement("id", giftId2);
            doc.createElement("giftName", gi2.getGiftName());
            doc.createElement("contents", this.getGiftContents(gi2.getContents()));
            doc.createElement("received", 1);
            doc.endObject();
            ++count;
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    @Transactional
    @Override
    public byte[] getGift(final PlayerDto playerDto, final int id) {
        final Date now = new Date();
        final GiftInfo giftInfo = this.giftInfoDao.read(id);
        final int playerId = playerDto.playerId;
        if (giftInfo == null || now.after(giftInfo.getExpiredTime())) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GIFT_ERROR_EXPIRED);
        }
        final String contents = giftInfo.getContents();
        final PlayerGift playerGift = this.playerGiftDao.getByPlayerIdAndGiftId(playerId, id);
        if (playerGift != null) {
            if (playerGift.getAllServer() == 1 || playerGift.getReceived() == 1) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.GIFT_ERROR_RECEIVED);
            }
            this.playerGiftDao.updateGift(playerGift.getId(), now);
        }
        else {
            if (1 != giftInfo.getAllServer() || !giftInfo.getYx().equals(playerDto.yx)) {
                return JsonBuilder.getJson(State.FAIL, LocalMessages.T_COMM_10011);
            }
            final PlayerGift pg = new PlayerGift();
            pg.setPlayerId(playerId);
            pg.setGiftId(id);
            pg.setReceived(1);
            pg.setReceivedTime(now);
            pg.setAllServer(1);
            this.playerGiftDao.create(pg);
        }
        final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(contents);
        final Map<Integer, Reward> rewardMap = reward.rewardPlayer(playerDto, this.dataGetter, "\u793c\u54c1\u5956\u52b1", false);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.startArray("rewards");
        for (final Reward temp : rewardMap.values()) {
            doc.startObject();
            doc.createElement("type", temp.getType());
            doc.createElement("value", temp.getNum());
            doc.endObject();
        }
        doc.endArray();
        doc.endObject();
        return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
    }
    
    private String getGiftContents(final String contents) {
        final StringBuffer sb = new StringBuffer();
        final String[] gifts = contents.split(";");
        String[] array;
        for (int length = (array = gifts).length, i = 0; i < length; ++i) {
            final String gift = array[i];
            final String[] temps = gift.split(",");
            sb.append(temps[1]);
            sb.append(this.reflect(temps[0]));
            sb.append(LocalMessages.CHINESE_COMMA);
        }
        return sb.substring(0, sb.length() - 1);
    }
    
    private String reflect(final String src) {
        if (StringUtils.isBlank(src)) {
            return "";
        }
        final String name = this.giftCache.getName(src);
        if (StringUtils.isNotBlank(name)) {
            return name;
        }
        if ("gold".equals(src)) {
            return LocalMessages.T_COMM_10009;
        }
        if ("copper".equals(src)) {
            return LocalMessages.T_COMM_10004;
        }
        if ("lumber".endsWith(src)) {
            return LocalMessages.T_COMM_10005;
        }
        if ("food".equals(src)) {
            return LocalMessages.T_COMM_10017;
        }
        if ("iron".equals(src)) {
            return LocalMessages.T_COMM_10018;
        }
        return "";
    }
    
    @Transactional
    @Override
    public byte[] getGiftByCode(final PlayerDto playerDto, final String code) {
        if (StringUtils.isBlank(code) || 32 != code.length()) {
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GIFT_CODE_ERROR);
        }
        final int playerId = playerDto.playerId;
        final Player player = this.playerDao.read(playerId);
        final StringBuffer arg = new StringBuffer();
        arg.append("server_name:");
        arg.append(Configuration.getProperty(player.getYx(), "gcld.servername"));
        arg.append("_");
        arg.append(Configuration.getProperty(player.getYx(), "gcld.serverids"));
        arg.append(";player_name:");
        arg.append(player.getPlayerName());
        arg.append(";gift_key:");
        arg.append(code);
        arg.append(";");
        GiftUuid gu = this.giftUuidDao.read(playerId);
        if (gu == null) {
            arg.append("uuid:0;");
        }
        else {
            arg.append("uuid:" + gu.getUuid() + ";");
        }
        arg.append("sign:");
        arg.append(MD5SecurityUtil.code(String.valueOf(player.getPlayerName()) + Configuration.getProperty("gcld.gift.key")));
        arg.append(";|");
        try {
            final Socket s = new Socket(Configuration.getProperty("gcld.gift.ip"), Configuration.getIntProperty("gcld.gift.port"));
            s.setSoTimeout(3000);
            String response = "";
            final OutputStream out = s.getOutputStream();
            out.write(arg.toString().getBytes("UTF-8"));
            out.flush();
            final BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            do {
                response = br.readLine();
                if (response == null) {
                    return JsonBuilder.getJson(State.FAIL, LocalMessages.GIFT_INNER_ERROR);
                }
            } while ((!StringUtils.isNotBlank(response) || !response.contains("gift_content")) && !response.contains(LocalMessages.GIFT));
            if (response.contains(LocalMessages.GIFT)) {
                s.close();
                return JsonBuilder.getJson(State.FAIL, response);
            }
            final JsonDocument doc = new JsonDocument();
            int uuid = 0;
            String expression = "";
            Pattern pattern = Pattern.compile("uuid:([0-9]*);");
            Matcher mat = pattern.matcher(response);
            if (mat.find()) {
                uuid = Integer.valueOf(mat.group(1));
            }
            pattern = Pattern.compile("gift_content:(.+?);");
            mat = pattern.matcher(response);
            if (mat.find()) {
                expression = mat.group(1);
            }
            if (gu == null) {
                gu = new GiftUuid();
                gu.setPlayerId(playerId);
                gu.setUuid(uuid);
                this.giftUuidDao.create(gu);
            }
            else {
                this.giftUuidDao.modifyUuid(playerId, uuid);
            }
            final JSONObject json = (JSONObject)JSON.parse(expression);
            final StringBuffer gift = new StringBuffer();
            this.handleBaseGift(json, gift, "gold");
            this.handleBaseGift(json, gift, "silver");
            this.handleBaseGift(json, gift, "wooden");
            this.handleBaseGift(json, gift, "food");
            this.handleBaseGift(json, gift, "iron");
            final JSONArray goodsArray = json.getJSONArray("goods");
            if (goodsArray != null && !goodsArray.isEmpty()) {
                for (final String key : this.giftCache.getKeys()) {
                    this.parseGoods(gift, goodsArray, key);
                }
            }
            String giftContents = StringUtils.replace(gift.toString(), "silver", "copper");
            giftContents = StringUtils.replace(giftContents, "wooden", "lumber");
            final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(giftContents.substring(0, giftContents.length() - 1));
            final Map<Integer, Reward> rewardMap = reward.rewardPlayer(playerDto, this.dataGetter, "\u793c\u54c1\u7801\u589e\u52a0", false);
            out.write(("uuid:" + uuid + ";gift_accepted;|").getBytes("UTF-8"));
            out.flush();
            s.close();
            doc.startObject();
            doc.startArray("rewards");
            for (final Reward temp : rewardMap.values()) {
                doc.startObject();
                doc.createElement("type", temp.getType());
                doc.createElement("value", temp.getNum());
                doc.endObject();
            }
            doc.endArray();
            doc.endObject();
            return JsonBuilder.getJson(State.SUCCESS, doc.toByte());
        }
        catch (Exception e) {
            GiftService.errorLogger.error("getGiftByCode ", e);
            return JsonBuilder.getJson(State.FAIL, LocalMessages.GIFT_CODE_RECEIVED_ERROR);
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
    
    private void handleBaseGift(final JSONObject json, final StringBuffer gift, final String key) {
        final String temp = json.getString(key);
        if (StringUtils.isNotBlank(temp)) {
            gift.append(key);
            gift.append(",");
            gift.append(temp);
            gift.append(";");
        }
    }
    
    @Override
    public void pushDayGift() {
        final long start = System.currentTimeMillis();
        this.playerOnlineRewardDao.resetOnlineGiftData(8);
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("haveDayGift", true);
        doc.endObject();
        final byte[] send = doc.toByte();
        final Collection<PlayerDto> playerDtos = Players.getAllPlayer();
        char[] cs = null;
        for (final PlayerDto playerDto : playerDtos) {
            try {
                cs = playerDto.cs;
                if (cs[38] != '1') {
                    continue;
                }
                Players.push(playerDto.playerId, PushCommand.PUSH_UPDATE, send);
            }
            catch (Exception e) {
                GiftService.errorLogger.error("class:giftService#method:pushDayGift#exception:", e);
            }
        }
        GiftService.timerLog.info(LogUtil.formatThreadLog("GiftService", "pushDayGift", 2, System.currentTimeMillis() - start, ""));
    }
}
