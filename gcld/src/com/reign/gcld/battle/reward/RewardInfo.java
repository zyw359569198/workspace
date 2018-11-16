package com.reign.gcld.battle.reward;

public class RewardInfo
{
    public static final int STATE_SUCC = 1;
    public static final int STATE_FAIL = 0;
    private int reward;
    private boolean canReward;
    private int addValue;
    private int type;
    
    public RewardInfo(final int reward) {
        this.reward = reward;
    }
    
    public RewardInfo(final boolean canReward) {
        this.canReward = canReward;
    }
    
    public int getReward() {
        return this.reward;
    }
    
    public void setReward(final int reward) {
        this.reward = reward;
    }
    
    public boolean isCanReward() {
        return this.canReward;
    }
    
    public void setCanReward(final boolean canReward) {
        this.canReward = canReward;
    }
    
    public int getAddValue() {
        return this.addValue;
    }
    
    public void setAddValue(final int addValue) {
        this.addValue = addValue;
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
}
