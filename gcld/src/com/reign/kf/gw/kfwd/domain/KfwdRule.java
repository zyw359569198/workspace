package com.reign.kf.gw.kfwd.domain;

import com.reign.framework.hibernate.model.*;
import java.util.*;

public class KfwdRule implements IModel
{
    private static final long serialVersionUID = -1783436452903431093L;
    public int pk;
    public int ruleId;
    public Date serverStartTime;
    public Date serverEndTime;
    public String levelRangeList;
    public String rewardRuleGroupType;
    public int matchLimit;
    public String roundGodList;
    public int levelRangeType;
    
    public int getPk() {
        return this.pk;
    }
    
    public void setPk(final int pk) {
        this.pk = pk;
    }
    
    public int getRuleId() {
        return this.ruleId;
    }
    
    public void setRuleId(final int ruleId) {
        this.ruleId = ruleId;
    }
    
    public Date getServerStartTime() {
        return this.serverStartTime;
    }
    
    public void setServerStartTime(final Date serverStartTime) {
        this.serverStartTime = serverStartTime;
    }
    
    public Date getServerEndTime() {
        return this.serverEndTime;
    }
    
    public void setServerEndTime(final Date serverEndTime) {
        this.serverEndTime = serverEndTime;
    }
    
    public String getLevelRangeList() {
        return this.levelRangeList;
    }
    
    public void setLevelRangeList(final String levelRangeList) {
        this.levelRangeList = levelRangeList;
    }
    
    public int getMatchLimit() {
        return this.matchLimit;
    }
    
    public void setMatchLimit(final int matchLimit) {
        this.matchLimit = matchLimit;
    }
    
    public String getRewardRuleGroupType() {
        return this.rewardRuleGroupType;
    }
    
    public void setRewardRuleGroupType(final String rewardRuleGroupType) {
        this.rewardRuleGroupType = rewardRuleGroupType;
    }
    
    public String getRoundGodList() {
        return this.roundGodList;
    }
    
    public void setRoundGodList(final String roundGodList) {
        this.roundGodList = roundGodList;
    }
    
    public int getLevelRangeType() {
        return this.levelRangeType;
    }
    
    public void setLevelRangeType(final int levelRangeType) {
        this.levelRangeType = levelRangeType;
    }
}
