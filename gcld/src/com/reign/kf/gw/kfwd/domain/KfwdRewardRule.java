package com.reign.kf.gw.kfwd.domain;

import com.reign.framework.hibernate.model.*;

public class KfwdRewardRule implements IModel
{
    private int pk;
    private String reward;
    private int winNum;
    private int groupType;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public int getGroupType() {
        return this.groupType;
    }
    
    public void setGroupType(final int groupType) {
        this.groupType = groupType;
    }
    
    public int getWinNum() {
        return this.winNum;
    }
    
    public void setWinNum(final int winNum) {
        this.winNum = winNum;
    }
}
