package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KtMrSpeed implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer troopType;
    private Integer cityType;
    private Integer time;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getTroopType() {
        return this.troopType;
    }
    
    public void setTroopType(final Integer troopType) {
        this.troopType = troopType;
    }
    
    public Integer getCityType() {
        return this.cityType;
    }
    
    public void setCityType(final Integer cityType) {
        this.cityType = cityType;
    }
    
    public Integer getTime() {
        return this.time;
    }
    
    public void setTime(final Integer time) {
        this.time = time;
    }
}
