package com.reign.gcld.world.domain;

import com.reign.framework.mybatis.*;

public class CitiesPerHour implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer hour;
    private String cities;
    
    public Integer getHour() {
        return this.hour;
    }
    
    public void setHour(final Integer hour) {
        this.hour = hour;
    }
    
    public String getCities() {
        return this.cities;
    }
    
    public void setCities(final String cities) {
        this.cities = cities;
    }
}
