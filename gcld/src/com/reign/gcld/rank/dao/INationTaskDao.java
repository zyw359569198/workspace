package com.reign.gcld.rank.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.rank.domain.*;
import java.util.*;

public interface INationTaskDao extends IBaseDao<NationTask>
{
    NationTask read(final int p0);
    
    NationTask readForUpdate(final int p0);
    
    List<NationTask> getModels();
    
    int getModelSize();
    
    int create(final NationTask p0);
    
    int deleteById(final int p0);
    
    void updateIsWin(final int p0, final int p1);
    
    NationTask getByForceAndTarget(final int p0, final int p1);
    
    void resetTaskIsWin(final Integer p0, final int p1);
    
    void deleteAllTasks();
    
    NationTask getByForce(final int p0);
    
    List<NationTask> getListByForce(final int p0);
    
    void updateTarget(final int p0, final Integer p1);
    
    void updateIsWinAndFinishTime(final int p0, final int p1, final long p2);
    
    void updateAttType(final int p0, final int p1);
    
    void updateManZuSaoDangTaskRelateInfo(final int p0, final String p1);
    
    void updateTaskRelativeInfo(final String p0);
    
    void resetTaskIsWinByForceId(final int p0);
    
    int updateIsWinAndEndTime(final Integer p0, final int p1, final Date p2);
    
    int resetIsWin(final int p0, final int p1);
}
