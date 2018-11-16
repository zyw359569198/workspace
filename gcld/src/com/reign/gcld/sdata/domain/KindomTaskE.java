package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KindomTaskE implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer degree;
    private Double rewardE;
    
    public Integer getDegree() {
        return this.degree;
    }
    
    public void setDegree(final Integer degree) {
        this.degree = degree;
    }
    
    public Double getRewardE() {
        return this.rewardE;
    }
    
    public void setRewardE(final Double rewardE) {
        this.rewardE = rewardE;
    }
}
