package com.reign.framework.netty.tcp;

import com.reign.framework.netty.servlet.*;
import org.jboss.netty.channel.*;
import java.io.*;
import com.reign.framework.common.*;
import com.reign.framework.netty.http.handler.*;
import java.util.*;
import org.jboss.netty.handler.codec.http.*;

public class TcpResponse implements Response
{
    private Channel channel;
    private boolean close;
    
    public TcpResponse(final Channel channel) {
        this.close = false;
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
            final ChannelFuture future = this.channel.write(obj);
            if (this.close) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
        else {
            System.out.println("can't write, channel[id:" + this.channel.getId() + ", interestOps:" + this.channel.getInterestOps() + ", bound:" + this.channel.isBound() + ", connected:" + this.channel.isConnected() + ", open:" + this.channel.isOpen() + ", readable:" + this.channel.isReadable() + ", writable:" + this.channel.isWritable() + "]");
        }
        return null;
    }
    
    @Override
    public ServerProtocol getProtocol() {
        return ServerProtocol.TCP;
    }
    
    @Override
    public void addCookie(final Cookie cookie) {
        throw new UnsupportedOperationException("tcp response can't offer this operation");
    }
    
    @Override
    public void addHeader(final String name, final String value) {
        throw new UnsupportedOperationException("tcp response can't offer this operation");
    }
    
    @Override
    public void writeChunk(final byte[] bytes) {
        throw new UnsupportedOperationException("tcp response can't offer this operation");
    }
    
    @Override
    public void onWriteChunk(final ChunkAction<byte[]> handler) {
        throw new UnsupportedOperationException("tcp response can't offer this operation");
    }
    
    @Override
    public Map<String, Cookie> getCookies() {
        throw new UnsupportedOperationException("tcp response can't offer this operation");
    }
    
    @Override
    public Map<String, String> getHeaders() {
        throw new UnsupportedOperationException("tcp response can't offer this operation");
    }
    
    @Override
    public byte[] getContent() {
        throw new UnsupportedOperationException("tcp response can't offer this operation");
    }
    
    @Override
    public Object getDirect() {
        throw new UnsupportedOperationException("tcp response can't offer this operation");
    }
    
    @Override
    public void setDirect(final Object obj) {
        throw new UnsupportedOperationException("tcp response can't offer this operation");
    }
    
    @Override
    public boolean isChunk() {
        throw new UnsupportedOperationException("tcp response can't offer this operation");
    }
    
    @Override
    public void setStatus(final HttpResponseStatus status) {
        throw new UnsupportedOperationException("tcp response can't offer this operation");
    }
    
    @Override
    public HttpResponseStatus getStatus() {
        throw new UnsupportedOperationException("tcp response can't offer this operation");
    }
    
    @Override
    public void markClose() {
        this.close = true;
    }
}
