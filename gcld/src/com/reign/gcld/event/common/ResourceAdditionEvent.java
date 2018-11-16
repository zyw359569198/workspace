package com.reign.gcld.event.common;

import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import com.reign.gcld.player.dto.*;
import com.reign.gcld.event.domain.*;
import com.reign.gcld.common.event.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.util.*;
import com.reign.gcld.building.domain.*;
import com.reign.framework.json.*;
import com.reign.gcld.common.*;
import java.util.*;

public class ResourceAdditionEvent extends DefaultEvent
{
    private static final Logger timerLog;
    public static Map<Integer, Integer> tokenMap;
    public static Map<Integer, Integer> functionIdMap;
    private static Map<Integer, Integer> itemIdMap;
    public static Map<Integer, Integer> rewardTypeMap;
    private IDataGetter dataGetter;
    
    static {
        timerLog = new TimerLogger();
        (ResourceAdditionEvent.tokenMap = new HashMap<Integer, Integer>()).put(1, 1);
        ResourceAdditionEvent.tokenMap.put(2, 6);
        (ResourceAdditionEvent.functionIdMap = new HashMap<Integer, Integer>()).put(2, 5);
        ResourceAdditionEvent.functionIdMap.put(3, 6);
        ResourceAdditionEvent.functionIdMap.put(4, 7);
        ResourceAdditionEvent.functionIdMap.put(5, 8);
        (ResourceAdditionEvent.itemIdMap = new HashMap<Integer, Integer>()).put(1, 1301);
        ResourceAdditionEvent.itemIdMap.put(2, 1311);
        ResourceAdditionEvent.itemIdMap.put(3, 1321);
        ResourceAdditionEvent.itemIdMap.put(4, 1341);
        ResourceAdditionEvent.itemIdMap.put(5, 1332);
        (ResourceAdditionEvent.rewardTypeMap = new HashMap<Integer, Integer>()).put(1, 86);
        ResourceAdditionEvent.rewardTypeMap.put(2, 87);
        ResourceAdditionEvent.rewardTypeMap.put(3, 88);
        ResourceAdditionEvent.rewardTypeMap.put(4, 80);
        ResourceAdditionEvent.rewardTypeMap.put(5, 85);
    }
    
    public ResourceAdditionEvent(final int eventId, final Date startTime, final Date endTime, final String paramInfo, final IDataGetter dataGetter) {
        super(eventId, startTime, endTime, paramInfo);
        this.dataGetter = dataGetter;
    }
    
    @Override
    public void handleOperation(final int type, final int playerId, final int val) {
        if (type == 12) {
            final int buildingType = val / 10;
            final int timeType = val % 10;
            ResourceAdditionEvent.timerLog.error("class:ResourceAdditionEvent#method:handleOperation#type:" + type + "#playerId:" + playerId + "#val:" + val + "#buildingType:" + buildingType + "#timeType" + timeType);
            this.dataGetter.getStoreHouseService().gainItems(playerId, ResourceAdditionEvent.tokenMap.get(timeType), ResourceAdditionEvent.itemIdMap.get(buildingType), "\u8d44\u6e90\u6536\u76ca\u6d3b\u52a8");
        }
    }
    
    public void buildJson(final JsonDocument doc, final PlayerDto playerDto, final PlayerEvent pe) {
        doc.createElement("eventId", this.eventId);
        doc.createElement("eventCd", this.getEventCD());
        doc.startArray("areas");
        final Date nowDate = new Date();
        for (int i = 1; i <= 5; ++i) {
            final Integer functionId = ResourceAdditionEvent.functionIdMap.get(i);
            if (functionId == null || playerDto.cs[functionId] == '1') {
                doc.startObject();
                doc.createElement("areaId", i);
                int output = this.dataGetter.getBuildingOutputCache().getBuildingsOutput(playerDto.playerId, i);
                if (i == 5) {
                    output = (int)Math.ceil(output / 60.0);
                }
                if (3 == i) {
                    if (output >= 800) {
                        EventListener.fireEvent(new CommonEvent(29, playerDto.playerId));
                    }
                }
                else if (5 == i) {
                    if (output >= 60000) {
                        EventListener.fireEvent(new CommonEvent(33, playerDto.playerId));
                    }
                    else if (output >= 52000) {
                        EventListener.fireEvent(new CommonEvent(30, playerDto.playerId));
                    }
                }
                doc.createElement("output", output);
                if (i == 5) {
                    doc.createElement("troopLv", (Object)(this.dataGetter.getTechEffectCache().getTechEffect(playerDto.playerId, 28) + 1));
                }
                doc.createElement("rate", (i == 5) ? 2 : 3);
                doc.createElement("token1", ResourceAdditionEvent.tokenMap.get(1));
                doc.createElement("token2", ResourceAdditionEvent.tokenMap.get(2));
                doc.createElement("gold1", this.dataGetter.getBuildingService().getResourceAddition(i, 2, (5 == i) ? 2 : 3).left);
                doc.createElement("gold2", this.dataGetter.getBuildingService().getResourceAddition(i, 3, (5 == i) ? 2 : 3).left);
                final PlayerResourceAddition pra = this.dataGetter.getPlayerResourceAdditionDao().getByPlayerIdAndType(playerDto.playerId, i);
                if (pra != null && pra.getEndTime().after(nowDate)) {
                    doc.createElement("additionMode", pra.getAdditionMode());
                    doc.createElement("additionRate", ((Chargeitem)this.dataGetter.getChargeitemCache().get((Object)this.dataGetter.getBuildingService().getId(i, pra.getAdditionMode()))).getParam());
                    doc.createElement("additionCd", CDUtil.getCD(pra.getEndTime(), nowDate));
                    doc.createElement("currentTimeType", pra.getTimeType());
                }
                else {
                    doc.createElement("additionMode", 0);
                    doc.createElement("additionRate", 1);
                    doc.createElement("additionCd", 0);
                }
                doc.endObject();
            }
        }
        doc.endArray();
    }
    
    @Override
    public void startEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveResourceAddittionActivity", 1);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[41] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
    }
    
    @Override
    public void overEvent() {
        final byte[] send = JsonBuilder.getSimpleJson("haveResourceAddittionActivity", 0);
        for (final PlayerDto dto : Players.getAllPlayer()) {
            if (dto.cs[41] == '1') {
                Players.push(dto.playerId, PushCommand.PUSH_UPDATE, send);
            }
        }
    }
}
