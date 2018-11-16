package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IBarbarainExpeditionArmyDao extends IBaseDao<BarbarainExpeditionArmy>
{
    BarbarainExpeditionArmy read(final int p0);
    
    BarbarainExpeditionArmy readForUpdate(final int p0);
    
    List<BarbarainExpeditionArmy> getModels();
    
    int getModelSize();
    
    int create(final BarbarainExpeditionArmy p0);
    
    int deleteById(final int p0);
    
    int batchCreate(final List<BarbarainExpeditionArmy> p0);
    
    int updateLocationAndState(final Integer p0, final Integer p1, final int p2);
    
    List<BarbarainExpeditionArmy> getBarEAsByLocationId(final Integer p0);
    
    int updateState(final Integer p0, final int p1);
    
    int updateHpAndTacticVal(final int p0, final int p1, final int p2);
    
    int resetStateByLocationAndState(final int p0, final int p1);
    
    int resetAllState();
    
    int removeAllInThisCity(final Integer p0);
}
