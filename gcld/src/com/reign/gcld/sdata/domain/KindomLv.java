package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KindomLv implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer lv;
    private Integer expUpgrade;
    private Integer expPerTask;
    private String reward;
    private Integer rewardChiefExp;
    private Integer rewardIron;
    private Integer rewardMbl;
    private Integer barbarainLv;
    private Integer efLv;
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public Integer getExpUpgrade() {
        return this.expUpgrade;
    }
    
    public void setExpUpgrade(final Integer expUpgrade) {
        this.expUpgrade = expUpgrade;
    }
    
    public Integer getExpPerTask() {
        return this.expPerTask;
    }
    
    public void setExpPerTask(final Integer expPerTask) {
        this.expPerTask = expPerTask;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public Integer getRewardChiefExp() {
        return this.rewardChiefExp;
    }
    
    public void setRewardChiefExp(final Integer rewardChiefExp) {
        this.rewardChiefExp = rewardChiefExp;
    }
    
    public Integer getRewardIron() {
        return this.rewardIron;
    }
    
    public void setRewardIron(final Integer rewardIron) {
        this.rewardIron = rewardIron;
    }
    
    public Integer getRewardMbl() {
        return this.rewardMbl;
    }
    
    public void setRewardMbl(final Integer rewardMbl) {
        this.rewardMbl = rewardMbl;
    }
    
    public Integer getBarbarainLv() {
        return this.barbarainLv;
    }
    
    public void setBarbarainLv(final Integer barbarainLv) {
        this.barbarainLv = barbarainLv;
    }
    
    public Integer getEfLv() {
        return this.efLv;
    }
    
    public void setEfLv(final Integer efLv) {
        this.efLv = efLv;
    }
}
