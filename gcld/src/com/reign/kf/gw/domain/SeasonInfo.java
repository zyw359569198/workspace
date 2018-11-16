package com.reign.kf.gw.domain;

import com.reign.framework.jdbc.orm.*;
import com.reign.framework.jdbc.orm.annotation.*;
import java.util.*;

@JdbcEntity
public class SeasonInfo implements JdbcModel
{
    private static final long serialVersionUID = 1L;
    @Id
    @AutoGenerator
    private int id;
    private int season;
    private Date signStartTime;
    private Date signEndTime;
    private Date matchTime;
    private String host;
    private int port;
    private String matchRule;
    private String tag;
    private String matchServer;
    private int maxTurn;
    private int rewardType;
    private int state;
    
    public int getId() {
        return this.id;
    }
    
    public void setId(final int id) {
        this.id = id;
    }
    
    public int getSeason() {
        return this.season;
    }
    
    public void setSeason(final int season) {
        this.season = season;
    }
    
    public Date getSignStartTime() {
        return this.signStartTime;
    }
    
    public void setSignStartTime(final Date signStartTime) {
        this.signStartTime = signStartTime;
    }
    
    public Date getSignEndTime() {
        return this.signEndTime;
    }
    
    public void setSignEndTime(final Date signEndTime) {
        this.signEndTime = signEndTime;
    }
    
    public Date getMatchTime() {
        return this.matchTime;
    }
    
    public void setMatchTime(final Date matchTime) {
        this.matchTime = matchTime;
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
    
    public String getMatchRule() {
        return this.matchRule;
    }
    
    public void setMatchRule(final String matchRule) {
        this.matchRule = matchRule;
    }
    
    public String getTag() {
        return this.tag;
    }
    
    public void setTag(final String tag) {
        this.tag = tag;
    }
    
    public String getMatchServer() {
        return this.matchServer;
    }
    
    public void setMatchServer(final String matchServer) {
        this.matchServer = matchServer;
    }
    
    public int getMaxTurn() {
        return this.maxTurn;
    }
    
    public void setMaxTurn(final int maxTurn) {
        this.maxTurn = maxTurn;
    }
    
    public int getRewardType() {
        return this.rewardType;
    }
    
    public void setRewardType(final int rewardType) {
        this.rewardType = rewardType;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
}
