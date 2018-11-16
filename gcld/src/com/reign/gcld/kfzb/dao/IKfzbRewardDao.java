package com.reign.gcld.kfzb.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.kfzb.domain.*;
import java.util.*;

public interface IKfzbRewardDao extends IBaseDao<KfzbReward>
{
    KfzbReward read(final int p0);
    
    KfzbReward readForUpdate(final int p0);
    
    List<KfzbReward> getModels();
    
    int getModelSize();
    
    int create(final KfzbReward p0);
    
    int deleteById(final int p0);
    
    List<KfzbReward> getBySeasonId(final int p0);
    
    int updateDoneNum(final int p0, final int p1, final int p2);
    
    int updateRewardInfo(final Integer p0, final int p1, final String p2);
    
    int getMaxSeasonId();
    
    KfzbReward getByPlayerIdSeasonId(final Integer p0, final int p1);
    
    int updateTitle(final Integer p0, final int p1, final String p2, final int p3);
    
    List<KfzbReward> getHaveTitleBySeasonId(final int p0);
}
