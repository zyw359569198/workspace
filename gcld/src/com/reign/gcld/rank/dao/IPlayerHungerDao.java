package com.reign.gcld.rank.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.rank.domain.*;
import java.util.*;

public interface IPlayerHungerDao extends IBaseDao<PlayerHunger>
{
    PlayerHunger read(final int p0);
    
    PlayerHunger readForUpdate(final int p0);
    
    List<PlayerHunger> getModels();
    
    int getModelSize();
    
    int create(final PlayerHunger p0);
    
    int deleteById(final int p0);
    
    int batchUpdate(final List<PlayerHunger> p0);
    
    int batchCreate(final List<PlayerHunger> p0);
}
