package com.reign.gcld.activity.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class Activity implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Date startTime;
    private Date endTime;
    private String paramsInfo;
    private String name;
    
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
    
    public String getParamsInfo() {
        return this.paramsInfo;
    }
    
    public void setParamsInfo(final String paramsInfo) {
        this.paramsInfo = paramsInfo;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
}
