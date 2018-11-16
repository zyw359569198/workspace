package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;

public class ConfTask implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String taskInfo;
    private String potentialReward;
    private String task;
    private String rewardType;
    private String reward;
    private Double lumberNeed;
    private Double lumberHave;
    private Double copperNeed;
    private Double copperHave;
    private Double expNeed;
    private Double expHave;
    private Integer time;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public String getTaskInfo() {
        return this.taskInfo;
    }
    
    public void setTaskInfo(final String taskInfo) {
        this.taskInfo = taskInfo;
    }
    
    public String getPotentialReward() {
        return this.potentialReward;
    }
    
    public void setPotentialReward(final String potentialReward) {
        this.potentialReward = potentialReward;
    }
    
    public String getTask() {
        return this.task;
    }
    
    public void setTask(final String task) {
        this.task = task;
    }
    
    public String getRewardType() {
        return this.rewardType;
    }
    
    public void setRewardType(final String rewardType) {
        this.rewardType = rewardType;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public Double getLumberNeed() {
        return this.lumberNeed;
    }
    
    public void setLumberNeed(final Double lumberNeed) {
        this.lumberNeed = lumberNeed;
    }
    
    public Double getLumberHave() {
        return this.lumberHave;
    }
    
    public void setLumberHave(final Double lumberHave) {
        this.lumberHave = lumberHave;
    }
    
    public Double getCopperNeed() {
        return this.copperNeed;
    }
    
    public void setCopperNeed(final Double copperNeed) {
        this.copperNeed = copperNeed;
    }
    
    public Double getCopperHave() {
        return this.copperHave;
    }
    
    public void setCopperHave(final Double copperHave) {
        this.copperHave = copperHave;
    }
    
    public Double getExpNeed() {
        return this.expNeed;
    }
    
    public void setExpNeed(final Double expNeed) {
        this.expNeed = expNeed;
    }
    
    public Double getExpHave() {
        return this.expHave;
    }
    
    public void setExpHave(final Double expHave) {
        this.expHave = expHave;
    }
    
    public Integer getTime() {
        return this.time;
    }
    
    public void setTime(final Integer time) {
        this.time = time;
    }
}
