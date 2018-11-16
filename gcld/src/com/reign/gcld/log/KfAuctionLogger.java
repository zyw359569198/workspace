package com.reign.gcld.log;

import org.apache.commons.logging.*;

public class KfAuctionLogger implements Log
{
    private static Log log;
    
    static {
        KfAuctionLogger.log = LogFactory.getLog("com.reign.kfauctionLogger");
    }
    
    @Override
	public void debug(final Object arg0) {
        KfAuctionLogger.log.debug(arg0);
    }
    
    @Override
	public void debug(final Object arg0, final Throwable arg1) {
        KfAuctionLogger.log.debug(arg0);
        KfAuctionLogger.log.debug(arg1);
    }
    
    @Override
	public void info(final Object arg0) {
        KfAuctionLogger.log.info(arg0);
    }
    
    @Override
	public void info(final Object arg0, final Throwable arg1) {
        KfAuctionLogger.log.info(arg0);
        KfAuctionLogger.log.info(arg1);
    }
    
    @Override
	public void warn(final Object arg0) {
        KfAuctionLogger.log.warn(arg0);
    }
    
    @Override
	public void warn(final Object arg0, final Throwable arg1) {
        KfAuctionLogger.log.warn(arg0);
        KfAuctionLogger.log.warn(arg1);
    }
    
    @Override
	public void trace(final Object arg0) {
        KfAuctionLogger.log.trace(arg0);
    }
    
    @Override
	public void trace(final Object arg0, final Throwable arg1) {
        KfAuctionLogger.log.trace(arg0);
        KfAuctionLogger.log.trace(arg1);
    }
    
    @Override
	public void error(final Object arg0) {
        KfAuctionLogger.log.error(arg0);
    }
    
    @Override
	public void error(final Object arg0, final Throwable arg1) {
        KfAuctionLogger.log.error(arg0);
        KfAuctionLogger.log.error(arg1);
    }
    
    @Override
	public void fatal(final Object arg0) {
        KfAuctionLogger.log.fatal(arg0);
    }
    
    @Override
	public void fatal(final Object arg0, final Throwable arg1) {
        KfAuctionLogger.log.fatal(arg0);
        KfAuctionLogger.log.fatal(arg1);
    }
    
    @Override
	public boolean isDebugEnabled() {
        return KfAuctionLogger.log.isDebugEnabled();
    }
    
    @Override
	public boolean isErrorEnabled() {
        return KfAuctionLogger.log.isErrorEnabled();
    }
    
    @Override
	public boolean isFatalEnabled() {
        return KfAuctionLogger.log.isFatalEnabled();
    }
    
    @Override
	public boolean isInfoEnabled() {
        return KfAuctionLogger.log.isInfoEnabled();
    }
    
    @Override
	public boolean isTraceEnabled() {
        return KfAuctionLogger.log.isTraceEnabled();
    }
    
    @Override
	public boolean isWarnEnabled() {
        return KfAuctionLogger.log.isWarnEnabled();
    }
}
