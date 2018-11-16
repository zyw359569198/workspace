package com.reign.gcld.world.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.world.domain.*;
import java.util.*;

public interface IPlayerKillRewardDao extends IBaseDao<PlayerKillReward>
{
    PlayerKillReward read(final int p0);
    
    PlayerKillReward readForUpdate(final int p0);
    
    List<PlayerKillReward> getModels();
    
    int getModelSize();
    
    int create(final PlayerKillReward p0);
    
    int deleteById(final int p0);
    
    int updateReward(final int p0, final int p1, final long p2);
    
    int deleteAll();
    
    int updateKillReward(final int p0, final int p1, final int p2, final int p3, final long p4);
    
    List<PlayerKillReward> getCanRewardList();
}
