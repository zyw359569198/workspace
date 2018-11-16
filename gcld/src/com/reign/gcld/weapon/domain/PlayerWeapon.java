package com.reign.gcld.weapon.domain;

import com.reign.framework.mybatis.*;

public class PlayerWeapon implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer weaponId;
    private Integer lv;
    private Integer type;
    private String gemId;
    private Integer times;
    
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
    
    public Integer getWeaponId() {
        return this.weaponId;
    }
    
    public void setWeaponId(final Integer weaponId) {
        this.weaponId = weaponId;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public String getGemId() {
        return this.gemId;
    }
    
    public void setGemId(final String gemId) {
        this.gemId = gemId;
    }
    
    public Integer getTimes() {
        return this.times;
    }
    
    public void setTimes(final Integer times) {
        this.times = times;
    }
}
