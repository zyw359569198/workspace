package com.reign.gcld.player.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.player.domain.*;
import com.reign.gcld.player.dto.*;
import java.util.*;
import com.reign.gcld.player.common.*;

public interface IPlayerResourceDao extends IBaseDao<PlayerResource>
{
    PlayerResource read(final int p0);
    
    int create(final PlayerResource p0);
    
    void addCopperIgnoreMax(final int p0, final double p1, final Object p2, final boolean p3);
    
    void setCopper(final int p0, final double p1, final Object p2);
    
    void addWoodIgnoreMax(final int p0, final double p1, final Object p2, final boolean p3);
    
    void setWood(final int p0, final double p1, final Object p2);
    
    void addFoodIgnoreMax(final int p0, final double p1, final Object p2);
    
    void setFood(final int p0, final double p1, final Object p2);
    
    boolean consumeFood(final int p0, final int p1, final Object p2);
    
    boolean consumeCopper(final int p0, final int p1, final Object p2);
    
    boolean consumeCopperUnconditional(final int p0, final int p1, final Object p2);
    
    boolean consumeWood(final int p0, final int p1, final Object p2);
    
    boolean consumeIron(final int p0, final int p1, final Object p2);
    
    void addIronIgnoreMax(final int p0, final int p1, final Object p2, final boolean p3);
    
    void setIron(final int p0, final double p1, final Object p2);
    
    void addExp(final int p0, final long p1, final Object p2);
    
    void setExp(final int p0, final long p1, final Object p2, final long p3);
    
    void updateResourceCareMax(final int p0, final List<ResourceDto> p1, final Date p2, final Object p3, final PlayerResource p4, final boolean p5);
    
    boolean addResourceIgnoreMax(final int p0, final List<ResourceDto> p1, final Object p2, final boolean p3);
    
    boolean consumeResource(final int p0, final int p1, final int p2, final int p3, final int p4, final Object p5);
    
    void pushIncenseData(final int p0, final int p1);
    
    int updateUpdateTime(final int p0, final Date p1);
    
    boolean updateResourceForKfgz(final int p0, final long p1, final long p2, final long p3, final long p4, final long p5, final long p6, final String p7);
    
    int clearKfgzVersion(final int p0, final long p1);
    
    int resourceUpdate(final int p0, final Resource p1);
    
    PlayerResource get(final int p0);
}
