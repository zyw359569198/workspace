package com.reign.gcld.kfwd.common.reward;

import com.reign.gcld.player.domain.*;
import com.reign.gcld.kfwd.common.*;
import com.reign.gcld.common.*;

public class MatchReward implements IMatchReward
{
    @Override
    public String getTurnRewardMsg(final Player player) {
        return null;
    }
    
    @Override
    public byte[] getMatchRewardMsg() {
        return null;
    }
    
    @Override
    public void rewardTurn(final MatchAttendee matchAttendee, final MatchFightMember matchFightMember, final int matchPoint, final IDataGetter dataGetter) {
        matchAttendee.setInspireTimes(0);
        matchAttendee.setRewardMode(0);
        matchAttendee.addPoints(matchPoint);
        matchFightMember.setPoint(matchPoint);
    }
}
