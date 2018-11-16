package com.reign.gcld.mine.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerMine implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer mineId;
    private Integer ownerId;
    private Integer type;
    private Integer page;
    private Date startTime;
    private Date hideTime;
    private Integer mode;
    private Integer state;
    private Integer taskId;
    private Integer isNew;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Integer getMineId() {
        return this.mineId;
    }
    
    public void setMineId(final Integer mineId) {
        this.mineId = mineId;
    }
    
    public Integer getOwnerId() {
        return this.ownerId;
    }
    
    public void setOwnerId(final Integer ownerId) {
        this.ownerId = ownerId;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getPage() {
        return this.page;
    }
    
    public void setPage(final Integer page) {
        this.page = page;
    }
    
    public Date getStartTime() {
        return this.startTime;
    }
    
    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }
    
    public Date getHideTime() {
        return this.hideTime;
    }
    
    public void setHideTime(final Date hideTime) {
        this.hideTime = hideTime;
    }
    
    public Integer getMode() {
        return this.mode;
    }
    
    public void setMode(final Integer mode) {
        this.mode = mode;
    }
    
    public Integer getState() {
        return this.state;
    }
    
    public void setState(final Integer state) {
        this.state = state;
    }
    
    public Integer getTaskId() {
        return this.taskId;
    }
    
    public void setTaskId(final Integer taskId) {
        this.taskId = taskId;
    }
    
    public Integer getIsNew() {
        return this.isNew;
    }
    
    public void setIsNew(final Integer isNew) {
        this.isNew = isNew;
    }
}
