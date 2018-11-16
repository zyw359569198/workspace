package com.reign.gcld.battle.common;

public class MilitaryDto
{
    private Integer state;
    private Integer lv;
    private Integer forces;
    private Integer locationId;
    
    public MilitaryDto() {
    }
    
    public MilitaryDto(final Integer state, final Integer lv) {
        this.state = state;
        this.lv = lv;
    }
    
    public MilitaryDto(final Integer state, final Integer lv, final Integer forces) {
        this.state = state;
        this.lv = lv;
        this.forces = forces;
    }
    
    public MilitaryDto(final Integer forces) {
        this.forces = forces;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public Integer getForces() {
        return this.forces;
    }
    
    public void setForces(final Integer forces) {
        this.forces = forces;
    }
    
    public Integer getLocationId() {
        return this.locationId;
    }
    
    public void setLocationId(final Integer locationId) {
        this.locationId = locationId;
    }
}
