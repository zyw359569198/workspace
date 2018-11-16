package com.reign.gcld.store.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerStore implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer storeState;
    private Integer equipRefreshTime;
    private Integer toolRefreshTime;
    private Date nextEquipDate;
    private Date nextToolDate;
    private String lockEquipId;
    private String unrefreshedEquip;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getStoreState() {
        return this.storeState;
    }
    
    public void setStoreState(final Integer storeState) {
        this.storeState = storeState;
    }
    
    public Integer getEquipRefreshTime() {
        return this.equipRefreshTime;
    }
    
    public void setEquipRefreshTime(final Integer equipRefreshTime) {
        this.equipRefreshTime = equipRefreshTime;
    }
    
    public Integer getToolRefreshTime() {
        return this.toolRefreshTime;
    }
    
    public void setToolRefreshTime(final Integer toolRefreshTime) {
        this.toolRefreshTime = toolRefreshTime;
    }
    
    public Date getNextEquipDate() {
        return this.nextEquipDate;
    }
    
    public void setNextEquipDate(final Date nextEquipDate) {
        this.nextEquipDate = nextEquipDate;
    }
    
    public Date getNextToolDate() {
        return this.nextToolDate;
    }
    
    public void setNextToolDate(final Date nextToolDate) {
        this.nextToolDate = nextToolDate;
    }
    
    public String getLockEquipId() {
        return this.lockEquipId;
    }
    
    public void setLockEquipId(final String lockEquipId) {
        this.lockEquipId = lockEquipId;
    }
    
    public String getUnrefreshedEquip() {
        return this.unrefreshedEquip;
    }
    
    public void setUnrefreshedEquip(final String unrefreshedEquip) {
        this.unrefreshedEquip = unrefreshedEquip;
    }
}
