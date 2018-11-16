package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.gcld.task.reward.*;

public class LoginRewardCombo implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private String name;
    private String pic;
    private Integer quality;
    private String cardQuality;
    private String rewardBase;
    private String rewardCombo;
    private Double prob;
    private ITaskReward baseReward;
    private ITaskReward comboReward;
    
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
    
    public String getPic() {
        return this.pic;
    }
    
    public void setPic(final String pic) {
        this.pic = pic;
    }
    
    public Integer getQuality() {
        return this.quality;
    }
    
    public void setQuality(final Integer quality) {
        this.quality = quality;
    }
    
    public String getCardQuality() {
        return this.cardQuality;
    }
    
    public void setCardQuality(final String cardQuality) {
        this.cardQuality = cardQuality;
    }
    
    public String getRewardBase() {
        return this.rewardBase;
    }
    
    public void setRewardBase(final String rewardBase) {
        this.rewardBase = rewardBase;
    }
    
    public String getRewardCombo() {
        return this.rewardCombo;
    }
    
    public void setRewardCombo(final String rewardCombo) {
        this.rewardCombo = rewardCombo;
    }
    
    public Double getProb() {
        return this.prob;
    }
    
    public void setProb(final Double prob) {
        this.prob = prob;
    }
    
    public ITaskReward getBaseReward() {
        return this.baseReward;
    }
    
    public void setBaseReward(final ITaskReward baseReward) {
        this.baseReward = baseReward;
    }
    
    public ITaskReward getComboReward() {
        return this.comboReward;
    }
    
    public void setComboReward(final ITaskReward comboReward) {
        this.comboReward = comboReward;
    }
}
