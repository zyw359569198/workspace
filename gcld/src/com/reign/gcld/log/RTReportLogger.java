package com.reign.gcld.log;

import com.reign.gcld.common.log.*;
import org.apache.commons.logging.*;

public class RTReportLogger implements Logger
{
    private static final Log log;
    
    static {
        log = LogFactory.getLog("com.reign.gcld.rtreport");
    }
    
    @Override
    public void debug(final Object arg0) {
        RTReportLogger.log.debug(arg0);
    }
    
    @Override
    public void debug(final Object arg0, final Throwable arg1) {
        RTReportLogger.log.debug(arg0);
        RTReportLogger.log.debug(arg1);
    }
    
    @Override
    public void info(final Object arg0) {
        RTReportLogger.log.info(arg0);
    }
    
    @Override
    public void info(final Object arg0, final Throwable arg1) {
        RTReportLogger.log.info(arg0);
        RTReportLogger.log.info(arg1);
    }
    
    @Override
    public void warn(final Object arg0) {
        RTReportLogger.log.warn(arg0);
    }
    
    @Override
    public void warn(final Object arg0, final Throwable arg1) {
        RTReportLogger.log.warn(arg0);
        RTReportLogger.log.warn(arg1);
    }
    
    @Override
    public void trace(final Object arg0) {
        RTReportLogger.log.trace(arg0);
    }
    
    @Override
    public void trace(final Object arg0, final Throwable arg1) {
        RTReportLogger.log.trace(arg0);
        RTReportLogger.log.trace(arg1);
    }
    
    @Override
    public void error(final Object arg0) {
        RTReportLogger.log.error(arg0);
    }
    
    @Override
    public void error(final Object arg0, final Throwable arg1) {
        RTReportLogger.log.error(arg0);
        RTReportLogger.log.error(arg1);
    }
    
    @Override
    public void fatal(final Object arg0) {
        RTReportLogger.log.fatal(arg0);
    }
    
    @Override
    public void fatal(final Object arg0, final Throwable arg1) {
        RTReportLogger.log.fatal(arg0);
        RTReportLogger.log.fatal(arg1);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return RTReportLogger.log.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return RTReportLogger.log.isTraceEnabled();
    }
}
