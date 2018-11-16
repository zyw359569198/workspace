package com.reign.kfgz.domain;

import com.reign.framework.hibernate.model.*;

public class KfgzBattleReward implements IModel
{
    int pk;
    String killRankRewardInfo;
    String soloReward;
    String cityReward;
    String occupyCityReward;
    String doubleInfo;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public String getKillRankRewardInfo() {
        return this.killRankRewardInfo;
    }
    
    public void setKillRankRewardInfo(final String killRankRewardInfo) {
        this.killRankRewardInfo = killRankRewardInfo;
    }
    
    public String getSoloReward() {
        return this.soloReward;
    }
    
    public void setSoloReward(final String soloReward) {
        this.soloReward = soloReward;
    }
    
    public String getOccupyCityReward() {
        return this.occupyCityReward;
    }
    
    public void setOccupyCityReward(final String occupyCityReward) {
        this.occupyCityReward = occupyCityReward;
    }
    
    public String getDoubleInfo() {
        return this.doubleInfo;
    }
    
    public void setDoubleInfo(final String doubleInfo) {
        this.doubleInfo = doubleInfo;
    }
    
    public String getCityReward() {
        return this.cityReward;
    }
    
    public void setCityReward(final String cityReward) {
        this.cityReward = cityReward;
    }
}
