package com.reign.kf.match.service;

import com.reign.kf.match.model.*;
import java.util.*;
import com.reign.kf.comm.param.match.*;
import com.reign.kf.comm.entity.match.*;

public interface IMatchService
{
    SignEntity sign(final SignAndSyncParam p0, final String p1);
    
    void scheduleMatch(final Match p0, final int p1);
    
    MatchResult startMatch(final Match p0, final int p1);
    
    MatchResult handleMatchException(final Match p0, final int p1);
    
    void handleMatchOver(final MatchResult p0);
    
    List<MatchScheduleEntity> getMatchSchedule(final QueryMatchScheduleParam p0, final String p1);
    
    MatchRTInfoEntity getMatchRTInfo(final QueryMatchRTInfoParam p0);
    
    MatchReportEntity getMatchReport(final QueryMatchReportParam p0);
    
    List<MatchRankEntity> getMatchTurnRank(final QueryTurnRankParam p0);
    
    MatchScheduleEntity getMatchNumSchedule(final QueryMatchNumScheduleParam p0);
    
    SignEntity sync(final SignAndSyncParam p0, final String p1);
    
    List<MatchResultEntity> getMatchResult(final QueryMatchResultParam p0, final String p1);
    
    void recover(final Match p0);
    
    InspireEntity inspire(final InspireParam p0, final String p1);
}
