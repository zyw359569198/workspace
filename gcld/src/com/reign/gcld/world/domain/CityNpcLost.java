package com.reign.gcld.world.domain;

import com.reign.framework.mybatis.*;

public class CityNpcLost implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer cityId;
    private String npcLost;
    
    public Integer getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final Integer cityId) {
        this.cityId = cityId;
    }
    
    public String getNpcLost() {
        return this.npcLost;
    }
    
    public void setNpcLost(final String npcLost) {
        this.npcLost = npcLost;
    }
}
