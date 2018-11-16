package com.reign.kf.comm.param.match;

public class QueryMatchNumScheduleParam
{
    public String matchTag;
    public int turn;
    public int session;
    public int matchNum;
    
    public String getMatchTag() {
        return this.matchTag;
    }
    
    public void setMatchTag(final String matchTag) {
        this.matchTag = matchTag;
    }
    
    public int getTurn() {
        return this.turn;
    }
    
    public void setTurn(final int turn) {
        this.turn = turn;
    }
    
    public int getSession() {
        return this.session;
    }
    
    public void setSession(final int session) {
        this.session = session;
    }
    
    public int getMatchNum() {
        return this.matchNum;
    }
    
    public void setMatchNum(final int matchNum) {
        this.matchNum = matchNum;
    }
}
