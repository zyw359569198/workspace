package com.reign.kf.gw.common;

import org.jboss.netty.channel.*;

public class MatchServerChannel
{
    public static final int TIME_OUT = 600000;
    public Channel channel;
    public String host;
    public int port;
    public String tag;
    public long lastAccessTime;
    public int matchNum;
    public int priority;
    public String key;
    
    public MatchServerChannel(final Channel channel, final String host, final int port, final String tag) {
        this.channel = channel;
        this.host = host;
        this.port = port;
        this.tag = tag;
        this.key = getKey(tag, host, port);
    }
    
    public boolean isValid() {
        final long cd = System.currentTimeMillis() - this.lastAccessTime;
        return this.channel.isConnected() && this.channel.isOpen() && cd < 600000L;
    }
    
    public void access() {
        this.lastAccessTime = System.currentTimeMillis();
    }
    
    public Integer getId() {
        return this.channel.getId();
    }
    
    public void close() {
        this.channel.close();
    }
    
    public static MatchServerChannel create(final Channel channel, final String host, final int port, final String tag) {
        final MatchServerChannel matchServerChannel = new MatchServerChannel(channel, host, port, tag);
        matchServerChannel.lastAccessTime = System.currentTimeMillis();
        return matchServerChannel;
    }
    
    public static String getKey(final String tag, final String host, final int port) {
        final StringBuilder builder = new StringBuilder(100);
        builder.append(tag).append(":").append(host).append(":").append(port);
        return builder.toString();
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.tag) + ":" + this.host + ":" + this.port;
    }
    
    public int getMatchNum() {
        return this.matchNum;
    }
    
    public void setMatchNum(final int matchNum) {
        this.matchNum = matchNum;
    }
}
