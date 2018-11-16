package com.reign.gcld.kfwd.common.responsehandler;

import com.reign.gcld.kfwd.common.*;
import com.reign.kf.comm.protocol.*;
import java.util.*;
import com.reign.gcld.kfwd.service.*;
import com.reign.gcld.kfwd.common.runner.*;
import java.util.concurrent.*;
import com.reign.kf.comm.entity.match.*;

public class QueryMatchResultHandler implements ResponseHandler
{
    private Match match;
    
    public QueryMatchResultHandler(final Match match) {
        this.match = match;
    }
    
    @Override
	public void handle(final Response response) {
        if (this.match.getState() == 7) {
            return;
        }
        final List<MatchResultEntity> matchResultEntityList = (List<MatchResultEntity>)response.getMessage();
        if (matchResultEntityList == null || (matchResultEntityList.size() == 0 && this.match.getSignCount() != 0)) {
            MatchService.getExecutor().schedule(new SendQueryMatchResultRequestRunner(this.match), 5L, TimeUnit.SECONDS);
        }
        else {
            this.match.sendQueryRankList(this.match.getTurn());
            this.match.handleMatchResult(matchResultEntityList);
        }
    }
}
