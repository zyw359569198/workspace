package com.reign.gcld.task.domain;

import com.reign.framework.mybatis.*;

public class PlayerTask implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer playerId;
    private Integer type;
    private Integer groupId;
    private Integer taskId;
    private Integer state;
    private Integer process;
    private Long startTime;
    
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
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getGroupId() {
        return this.groupId;
    }
    
    public void setGroupId(final Integer groupId) {
        this.groupId = groupId;
    }
    
    public Integer getTaskId() {
        return this.taskId;
    }
    
    public void setTaskId(final Integer taskId) {
        this.taskId = taskId;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Integer getProcess() {
        return this.process;
    }
    
    public void setProcess(final Integer process) {
        this.process = process;
    }
    
    public Long getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(final Long startTime) {
        this.startTime = startTime;
    }
}
