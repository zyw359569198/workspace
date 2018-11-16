package com.reign.framework.netty.servlet;

import org.jboss.netty.channel.*;

public interface Session
{
    String getId();
    
    Object getAttribute(final String p0);
    
    void setAttribute(final String p0, final Object p1);
    
    boolean removeAttribute(final String p0);
    
    void invalidate();
    
    void markDiscard();
    
    void access();
    
    void setValid(final boolean p0);
    
    boolean isValid();
    
    void expire();
    
    void setChannel(final Channel p0);
    
    void setResponse(final Response p0);
    
    Channel getChannel();
    
    Response getResponse();
    
    ChannelFuture write(final Object p0);
}
