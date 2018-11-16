package com.reign.gcld.log;

import com.reign.gcld.common.log.*;
import org.apache.commons.logging.*;

public class InterfaceLogger implements Logger
{
    private static final Log log;
    
    static {
        log = LogFactory.getLog("com.reign.gcld.interface");
    }
    
    @Override
    public void debug(final Object arg0) {
        InterfaceLogger.log.debug(arg0);
    }
    
    @Override
    public void debug(final Object arg0, final Throwable arg1) {
        InterfaceLogger.log.debug(arg0);
        InterfaceLogger.log.debug(arg1);
    }
    
    @Override
    public void info(final Object arg0) {
        InterfaceLogger.log.info(arg0);
    }
    
    @Override
    public void info(final Object arg0, final Throwable arg1) {
        InterfaceLogger.log.info(arg0);
        InterfaceLogger.log.info(arg1);
    }
    
    @Override
    public void warn(final Object arg0) {
        InterfaceLogger.log.warn(arg0);
    }
    
    @Override
    public void warn(final Object arg0, final Throwable arg1) {
        InterfaceLogger.log.warn(arg0);
        InterfaceLogger.log.warn(arg1);
    }
    
    @Override
    public void trace(final Object arg0) {
        InterfaceLogger.log.trace(arg0);
    }
    
    @Override
    public void trace(final Object arg0, final Throwable arg1) {
        InterfaceLogger.log.trace(arg0);
        InterfaceLogger.log.trace(arg1);
    }
    
    @Override
    public void error(final Object arg0) {
        InterfaceLogger.log.error(arg0);
    }
    
    @Override
    public void error(final Object arg0, final Throwable arg1) {
        InterfaceLogger.log.error(arg0);
        InterfaceLogger.log.error(arg1);
    }
    
    @Override
    public void fatal(final Object arg0) {
        InterfaceLogger.log.fatal(arg0);
    }
    
    @Override
    public void fatal(final Object arg0, final Throwable arg1) {
        InterfaceLogger.log.fatal(arg0);
        InterfaceLogger.log.fatal(arg1);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return InterfaceLogger.log.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return InterfaceLogger.log.isTraceEnabled();
    }
}
