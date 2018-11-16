package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IBarbarainPhantomDao extends IBaseDao<BarbarainPhantom>
{
    BarbarainPhantom read(final int p0);
    
    BarbarainPhantom readForUpdate(final int p0);
    
    List<BarbarainPhantom> getModels();
    
    int getModelSize();
    
    int create(final BarbarainPhantom p0);
    
    int deleteById(final int p0);
    
    List<BarbarainPhantom> getBarPhantomByLocationId(final int p0);
    
    int updateHpTacticVal(final int p0, final int p1, final int p2);
    
    int updateState(final int p0, final int p1);
    
    int resetStateByLocationAndState(final int p0, final int p1);
    
    int resetStateAll();
    
    int callProcedureBatchInsert(final int p0, final int p1, final int p2, final int p3, final String p4, final String p5, final int p6, final String p7);
    
    int batchCreate(final List<BarbarainPhantom> p0);
    
    int getMaxVid();
    
    List<BarbarainPhantom> getBarPhantomByForceId(final int p0);
    
    int removeAllInThisCity(final Integer p0);
}
