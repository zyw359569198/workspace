package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IPlayerBatRankDao extends IBaseDao<PlayerBatRank>
{
    PlayerBatRank read(final int p0);
    
    PlayerBatRank readForUpdate(final int p0);
    
    List<PlayerBatRank> getModels();
    
    int getModelSize();
    
    int create(final PlayerBatRank p0);
    
    int deleteById(final int p0);
    
    int resetReward();
    
    int resetOnePlayerReward(final int p0);
    
    int updateRewardAndRank(final int p0, final String p1, final int p2);
    
    int updateLastRankTime(final int p0, final Date p1);
    
    int updateRankScore(final int p0, final int p1);
    
    int updateRankBatNumAndBuyTimes(final int p0, final int p1, final int p2);
    
    int resetBuyNumTimes();
    
    int addRankBatNumPerTwoHours(final int p0);
    
    int setLastRankTimeAsNow();
    
    int updateRankBatNum(final int p0, final int p1);
    
    int updateRankScoreBoundedByMax(final int p0, final int p1, final int p2);
}
