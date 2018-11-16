package com.reign.kfwd.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfwd.domain.*;
import java.util.*;

public interface IKfwdBattleWarriorGeneralDao extends IBaseDao<KfwdBattleWarriorGeneral, Integer>
{
    int getAllWarriorGeneralCount(final int p0);
    
    List<KfwdBattleWarriorGeneral> getAllGeneralBySeasonIdAndSize(final int p0, final int p1, final int p2);
}
