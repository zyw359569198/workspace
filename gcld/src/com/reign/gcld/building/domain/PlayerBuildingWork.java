package com.reign.gcld.building.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerBuildingWork implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer workId;
    private Date startTime;
    private Date endTime;
    private Integer targetBuildId;
    private Integer workState;
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
    
    public Integer getWorkId() {
        return this.workId;
    }
    
    public void setWorkId(final Integer workId) {
        this.workId = workId;
    }
    
    public Date getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }
    
    public Date getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }
    
    public Integer getTargetBuildId() {
        return this.targetBuildId;
    }
    
    public void setTargetBuildId(final Integer targetBuildId) {
        this.targetBuildId = targetBuildId;
    }
    
    public Integer getWorkState() {
        return this.workState;
    }
    
    public void setWorkState(final Integer workState) {
        this.workState = workState;
    }
    
    public Integer getTaskId() {
        return this.taskId;
    }
    
    public void setTaskId(final Integer taskId) {
        this.taskId = taskId;
    }
}
