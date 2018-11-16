package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class HourlyReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer rewardFood;
    private String name;
    private Integer rewardCopper;
    private Double prob;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getRewardFood() {
        return this.rewardFood;
    }
    
    public void setRewardFood(final Integer rewardFood) {
        this.rewardFood = rewardFood;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getRewardCopper() {
        return this.rewardCopper;
    }
    
    public void setRewardCopper(final Integer rewardCopper) {
        this.rewardCopper = rewardCopper;
    }
    
    public Double getProb() {
        return this.prob;
    }
    
    public void setProb(final Double prob) {
        this.prob = prob;
    }
}
