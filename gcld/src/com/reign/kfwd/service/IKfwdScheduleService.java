package com.reign.kfwd.service;

import java.util.*;
import com.reign.kfwd.domain.*;
import com.reign.kf.match.operationresult.*;
import com.reign.util.*;
import com.reign.framework.json.*;
import com.reign.kf.comm.entity.kfwd.request.*;
import com.reign.kf.comm.entity.kfwd.response.*;

public interface IKfwdScheduleService
{
    void intiSeasonInfo(final KfwdSeasonInfo p0);
    
    boolean beginMatch(final int p0);
    
    boolean hasScheduledMatch(final int p0);
    
    void scheduleMatch(final KfwdMatchScheduleInfo p0);
    
    KfwdSignResult processNationRegist(final String p0, final KfwdSignInfoParam p1, final boolean p2);
    
    void scheduleMatchRound(final int p0, final int p1, final int p2);
    
    List<KfwdRuntimeMatch> createNewRoundRunTimeMatch(final int p0, final int p1, final int p2);
    
    void buildNewMatchAndRun(final KfwdRuntimeMatch p0);
    
    void newNextRoundMatch(final int p0);
    
    void runNextRoundMatch(final int p0, final int p1, final int p2);
    
    KfwdRuntimeMatch buildNewMatch(final KfwdRuntimeMatch p0);
    
    void buildIniNewMatch(final KfwdRuntimeMatch p0);
    
    boolean processInspire(final KfwdRTInspire p0);
    
    KfwdRTMatchInfo getRTMatchInfo(final KfwdPlayerKey p0);
    
    KfwdRTDisPlayInfo getRTDisPlayerInfo(final KfwdPlayerKey p0);
    
    KfwdRankingListInfo getRTRankingList(final KfwdRTRankingListKey p0);
    
    KfwdState getWdState();
    
    void doFinishMatch(final KfwdRuntimeMatch p0);
    
    OperateResult playerLogin(final int p0, final String p1);
    
    Tuple<byte[], State> getPlayerMatchInfo(final int p0);
    
    KfwdDoubleRewardResult doDoubleReward(final KfwdDoubleRewardKey p0);
    
    KfwdTicketResultInfo doGetTicketRewardInfo(final KfwdPlayerKey p0);
    
    byte[] getBattleIniInfo(final int p0);
    
    byte[] getKfwdRankingInfo(final int p0);
    
    byte[] chooseStrategyOrTactic(final int p0, final int p1, final int p2, final int p3);
    
    byte[] getKfwdDayReward(final int p0, final int p1);
}
