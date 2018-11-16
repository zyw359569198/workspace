package com.reign.framework.netty.servlet;

import com.reign.framework.jdbc.orm.*;
import java.util.concurrent.*;
import org.jboss.netty.channel.*;
import com.reign.framework.netty.util.*;
import org.jboss.netty.buffer.*;
import java.util.*;
import com.alibaba.fastjson.*;
import com.reign.util.buffer.*;

public class StandardSession implements Session, BinaryModel
{
    private static final int MAX_MSG_LEN = 100;
    public ConcurrentMap<String, Object> map;
    public String id;
    public long createTime;
    public long lastAccessTime;
    public boolean isValid;
    public boolean expire;
    public Channel channel;
    public List<Object> msgList;
    public Response response;
    public List<SessionListener> sessionListeners;
    public List<SessionAttributeListener> sessionAttributeListeners;
    public ServletConfig servletConfig;
    private Object lock;
    private boolean discard;
    
    public StandardSession() {
        this.map = new ConcurrentHashMap<String, Object>();
        this.isValid = true;
        this.expire = false;
        this.msgList = null;
        this.response = null;
        this.lock = new Object();
        this.discard = false;
    }
    
    public StandardSession(final String id, final List<SessionListener> sessionListeners, final List<SessionAttributeListener> sessionAttributeListeners, final ServletConfig servletConfig) {
        this.map = new ConcurrentHashMap<String, Object>();
        this.isValid = true;
        this.expire = false;
        this.msgList = null;
        this.response = null;
        this.lock = new Object();
        this.discard = false;
        this.id = id;
        this.createTime = System.currentTimeMillis();
        this.lastAccessTime = System.currentTimeMillis();
        this.sessionListeners = sessionListeners;
        this.sessionAttributeListeners = sessionAttributeListeners;
        this.servletConfig = servletConfig;
        this.notifyListener(sessionListeners, Type.CREATE);
    }
    
    @Override
    public Object getAttribute(final String key) {
        return this.map.get(key);
    }
    
    @Override
    public void setAttribute(final String key, final Object value) {
        final Object obj = this.map.put(key, value);
        if (obj == null) {
            this.notifyListener(this.sessionAttributeListeners, new SessionAttributeEvent(key, value, this), Type.ADD);
        }
        else {
            this.notifyListener(this.sessionAttributeListeners, new SessionAttributeEvent(key, obj, this), Type.REPLACE);
        }
    }
    
    @Override
    public boolean removeAttribute(final String key) {
        final Object obj = this.map.remove(key);
        this.notifyListener(this.sessionAttributeListeners, new SessionAttributeEvent(key, obj, this), Type.REMOVE);
        return obj != null;
    }
    
    @Override
    public void markDiscard() {
        this.discard = true;
    }
    
    @Override
    public void invalidate() {
        if (this.discard) {
            this.discard();
            return;
        }
        try {
            this.notifyListener(this.sessionListeners, Type.DESTORY);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (this.msgList != null) {
            this.msgList.clear();
        }
        if (this.channel != null) {
            try {
                this.channel.close();
            }
            catch (Exception ex) {}
        }
        if (this.response != null && this.response.getChannel() != null) {
            try {
                this.response.getChannel().close();
            }
            catch (Exception ex2) {}
        }
        this.map.clear();
        this.response = null;
        this.channel = null;
        this.map = null;
        GroupManager.getInstance().leave(this.id);
        SessionManager.getInstance().sessions.remove(this.id);
    }
    
    @Override
    public String getId() {
        return this.id;
    }
    
    @Override
    public void access() {
        this.lastAccessTime = System.currentTimeMillis();
    }
    
    @Override
    public void setValid(final boolean isValid) {
        this.isValid = isValid;
    }
    
    @Override
    public boolean isValid() {
        if (this.expire) {
            return false;
        }
        if (!this.isValid) {
            return false;
        }
        if (this.discard) {
            return false;
        }
        if (System.currentTimeMillis() - this.lastAccessTime < this.servletConfig.getSessionTimeOutMillis()) {
            return true;
        }
        this.isValid = false;
        this.expire = true;
        return false;
    }
    
    @Override
    public void expire() {
        this.invalidate();
    }
    
    @Override
    public void setChannel(final Channel channel) {
        if (this.channel != null && this.channel.isOpen() && !this.channel.getId().equals(channel.getId())) {
            this.channel.close();
        }
        this.safeCloseResponse();
        this.channel = null;
        this.channel = channel;
    }
    
    @Override
    public Response getResponse() {
        return this.response;
    }
    
    @Override
    public Channel getChannel() {
        return this.channel;
    }
    
    @Override
    public ChannelFuture write(final Object obj) {
        final Response response = this.response;
        final Channel channel = this.channel;
        if (response != null) {
            if (response.isWritable()) {
                this.doHttpHistoryMsg(response);
                if (obj instanceof byte[]) {
                    response.writeChunk(WrapperUtil.wrapperBody((byte[])obj));
                }
                else if (obj instanceof ChannelBuffer) {
                    final ChannelBuffer buffer = (ChannelBuffer)obj;
                    response.writeChunk(buffer.array());
                }
            }
            else {
                this.addMsgToList(obj);
            }
            return null;
        }
        if (channel == null) {
            this.addMsgToList(obj);
            return null;
        }
        if (channel.isWritable()) {
            this.doTcpHistoryMsg(channel);
            return channel.write(obj);
        }
        this.addMsgToList(obj);
        return null;
    }
    
    @Override
    public void setResponse(final Response response) {
        if (this.response != null && this.response.getChannel() != null && this.response.getChannel().isOpen() && !this.response.getChannel().getId().equals(response.getChannel().getId())) {
            this.response.getChannel().close();
        }
        this.safeCloseChannel();
        this.response = null;
        this.response = response;
    }
    
    private void safeCloseChannel() {
        if (this.channel != null && this.channel.isOpen()) {
            this.channel.close();
        }
        this.channel = null;
    }
    
    private void safeCloseResponse() {
        if (this.response != null && this.response.getChannel() != null && this.response.getChannel().isOpen()) {
            this.response.getChannel().close();
        }
        this.response = null;
    }
    
    private void notifyListener(final List<SessionListener> sessionListeners, final Type type) {
        for (final SessionListener listener : sessionListeners) {
            switch (type) {
                default: {
                    continue;
                }
                case CREATE: {
                    listener.sessionCreated(new SessionEvent(this));
                    continue;
                }
                case DESTORY: {
                    listener.sessionDestroyed(new SessionEvent(this));
                    continue;
                }
            }
        }
    }
    
    private void notifyListener(final List<SessionAttributeListener> sessionAttributeListeners, final SessionEvent event, final Type type) {
        for (final SessionAttributeListener listener : sessionAttributeListeners) {
            switch (type) {
                default: {
                    continue;
                }
                case ADD: {
                    listener.attributeAdded((SessionAttributeEvent)event);
                    continue;
                }
                case REMOVE: {
                    listener.attributeRemoved((SessionAttributeEvent)event);
                    continue;
                }
                case REPLACE: {
                    listener.attributeReplaced((SessionAttributeEvent)event);
                    continue;
                }
            }
        }
    }
    
    private void addMsgToList(final Object obj) {
        synchronized (this.lock) {
            if (this.msgList == null) {
                this.msgList = new ArrayList<Object>(100);
            }
            if (this.msgList.size() < 100) {
                this.msgList.add(obj);
            }
        }
        // monitorexit(this.lock)
    }
    
    private void doHttpHistoryMsg(final Response response) {
        if (this.msgList != null && this.msgList.size() > 0) {
            synchronized (this.lock) {
                for (final Object obj : this.msgList) {
                    if (obj instanceof byte[]) {
                        response.writeChunk(WrapperUtil.wrapperBody((byte[])obj));
                    }
                    else {
                        if (!(obj instanceof ChannelBuffer)) {
                            continue;
                        }
                        final ChannelBuffer buffer = (ChannelBuffer)obj;
                        response.writeChunk(buffer.array());
                    }
                }
                this.msgList.clear();
            }
            // monitorexit(this.lock)
        }
    }
    
    private void doTcpHistoryMsg(final Channel channel) {
        if (this.msgList != null && this.msgList.size() > 0) {
            synchronized (this.lock) {
                for (final Object object : this.msgList) {
                    channel.write(object);
                }
                this.msgList.clear();
            }
            // monitorexit(this.lock)
        }
    }
    
    private void discard() {
        try {
            this.notifyListener(this.sessionListeners, Type.DESTORY);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (this.msgList != null) {
            this.msgList.clear();
        }
        if (this.response != null && this.response.getChannel() != null) {
            try {
                this.response.getChannel().close();
            }
            catch (Exception ex) {}
        }
        this.map.clear();
        this.response = null;
        this.channel = null;
        this.map = null;
        GroupManager.getInstance().leave(this.id);
        SessionManager.getInstance().sessions.remove(this.id);
    }
    
    public StandardSession(final byte[] bytes) {
        this.map = new ConcurrentHashMap<String, Object>();
        this.isValid = true;
        this.expire = false;
        this.msgList = null;
        this.response = null;
        this.lock = new Object();
        this.discard = false;
        final IChannelBuffer buffer = ChannelBuffers.wrappedBuffer(bytes);
        this.id = buffer.readString();
        this.map.putAll((Map<?, ?>)JSON.parseObject(buffer.readString(), (Class)Map.class));
    }
    
    @Override
    public byte[] toByte() {
        final IChannelBuffer buffer = ChannelBuffers.dynamicBuffer(16);
        buffer.writeString(this.id);
        buffer.writeString(JSON.toJSONString(this.map));
        return buffer.array();
    }
    
    private enum Type
    {
        CREATE("CREATE", 0), 
        DESTORY("DESTORY", 1), 
        ADD("ADD", 2), 
        REPLACE("REPLACE", 3), 
        REMOVE("REMOVE", 4);
        
        private Type(final String s, final int n) {
        }
    }
}
