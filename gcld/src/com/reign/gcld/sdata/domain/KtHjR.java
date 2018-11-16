package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KtHjR implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer kindomLv;
    private Integer period;
    private Integer rewardExp;
    private Integer rewardIron;
    private Integer cityNum;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getKindomLv() {
        return this.kindomLv;
    }
    
    public void setKindomLv(final Integer kindomLv) {
        this.kindomLv = kindomLv;
    }
    
    public Integer getPeriod() {
        return this.period;
    }
    
    public void setPeriod(final Integer period) {
        this.period = period;
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
    
    public Integer getCityNum() {
        return this.cityNum;
    }
    
    public void setCityNum(final Integer cityNum) {
        this.cityNum = cityNum;
    }
}
