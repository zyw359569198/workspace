package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.gcld.task.reward.*;

public class Power implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String intro;
    private String reward;
    private Integer nextPower;
    private String plot;
    private Integer goldInit;
    private Integer goldIncrease;
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
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getIntro() {
        return this.intro;
    }
    
    public void setIntro(final String intro) {
        this.intro = intro;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public Integer getNextPower() {
        return this.nextPower;
    }
    
    public void setNextPower(final Integer nextPower) {
        this.nextPower = nextPower;
    }
    
    public String getPlot() {
        return this.plot;
    }
    
    public void setPlot(final String plot) {
        this.plot = plot;
    }
    
    public Integer getGoldInit() {
        return this.goldInit;
    }
    
    public void setGoldInit(final Integer goldInit) {
        this.goldInit = goldInit;
    }
    
    public Integer getGoldIncrease() {
        return this.goldIncrease;
    }
    
    public void setGoldIncrease(final Integer goldIncrease) {
        this.goldIncrease = goldIncrease;
    }
}
