package com.reign.gcld.battle.common;

import com.reign.gcld.common.log.*;
import com.reign.util.*;
import com.reign.gcld.log.*;
import java.util.*;

public class BattleSceneLog implements Logger
{
    private static final Logger battleLog;
    private List<Tuple<String, Object>> sceneList;
    
    static {
        battleLog = new BattleLogger();
    }
    
    private BattleSceneLog() {
        this.sceneList = null;
    }
    
    public static BattleSceneLog getInstance() {
        final BattleSceneLog instance = new BattleSceneLog();
        instance.sceneList = new LinkedList<Tuple<String, Object>>();
        return instance;
    }
    
    public BattleSceneLog append(final String fieldName, Object fieldValue) {
        if (fieldName == null && fieldValue == null) {
            return this;
        }
        if (fieldValue == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("fieldValue is null").append("fieldName", fieldName).flush();
            fieldValue = "null";
        }
        this.sceneList.add(new Tuple(fieldName, fieldValue));
        return this;
    }
    
    public BattleSceneLog appendBattleId(final String battleId) {
        this.sceneList.add(new Tuple("battleId", battleId));
        return this;
    }
    
    public BattleSceneLog appendPlayerId(final int playerId) {
        this.sceneList.add(new Tuple("playerId", playerId));
        return this;
    }
    
    public BattleSceneLog appendPlayerName(String playerName) {
        if (playerName == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("playerName is null").append("playerName", playerName).flush();
            playerName = "null";
        }
        this.sceneList.add(new Tuple("playerName", playerName));
        return this;
    }
    
    public BattleSceneLog appendGeneralId(final int generalId) {
        this.sceneList.add(new Tuple("generalId", generalId));
        return this;
    }
    
    public BattleSceneLog appendGeneralName(String generalName) {
        if (generalName == null) {
            ErrorSceneLog.getInstance().appendErrorMsg("generalName is null").append("generalName", generalName).flush();
            generalName = "null";
        }
        this.sceneList.add(new Tuple("generalName", generalName));
        return this;
    }
    
    public BattleSceneLog appendClassName(final String className) {
        this.sceneList.add(new Tuple("ClassName", className));
        return this;
    }
    
    public BattleSceneLog appendMethodName(final String methodName) {
        this.sceneList.add(new Tuple("MethodName", methodName));
        return this;
    }
    
    public BattleSceneLog appendLogMsg(final String msg) {
        this.sceneList.add(new Tuple("LogMsg", msg));
        return this;
    }
    
    public BattleSceneLog appendOthers(final String others) {
        this.sceneList.add(new Tuple("Others", others));
        return this;
    }
    
    public void flush() {
        final StringBuilder sb = new StringBuilder();
        for (final Tuple<String, Object> tuple : this.sceneList) {
            sb.append(String.valueOf(tuple.left) + ":" + tuple.right.toString() + " | ");
        }
        sb.append("\n");
        BattleSceneLog.battleLog.debug(sb.toString());
        this.sceneList.clear();
    }
    
    @Override
    public void trace(final Object obj) {
        BattleSceneLog.battleLog.trace(obj);
    }
    
    @Override
    public void trace(final Object obj, final Throwable throwable) {
        BattleSceneLog.battleLog.trace(obj, throwable);
    }
    
    @Override
    public void debug(final Object obj) {
        BattleSceneLog.battleLog.debug(obj);
    }
    
    @Override
    public void debug(final Object obj, final Throwable throwable) {
        BattleSceneLog.battleLog.debug(obj, throwable);
    }
    
    @Override
    public void info(final Object obj) {
        BattleSceneLog.battleLog.info(obj);
    }
    
    @Override
    public void info(final Object obj, final Throwable throwable) {
        BattleSceneLog.battleLog.info(obj, throwable);
    }
    
    @Override
    public void warn(final Object obj) {
        BattleSceneLog.battleLog.warn(obj);
    }
    
    @Override
    public void warn(final Object obj, final Throwable throwable) {
        BattleSceneLog.battleLog.warn(obj, throwable);
    }
    
    @Override
    public void error(final Object obj) {
        BattleSceneLog.battleLog.error(obj);
    }
    
    @Override
    public void error(final Object obj, final Throwable throwable) {
        BattleSceneLog.battleLog.error(obj, throwable);
    }
    
    @Override
    public void fatal(final Object obj) {
        BattleSceneLog.battleLog.fatal(obj);
    }
    
    @Override
    public void fatal(final Object obj, final Throwable throwable) {
        BattleSceneLog.battleLog.fatal(obj, throwable);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return BattleSceneLog.battleLog.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return BattleSceneLog.battleLog.isTraceEnabled();
    }
}
