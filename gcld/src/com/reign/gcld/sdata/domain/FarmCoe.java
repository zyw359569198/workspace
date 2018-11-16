package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class FarmCoe implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer lvHigh;
    private Integer lvLow;
    private Integer coe;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getLvHigh() {
        return this.lvHigh;
    }
    
    public void setLvHigh(final Integer lvHigh) {
        this.lvHigh = lvHigh;
    }
    
    public Integer getLvLow() {
        return this.lvLow;
    }
    
    public void setLvLow(final Integer lvLow) {
        this.lvLow = lvLow;
    }
    
    public Integer getCoe() {
        return this.coe;
    }
    
    public void setCoe(final Integer coe) {
        this.coe = coe;
    }
}
