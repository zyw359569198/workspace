package com.reign.gcld.log;

import org.apache.commons.logging.*;

public class KfgzLogger implements Log
{
    private static Log log;
    
    static {
        KfgzLogger.log = LogFactory.getLog("com.reign.KfgzLogger");
    }
    
    @Override
	public void debug(final Object arg0) {
        KfgzLogger.log.debug(arg0);
    }
    
    @Override
	public void debug(final Object arg0, final Throwable arg1) {
        KfgzLogger.log.debug(arg0);
        KfgzLogger.log.debug(arg1);
    }
    
    @Override
	public void info(final Object arg0) {
        KfgzLogger.log.info(arg0);
    }
    
    @Override
	public void info(final Object arg0, final Throwable arg1) {
        KfgzLogger.log.info(arg0);
        KfgzLogger.log.info(arg1);
    }
    
    @Override
	public void warn(final Object arg0) {
        KfgzLogger.log.warn(arg0);
    }
    
    @Override
	public void warn(final Object arg0, final Throwable arg1) {
        KfgzLogger.log.warn(arg0);
        KfgzLogger.log.warn(arg1);
    }
    
    @Override
	public void trace(final Object arg0) {
        KfgzLogger.log.trace(arg0);
    }
    
    @Override
	public void trace(final Object arg0, final Throwable arg1) {
        KfgzLogger.log.trace(arg0);
        KfgzLogger.log.trace(arg1);
    }
    
    @Override
	public void error(final Object arg0) {
        KfgzLogger.log.error(arg0);
    }
    
    @Override
	public void error(final Object arg0, final Throwable arg1) {
        KfgzLogger.log.error(arg0);
        KfgzLogger.log.error(arg1);
    }
    
    @Override
	public void fatal(final Object arg0) {
        KfgzLogger.log.fatal(arg0);
    }
    
    @Override
	public void fatal(final Object arg0, final Throwable arg1) {
        KfgzLogger.log.fatal(arg0);
        KfgzLogger.log.fatal(arg1);
    }
    
    @Override
	public boolean isDebugEnabled() {
        return KfgzLogger.log.isDebugEnabled();
    }
    
    @Override
	public boolean isErrorEnabled() {
        return KfgzLogger.log.isErrorEnabled();
    }
    
    @Override
	public boolean isFatalEnabled() {
        return KfgzLogger.log.isFatalEnabled();
    }
    
    @Override
	public boolean isInfoEnabled() {
        return KfgzLogger.log.isInfoEnabled();
    }
    
    @Override
	public boolean isTraceEnabled() {
        return KfgzLogger.log.isTraceEnabled();
    }
    
    @Override
	public boolean isWarnEnabled() {
        return KfgzLogger.log.isWarnEnabled();
    }
}
