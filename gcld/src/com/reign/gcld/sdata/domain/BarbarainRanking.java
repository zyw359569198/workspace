package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class BarbarainRanking implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer barbarainLv;
    private Integer highLv;
    private Integer lowLv;
    private Integer rewardExp;
    private Integer rewardIron;
    private Integer lv;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getBarbarainLv() {
        return this.barbarainLv;
    }
    
    public void setBarbarainLv(final Integer barbarainLv) {
        this.barbarainLv = barbarainLv;
    }
    
    public Integer getHighLv() {
        return this.highLv;
    }
    
    public void setHighLv(final Integer highLv) {
        this.highLv = highLv;
    }
    
    public Integer getLowLv() {
        return this.lowLv;
    }
    
    public void setLowLv(final Integer lowLv) {
        this.lowLv = lowLv;
    }
    
    public Integer getRewardExp() {
        return this.rewardExp;
    }
    
    public void setRewardExp(final Integer rewardExp) {
        this.rewardExp = rewardExp;
    }
    
    public Integer getRewardIron() {
        return this.rewardIron;
    }
    
    public void setRewardIron(final Integer rewardIron) {
        this.rewardIron = rewardIron;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
}
