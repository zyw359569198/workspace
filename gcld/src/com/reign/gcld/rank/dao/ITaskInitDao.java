package com.reign.gcld.rank.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.rank.domain.*;
import java.util.*;

public interface ITaskInitDao extends IBaseDao<TaskInit>
{
    TaskInit read(final int p0);
    
    TaskInit readForUpdate(final int p0);
    
    List<TaskInit> getModels();
    
    int getModelSize();
    
    int create(final TaskInit p0);
    
    int deleteById(final int p0);
    
    void batchCreate(final List<TaskInit> p0);
    
    void deleteAlls();
    
    int updateType(final int p0, final int p1);
}
