package com.reign.gcld.event.common;

import com.reign.gcld.common.log.*;
import com.reign.util.*;
import com.reign.gcld.log.*;
import java.util.concurrent.*;
import com.reign.gcld.event.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.activity.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import java.util.*;

public class IronGiveEvent extends DefaultEvent
{
    private static final Logger timerLog;
    public static int LENGTH;
    public static int IRON_TICKET;
    private static Map<Integer, Integer> timesMap;
    private static Map<Integer, Integer> ironBaseMap;
    private static Map<Integer, Tuple<Integer, Integer>> ironRateMap;
    private static int SIZE;
    private static Map<String, Integer> ironMap;
    private IDataGetter dataGetter;
    
    static {
        timerLog = new TimerLogger();
        IronGiveEvent.LENGTH = 4;
        IronGiveEvent.IRON_TICKET = 20;
        (IronGiveEvent.timesMap = new HashMap<Integer, Integer>()).put(1, 8);
        IronGiveEvent.timesMap.put(2, 10);
        IronGiveEvent.timesMap.put(3, 12);
        IronGiveEvent.timesMap.put(4, 5);
        (IronGiveEvent.ironBaseMap = new HashMap<Integer, Integer>()).put(1, 3000);
        IronGiveEvent.ironBaseMap.put(2, 6000);
        IronGiveEvent.ironBaseMap.put(3, 8000);
        IronGiveEvent.ironBaseMap.put(4, 2000);
        (IronGiveEvent.ironRateMap = new HashMap<Integer, Tuple<Integer, Integer>>()).put(1, new Tuple(69, 3));
        IronGiveEvent.ironRateMap.put(2, new Tuple(80, 4));
        IronGiveEvent.ironRateMap.put(3, new Tuple(90, 6));
        IronGiveEvent.ironRateMap.put(4, new Tuple(100, 8));
        IronGiveEvent.ironRateMap.put(5, new Tuple(110, 10));
        IronGiveEvent.ironRateMap.put(6, new Tuple(Integer.MAX_VALUE, 13));
        IronGiveEvent.SIZE = IronGiveEvent.ironRateMap.size();
        IronGiveEvent.ironMap = new ConcurrentHashMap<String, Integer>();
    }
    
    public static int getIron(final int playerLv, final int index) {
        final String key = String.valueOf(playerLv) + "_" + index;
        Integer iron = IronGiveEvent.ironMap.get(key);
        if (iron != null) {
            return iron;
        }
        final int base = IronGiveEvent.ironBaseMap.get(index);
        int rate = 0;
        for (int i = 1; i <= IronGiveEvent.SIZE; ++i) {
            final Tuple<Integer, Integer> tuple = IronGiveEvent.ironRateMap.get(i);
            if (playerLv <= tuple.left) {
                rate = tuple.right;
                break;
            }
        }
        iron = base * rate;
        IronGiveEvent.ironMap.put(key, iron);
        return iron;
    }
    
    public IronGiveEvent(final int eventId, final Date startTime, final Date endTime, final String paramInfo, final IDataGetter dataGetter) {
        super(eventId, startTime, endTime, paramInfo);
        this.dataGetter = dataGetter;
    }
    
    @Override
    public void handleOperation(final int type, final int playerId, final int val) {
        if (type == 15) {
            IronGiveEvent.timerLog.error("class:IronGiveEvent#method:handleOperation#type:" + type + "#playerId:" + playerId + "#val:" + val);
            PlayerEvent playerEvent = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 15);
            if (playerEvent == null) {
                playerEvent = this.initIronRewardEvent(playerId);
                this.dataGetter.getPlayerEventDao().create(playerEvent);
            }
            if (val > 0) {
                this.dataGetter.getPlayerEventDao().addParam5(playerId, 15, val);
            }
        }
    }
    
    private PlayerEvent initIronRewardEvent(final int playerId) {
        final PlayerEvent pe = new PlayerEvent();
        pe.setEventId(15);
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
        doc.startArray("irons");
        for (int i = 1; i <= IronGiveEvent.LENGTH; ++i) {
            doc.startObject();
            doc.createElement("id", i);
            doc.createElement("iron", getIron(playerDto.playerLv, i));
            doc.createElement("gold", this.dataGetter.getEventService().getGold(i));
            doc.createElement("num", getNum(i, pe));
            doc.endObject();
        }
        doc.endArray();
        doc.createElement("ticket", pe.getParam5());
        doc.startArray("cities");
        int count = 0;
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
    }
    
    public static int getNum(final int index, final PlayerEvent pe) {
        if (1 == index) {
            return IronGiveEvent.timesMap.get(index) - pe.getParam1();
        }
        if (2 == index) {
            return IronGiveEvent.timesMap.get(index) - pe.getParam2();
        }
        if (3 == index) {
            return IronGiveEvent.timesMap.get(index) - pe.getParam3();
        }
        if (4 == index) {
            return IronGiveEvent.timesMap.get(index) - pe.getParam4();
        }
        return 0;
    }
    
    @Override
    public void startEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveIronGiveActivity", 1);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
        }
        this.dataGetter.getJobService().addJob("timerBattleService", "addMoonCakeArmyForThreeCountry", "", System.currentTimeMillis(), false);
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
    }
    
    @Override
    public void overEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveIronGiveActivity", 0);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
    }
}
