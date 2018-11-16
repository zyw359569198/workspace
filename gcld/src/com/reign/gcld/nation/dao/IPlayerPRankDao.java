package com.reign.gcld.nation.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.nation.domain.*;
import java.util.*;

public interface IPlayerPRankDao extends IBaseDao<PlayerPRank>
{
    PlayerPRank read(final int p0);
    
    PlayerPRank readForUpdate(final int p0);
    
    List<PlayerPRank> getModels();
    
    int getModelSize();
    
    int create(final PlayerPRank p0);
    
    int deleteById(final int p0);
    
    List<PlayerPRank> getRankList();
    
    int addKillNum(final int p0, final int p1);
    
    int received(final int p0);
    
    int clear(final int p0);
    
    boolean hasReward(final int p0);
}
