package com.reign.gcld.feat.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.feat.domain.*;
import java.util.*;

public interface IPlayerFeatRankDao extends IBaseDao<PlayerFeatRank>
{
    PlayerFeatRank read(final int p0);
    
    PlayerFeatRank readForUpdate(final int p0);
    
    List<PlayerFeatRank> getModels();
    
    int getModelSize();
    
    int create(final PlayerFeatRank p0);
    
    int deleteById(final int p0);
    
    List<PlayerFeatRank> getRankList();
    
    int getTotalFeat(final int p0);
    
    int addOccupyAndFeat(final int p0, final int p1);
    
    int addAssistAndFeat(final int p0, final int p1);
    
    int addCheerAndFeat(final int p0, final int p1);
    
    int addKillNumAndFeat(final int p0, final int p1, final int p2);
    
    List<PlayerFeatRank> getRewardRankList();
    
    int clearAll();
    
    int clearLastRankReced();
    
    int updateLastRank(final int p0, final int p1);
    
    int received(final int p0);
    
    int addTotalFeat(final int p0, final int p1);
}
