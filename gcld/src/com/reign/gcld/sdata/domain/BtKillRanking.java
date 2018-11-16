package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class BtKillRanking implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer btLv;
    private Integer highLv;
    private Integer lowLv;
    private Integer rewardExp;
    private Integer rewardIron;
    private Integer lv;
    private Integer rewardExp2;
    private Integer rewardIron2;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getBtLv() {
        return this.btLv;
    }
    
    public void setBtLv(final Integer btLv) {
        this.btLv = btLv;
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
    
    public Integer getRewardExp2() {
        return this.rewardExp2;
    }
    
    public void setRewardExp2(final Integer rewardExp2) {
        this.rewardExp2 = rewardExp2;
    }
    
    public Integer getRewardIron2() {
        return this.rewardIron2;
    }
    
    public void setRewardIron2(final Integer rewardIron2) {
        this.rewardIron2 = rewardIron2;
    }
}
