package com.reign.kfzb.dto.request;

public class KfzbRTSupport
{
    private int seasonId;
    private int matchId;
    private int cId;
    private int supAdd;
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
    }
    
    public int getMatchId() {
        return this.matchId;
    }
    
    public void setMatchId(final int matchId) {
        this.matchId = matchId;
    }
    
    public int getcId() {
        return this.cId;
    }
    
    public void setcId(final int cId) {
        this.cId = cId;
    }
    
    public int getSupAdd() {
        return this.supAdd;
    }
    
    public void setSupAdd(final int supAdd) {
        this.supAdd = supAdd;
    }
}
