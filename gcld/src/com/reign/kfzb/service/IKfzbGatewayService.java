package com.reign.kfzb.service;

import com.reign.kf.comm.entity.*;
import com.reign.kfzb.domain.*;
import com.reign.kfzb.dto.response.*;
import com.reign.kfzb.dto.request.*;

public interface IKfzbGatewayService
{
    KfzbSeasonInfo handleSeasonInfo(final GameServerEntity p0);
    
    void processKfgzSeasonInfo();
    
    void clearFinshedSeasonInfo();
    
    void scheduleNewSeason(final KfzbSeasonInfoD p0);
    
    KfzbRewardInfo handleRewardInfo();
    
    KfzbPlayerLimitInfo handlePlayerLimitInfo();
    
    Integer handleWinnerInfo(final KfzbWinnerInfo p0);
    
    KfzbWinnerInfo handleGetTop16PlayerInfo();
}
