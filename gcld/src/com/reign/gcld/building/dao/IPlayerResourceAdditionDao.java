package com.reign.gcld.building.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.building.domain.*;
import java.util.*;

public interface IPlayerResourceAdditionDao extends IBaseDao<PlayerResourceAddition>
{
    PlayerResourceAddition read(final int p0);
    
    PlayerResourceAddition readForUpdate(final int p0);
    
    List<PlayerResourceAddition> getModels();
    
    int getModelSize();
    
    int create(final PlayerResourceAddition p0);
    
    int deleteById(final int p0);
    
    PlayerResourceAddition getByPlayerIdAndType(final int p0, final int p1);
    
    void update(final int p0, final Date p1, final int p2, final int p3, final int p4);
    
    List<PlayerResourceAddition> getListByPlayerId(final int p0);
    
    List<PlayerResourceAddition> getListByTime(final Date p0);
}
