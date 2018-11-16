package com.reign.kfzb.service;

import com.reign.kfzb.dto.*;
import java.util.*;
import com.reign.kfzb.domain.*;
import com.reign.util.*;
import com.reign.framework.json.*;
import com.reign.kfzb.dto.request.*;
import com.reign.kfzb.dto.response.*;

public interface IKfzbScheduleService
{
    void intiSeasonInfo(final KfzbSeasonInfo p0);
    
    boolean beginMatch(final int p0);
    
    boolean hasScheduledMatch(final int p0);
    
    void scheduleMatch(final KfzbSeasonInfo p0);
    
    int checkandGetWarriorData();
    
    void createMatch(final KfzbRuntimeMatch p0);
    
    void buildNewMatchAndRun(final KfzbRuntimeMatch p0);
    
    KfzbRuntimeMatch buildNewMatch(final KfzbRuntimeMatch p0);
    
    void runNextLayerMatch(final int p0, final int p1);
    
    KfzbState getWdState();
    
    KfzbRTMatchInfo getRTMatchInfo(final KfzbPlayerKey p0, final String p1);
    
    void processRTSupport(final KfzbRTSupport p0);
    
    void saveMatchSupport(final KfzbMatchSupportInfo p0);
    
    KfzbMatchSupport getRTSupportInfo(final KfzbMatchKey p0);
    
    KfzbMatchInfo getMatchInfo(final KfzbMatchKey p0);
    
    void setPlayerLastSynTime(final int p0, final long p1);
    
    void saveWarriorPos(final List<KfzbBattleWarrior> p0);
    
    void doFinishMatch(final KfzbRuntimeMatch p0);
    
    void updateRuntimeResultMaxLayerId(final int p0, final int p1);
    
    KfzbPhase1RewardInfoList getKfzbRewardListInfo(final List<Integer> p0);
    
    byte[] getBattleIniInfo(final int p0);
    
    byte[] chooseStrategyOrTactic(final int p0, final int p1, final int p2, final int p3);
    
    Tuple<byte[], State> getPlayerMatchInfo(final int p0);
    
    KfzbBattleReport getPhase2MatchDetail(final KfzbPhase2MatchKey p0);
    
    KfzbSignResult processSynPlayerData(final String p0, final KfzbSignInfo p1, final boolean p2);
    
    KfzbPlayerGroupInfo getPlayerGroupInfo(final List<Integer> p0);
    
    void createMatchList(final List<KfzbRuntimeMatch> p0, final int p1);
}
