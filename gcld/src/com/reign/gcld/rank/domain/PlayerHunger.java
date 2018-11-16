package com.reign.gcld.rank.domain;

import com.reign.framework.mybatis.*;

public class PlayerHunger implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer hunger;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getHunger() {
        return this.hunger;
    }
    
    public void setHunger(final Integer hunger) {
        this.hunger = hunger;
    }
}
