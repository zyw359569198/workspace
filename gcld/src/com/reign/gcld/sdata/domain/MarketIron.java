package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class MarketIron implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer degree;
    private Integer itemNum;
    private Integer costNum;
    private Integer quality;
    private Integer prob;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getDegree() {
        return this.degree;
    }
    
    public void setDegree(final Integer degree) {
        this.degree = degree;
    }
    
    public Integer getItemNum() {
        return this.itemNum;
    }
    
    public void setItemNum(final Integer itemNum) {
        this.itemNum = itemNum;
    }
    
    public Integer getCostNum() {
        return this.costNum;
    }
    
    public void setCostNum(final Integer costNum) {
        this.costNum = costNum;
    }
    
    public Integer getQuality() {
        return this.quality;
    }
    
    public void setQuality(final Integer quality) {
        this.quality = quality;
    }
    
    public Integer getProb() {
        return this.prob;
    }
    
    public void setProb(final Integer prob) {
        this.prob = prob;
    }
}
