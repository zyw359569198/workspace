package com.reign.kf.comm.entity.kfwd.request;

public class KfwdSIFromMatch
{
    public static int ERROR_SEASON;
    private String matchSvrName;
    private int state;
    private int currentSeason;
    private String matchResult;
    private String matchUrl;
    
    static {
        KfwdSIFromMatch.ERROR_SEASON = -1;
    }
    
    public KfwdSIFromMatch() {
        this.currentSeason = KfwdSIFromMatch.ERROR_SEASON;
        this.matchResult = "";
    }
    
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
    
    public int getCurrentSeason() {
        return this.currentSeason;
    }
    
    public void setCurrentSeason(final int currentSeason) {
        this.currentSeason = currentSeason;
    }
    
    public String getMatchUrl() {
        return this.matchUrl;
    }
    
    public void setMatchUrl(final String matchUrl) {
        this.matchUrl = matchUrl;
    }
    
    public String getMatchResult() {
        return this.matchResult;
    }
    
    public void setMatchResult(final String matchResult) {
        this.matchResult = matchResult;
    }
}
