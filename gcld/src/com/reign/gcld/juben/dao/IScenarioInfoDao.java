package com.reign.gcld.juben.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.juben.domain.*;
import java.util.*;

public interface IScenarioInfoDao extends IBaseDao<ScenarioInfo>
{
    ScenarioInfo read(final int p0);
    
    ScenarioInfo readForUpdate(final int p0);
    
    List<ScenarioInfo> getModels();
    
    int getModelSize();
    
    int create(final ScenarioInfo p0);
    
    int deleteById(final int p0);
    
    int updateMinTime(final int p0, final int p1, final int p2, final int p3, final int p4, final String p5);
}
