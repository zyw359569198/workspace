package com.reign.gcld.grouparmy.domain;

import com.reign.framework.mybatis.*;

public class PlayerGroupArmy implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer generalId;
    private Integer armyId;
    private Integer isLeader;
    
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
    
    public Integer getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final Integer generalId) {
        this.generalId = generalId;
    }
    
    public Integer getArmyId() {
        return this.armyId;
    }
    
    public void setArmyId(final Integer armyId) {
        this.armyId = armyId;
    }
    
    public Integer getIsLeader() {
        return this.isLeader;
    }
    
    public void setIsLeader(final Integer isLeader) {
        this.isLeader = isLeader;
    }
}
