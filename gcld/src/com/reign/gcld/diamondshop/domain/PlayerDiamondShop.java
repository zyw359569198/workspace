package com.reign.gcld.diamondshop.domain;

import com.reign.framework.mybatis.*;

public class PlayerDiamondShop implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer shopId;
    private Integer lv;
    private Integer rTimes;
    
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
    
    public Integer getShopId() {
        return this.shopId;
    }
    
    public void setShopId(final Integer shopId) {
        this.shopId = shopId;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public Integer getRTimes() {
        return this.rTimes;
    }
    
    public void setRTimes(final Integer rTimes) {
        this.rTimes = rTimes;
    }
}
