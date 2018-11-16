package com.reign.util.log;

import org.apache.commons.lang.*;
import org.apache.commons.logging.*;

public class CommonLog implements Logger
{
    private static int LINES;
    private Log log;
    private static final String EXCEPTION_STR = "com.reign";
    private static final ErrorLogger errorLog;
    
    static {
        CommonLog.LINES = 100;
        errorLog = new ErrorLogger();
    }
    
    public static void changeLines(final String value) {
        if (StringUtils.isNotBlank(value) && StringUtils.isNumeric(value)) {
            try {
                CommonLog.LINES = Integer.parseInt(value);
            }
            catch (Exception e) {
                CommonLog.LINES = 1;
            }
        }
    }
    
    private CommonLog(final Log log) {
        this.log = null;
        this.log = log;
    }
    
    public static <E> Logger getLog(final Class<E> clazz) {
        return new CommonLog(LogFactory.getLog(clazz));
    }
    
    @Override
    public void debug(final Object arg0) {
        this.log.debug(arg0);
    }
    
    @Override
    public void debug(final Object arg0, final Throwable arg1) {
        this.log.debug(arg0);
        this.log.debug(this.getThrowableTrace(arg1, CommonLog.LINES));
    }
    
    @Override
    public void info(final Object arg0) {
        this.log.info(arg0);
    }
    
    @Override
    public void info(final Object arg0, final Throwable arg1) {
        this.log.info(arg0);
        this.log.info(this.getThrowableTrace(arg1, CommonLog.LINES));
    }
    
    @Override
    public void warn(final Object arg0) {
        CommonLog.errorLog.warn(arg0);
    }
    
    @Override
    public void warn(final Object arg0, final Throwable arg1) {
        CommonLog.errorLog.warn(arg0);
        CommonLog.errorLog.warn(this.getThrowableTrace(arg1, CommonLog.LINES));
    }
    
    @Override
    public void trace(final Object arg0) {
        this.log.trace(arg0);
    }
    
    @Override
    public void trace(final Object arg0, final Throwable arg1) {
        this.log.trace(arg0);
        this.log.trace(this.getThrowableTrace(arg1, CommonLog.LINES));
    }
    
    @Override
    public void error(final Object arg0) {
        CommonLog.errorLog.error(arg0);
    }
    
    @Override
    public void error(final Object arg0, final Throwable arg1) {
        CommonLog.errorLog.error(arg0);
        CommonLog.errorLog.error(this.getThrowableTrace(arg1, CommonLog.LINES));
    }
    
    @Override
    public void fatal(final Object arg0) {
        CommonLog.errorLog.fatal(arg0);
    }
    
    @Override
    public void fatal(final Object arg0, final Throwable arg1) {
        CommonLog.errorLog.fatal(arg0);
        CommonLog.errorLog.fatal(this.getThrowableTrace(arg1, CommonLog.LINES));
    }
    
    private String getThrowableTrace(final Throwable arg1, final int num) {
        final StackTraceElement[] stacks = arg1.getStackTrace();
        final StringBuilder builder = new StringBuilder(256);
        builder.append(arg1.toString());
        int index = 1;
        boolean count = false;
        StackTraceElement[] array;
        for (int length = (array = stacks).length, i = 0; i < length; ++i) {
            final StackTraceElement element = array[i];
            final String value = element.toString();
            builder.append("\n").append("\t").append(value);
            if (!count && value.indexOf("com.reign") != -1) {
                count = true;
            }
            if (count && ++index > num) {
                break;
            }
        }
        return builder.toString();
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
