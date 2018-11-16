package com.reign.gcld.log;

import com.reign.gcld.common.log.*;
import org.apache.commons.logging.*;

public class WorldLogger implements Logger
{
    private static Log log;
    
    static {
        WorldLogger.log = LogFactory.getLog(WorldLogger.class);
    }
    
    @Override
    public void trace(final Object obj) {
        WorldLogger.log.trace(obj);
    }
    
    @Override
    public void trace(final Object obj, final Throwable throwable) {
        WorldLogger.log.trace(obj, throwable);
    }
    
    @Override
    public void debug(final Object obj) {
        WorldLogger.log.debug(obj);
    }
    
    @Override
    public void debug(final Object obj, final Throwable throwable) {
        WorldLogger.log.debug(obj, throwable);
    }
    
    @Override
    public void info(final Object obj) {
        WorldLogger.log.info(obj);
    }
    
    @Override
    public void info(final Object obj, final Throwable throwable) {
        WorldLogger.log.info(obj, throwable);
    }
    
    @Override
    public void warn(final Object obj) {
        WorldLogger.log.warn(obj);
    }
    
    @Override
    public void warn(final Object obj, final Throwable throwable) {
        WorldLogger.log.warn(obj, throwable);
    }
    
    @Override
    public void error(final Object obj) {
        WorldLogger.log.error(obj);
    }
    
    @Override
    public void error(final Object obj, final Throwable throwable) {
        WorldLogger.log.error(obj, throwable);
    }
    
    @Override
    public void fatal(final Object obj) {
        WorldLogger.log.fatal(obj);
    }
    
    @Override
    public void fatal(final Object obj, final Throwable throwable) {
        WorldLogger.log.fatal(obj, throwable);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return WorldLogger.log.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return WorldLogger.log.isTraceEnabled();
    }
}
