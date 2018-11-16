package com.reign.gcld.log;

import com.reign.gcld.common.log.*;
import org.apache.commons.logging.*;

public class BattleLogger implements Logger
{
    private static Log log;
    
    static {
        BattleLogger.log = LogFactory.getLog(BattleLogger.class);
    }
    
    @Override
    public void debug(final Object obj) {
        BattleLogger.log.debug(obj);
    }
    
    @Override
    public void debug(final Object obj, final Throwable throwable) {
        BattleLogger.log.debug(obj, throwable);
    }
    
    @Override
    public void error(final Object obj) {
        BattleLogger.log.error(obj);
    }
    
    @Override
    public void error(final Object obj, final Throwable throwable) {
        BattleLogger.log.error(obj, throwable);
    }
    
    @Override
    public void fatal(final Object obj) {
        BattleLogger.log.fatal(obj);
    }
    
    @Override
    public void fatal(final Object obj, final Throwable throwable) {
        BattleLogger.log.fatal(obj, throwable);
    }
    
    @Override
    public void info(final Object obj) {
        BattleLogger.log.info(obj);
    }
    
    @Override
    public void info(final Object obj, final Throwable throwable) {
        BattleLogger.log.info(obj, throwable);
    }
    
    @Override
    public void trace(final Object obj) {
        BattleLogger.log.trace(obj);
    }
    
    @Override
    public void trace(final Object obj, final Throwable throwable) {
        BattleLogger.log.trace(obj, throwable);
    }
    
    @Override
    public void warn(final Object obj) {
        BattleLogger.log.warn(obj);
    }
    
    @Override
    public void warn(final Object obj, final Throwable throwable) {
        BattleLogger.log.warn(obj, throwable);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return BattleLogger.log.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return BattleLogger.log.isTraceEnabled();
    }
}
