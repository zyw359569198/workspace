package com.reign.gcld.general.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerGeneralMilitary implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer forceId;
    private Integer generalId;
    private Integer lv;
    private Long exp;
    private Integer leader;
    private Integer strength;
    private Integer forces;
    private Date updateForcesTime;
    private Integer state;
    private Integer locationId;
    private Integer morale;
    private Integer auto;
    private Integer tacticEffect;
    private Integer jubenLoId;
    
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
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public Integer getGeneralId() {
        return this.generalId;
    }
    
    public void setGeneralId(final Integer generalId) {
        this.generalId = generalId;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public Long getExp() {
        return this.exp;
    }
    
    public void setExp(final Long exp) {
        this.exp = exp;
    }
    
    public Integer getLeader() {
        return this.leader;
    }
    
    public Integer getLeader(final Integer addInteger) {
        return this.leader + ((addInteger == null) ? 0 : addInteger);
    }
    
    public void setLeader(final Integer leader) {
        this.leader = leader;
    }
    
    public Integer getStrength() {
        return this.strength;
    }
    
    public Integer getStrength(final Integer addInteger) {
        return this.strength + ((addInteger == null) ? 0 : addInteger);
    }
    
    public void setStrength(final Integer strength) {
        this.strength = strength;
    }
    
    public Integer getForces() {
        return this.forces;
    }
    
    public void setForces(final Integer forces) {
        this.forces = forces;
    }
    
    public Date getUpdateForcesTime() {
        return this.updateForcesTime;
    }
    
    public void setUpdateForcesTime(final Date updateForcesTime) {
        this.updateForcesTime = updateForcesTime;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Integer getLocationId() {
        return this.locationId;
    }
    
    public void setLocationId(final Integer locationId) {
        this.locationId = locationId;
    }
    
    public Integer getMorale() {
        return this.morale;
    }
    
    public void setMorale(final Integer morale) {
        this.morale = morale;
    }
    
    public Integer getAuto() {
        return this.auto;
    }
    
    public void setAuto(final Integer auto) {
        this.auto = auto;
    }
    
    public Integer getTacticEffect() {
        return this.tacticEffect;
    }
    
    public void setTacticEffect(final Integer tacticEffect) {
        this.tacticEffect = tacticEffect;
    }
    
    public Integer getJubenLoId() {
        return this.jubenLoId;
    }
    
    public void setJubenLoId(final Integer jubenLoId) {
        this.jubenLoId = jubenLoId;
    }
}
