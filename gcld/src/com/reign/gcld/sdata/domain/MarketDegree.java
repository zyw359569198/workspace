package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class MarketDegree implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer degree;
    private Integer minLv;
    private Integer maxLv;
    private String qList;
    private Double ironProb;
    
    public Integer getDegree() {
        return this.degree;
    }
    
    public void setDegree(final Integer degree) {
        this.degree = degree;
    }
    
    public Integer getMinLv() {
        return this.minLv;
    }
    
    public void setMinLv(final Integer minLv) {
        this.minLv = minLv;
    }
    
    public Integer getMaxLv() {
        return this.maxLv;
    }
    
    public void setMaxLv(final Integer maxLv) {
        this.maxLv = maxLv;
    }
    
    public String getQList() {
        return this.qList;
    }
    
    public void setQList(final String qList) {
        this.qList = qList;
    }
    
    public Double getIronProb() {
        return this.ironProb;
    }
    
    public void setIronProb(final Double ironProb) {
        this.ironProb = ironProb;
    }
}
