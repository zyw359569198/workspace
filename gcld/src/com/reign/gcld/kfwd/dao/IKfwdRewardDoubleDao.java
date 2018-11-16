package com.reign.gcld.kfwd.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.kfwd.domain.*;
import java.util.*;

public interface IKfwdRewardDoubleDao extends IBaseDao<KfwdRewardDouble>
{
    KfwdRewardDouble read(final int p0);
    
    KfwdRewardDouble readForUpdate(final int p0);
    
    List<KfwdRewardDouble> getModels();
    
    int getModelSize();
    
    int create(final KfwdRewardDouble p0);
    
    int deleteById(final int p0);
    
    KfwdRewardDouble getPlayerRewardInfoByPIdAndSeasonId(final int p0, final int p1);
    
    void updateNewDoubleInfo(final KfwdRewardDouble p0);
}
