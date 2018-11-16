package com.reign.gcld.world.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.world.domain.*;
import java.util.*;

public interface IWholeKillDao extends IBaseDao<WholeKill>
{
    WholeKill read(final int p0);
    
    WholeKill readForUpdate(final int p0);
    
    List<WholeKill> getModels();
    
    int getModelSize();
    
    int create(final WholeKill p0);
    
    int deleteById(final int p0);
    
    int updateKillNum(final int p0, final int p1);
    
    List<WholeKill> getRankList();
    
    void updateWholeKill();
    
    void updateKillRank(final int p0, final int p1);
    
    void received(final int p0);
}
