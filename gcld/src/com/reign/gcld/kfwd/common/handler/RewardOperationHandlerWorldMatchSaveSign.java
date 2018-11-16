package com.reign.gcld.kfwd.common.handler;

import com.reign.gcld.kfwd.common.*;
import com.reign.gcld.common.*;
import com.reign.gcld.kfwd.domain.*;

public class RewardOperationHandlerWorldMatchSaveSign implements IRewardOperationHandler
{
    private MatchAttendee matchAttendee;
    
    public RewardOperationHandlerWorldMatchSaveSign(final MatchAttendee matchAttendee) {
        this.matchAttendee = matchAttendee;
    }
    
    @Override
    public void handle(final IDataGetter dataGetter) {
        KfwdMatchSign worldMatchSign = dataGetter.getKfwdMatchSignDao().getWorldMatchSign(this.matchAttendee.getMatchTag(), this.matchAttendee.getPlayerId());
        if (worldMatchSign == null) {
            worldMatchSign = new KfwdMatchSign(this.matchAttendee);
            dataGetter.getKfwdMatchSignDao().create(worldMatchSign);
        }
        else {
            worldMatchSign.assign(this.matchAttendee);
            dataGetter.getKfwdMatchSignDao().update(worldMatchSign);
        }
    }
}
