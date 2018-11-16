package com.reign.gcld.system.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class ServerTime implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Date startTime;
    private Date endTime;
    
    public ServerTime() {
    }
    
    public ServerTime(final Date startTime, final Date endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
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
}
