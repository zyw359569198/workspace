package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRewardIron extends InMemmoryIndivTaskReward implements Cloneable
{
    public InMemmoryIndivTaskRewardIron(final String[] rewards) {
        super(rewards);
        this.rewardType = 4;
    }
    
    @Override
    protected InMemmoryIndivTaskRewardIron clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRewardIron)super.clone();
    }
}
