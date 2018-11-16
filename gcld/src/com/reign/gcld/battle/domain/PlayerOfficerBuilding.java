package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;

public class PlayerOfficerBuilding implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer forceId;
    private Integer buildingId;
    private Integer isLeader;
    private Integer state;
    private Integer isNew;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public Integer getBuildingId() {
        return this.buildingId;
    }
    
    public void setBuildingId(final Integer buildingId) {
        this.buildingId = buildingId;
    }
    
    public Integer getIsLeader() {
        return this.isLeader;
    }
    
    public void setIsLeader(final Integer isLeader) {
        this.isLeader = isLeader;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Integer getIsNew() {
        return this.isNew;
    }
    
    public void setIsNew(final Integer isNew) {
        this.isNew = isNew;
    }
}
