package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WdSjFe implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer t;
    private Double e;
    
    public Integer getT() {
        return this.t;
    }
    
    public void setT(final Integer t) {
        this.t = t;
    }
    
    public Double getE() {
        return this.e;
    }
    
    public void setE(final Double e) {
        this.e = e;
    }
}
