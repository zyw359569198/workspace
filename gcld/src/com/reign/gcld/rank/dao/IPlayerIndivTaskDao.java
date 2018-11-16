package com.reign.gcld.rank.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.rank.domain.*;
import java.util.*;

public interface IPlayerIndivTaskDao extends IBaseDao<PlayerIndivTask>
{
    PlayerIndivTask read(final int p0);
    
    PlayerIndivTask readForUpdate(final int p0);
    
    List<PlayerIndivTask> getModels();
    
    int getModelSize();
    
    int create(final PlayerIndivTask p0);
    
    int deleteById(final int p0);
    
    int updateTaskInfo(final String p0, final int p1);
    
    void deleteAll();
}
