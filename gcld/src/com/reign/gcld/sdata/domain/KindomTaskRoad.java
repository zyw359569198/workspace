package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KindomTaskRoad implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String cities;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getCities() {
        return this.cities;
    }
    
    public void setCities(final String cities) {
        this.cities = cities;
    }
}
