package com.reign.kfgz.domain;

import com.reign.framework.hibernate.model.*;
import java.util.*;

public class KfgzSeasonInfo implements IModel
{
    int pk;
    int seasonId;
    int state;
    int ruleId;
    int rewardgId;
    Date beginTime;
    Date firstBattleTime;
    Date endTime;
    int gameServerLimit;
    
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
    
    public int getGameServerLimit() {
        return this.gameServerLimit;
    }
    
    public void setGameServerLimit(final int gameServerLimit) {
        this.gameServerLimit = gameServerLimit;
    }
}
