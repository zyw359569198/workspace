package com.reign.gcld.kfgz.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.kfgz.domain.*;
import java.util.*;

public interface IKfgzPlayerRewardDao extends IBaseDao<KfgzPlayerReward>
{
    int hasData(final int p0, final int p1, final int p2);
    
    KfgzPlayerReward read(final int p0);
    
    KfgzPlayerReward readForUpdate(final int p0);
    
    List<KfgzPlayerReward> getModels();
    
    int getModelSize();
    
    int create(final KfgzPlayerReward p0);
    
    int deleteById(final int p0);
    
    KfgzPlayerReward getKfgzPlayerReward(final int p0, final int p1, final int p2);
    
    int addRewardTimes(final int p0, final int p1);
    
    List<KfgzPlayerReward> getModelsBySeasonIdAndgzIdForReward(final int p0, final int p1);
    
    int getMaxGzId(final int p0);
    
    int getMaxSeasonId();
    
    List<KfgzPlayerReward> getModelsBySeasonIdForReward(final int p0, final int p1);
}
