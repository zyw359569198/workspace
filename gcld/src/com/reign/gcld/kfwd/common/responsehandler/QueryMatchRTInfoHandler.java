package com.reign.gcld.kfwd.common.responsehandler;

import com.reign.gcld.kfwd.common.*;
import com.reign.kf.comm.protocol.*;
import com.reign.kf.comm.entity.match.*;

public class QueryMatchRTInfoHandler implements ResponseHandler
{
    private Match match;
    
    public QueryMatchRTInfoHandler(final Match match) {
        this.match = match;
    }
    
    @Override
	public void handle(final Response response) {
        if (this.match.getState() == 7) {
            return;
        }
        final MatchRTInfoEntity matchRTInfoEntity = (MatchRTInfoEntity)response.getMessage();
        if (matchRTInfoEntity == null) {
            return;
        }
        this.match.updateMatchFight(matchRTInfoEntity);
    }
}
