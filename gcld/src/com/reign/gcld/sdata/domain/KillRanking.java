package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.gcld.task.reward.*;

public class KillRanking implements IModel
{
    private static final long serialVersionUID = 1L;
    private Double worldOutputE2;
    private String ironReward;
    private Integer kindomTaskIron;
    private String baseReward;
    private Integer ranking;
    private ITaskReward bReward;
    private ITaskReward iReward;
    
    public ITaskReward getbReward() {
        return this.bReward;
    }
    
    public void setbReward(final ITaskReward bReward) {
        this.bReward = bReward;
    }
    
    public ITaskReward getiReward() {
        return this.iReward;
    }
    
    public void setiReward(final ITaskReward iReward) {
        this.iReward = iReward;
    }
    
    public Double getWorldOutputE2() {
        return this.worldOutputE2;
    }
    
    public void setWorldOutputE2(final Double worldOutputE2) {
        this.worldOutputE2 = worldOutputE2;
    }
    
    public String getIronReward() {
        return this.ironReward;
    }
    
    public void setIronReward(final String ironReward) {
        this.ironReward = ironReward;
    }
    
    public Integer getKindomTaskIron() {
        return this.kindomTaskIron;
    }
    
    public void setKindomTaskIron(final Integer kindomTaskIron) {
        this.kindomTaskIron = kindomTaskIron;
    }
    
    public String getBaseReward() {
        return this.baseReward;
    }
    
    public void setBaseReward(final String baseReward) {
        this.baseReward = baseReward;
    }
    
    public Integer getRanking() {
        return this.ranking;
    }
    
    public void setRanking(final Integer ranking) {
        this.ranking = ranking;
    }
}
