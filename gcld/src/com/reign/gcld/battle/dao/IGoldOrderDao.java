package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IGoldOrderDao extends IBaseDao<GoldOrder>
{
    GoldOrder read(final int p0);
    
    GoldOrder readForUpdate(final int p0);
    
    List<GoldOrder> getModels();
    
    int getModelSize();
    
    int create(final GoldOrder p0);
    
    int deleteById(final int p0);
    
    int deleteByForceIdAndCityId(final int p0, final int p1);
    
    GoldOrder getByForceIdAndCityId(final int p0, final int p1);
    
    int updateNumByForceIdAndCityId(final int p0, final int p1, final int p2);
    
    int deleteByCityId(final int p0);
}
