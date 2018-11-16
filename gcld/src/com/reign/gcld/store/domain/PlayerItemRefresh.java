package com.reign.gcld.store.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerItemRefresh implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer itemId;
    private Integer type;
    private Integer locked;
    private Date unlockTime;
    private Integer price;
    private Integer isGold;
    private Integer bought;
    private Integer isCheap;
    private String refreshAttribute;
    
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
    
    public Integer getItemId() {
        return this.itemId;
    }
    
    public void setItemId(final Integer itemId) {
        this.itemId = itemId;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getLocked() {
        return this.locked;
    }
    
    public void setLocked(final Integer locked) {
        this.locked = locked;
    }
    
    public Date getUnlockTime() {
        return this.unlockTime;
    }
    
    public void setUnlockTime(final Date unlockTime) {
        this.unlockTime = unlockTime;
    }
    
    public Integer getPrice() {
        return this.price;
    }
    
    public void setPrice(final Integer price) {
        this.price = price;
    }
    
    public Integer getIsGold() {
        return this.isGold;
    }
    
    public void setIsGold(final Integer isGold) {
        this.isGold = isGold;
    }
    
    public Integer getBought() {
        return this.bought;
    }
    
    public void setBought(final Integer bought) {
        this.bought = bought;
    }
    
    public Integer getIsCheap() {
        return this.isCheap;
    }
    
    public void setIsCheap(final Integer isCheap) {
        this.isCheap = isCheap;
    }
    
    public String getRefreshAttribute() {
        return this.refreshAttribute;
    }
    
    public void setRefreshAttribute(final String refreshAttribute) {
        this.refreshAttribute = refreshAttribute;
    }
}
