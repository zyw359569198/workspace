package com.reign.gcld.event.common;

import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import com.reign.gcld.event.domain.*;
import com.reign.gcld.slave.util.*;
import com.reign.gcld.activity.common.*;
import com.reign.gcld.sdata.domain.*;
import java.util.concurrent.*;
import com.reign.gcld.world.common.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.player.dto.*;
import java.util.*;

public class NationalDayEvent extends DefaultEvent
{
    private static final Logger log;
    private static final Logger timerLog;
    private static Map<Integer, Integer> lvReputeMap;
    private static int LENGTH;
    public static int MAX_NUM;
    public static Map<Integer, Integer> rewardMap;
    public static Map<Integer, Integer> npcBaseMap;
    public static Map<Integer, Integer> npcMaxMap;
    public static final int GENERAL_TREASURE_HESHIBI = 4;
    public static final int GENERAL_TREASURE_GOLD = 500;
    private IDataGetter dataGetter;
    
    static {
        log = CommonLog.getLog(NationalDayEvent.class);
        timerLog = new TimerLogger();
        (NationalDayEvent.lvReputeMap = new HashMap<Integer, Integer>()).put(1, 165);
        NationalDayEvent.lvReputeMap.put(2, 375);
        NationalDayEvent.lvReputeMap.put(3, 630);
        NationalDayEvent.lvReputeMap.put(4, 1000);
        NationalDayEvent.LENGTH = NationalDayEvent.lvReputeMap.size();
        NationalDayEvent.MAX_NUM = 1000;
        (NationalDayEvent.rewardMap = new HashMap<Integer, Integer>()).put(1, 10);
        NationalDayEvent.rewardMap.put(2, 13);
        NationalDayEvent.rewardMap.put(3, 16);
        NationalDayEvent.rewardMap.put(4, 25);
        (NationalDayEvent.npcBaseMap = new HashMap<Integer, Integer>()).put(1, 0);
        NationalDayEvent.npcBaseMap.put(2, 15);
        NationalDayEvent.npcBaseMap.put(3, 30);
        (NationalDayEvent.npcMaxMap = new HashMap<Integer, Integer>()).put(1, 15);
        NationalDayEvent.npcMaxMap.put(2, 30);
        NationalDayEvent.npcMaxMap.put(3, 45);
    }
    
    public static int getNpcBase(final int day) {
        final Integer result = NationalDayEvent.npcBaseMap.get(day);
        return (result == null) ? 0 : result;
    }
    
    public NationalDayEvent(final int eventId, final Date startTime, final Date endTime, final String paramInfo, final IDataGetter dataGetter) {
        super(eventId, startTime, endTime, paramInfo);
        this.dataGetter = dataGetter;
    }
    
    @Override
    public void handleOperation(final int type, final int playerId, final int val) {
        if (type == 11) {
            NationalDayEvent.timerLog.error("class:NationalDayEvent#method:handleOperation#type:" + type + "#playerId:" + playerId + "#val:" + val);
            PlayerEvent playerEvent = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 11);
            if (playerEvent == null) {
                playerEvent = this.initNationalDayEvent(playerId);
                this.dataGetter.getPlayerEventDao().create(playerEvent);
                CityEventManager.getInstance().addPlayerEvent(playerId, 4);
                CityEventManager.getInstance().bobaoOnePlayerEvent(playerId, 4);
            }
            final int before = playerEvent.getParam1();
            final int after = before + val;
            final int bits = this.getBit(after);
            NationalDayEvent.timerLog.error("class:NationalDayEvent#method:handleOperation#type:" + type + "#playerId:" + playerId + "#val:" + val + "#before:" + before + "#after:" + after);
            this.dataGetter.getPlayerEventDao().updateInfo3(playerId, type, after, bits);
        }
    }
    
    private PlayerEvent initNationalDayEvent(final int playerId) {
        final PlayerEvent pe = new PlayerEvent();
        pe.setEventId(11);
        pe.setPlayerId(playerId);
        pe.setParam1(0);
        pe.setParam2(0);
        pe.setParam3(0);
        pe.setParam4(0);
        pe.setParam5(0);
        pe.setParam6(0);
        pe.setParam7(0);
        pe.setParam8(getNpcBase(this.getDayth()));
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
        final int param6 = pe.getParam6();
        final int param7 = pe.getParam7();
        final int param8 = pe.getParam8();
        doc.createElement("totalRepute", param1);
        doc.createElement("needRepute", NationalDayEvent.MAX_NUM);
        doc.startArray("gifts");
        for (int i = 1; i <= NationalDayEvent.LENGTH; ++i) {
            doc.startObject();
            doc.createElement("giftId", i);
            final int needNum = NationalDayEvent.lvReputeMap.get(i);
            doc.createElement("needNum", needNum);
            int state = 0;
            if (param1 >= needNum) {
                if (1 == SlaveUtil.hasReward(param2, i) && 1 == SlaveUtil.hasReward(param3, i)) {
                    state = 1;
                }
            }
            else {
                state = 2;
            }
            doc.createElement("state", state);
            doc.createElement("type", "phantom");
            doc.createElement("value", NationalDayEvent.rewardMap.get(i));
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
                doc.createElement("dropType", MiddleAutumnCache.getInstance().getCurrentDropType(playerDto.forceId));
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
        doc.startArray("npcs");
        final int currentId = param8;
        if (currentId < NationalDayEvent.npcMaxMap.get(this.getDayth())) {
            int j;
            for (int base = j = currentId / 5 * 5 + 1; j < base + 5; ++j) {
                doc.startObject();
                doc.createElement("id", j);
                int state2 = (currentId >= j) ? 1 : ((currentId + 1 == j) ? 0 : 2);
                final FstNdEvent fne = (FstNdEvent)this.dataGetter.getFstNdEventCache().get((Object)j);
                final Tuple<Integer, Integer> tuple = CityEventManager.getInstance().getCostTypeAndNum(fne.getCost1());
                doc.createElement("pic", fne.getPic());
                doc.createElement("name", fne.getName());
                doc.createElement("costType", tuple.left);
                doc.createElement("costValue", tuple.right);
                boolean flag = false;
                if (state2 == 0) {
                    final Map<Integer, PlayerEventObj> map = CityEventManager.getInstance().playerEventMap.get(playerDto.playerId);
                    if (map != null) {
                        final PlayerEventObj peo = map.get(4);
                        if (peo != null) {
                            final WorldCity wc2 = (WorldCity)this.dataGetter.getWorldCityCache().get((Object)peo.cityId);
                            if (wc2 != null) {
                                doc.createElement("cityId", peo.cityId);
                                doc.createElement("cityName", wc2.getName());
                                flag = true;
                            }
                        }
                    }
                }
                if (state2 == 0 && !flag) {
                    state2 = 2;
                }
                doc.createElement("state", state2);
                doc.endObject();
            }
        }
        doc.endArray();
        doc.createElement("bmw", param5);
        doc.createElement("xo", param6);
        doc.createElement("picasso", param7);
        int canReceived = 0;
        if (param1 >= NationalDayEvent.MAX_NUM) {
            if (param4 == 0) {
                canReceived = 1;
            }
            else if (1 == param4 || 2 == param4) {
                canReceived = 2;
            }
        }
        doc.createElement("canReceived", canReceived);
        doc.createElement("gold", 500);
    }
    
    @Override
    public void startEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveNationalDayActivity", 1);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[10] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
        this.dataGetter.getJobService().addJob("timerBattleService", "addMoonCakeArmyForThreeCountry", "", System.currentTimeMillis(), false);
        this.dataGetter.getPlayerBattleAttributeDao().resetEventNationalTreasureCountToday();
        CityEventManager.getInstance().addFirstRoundPlayerEvent(4);
        this.dataGetter.getJobService().addJob("eventService", "nationalDayTimeTask", "1", TimeUtil.getNext1Day0Clock(), false);
        this.dataGetter.getJobService().addJob("eventService", "nationalDayTimeTask", "2", TimeUtil.getNext2Day0Clock(), false);
        NationalDayEvent.timerLog.error("class:EventFactory#method:createEvent#target:nationalDayTimeTask#1-day-job_time:" + new Date(TimeUtil.getNext1Day0Clock()));
        NationalDayEvent.timerLog.error("class:EventFactory#method:createEvent#target:nationalDayTimeTask#2-day-job_time:" + new Date(TimeUtil.getNext2Day0Clock()));
    }
    
    @Override
    public void overEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveNationalDayActivity", 0);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[10] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        CityEventManager.getInstance().removePlayerEventByEventType(4);
        final List<PlayerEvent> peList = this.dataGetter.getPlayerEventDao().getPlayerEventList(this.eventId);
        for (final PlayerEvent playerEvent : peList) {
            try {
                final int param1 = playerEvent.getParam1();
                final int param2 = playerEvent.getParam2();
                final int param3 = playerEvent.getParam3();
                final int param4 = playerEvent.getParam4();
                int phantom = 0;
                for (int i = 1; i <= NationalDayEvent.LENGTH; ++i) {
                    if (1 == SlaveUtil.hasReward(param2, i) && SlaveUtil.hasReward(param3, i) == 0) {
                        phantom += NationalDayEvent.rewardMap.get(i);
                    }
                }
                final int playerId = playerEvent.getPlayerId();
                final StringBuffer sb = new StringBuffer(LocalMessages.NATIONAL_DAY_ACTIVITY_MAIL_CONTENT_MAIN);
                int count = 0;
                if (phantom > 0) {
                    this.dataGetter.getPlayerBattleAttributeDao().addVip3PhantomCount(playerId, phantom, "\u56fd\u5e86\u793c\u5305\u83b7\u5f97\u514d\u8d39\u501f\u5175\u6b21\u6570");
                    if (count > 0) {
                        sb.append("\uff0c");
                    }
                    ++count;
                    sb.append(MessageFormatter.format(LocalMessages.NATIONAL_DAY_ACTIVITY_MAIL_CONTENT_PHANTON, new Object[] { phantom }));
                }
                if (param1 >= NationalDayEvent.MAX_NUM && param4 == 0) {
                    this.dataGetter.getTreasureService().tryGetGeneralTreasure(PlayerDtoUtil.getPlayerDto(this.dataGetter.getPlayerDao().read(playerId), this.dataGetter.getPlayerAttributeDao().read(playerId)), 4, false, 0, 0, true, "\u56fd\u5e86\u6d3b\u52a8\u83b7\u5f97\u548c\u6c0f\u74a7");
                    if (count > 0) {
                        sb.append("\uff0c");
                    }
                    ++count;
                    sb.append(LocalMessages.NATIONAL_DAY_ACTIVITY_MAIL_CONTENT_HESHIBI);
                }
                if (count <= 0) {
                    continue;
                }
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.NATIONAL_DAY_ACTIVITY_MAIL_TITLE, sb.toString(), 1, playerId, 0);
            }
            catch (Exception e) {
                NationalDayEvent.log.error("class:NationalDayEvent#method:overEvent#playerId:" + playerEvent.getPlayerId(), e);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
    }
    
    public int getBit(final int repute) {
        int result = 0;
        for (int i = 1; i <= NationalDayEvent.LENGTH && repute >= NationalDayEvent.lvReputeMap.get(i); ++i) {
            result |= (int)Math.pow(2.0, i - 1);
        }
        return result;
    }
}
