package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WorldOutputPerTime implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Double beta;
    private Integer quality;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Double getBeta() {
        return this.beta;
    }
    
    public void setBeta(final Double beta) {
        this.beta = beta;
    }
    
    public Integer getQuality() {
        return this.quality;
    }
    
    public void setQuality(final Integer quality) {
        this.quality = quality;
    }
}
