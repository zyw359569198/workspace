package com.reign.util.log;

import org.apache.commons.logging.*;

public class DayReportLogger implements Logger
{
    private static final Log log;
    
    static {
        log = LogFactory.getLog("com.reign.dayreport");
    }
    
    @Override
    public void debug(final Object arg0) {
        DayReportLogger.log.debug(arg0);
    }
    
    @Override
    public void debug(final Object arg0, final Throwable arg1) {
        DayReportLogger.log.debug(arg0, arg1);
    }
    
    @Override
    public void info(final Object arg0) {
        DayReportLogger.log.info(arg0);
    }
    
    @Override
    public void info(final Object arg0, final Throwable arg1) {
        DayReportLogger.log.info(arg0, arg1);
    }
    
    @Override
    public void warn(final Object arg0) {
        DayReportLogger.log.warn(arg0);
    }
    
    @Override
    public void warn(final Object arg0, final Throwable arg1) {
        DayReportLogger.log.warn(arg0, arg1);
    }
    
    @Override
    public void trace(final Object arg0) {
        DayReportLogger.log.trace(arg0);
    }
    
    @Override
    public void trace(final Object arg0, final Throwable arg1) {
        DayReportLogger.log.trace(arg0, arg1);
    }
    
    @Override
    public void error(final Object arg0) {
        DayReportLogger.log.error(arg0);
    }
    
    @Override
    public void error(final Object arg0, final Throwable arg1) {
        DayReportLogger.log.error(arg0, arg1);
    }
    
    @Override
    public void fatal(final Object arg0) {
        DayReportLogger.log.fatal(arg0);
    }
    
    @Override
    public void fatal(final Object arg0, final Throwable arg1) {
        DayReportLogger.log.fatal(arg0, arg1);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return DayReportLogger.log.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return DayReportLogger.log.isTraceEnabled();
    }
    
    @Override
	public boolean isErrorEnabled() {
        return DayReportLogger.log.isErrorEnabled();
    }
    
    @Override
	public boolean isFatalEnabled() {
        return DayReportLogger.log.isFatalEnabled();
    }
    
    @Override
	public boolean isInfoEnabled() {
        return DayReportLogger.log.isInfoEnabled();
    }
    
    @Override
	public boolean isWarnEnabled() {
        return DayReportLogger.log.isWarnEnabled();
    }
}
