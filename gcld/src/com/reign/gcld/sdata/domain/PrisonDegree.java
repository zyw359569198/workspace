package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class PrisonDegree implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer degree;
    private Integer cost;
    private Integer expExtra;
    private Integer timeExtra;
    private Integer expSum;
    private Integer expFree;
    private Double getExpProb;
    private Integer tryGold;
    
    public Integer getDegree() {
        return this.degree;
    }
    
    public void setDegree(final Integer degree) {
        this.degree = degree;
    }
    
    public Integer getCost() {
        return this.cost;
    }
    
    public void setCost(final Integer cost) {
        this.cost = cost;
    }
    
    public Integer getExpExtra() {
        return this.expExtra;
    }
    
    public void setExpExtra(final Integer expExtra) {
        this.expExtra = expExtra;
    }
    
    public Integer getTimeExtra() {
        return this.timeExtra;
    }
    
    public void setTimeExtra(final Integer timeExtra) {
        this.timeExtra = timeExtra;
    }
    
    public Integer getExpSum() {
        return this.expSum;
    }
    
    public void setExpSum(final Integer expSum) {
        this.expSum = expSum;
    }
    
    public Integer getExpFree() {
        return this.expFree;
    }
    
    public void setExpFree(final Integer expFree) {
        this.expFree = expFree;
    }
    
    public Double getGetExpProb() {
        return this.getExpProb;
    }
    
    public void setGetExpProb(final Double getExpProb) {
        this.getExpProb = getExpProb;
    }
    
    public Integer getTryGold() {
        return this.tryGold;
    }
    
    public void setTryGold(final Integer tryGold) {
        this.tryGold = tryGold;
    }
}
