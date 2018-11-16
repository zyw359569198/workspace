package com.reign.gcld.nation.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.nation.domain.*;
import java.util.*;

public interface IPlayerTryRankDao extends IBaseDao<PlayerTryRank>
{
    PlayerTryRank read(final int p0);
    
    PlayerTryRank readForUpdate(final int p0);
    
    List<PlayerTryRank> getModels();
    
    int getModelSize();
    
    int create(final PlayerTryRank p0);
    
    int deleteById(final int p0);
    
    List<PlayerTryRank> getRankList();
    
    int addKillNum(final int p0, final int p1);
    
    int received(final int p0);
    
    int clear(final int p0);
    
    boolean hasReward(final int p0);
}
