package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class StoreItems implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer itemId;
    private Integer minRefurTime;
    private Integer gold;
    private Integer copper;
    private Double goldProb;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getItemId() {
        return this.itemId;
    }
    
    public void setItemId(final Integer itemId) {
        this.itemId = itemId;
    }
    
    public Integer getMinRefurTime() {
        return this.minRefurTime;
    }
    
    public void setMinRefurTime(final Integer minRefurTime) {
        this.minRefurTime = minRefurTime;
    }
    
    public Integer getGold() {
        return this.gold;
    }
    
    public void setGold(final Integer gold) {
        this.gold = gold;
    }
    
    public Integer getCopper() {
        return this.copper;
    }
    
    public void setCopper(final Integer copper) {
        this.copper = copper;
    }
    
    public Double getGoldProb() {
        return this.goldProb;
    }
    
    public void setGoldProb(final Double goldProb) {
        this.goldProb = goldProb;
    }
}
