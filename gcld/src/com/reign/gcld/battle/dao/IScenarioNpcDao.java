package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IScenarioNpcDao extends IBaseDao<ScenarioNpc>
{
    ScenarioNpc read(final int p0);
    
    ScenarioNpc readForUpdate(final int p0);
    
    List<ScenarioNpc> getModels();
    
    int getModelSize();
    
    int create(final ScenarioNpc p0);
    
    int deleteById(final int p0);
    
    int updateLocationAndState(final Integer p0, final int p1, final int p2);
    
    int updateHpAndTacticVal(final Integer p0, final int p1, final int p2);
    
    int resetAllState();
    
    int resetStateByLocationAndState(final int p0, final int p1);
    
    List<ScenarioNpc> getByPlayerIdLocationId(final int p0, final int p1);
    
    int updateState(final Integer p0, final int p1);
    
    int batchCreate(final List<ScenarioNpc> p0);
    
    int getMaxVid();
    
    int deleteAllInThisCity(final int p0, final int p1);
    
    int deleteAll(final int p0);
}
