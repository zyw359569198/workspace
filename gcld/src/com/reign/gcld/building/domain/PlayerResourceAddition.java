package com.reign.gcld.building.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerResourceAddition implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer resourceType;
    private Integer additionMode;
    private Date endTime;
    private Integer timeType;
    private Integer taskId;
    
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
    
    public Integer getResourceType() {
        return this.resourceType;
    }
    
    public void setResourceType(final Integer resourceType) {
        this.resourceType = resourceType;
    }
    
    public Integer getAdditionMode() {
        return this.additionMode;
    }
    
    public void setAdditionMode(final Integer additionMode) {
        this.additionMode = additionMode;
    }
    
    public Date getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }
    
    public Integer getTimeType() {
        return this.timeType;
    }
    
    public void setTimeType(final Integer timeType) {
        this.timeType = timeType;
    }
    
    public Integer getTaskId() {
        return this.taskId;
    }
    
    public void setTaskId(final Integer taskId) {
        this.taskId = taskId;
    }
}
