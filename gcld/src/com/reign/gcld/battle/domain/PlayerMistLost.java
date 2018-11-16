package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;

public class PlayerMistLost implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer areaId;
    private String npcLost;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getAreaId() {
        return this.areaId;
    }
    
    public void setAreaId(final Integer areaId) {
        this.areaId = areaId;
    }
    
    public String getNpcLost() {
        return this.npcLost;
    }
    
    public void setNpcLost(final String npcLost) {
        this.npcLost = npcLost;
    }
}
