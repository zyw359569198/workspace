package com.reign.gcld.kfgz.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.kfgz.domain.*;
import java.util.*;

public interface IKfgzPlayerFinalRewardDao extends IBaseDao<KfgzPlayerFinalReward>
{
    KfgzPlayerFinalReward read(final int p0);
    
    KfgzPlayerFinalReward readForUpdate(final int p0);
    
    List<KfgzPlayerFinalReward> getModels();
    
    int getModelSize();
    
    int create(final KfgzPlayerFinalReward p0);
    
    int deleteById(final int p0);
    
    List<KfgzPlayerFinalReward> getBySeasonId(final int p0);
    
    KfgzPlayerFinalReward safeGetKfgzPlayerFinalReward(final int p0, final int p1, final int p2);
    
    int addGetFinalReward(final int p0, final int p1, final String p2, final String p3, final int p4);
}
