package com.reign.framework.netty.servlet;

import org.jboss.netty.channel.*;
import java.io.*;
import com.reign.framework.common.*;
import java.util.*;
import com.reign.framework.netty.http.handler.*;
import org.jboss.netty.handler.codec.http.*;

public interface Response
{
    Channel getChannel();
    
    boolean isWritable();
    
    ChannelFuture write(final Object p0) throws IOException;
    
    ServerProtocol getProtocol();
    
    void addCookie(final Cookie p0);
    
    Map<String, Cookie> getCookies();
    
    Map<String, String> getHeaders();
    
    void addHeader(final String p0, final String p1);
    
    boolean isChunk();
    
    void writeChunk(final byte[] p0);
    
    void onWriteChunk(final ChunkAction<byte[]> p0);
    
    Object getDirect();
    
    void setDirect(final Object p0);
    
    byte[] getContent();
    
    void setStatus(final HttpResponseStatus p0);
    
    HttpResponseStatus getStatus();
    
    void markClose();
}
