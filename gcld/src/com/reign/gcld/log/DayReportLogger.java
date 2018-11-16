package com.reign.gcld.log;

import com.reign.gcld.common.log.*;
import org.apache.commons.logging.*;

public class DayReportLogger implements Logger
{
    private static final Log log;
    
    static {
        log = LogFactory.getLog("com.reign.gcld.dayreport");
    }
    
    @Override
    public void debug(final Object arg0) {
        DayReportLogger.log.debug(arg0);
    }
    
    @Override
    public void debug(final Object arg0, final Throwable arg1) {
        DayReportLogger.log.debug(arg0);
        DayReportLogger.log.debug(arg1);
    }
    
    @Override
    public void info(final Object arg0) {
        DayReportLogger.log.info(arg0);
    }
    
    @Override
    public void info(final Object arg0, final Throwable arg1) {
        DayReportLogger.log.info(arg0);
        DayReportLogger.log.info(arg1);
    }
    
    @Override
    public void warn(final Object arg0) {
        DayReportLogger.log.warn(arg0);
    }
    
    @Override
    public void warn(final Object arg0, final Throwable arg1) {
        DayReportLogger.log.warn(arg0);
        DayReportLogger.log.warn(arg1);
    }
    
    @Override
    public void trace(final Object arg0) {
        DayReportLogger.log.trace(arg0);
    }
    
    @Override
    public void trace(final Object arg0, final Throwable arg1) {
        DayReportLogger.log.trace(arg0);
        DayReportLogger.log.trace(arg1);
    }
    
    @Override
    public void error(final Object arg0) {
        DayReportLogger.log.error(arg0);
    }
    
    @Override
    public void error(final Object arg0, final Throwable arg1) {
        DayReportLogger.log.error(arg0);
        DayReportLogger.log.error(arg1);
    }
    
    @Override
    public void fatal(final Object arg0) {
        DayReportLogger.log.fatal(arg0);
    }
    
    @Override
    public void fatal(final Object arg0, final Throwable arg1) {
        DayReportLogger.log.fatal(arg0);
        DayReportLogger.log.fatal(arg1);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return DayReportLogger.log.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return DayReportLogger.log.isTraceEnabled();
    }
}
