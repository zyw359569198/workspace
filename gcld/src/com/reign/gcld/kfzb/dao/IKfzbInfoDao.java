package com.reign.gcld.kfzb.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.kfzb.domain.*;
import java.util.*;

public interface IKfzbInfoDao extends IBaseDao<KfzbInfo>
{
    KfzbInfo read(final int p0);
    
    KfzbInfo readForUpdate(final int p0);
    
    List<KfzbInfo> getModels();
    
    int getModelSize();
    
    int create(final KfzbInfo p0);
    
    int deleteById(final int p0);
    
    KfzbInfo getByPlayerIdSeasonId(final int p0, final int p1);
    
    int updateSupport1DecreaseFlower1(final int p0, final int p1, final String p2);
    
    int updateSupport2DecreaseFlower2(final int p0, final int p1, final String p2);
    
    List<KfzbInfo> getBySeasonId(final int p0);
    
    int buyFlower1(final Integer p0, final Integer p1);
    
    int buyFlower2(final Integer p0, final Integer p1);
}
