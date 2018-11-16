package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.gcld.task.reward.*;

public class WorldSearchDefault implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer type;
    private Integer level;
    private String name;
    private String reward;
    private String disc;
    private String pic;
    private ITaskReward taskReward;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public Integer getLevel() {
        return this.level;
    }
    
    public void setLevel(final Integer level) {
        this.level = level;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public String getDisc() {
        return this.disc;
    }
    
    public void setDisc(final String disc) {
        this.disc = disc;
    }
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public ITaskReward getTaskReward() {
        return this.taskReward;
    }
    
    public void setTaskReward(final ITaskReward reward) {
        this.taskReward = reward;
    }
}
