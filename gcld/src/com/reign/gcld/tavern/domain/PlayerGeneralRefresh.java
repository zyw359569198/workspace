package com.reign.gcld.tavern.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerGeneralRefresh implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer generalId;
    private Integer position;
    private Integer locked;
    private Date unlockTime;
    private Integer price;
    private Integer isGold;
    private Integer isCheap;
    private Integer bought;
    
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
    
    public Integer getPosition() {
        return this.position;
    }
    
    public void setPosition(final Integer position) {
        this.position = position;
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
    
    public Integer getIsCheap() {
        return this.isCheap;
    }
    
    public void setIsCheap(final Integer isCheap) {
        this.isCheap = isCheap;
    }
    
    public Integer getBought() {
        return this.bought;
    }
    
    public void setBought(final Integer bought) {
        this.bought = bought;
    }
}
