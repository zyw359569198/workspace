package com.reign.kf.gw.kfwd.domain;

import com.reign.framework.hibernate.model.*;

public class KfwdGwScheduleInfo implements IModel
{
    private int pk;
    private int seasonId;
    private int scheduleId;
    private String matchAddress;
    private String matchName;
    private String gameServer;
    private int ruleId;
    private int state;
    public String levelRange;
    public int rewardRule;
    public int roundGold;
    public int levelRangeType;
    
    public KfwdGwScheduleInfo() {
        this.state = -1;
    }
    
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
    
    public String getMatchAddress() {
        return this.matchAddress;
    }
    
    public void setMatchAddress(final String matchAddress) {
        this.matchAddress = matchAddress;
    }
    
    public String getMatchName() {
        return this.matchName;
    }
    
    public void setMatchName(final String matchName) {
        this.matchName = matchName;
    }
    
    public String getGameServer() {
        return this.gameServer;
    }
    
    public void setGameServer(final String gameServer) {
        this.gameServer = gameServer;
    }
    
    public int getState() {
        return this.state;
    }
    
    public void setState(final int state) {
        this.state = state;
    }
    
    public int getRuleId() {
        return this.ruleId;
    }
    
    public void setRuleId(final int ruleId) {
        this.ruleId = ruleId;
    }
    
    public int getScheduleId() {
        return this.scheduleId;
    }
    
    public void setScheduleId(final int scheduleId) {
        this.scheduleId = scheduleId;
    }
    
    public String getLevelRange() {
        return this.levelRange;
    }
    
    public void setLevelRange(final String levelRange) {
        this.levelRange = levelRange;
    }
    
    public int getRewardRule() {
        return this.rewardRule;
    }
    
    public void setRewardRule(final int rewardRule) {
        this.rewardRule = rewardRule;
    }
    
    public int getRoundGold() {
        return this.roundGold;
    }
    
    public void setRoundGold(final int roundGold) {
        this.roundGold = roundGold;
    }
    
    public int getLevelRangeType() {
        return this.levelRangeType;
    }
    
    public void setLevelRangeType(final int levelRangeType) {
        this.levelRangeType = levelRangeType;
    }
}
