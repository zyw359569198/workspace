package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KtKjTr implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer kindomLv;
    private Integer rewardIron;
    private Integer rewardExp;
    
    public Integer getKindomLv() {
        return this.kindomLv;
    }
    
    public void setKindomLv(final Integer kindomLv) {
        this.kindomLv = kindomLv;
    }
    
    public Integer getRewardIron() {
        return this.rewardIron;
    }
    
    public void setRewardIron(final Integer rewardIron) {
        this.rewardIron = rewardIron;
    }
    
    public Integer getRewardExp() {
        return this.rewardExp;
    }
    
    public void setRewardExp(final Integer rewardExp) {
        this.rewardExp = rewardExp;
    }
}
