package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.gcld.task.reward.*;

public class UpdateReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer lv;
    private Integer type;
    private String reward;
    private ITaskReward taskReward;
    
    public ITaskReward getTaskReward() {
        return this.taskReward;
    }
    
    public void setTaskReward(final ITaskReward taskReward) {
        this.taskReward = taskReward;
    }
    
    public Integer getLv() {
        return this.lv;
    }
    
    public void setLv(final Integer lv) {
        this.lv = lv;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
}
