package com.reign.gcld.world.domain;

import com.reign.framework.mybatis.*;

public class CountryRewardPerHour implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer hour;
    private String rewards;
    private Integer forceId;
    
    public Integer getHour() {
        return this.hour;
    }
    
    public void setHour(final Integer hour) {
        this.hour = hour;
    }
    
    public String getRewards() {
        return this.rewards;
    }
    
    public void setRewards(final String rewards) {
        this.rewards = rewards;
    }
    
    public Integer getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final Integer forceId) {
        this.forceId = forceId;
    }
}
