package com.reign.gcld.rank.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.rank.domain.*;
import java.util.*;

public interface IPlayerRankingRewardDao extends IBaseDao<PlayerRankingReward>
{
    PlayerRankingReward read(final int p0);
    
    PlayerRankingReward readForUpdate(final int p0);
    
    List<PlayerRankingReward> getModels();
    
    int getModelSize();
    
    int create(final PlayerRankingReward p0);
    
    int deleteById(final int p0);
    
    PlayerRankingReward getByTypeAndPlayerId(final Integer p0, final int p1);
    
    void updateReward(final Integer p0, final String p1);
}
