package com.reign.gcld.store.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.store.domain.*;
import java.util.*;

public interface IPlayerItemRefreshDao extends IBaseDao<PlayerItemRefresh>
{
    PlayerItemRefresh read(final int p0);
    
    PlayerItemRefresh readForUpdate(final int p0);
    
    List<PlayerItemRefresh> getModels();
    
    int getModelSize();
    
    int create(final PlayerItemRefresh p0);
    
    int deleteById(final int p0);
    
    List<PlayerItemRefresh> getListByPlayerId(final int p0);
    
    PlayerItemRefresh getPlayerItemRefresh(final int p0, final int p1);
    
    void lockItem(final int p0);
    
    void unlockItem(final int p0, final Date p1);
    
    void buyItem(final int p0, final int p1);
    
    void update(final PlayerItemRefresh p0);
}
