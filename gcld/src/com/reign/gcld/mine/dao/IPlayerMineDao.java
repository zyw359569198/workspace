package com.reign.gcld.mine.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.mine.domain.*;
import java.util.*;

public interface IPlayerMineDao extends IBaseDao<PlayerMine>
{
    PlayerMine read(final int p0);
    
    PlayerMine readForUpdate(final int p0);
    
    List<PlayerMine> getModels();
    
    int getModelSize();
    
    int create(final PlayerMine p0);
    
    int deleteById(final int p0);
    
    PlayerMine getByOwner(final int p0, final int p1);
    
    PlayerMine getByMineId(final int p0);
    
    Map<Integer, PlayerMine> getByPage(final int p0, final int p1);
    
    int updateMode(final int p0, final int p1, final Date p2);
    
    int updateIsNew(final int p0, final int p1);
}
