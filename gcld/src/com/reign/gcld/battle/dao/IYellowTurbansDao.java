package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IYellowTurbansDao extends IBaseDao<YellowTurbans>
{
    YellowTurbans read(final int p0);
    
    YellowTurbans readForUpdate(final int p0);
    
    List<YellowTurbans> getModels();
    
    int getModelSize();
    
    int create(final YellowTurbans p0);
    
    int deleteById(final int p0);
    
    int batchCreate(final List<YellowTurbans> p0);
    
    List<YellowTurbans> getYellowTurbansByCityId(final int p0);
    
    int updateState(final Integer p0, final int p1);
    
    int updateHpAndTacticVal(final Integer p0, final int p1, final int p2);
    
    int deleteByCityId(final int p0);
    
    int getMaxVid();
    
    int resetStateByLocationAndState(final int p0, final int p1);
    
    int resetAllState();
}
