package com.reign.gcld.kfzb.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.kfzb.domain.*;
import java.util.*;

public interface IKfzbSupportDao extends IBaseDao<KfzbSupport>
{
    KfzbSupport read(final int p0);
    
    KfzbSupport readForUpdate(final int p0);
    
    List<KfzbSupport> getModels();
    
    int getModelSize();
    
    int create(final KfzbSupport p0);
    
    int deleteById(final int p0);
    
    List<KfzbSupport> getUnRewardedListByWithIndex(final int p0, final int p1, final int p2, final int p3);
    
    int updateAsRewarded(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    int updateAsFailed(final int p0, final int p1, final int p2, final int p3, final int p4);
    
    KfzbSupport getByUniqIndex(final int p0, final int p1, final int p2, final int p3);
    
    List<KfzbSupport> getUnTakedSupportInfo(final int p0, final int p1);
    
    int updateTaketIt(final KfzbSupport p0);
    
    List<KfzbSupport> getUnTakedSupportInfoBySeasonId(final int p0);
}
