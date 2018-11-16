package com.reign.gcld.log;

import com.reign.gcld.common.log.*;
import org.apache.commons.logging.*;

public class TimerLogger implements Logger
{
    private static Log log;
    
    static {
        TimerLogger.log = LogFactory.getLog(TimerLogger.class);
    }
    
    @Override
    public void debug(final Object obj) {
        TimerLogger.log.debug(obj);
    }
    
    @Override
    public void debug(final Object obj, final Throwable throwable) {
        TimerLogger.log.debug(obj, throwable);
    }
    
    @Override
    public void error(final Object obj) {
        TimerLogger.log.error(obj);
    }
    
    @Override
    public void error(final Object obj, final Throwable throwable) {
        TimerLogger.log.error(obj, throwable);
    }
    
    @Override
    public void fatal(final Object obj) {
        TimerLogger.log.fatal(obj);
    }
    
    @Override
    public void fatal(final Object obj, final Throwable throwable) {
        TimerLogger.log.fatal(obj, throwable);
    }
    
    @Override
    public void info(final Object obj) {
        TimerLogger.log.info(obj);
    }
    
    @Override
    public void info(final Object obj, final Throwable throwable) {
        TimerLogger.log.info(obj, throwable);
    }
    
    @Override
    public void trace(final Object obj) {
        TimerLogger.log.trace(obj);
    }
    
    @Override
    public void trace(final Object obj, final Throwable throwable) {
        TimerLogger.log.trace(obj, throwable);
    }
    
    @Override
    public void warn(final Object obj) {
        TimerLogger.log.warn(obj);
    }
    
    @Override
    public void warn(final Object obj, final Throwable throwable) {
        TimerLogger.log.warn(obj, throwable);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return TimerLogger.log.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return TimerLogger.log.isTraceEnabled();
    }
}
