package com.reign.gcld.scenario.common;

import com.reign.gcld.log.*;
import com.reign.framework.json.*;
import org.apache.commons.lang.*;
import com.reign.gcld.common.*;
import com.reign.gcld.sdata.domain.*;
import com.reign.gcld.scenario.common.action.*;

public class ScenarioEventJsonBuilder
{
    static ErrorLogger errorLogger;
    
    static {
        ScenarioEventJsonBuilder.errorLogger = new ErrorLogger();
    }
    
    public static void sendFlagInfo(final int playerId, final MultiResult flagInfo, final int eventId, final int flag, final String titleString, final int deadLine, final String deadLineName) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("eventId", eventId);
        doc.createElement("playerId", playerId);
        if (flagInfo != null) {
            doc.createElement("cityId", flagInfo.result1);
            doc.createElement("pic", flagInfo.result2);
            doc.createElement("isGeneral", flagInfo.result3);
        }
        doc.createElement("flag", flag);
        if (!StringUtils.isBlank(titleString)) {
            doc.createElement("title", titleString);
        }
        if (deadLine != -1) {
            doc.createElement("deadLine", deadLine * 1000L);
            doc.createElement("name", deadLineName);
        }
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_JUBEN_FLAG, doc.toByte());
    }
    
    public static void sendDialog(final int playerId, final int dialogId, final Object arg) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("id", dialogId);
        if (arg != null && (arg instanceof Integer || arg instanceof String)) {
            doc.createElement("arg", arg);
        }
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_JUBEN_DIALOG, doc.toByte());
    }
    
    public static void sendEventFinished(final int playerId, final int eventId, final IDataGetter getter) {
        try {
            final SoloEvent event = (SoloEvent)getter.getSoloEventCache().get((Object)eventId);
            if (event == null) {
                return;
            }
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            doc.createElement("id", eventId);
            if (!StringUtils.isBlank(event.getDealcondition())) {
                final String[] single = event.getDealcondition().split(",");
                doc.createElement("number", Integer.parseInt(single[1]));
                doc.createElement("type", single[0]);
            }
            if (!StringUtils.isBlank(event.getDealoperation1())) {
                final String[] single = event.getDealoperation1().split(",");
                doc.createElement("time", Integer.parseInt(single[1]));
            }
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_JUBEN_EVENTFINISH, doc.toByte());
        }
        catch (Exception e) {
            ScenarioEventJsonBuilder.errorLogger.error("sendEventFinished fail....playerId:" + playerId + "\t eventId:" + eventId);
            ScenarioEventJsonBuilder.errorLogger.error(e.getMessage(), e);
        }
    }
    
    public static void sendEventOver(final int playerId, final int eventId, final int state) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("id", eventId);
        doc.createElement("state", state);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_JUBEN_EVENTOVER, doc.toByte());
    }
    
    public static void sendGeneralAddInfo(final int playerId, final ScenarioAction add, final boolean isOver) {
        if (add instanceof ScenarioActionGeneralAdd) {
            final JsonDocument doc = new JsonDocument();
            doc.startObject();
            if (isOver) {
                doc.createElement("leftTime", 0);
            }
            else {
                doc.createElement("leftTime", add.repeatCirce);
            }
            final ScenarioActionGeneralAdd addGeneralAdd = (ScenarioActionGeneralAdd)add;
            doc.createElement("cityId", addGeneralAdd.getCityId());
            doc.endObject();
            Players.push(playerId, PushCommand.PUSH_JUBEN_GENERALADD, doc.toByte());
        }
    }
    
    public static void sendAllTrickInfo(final Object pic, final String trickName, final long repeatCirce, final int playerId) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("pic", pic);
        doc.createElement("stratagemName", trickName);
        doc.createElement("nextTrickTime", repeatCirce);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_ALL_TRICK, doc.toByte());
    }
    
    public static void sendChasingInfo(final ScenarioChansingInfo chansingInfo, final int playerId, final ScenarioEvent scenarioEvent) {
        if (chansingInfo == null) {
            return;
        }
        final long triggerTime = scenarioEvent.getTriggerTime();
        final long now = System.currentTimeMillis();
        final long lastTime = now - triggerTime;
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        chansingInfo.appendChasingInfo(lastTime, doc);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_CHASING_INFO, doc.toByte());
    }
    
    public static void sendMarchingInfo(final int serial, final int marchState, final long nextExcutedTime, final int curCityId, final int playerId, final int nextCityId, final int forceId, final String pic) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("serial", serial);
        doc.createElement("marchState", marchState);
        if (marchState == 0) {
            doc.createElement("nextExcutedTime", nextExcutedTime);
        }
        if (nextCityId > 0) {
            doc.createElement("nextCityId", nextCityId);
        }
        doc.createElement("curCityId", curCityId);
        doc.createElement("forceId", forceId);
        doc.createElement("pic", pic);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_JUBEN_MARCHING_INFO, doc.toByte());
    }
    
    public static void sendMengdeInfo(final int playerId, final int dst, final boolean isSafe) {
        final JsonDocument doc = new JsonDocument();
        doc.startObject();
        doc.createElement("location", dst);
        doc.createElement("isSafe", isSafe);
        doc.endObject();
        Players.push(playerId, PushCommand.PUSH_JUBEN_MENGDEINFO, doc.toByte());
    }
}
