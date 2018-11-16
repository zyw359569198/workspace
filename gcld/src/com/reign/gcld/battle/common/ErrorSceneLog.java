package com.reign.gcld.battle.common;

import com.reign.gcld.common.log.*;
import com.reign.util.*;
import com.reign.gcld.log.*;
import java.util.*;

public class ErrorSceneLog implements Logger
{
    private static final Logger errorLog;
    private List<Tuple<String, Object>> sceneList;
    
    static {
        errorLog = new ErrorLogger();
    }
    
    private ErrorSceneLog() {
        this.sceneList = null;
    }
    
    public static ErrorSceneLog getInstance() {
        final ErrorSceneLog instance = new ErrorSceneLog();
        instance.sceneList = new LinkedList<Tuple<String, Object>>();
        return instance;
    }
    
    public ErrorSceneLog append(final String fieldName, final Object fieldValue) {
        this.sceneList.add(new Tuple(fieldName, fieldValue));
        return this;
    }
    
    public ErrorSceneLog appendBattleId(final String battleId) {
        this.sceneList.add(new Tuple("battleId", battleId));
        return this;
    }
    
    public ErrorSceneLog appendPlayerId(final int playerId) {
        this.sceneList.add(new Tuple("PlayerId", playerId));
        return this;
    }
    
    public ErrorSceneLog appendPlayerName(final String playerName) {
        this.sceneList.add(new Tuple("PlayerName", playerName));
        return this;
    }
    
    public ErrorSceneLog appendGeneralId(final int generalId) {
        this.sceneList.add(new Tuple("generalId", generalId));
        return this;
    }
    
    public ErrorSceneLog appendGeneralName(final String generalName) {
        this.sceneList.add(new Tuple("generalName", generalName));
        return this;
    }
    
    public ErrorSceneLog appendClassName(final String className) {
        this.sceneList.add(new Tuple("ClassName", className));
        return this;
    }
    
    public ErrorSceneLog appendMethodName(final String methodName) {
        this.sceneList.add(new Tuple("MethodName", methodName));
        return this;
    }
    
    public ErrorSceneLog appendErrorMsg(final String msg) {
        this.sceneList.add(new Tuple("Error", msg));
        return this;
    }
    
    public ErrorSceneLog appendOthers(final String others) {
        this.sceneList.add(new Tuple("Others", others));
        return this;
    }
    
    public void flush() {
        try {
            final StringBuilder sb = new StringBuilder();
            for (final Tuple<String, Object> tuple : this.sceneList) {
                if (tuple == null) {
                    ErrorSceneLog.errorLog.error("//////////////////////////////////ErrorSceneLog flush. tuple is null");
                }
                else if (tuple.left == null) {
                    ErrorSceneLog.errorLog.error("//////////////////////////////////ErrorSceneLog flush. tuple.left is null");
                }
                else if (tuple.right == null) {
                    ErrorSceneLog.errorLog.error("//////////////////////////////////ErrorSceneLog flush. tuple.right is null:left=" + tuple.left);
                }
                else {
                    sb.append(String.valueOf(tuple.left) + ":" + tuple.right.toString() + " | ");
                }
            }
            sb.append("\n");
            ErrorSceneLog.errorLog.error(sb.toString());
            this.sceneList.clear();
        }
        catch (Exception e) {
            ErrorSceneLog.errorLog.trace("ErrorSceneLog.flush catch Exception", e);
        }
    }
    
    @Override
    public void trace(final Object obj) {
        ErrorSceneLog.errorLog.trace(obj);
    }
    
    @Override
    public void trace(final Object obj, final Throwable throwable) {
        ErrorSceneLog.errorLog.trace(obj, throwable);
    }
    
    @Override
    public void debug(final Object obj) {
        ErrorSceneLog.errorLog.debug(obj);
    }
    
    @Override
    public void debug(final Object obj, final Throwable throwable) {
        ErrorSceneLog.errorLog.debug(obj, throwable);
    }
    
    @Override
    public void info(final Object obj) {
        ErrorSceneLog.errorLog.info(obj);
    }
    
    @Override
    public void info(final Object obj, final Throwable throwable) {
        ErrorSceneLog.errorLog.info(obj, throwable);
    }
    
    @Override
    public void warn(final Object obj) {
        ErrorSceneLog.errorLog.warn(obj);
    }
    
    @Override
    public void warn(final Object obj, final Throwable throwable) {
        ErrorSceneLog.errorLog.warn(obj, throwable);
    }
    
    @Override
    public void error(final Object obj) {
        ErrorSceneLog.errorLog.error(obj);
    }
    
    @Override
    public void error(final Object obj, final Throwable throwable) {
        ErrorSceneLog.errorLog.error(obj, throwable);
    }
    
    @Override
    public void fatal(final Object obj) {
        ErrorSceneLog.errorLog.fatal(obj);
    }
    
    @Override
    public void fatal(final Object obj, final Throwable throwable) {
        ErrorSceneLog.errorLog.fatal(obj, throwable);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return ErrorSceneLog.errorLog.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return ErrorSceneLog.errorLog.isTraceEnabled();
    }
}
