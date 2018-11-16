package com.reign.gcld.activity.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.activity.domain.*;
import java.util.*;

public interface IActivityNpcDao extends IBaseDao<ActivityNpc>
{
    ActivityNpc read(final int p0);
    
    ActivityNpc readForUpdate(final int p0);
    
    List<ActivityNpc> getModels();
    
    int getModelSize();
    
    int create(final ActivityNpc p0);
    
    int deleteById(final int p0);
    
    int batchCreate(final List<ActivityNpc> p0);
    
    int updateLocationAndState(final Integer p0, final Integer p1, final int p2);
    
    List<ActivityNpc> getActivityNpcsByLocationId(final Integer p0);
    
    int updateState(final Integer p0, final int p1);
    
    int updateHpAndTacticVal(final int p0, final int p1, final int p2);
    
    int resetStateByLocationAndState(final int p0, final int p1);
    
    int resetAllState();
    
    int getMaxVid();
    
    int deleteAll();
    
    List<ActivityNpc> getActivityNpcsByLocationIdAndForceIdExclude(final Integer p0, final Integer p1, final Integer p2);
}
