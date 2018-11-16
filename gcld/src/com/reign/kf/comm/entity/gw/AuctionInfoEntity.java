package com.reign.kf.comm.entity.gw;

public class AuctionInfoEntity
{
    public static final int STATE_ASSIGNED = 0;
    public static final int STATE_CONNECTED = 1;
    public static final int STATE_ABORT = 2;
    private int id;
    private int startHour;
    private int endHour;
    private int baseHour;
    private int scanHour;
    private String rule;
    private String host;
    private int port;
    private int state;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getStartHour() {
        return this.startHour;
    }
    
    public void setStartHour(final int startHour) {
        this.startHour = startHour;
    }
    
    public int getEndHour() {
        return this.endHour;
    }
    
    public void setEndHour(final int endHour) {
        this.endHour = endHour;
    }
    
    public int getBaseHour() {
        return this.baseHour;
    }
    
    public void setBaseHour(final int baseHour) {
        this.baseHour = baseHour;
    }
    
    public String getRule() {
        return this.rule;
    }
    
    public void setRule(final String rule) {
        this.rule = rule;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public String getHost() {
        return this.host;
    }
    
    public void setHost(final String host) {
        this.host = host;
    }
    
    public int getPort() {
        return this.port;
    }
    
    public void setPort(final int port) {
        this.port = port;
    }
    
    public int getScanHour() {
        return this.scanHour;
    }
    
    public void setScanHour(final int scanHour) {
        this.scanHour = scanHour;
    }
}
