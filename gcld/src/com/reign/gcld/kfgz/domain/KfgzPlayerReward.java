package com.reign.gcld.kfgz.domain;

import com.reign.framework.mybatis.*;

public class KfgzPlayerReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer id;
    private Integer competitorId;
    private Integer seasonId;
    private Integer rewardTimes;
    private String reward;
    private Integer gzid;
    private Integer nation;
    
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    public Integer getCompetitorId() {
        return this.competitorId;
    }
    
    public void setCompetitorId(final Integer competitorId) {
        this.competitorId = competitorId;
    }
    
    public Integer getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final Integer seasonId) {
        this.seasonId = seasonId;
    }
    
    public Integer getRewardTimes() {
        return this.rewardTimes;
    }
    
    public void setRewardTimes(final Integer rewardTimes) {
        this.rewardTimes = rewardTimes;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public Integer getGzid() {
        return this.gzid;
    }
    
    public void setGzid(final Integer gzid) {
        this.gzid = gzid;
    }
    
    public Integer getNation() {
        return this.nation;
    }
    
    public void setNation(final Integer nation) {
        this.nation = nation;
    }
}
