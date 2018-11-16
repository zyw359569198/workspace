package com.reign.util.log;

import org.apache.commons.logging.*;

public class YxOperationLogger implements Logger
{
    private Log log;
    
    public YxOperationLogger() {
        this.log = LogFactory.getLog("com.reign.yxoperation");
    }
    
    @Override
    public void debug(final Object arg0) {
        this.log.debug(arg0);
    }
    
    @Override
    public void debug(final Object arg0, final Throwable arg1) {
        this.log.debug(arg0, arg1);
    }
    
    @Override
    public void info(final Object arg0) {
        this.log.info(arg0);
    }
    
    @Override
    public void info(final Object arg0, final Throwable arg1) {
        this.log.info(arg0, arg1);
    }
    
    @Override
    public void warn(final Object arg0) {
        this.log.warn(arg0);
    }
    
    @Override
    public void warn(final Object arg0, final Throwable arg1) {
        this.log.warn(arg0, arg1);
    }
    
    @Override
    public void trace(final Object arg0) {
        this.log.trace(arg0);
    }
    
    @Override
    public void trace(final Object arg0, final Throwable arg1) {
        this.log.trace(arg0, arg1);
    }
    
    @Override
    public void error(final Object arg0) {
        this.log.error(arg0);
    }
    
    @Override
    public void error(final Object arg0, final Throwable arg1) {
        this.log.error(arg0, arg1);
    }
    
    @Override
    public void fatal(final Object arg0) {
        this.log.fatal(arg0);
    }
    
    @Override
    public void fatal(final Object arg0, final Throwable arg1) {
        this.log.fatal(arg0, arg1);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return this.log.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return this.log.isTraceEnabled();
    }
    
    @Override
	public boolean isErrorEnabled() {
        return this.log.isErrorEnabled();
    }
    
    @Override
	public boolean isFatalEnabled() {
        return this.log.isFatalEnabled();
    }
    
    @Override
	public boolean isInfoEnabled() {
        return this.log.isInfoEnabled();
    }
    
    @Override
	public boolean isWarnEnabled() {
        return this.log.isWarnEnabled();
    }
}
