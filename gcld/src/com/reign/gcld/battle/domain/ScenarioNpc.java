package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class ScenarioNpc implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer scenarioId;
    private Integer locationId;
    private Integer forceId;
    private Integer npcType;
    private Integer armyId;
    private Integer hp;
    private Integer tacticVal;
    private Integer state;
    private Date addTime;
    
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
    
    public Integer getScenarioId() {
        return this.scenarioId;
    }
    
    public void setScenarioId(final Integer scenarioId) {
        this.scenarioId = scenarioId;
    }
    
    public Integer getLocationId() {
        return this.locationId;
    }
    
    public void setLocationId(final Integer locationId) {
        this.locationId = locationId;
    }
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public Integer getNpcType() {
        return this.npcType;
    }
    
    public void setNpcType(final Integer npcType) {
        this.npcType = npcType;
    }
    
    public Integer getArmyId() {
        return this.armyId;
    }
    
    public void setArmyId(final Integer armyId) {
        this.armyId = armyId;
    }
    
    public Integer getHp() {
        return this.hp;
    }
    
    public void setHp(final Integer hp) {
        this.hp = hp;
    }
    
    public Integer getTacticVal() {
        return this.tacticVal;
    }
    
    public void setTacticVal(final Integer tacticVal) {
        this.tacticVal = tacticVal;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Date getAddTime() {
        return this.addTime;
    }
    
    public void setAddTime(final Date addTime) {
        this.addTime = addTime;
    }
}
