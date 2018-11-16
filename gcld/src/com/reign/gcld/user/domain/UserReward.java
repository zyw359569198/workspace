package com.reign.gcld.user.domain;

import com.reign.framework.mybatis.*;

public class UserReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer vId;
    private Integer userId;
    private String yx;
    private Integer rewardType;
    
    public Integer getVId() {
        return this.vId;
    }
    
    public void setVId(final Integer vId) {
        this.vId = vId;
    }
    
    public Integer getUserId() {
        return this.userId;
    }
    
    public void setUserId(final Integer userId) {
        this.userId = userId;
    }
    
    public String getYx() {
        return this.yx;
    }
    
    public void setYx(final String yx) {
        this.yx = yx;
    }
    
    public Integer getRewardType() {
        return this.rewardType;
    }
    
    public void setRewardType(final Integer rewardType) {
        this.rewardType = rewardType;
    }
}
