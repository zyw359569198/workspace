package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class GeneralQuantity implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer lv;
    private Integer maxCivil;
    private Integer maxMilitary;
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public Integer getMaxCivil() {
        return this.maxCivil;
    }
    
    public void setMaxCivil(final Integer maxCivil) {
        this.maxCivil = maxCivil;
    }
    
    public Integer getMaxMilitary() {
        return this.maxMilitary;
    }
    
    public void setMaxMilitary(final Integer maxMilitary) {
        this.maxMilitary = maxMilitary;
    }
}
