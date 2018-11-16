package com.reign.gcld.treasure.domain;

import com.reign.framework.mybatis.*;

public class PlayerTreasure implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer treasureId;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getTreasureId() {
        return this.treasureId;
    }
    
    public void setTreasureId(final Integer treasureId) {
        this.treasureId = treasureId;
    }
}
