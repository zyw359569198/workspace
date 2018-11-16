package com.reign.kf.gw.kfwd.service;

import com.reign.kf.comm.entity.*;
import com.reign.kf.comm.entity.kfwd.response.*;

public interface IKfwdGatewayService
{
    KfwdSeasonInfo handleAstdKFwdSeasonInfo(final GameServerEntity p0);
    
    void processKfwdSeasonInfo();
    
    void scheduleNewSeason(final com.reign.kf.gw.kfwd.domain.KfwdSeasonInfo p0);
    
    KfwdSeasonInfo handleMatchKFwdSeasonInfo(final MatchServerEntity p0);
    
    KfwdMatchScheduleInfo handleMatchKFwdSeasonScheduleInfo(final MatchServerEntity p0);
    
    KfwdRewardResult handleMatchKFwdRewardRuleInfo(final MatchServerEntity p0);
    
    KfwdScheduleInfoDto handleAstdKFwdSeasonScheduleInfo(final GameServerEntity p0);
    
    KfwdRewardResult handleAstdKFwdSeasonRewardInfo(final GameServerEntity p0);
    
    void checkAndregistServer(final GameServerEntity p0);
    
    KfwdTicketMarketListInfo handleGetTicketMarketInfo();
    
    void fethInfoFromBg();
    
    KfwdRankTreasureList handleGetTreasureRewardInfo(final GameServerEntity p0);
}
