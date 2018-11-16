package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class TavernStat implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer preStat;
    private Integer nextStat;
    private Double prob;
    
    public Integer getPreStat() {
        return this.preStat;
    }
    
    public void setPreStat(final Integer preStat) {
        this.preStat = preStat;
    }
    
    public Integer getNextStat() {
        return this.nextStat;
    }
    
    public void setNextStat(final Integer nextStat) {
        this.nextStat = nextStat;
    }
    
    public Double getProb() {
        return this.prob;
    }
    
    public void setProb(final Double prob) {
        this.prob = prob;
    }
}
