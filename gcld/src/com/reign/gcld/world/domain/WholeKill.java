package com.reign.gcld.world.domain;

import com.reign.framework.mybatis.*;

public class WholeKill implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer killNum;
    private Integer receivedReward;
    private Integer lastNum;
    private Integer lastRank;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getKillNum() {
        return this.killNum;
    }
    
    public void setKillNum(final Integer killNum) {
        this.killNum = killNum;
    }
    
    public Integer getReceivedReward() {
        return this.receivedReward;
    }
    
    public void setReceivedReward(final Integer receivedReward) {
        this.receivedReward = receivedReward;
    }
    
    public Integer getLastNum() {
        return this.lastNum;
    }
    
    public void setLastNum(final Integer lastNum) {
        this.lastNum = lastNum;
    }
    
    public Integer getLastRank() {
        return this.lastRank;
    }
    
    public void setLastRank(final Integer lastRank) {
        this.lastRank = lastRank;
    }
}
