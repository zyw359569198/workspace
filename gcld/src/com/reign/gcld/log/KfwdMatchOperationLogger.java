package com.reign.gcld.log;

import org.apache.commons.logging.*;

public class KfwdMatchOperationLogger implements Log
{
    private static Log log;
    
    static {
        KfwdMatchOperationLogger.log = LogFactory.getLog("com.reign.KfwdMatchOperationLogger");
    }
    
    @Override
	public void debug(final Object arg0) {
        KfwdMatchOperationLogger.log.debug(arg0);
    }
    
    @Override
	public void debug(final Object arg0, final Throwable arg1) {
        KfwdMatchOperationLogger.log.debug(arg0);
        KfwdMatchOperationLogger.log.debug(arg1);
    }
    
    @Override
	public void info(final Object arg0) {
        KfwdMatchOperationLogger.log.info(arg0);
    }
    
    @Override
	public void info(final Object arg0, final Throwable arg1) {
        KfwdMatchOperationLogger.log.info(arg0);
        KfwdMatchOperationLogger.log.info(arg1);
    }
    
    @Override
	public void warn(final Object arg0) {
        KfwdMatchOperationLogger.log.warn(arg0);
    }
    
    @Override
	public void warn(final Object arg0, final Throwable arg1) {
        KfwdMatchOperationLogger.log.warn(arg0);
        KfwdMatchOperationLogger.log.warn(arg1);
    }
    
    @Override
	public void trace(final Object arg0) {
        KfwdMatchOperationLogger.log.trace(arg0);
    }
    
    @Override
	public void trace(final Object arg0, final Throwable arg1) {
        KfwdMatchOperationLogger.log.trace(arg0);
        KfwdMatchOperationLogger.log.trace(arg1);
    }
    
    @Override
	public void error(final Object arg0) {
        KfwdMatchOperationLogger.log.error(arg0);
    }
    
    @Override
	public void error(final Object arg0, final Throwable arg1) {
        KfwdMatchOperationLogger.log.error(arg0);
        KfwdMatchOperationLogger.log.error(arg1);
    }
    
    @Override
	public void fatal(final Object arg0) {
        KfwdMatchOperationLogger.log.fatal(arg0);
    }
    
    @Override
	public void fatal(final Object arg0, final Throwable arg1) {
        KfwdMatchOperationLogger.log.fatal(arg0);
        KfwdMatchOperationLogger.log.fatal(arg1);
    }
    
    @Override
	public boolean isDebugEnabled() {
        return KfwdMatchOperationLogger.log.isDebugEnabled();
    }
    
    @Override
	public boolean isErrorEnabled() {
        return KfwdMatchOperationLogger.log.isErrorEnabled();
    }
    
    @Override
	public boolean isFatalEnabled() {
        return KfwdMatchOperationLogger.log.isFatalEnabled();
    }
    
    @Override
	public boolean isInfoEnabled() {
        return KfwdMatchOperationLogger.log.isInfoEnabled();
    }
    
    @Override
	public boolean isTraceEnabled() {
        return KfwdMatchOperationLogger.log.isTraceEnabled();
    }
    
    @Override
	public boolean isWarnEnabled() {
        return KfwdMatchOperationLogger.log.isWarnEnabled();
    }
}
