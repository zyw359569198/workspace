package com.reign.gcld.building.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerBuilding implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer buildingId;
    private Integer lv;
    private Integer areaId;
    private Integer outputType;
    private Date updateTime;
    private Integer state;
    private Integer isNew;
    private Integer eventId;
    private Integer speedUpNum;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getBuildingId() {
        return this.buildingId;
    }
    
    public void setBuildingId(final Integer buildingId) {
        this.buildingId = buildingId;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public Integer getAreaId() {
        return this.areaId;
    }
    
    public void setAreaId(final Integer areaId) {
        this.areaId = areaId;
    }
    
    public Integer getOutputType() {
        return this.outputType;
    }
    
    public void setOutputType(final Integer outputType) {
        this.outputType = outputType;
    }
    
    public Date getUpdateTime() {
        return this.updateTime;
    }
    
    public void setUpdateTime(final Date updateTime) {
        this.updateTime = updateTime;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Integer getIsNew() {
        return this.isNew;
    }
    
    public void setIsNew(final Integer isNew) {
        this.isNew = isNew;
    }
    
    public Integer getEventId() {
        return this.eventId;
    }
    
    public void setEventId(final Integer eventId) {
        this.eventId = eventId;
    }
    
    public Integer getSpeedUpNum() {
        return this.speedUpNum;
    }
    
    public void setSpeedUpNum(final Integer speedUpNum) {
        this.speedUpNum = speedUpNum;
    }
}
