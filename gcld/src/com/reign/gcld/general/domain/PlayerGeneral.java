package com.reign.gcld.general.domain;

import com.reign.framework.mybatis.*;

public class PlayerGeneral implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer generalId;
    private Integer lv;
    private Long exp;
    private Integer intel;
    private Integer politics;
    private Integer leader;
    private Integer strength;
    private Integer forces;
    private Integer type;
    
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
    
    public Integer getIntel() {
        return this.intel;
    }
    
    public void setIntel(final Integer intel) {
        this.intel = intel;
    }
    
    public Integer getPolitics() {
        return this.politics;
    }
    
    public void setPolitics(final Integer politics) {
        this.politics = politics;
    }
    
    public Integer getLeader() {
        return this.leader;
    }
    
    public void setLeader(final Integer leader) {
        this.leader = leader;
    }
    
    public Integer getStrength() {
        return this.strength;
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
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
}
