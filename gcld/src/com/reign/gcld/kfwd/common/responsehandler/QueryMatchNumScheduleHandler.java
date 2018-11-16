package com.reign.gcld.kfwd.common.responsehandler;

import com.reign.kf.comm.protocol.*;
import com.reign.kf.comm.entity.match.*;
import com.reign.kf.comm.param.match.*;
import com.reign.gcld.kfwd.common.*;

public class QueryMatchNumScheduleHandler implements RequestHandler
{
    private Match match;
    
    public QueryMatchNumScheduleHandler(final Match match) {
        this.match = match;
    }
    
    @Override
	public void handle(final Request request, final Response response) {
        if (this.match.getState() == 7) {
            return;
        }
        final MatchScheduleEntity matchScheduleEntity = (MatchScheduleEntity)response.getMessage();
        if (matchScheduleEntity == null) {
            final QueryMatchNumScheduleParam queryMatchNumScheduleParam = (QueryMatchNumScheduleParam)request.getMessage();
            final MatchFight matchFight = this.match.getMatchFightMap().get(queryMatchNumScheduleParam.getSession());
            if (matchFight != null) {
                this.match.sendQueryMatchNumScheduleRequest(matchFight);
            }
        }
        else {
            this.match.setMatchNumSchedule(matchScheduleEntity);
        }
    }
}
