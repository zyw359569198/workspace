package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.gcld.task.reward.*;

public class TradeCombo implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer cardQuality;
    private String reward;
    private ITaskReward taskReward;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Integer getCardQuality() {
        return this.cardQuality;
    }
    
    public void setCardQuality(final Integer cardQuality) {
        this.cardQuality = cardQuality;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public ITaskReward getTaskReward() {
        return this.taskReward;
    }
    
    public void setTaskReward(final ITaskReward taskReward) {
        this.taskReward = taskReward;
    }
}
