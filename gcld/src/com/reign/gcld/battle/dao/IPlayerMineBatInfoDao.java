package com.reign.gcld.battle.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.battle.domain.*;
import java.util.*;

public interface IPlayerMineBatInfoDao extends IBaseDao<PlayerMineBatInfo>
{
    PlayerMineBatInfo read(final int p0);
    
    PlayerMineBatInfo readForUpdate(final int p0);
    
    List<PlayerMineBatInfo> getModels();
    
    int getModelSize();
    
    int create(final PlayerMineBatInfo p0);
    
    int deleteById(final int p0);
    
    int updateInfo(final int p0, final String p1);
}
