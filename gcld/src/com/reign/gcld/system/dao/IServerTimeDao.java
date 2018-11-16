package com.reign.gcld.system.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.system.domain.*;
import java.util.*;

public interface IServerTimeDao extends IBaseDao<ServerTime>
{
    ServerTime read(final int p0);
    
    ServerTime readForUpdate(final int p0);
    
    List<ServerTime> getModels();
    
    int getModelSize();
    
    int create(final ServerTime p0);
    
    int deleteById(final int p0);
    
    int updateEndTime(final int p0, final Date p1);
    
    ServerTime getLastServerTime();
}
