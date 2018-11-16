package com.reign.gcld.event.common;

import com.reign.gcld.log.*;
import com.reign.gcld.common.log.*;
import com.reign.gcld.event.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.slave.util.*;
import com.reign.framework.json.*;
import com.reign.gcld.incense.domain.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import java.util.*;

public class IronRewardEvent extends DefaultEvent
{
    private static final Logger timerLog;
    private static final Logger log;
    private static Map<Integer, Integer> incenseTimesMap;
    public static int LENGTH;
    private static int TIMES;
    public static int IRON;
    public static Map<Integer, Integer> itemIdMap;
    public static Map<Integer, Integer> itemIdReverseMap;
    public static Map<Integer, Integer> effectTypeMap;
    private static Map<Integer, Integer> modMap;
    public static int GOLD_1;
    public static int GOLD_5;
    public static String MULTI;
    public static String LIMIT;
    public static String TIME;
    public static long MORE_CD;
    public static long REAL_MORE_CD;
    private IDataGetter dataGetter;
    
    static {
        timerLog = new TimerLogger();
        log = CommonLog.getLog(IronRewardEvent.class);
        (IronRewardEvent.incenseTimesMap = new HashMap<Integer, Integer>()).put(1, 5);
        IronRewardEvent.incenseTimesMap.put(2, 15);
        IronRewardEvent.incenseTimesMap.put(3, 50);
        IronRewardEvent.incenseTimesMap.put(4, 150);
        IronRewardEvent.incenseTimesMap.put(5, 400);
        IronRewardEvent.LENGTH = IronRewardEvent.incenseTimesMap.size();
        IronRewardEvent.TIMES = 3;
        IronRewardEvent.IRON = 5000;
        (IronRewardEvent.itemIdMap = new HashMap<Integer, Integer>()).put(1, 1501);
        IronRewardEvent.itemIdMap.put(2, 1502);
        IronRewardEvent.itemIdMap.put(3, 1503);
        IronRewardEvent.itemIdMap.put(4, 1504);
        IronRewardEvent.itemIdMap.put(5, 1505);
        IronRewardEvent.itemIdReverseMap = new HashMap<Integer, Integer>();
        for (final Map.Entry<Integer, Integer> entry : IronRewardEvent.itemIdMap.entrySet()) {
            IronRewardEvent.itemIdReverseMap.put(entry.getValue(), entry.getKey());
        }
        (IronRewardEvent.effectTypeMap = new HashMap<Integer, Integer>()).put(1, 1);
        IronRewardEvent.effectTypeMap.put(2, 2);
        IronRewardEvent.effectTypeMap.put(3, 1);
        IronRewardEvent.effectTypeMap.put(4, 2);
        IronRewardEvent.effectTypeMap.put(5, 2);
        (IronRewardEvent.modMap = new HashMap<Integer, Integer>()).put(1, 0);
        IronRewardEvent.modMap.put(2, 1);
        IronRewardEvent.modMap.put(3, 1);
        IronRewardEvent.modMap.put(4, 1);
        IronRewardEvent.modMap.put(5, 0);
        IronRewardEvent.GOLD_1 = 1;
        IronRewardEvent.GOLD_5 = 5;
        IronRewardEvent.MULTI = "MULTI";
        IronRewardEvent.LIMIT = "LIMIT";
        IronRewardEvent.TIME = "TIME";
        IronRewardEvent.MORE_CD = 2000L;
        IronRewardEvent.REAL_MORE_CD = 2500L;
    }
    
    public IronRewardEvent(final int eventId, final Date startTime, final Date endTime, final String paramInfo, final IDataGetter dataGetter) {
        super(eventId, startTime, endTime, paramInfo);
        this.dataGetter = dataGetter;
    }
    
    @Override
    public void handleOperation(final int type, final int playerId, final int val) {
        if (type == 13) {
            IronRewardEvent.timerLog.error("class:IronRewardEvent#method:handleOperation#type:" + type + "#playerId:" + playerId + "#val:" + val);
            PlayerEvent playerEvent = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 13);
            if (playerEvent == null) {
                playerEvent = this.initIronRewardEvent(playerId);
                this.dataGetter.getPlayerEventDao().create(playerEvent);
            }
            if (val != 1) {
                return;
            }
            final int before = playerEvent.getParam1();
            final int after = before + val;
            final int param2 = this.getBits(after);
            this.dataGetter.getPlayerEventDao().updateInfo3(playerId, 13, after, param2);
            if (after % IronRewardEvent.TIMES == 0) {
                this.dataGetter.getPlayerResourceDao().addIronIgnoreMax(playerId, IronRewardEvent.IRON, "\u9554\u94c1\u56de\u9988\u6d3b\u52a8\u83b7\u5f97\u9554\u94c1", true);
            }
        }
    }
    
    private int getBits(final int incenseTimes) {
        if (incenseTimes <= 0) {
            return 0;
        }
        int result = 0;
        for (int i = 1; i <= IronRewardEvent.LENGTH && incenseTimes >= IronRewardEvent.incenseTimesMap.get(i); ++i) {
            result += (int)Math.pow(2.0, i - 1);
        }
        return result;
    }
    
    private PlayerEvent initIronRewardEvent(final int playerId) {
        final PlayerEvent pe = new PlayerEvent();
        pe.setEventId(13);
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
        doc.createElement("gold1", this.dataGetter.getIncenseService().getGold(playerDto.playerId, 4, 1));
        doc.createElement("gold5", this.dataGetter.getIncenseService().getGold(playerDto.playerId, 4, 5));
        if (pe == null) {
            this.handleOperation(this.eventId, playerDto.playerId, 0);
            pe = this.dataGetter.getPlayerEventDao().getPlayerEvent(playerDto.playerId, this.eventId);
        }
        doc.createElement("num", pe.getParam1());
        doc.createElement("times", IronRewardEvent.TIMES);
        doc.createElement("extIron", IronRewardEvent.IRON);
        doc.startArray("incenseTimes");
        for (int i = 1; i <= IronRewardEvent.LENGTH; ++i) {
            doc.startObject();
            doc.createElement("id", i);
            doc.createElement("times", IronRewardEvent.incenseTimesMap.get(i));
            final Items item = (Items)this.dataGetter.getItemsCache().get((Object)IronRewardEvent.itemIdMap.get(i));
            doc.createElement("pic", item.getPic());
            doc.createElement("received", SlaveUtil.hasReward(pe.getParam3(), i));
            doc.createElement("effectType", IronRewardEvent.effectTypeMap.get(i));
            doc.createElement("mod", IronRewardEvent.modMap.get(i));
            doc.createElement("tips", item.getIntro());
            final ThreeTuple<Integer, Integer, Long> threeTuple = this.dataGetter.getEventService().getEffect(item.getEffect());
            doc.createElement("limit", threeTuple.left);
            doc.createElement("multi", threeTuple.middle);
            doc.createElement("cd", threeTuple.right + IronRewardEvent.MORE_CD);
            doc.endObject();
        }
        doc.endArray();
        doc.startArray("golds");
        for (int i = 1; i <= IronRewardEvent.LENGTH; ++i) {
            doc.startObject();
            doc.createElement("indenx", i);
            doc.createElement("gold", this.dataGetter.getIncenseService().getIndexGold(playerDto.playerId, 4, i));
            doc.endObject();
        }
        doc.endArray();
    }
    
    @Override
    public void startEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveIronRewardActivity", 1);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            final PlayerIncense pi = this.dataGetter.getPlayerIncenseDao().read(dto.playerId);
            if (pi != null && SlaveUtil.hasReward(pi.getOpenBit(), 4) == 1) {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
    }
    
    @Override
    public void overEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveIronRewardActivity", 0);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            final PlayerIncense pi = this.dataGetter.getPlayerIncenseDao().read(dto.playerId);
            if (pi != null && SlaveUtil.hasReward(pi.getOpenBit(), 4) == 1) {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        final List<PlayerEvent> peList = this.dataGetter.getPlayerEventDao().getIronRewardList(13);
        for (final PlayerEvent pe : peList) {
            try {
                final StringBuffer mailMsg = new StringBuffer(LocalMessages.IRON_REWARD_MAIL_CONTENT_HEAD);
                for (int i = 1; i <= IronRewardEvent.LENGTH; ++i) {
                    if (1 == SlaveUtil.hasReward(pe.getParam2(), i) && 1 != SlaveUtil.hasReward(pe.getParam3(), i)) {
                        this.dataGetter.getStoreHouseService().gainItems(pe.getPlayerId(), 1, IronRewardEvent.itemIdMap.get(i), "\u9554\u94c1\u56de\u9988\u6d3b\u52a8\u83b7\u53d6\u4ee4");
                        mailMsg.append(MessageFormatter.format(LocalMessages.IRON_REWARD_MAIL_TOKEN, new Object[] { ((Items)this.dataGetter.getItemsCache().get((Object)IronRewardEvent.itemIdMap.get(i))).getName() }));
                    }
                }
                mailMsg.setLength(mailMsg.length() - 1);
                mailMsg.append(LocalMessages.IRON_REWARD_MAIL_CONTENT_TAIL);
                this.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.IRON_REWARD_MAIL_TITLE, mailMsg.toString(), 1, pe.getPlayerId(), 0);
            }
            catch (Exception e) {
                IronRewardEvent.log.error("class:IronRewardEvent#method:overEvent#playerId:" + pe.getPlayerId(), e);
            }
        }
        this.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
    }
    
    public static void main(final String[] args) {
        System.out.println((int)Math.pow(2.0, 0.0));
        final StringBuffer sb = new StringBuffer("abc");
        sb.setLength(sb.length() - 1);
        System.out.println((int)Math.pow(2.0, 0.0));
        System.out.println(sb);
    }
}
