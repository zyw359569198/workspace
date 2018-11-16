package com.reign.gcld.building.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.building.domain.*;
import java.util.*;

public interface IBluePrintDao extends IBaseDao<BluePrint>
{
    BluePrint read(final int p0);
    
    BluePrint readForUpdate(final int p0);
    
    List<BluePrint> getModels();
    
    int getModelSize();
    
    int create(final BluePrint p0);
    
    int deleteById(final int p0);
    
    int getCount(final int p0, final int p1);
    
    BluePrint getByPlayerIdAndIndex(final int p0, final int p1);
    
    int updateState(final int p0, final int p1);
    
    int updateStateByPlayerIdAndIndex(final int p0, final int p1, final int p2);
    
    int cons(final int p0, final int p1, final Date p2, final int p3);
    
    List<Integer> getBluePrintIndexList(final int p0);
}
