package com.reign.gcld.battle.reward;

import com.reign.gcld.common.*;

public interface IReward
{
    RewardInfo rewardPlayer(final IDataGetter p0, final int p1, final String p2, final Object p3);
    
    RewardInfo getReward(final IDataGetter p0, final int p1, final Object p2);
    
    RewardInfo canReward(final IDataGetter p0, final int p1, final Object p2);
}
