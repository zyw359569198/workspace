package com.reign.kfzb.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfzb.domain.*;
import java.util.*;

public interface IKfzbBattleWarriorGeneralDao extends IBaseDao<KfzbBattleWarriorGeneral, Integer>
{
    List<KfzbBattleWarriorGeneral> getGeneralInfoBySeasonId(final int p0);
    
    KfzbBattleWarriorGeneral getGInfoByCIdAndSeasonId(final int p0, final int p1);
}
