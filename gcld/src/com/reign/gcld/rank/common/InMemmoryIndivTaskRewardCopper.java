package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRewardCopper extends InMemmoryIndivTaskReward implements Cloneable
{
    public InMemmoryIndivTaskRewardCopper(final String[] rewards) {
        super(rewards);
        this.rewardType = 1;
    }
    
    @Override
    protected InMemmoryIndivTaskRewardCopper clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRewardCopper)super.clone();
    }
}
