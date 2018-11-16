package com.reign.gcld.log;

import org.apache.commons.logging.*;

public class FeastLogger implements Log
{
    private static Log log;
    
    static {
        FeastLogger.log = LogFactory.getLog(FeastLogger.class);
    }
    
    @Override
	public void debug(final Object arg0) {
        FeastLogger.log.debug(arg0);
    }
    
    @Override
	public void debug(final Object arg0, final Throwable arg1) {
        FeastLogger.log.debug(arg0);
        FeastLogger.log.debug(arg1);
    }
    
    @Override
	public void info(final Object arg0) {
        FeastLogger.log.info(arg0);
    }
    
    @Override
	public void info(final Object arg0, final Throwable arg1) {
        FeastLogger.log.info(arg0);
        FeastLogger.log.info(arg1);
    }
    
    @Override
	public void warn(final Object arg0) {
        FeastLogger.log.warn(arg0);
    }
    
    @Override
	public void warn(final Object arg0, final Throwable arg1) {
        FeastLogger.log.warn(arg0);
        FeastLogger.log.warn(arg1);
    }
    
    @Override
	public void trace(final Object arg0) {
        FeastLogger.log.trace(arg0);
    }
    
    @Override
	public void trace(final Object arg0, final Throwable arg1) {
        FeastLogger.log.trace(arg0);
        FeastLogger.log.trace(arg1);
    }
    
    @Override
	public void error(final Object arg0) {
        FeastLogger.log.error(arg0);
    }
    
    @Override
	public void error(final Object arg0, final Throwable arg1) {
        FeastLogger.log.error(arg0);
        FeastLogger.log.error(arg1);
    }
    
    @Override
	public void fatal(final Object arg0) {
        FeastLogger.log.fatal(arg0);
    }
    
    @Override
	public void fatal(final Object arg0, final Throwable arg1) {
        FeastLogger.log.fatal(arg0);
        FeastLogger.log.fatal(arg1);
    }
    
    @Override
	public boolean isDebugEnabled() {
        return FeastLogger.log.isDebugEnabled();
    }
    
    @Override
	public boolean isErrorEnabled() {
        return FeastLogger.log.isErrorEnabled();
    }
    
    @Override
	public boolean isFatalEnabled() {
        return FeastLogger.log.isFatalEnabled();
    }
    
    @Override
	public boolean isInfoEnabled() {
        return FeastLogger.log.isInfoEnabled();
    }
    
    @Override
	public boolean isTraceEnabled() {
        return FeastLogger.log.isTraceEnabled();
    }
    
    @Override
	public boolean isWarnEnabled() {
        return FeastLogger.log.isWarnEnabled();
    }
}
