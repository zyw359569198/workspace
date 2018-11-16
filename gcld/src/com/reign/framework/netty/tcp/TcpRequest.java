package com.reign.framework.netty.tcp;

import com.reign.framework.netty.tcp.handler.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.common.*;
import java.net.*;
import java.util.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.buffer.*;
import org.jboss.netty.channel.*;
import java.io.*;
import com.reign.framework.netty.util.*;

public class TcpRequest implements Request
{
    private Map<String, String[]> paramMap;
    private String command;
    private Channel channel;
    private ServletContext sc;
    private String sessionId;
    private int requestId;
    private ChannelHandlerContext ctx;
    private byte[] content;
    private volatile boolean parse;
    
    public TcpRequest(final ChannelHandlerContext ctx, final ServletContext sc, final Channel channel, final RequestMessage message) {
        this.paramMap = new HashMap<String, String[]>();
        this.ctx = ctx;
        this.sc = sc;
        this.command = message.getCommand();
        this.channel = channel;
        this.requestId = message.getRequestId();
        this.content = message.getContent();
        this.sessionId = message.getSessionId();
        SessionManager.getInstance().access(this.sessionId);
    }
    
    @Override
    public Map<String, String[]> getParamterMap() {
        this.parseParam(this.content);
        return this.paramMap;
    }
    
    @Override
    public String[] getParamterValues(final String key) {
        this.parseParam(this.content);
        return this.paramMap.get(key);
    }
    
    @Override
    public Session getSession() {
        return this.getSession(true);
    }
    
    @Override
    public Session getSession(final boolean allowCreate) {
        final Session session = SessionManager.getInstance().getSession(this.sessionId, allowCreate);
        if (allowCreate && session != null && !session.getId().equals(this.sessionId)) {
            this.sessionId = session.getId();
            session.setChannel(this.channel);
        }
        if (session != null) {
            session.access();
        }
        return session;
    }
    
    @Override
    public Session getNewSession() {
        throw new UnsupportedOperationException("tcp response can't offer this operation");
    }
    
    @Override
    public ServletContext getServletContext() {
        return this.sc;
    }
    
    @Override
    public String getCommand() {
        return this.command;
    }
    
    @Override
    public Channel getChannel() {
        return this.channel;
    }
    
    @Override
    public int getRequestId() {
        return this.requestId;
    }
    
    @Override
    public Object getAttachment() {
        return this.ctx.getAttachment();
    }
    
    @Override
    public void setSessionId(final String sessionId) {
        this.ctx.setAttachment(sessionId);
        this.sessionId = sessionId;
    }
    
    @Override
    public void setAttachment(final Object obj) {
        this.ctx.setAttachment(obj);
    }
    
    @Override
    public ServerProtocol getProtocol() {
        return ServerProtocol.TCP;
    }
    
    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress)this.channel.getRemoteAddress();
    }
    
    @Override
    public String getHeader(final String key) {
        throw new UnsupportedOperationException("tcp request can't offer this operation");
    }
    
    @Override
    public Collection<Cookie> getCookies() {
        throw new UnsupportedOperationException("tcp request can't offer this operation");
    }
    
    @Override
    public String getCookieValue(final String key) {
        throw new UnsupportedOperationException("tcp request can't offer this operation");
    }
    
    @Override
    public boolean isHttpLong() {
        throw new UnsupportedOperationException("tcp request can't offer this operation");
    }
    
    @Override
    public byte[] getContent() {
        return this.content;
    }
    
    @Override
    public void pushAndClose(final ChannelBuffer buffer) {
        if (this.channel != null && this.channel.isWritable()) {
            final ChannelFuture future = this.channel.write(buffer);
            future.addListener(ChannelFutureListener.CLOSE);
        }
    }
    
    private void parseParam(final byte[] bytes) {
        if (this.parse) {
            return;
        }
        try {
            this.parseParam(new String(bytes));
        }
        catch (UnsupportedEncodingException ex) {}
        this.parse = true;
    }
    
    private void parseParam(final String content) throws UnsupportedEncodingException {
        final String str = content.trim();
        final String[] strs = str.split("&");
        String[] array;
        for (int length = (array = strs).length, i = 0; i < length; ++i) {
            final String value = array[i];
            final String[] values = value.split("=");
            final String k = Utils.decode(values[0], "utf-8");
            if (values.length == 1) {
                this.paramMap.put(k, null);
            }
            else {
                final String v = Utils.decode(values[1], "utf-8");
                if (this.paramMap.containsKey(k)) {
                    this.paramMap.put(k, RequestUtil.getValue(this.paramMap.get(k), v));
                }
                else {
                    this.paramMap.put(k, new String[] { v });
                }
            }
        }
    }
}
