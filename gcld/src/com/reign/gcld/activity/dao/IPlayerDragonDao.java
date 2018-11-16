package com.reign.gcld.activity.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.activity.domain.*;
import java.util.*;

public interface IPlayerDragonDao extends IBaseDao<PlayerDragon>
{
    PlayerDragon read(final int p0);
    
    PlayerDragon readForUpdate(final int p0);
    
    List<PlayerDragon> getModels();
    
    int getModelSize();
    
    int create(final PlayerDragon p0);
    
    int deleteById(final int p0);
    
    int getDragonNumByPlayerId(final int p0);
    
    int getBoxNumByPlayerId(final int p0);
    
    int setDragonNumByPlayerId(final int p0, final int p1);
    
    int useDragon(final int p0);
    
    int useBox(final int p0);
    
    int addDragonNumByPlayerId(final int p0, final int p1);
    
    int addBoxNumByPlayerId(final int p0, final int p1, final int p2);
    
    List<PlayerDragon> getDragonNumList();
    
    int clearDragon();
    
    int addFeatBoxNum(final int p0, final int p1, final int p2, final String p3);
    
    int getFeatBoxNum(final int p0);
    
    int useFeatBoxNum(final int p0, final String p1);
}
