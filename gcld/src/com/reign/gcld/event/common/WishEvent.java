package com.reign.gcld.event.common;

import com.reign.gcld.common.log.*;
import com.reign.util.*;
import com.reign.gcld.log.*;
import java.util.concurrent.*;
import org.apache.commons.lang.*;
import com.reign.gcld.event.domain.*;
import com.reign.gcld.slave.util.*;
import com.reign.gcld.task.reward.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import com.reign.gcld.player.dto.*;
import java.util.*;

public class WishEvent extends DefaultEvent
{
    private static final Logger timerLog;
    private static Map<Integer, String> baseTypeMap;
    public static int LENGTH;
    public static int BITS;
    private static Map<Integer, Tuple4<Tuple<Integer, Integer>, Tuple<Integer, Integer>, Tuple<Integer, Integer>, Tuple<Integer, Integer>>> baseMap;
    private static Map<Integer, Tuple<Integer, Integer>> rateMap;
    private static int RATE_LENGTH;
    public static final int GOLD = 88;
    private static Map<String, String> baseRewadMap;
    private static Map<String, String> goldRewardMap;
    private static Map<Integer, Integer> rateCacheMap;
    private static Map<Integer, Boolean> rewardRateBolleanMap;
    public static long START_RECEIVED_DAY_MS;
    public static Map<Integer, String> templeMap;
    public static String[] messageList;
    public static int msgIndex;
    private IDataGetter dataGetter;
    
    static {
        timerLog = new TimerLogger();
        (WishEvent.baseTypeMap = new HashMap<Integer, String>()).put(1, "ChiefExp,");
        WishEvent.baseTypeMap.put(2, "iron,");
        WishEvent.baseTypeMap.put(3, "freeNiubiQuenchingTimes,");
        WishEvent.baseTypeMap.put(4, "ticket,");
        WishEvent.LENGTH = WishEvent.baseTypeMap.size();
        WishEvent.BITS = 0;
        for (int i = 0; i < WishEvent.LENGTH; ++i) {
            WishEvent.BITS += (int)Math.pow(2.0, i);
        }
        (WishEvent.baseMap = new HashMap<Integer, Tuple4<Tuple<Integer, Integer>, Tuple<Integer, Integer>, Tuple<Integer, Integer>, Tuple<Integer, Integer>>>()).put(1, new Tuple4(new Tuple(150000, 300000), new Tuple(8000, 15000), new Tuple(15, 25), new Tuple(10000, 15000)));
        WishEvent.baseMap.put(2, new Tuple4(new Tuple(50000, 100000), new Tuple(20000, 40000), new Tuple(15, 25), new Tuple(10000, 15000)));
        WishEvent.baseMap.put(3, new Tuple4(new Tuple(50000, 100000), new Tuple(8000, 15000), new Tuple(30, 50), new Tuple(10000, 15000)));
        WishEvent.baseMap.put(4, new Tuple4(new Tuple(50000, 100000), new Tuple(8000, 15000), new Tuple(15, 25), new Tuple(20000, 35000)));
        (WishEvent.rateMap = new HashMap<Integer, Tuple<Integer, Integer>>()).put(1, new Tuple(50, 1));
        WishEvent.rateMap.put(2, new Tuple(60, 1));
        WishEvent.rateMap.put(3, new Tuple(70, 3));
        WishEvent.rateMap.put(4, new Tuple(80, 8));
        WishEvent.rateMap.put(5, new Tuple(90, 16));
        WishEvent.rateMap.put(6, new Tuple(100, 20));
        WishEvent.rateMap.put(7, new Tuple(Integer.MAX_VALUE, 24));
        WishEvent.RATE_LENGTH = WishEvent.rateMap.size();
        WishEvent.baseRewadMap = new ConcurrentHashMap<String, String>();
        WishEvent.goldRewardMap = new ConcurrentHashMap<String, String>();
        WishEvent.rateCacheMap = new ConcurrentHashMap<Integer, Integer>();
        (WishEvent.rewardRateBolleanMap = new HashMap<Integer, Boolean>()).put(1, true);
        WishEvent.rewardRateBolleanMap.put(2, true);
        WishEvent.rewardRateBolleanMap.put(3, false);
        WishEvent.rewardRateBolleanMap.put(4, false);
        WishEvent.START_RECEIVED_DAY_MS = 3 * Constants.ONE_DAY_MS;
        (WishEvent.templeMap = new HashMap<Integer, String>()).put(1, LocalMessages.WISH_NOTICE_1);
        WishEvent.templeMap.put(2, LocalMessages.WISH_NOTICE_2);
        WishEvent.templeMap.put(3, LocalMessages.WISH_NOTICE_3);
        WishEvent.templeMap.put(4, LocalMessages.WISH_NOTICE_4);
        WishEvent.messageList = new String[10];
        WishEvent.msgIndex = 0;
    }
    
    public static String getGoldRewardNumString(final int playerLv, final int index, final int id) {
        final String key = String.valueOf(playerLv) + "_" + index + "_" + id;
        String goldReward = WishEvent.goldRewardMap.get(key);
        if (StringUtils.isNotBlank(goldReward)) {
            return goldReward;
        }
        final Tuple4<Tuple<Integer, Integer>, Tuple<Integer, Integer>, Tuple<Integer, Integer>, Tuple<Integer, Integer>> tuple4 = WishEvent.baseMap.get(index);
        goldReward = WishEvent.baseTypeMap.get(id);
        int value = getTuple(tuple4, id).right;
        if (WishEvent.rewardRateBolleanMap.get(id)) {
            value *= getRate(playerLv);
        }
        goldReward = String.valueOf(goldReward) + String.valueOf(value);
        WishEvent.goldRewardMap.put(key, goldReward);
        return goldReward;
    }
    
    public static String getBaseRewardNumString(final int playerLv, final int index, final int id) {
        final String key = String.valueOf(playerLv) + "_" + index + "_" + id;
        String baseReward = WishEvent.baseRewadMap.get(key);
        if (StringUtils.isNotBlank(baseReward)) {
            return baseReward;
        }
        final Tuple4<Tuple<Integer, Integer>, Tuple<Integer, Integer>, Tuple<Integer, Integer>, Tuple<Integer, Integer>> tuple4 = WishEvent.baseMap.get(index);
        baseReward = WishEvent.baseTypeMap.get(id);
        int value = getTuple(tuple4, id).left;
        if (WishEvent.rewardRateBolleanMap.get(id)) {
            value *= getRate(playerLv);
        }
        baseReward = String.valueOf(baseReward) + String.valueOf(value);
        WishEvent.baseRewadMap.put(key, baseReward);
        return baseReward;
    }
    
    private static Tuple<Integer, Integer> getTuple(final Tuple4<Tuple<Integer, Integer>, Tuple<Integer, Integer>, Tuple<Integer, Integer>, Tuple<Integer, Integer>> tuple4, final int i) {
        if (1 == i) {
            return tuple4.get_1();
        }
        if (2 == i) {
            return tuple4.get_2();
        }
        if (3 == i) {
            return tuple4.get_3();
        }
        if (4 == i) {
            return tuple4.get_4();
        }
        return null;
    }
    
    private static int getRate(final int playerLv) {
        Integer rate = WishEvent.rateCacheMap.get(playerLv);
        if (rate != null) {
            return rate;
        }
        rate = 0;
        for (int i = 1; i <= WishEvent.RATE_LENGTH; ++i) {
            final Tuple<Integer, Integer> tuple = WishEvent.rateMap.get(i);
            if (playerLv <= tuple.left) {
                rate = tuple.right;
                break;
            }
        }
        WishEvent.rateCacheMap.put(playerLv, rate);
        return rate;
    }
    
    public WishEvent(final int eventId, final Date startTime, final Date endTime, final String paramInfo, final IDataGetter dataGetter) {
        super(eventId, startTime, endTime, paramInfo);
        this.dataGetter = dataGetter;
    }
    
    @Override
    public void handleOperation(final int type, final int playerId, final int val) {
        if (type == 17) {
            WishEvent.timerLog.error("class:WishEvent#method:handleOperation#type:" + type + "#playerId:" + playerId + "#val:" + val);
            PlayerEvent playerEvent = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 17);
            if (playerEvent == null) {
                playerEvent = this.initWishEvent(playerId);
                this.dataGetter.getPlayerEventDao().create(playerEvent);
            }
        }
    }
    
    private PlayerEvent initWishEvent(final int playerId) {
        final PlayerEvent pe = new PlayerEvent();
        pe.setEventId(17);
        pe.setPlayerId(playerId);
        pe.setParam1(0);
        pe.setParam2(0);
        pe.setParam3(0);
        pe.setParam4(0);
        pe.setParam5(0);
        pe.setParam6(0);
        pe.setParam7(0);
        pe.setParam8(0);
        pe.setParam9(0);
        pe.setParam10(0);
        return pe;
    }
    
    public void buildJson(final JsonDocument doc, final PlayerDto playerDto, PlayerEvent pe) {
        doc.createElement("eventId", this.eventId);
        doc.createElement("eventCd", this.getEventCD());
        if (pe == null) {
            this.handleOperation(this.eventId, playerDto.playerId, 0);
            pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerDto.playerId, this.eventId);
        }
        final boolean isGoldWish = pe.getParam2() == 2;
        doc.startArray("wishs");
        for (int i = 1; i <= WishEvent.LENGTH; ++i) {
            doc.startObject();
            doc.createElement("id", i);
            doc.startArray("rewards");
            for (int j = 1; j <= WishEvent.LENGTH; ++j) {
                doc.startObject();
                doc.createElement("id", j);
                doc.createElement("received", SlaveUtil.hasReward(pe.getParam4(), j));
                doc.startArray("rewardArr");
                String rewards = null;
                if (isGoldWish) {
                    rewards = getGoldRewardNumString(playerDto.playerLv, i, j);
                }
                else {
                    rewards = getBaseRewardNumString(playerDto.playerLv, i, j);
                }
                final ITaskReward taskReward = TaskRewardFactory.getInstance().getTaskReward(rewards);
                final Map<Integer, Reward> rewardMap = taskReward.getReward(playerDto, this.dataGetter, null);
                for (final Reward reward : rewardMap.values()) {
                    doc.startObject();
                    doc.createElement("type", reward.getType());
                    doc.createElement("value", reward.getNum());
                    doc.endObject();
                }
                doc.endArray();
                doc.endObject();
            }
            doc.endArray();
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("gold", 88);
        final int status = pe.getParam2();
        doc.createElement("status", status);
        doc.createElement("received", pe.getParam3() > 0 && pe.getParam4() >= pe.getParam3());
        if (status > 0) {
            final long cd = this.getEventCD() - WishEvent.START_RECEIVED_DAY_MS;
            doc.createElement("cd", (cd < 0L) ? 0L : cd);
        }
        doc.createElement("selectId", pe.getParam1());
    }
    
    @Override
    public void startEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveWishActivity", 1);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[10] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
    }
    
    @Override
    public void overEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveWishActivity", 0);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[10] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        final List<PlayerEvent> peList = this.dataGetter.getPlayerEventDao().getWishRewardList(17);
        for (final PlayerEvent pe : peList) {
            try {
                final int playerId = pe.getPlayerId();
                PlayerDto playerDto = Players.getPlayer(playerId);
                if (playerDto == null) {
                    playerDto = PlayerDtoUtil.getPlayerDto(this.dataGetter.getPlayerDao().read(playerId), this.dataGetter.getPlayerAttributeDao().read(playerId));
                }
                final int playerLv = playerDto.playerLv;
                final StringBuffer msg = new StringBuffer(LocalMessages.WISH_MAIL_CONTENT);
                final int select = pe.getParam2();
                String rewardStr = "";
                if (1 == select) {
                    for (int i = 1; i <= WishEvent.LENGTH; ++i) {
                        if (SlaveUtil.hasReward(pe.getParam3(), i) == 1 && SlaveUtil.hasReward(pe.getParam4(), i) == 0) {
                            rewardStr = String.valueOf(rewardStr) + getBaseRewardNumString(playerLv, pe.getParam1(), i);
                            rewardStr = String.valueOf(rewardStr) + ";";
                        }
                    }
                    rewardStr = rewardStr.substring(0, rewardStr.length() - 1);
                }
                else if (2 == select) {
                    for (int i = 1; i <= WishEvent.LENGTH; ++i) {
                        if (SlaveUtil.hasReward(pe.getParam3(), i) == 1 && SlaveUtil.hasReward(pe.getParam4(), i) == 0) {
                            rewardStr = String.valueOf(rewardStr) + getGoldRewardNumString(playerLv, pe.getParam1(), i);
                            rewardStr = String.valueOf(rewardStr) + ";";
                        }
                    }
                    rewardStr = rewardStr.substring(0, rewardStr.length() - 1);
                }
                final ITaskReward reward = TaskRewardFactory.getInstance().getTaskReward(rewardStr);
                final Map<Integer, Reward> map = reward.rewardPlayer(playerDto, this.dataGetter, "\u65b0\u5e74\u8bb8\u613f\u6d3b\u52a8\u5956\u52b1", null);
                for (final Reward temp : map.values()) {
                    msg.append(temp.getName());
                    msg.append(temp.getNum());
                    msg.append("\uff0c");
                }
                msg.setLength(msg.length() - 1);
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.WISH_MAIL_HEAD, msg.toString(), 1, playerId, 0);
            }
            catch (Exception e) {
                WishEvent.timerLog.error("class:WishEvent#method:overEvent#playerId:" + pe.getPlayerId() + "#exception:", e);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
    }
}
