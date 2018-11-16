package com.reign.kf.match.sdata.domain;

import com.reign.framework.mybatis.*;

public class KfgzWorldCity implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String terrain;
    private Integer type;
    private Integer force_id;
    private Integer world_id;
    private Integer exp;
    private Integer food;
    private Integer iron;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setTerrain(final String terrain) {
        this.terrain = terrain;
    }
    
    public String getTerrain() {
        return this.terrain;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setForce_id(final Integer force_id) {
        this.force_id = force_id;
    }
    
    public Integer getForce_id() {
        return this.force_id;
    }
    
    public Integer getWorld_id() {
        return this.world_id;
    }
    
    public void setWorld_id(final Integer world_id) {
        this.world_id = world_id;
    }
    
    public void setExp(final Integer exp) {
        this.exp = exp;
    }
    
    public Integer getExp() {
        return this.exp;
    }
    
    public void setFood(final Integer food) {
        this.food = food;
    }
    
    public Integer getFood() {
        return this.food;
    }
    
    public void setIron(final Integer iron) {
        this.iron = iron;
    }
    
    public Integer getIron() {
        return this.iron;
    }
}
