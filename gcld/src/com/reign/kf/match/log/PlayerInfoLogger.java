package com.reign.kf.match.log;

import org.apache.commons.logging.*;

public class PlayerInfoLogger implements Logger
{
    private static Log log;
    
    static {
        PlayerInfoLogger.log = LogFactory.getLog("com.reign.kf.match.DayReportLogger");
    }
    
    @Override
    public void debug(final Object arg0) {
        PlayerInfoLogger.log.debug(arg0);
    }
    
    @Override
    public void debug(final Object arg0, final Throwable arg1) {
        PlayerInfoLogger.log.debug(arg0, arg1);
    }
    
    @Override
    public void info(final Object arg0) {
        PlayerInfoLogger.log.info(arg0);
    }
    
    @Override
    public void info(final Object arg0, final Throwable arg1) {
        PlayerInfoLogger.log.info(arg0, arg1);
    }
    
    @Override
    public void warn(final Object arg0) {
        PlayerInfoLogger.log.warn(arg0);
    }
    
    @Override
    public void warn(final Object arg0, final Throwable arg1) {
        PlayerInfoLogger.log.warn(arg0, arg1);
    }
    
    @Override
    public void trace(final Object arg0) {
        PlayerInfoLogger.log.trace(arg0);
    }
    
    @Override
    public void trace(final Object arg0, final Throwable arg1) {
        PlayerInfoLogger.log.trace(arg0, arg1);
    }
    
    @Override
    public void error(final Object arg0) {
        PlayerInfoLogger.log.error(arg0);
    }
    
    @Override
    public void error(final Object arg0, final Throwable arg1) {
        PlayerInfoLogger.log.error(arg0, arg1);
    }
    
    @Override
    public void fatal(final Object arg0) {
        PlayerInfoLogger.log.fatal(arg0);
    }
    
    @Override
    public void fatal(final Object arg0, final Throwable arg1) {
        PlayerInfoLogger.log.fatal(arg0, arg1);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return PlayerInfoLogger.log.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return PlayerInfoLogger.log.isTraceEnabled();
    }
    
    @Override
    public boolean isErrorEnabled() {
        return PlayerInfoLogger.log.isErrorEnabled();
    }
    
    @Override
    public boolean isFatalEnabled() {
        return PlayerInfoLogger.log.isFatalEnabled();
    }
    
    @Override
    public boolean isInfoEnabled() {
        return PlayerInfoLogger.log.isInfoEnabled();
    }
    
    @Override
    public boolean isWarnEnabled() {
        return PlayerInfoLogger.log.isWarnEnabled();
    }
}
