package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.gcld.task.reward.*;

public class TradeMarchant implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private Integer quality;
    private String qualityName;
    private String effectName;
    private String reward;
    private Double prob;
    private String pic;
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
    
    public Integer getQuality() {
        return this.quality;
    }
    
    public void setQuality(final Integer quality) {
        this.quality = quality;
    }
    
    public String getQualityName() {
        return this.qualityName;
    }
    
    public void setQualityName(final String qualityName) {
        this.qualityName = qualityName;
    }
    
    public String getEffectName() {
        return this.effectName;
    }
    
    public void setEffectName(final String effectName) {
        this.effectName = effectName;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public Double getProb() {
        return this.prob;
    }
    
    public void setProb(final Double prob) {
        this.prob = prob;
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
    
    public void setTaskReward(final ITaskReward taskReward) {
        this.taskReward = taskReward;
    }
}
