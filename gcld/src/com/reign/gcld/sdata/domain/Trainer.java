package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class Trainer implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer time;
    private Double costRate;
    private Integer exp;
    private Double efficiency;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getTime() {
        return this.time;
    }
    
    public void setTime(final Integer time) {
        this.time = time;
    }
    
    public Double getCostRate() {
        return this.costRate;
    }
    
    public void setCostRate(final Double costRate) {
        this.costRate = costRate;
    }
    
    public Integer getExp() {
        return this.exp;
    }
    
    public void setExp(final Integer exp) {
        this.exp = exp;
    }
    
    public Double getEfficiency() {
        return this.efficiency;
    }
    
    public void setEfficiency(final Double efficiency) {
        this.efficiency = efficiency;
    }
}
