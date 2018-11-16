package com.reign.gcld.store.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.store.domain.*;
import java.util.*;

public interface IPlayerStoreDao extends IBaseDao<PlayerStore>
{
    PlayerStore read(final int p0);
    
    PlayerStore readForUpdate(final int p0);
    
    List<PlayerStore> getModels();
    
    int getModelSize();
    
    int create(final PlayerStore p0);
    
    int deleteById(final int p0);
    
    void updatePlayerStore(final PlayerStore p0);
    
    void updateLockId(final int p0, final String p1);
    
    int updateUnrefreshEquip(final int p0, final String p1);
}
