package com.reign.kfwd.cache;

import com.reign.kf.comm.entity.kfwd.response.*;
import com.reign.kfwd.domain.*;

public interface IKfwdCacheManager
{
    void iniSeasonCache(final KfwdSeasonInfo p0);
    
    void putIntoCache(final KfwdBattleWarriorGeneral p0);
    
    void putIntoCache(final KfwdBattleWarrior p0);
    
    void putIntoCache(final KfwdRuntimeInspire p0);
    
    KfwdBattleWarrior getBattleWarrior(final int p0);
    
    KfwdBattleWarriorGeneral getBattleWarriorGeneral(final int p0);
    
    KfwdRuntimeInspire getInspire(final int p0, final int p1);
    
    void putIntoCache(final KfwdTicketReward p0);
    
    void putIntoCache(final KfwdRewardDouble p0);
    
    KfwdRewardDouble getRewardDouble(final int p0);
    
    KfwdTicketReward getTicketInfo(final int p0);
}
