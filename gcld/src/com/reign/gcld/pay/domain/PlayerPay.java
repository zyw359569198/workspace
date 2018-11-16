package com.reign.gcld.pay.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerPay implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private String orderId;
    private Integer gold;
    private String userId;
    private Integer playerId;
    private String yx;
    private Date createTime;
    private Integer type;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public String getOrderId() {
        return this.orderId;
    }
    
    public void setOrderId(final String orderId) {
        this.orderId = orderId;
    }
    
    public Integer getGold() {
        return this.gold;
    }
    
    public void setGold(final Integer gold) {
        this.gold = gold;
    }
    
    public String getUserId() {
        return this.userId;
    }
    
    public void setUserId(final String userId) {
        this.userId = userId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public String getYx() {
        return this.yx;
    }
    
    public void setYx(final String yx) {
        this.yx = yx;
    }
    
    public Date getCreateTime() {
        return this.createTime;
    }
    
    public void setCreateTime(final Date createTime) {
        this.createTime = createTime;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
}
