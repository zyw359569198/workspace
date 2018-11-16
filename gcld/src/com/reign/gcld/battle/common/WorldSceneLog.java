package com.reign.gcld.battle.common;

import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import java.io.*;

public class WorldSceneLog implements Logger, Serializable
{
    private static final long serialVersionUID = 1013122147908259288L;
    private static final Logger worldLog;
    private StringBuilder buffer;
    
    static {
        worldLog = new WorldLogger();
    }
    
    private WorldSceneLog() {
        this.buffer = null;
    }
    
    public static WorldSceneLog getInstance() {
        final WorldSceneLog instance = new WorldSceneLog();
        instance.buffer = new StringBuilder();
        return instance;
    }
    
    public WorldSceneLog appendBattleId(final String battleId) {
        this.buffer.append("battleId:" + battleId + " | ");
        return this;
    }
    
    public WorldSceneLog appendCityId(final int cityId) {
        this.buffer.append("cityId:" + cityId + " | ");
        return this;
    }
    
    public WorldSceneLog appendCityName(final String cityName) {
        this.buffer.append("city:" + cityName + " | ");
        return this;
    }
    
    public WorldSceneLog appendPlayerId(final int PlayerId) {
        this.buffer.append("pId:" + PlayerId + " | ");
        return this;
    }
    
    public WorldSceneLog appendPlayerName(final String PlayerName) {
        this.buffer.append("pName:" + PlayerName + " | ");
        return this;
    }
    
    public WorldSceneLog appendAttForceId(final int attForceId) {
        this.buffer.append("attForce:" + attForceId + " | ");
        return this;
    }
    
    public WorldSceneLog appendDefForceId(final int defForceId) {
        this.buffer.append("defForce:" + defForceId + " | ");
        return this;
    }
    
    public WorldSceneLog appendGeneralId(final int generalId) {
        this.buffer.append("gId:" + generalId + " | ");
        return this;
    }
    
    public WorldSceneLog appendGeneralName(final String generalName) {
        this.buffer.append("gName:" + generalName + " | ");
        return this;
    }
    
    public WorldSceneLog appendClassName(final String ClassName) {
        this.buffer.append("ClassName:" + ClassName + " | ");
        return this;
    }
    
    public WorldSceneLog appendMethodName(final String methodName) {
        this.buffer.append("methodName:" + methodName + " | ");
        return this;
    }
    
    public WorldSceneLog appendLogMsg(final String LogMsg) {
        this.buffer.append("Msg:" + LogMsg + " | ");
        return this;
    }
    
    public WorldSceneLog append(final String fieldName, final Object fieldValue) {
        this.buffer.append(String.valueOf(fieldName) + ":" + fieldValue + " | ");
        return this;
    }
    
    public WorldSceneLog appendOthers(final String others) {
        this.buffer.append("others:" + others + " | ");
        return this;
    }
    
    public WorldSceneLog newLine() {
        this.buffer.append("\n");
        return this;
    }
    
    public WorldSceneLog Indent() {
        this.buffer.append("\t");
        return this;
    }
    
    public void flush() {
        WorldSceneLog.worldLog.error(this.buffer.toString());
        this.buffer.delete(0, this.buffer.length());
    }
    
    @Override
    public void trace(final Object obj) {
        WorldSceneLog.worldLog.trace(obj);
    }
    
    @Override
    public void trace(final Object obj, final Throwable throwable) {
        WorldSceneLog.worldLog.trace(obj, throwable);
    }
    
    @Override
    public void debug(final Object obj) {
        WorldSceneLog.worldLog.debug(obj);
    }
    
    @Override
    public void debug(final Object obj, final Throwable throwable) {
        WorldSceneLog.worldLog.debug(obj, throwable);
    }
    
    @Override
    public void info(final Object obj) {
        WorldSceneLog.worldLog.info(obj);
    }
    
    @Override
    public void info(final Object obj, final Throwable throwable) {
        WorldSceneLog.worldLog.info(obj, throwable);
    }
    
    @Override
    public void warn(final Object obj) {
        WorldSceneLog.worldLog.warn(obj);
    }
    
    @Override
    public void warn(final Object obj, final Throwable throwable) {
        WorldSceneLog.worldLog.warn(obj, throwable);
    }
    
    @Override
    public void error(final Object obj) {
        WorldSceneLog.worldLog.error(obj);
    }
    
    @Override
    public void error(final Object obj, final Throwable throwable) {
        WorldSceneLog.worldLog.error(obj, throwable);
    }
    
    @Override
    public void fatal(final Object obj) {
        WorldSceneLog.worldLog.fatal(obj);
    }
    
    @Override
    public void fatal(final Object obj, final Throwable throwable) {
        WorldSceneLog.worldLog.fatal(obj, throwable);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return WorldSceneLog.worldLog.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return WorldSceneLog.worldLog.isTraceEnabled();
    }
}
