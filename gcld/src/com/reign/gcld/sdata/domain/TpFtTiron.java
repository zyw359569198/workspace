package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class TpFtTiron implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer iron;
    private Integer quality;
    private Double prob;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getIron() {
        return this.iron;
    }
    
    public void setIron(final Integer iron) {
        this.iron = iron;
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
