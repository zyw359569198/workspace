package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRewardQuenching extends InMemmoryIndivTaskReward implements Cloneable
{
    public InMemmoryIndivTaskRewardQuenching(final String[] rewards) {
        super(rewards);
        this.rewardType = 12;
    }
    
    @Override
    protected InMemmoryIndivTaskRewardQuenching clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRewardQuenching)super.clone();
    }
}
