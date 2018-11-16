package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.gcld.task.reward.*;

public class SearchCombo implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer pos;
    private Integer lvDegree;
    private Integer minLv;
    private Integer maxLv;
    private String reward;
    private ITaskReward taskReward;
    
    public ITaskReward getTaskReward() {
        return this.taskReward;
    }
    
    public void setTaskReward(final ITaskReward taskReward) {
        this.taskReward = taskReward;
    }
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getPos() {
        return this.pos;
    }
    
    public void setPos(final Integer pos) {
        this.pos = pos;
    }
    
    public Integer getLvDegree() {
        return this.lvDegree;
    }
    
    public void setLvDegree(final Integer lvDegree) {
        this.lvDegree = lvDegree;
    }
    
    public Integer getMinLv() {
        return this.minLv;
    }
    
    public void setMinLv(final Integer minLv) {
        this.minLv = minLv;
    }
    
    public Integer getMaxLv() {
        return this.maxLv;
    }
    
    public void setMaxLv(final Integer maxLv) {
        this.maxLv = maxLv;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
}
