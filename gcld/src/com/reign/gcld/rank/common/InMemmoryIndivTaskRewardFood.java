package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRewardFood extends InMemmoryIndivTaskReward implements Cloneable
{
    public InMemmoryIndivTaskRewardFood(final String[] rewards) {
        super(rewards);
        this.rewardType = 3;
    }
    
    @Override
    protected InMemmoryIndivTaskRewardFood clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRewardFood)super.clone();
    }
}
