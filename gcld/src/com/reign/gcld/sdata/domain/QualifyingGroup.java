package com.reign.gcld.sdata.domain;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.common.*;

public class QualifyingGroup implements IModel
{
    private static final long serialVersionUID = 1L;
    private Integer group;
    private String rewardQualifying;
    private BattleDropAnd battleRewardQualifying;
    private String rewardWin;
    private BattleDropAnd battleRewardWin;
    private String rewardLose;
    private BattleDropAnd battleRewardLose;
    private Integer creditWin;
    private Integer creditLose;
    
    public Integer getGroup() {
        return this.group;
    }
    
    public void setGroup(final Integer group) {
        this.group = group;
    }
    
    public String getRewardQualifying() {
        return this.rewardQualifying;
    }
    
    public void setRewardQualifying(final String rewardQualifying) {
        this.rewardQualifying = rewardQualifying;
    }
    
    public String getRewardWin() {
        return this.rewardWin;
    }
    
    public void setRewardWin(final String rewardWin) {
        this.rewardWin = rewardWin;
    }
    
    public String getRewardLose() {
        return this.rewardLose;
    }
    
    public void setRewardLose(final String rewardLose) {
        this.rewardLose = rewardLose;
    }
    
    public Integer getCreditWin() {
        return this.creditWin;
    }
    
    public void setCreditWin(final Integer creditWin) {
        this.creditWin = creditWin;
    }
    
    public Integer getCreditLose() {
        return this.creditLose;
    }
    
    public void setCreditLose(final Integer creditLose) {
        this.creditLose = creditLose;
    }
    
    public BattleDropAnd getBattleRewardQualifying() {
        return this.battleRewardQualifying;
    }
    
    public void setBattleRewardQualifying(final BattleDropAnd battleRewardQualifying) {
        this.battleRewardQualifying = battleRewardQualifying;
    }
    
    public BattleDropAnd getBattleRewardWin() {
        return this.battleRewardWin;
    }
    
    public void setBattleRewardWin(final BattleDropAnd battleRewardWin) {
        this.battleRewardWin = battleRewardWin;
    }
    
    public BattleDropAnd getBattleRewardLose() {
        return this.battleRewardLose;
    }
    
    public void setBattleRewardLose(final BattleDropAnd battleRewardLose) {
        this.battleRewardLose = battleRewardLose;
    }
}
