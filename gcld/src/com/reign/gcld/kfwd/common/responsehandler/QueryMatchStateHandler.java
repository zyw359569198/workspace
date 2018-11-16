package com.reign.gcld.kfwd.common.responsehandler;

import com.reign.kf.comm.protocol.*;
import com.reign.kf.comm.entity.match.*;
import com.reign.gcld.kfwd.common.*;
import com.reign.gcld.kfwd.service.*;
import com.reign.gcld.kfwd.common.runner.*;
import java.util.concurrent.*;

public class QueryMatchStateHandler implements ResponseHandler
{
    @Override
	public void handle(final Response response) {
        final MatchStateEntity matchStateEntity = (MatchStateEntity)response.getMessage();
        final Match match = MatchManager.getInstance().getMatch(matchStateEntity.getMatchTag());
        if (match != null) {
            if (match.getState() == 7) {
                return;
            }
            switch (matchStateEntity.getState()) {
                case 0: {
                    this.handleStateUnsign(match, matchStateEntity);
                    break;
                }
                case 1: {
                    this.handleStateSigning(match, matchStateEntity);
                    break;
                }
                case 2: {
                    this.handleStateEndSign(match, matchStateEntity);
                    break;
                }
                case 3: {
                    this.handleStateEndAssign(match, matchStateEntity);
                    break;
                }
                case 4: {
                    this.handleStateEndAssign(match, matchStateEntity);
                    break;
                }
            }
        }
    }
    
    public void handleStateUnsign(final Match match, final MatchStateEntity matchStateEntity) {
        match.startPrepare(matchStateEntity.getCd());
        MatchService.getExecutor().schedule(new SendQueryMatchStateRequestRunner(match), matchStateEntity.getCd(), TimeUnit.MILLISECONDS);
    }
    
    public void handleStateSigning(final Match match, final MatchStateEntity matchStateEntity) {
        match.startSignup(matchStateEntity.getCd());
        MatchService.getExecutor().schedule(new SendQueryMatchStateRequestRunner(match), matchStateEntity.getCd(), TimeUnit.MILLISECONDS);
    }
    
    public void handleStateEndSign(final Match match, final MatchStateEntity matchStateEntity) {
        match.endSignup(matchStateEntity.getCd());
        MatchService.getExecutor().schedule(new SendQueryMatchStateRequestRunner(match), matchStateEntity.getCd(), TimeUnit.MILLISECONDS);
    }
    
    public void handleStateEndAssign(final Match match, final MatchStateEntity matchStateEntity) {
        match.endArrang();
    }
}
