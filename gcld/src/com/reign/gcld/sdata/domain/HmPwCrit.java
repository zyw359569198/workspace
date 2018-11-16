package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class HmPwCrit implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer crit;
    private Double prob1;
    private Double prob2;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getCrit() {
        return this.crit;
    }
    
    public void setCrit(final Integer crit) {
        this.crit = crit;
    }
    
    public Double getProb1() {
        return this.prob1;
    }
    
    public void setProb1(final Double prob1) {
        this.prob1 = prob1;
    }
    
    public Double getProb2() {
        return this.prob2;
    }
    
    public void setProb2(final Double prob2) {
        this.prob2 = prob2;
    }
}
