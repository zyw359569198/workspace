package com.reign.gcld.log;

import com.reign.gcld.common.log.*;
import org.apache.commons.logging.*;

public class OpReportLogger implements Logger
{
    private static final Log log;
    
    static {
        log = LogFactory.getLog("com.reign.gcld.opreport");
    }
    
    @Override
    public void debug(final Object arg0) {
        OpReportLogger.log.debug(arg0);
    }
    
    @Override
    public void debug(final Object arg0, final Throwable arg1) {
        OpReportLogger.log.debug(arg0);
        OpReportLogger.log.debug(arg1);
    }
    
    @Override
    public void info(final Object arg0) {
        OpReportLogger.log.info(arg0);
    }
    
    @Override
    public void info(final Object arg0, final Throwable arg1) {
        OpReportLogger.log.info(arg0);
        OpReportLogger.log.info(arg1);
    }
    
    @Override
    public void warn(final Object arg0) {
        OpReportLogger.log.warn(arg0);
    }
    
    @Override
    public void warn(final Object arg0, final Throwable arg1) {
        OpReportLogger.log.warn(arg0);
        OpReportLogger.log.warn(arg1);
    }
    
    @Override
    public void trace(final Object arg0) {
        OpReportLogger.log.trace(arg0);
    }
    
    @Override
    public void trace(final Object arg0, final Throwable arg1) {
        OpReportLogger.log.trace(arg0);
        OpReportLogger.log.trace(arg1);
    }
    
    @Override
    public void error(final Object arg0) {
        OpReportLogger.log.error(arg0);
    }
    
    @Override
    public void error(final Object arg0, final Throwable arg1) {
        OpReportLogger.log.error(arg0);
        OpReportLogger.log.error(arg1);
    }
    
    @Override
    public void fatal(final Object arg0) {
        OpReportLogger.log.fatal(arg0);
    }
    
    @Override
    public void fatal(final Object arg0, final Throwable arg1) {
        OpReportLogger.log.fatal(arg0);
        OpReportLogger.log.fatal(arg1);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return OpReportLogger.log.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return OpReportLogger.log.isTraceEnabled();
    }
}
