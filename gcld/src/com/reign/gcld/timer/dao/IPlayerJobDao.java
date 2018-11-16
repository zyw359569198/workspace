package com.reign.gcld.timer.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.timer.domain.*;
import java.util.*;

public interface IPlayerJobDao extends IBaseDao<PlayerJob>
{
    PlayerJob read(final int p0);
    
    PlayerJob readForUpdate(final int p0);
    
    List<PlayerJob> getModels();
    
    int getModelSize();
    
    int create(final PlayerJob p0);
    
    int deleteById(final int p0);
    
    int updateJobState(final int p0, final int p1);
    
    List<PlayerJob> getJobListByState(final int p0);
    
    int getMaxJobId();
    
    int updateJobExeTime(final int p0, final long p1);
}
