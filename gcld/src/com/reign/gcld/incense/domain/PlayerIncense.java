package com.reign.gcld.incense.domain;

import com.reign.framework.mybatis.*;

public class PlayerIncense implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer incenseNum;
    private Integer openBit;
    private Integer copperTimes;
    private Integer woodTimes;
    private Integer foodTimes;
    private Integer ironTimes;
    private Integer gemTimes;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getIncenseNum() {
        return this.incenseNum;
    }
    
    public void setIncenseNum(final Integer incenseNum) {
        this.incenseNum = incenseNum;
    }
    
    public Integer getOpenBit() {
        return this.openBit;
    }
    
    public void setOpenBit(final Integer openBit) {
        this.openBit = openBit;
    }
    
    public Integer getCopperTimes() {
        return this.copperTimes;
    }
    
    public void setCopperTimes(final Integer copperTimes) {
        this.copperTimes = copperTimes;
    }
    
    public Integer getWoodTimes() {
        return this.woodTimes;
    }
    
    public void setWoodTimes(final Integer woodTimes) {
        this.woodTimes = woodTimes;
    }
    
    public Integer getFoodTimes() {
        return this.foodTimes;
    }
    
    public void setFoodTimes(final Integer foodTimes) {
        this.foodTimes = foodTimes;
    }
    
    public Integer getIronTimes() {
        return this.ironTimes;
    }
    
    public void setIronTimes(final Integer ironTimes) {
        this.ironTimes = ironTimes;
    }
    
    public Integer getGemTimes() {
        return this.gemTimes;
    }
    
    public void setGemTimes(final Integer gemTimes) {
        this.gemTimes = gemTimes;
    }
}
