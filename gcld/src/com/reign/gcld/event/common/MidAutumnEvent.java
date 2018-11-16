package com.reign.gcld.event.common;

import java.util.concurrent.locks.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import com.reign.gcld.event.domain.*;
import com.reign.gcld.slave.util.*;
import com.reign.gcld.activity.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.player.dto.*;
import java.util.*;

public class MidAutumnEvent extends DefaultEvent
{
    private static final Logger log;
    private static final Logger timerLog;
    public static Map<Integer, Integer> limitMap;
    private static Map<Integer, Integer> lvMoonCakeMap;
    private static int LENGTH;
    public static int MAX_NUM;
    public static Map<Integer, Integer> rewardMap;
    public static int TICKET;
    public static int IRON;
    public static int FOOD;
    public static int IRON_TOKEN_INDEX;
    public static ReentrantLock[] locks;
    public static final int LOCKS_LEN;
    private IDataGetter dataGetter;
    
    static {
        log = CommonLog.getLog(MidAutumnEvent.class);
        timerLog = new TimerLogger();
        (MidAutumnEvent.limitMap = new HashMap<Integer, Integer>()).put(1, 400);
        MidAutumnEvent.limitMap.put(2, 800);
        MidAutumnEvent.limitMap.put(3, 1200);
        (MidAutumnEvent.lvMoonCakeMap = new HashMap<Integer, Integer>()).put(1, 20);
        MidAutumnEvent.lvMoonCakeMap.put(2, 50);
        MidAutumnEvent.lvMoonCakeMap.put(3, 100);
        MidAutumnEvent.lvMoonCakeMap.put(4, 200);
        MidAutumnEvent.lvMoonCakeMap.put(5, 400);
        MidAutumnEvent.LENGTH = MidAutumnEvent.lvMoonCakeMap.size();
        MidAutumnEvent.MAX_NUM = 999;
        (MidAutumnEvent.rewardMap = new HashMap<Integer, Integer>()).put(1, 200000);
        MidAutumnEvent.rewardMap.put(2, 300000);
        MidAutumnEvent.rewardMap.put(3, 50);
        MidAutumnEvent.rewardMap.put(4, 10);
        MidAutumnEvent.rewardMap.put(5, 5);
        MidAutumnEvent.TICKET = 9000;
        MidAutumnEvent.IRON = 90000;
        MidAutumnEvent.FOOD = 900000;
        MidAutumnEvent.IRON_TOKEN_INDEX = 1341;
        MidAutumnEvent.locks = new ReentrantLock[10240];
        LOCKS_LEN = MidAutumnEvent.locks.length;
        for (int i = 0; i < MidAutumnEvent.LOCKS_LEN; ++i) {
            MidAutumnEvent.locks[i] = new ReentrantLock(false);
        }
    }
    
    public MidAutumnEvent(final int eventId, final Date startTime, final Date endTime, final String paramInfo, final IDataGetter dataGetter) {
        super(eventId, startTime, endTime, paramInfo);
        this.dataGetter = dataGetter;
    }
    
    @Override
    public void handleOperation(final int type, final int playerId, final int val) {
        if (type == 10) {
            try {
                MidAutumnEvent.locks[playerId % MidAutumnEvent.LOCKS_LEN].lock();
                MidAutumnEvent.timerLog.error("class:MidAutumnEvent#method:handleOperation#type:" + type + "#playerId:" + playerId + "#val:" + val);
                PlayerEvent playerEvent = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 10);
                if (playerEvent == null) {
                    playerEvent = this.initMidAutumnEvent(playerId);
                    this.dataGetter.getPlayerEventDao().create(playerEvent);
                }
                final int dayth = this.getDayth();
                final int limit = this.getLimt(dayth);
                final int before = playerEvent.getParam1();
                if (limit <= 0 || before >= limit) {
                    return;
                }
                final int after = (before + val >= limit) ? limit : (before + val);
                final int currentNum = playerEvent.getParam2() + after - before;
                final int bits = this.getBit(currentNum);
                MidAutumnEvent.timerLog.error("class:MidAutumnEvent#method:handleOperation#type:" + type + "#playerId:" + playerId + "#val:" + val + "#before:" + before + "#after:" + after);
                this.dataGetter.getPlayerEventDao().updateInfo2(playerId, type, after, currentNum, bits);
            }
            finally {
                MidAutumnEvent.locks[playerId % MidAutumnEvent.LOCKS_LEN].unlock();
            }
            MidAutumnEvent.locks[playerId % MidAutumnEvent.LOCKS_LEN].unlock();
        }
    }
    
    private PlayerEvent initMidAutumnEvent(final int playerId) {
        final PlayerEvent pe = new PlayerEvent();
        pe.setEventId(10);
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
        final int param1 = pe.getParam1();
        final int param2 = pe.getParam2();
        final int param3 = pe.getParam3();
        final int param4 = pe.getParam4();
        final int param5 = pe.getParam5();
        doc.createElement("totalNum", param1);
        doc.createElement("num", param2);
        doc.createElement("needNum", MidAutumnEvent.MAX_NUM);
        doc.startArray("gifts");
        for (int i = 1; i <= MidAutumnEvent.LENGTH; ++i) {
            doc.startObject();
            doc.createElement("giftId", i);
            final int needNum = MidAutumnEvent.lvMoonCakeMap.get(i);
            doc.createElement("needNum", needNum);
            int state = 0;
            if (param2 >= needNum) {
                if (1 == SlaveUtil.hasReward(param3, i) && 1 == SlaveUtil.hasReward(param4, i)) {
                    state = 1;
                }
            }
            else {
                state = 2;
            }
            doc.createElement("state", state);
            doc.createElement("type", this.getRewardType(i));
            doc.createElement("value", MidAutumnEvent.rewardMap.get(i));
            doc.endObject();
        }
        doc.endArray();
        int count = 0;
        doc.startArray("cities");
        final int cityId = MiddleAutumnCache.getInstance().getCurrentCityId(playerDto.forceId);
        if (cityId > 0) {
            final WorldCity wc = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)cityId);
            if (wc != null) {
                doc.startObject();
                doc.createElement("cityId", cityId);
                doc.createElement("cityName", wc.getName());
                doc.endObject();
                ++count;
            }
        }
        doc.endArray();
        long cd = 0L;
        if (count == 0) {
            cd = MiddleAutumnCache.getInstance().getNextMoonCakeTime(playerDto.forceId) - System.currentTimeMillis();
        }
        doc.createElement("cd", cd);
        doc.createElement("ticket", MidAutumnEvent.TICKET);
        doc.createElement("iron", MidAutumnEvent.IRON);
        doc.createElement("food", MidAutumnEvent.FOOD);
        doc.createElement("canReceived", param1 >= MidAutumnEvent.MAX_NUM && param5 == 0);
    }
    
    @Override
    public void startEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveMidAutumnActivity", 1);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[10] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
        this.dataGetter.getJobService().addJob("timerBattleService", "addMoonCakeArmyForThreeCountry", "", System.currentTimeMillis(), false);
        this.dataGetter.getJobService().addJob("eventService", "moonCakeTimeTask", "1", TimeUtil.getNext1Day0Clock(), false);
        this.dataGetter.getJobService().addJob("eventService", "moonCakeTimeTask", "2", TimeUtil.getNext2Day0Clock(), false);
        MidAutumnEvent.timerLog.error("class:MidAutumnEvent#method:startEvent#1-day-job_time:" + new Date(TimeUtil.getNext1Day0Clock()));
        MidAutumnEvent.timerLog.error("class:MidAutumnEvent#method:startEvent#2-day-job_time:" + new Date(TimeUtil.getNext2Day0Clock()));
    }
    
    @Override
    public void overEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveMidAutumnActivity", 0);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[10] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        final List<PlayerEvent> peList = this.dataGetter.getPlayerEventDao().getPlayerEventList(this.eventId);
        for (final PlayerEvent playerEvent : peList) {
            try {
                final int param1 = playerEvent.getParam1();
                final int param2 = playerEvent.getParam3();
                final int param3 = playerEvent.getParam4();
                final int param4 = playerEvent.getParam5();
                int food = 0;
                int dstq = 0;
                int phantom = 0;
                int token = 0;
                if (1 == SlaveUtil.hasReward(param2, 1) && SlaveUtil.hasReward(param3, 1) == 0) {
                    food += MidAutumnEvent.rewardMap.get(1);
                }
                if (1 == SlaveUtil.hasReward(param2, 2) && SlaveUtil.hasReward(param3, 2) == 0) {
                    food += MidAutumnEvent.rewardMap.get(2);
                }
                if (1 == SlaveUtil.hasReward(param2, 3) && SlaveUtil.hasReward(param3, 3) == 0) {
                    dstq += MidAutumnEvent.rewardMap.get(3);
                }
                if (1 == SlaveUtil.hasReward(param2, 4) && SlaveUtil.hasReward(param3, 4) == 0) {
                    phantom += MidAutumnEvent.rewardMap.get(4);
                }
                if (1 == SlaveUtil.hasReward(param2, 5) && SlaveUtil.hasReward(param3, 5) == 0) {
                    token += MidAutumnEvent.rewardMap.get(5);
                }
                final int playerId = playerEvent.getPlayerId();
                final StringBuffer sb = new StringBuffer(LocalMessages.MID_AUTUMU_ACTIVITY_MAIL_CONTENT_HEAD);
                int count = 0;
                if (food > 0) {
                    this.dataGetter.getPlayerResourceDao().addFoodIgnoreMax(playerId, food, "\u4e2d\u79cb\u793c\u5305\u83b7\u5f97\u7cae\u98df");
                    if (count > 0) {
                        sb.append("\uff0c");
                    }
                    ++count;
                    sb.append(MessageFormatter.format(LocalMessages.MID_AUTUMU_ACTIVITY_MAIL_CONTENT_FOOD, new Object[] { food }));
                }
                if (dstq > 0) {
                    this.dataGetter.getStoreHouseService().gainSearchItems(106, dstq, PlayerDtoUtil.getPlayerDto(this.dataGetter.getPlayerDao().read(playerId), this.dataGetter.getPlayerAttributeDao().read(playerId)), "\u4e2d\u79cb\u793c\u5305\u83b7\u5f97\u4e39\u4e66\u94c1\u5238");
                    if (count > 0) {
                        sb.append("\uff0c");
                    }
                    ++count;
                    sb.append(MessageFormatter.format(LocalMessages.MID_AUTUMU_ACTIVITY_MAIL_CONTENT_DSTQ, new Object[] { dstq }));
                }
                if (phantom > 0) {
                    this.dataGetter.getPlayerBattleAttributeDao().addVip3PhantomCount(playerId, phantom, "\u4e2d\u79cb\u793c\u5305\u83b7\u5f97\u514d\u8d39\u501f\u5175\u6b21\u6570");
                    if (count > 0) {
                        sb.append("\uff0c");
                    }
                    ++count;
                    sb.append(MessageFormatter.format(LocalMessages.MID_AUTUMU_ACTIVITY_MAIL_CONTENT_PHANTOM, new Object[] { phantom }));
                }
                if (token > 0) {
                    this.dataGetter.getStoreHouseService().gainItems(playerId, token, MidAutumnEvent.IRON_TOKEN_INDEX, "\u4e2d\u79cb\u793c\u5305\u83b7\u5f97\u9554\u94c1\u4ee4");
                    if (count > 0) {
                        sb.append("\uff0c");
                    }
                    ++count;
                    sb.append(MessageFormatter.format(LocalMessages.MID_AUTUMU_ACTIVITY_MAIL_CONTENT_IRON_TOKEN, new Object[] { token }));
                }
                if (count > 0) {
                    this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.MID_AUTUMU_ACTIVITY_MAIL_TITLE, sb.toString(), 1, playerId, 0);
                }
                if (param1 < MidAutumnEvent.MAX_NUM || param4 != 0) {
                    continue;
                }
                this.dataGetter.getPlayerTicketsDao().addTickets(playerId, MidAutumnEvent.TICKET, "\u4e2d\u79cb\u5927\u793c\u5305\u83b7\u5f97\u70b9\u5238", true);
                this.dataGetter.getPlayerResourceDao().addIronIgnoreMax(playerId, MidAutumnEvent.IRON, "\u4e2d\u79cb\u5927\u793c\u5305\u83b7\u53d6\u9554\u94c1", true);
                this.dataGetter.getPlayerResourceDao().addFoodIgnoreMax(playerId, MidAutumnEvent.FOOD, "\u4e2d\u79cb\u5927\u793c\u5305\u83b7\u5f97\u7cae\u98df");
                final String mailMsg = MessageFormatter.format(LocalMessages.MID_AUTUMU_ACTIVITY_MAIL_BIG_CONTENT, new Object[] { MidAutumnEvent.MAX_NUM, MidAutumnEvent.TICKET, MidAutumnEvent.IRON, MidAutumnEvent.FOOD });
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.MID_AUTUMU_ACTIVITY_MAIL_TITLE, mailMsg, 1, playerId, 0);
            }
            catch (Exception e) {
                MidAutumnEvent.log.error("class:MidAutumnEvent#method:overEvent#playerId:" + playerEvent.getPlayerId(), e);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
    }
    
    public int getLimt(final int dayth) {
        final Integer result = MidAutumnEvent.limitMap.get(dayth);
        if (result == null) {
            MidAutumnEvent.log.error("class:MidAutumnEvent#method:getLimt#dayth:" + dayth);
            return 0;
        }
        return result;
    }
    
    public int getBit(final int moonCake) {
        int result = 0;
        for (int i = 1; i <= MidAutumnEvent.LENGTH && moonCake >= MidAutumnEvent.lvMoonCakeMap.get(i); ++i) {
            result |= (int)Math.pow(2.0, i - 1);
        }
        return result;
    }
    
    private String getRewardType(final int giftId) {
        if (1 == giftId || 2 == giftId) {
            return "food";
        }
        if (3 == giftId) {
            return "dstq";
        }
        if (4 == giftId) {
            return "phantom";
        }
        if (5 == giftId) {
            return "token";
        }
        return "";
    }
}
