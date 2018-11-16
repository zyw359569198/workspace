package com.reign.gcld.dinner.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.dinner.domain.*;
import java.util.*;

public interface IPlayerDinnerDao extends IBaseDao<PlayerDinner>
{
    PlayerDinner read(final int p0);
    
    PlayerDinner readForUpdate(final int p0);
    
    List<PlayerDinner> getModels();
    
    int getModelSize();
    
    int create(final PlayerDinner p0);
    
    int deleteById(final int p0);
    
    void addDinnerNum(final int p0);
    
    int consumeDinnerNum(final int p0);
    
    void rewardDinnerNum(final int p0, final int p1);
    
    int getDinnerNum(final int p0);
}
