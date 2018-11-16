package com.reign.gcld.kfwd.service;

import com.reign.kf.comm.entity.kfwd.response.*;

public interface IKfwdSeasonService
{
    boolean hasScheduledSeason(final int p0);
    
    void createNewSeason(final KfwdSeasonInfo p0, final KfwdScheduleInfoDto p1, final KfwdRewardResult p2, final KfwdTicketMarketListInfo p3, final KfwdRankTreasureList p4);
}
