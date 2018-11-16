package com.reign.gcld.player.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerIncenseWeaponEffect implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer incenseId;
    private Integer incenseLimit;
    private Date incenseEndTime;
    private Integer incenseMulti;
    private Integer weaponId;
    private Integer weaponLimit;
    private Date weaponEndTime;
    private Integer weaponMulti;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getIncenseId() {
        return this.incenseId;
    }
    
    public void setIncenseId(final Integer incenseId) {
        this.incenseId = incenseId;
    }
    
    public Integer getIncenseLimit() {
        return this.incenseLimit;
    }
    
    public void setIncenseLimit(final Integer incenseLimit) {
        this.incenseLimit = incenseLimit;
    }
    
    public Date getIncenseEndTime() {
        return this.incenseEndTime;
    }
    
    public void setIncenseEndTime(final Date incenseEndTime) {
        this.incenseEndTime = incenseEndTime;
    }
    
    public Integer getIncenseMulti() {
        return this.incenseMulti;
    }
    
    public void setIncenseMulti(final Integer incenseMulti) {
        this.incenseMulti = incenseMulti;
    }
    
    public Integer getWeaponId() {
        return this.weaponId;
    }
    
    public void setWeaponId(final Integer weaponId) {
        this.weaponId = weaponId;
    }
    
    public Integer getWeaponLimit() {
        return this.weaponLimit;
    }
    
    public void setWeaponLimit(final Integer weaponLimit) {
        this.weaponLimit = weaponLimit;
    }
    
    public Date getWeaponEndTime() {
        return this.weaponEndTime;
    }
    
    public void setWeaponEndTime(final Date weaponEndTime) {
        this.weaponEndTime = weaponEndTime;
    }
    
    public Integer getWeaponMulti() {
        return this.weaponMulti;
    }
    
    public void setWeaponMulti(final Integer weaponMulti) {
        this.weaponMulti = weaponMulti;
    }
}
