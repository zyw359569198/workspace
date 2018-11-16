package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRewardFeat extends InMemmoryIndivTaskReward implements Cloneable
{
    public InMemmoryIndivTaskRewardFeat(final String[] rewards) {
        super(rewards);
        this.rewardType = 13;
    }
    
    @Override
    protected InMemmoryIndivTaskRewardFeat clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRewardFeat)super.clone();
    }
}
