package com.reign.framework.netty.servlet;

import org.jboss.netty.channel.*;
import com.reign.framework.common.*;
import java.util.*;
import org.jboss.netty.handler.codec.http.*;
import java.net.*;
import org.jboss.netty.buffer.*;

public interface Request
{
    Map<String, String[]> getParamterMap();
    
    String[] getParamterValues(final String p0);
    
    Session getSession();
    
    Session getSession(final boolean p0);
    
    Session getNewSession();
    
    ServletContext getServletContext();
    
    String getCommand();
    
    Channel getChannel();
    
    int getRequestId();
    
    Object getAttachment();
    
    void setSessionId(final String p0);
    
    void setAttachment(final Object p0);
    
    ServerProtocol getProtocol();
    
    String getHeader(final String p0);
    
    String getCookieValue(final String p0);
    
    Collection<Cookie> getCookies();
    
    boolean isHttpLong();
    
    InetSocketAddress getRemoteAddress();
    
    byte[] getContent();
    
    void pushAndClose(final ChannelBuffer p0);
}
