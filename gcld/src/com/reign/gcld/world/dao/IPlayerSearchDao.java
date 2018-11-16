package com.reign.gcld.world.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.world.domain.*;
import java.util.*;

public interface IPlayerSearchDao extends IBaseDao<PlayerSearch>
{
    PlayerSearch read(final int p0);
    
    PlayerSearch readForUpdate(final int p0);
    
    List<PlayerSearch> getModels();
    
    int getModelSize();
    
    int create(final PlayerSearch p0);
    
    int deleteById(final int p0);
    
    void addSearchNum(final int p0, final int p1, final int p2, final Date p3);
    
    void addSearchBuyNum(final int p0, final int p1);
    
    void updateCanSearchInfo(final int p0, final String p1);
    
    void resetBuyNum(final int p0);
    
    void updateCurrSearchInfoAndUse(final int p0, final String p1);
    
    void rewardSearchNum(final int p0, final int p1);
    
    int getSearchNum(final int p0);
}
