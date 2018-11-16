package com.reign.gcld.kfwd.common.responsehandler;

import com.reign.kf.comm.protocol.*;
import com.reign.kf.comm.entity.match.*;
import com.reign.kf.comm.param.match.*;
import com.reign.gcld.kfwd.common.*;

public class QueryMatchReportHandler implements RequestHandler
{
    private Match match;
    
    public QueryMatchReportHandler(final Match match) {
        this.match = match;
    }
    
    @Override
	public void handle(final Request request, final Response response) {
        if (this.match.getState() == 7) {
            return;
        }
        final MatchReportEntity matchReportEntity = (MatchReportEntity)response.getMessage();
        if (matchReportEntity == null) {
            final QueryMatchReportParam queryMatchReportParam = (QueryMatchReportParam)request.getMessage();
            final MatchFight matchFight = this.match.getMatchFightMap().get(queryMatchReportParam.getSession());
            if (matchFight != null) {
                this.match.sendQueryMatchReportRequest(matchFight);
            }
        }
        else {
            this.match.updateMatchReport(matchReportEntity);
        }
    }
}
