package com.reign.kfgz.domain;

import com.reign.framework.hibernate.model.*;

public class KfgzRule implements IModel
{
    int ruleId;
    int battleNum;
    String battleTime;
    String battleDelayInfo;
    
    public int getRuleId() {
        return this.ruleId;
    }
    
    public void setRuleId(final int ruleId) {
        this.ruleId = ruleId;
    }
    
    public int getBattleNum() {
        return this.battleNum;
    }
    
    public void setBattleNum(final int battleNum) {
        this.battleNum = battleNum;
    }
    
    public String getBattleTime() {
        return this.battleTime;
    }
    
    public void setBattleTime(final String battleTime) {
        this.battleTime = battleTime;
    }
    
    public String getBattleDelayInfo() {
        return this.battleDelayInfo;
    }
    
    public void setBattleDelayInfo(final String battleDelayInfo) {
        this.battleDelayInfo = battleDelayInfo;
    }
}
