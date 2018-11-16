package com.reign.gcld.kfwd.common.responsehandler;

import com.reign.gcld.kfwd.common.*;
import com.reign.kf.comm.protocol.*;
import java.util.*;
import com.reign.kf.comm.param.match.*;
import com.reign.gcld.kfwd.service.*;
import com.reign.gcld.kfwd.common.runner.*;
import java.util.concurrent.*;
import com.reign.gcld.common.event.*;
import com.reign.kf.comm.entity.match.*;

public class QueryTurnRankHandler implements RequestHandler
{
    private Match match;
    
    public QueryTurnRankHandler(final Match match) {
        this.match = match;
    }
    
    @Override
	public void handle(final Request request, final Response response) {
        if (this.match.getState() == 7) {
            return;
        }
        final List<MatchRankEntity> matchRankEntityList = (List<MatchRankEntity>)response.getMessage();
        if (matchRankEntityList == null) {
            final QueryTurnRankParam queryTurnRankParam = (QueryTurnRankParam)request.getMessage();
            MatchService.getExecutor().schedule(new SendQueryMatchRankRequestRunner(this.match, queryTurnRankParam.getTurn()), 5L, TimeUnit.SECONDS);
        }
        else {
            this.match.handleMatchRank(matchRankEntityList);
            EventListener.fireEvent(new CommonEvent(26, -1));
        }
    }
}
