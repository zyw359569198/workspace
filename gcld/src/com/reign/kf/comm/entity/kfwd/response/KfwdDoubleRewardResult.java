package com.reign.kf.comm.entity.kfwd.response;

import com.reign.kf.comm.entity.kfwd.request.*;

public class KfwdDoubleRewardResult
{
    private int state;
    String reason;
    int competitorId;
    int playerId;
    int doubleCoef;
    int round;
    int cost;
    
    public KfwdDoubleRewardResult() {
        this.reason = "";
    }
    
    public KfwdDoubleRewardResult(final int state, final KfwdDoubleRewardKey key, final int cost) {
        this.reason = "";
        this.state = state;
        this.competitorId = key.getCompetitorId();
        this.playerId = key.getPlayerId();
        this.round = key.getRound();
        this.doubleCoef = key.getDoubleCoef();
        this.cost = cost;
    }
    
    public KfwdDoubleRewardResult(final int state, final KfwdDoubleRewardKey key, final String reason) {
        this.reason = "";
        this.state = state;
        this.competitorId = key.getCompetitorId();
        this.playerId = key.getPlayerId();
        this.round = key.getRound();
        this.doubleCoef = key.getDoubleCoef();
        this.reason = reason;
    }
    
    public int getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final int competitorId) {
        this.competitorId = competitorId;
    }
    
    public int getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final int playerId) {
        this.playerId = playerId;
    }
    
    public int getDoubleCoef() {
        return this.doubleCoef;
    }
    
    public void setDoubleCoef(final int doubleCoef) {
        this.doubleCoef = doubleCoef;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public int getCost() {
        return this.cost;
    }
    
    public void setCost(final int cost) {
        this.cost = cost;
    }
    
    public String getReason() {
        return this.reason;
    }
    
    public void setReason(final String reason) {
        this.reason = reason;
    }
    
    public int getRound() {
        return this.round;
    }
    
    public void setRound(final int round) {
        this.round = round;
    }
}
