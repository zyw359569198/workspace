package com.reign.kf.comm.entity.gw;

import java.util.*;

public class SeasonInfoEntity
{
    public static final int SEASON_STATE_ASSIGNED = 0;
    public static final int SEASON_STATE_CONNECTED = 1;
    public static final int SEASON_STATE_READY = 2;
    public static final int SEASON_STATE_CANCEL = 3;
    public static final int SEASON_STATE_FINISH = 4;
    public static final int SEASON_STATE_CANCEL_CONFIRM = 5;
    private Integer id;
    private Integer season;
    private Date signStartTime;
    private Date signEndTime;
    private Date matchTime;
    private String host;
    private int port;
    private String matchRule;
    private Integer state;
    private String tag;
    private String matchServer;
    private Integer rewardType;
    private Integer maxTurn;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getSeason() {
        return this.season;
    }
    
    public void setSeason(final Integer season) {
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
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
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
    
    public Integer getMaxTurn() {
        return this.maxTurn;
    }
    
    public void setMaxTurn(final Integer maxTurn) {
        this.maxTurn = maxTurn;
    }
    
    public Integer getRewardType() {
        return this.rewardType;
    }
    
    public void setRewardType(final Integer rewardType) {
        this.rewardType = rewardType;
    }
}
