package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface INationInfoDao extends IBaseDao<NationInfo>
{
    NationInfo read(final int p0);
    
    NationInfo readForUpdate(final int p0);
    
    List<NationInfo> getModels();
    
    int getModelSize();
    
    int create(final NationInfo p0);
    
    int deleteById(final int p0);
    
    int updateRankInfo(final int p0, final String p1);
    
    int addHzWinNumByForceId(final int p0);
}
