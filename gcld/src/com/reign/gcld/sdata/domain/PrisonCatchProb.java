package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class PrisonCatchProb implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer n;
    private Integer prisonLowLv;
    private Integer prisonHighLv;
    private Double prob;
    private Integer probLv;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getN() {
        return this.n;
    }
    
    public void setN(final Integer n) {
        this.n = n;
    }
    
    public Integer getPrisonLowLv() {
        return this.prisonLowLv;
    }
    
    public void setPrisonLowLv(final Integer prisonLowLv) {
        this.prisonLowLv = prisonLowLv;
    }
    
    public Integer getPrisonHighLv() {
        return this.prisonHighLv;
    }
    
    public void setPrisonHighLv(final Integer prisonHighLv) {
        this.prisonHighLv = prisonHighLv;
    }
    
    public Double getProb() {
        return this.prob;
    }
    
    public void setProb(final Double prob) {
        this.prob = prob;
    }
    
    public Integer getProbLv() {
        return this.probLv;
    }
    
    public void setProbLv(final Integer probLv) {
        this.probLv = probLv;
    }
}
