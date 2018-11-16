package com.reign.kfgz.service;

import com.reign.kfgz.domain.*;
import com.reign.kf.comm.entity.*;
import java.util.*;
import com.reign.kfgz.dto.request.*;
import com.reign.kfgz.dto.response.*;

public interface IKfgzGatewayService
{
    void processKfgzSeasonInfo();
    
    void scheduleNewSeason(final KfgzSeasonInfo p0);
    
    KfgzSeasonInfoRes handleKfgzSeasonInfo();
    
    Integer handleGetGameServerPlayerUid();
    
    void saveNewUid(final int p0);
    
    KfgzScheduleInfoList handleGamerServerKfgzScheduleInfo(final GameServerEntity p0);
    
    KfgzRuleInfoList handleKfgzRuleInfo();
    
    KfgzRewardInfoRes handleKfgzRewardInfo();
    
    KfgzScheduleInfoList getSchInfoFromMatch(final MatchServerEntity p0);
    
    KfgzScheduleInfoList handleGamerServerKfgzAllScheduleInfo(final GameServerEntity p0);
    
    KfgzBattleResultRes handleBattleResInfo(final KfgzBattleResultInfo p0);
    
    void saveNationResList(final List<KfgzNationResultReq> p0);
    
    void savePlayerRanking(final KfgzPlayerRankingInfoReq p0);
    
    KfgzAllRankRes getBattleRankInfo(final kfgzNationGzKey p0);
    
    void saveAll(final List<KfgzPlayerRankingInfoReq> p0);
    
    void saveSeasonInfo(final KfgzSeasonInfo p0);
}
