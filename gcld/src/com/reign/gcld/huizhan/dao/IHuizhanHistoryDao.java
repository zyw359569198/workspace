package com.reign.gcld.huizhan.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.huizhan.domain.*;
import java.util.*;

public interface IHuizhanHistoryDao extends IBaseDao<HuizhanHistory>
{
    HuizhanHistory read(final int p0);
    
    HuizhanHistory readForUpdate(final int p0);
    
    List<HuizhanHistory> getModels();
    
    int getModelSize();
    
    int create(final HuizhanHistory p0);
    
    int deleteById(final int p0);
    
    HuizhanHistory getLatestHuizhan();
    
    int updateHzStateById(final int p0, final int p1);
    
    int updateHzEndTimeById(final Date p0, final int p1);
    
    int updateWinnerByVid(final int p0, final int p1);
    
    List<HuizhanHistory> getHuizhanByDate(final Date p0);
    
    int getWinNumByForceId(final int p0);
    
    int updateHzAttForce1ByVid(final int p0, final int p1);
    
    int updateHzAttForce2ByVid(final int p0, final int p1);
    
    int updateHzDefForceByVid(final int p0, final int p1);
    
    int updateGatherFlagByVid(final int p0, final int p1);
    
    int getFinishedHzNum();
}
