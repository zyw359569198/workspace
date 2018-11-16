package com.reign.gcld.slave.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.slave.domain.*;
import java.util.*;

public interface ISlaveholderDao extends IBaseDao<Slaveholder>
{
    Slaveholder read(final int p0);
    
    Slaveholder readForUpdate(final int p0);
    
    List<Slaveholder> getModels();
    
    int getModelSize();
    
    int create(final Slaveholder p0);
    
    int deleteById(final int p0);
    
    int updateLimbo(final int p0, final int p1);
    
    int clearSlaveDayNum();
    
    int updateLashLv(final int p0, final int p1);
    
    int updateGrabNum(final int p0);
    
    int addAutoLashExp(final int p0, final int p1);
    
    int addPoint(final int p0);
    
    int updateExpireTime(final int p0, final Date p1);
    
    int updateTrailGold(final int p0, final int p1);
    
    int resetExpireTimeAndTrailGold(final int p0);
    
    int addExpireTimeAndTrailGold(final int p0, final Date p1, final int p2);
}
