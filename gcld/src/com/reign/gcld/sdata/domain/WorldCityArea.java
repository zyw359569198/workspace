package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WorldCityArea implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer area;
    private Integer enterRank;
    private Integer firstEntranceGold;
    private Integer maskChief;
    private String maskNpcs;
    private Integer troopConscribeSpeed;
    private Integer countryNpcMinDegree;
    private Integer[] maskArmiesId;
    
    public Integer getArea() {
        return this.area;
    }
    
    public void setArea(final Integer area) {
        this.area = area;
    }
    
    public Integer getEnterRank() {
        return this.enterRank;
    }
    
    public void setEnterRank(final Integer enterRank) {
        this.enterRank = enterRank;
    }
    
    public Integer getFirstEntranceGold() {
        return this.firstEntranceGold;
    }
    
    public void setFirstEntranceGold(final Integer firstEntranceGold) {
        this.firstEntranceGold = firstEntranceGold;
    }
    
    public Integer getMaskChief() {
        return this.maskChief;
    }
    
    public void setMaskChief(final Integer maskChief) {
        this.maskChief = maskChief;
    }
    
    public String getMaskNpcs() {
        return this.maskNpcs;
    }
    
    public void setMaskNpcs(final String maskNpcs) {
        this.maskNpcs = maskNpcs;
    }
    
    public Integer getTroopConscribeSpeed() {
        return this.troopConscribeSpeed;
    }
    
    public void setTroopConscribeSpeed(final Integer troopConscribeSpeed) {
        this.troopConscribeSpeed = troopConscribeSpeed;
    }
    
    public Integer getCountryNpcMinDegree() {
        return this.countryNpcMinDegree;
    }
    
    public void setCountryNpcMinDegree(final Integer countryNpcMinDegree) {
        this.countryNpcMinDegree = countryNpcMinDegree;
    }
    
    public Integer[] getMaskArmiesId() {
        return this.maskArmiesId;
    }
    
    public void setMaskArmiesId(final Integer[] maskArmiesId) {
        this.maskArmiesId = maskArmiesId;
    }
}
