package com.reign.gcld.system.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.system.domain.*;
import java.util.*;

public interface IDbVersionDao extends IBaseDao<DbVersion>
{
    DbVersion read(final int p0);
    
    DbVersion readForUpdate(final int p0);
    
    List<DbVersion> getModels();
    
    int getModelSize();
    
    int create(final DbVersion p0);
    
    int deleteById(final int p0);
    
    int updateServerTime(final Date p0);
    
    int getDiffDay();
    
    int updateSeasonId(final int p0);
}
