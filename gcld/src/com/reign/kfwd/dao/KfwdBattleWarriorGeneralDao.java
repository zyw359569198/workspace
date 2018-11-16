package com.reign.kfwd.dao;

import com.reign.kf.match.common.*;
import com.reign.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import org.hibernate.*;

@Component
public class KfwdBattleWarriorGeneralDao extends DirectBaseDao<KfwdBattleWarriorGeneral, Integer> implements IKfwdBattleWarriorGeneralDao
{
    @Override
    public int getAllWarriorGeneralCount(final int seasonId) {
        final String hql = "select count(*) from KfwdBattleWarriorGeneral where seasonId=?";
        final List<Long> res = (List<Long>)this.getResultByHQLAndParam(hql, seasonId);
        if (res.size() > 0) {
            return (int)(Object)res.get(0);
        }
        return 0;
    }
    
    @Override
    public List<KfwdBattleWarriorGeneral> getAllGeneralBySeasonIdAndSize(final int seasonId, final int firstPos, final int num) {
        final Session session = this.getSession();
        final String hql = "from KfwdBattleWarriorGeneral where seasonId=? order by competitorId";
        final Query query = session.createQuery(hql);
        query.setCacheable(true);
        query.setParameter(0, seasonId);
        query.setFirstResult(firstPos);
        query.setMaxResults(num);
        final List<KfwdBattleWarriorGeneral> resultList = query.list();
        this.releaseSession(session);
        return resultList;
    }
}
