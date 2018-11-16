package com.reign.gcld.store.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.store.domain.*;
import java.util.*;

public interface IStoreHouseBakDao extends IBaseDao<StoreHouseBak>
{
    StoreHouseBak read(final int p0);
    
    StoreHouseBak readForUpdate(final int p0);
    
    List<StoreHouseBak> getModels();
    
    int getModelSize();
    
    int create(final StoreHouseBak p0);
    
    int deleteById(final int p0);
    
    List<StoreHouseBak> getListByStoreId(final int p0);
    
    void demoutDelete(final int p0);
    
    void changeSuitId(final int p0, final int p1, final int p2, final int p3);
    
    void changeBackJunior(final int p0, final int p1, final int p2, final int p3);
    
    List<StoreHouseBak> getBySuitIdAndIndex(final int p0, final int p1);
    
    int deleteByPlayerId(final int p0);
}
