package com.reign.gcld.rank.common;

import java.util.*;

public class NationTaskAnd
{
    private int taskId;
    private int cityId;
    private String cityName;
    private Date endTime;
    private String taskIntros;
    private int state;
    private int canGetReward;
    private int attType;
    private int isWin;
    private int forceId;
    private int finishTime;
    private int target;
    private String taskRelative;
    private int taskType;
    
    public String getTaskRelative() {
        return this.taskRelative;
    }
    
    public void setTaskRelative(final String taskRelative) {
        this.taskRelative = taskRelative;
    }
    
    public int getTarget() {
        return this.target;
    }
    
    public void setTarget(final int target) {
        this.target = target;
    }
    
    public int getFinishTime() {
        return this.finishTime;
    }
    
    public void setFinishTime(final int finishTime) {
        this.finishTime = finishTime;
    }
    
    public int getTaskType() {
        return this.taskType;
    }
    
    public void setTaskType(final int taskType) {
        this.taskType = taskType;
    }
    
    public int getForceId() {
        return this.forceId;
    }
    
    public void setForceId(final int forceId) {
        this.forceId = forceId;
    }
    
    public int getIsWin() {
        return this.isWin;
    }
    
    public void setIsWin(final int isWin) {
        this.isWin = isWin;
    }
    
    public int getAttType() {
        return this.attType;
    }
    
    public void setAttType(final int attType) {
        this.attType = attType;
    }
    
    public int getTaskId() {
        return this.taskId;
    }
    
    public void setTaskId(final int taskId) {
        this.taskId = taskId;
    }
    
    public int getCityId() {
        return this.cityId;
    }
    
    public void setCityId(final int cityId) {
        this.cityId = cityId;
    }
    
    public String getCityName() {
        return this.cityName;
    }
    
    public void setCityName(final String cityName) {
        this.cityName = cityName;
    }
    
    public Date getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }
    
    public String getTaskIntros() {
        return this.taskIntros;
    }
    
    public void setTaskIntros(final String taskIntros) {
        this.taskIntros = taskIntros;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public int getCanGetReward() {
        return this.canGetReward;
    }
    
    public void setCanGetReward(final int canGetReward) {
        this.canGetReward = canGetReward;
    }
}
