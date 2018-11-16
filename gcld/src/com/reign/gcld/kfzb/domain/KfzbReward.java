package com.reign.gcld.kfzb.domain;

import com.reign.framework.mybatis.*;

public class KfzbReward implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer seasonId;
    private String rewardinfo;
    private Integer doneNum;
    private String title;
    private String info;
    private Integer lastPos;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final Integer seasonId) {
        this.seasonId = seasonId;
    }
    
    public String getRewardinfo() {
        return this.rewardinfo;
    }
    
    public void setRewardinfo(final String rewardinfo) {
        this.rewardinfo = rewardinfo;
    }
    
    public Integer getDoneNum() {
        return this.doneNum;
    }
    
    public void setDoneNum(final Integer doneNum) {
        this.doneNum = doneNum;
    }
    
    public String getTitle() {
        return this.title;
    }
    
    public void setTitle(final String title) {
        this.title = title;
    }
    
    public String getInfo() {
        return this.info;
    }
    
    public void setInfo(final String info) {
        this.info = info;
    }
    
    public Integer getLastPos() {
        return this.lastPos;
    }
    
    public void setLastPos(final Integer lastPos) {
        this.lastPos = lastPos;
    }
}
