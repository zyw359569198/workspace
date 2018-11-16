package com.reign.gcld.treasure.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.treasure.domain.*;
import java.util.*;

public interface IPlayerTreasureDao extends IBaseDao<PlayerTreasure>
{
    PlayerTreasure read(final int p0);
    
    PlayerTreasure readForUpdate(final int p0);
    
    List<PlayerTreasure> getModels();
    
    int getModelSize();
    
    int create(final PlayerTreasure p0);
    
    int deleteById(final int p0);
    
    List<PlayerTreasure> getPlayerTreasures(final int p0);
    
    PlayerTreasure getPlayerTreasure(final int p0, final int p1);
    
    int getTreasureCount(final int p0);
}
