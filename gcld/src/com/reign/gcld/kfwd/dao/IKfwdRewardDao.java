package com.reign.gcld.kfwd.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.kfwd.domain.*;
import java.util.*;

public interface IKfwdRewardDao extends IBaseDao<KfwdReward>
{
    KfwdReward read(final int p0);
    
    KfwdReward readForUpdate(final int p0);
    
    List<KfwdReward> getModels();
    
    int getModelSize();
    
    int create(final KfwdReward p0);
    
    int deleteById(final int p0);
    
    KfwdReward getRewardByPlayerIdAndSeasonId(final Integer p0, final int p1);
    
    void updateNewRewardInfo(final KfwdReward p0);
    
    List<KfwdReward> getRewardBySeasonId(final int p0);
    
    Integer getMaxSeasonId();
    
    void updateGetTreasure(final KfwdReward p0);
}
