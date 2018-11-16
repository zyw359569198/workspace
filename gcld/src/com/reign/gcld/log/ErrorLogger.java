package com.reign.gcld.log;

import com.reign.gcld.common.log.*;
import org.apache.commons.logging.*;

public class ErrorLogger implements Logger
{
    private static final Log log;
    
    static {
        log = LogFactory.getLog("com.reign.gcld.error");
    }
    
    @Override
    public void debug(final Object arg0) {
        ErrorLogger.log.debug(arg0);
    }
    
    @Override
    public void debug(final Object arg0, final Throwable arg1) {
        ErrorLogger.log.debug(arg0, arg1);
    }
    
    @Override
    public void info(final Object arg0) {
        ErrorLogger.log.info(arg0);
    }
    
    @Override
    public void info(final Object arg0, final Throwable arg1) {
        ErrorLogger.log.info(arg0, arg1);
    }
    
    @Override
    public void warn(final Object arg0) {
        ErrorLogger.log.warn(arg0);
    }
    
    @Override
    public void warn(final Object arg0, final Throwable arg1) {
        ErrorLogger.log.warn(arg0, arg1);
    }
    
    @Override
    public void trace(final Object arg0) {
        ErrorLogger.log.trace(arg0);
    }
    
    @Override
    public void trace(final Object arg0, final Throwable arg1) {
        ErrorLogger.log.trace(arg0, arg1);
    }
    
    @Override
    public void error(final Object arg0) {
        ErrorLogger.log.error(arg0);
    }
    
    @Override
    public void error(final Object arg0, final Throwable arg1) {
        ErrorLogger.log.error(arg0, arg1);
    }
    
    @Override
    public void fatal(final Object arg0) {
        ErrorLogger.log.fatal(arg0);
    }
    
    @Override
    public void fatal(final Object arg0, final Throwable arg1) {
        ErrorLogger.log.fatal(arg0, arg1);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return ErrorLogger.log.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return ErrorLogger.log.isTraceEnabled();
    }
}
