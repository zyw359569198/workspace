package com.reign.framework.netty.http;

import org.jboss.netty.channel.*;
import com.reign.framework.netty.servlet.*;
import org.jboss.netty.handler.codec.http.*;
import java.util.*;
import com.reign.framework.common.*;
import java.net.*;
import org.jboss.netty.buffer.*;
import java.io.*;
import org.apache.commons.lang.*;
import com.reign.framework.netty.util.*;

public class HttpRequest implements Request
{
    private Map<String, String[]> paramMap;
    private String command;
    private Channel channel;
    private ServletContext sc;
    private String sessionId;
    private int requestId;
    private ChannelHandlerContext ctx;
    private Map<String, Cookie> cookies;
    private Map<String, String> headers;
    private Response response;
    private boolean longHttp;
    private byte[] getContent;
    private byte[] postContent;
    private volatile boolean parseGet;
    private volatile boolean parsePost;
    private String url;
    
    public HttpRequest(final ChannelHandlerContext ctx, final ServletContext sc, final Channel channel, final byte[] getContent, final byte[] postContent, final String command, final Map<String, Cookie> cookies, final Map<String, String> headers, final Response response) {
        this.paramMap = new HashMap<String, String[]>();
        this.ctx = ctx;
        this.sc = sc;
        this.channel = channel;
        this.response = response;
        this.cookies = cookies;
        this.headers = headers;
        this.getContent = getContent;
        this.postContent = postContent;
        if ("gateway".equals(command)) {
            final String[] value = this.getParamterValues("command");
            this.command = ((value == null) ? null : value[0]);
        }
        else {
            this.command = command;
        }
        this.sessionId = this.getCookieValue("REIGNID");
        SessionManager.getInstance().access(this.sessionId);
        if ("longhttp".equalsIgnoreCase(this.command)) {
            final Session session = this.getSession(false);
            if (session != null) {
                session.setResponse(response);
            }
            this.longHttp = true;
        }
    }
    
    @Override
    public Map<String, String[]> getParamterMap() {
        this.parseParam();
        return this.paramMap;
    }
    
    @Override
    public String[] getParamterValues(final String key) {
        this.parseParam();
        return this.paramMap.get(key);
    }
    
    @Override
    public Session getSession() {
        return this.getSession(true);
    }
    
    @Override
    public Session getNewSession() {
        final Session session = SessionManager.getInstance().getSession(null, true);
        this.sessionId = session.getId();
        this.response.addCookie(new DefaultCookie("REIGNID", session.getId()));
        session.access();
        return session;
    }
    
    @Override
    public Session getSession(final boolean allowCreate) {
        final Session session = SessionManager.getInstance().getSession(this.sessionId, allowCreate);
        if (allowCreate && session != null && !session.getId().equals(this.sessionId)) {
            this.sessionId = session.getId();
            this.response.addCookie(new DefaultCookie("REIGNID", session.getId()));
        }
        if (session != null) {
            session.access();
        }
        return session;
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
    public String getHeader(final String key) {
        return this.headers.get(key);
    }
    
    @Override
    public Collection<Cookie> getCookies() {
        return this.cookies.values();
    }
    
    @Override
    public String getCookieValue(final String key) {
        if (this.cookies != null) {
            final Cookie cookie = this.cookies.get(key);
            if (cookie != null) {
                return cookie.getValue();
            }
        }
        return null;
    }
    
    @Override
    public ServerProtocol getProtocol() {
        return ServerProtocol.HTTP;
    }
    
    @Override
    public boolean isHttpLong() {
        return this.longHttp;
    }
    
    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress)this.channel.getRemoteAddress();
    }
    
    @Override
    public byte[] getContent() {
        byte[] content = this.postContent;
        if (content == null) {
            content = this.getContent;
        }
        return content;
    }
    
    @Override
    public void pushAndClose(final ChannelBuffer buffer) {
        throw new UnsupportedOperationException("http request can't offer this operation");
    }
    
    private void parseParam() {
        this.parseGetParam(this.getContent);
        this.parsePostParam(this.postContent);
    }
    
    private void parseGetParam(final byte[] bytes) {
        if (this.parseGet) {
            return;
        }
        if (bytes != null) {
            try {
                this.parseParam(new String(bytes));
            }
            catch (UnsupportedEncodingException ex) {}
        }
        this.parseGet = true;
    }
    
    private void parsePostParam(final byte[] bytes) {
        if (this.parsePost) {
            return;
        }
        if (bytes != null) {
            try {
                this.parseParam(new String(bytes));
            }
            catch (UnsupportedEncodingException ex) {}
        }
        this.parsePost = true;
    }
    
    private void parseParam(final String content) throws UnsupportedEncodingException {
        if (StringUtils.isBlank(content)) {
            return;
        }
        final String str = content.trim();
        final String[] strs = str.split("&");
        String[] array;
        for (int length = (array = strs).length, j = 0; j < length; ++j) {
            final String value = array[j];
            final String[] values = value.split("=");
            final String k = Utils.decode(values[0], "utf-8");
            if (values.length == 1) {
                this.paramMap.put(k, null);
            }
            else {
                String v = Utils.decode(values[1], "utf-8");
                for (int i = 2; i < values.length; ++i) {
                    v = String.valueOf(v) + "=" + Utils.decode(values[i], "utf-8");
                }
                if (this.paramMap.containsKey(k)) {
                    this.paramMap.put(k, RequestUtil.getValue(this.paramMap.get(k), v));
                }
                else {
                    this.paramMap.put(k, new String[] { v });
                }
            }
        }
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public void setUrl(final String url) {
        this.url = url;
    }
}
