package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class KtNfTroop implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer nation;
    private Integer troopId;
    private Integer type;
    private String name;
    private String path;
    private Integer soil;
    private Integer stone;
    private Integer lumber;
    private Integer interval;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getNation() {
        return this.nation;
    }
    
    public void setNation(final Integer nation) {
        this.nation = nation;
    }
    
    public Integer getTroopId() {
        return this.troopId;
    }
    
    public void setTroopId(final Integer troopId) {
        this.troopId = troopId;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getPath() {
        return this.path;
    }
    
    public void setPath(final String path) {
        this.path = path;
    }
    
    public Integer getSoil() {
        return this.soil;
    }
    
    public void setSoil(final Integer soil) {
        this.soil = soil;
    }
    
    public Integer getStone() {
        return this.stone;
    }
    
    public void setStone(final Integer stone) {
        this.stone = stone;
    }
    
    public Integer getLumber() {
        return this.lumber;
    }
    
    public void setLumber(final Integer lumber) {
        this.lumber = lumber;
    }
    
    public Integer getInterval() {
        return this.interval;
    }
    
    public void setInterval(final Integer interval) {
        this.interval = interval;
    }
}
