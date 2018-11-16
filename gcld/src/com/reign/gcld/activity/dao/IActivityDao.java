package com.reign.gcld.activity.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.activity.domain.*;
import java.util.*;

public interface IActivityDao extends IBaseDao<Activity>
{
    Activity read(final int p0);
    
    Activity readForUpdate(final int p0);
    
    List<Activity> getModels();
    
    int getModelSize();
    
    int create(final Activity p0);
    
    int deleteById(final int p0);
    
    int updateInfo(final int p0, final Date p1, final Date p2, final String p3);
    
    Map<Integer, Activity> getActivityMap();
}
