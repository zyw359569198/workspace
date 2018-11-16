package com.reign.gcld.market.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.market.domain.*;
import java.util.*;

public interface IPlayerMarketDao extends IBaseDao<PlayerMarket>
{
    PlayerMarket read(final int p0);
    
    PlayerMarket readForUpdate(final int p0);
    
    List<PlayerMarket> getModels();
    
    int getModelSize();
    
    int create(final PlayerMarket p0);
    
    int deleteById(final int p0);
    
    void rewardCanbuyNum(final int p0, final int p1);
    
    void updateInfo(final int p0, final String p1, final Date p2);
    
    void minuseCanbuyNum(final int p0);
    
    double getNum(final int p0);
    
    int batchAddCanbuyNum(final Map<Integer, Double> p0, final int p1, final Date p2);
    
    void addCanbuyNum(final int p0, final double p1, final int p2, final Date p3);
    
    List<Integer> getCanBuyNumList(final List<Integer> p0);
}
