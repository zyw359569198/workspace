package com.reign.kfgz.dto.response;

import java.util.*;

public class KfgzSeasonInfoRes
{
    public static final int START = 2;
    public static final int ISSUE_REWARD = 3;
    public static final int END = 4;
    int seasonId;
    int state;
    int ruleId;
    int rewardgId;
    Date beginTime;
    Date firstBattleTime;
    Date endTime;
    String ruleBattleTime;
    String battleDelayInfo;
    
    public int getSeasonId() {
        return this.seasonId;
    }
    
    public void setSeasonId(final int seasonId) {
        this.seasonId = seasonId;
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
    
    public int getRewardgId() {
        return this.rewardgId;
    }
    
    public void setRewardgId(final int rewardgId) {
        this.rewardgId = rewardgId;
    }
    
    public Date getBeginTime() {
        return this.beginTime;
    }
    
    public void setBeginTime(final Date beginTime) {
        this.beginTime = beginTime;
    }
    
    public Date getFirstBattleTime() {
        return this.firstBattleTime;
    }
    
    public void setFirstBattleTime(final Date firstBattleTime) {
        this.firstBattleTime = firstBattleTime;
    }
    
    public Date getEndTime() {
        return this.endTime;
    }
    
    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }
    
    public String getRuleBattleTime() {
        return this.ruleBattleTime;
    }
    
    public void setRuleBattleTime(final String ruleBattleTime) {
        this.ruleBattleTime = ruleBattleTime;
    }
    
    public String getBattleDelayInfo() {
        return this.battleDelayInfo;
    }
    
    public void setBattleDelayInfo(final String battleDelayInfo) {
        this.battleDelayInfo = battleDelayInfo;
    }
}
