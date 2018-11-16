package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class TroopConscribeSpeed implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer level;
    private Double speedMutiE;
    
    public Integer getLevel() {
        return this.level;
    }
    
    public void setLevel(final Integer level) {
        this.level = level;
    }
    
    public Double getSpeedMutiE() {
        return this.speedMutiE;
    }
    
    public void setSpeedMutiE(final Double speedMutiE) {
        this.speedMutiE = speedMutiE;
    }
}
