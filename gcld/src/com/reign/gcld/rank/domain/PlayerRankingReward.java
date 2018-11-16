package com.reign.gcld.rank.domain;

import com.reign.framework.mybatis.*;

public class PlayerRankingReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerid;
    private Integer type;
    private String rewardStr;
    private Integer vid;
    
    public Integer getPlayerid() {
        return this.playerid;
    }
    
    public void setPlayerid(final Integer playerid) {
        this.playerid = playerid;
    }
    
    public Integer getType() {
        return this.type;
    }
    
    public void setType(final Integer type) {
        this.type = type;
    }
    
    public String getRewardStr() {
        return this.rewardStr;
    }
    
    public void setRewardStr(final String rewardStr) {
        this.rewardStr = rewardStr;
    }
    
    public Integer getVid() {
        return this.vid;
    }
    
    public void setVid(final Integer vid) {
        this.vid = vid;
    }
}
