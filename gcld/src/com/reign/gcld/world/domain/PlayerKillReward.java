package com.reign.gcld.world.domain;

import com.reign.framework.mybatis.*;

public class PlayerKillReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer killNum;
    private Integer nameList;
    private Integer reward;
    private Long rewardTime;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getKillNum() {
        return this.killNum;
    }
    
    public void setKillNum(final Integer killNum) {
        this.killNum = killNum;
    }
    
    public Integer getNameList() {
        return this.nameList;
    }
    
    public void setNameList(final Integer nameList) {
        this.nameList = nameList;
    }
    
    public Integer getReward() {
        return this.reward;
    }
    
    public void setReward(final Integer reward) {
        this.reward = reward;
    }
    
    public Long getRewardTime() {
        return this.rewardTime;
    }
    
    public void setRewardTime(final Long rewardTime) {
        this.rewardTime = rewardTime;
    }
}
