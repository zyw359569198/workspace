package com.reign.util.log;

import org.apache.commons.logging.*;

public class FlashErrorLogger implements Logger
{
    private static final Log log;
    
    static {
        log = LogFactory.getLog("com.reign.flasherror");
    }
    
    @Override
    public void debug(final Object arg0) {
        FlashErrorLogger.log.debug(arg0);
    }
    
    @Override
    public void debug(final Object arg0, final Throwable arg1) {
        FlashErrorLogger.log.debug(arg0, arg1);
    }
    
    @Override
    public void info(final Object arg0) {
        FlashErrorLogger.log.info(arg0);
    }
    
    @Override
    public void info(final Object arg0, final Throwable arg1) {
        FlashErrorLogger.log.info(arg0, arg1);
    }
    
    @Override
    public void warn(final Object arg0) {
        FlashErrorLogger.log.warn(arg0);
    }
    
    @Override
    public void warn(final Object arg0, final Throwable arg1) {
        FlashErrorLogger.log.warn(arg0, arg1);
    }
    
    @Override
    public void trace(final Object arg0) {
        FlashErrorLogger.log.trace(arg0);
    }
    
    @Override
    public void trace(final Object arg0, final Throwable arg1) {
        FlashErrorLogger.log.trace(arg0, arg1);
    }
    
    @Override
    public void error(final Object arg0) {
        FlashErrorLogger.log.error(arg0);
    }
    
    @Override
    public void error(final Object arg0, final Throwable arg1) {
        FlashErrorLogger.log.error(arg0, arg1);
    }
    
    @Override
    public void fatal(final Object arg0) {
        FlashErrorLogger.log.fatal(arg0);
    }
    
    @Override
    public void fatal(final Object arg0, final Throwable arg1) {
        FlashErrorLogger.log.fatal(arg0, arg1);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return FlashErrorLogger.log.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return FlashErrorLogger.log.isTraceEnabled();
    }
    
    @Override
	public boolean isErrorEnabled() {
        return FlashErrorLogger.log.isErrorEnabled();
    }
    
    @Override
	public boolean isFatalEnabled() {
        return FlashErrorLogger.log.isFatalEnabled();
    }
    
    @Override
	public boolean isInfoEnabled() {
        return FlashErrorLogger.log.isInfoEnabled();
    }
    
    @Override
	public boolean isWarnEnabled() {
        return FlashErrorLogger.log.isWarnEnabled();
    }
}
