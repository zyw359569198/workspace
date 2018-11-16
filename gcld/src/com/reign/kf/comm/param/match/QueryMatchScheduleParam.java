package com.reign.kf.comm.param.match;

public class QueryMatchScheduleParam
{
    public String matchTag;
    public int turn;
    public boolean all;
    
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
    
    public boolean isAll() {
        return this.all;
    }
    
    public void setAll(final boolean all) {
        this.all = all;
    }
}
