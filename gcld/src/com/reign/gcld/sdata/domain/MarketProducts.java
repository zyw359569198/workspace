package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class MarketProducts implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer degree;
    private String itemType;
    private Integer itemNum;
    private String costType;
    private Integer costNum;
    private Integer quality;
    private Double prob;
    
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
    
    public String getItemType() {
        return this.itemType;
    }
    
    public void setItemType(final String itemType) {
        this.itemType = itemType;
    }
    
    public Integer getItemNum() {
        return this.itemNum;
    }
    
    public void setItemNum(final Integer itemNum) {
        this.itemNum = itemNum;
    }
    
    public String getCostType() {
        return this.costType;
    }
    
    public void setCostType(final String costType) {
        this.costType = costType;
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
    
    public Double getProb() {
        return this.prob;
    }
    
    public void setProb(final Double prob) {
        this.prob = prob;
    }
}
