package com.reign.kf.comm.entity.kfwd.response;

public class KfwdRewardRuleInfo
{
    private int pk;
    private int seasonId;
    private String reward;
    private int winNum;
    private int groupType;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
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
