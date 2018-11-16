package com.reign.kfgz.dto.response;

import com.reign.kfgz.constants.*;
import org.codehaus.jackson.annotate.*;

@JsonAutoDetect
public class KfgzRuleInfoRes
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
    
    @JsonIgnore
    public long getBattleMSeconds() {
        return KfgzCommConstants.getBattleTimeByRuleBattleTime(this.battleTime);
    }
}
