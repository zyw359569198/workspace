package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IExpeditionArmyDao extends IBaseDao<ExpeditionArmy>
{
    ExpeditionArmy read(final int p0);
    
    ExpeditionArmy readForUpdate(final int p0);
    
    List<ExpeditionArmy> getModels();
    
    int getModelSize();
    
    int create(final ExpeditionArmy p0);
    
    int deleteById(final int p0);
    
    int updateLocationAndState(final Integer p0, final int p1, final int p2);
    
    int updateHpAndTacticVal(final Integer p0, final int p1, final int p2);
    
    int resetAllState();
    
    int resetStateByLocationAndState(final int p0, final int p1);
    
    List<ExpeditionArmy> getEAsByLocationId(final int p0);
    
    int updateState(final Integer p0, final int p1);
    
    int deleteByLocationId(final int p0);
    
    int batchCreate(final List<ExpeditionArmy> p0);
}
