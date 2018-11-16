package com.reign.kfzb.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfzb.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfzbBattleWarriorGeneralDao extends BaseDao<KfzbBattleWarriorGeneral, Integer> implements IKfzbBattleWarriorGeneralDao
{
    @Override
	public List<KfzbBattleWarriorGeneral> getGeneralInfoBySeasonId(final int seasonId) {
        final String hql = "from KfzbBattleWarriorGeneral where seasonId=?";
        return (List<KfzbBattleWarriorGeneral>)this.getResultByHQLAndParam(hql, new Object[] { seasonId });
    }
    
    @Override
	public KfzbBattleWarriorGeneral getGInfoByCIdAndSeasonId(final int player1Id, final int seasonId) {
        final String hql = "from KfzbBattleWarriorGeneral where seasonId=? and competitorId=?";
        return this.getFirstResultByHQLAndParam(hql, new Object[] { seasonId, player1Id });
    }
}
