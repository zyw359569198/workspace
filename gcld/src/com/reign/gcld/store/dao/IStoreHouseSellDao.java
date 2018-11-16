package com.reign.gcld.store.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.store.domain.*;
import java.util.*;

public interface IStoreHouseSellDao extends IBaseDao<StoreHouseSell>
{
    StoreHouseSell read(final int p0);
    
    StoreHouseSell readForUpdate(final int p0);
    
    List<StoreHouseSell> getModels();
    
    int getModelSize();
    
    int create(final StoreHouseSell p0);
    
    int deleteById(final int p0);
    
    List<StoreHouseSell> getByPlayerId(final int p0);
    
    StoreHouseSell getByItemId(final int p0, final int p1, final int p2);
    
    int addNumByVid(final int p0, final int p1);
}
