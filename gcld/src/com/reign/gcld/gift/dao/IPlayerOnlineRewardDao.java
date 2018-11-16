package com.reign.gcld.gift.dao;

import com.reign.framework.mybatis.*;
import com.reign.gcld.gift.domain.*;
import java.util.*;

public interface IPlayerOnlineRewardDao extends IBaseDao<PlayerOnlineReward>
{
    PlayerOnlineReward read(final int p0);
    
    PlayerOnlineReward readForUpdate(final int p0);
    
    List<PlayerOnlineReward> getModels();
    
    int getModelSize();
    
    int create(final PlayerOnlineReward p0);
    
    int deleteById(final int p0);
    
    int getOnlineNum(final int p0);
    
    int useOnlineNum(final int p0);
    
    int addOnlineNum(final int p0);
    
    int setOnlineGiftBaseData(final int p0, final int p1, final int p2);
    
    int resetOnlineGiftData(final int p0);
}
