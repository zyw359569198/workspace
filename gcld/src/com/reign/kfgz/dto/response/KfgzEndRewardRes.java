package com.reign.kfgz.dto.response;

public class KfgzEndRewardRes
{
    int pk;
    String rewardInfo;
    String doubleInfo;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public String getRewardInfo() {
        return this.rewardInfo;
    }
    
    public void setRewardInfo(final String rewardInfo) {
        this.rewardInfo = rewardInfo;
    }
    
    public String getDoubleInfo() {
        return this.doubleInfo;
    }
    
    public void setDoubleInfo(final String doubleInfo) {
        this.doubleInfo = doubleInfo;
    }
}
