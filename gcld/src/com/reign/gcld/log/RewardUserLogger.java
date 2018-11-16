package com.reign.gcld.log;

import com.reign.gcld.common.log.*;
import org.apache.commons.logging.*;

public class RewardUserLogger implements Logger
{
    private static final Log log;
    
    static {
        log = LogFactory.getLog("com.reign.gcld.rewarduser");
    }
    
    @Override
    public void debug(final Object arg0) {
        RewardUserLogger.log.debug(arg0);
    }
    
    @Override
    public void debug(final Object arg0, final Throwable arg1) {
        RewardUserLogger.log.debug(arg0);
        RewardUserLogger.log.debug(arg1);
    }
    
    @Override
    public void info(final Object arg0) {
        RewardUserLogger.log.info(arg0);
    }
    
    @Override
    public void info(final Object arg0, final Throwable arg1) {
        RewardUserLogger.log.info(arg0);
        RewardUserLogger.log.info(arg1);
    }
    
    @Override
    public void warn(final Object arg0) {
        RewardUserLogger.log.warn(arg0);
    }
    
    @Override
    public void warn(final Object arg0, final Throwable arg1) {
        RewardUserLogger.log.warn(arg0);
        RewardUserLogger.log.warn(arg1);
    }
    
    @Override
    public void trace(final Object arg0) {
        RewardUserLogger.log.trace(arg0);
    }
    
    @Override
    public void trace(final Object arg0, final Throwable arg1) {
        RewardUserLogger.log.trace(arg0);
        RewardUserLogger.log.trace(arg1);
    }
    
    @Override
    public void error(final Object arg0) {
        RewardUserLogger.log.error(arg0);
    }
    
    @Override
    public void error(final Object arg0, final Throwable arg1) {
        RewardUserLogger.log.error(arg0);
        RewardUserLogger.log.error(arg1);
    }
    
    @Override
    public void fatal(final Object arg0) {
        RewardUserLogger.log.fatal(arg0);
    }
    
    @Override
    public void fatal(final Object arg0, final Throwable arg1) {
        RewardUserLogger.log.fatal(arg0);
        RewardUserLogger.log.fatal(arg1);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return RewardUserLogger.log.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return RewardUserLogger.log.isTraceEnabled();
    }
}
