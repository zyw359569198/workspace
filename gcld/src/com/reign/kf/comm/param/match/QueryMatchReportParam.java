package com.reign.kf.comm.param.match;

public class QueryMatchReportParam
{
    private String matchTag;
    private int matchId;
    private int session;
    
    public String getMatchTag() {
        return this.matchTag;
    }
    
    public void setMatchTag(final String matchTag) {
        this.matchTag = matchTag;
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public int getSession() {
        return this.session;
    }
    
    public void setSession(final int session) {
        this.session = session;
    }
}
