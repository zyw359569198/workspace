package com.reign.gcld.log;

import com.reign.gcld.common.log.*;
import org.apache.commons.logging.*;

public class AsynchronousDBOperationLogger implements Logger
{
    private static final Log log;
    
    static {
        log = LogFactory.getLog("com.reign.gcld.asynchronousDBOperation");
    }
    
    @Override
    public void debug(final Object arg0) {
        AsynchronousDBOperationLogger.log.debug(arg0);
    }
    
    @Override
    public void debug(final Object arg0, final Throwable arg1) {
        AsynchronousDBOperationLogger.log.debug(arg0, arg1);
    }
    
    @Override
    public void info(final Object arg0) {
        AsynchronousDBOperationLogger.log.info(arg0);
    }
    
    @Override
    public void info(final Object arg0, final Throwable arg1) {
        AsynchronousDBOperationLogger.log.info(arg0, arg1);
    }
    
    @Override
    public void warn(final Object arg0) {
        AsynchronousDBOperationLogger.log.warn(arg0);
    }
    
    @Override
    public void warn(final Object arg0, final Throwable arg1) {
        AsynchronousDBOperationLogger.log.warn(arg0, arg1);
    }
    
    @Override
    public void trace(final Object arg0) {
        AsynchronousDBOperationLogger.log.trace(arg0);
    }
    
    @Override
    public void trace(final Object arg0, final Throwable arg1) {
        AsynchronousDBOperationLogger.log.trace(arg0, arg1);
    }
    
    @Override
    public void error(final Object arg0) {
        AsynchronousDBOperationLogger.log.error(arg0);
    }
    
    @Override
    public void error(final Object arg0, final Throwable arg1) {
        AsynchronousDBOperationLogger.log.error(arg0, arg1);
    }
    
    @Override
    public void fatal(final Object arg0) {
        AsynchronousDBOperationLogger.log.fatal(arg0);
    }
    
    @Override
    public void fatal(final Object arg0, final Throwable arg1) {
        AsynchronousDBOperationLogger.log.fatal(arg0, arg1);
    }
    
    @Override
    public boolean isDebugEnabled() {
        return AsynchronousDBOperationLogger.log.isDebugEnabled();
    }
    
    @Override
    public boolean isTraceEnabled() {
        return AsynchronousDBOperationLogger.log.isTraceEnabled();
    }
}
