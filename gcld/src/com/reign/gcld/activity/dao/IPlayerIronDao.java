package com.reign.gcld.activity.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.activity.domain.*;
import java.util.*;

public interface IPlayerIronDao extends IBaseDao<PlayerIron>
{
    PlayerIron read(final int p0);
    
    PlayerIron readForUpdate(final int p0);
    
    List<PlayerIron> getModels();
    
    int getModelSize();
    
    int create(final PlayerIron p0);
    
    int deleteById(final int p0);
    
    List<PlayerIron> getReceivedList();
    
    int clearAll();
    
    int getIron(final int p0);
    
    int addIron(final int p0, final int p1);
    
    int updateReward(final int p0, final int p1);
    
    int useIron(final int p0);
}
