package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class FightStragtegyCoe implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer attStrategy;
    private Integer defStrategy;
    private Integer winerSide;
    private Double attLost;
    private Double defLost;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getAttStrategy() {
        return this.attStrategy;
    }
    
    public void setAttStrategy(final Integer attStrategy) {
        this.attStrategy = attStrategy;
    }
    
    public Integer getDefStrategy() {
        return this.defStrategy;
    }
    
    public void setDefStrategy(final Integer defStrategy) {
        this.defStrategy = defStrategy;
    }
    
    public Integer getWinerSide() {
        return this.winerSide;
    }
    
    public void setWinerSide(final Integer winerSide) {
        this.winerSide = winerSide;
    }
    
    public Double getAttLost() {
        return this.attLost;
    }
    
    public void setAttLost(final Double attLost) {
        this.attLost = attLost;
    }
    
    public Double getDefLost() {
        return this.defLost;
    }
    
    public void setDefLost(final Double defLost) {
        this.defLost = defLost;
    }
}
