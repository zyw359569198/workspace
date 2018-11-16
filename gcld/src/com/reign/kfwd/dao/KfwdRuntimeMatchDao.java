package com.reign.kfwd.dao;

import com.reign.kf.match.common.*;
import com.reign.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import org.hibernate.*;
import com.reign.kfwd.constants.*;

@Component
public class KfwdRuntimeMatchDao extends DirectBaseDao<KfwdRuntimeMatch, Integer> implements IKfwdRuntimeMatchDao
{
    @Override
    public List<KfwdRuntimeMatch> getLastRoundMatch(final int curSeasonId, final int scheduleId) {
        final String hql = "from KfwdRuntimeMatch where seasonId=? and scheduleId=? order by round desc";
        final Session session = this.getSession();
        final Query query = session.createQuery(hql);
        query.setCacheable(true);
        query.setParameter(0, curSeasonId);
        query.setParameter(1, scheduleId);
        query.setFirstResult(0);
        query.setMaxResults(1);
        final List<KfwdRuntimeMatch> resultList = query.list();
        this.releaseSession(session);
        int round = 0;
        if (resultList.size() > 0) {
            round = resultList.get(0).getRound();
        }
        if (round <= 0) {
            return null;
        }
        final String hql2 = "from KfwdRuntimeMatch where seasonId=? and scheduleId=? and round=?";
        return (List<KfwdRuntimeMatch>)this.getResultByHQLAndParam(hql2, curSeasonId, scheduleId, round);
    }
    
    @Override
    public List<KfwdRuntimeMatch> getRunMatchByRound(final int curSeasonId, final int scheduleId, final int round) {
        final String hql2 = "from KfwdRuntimeMatch where seasonId=? and scheduleId=? and round=? and sRoundWinner=0";
        return (List<KfwdRuntimeMatch>)this.getResultByHQLAndParam(hql2, curSeasonId, scheduleId, round);
    }
    
    @Override
    public List<KfwdRuntimeMatch> getSRoundMatch(final int curSeasonId, final int scheduleId, final int round, final int sRound) {
        final String hql = "from KfwdRuntimeMatch where seasonId=? and scheduleId=? and round=? and sRound=?";
        return (List<KfwdRuntimeMatch>)this.getResultByHQLAndParam(hql, curSeasonId, scheduleId, round, sRound);
    }
    
    @Override
    public KfwdRuntimeMatch getOneLastRoundLastMatch(final int seasonId) {
        final String hql = "from KfwdRuntimeMatch where seasonId=? and round=? and sRound=?";
        return ((DirectBaseDao<KfwdRuntimeMatch, PK>)this).getFirstResultByHQLAndParam(hql, seasonId, KfwdConstantsAndMethod.MAXROUND, 1);
    }
}
