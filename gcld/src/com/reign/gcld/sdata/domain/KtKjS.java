package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KtKjS implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer t;
    private Double reR;
    private Double reT;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getT() {
        return this.t;
    }
    
    public void setT(final Integer t) {
        this.t = t;
    }
    
    public Double getReR() {
        return this.reR;
    }
    
    public void setReR(final Double reR) {
        this.reR = reR;
    }
    
    public Double getReT() {
        return this.reT;
    }
    
    public void setReT(final Double reT) {
        this.reT = reT;
    }
}
