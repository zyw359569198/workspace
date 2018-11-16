package com.reign.framework.netty.http;

import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.http.handler.*;
import org.jboss.netty.channel.*;
import java.io.*;
import com.reign.framework.common.*;
import java.util.*;
import org.jboss.netty.handler.codec.http.*;

public class HttpRespone implements Response
{
    private Channel channel;
    private HttpResponse httpResponse;
    private Map<String, String> headers;
    private Map<String, Cookie> cookies;
    private Object direct;
    private ByteArrayOutputStream outPutStream;
    private boolean chunk;
    private ChunkAction<byte[]> chunkHandler;
    
    public HttpRespone(final Channel channel) {
        this.chunk = false;
        this.channel = channel;
    }
    
    @Override
    public Channel getChannel() {
        return this.channel;
    }
    
    @Override
    public boolean isWritable() {
        return this.channel.isWritable();
    }
    
    @Override
    public ChannelFuture write(final Object obj) throws IOException {
        if (this.channel.isWritable()) {
            this.getOutPutStream().write((byte[])obj);
        }
        return null;
    }
    
    @Override
    public ServerProtocol getProtocol() {
        return ServerProtocol.HTTP;
    }
    
    @Override
    public void addCookie(final Cookie cookie) {
        this.getInternalCookies().put(cookie.getName(), cookie);
    }
    
    @Override
    public void addHeader(final String name, final String value) {
        this.getHeads().put(name, value);
    }
    
    @Override
    public void writeChunk(final byte[] bytes) {
        if (this.channel.isWritable()) {
            if (this.chunkHandler == null) {
                throw new UnsupportedOperationException("Your HTTP server doesn't yet support chunked response stream");
            }
            this.chunk = true;
            this.chunkHandler.invoke(bytes);
        }
    }
    
    @Override
    public void onWriteChunk(final ChunkAction<byte[]> handler) {
        this.chunkHandler = handler;
    }
    
    @Override
    public Map<String, Cookie> getCookies() {
        return this.cookies;
    }
    
    @Override
    public Map<String, String> getHeaders() {
        return this.headers;
    }
    
    @Override
    public byte[] getContent() {
        return this.getOutPutStream().toByteArray();
    }
    
    @Override
    public Object getDirect() {
        return this.direct;
    }
    
    @Override
    public void setDirect(final Object obj) {
        this.direct = obj;
    }
    
    @Override
    public boolean isChunk() {
        return this.chunk;
    }
    
    private synchronized Map<String, Cookie> getInternalCookies() {
        if (this.cookies == null) {
            this.cookies = new HashMap<String, Cookie>(16);
        }
        return this.cookies;
    }
    
    private synchronized Map<String, String> getHeads() {
        if (this.headers == null) {
            this.headers = new HashMap<String, String>(16);
        }
        return this.headers;
    }
    
    public synchronized HttpResponse getHttpResponse() {
        if (this.httpResponse == null) {
            this.httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        }
        return this.httpResponse;
    }
    
    public synchronized ByteArrayOutputStream getOutPutStream() {
        if (this.outPutStream == null) {
            this.outPutStream = new ByteArrayOutputStream();
        }
        return this.outPutStream;
    }
    
    @Override
    public synchronized void setStatus(final HttpResponseStatus status) {
        if (this.httpResponse == null) {
            this.httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
            return;
        }
        this.httpResponse.setStatus(status);
    }
    
    @Override
    public synchronized HttpResponseStatus getStatus() {
        if (this.httpResponse == null) {
            return HttpResponseStatus.OK;
        }
        return this.httpResponse.getStatus();
    }
    
    @Override
    public void markClose() {
    }
}
