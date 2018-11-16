package com.reign.gcld.battle.domain;

import com.reign.framework.mybatis.*;
import java.util.*;

public class PlayerBatRank implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer playerId;
    private Integer rank;
    private String reward;
    private Date lastRankTime;
    private Integer rankScore;
    private Integer buyTimesToday;
    private Integer rankBatNum;
    
    public Integer getPlayerId() {
        return this.playerId;
    }
    
    public void setPlayerId(final Integer playerId) {
        this.playerId = playerId;
    }
    
    public Integer getRank() {
        return this.rank;
    }
    
    public void setRank(final Integer rank) {
        this.rank = rank;
    }
    
    public String getReward() {
        return this.reward;
    }
    
    public void setReward(final String reward) {
        this.reward = reward;
    }
    
    public Date getLastRankTime() {
        return this.lastRankTime;
    }
    
    public void setLastRankTime(final Date lastRankTime) {
        this.lastRankTime = lastRankTime;
    }
    
    public Integer getRankScore() {
        return this.rankScore;
    }
    
    public void setRankScore(final Integer rankScore) {
        this.rankScore = rankScore;
    }
    
    public Integer getBuyTimesToday() {
        return this.buyTimesToday;
    }
    
    public void setBuyTimesToday(final Integer buyTimesToday) {
        this.buyTimesToday = buyTimesToday;
    }
    
    public Integer getRankBatNum() {
        return this.rankBatNum;
    }
    
    public void setRankBatNum(final Integer rankBatNum) {
        this.rankBatNum = rankBatNum;
    }
}
