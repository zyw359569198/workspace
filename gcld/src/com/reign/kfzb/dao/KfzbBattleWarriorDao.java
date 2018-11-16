package com.reign.kfzb.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfzb.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfzbBattleWarriorDao extends BaseDao<KfzbBattleWarrior, Integer> implements IKfzbBattleWarriorDao
{
    @Override
	public List<KfzbBattleWarrior> getWarriorBySeasonId(final int curSeasonId) {
        final String hql = "from KfzbBattleWarrior where seasonId=?";
        final List<KfzbBattleWarrior> list = (List<KfzbBattleWarrior>)this.getResultByHQLAndParam(hql, new Object[] { curSeasonId });
        return list;
    }
    
    @Override
	public List<KfzbBattleWarrior> getWarriorBySeasonIdAndGameServer(final int curSeasonId, final String gameServer) {
        final String hql = "from KfzbBattleWarrior where seasonId=? and gameServer=?";
        final List<KfzbBattleWarrior> list = (List<KfzbBattleWarrior>)this.getResultByHQLAndParam(hql, new Object[] { curSeasonId, gameServer });
        return list;
    }
    
    @Override
	public KfzbBattleWarrior getPlayerByCId(final int seasonId, final int competitorId) {
        final String hql = "from KfzbBattleWarrior where seasonId=? and competitorId=?";
        return this.getFirstResultByHQLAndParam(hql, new Object[] { seasonId, competitorId });
    }
}
