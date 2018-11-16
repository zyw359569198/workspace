package com.reign.gcld.activity.domain;

import com.reign.framework.mybatis.*;

public class PlayerLvExp implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Long exp;
    private Integer lv;
    private Long newExp;
    private Integer newLv;
    private Integer reward;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Long getExp() {
        return this.exp;
    }
    
    public void setExp(final Long exp) {
        this.exp = exp;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public Long getNewExp() {
        return this.newExp;
    }
    
    public void setNewExp(final Long newExp) {
        this.newExp = newExp;
    }
    
    public Integer getNewLv() {
        return this.newLv;
    }
    
    public void setNewLv(final Integer newLv) {
        this.newLv = newLv;
    }
    
    public Integer getReward() {
        return this.reward;
    }
    
    public void setReward(final Integer reward) {
        this.reward = reward;
    }
}
