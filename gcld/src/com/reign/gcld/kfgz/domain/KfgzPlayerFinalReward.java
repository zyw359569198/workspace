package com.reign.gcld.kfgz.domain;

import com.reign.framework.mybatis.*;

public class KfgzPlayerFinalReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private String rewardTimes;
    private Integer seasonId;
    private Integer nationScore;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public String getRewardTimes() {
        return this.rewardTimes;
    }
    
    public void setRewardTimes(final String rewardTimes) {
        this.rewardTimes = rewardTimes;
    }
    
    public Integer getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final Integer seasonId) {
        this.seasonId = seasonId;
    }
    
    public Integer getNationScore() {
        return this.nationScore;
    }
    
    public void setNationScore(final Integer nationScore) {
        this.nationScore = nationScore;
    }
    
    public int getRewardTimesById(final int id) {
        return this.rewardTimes.charAt(id - 1) - '0';
    }
}
