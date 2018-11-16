package com.reign.gcld.activity.domain;

import com.reign.framework.mybatis.*;

public class PlayerIron implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer iron;
    private Integer reward;
    private Integer received;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getIron() {
        return this.iron;
    }
    
    public void setIron(final Integer iron) {
        this.iron = iron;
    }
    
    public Integer getReward() {
        return this.reward;
    }
    
    public void setReward(final Integer reward) {
        this.reward = reward;
    }
    
    public Integer getReceived() {
        return this.received;
    }
    
    public void setReceived(final Integer received) {
        this.received = received;
    }
}
