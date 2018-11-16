package com.reign.gcld.event.common;

import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.gcld.event.domain.*;
import com.reign.gcld.slave.util.*;
import java.util.*;
import com.reign.gcld.slave.domain.*;
import com.reign.gcld.player.dto.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import com.reign.gcld.event.util.*;

public class SlaveEvent extends DefaultEvent
{
    private static final Logger log;
    public static Map<Integer, Integer> rateMap;
    private static Map<Integer, Integer> lvBitMap;
    private static IDataGetter dataGetter;
    public static int lv_2;
    public static int lv_3;
    public static int lv_4;
    public static int lv_5;
    public static int ADDITION_EXP;
    public static Map<Integer, Integer> rewardTypeMap;
    public static Map<Integer, Integer> rewardValueMap;
    
    static {
        log = CommonLog.getLog(SlaveEvent.class);
        SlaveEvent.rateMap = new ConcurrentHashMap<Integer, Integer>();
        (SlaveEvent.lvBitMap = new HashMap<Integer, Integer>()).put(1, 0);
        SlaveEvent.lvBitMap.put(2, (int)Math.pow(2.0, 0.0));
        SlaveEvent.lvBitMap.put(3, (int)Math.pow(2.0, 0.0) | (int)Math.pow(2.0, 1.0));
        SlaveEvent.lvBitMap.put(4, (int)Math.pow(2.0, 0.0) | (int)Math.pow(2.0, 1.0) | (int)Math.pow(2.0, 2.0));
        SlaveEvent.lvBitMap.put(5, (int)Math.pow(2.0, 0.0) | (int)Math.pow(2.0, 1.0) | (int)Math.pow(2.0, 2.0) | (int)Math.pow(2.0, 3.0));
        SlaveEvent.lv_2 = 500000;
        SlaveEvent.lv_3 = 1000000;
        SlaveEvent.lv_4 = 100000;
        SlaveEvent.lv_5 = 200000;
        SlaveEvent.ADDITION_EXP = 2500;
        SlaveEvent.rewardTypeMap = new HashMap<Integer, Integer>();
        SlaveEvent.rewardValueMap = new HashMap<Integer, Integer>();
        SlaveEvent.rewardTypeMap.put(1, 5);
        SlaveEvent.rewardTypeMap.put(2, 5);
        SlaveEvent.rewardTypeMap.put(3, 4);
        SlaveEvent.rewardTypeMap.put(4, 4);
        SlaveEvent.rewardValueMap.put(1, 500000);
        SlaveEvent.rewardValueMap.put(2, 1000000);
        SlaveEvent.rewardValueMap.put(3, 100000);
        SlaveEvent.rewardValueMap.put(4, 200000);
    }
    
    public static void init(final IDataGetter dataGetter) {
        SlaveEvent.dataGetter = dataGetter;
        final List<PlayerEvent> list = dataGetter.getPlayerEventDao().getPlayerEventList(9);
        for (final PlayerEvent pe : list) {
            final int rateNum = SlaveUtil.get1Num(pe.getParam3());
            SlaveEvent.rateMap.put(pe.getPlayerId(), rateNum);
        }
    }
    
    public SlaveEvent(final int eventId, final Date startTime, final Date endTime, final String paramInfo, final IDataGetter dataGetter) {
        super(eventId, startTime, endTime, paramInfo);
        SlaveEvent.dataGetter = dataGetter;
    }
    
    @Override
    public void handleOperation(final int type, final int playerId, final int val) {
        if (type == 9) {
            PlayerEvent playerEvent = SlaveEvent.dataGetter.getPlayerEventDao().getPlayerEvent(playerId, 9);
            if (playerEvent == null) {
                playerEvent = this.initSlaveEvent(playerId);
                SlaveEvent.dataGetter.getPlayerEventDao().create(playerEvent);
            }
            if (val < 1 || val > 4) {
                return;
            }
            SlaveEvent.dataGetter.getPlayerEventDao().setParam1(playerId, type, val - 1);
        }
    }
    
    private PlayerEvent initSlaveEvent(final Slaveholder sh) {
        final PlayerEvent pe = new PlayerEvent();
        pe.setEventId(9);
        pe.setPlayerId(sh.getPlayerId());
        pe.setParam1(SlaveEvent.lvBitMap.get(sh.getLashLv()));
        pe.setParam2(SlaveEvent.lvBitMap.get(sh.getLashLv()));
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
    
    private PlayerEvent initSlaveEvent(final int playerId) {
        final PlayerEvent pe = new PlayerEvent();
        pe.setEventId(9);
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
            pe = SlaveEvent.dataGetter.getPlayerEventDao().getPlayerEvent(playerDto.playerId, this.eventId);
        }
        final int param1 = pe.getParam1();
        final int param2 = pe.getParam2();
        final int param3 = pe.getParam3();
        doc.createElement("num", this.getCanCaptureNum(param2));
        doc.startArray("slaves");
        for (int i = 1; i <= 4; ++i) {
            doc.startObject();
            doc.createElement("slaveId", i);
            doc.createElement("state", this.getState(param1, param2, param3, i));
            doc.createElement("rewardType", SlaveEvent.rewardTypeMap.get(i));
            doc.createElement("rewardValue", SlaveEvent.rewardValueMap.get(i));
            doc.createElement("rate", SlaveEvent.ADDITION_EXP);
            doc.endObject();
        }
        doc.endArray();
    }
    
    private int getState(final int param1, final int param2, final int param3, final int pos) {
        final int temp1 = SlaveUtil.hasReward(param1, pos);
        if (temp1 == 0) {
            return 0;
        }
        final int temp2 = SlaveUtil.hasReward(param2, pos);
        if (temp2 == 0) {
            return 1;
        }
        final int temp3 = SlaveUtil.hasReward(param3, pos);
        if (temp3 == 0) {
            return 2;
        }
        return 3;
    }
    
    private int getCanCaptureNum(final int param2) {
        final int result = 4 - SlaveUtil.get1Num(param2);
        return (result <= 0) ? 0 : result;
    }
    
    @Override
    public void startEvent() {
        try {
            final List<Slaveholder> shList = SlaveEvent.dataGetter.getSlaveholderDao().getModels();
            for (final Slaveholder sh : shList) {
                PlayerEvent pe = SlaveEvent.dataGetter.getPlayerEventDao().getPlayerEvent(sh.getPlayerId(), 9);
                if (pe == null) {
                    pe = this.initSlaveEvent(sh);
                    SlaveEvent.dataGetter.getPlayerEventDao().create(pe);
                }
                else {
                    SlaveEvent.dataGetter.getPlayerEventDao().updateInfo(sh.getPlayerId(), 9, SlaveEvent.lvBitMap.get(sh.getLashLv()), SlaveEvent.lvBitMap.get(sh.getLashLv()), 0, 0);
                }
            }
        }
        catch (Exception e) {
            SlaveEvent.log.error("class:SlaveEvent#method:startEvent", e);
        }
        final byte[] send = JsonBuilder.getSimpleJson("haveSlaveActivity", 1);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[52] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        SlaveEvent.rateMap.clear();
    }
    
    @Override
    public void overEvent() {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("haveSlaveActivity", 0);
        doc.createElement("slaveActivityBuff", 0);
        doc.endObject();
        final byte[] send = doc.toByte();
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[52] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
        SlaveEvent.rateMap.clear();
        final List<PlayerEvent> peList = SlaveEvent.dataGetter.getPlayerEventDao().getPlayerEventList(this.eventId);
        for (final PlayerEvent playerEvent : peList) {
            final int param1 = playerEvent.getParam1();
            final int param2 = playerEvent.getParam2();
            int exp = 0;
            int iron = 0;
            if (1 == SlaveUtil.hasReward(param1, 1) && SlaveUtil.hasReward(param2, 1) == 0) {
                exp += SlaveEvent.lv_2;
            }
            if (1 == SlaveUtil.hasReward(param1, 2) && SlaveUtil.hasReward(param2, 2) == 0) {
                exp += SlaveEvent.lv_3;
            }
            if (1 == SlaveUtil.hasReward(param1, 3) && SlaveUtil.hasReward(param2, 3) == 0) {
                iron += SlaveEvent.lv_4;
            }
            if (1 == SlaveUtil.hasReward(param1, 4) && SlaveUtil.hasReward(param2, 4) == 0) {
                iron += SlaveEvent.lv_5;
            }
            if (exp <= 0 && iron <= 0) {
                continue;
            }
            final int playerId = playerEvent.getPlayerId();
            try {
                if (exp > 0) {
                    SlaveEvent.dataGetter.getPlayerService().updateExpAndPlayerLevel(playerId, exp, "\u7262\u623f\u6d3b\u52a8\u589e\u52a0\u7ecf\u9a8c");
                }
                if (iron > 0) {
                    SlaveEvent.dataGetter.getPlayerResourceDao().addIronIgnoreMax(playerId, iron, "\u7262\u623f\u6d3b\u52a8\u83b7\u53d6\u9554\u94c1", true);
                }
                String mailMsg = "";
                if (exp > 0 && iron > 0) {
                    mailMsg = MessageFormatter.format(LocalMessages.SLAVE_ACTIVITY_MAIL_CONTENT_ALL, new Object[] { exp, iron });
                }
                else if (exp > 0) {
                    mailMsg = MessageFormatter.format(LocalMessages.SLAVE_ACTIVITY_MAIL_CONTENT_L, new Object[] { exp });
                }
                else {
                    mailMsg = MessageFormatter.format(LocalMessages.SLAVE_ACTIVITY_MAIL_CONTENT_R, new Object[] { iron });
                }
                SlaveEvent.dataGetter.getMailService().writeSystemMail(LocalMessages.T_MAIL_ROLE_SYSTEM, LocalMessages.SLAVE_ACTIVITY_MAIL_TITLE, mailMsg, 1, playerId, 0);
            }
            catch (Exception e) {
                SlaveEvent.log.error("class:SlaveEvent#method:overEvent#playerId:" + playerId + "#exp:" + exp + "#iron" + iron, e);
            }
        }
        SlaveEvent.dataGetter.getPlayerEventDao().clearEvent(this.eventId);
    }
    
    public static int getAdditionExp(final int playerId) {
        if (!EventUtil.isEventTime(9)) {
            return 0;
        }
        final Integer rateNum = SlaveEvent.rateMap.get(playerId);
        return (rateNum == null) ? 0 : (rateNum * SlaveEvent.ADDITION_EXP);
    }
}
