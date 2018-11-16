package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class WorldGuard implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer country;
    private String name;
    private Integer degree;
    private Integer armyId;
    private Integer lv;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getCountry() {
        return this.country;
    }
    
    public void setCountry(final Integer country) {
        this.country = country;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getDegree() {
        return this.degree;
    }
    
    public void setDegree(final Integer degree) {
        this.degree = degree;
    }
    
    public Integer getArmyId() {
        return this.armyId;
    }
    
    public void setArmyId(final Integer armyId) {
        this.armyId = armyId;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
}
