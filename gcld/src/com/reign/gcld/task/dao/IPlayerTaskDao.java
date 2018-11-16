package com.reign.gcld.task.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.task.domain.*;
import java.util.*;

public interface IPlayerTaskDao extends IBaseDao<PlayerTask>
{
    PlayerTask read(final int p0);
    
    PlayerTask readForUpdate(final int p0);
    
    List<PlayerTask> getModels();
    
    int getModelSize();
    
    int create(final PlayerTask p0);
    
    int deleteById(final int p0);
    
    int update(final PlayerTask p0);
    
    int addProcess(final int p0, final int p1);
    
    int clearDailyTask(final int p0);
    
    List<PlayerTask> getPlayerTasks(final int p0);
    
    PlayerTask getCurMainTask(final int p0);
    
    PlayerTask getDailyTask(final int p0, final int p1, final int p2);
    
    PlayerTask getBranchTask(final int p0, final int p1, final int p2);
    
    void resetDailyTask(final int p0);
    
    void resetMainTask(final int p0, final int p1);
    
    List<PlayerTask> getDisPlayPlayerTask(final int p0);
}
