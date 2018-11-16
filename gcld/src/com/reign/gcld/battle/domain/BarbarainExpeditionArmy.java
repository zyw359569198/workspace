package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class BarbarainExpeditionArmy implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer state;
    private Date createTime;
    private Integer locationId;
    private Integer forceId;
    private Integer worldPaidBId;
    private Integer armyId;
    private Integer hp;
    private Integer tacticval;
    private String moveLine;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Date getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
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
    
    public Integer getWorldPaidBId() {
        return this.worldPaidBId;
    }
    
    public void setWorldPaidBId(final Integer worldPaidBId) {
        this.worldPaidBId = worldPaidBId;
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
    
    public Integer getTacticval() {
        return this.tacticval;
    }
    
    public void setTacticval(final Integer tacticval) {
        this.tacticval = tacticval;
    }
    
    public String getMoveLine() {
        return this.moveLine;
    }
    
    public void setMoveLine(final String moveLine) {
        this.moveLine = moveLine;
    }
}
