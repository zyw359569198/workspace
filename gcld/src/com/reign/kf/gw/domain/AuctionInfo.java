package com.reign.kf.gw.domain;

import com.reign.framework.jdbc.orm.*;
import com.reign.framework.jdbc.orm.annotation.*;

@JdbcEntity
public class AuctionInfo implements JdbcModel
{
    private static final long serialVersionUID = 1L;
    @Id
    @AutoGenerator
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
    
    public int getScanHour() {
        return this.scanHour;
    }
    
    public void setScanHour(final int scanHour) {
        this.scanHour = scanHour;
    }
    
    public String getRule() {
        return this.rule;
    }
    
    public void setRule(final String rule) {
        this.rule = rule;
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
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
}
