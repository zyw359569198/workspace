package com.reign.gcld.world.domain;

import com.reign.framework.mybatis.*;

public class CityNpc implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer cityId;
    private Integer npcId;
    private Integer npcLv;
    private Integer armyLv;
    private Integer att;
    private Integer def;
    private Integer hp;
    private Integer maxHp;
    private Integer forceId;
    private Integer upTimes;
    private Integer copyHp;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Integer getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final Integer cityId) {
        this.cityId = cityId;
    }
    
    public Integer getNpcId() {
        return this.npcId;
    }
    
    public void setNpcId(final Integer npcId) {
        this.npcId = npcId;
    }
    
    public Integer getNpcLv() {
        return this.npcLv;
    }
    
    public void setNpcLv(final Integer npcLv) {
        this.npcLv = npcLv;
    }
    
    public Integer getArmyLv() {
        return this.armyLv;
    }
    
    public void setArmyLv(final Integer armyLv) {
        this.armyLv = armyLv;
    }
    
    public Integer getAtt() {
        return this.att;
    }
    
    public void setAtt(final Integer att) {
        this.att = att;
    }
    
    public Integer getDef() {
        return this.def;
    }
    
    public void setDef(final Integer def) {
        this.def = def;
    }
    
    public Integer getHp() {
        return this.hp;
    }
    
    public void setHp(final Integer hp) {
        this.hp = hp;
    }
    
    public Integer getMaxHp() {
        return this.maxHp;
    }
    
    public void setMaxHp(final Integer maxHp) {
        this.maxHp = maxHp;
    }
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
    
    public Integer getUpTimes() {
        return this.upTimes;
    }
    
    public void setUpTimes(final Integer upTimes) {
        this.upTimes = upTimes;
    }
    
    public Integer getCopyHp() {
        return this.copyHp;
    }
    
    public void setCopyHp(final Integer copyHp) {
        this.copyHp = copyHp;
    }
}
