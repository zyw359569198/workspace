package com.reign.kf.comm.entity.kfwd.response;

import org.codehaus.jackson.annotate.*;

@JsonAutoDetect
public class KfwdRuntimeResultDto
{
    private int competitorId;
    int winNum;
    String playerName;
    private String serverName;
    private String serverId;
    int score;
    int pos;
    int self;
    String dayScore;
    int plv;
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public int getWinNum() {
        return this.winNum;
    }
    
    public void setWinNum(final int winNum) {
        this.winNum = winNum;
    }
    
    public String getPlayerName() {
        return this.playerName;
    }
    
    public void setPlayerName(final String playerName) {
        this.playerName = playerName;
    }
    
    public String getServerName() {
        return this.serverName;
    }
    
    public void setServerName(final String serverName) {
        this.serverName = serverName;
    }
    
    public String getServerId() {
        return this.serverId;
    }
    
    public void setServerId(final String serverId) {
        this.serverId = serverId;
    }
    
    public int getScore() {
        return this.score;
    }
    
    public void setScore(final int score) {
        this.score = score;
    }
    
    public int getPos() {
        return this.pos;
    }
    
    public void setPos(final int pos) {
        this.pos = pos;
    }
    
    public String getDayScore() {
        return this.dayScore;
    }
    
    public void setDayScore(final String dayScore) {
        this.dayScore = dayScore;
    }
    
    public int getPlv() {
        return this.plv;
    }
    
    public void setPlv(final int plv) {
        this.plv = plv;
    }
    
    @JsonIgnore
    public int getSelf() {
        return this.self;
    }
    
    public void setSelf(final int self) {
        this.self = self;
    }
}
