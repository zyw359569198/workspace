package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRewardExp extends InMemmoryIndivTaskReward implements Cloneable
{
    public InMemmoryIndivTaskRewardExp(final String[] rewards) {
        super(rewards);
        this.rewardType = 10;
    }
    
    @Override
    protected InMemmoryIndivTaskRewardExp clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRewardExp)super.clone();
    }
}
