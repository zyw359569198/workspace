package com.reign.kf.comm.entity;

public class MatchServerEntity
{
    private String matchSvrName;
    private int state;
    private String matchUrl;
    private int seasonId;
    
    public String getMatchSvrName() {
        return this.matchSvrName;
    }
    
    public void setMatchSvrName(final String matchSvrName) {
        this.matchSvrName = matchSvrName;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public String getMatchUrl() {
        return this.matchUrl;
    }
    
    public void setMatchUrl(final String matchUrl) {
        this.matchUrl = matchUrl;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
}
