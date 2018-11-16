package com.reign.kf.comm.entity.kfwd.request;

public class KfwdDoubleRewardKey
{
    int competitorId;
    int playerId;
    int doubleCoef;
    int round;
    int gold;
    
    public int getGold() {
        return this.gold;
    }
    
    public void setGold(final int gold) {
        this.gold = gold;
    }
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public int getDoubleCoef() {
        return this.doubleCoef;
    }
    
    public void setDoubleCoef(final int doubleCoef) {
        this.doubleCoef = doubleCoef;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public int getRound() {
        return this.round;
    }
    
    public void setRound(final int round) {
        this.round = round;
    }
}
