package com.reign.gcld.activity.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.activity.domain.*;
import java.util.*;

public interface IPlayerLvExpDao extends IBaseDao<PlayerLvExp>
{
    PlayerLvExp read(final int p0);
    
    PlayerLvExp readForUpdate(final int p0);
    
    List<PlayerLvExp> getModels();
    
    int getModelSize();
    
    int create(final PlayerLvExp p0);
    
    int deleteById(final int p0);
    
    int deleteAll();
    
    int updateReward(final int p0);
    
    int initActivity();
    
    int endActivity();
}
