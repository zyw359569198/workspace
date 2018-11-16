package com.reign.kfzb.cache;

import com.reign.kfzb.dto.response.*;
import com.reign.kfzb.domain.*;

public interface IKfzbCacheManager
{
    void iniSeasonCache(final KfzbSeasonInfo p0);
    
    KfzbBattleWarrior getBattleWarrior(final int p0);
    
    void putIntoCache(final KfzbBattleWarrior p0);
    
    KfzbBattleWarriorGeneral getBattleWarriorGeneral(final int p0);
    
    void putIntoCache(final KfzbBattleWarriorGeneral p0);
    
    String getGameServerByCId(final int p0);
}
