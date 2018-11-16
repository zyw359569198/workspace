package com.reign.kf.match.log;

import org.apache.commons.logging.*;

public interface Logger extends Log
{
    @Override
	void trace(final Object p0);
    
    @Override
	void trace(final Object p0, final Throwable p1);
    
    @Override
	void debug(final Object p0);
    
    @Override
	void debug(final Object p0, final Throwable p1);
    
    @Override
	void info(final Object p0);
    
    @Override
	void info(final Object p0, final Throwable p1);
    
    @Override
	void warn(final Object p0);
    
    @Override
	void warn(final Object p0, final Throwable p1);
    
    @Override
	void error(final Object p0);
    
    @Override
	void error(final Object p0, final Throwable p1);
    
    @Override
	void fatal(final Object p0);
    
    @Override
	void fatal(final Object p0, final Throwable p1);
    
    @Override
	boolean isDebugEnabled();
    
    @Override
	boolean isTraceEnabled();
    
    @Override
	boolean isErrorEnabled();
    
    @Override
	boolean isFatalEnabled();
    
    @Override
	boolean isInfoEnabled();
    
    @Override
	boolean isWarnEnabled();
}
