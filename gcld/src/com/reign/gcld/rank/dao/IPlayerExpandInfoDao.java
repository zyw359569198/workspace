package com.reign.gcld.rank.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.rank.domain.*;
import java.util.*;

public interface IPlayerExpandInfoDao extends IBaseDao<PlayerExpandInfo>
{
    PlayerExpandInfo read(final int p0);
    
    PlayerExpandInfo readForUpdate(final int p0);
    
    List<PlayerExpandInfo> getModels();
    
    int getModelSize();
    
    int create(final PlayerExpandInfo p0);
    
    int deleteById(final int p0);
    
    void batchCreate(final List<PlayerExpandInfo> p0);
    
    void deleteAll();
    
    PlayerExpandInfo getByForceAndRank(final int p0, final int p1);
    
    void updateIsRewardedTask(final int p0, final int p1);
    
    List<PlayerExpandInfo> getByForceId(final Integer p0);
    
    void eraseByForceId(final Integer p0);
}
