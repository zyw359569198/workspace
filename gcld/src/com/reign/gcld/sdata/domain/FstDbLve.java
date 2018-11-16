package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class FstDbLve implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer lvLow;
    private Integer lvHigh;
    private Double e;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getLvLow() {
        return this.lvLow;
    }
    
    public void setLvLow(final Integer lvLow) {
        this.lvLow = lvLow;
    }
    
    public Integer getLvHigh() {
        return this.lvHigh;
    }
    
    public void setLvHigh(final Integer lvHigh) {
        this.lvHigh = lvHigh;
    }
    
    public Double getE() {
        return this.e;
    }
    
    public void setE(final Double e) {
        this.e = e;
    }
}
