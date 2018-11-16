package com.reign.kfzb.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfzb.domain.*;
import java.util.*;

public interface IKfzbBattleWarriorDao extends IBaseDao<KfzbBattleWarrior, Integer>
{
    List<KfzbBattleWarrior> getWarriorBySeasonId(final int p0);
    
    List<KfzbBattleWarrior> getWarriorBySeasonIdAndGameServer(final int p0, final String p1);
    
    KfzbBattleWarrior getPlayerByCId(final int p0, final int p1);
}
