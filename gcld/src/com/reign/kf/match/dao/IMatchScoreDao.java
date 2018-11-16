package com.reign.kf.match.dao;

import com.reign.framework.jdbc.orm.*;
import com.reign.kf.match.domain.*;
import java.util.*;

public interface IMatchScoreDao extends IBaseDao<MatchScore, Integer>
{
    List<MatchScore> getRankInfo(final String p0, final int p1);
    
    List<MatchScore> getScoreRankInfo(final String p0, final int p1);
    
    List<MatchScore> getMatchScore(final String p0);
    
    List<MatchScore> getMatchResult(final String p0, final String p1);
    
    MatchScore getLastScore(final String p0, final long p1);
}
