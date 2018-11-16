package com.reign.gcld.log;

import org.apache.commons.logging.*;

public class KfzbLogger implements Log
{
    private static Log log;
    
    static {
        KfzbLogger.log = LogFactory.getLog("com.reign.KfzbLogger");
    }
    
    @Override
	public void debug(final Object arg0) {
        KfzbLogger.log.debug(arg0);
    }
    
    @Override
	public void debug(final Object arg0, final Throwable arg1) {
        KfzbLogger.log.debug(arg0);
        KfzbLogger.log.debug(arg1);
    }
    
    @Override
	public void info(final Object arg0) {
        KfzbLogger.log.info(arg0);
    }
    
    @Override
	public void info(final Object arg0, final Throwable arg1) {
        KfzbLogger.log.info(arg0);
        KfzbLogger.log.info(arg1);
    }
    
    @Override
	public void warn(final Object arg0) {
        KfzbLogger.log.warn(arg0);
    }
    
    @Override
	public void warn(final Object arg0, final Throwable arg1) {
        KfzbLogger.log.warn(arg0);
        KfzbLogger.log.warn(arg1);
    }
    
    @Override
	public void trace(final Object arg0) {
        KfzbLogger.log.trace(arg0);
    }
    
    @Override
	public void trace(final Object arg0, final Throwable arg1) {
        KfzbLogger.log.trace(arg0);
        KfzbLogger.log.trace(arg1);
    }
    
    @Override
	public void error(final Object arg0) {
        KfzbLogger.log.error(arg0);
    }
    
    @Override
	public void error(final Object arg0, final Throwable arg1) {
        KfzbLogger.log.error(arg0);
        KfzbLogger.log.error(arg1);
    }
    
    @Override
	public void fatal(final Object arg0) {
        KfzbLogger.log.fatal(arg0);
    }
    
    @Override
	public void fatal(final Object arg0, final Throwable arg1) {
        KfzbLogger.log.fatal(arg0);
        KfzbLogger.log.fatal(arg1);
    }
    
    @Override
	public boolean isDebugEnabled() {
        return KfzbLogger.log.isDebugEnabled();
    }
    
    @Override
	public boolean isErrorEnabled() {
        return KfzbLogger.log.isErrorEnabled();
    }
    
    @Override
	public boolean isFatalEnabled() {
        return KfzbLogger.log.isFatalEnabled();
    }
    
    @Override
	public boolean isInfoEnabled() {
        return KfzbLogger.log.isInfoEnabled();
    }
    
    @Override
	public boolean isTraceEnabled() {
        return KfzbLogger.log.isTraceEnabled();
    }
    
    @Override
	public boolean isWarnEnabled() {
        return KfzbLogger.log.isWarnEnabled();
    }
}
