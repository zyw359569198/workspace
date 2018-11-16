package com.reign.gcld.kfwd.common.responsehandler;

import com.reign.gcld.kfwd.common.*;
import com.reign.kf.comm.protocol.*;
import java.util.*;
import com.reign.gcld.kfwd.service.*;
import com.reign.gcld.kfwd.common.runner.*;
import java.util.concurrent.*;
import com.reign.kf.comm.entity.match.*;

public class QueryMatchScheduleHandler implements ResponseHandler
{
    private Match match;
    
    public QueryMatchScheduleHandler(final Match match) {
        this.match = match;
    }
    
    @Override
	public void handle(final Response response) {
        if (this.match.getState() == 7) {
            return;
        }
        final List<MatchScheduleEntity> matchScheduleEntityList = (List<MatchScheduleEntity>)response.getMessage();
        if (matchScheduleEntityList == null || matchScheduleEntityList.size() == 0) {
            MatchService.getExecutor().schedule(new ResendQueryMatchScheduleRequestRunner(this.match), 5L, TimeUnit.SECONDS);
        }
        else {
            this.match.setMatchSchedule(matchScheduleEntityList);
        }
    }
}
