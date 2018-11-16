package com.reign.gcld.kfwd.common.reward;

import com.reign.gcld.player.domain.*;
import com.reign.gcld.kfwd.common.*;
import com.reign.gcld.common.*;

public interface IMatchReward
{
    String getTurnRewardMsg(final Player p0);
    
    byte[] getMatchRewardMsg();
    
    void rewardTurn(final MatchAttendee p0, final MatchFightMember p1, final int p2, final IDataGetter p3);
}
