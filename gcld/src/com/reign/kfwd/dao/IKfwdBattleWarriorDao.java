package com.reign.kfwd.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfwd.domain.*;
import java.util.*;

public interface IKfwdBattleWarriorDao extends IBaseDao<KfwdBattleWarrior, Integer>
{
    List<KfwdBattleWarrior> getAllWarriorBySeasonId(final int p0);
    
    KfwdBattleWarrior getPlayer(final String p0, final Integer p1, final int p2);
}
