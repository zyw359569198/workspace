package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerPower implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer powerId;
    private Integer attackable;
    private Integer complete;
    private Integer reward;
    private Date expireTime;
    private Integer state;
    private Integer buyCount;
    
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
    
    public Integer getPowerId() {
        return this.powerId;
    }
    
    public void setPowerId(final Integer powerId) {
        this.powerId = powerId;
    }
    
    public Integer getAttackable() {
        return this.attackable;
    }
    
    public void setAttackable(final Integer attackable) {
        this.attackable = attackable;
    }
    
    public Integer getComplete() {
        return this.complete;
    }
    
    public void setComplete(final Integer complete) {
        this.complete = complete;
    }
    
    public Integer getReward() {
        return this.reward;
    }
    
    public void setReward(final Integer reward) {
        this.reward = reward;
    }
    
    public Date getExpireTime() {
        return this.expireTime;
    }
    
    public void setExpireTime(final Date expireTime) {
        this.expireTime = expireTime;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Integer getBuyCount() {
        return this.buyCount;
    }
    
    public void setBuyCount(final Integer buyCount) {
        this.buyCount = buyCount;
    }
}
