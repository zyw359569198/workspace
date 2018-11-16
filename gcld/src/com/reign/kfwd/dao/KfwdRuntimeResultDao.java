package com.reign.kfwd.dao;

import com.reign.kf.match.common.*;
import com.reign.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import org.hibernate.*;

@Component
public class KfwdRuntimeResultDao extends DirectBaseDao<KfwdRuntimeResult, Integer> implements IKfwdRuntimeResultDao
{
    @Override
    public List<KfwdRuntimeResult> getSortResultByScheduleId(final int seasonId, final int scheduleId) {
        final String hql = "from KfwdRuntimeResult where seasonId=? and scheduleId=? order by score desc";
        return (List<KfwdRuntimeResult>)this.getResultByHQLAndParam(hql, seasonId, scheduleId);
    }
    
    @Override
    public KfwdRuntimeResult getPlayerByCId(final int seasonId, final int scheduleId, final int winnerId) {
        return this.read(winnerId);
    }
    
    @Override
    public List<KfwdRuntimeResult> getResultByScheduleId(final int curSeasonId, final int scheduleId) {
        final String hql = "from KfwdRuntimeResult where seasonId=? and scheduleId=? ";
        return (List<KfwdRuntimeResult>)this.getResultByHQLAndParam(hql, curSeasonId, scheduleId);
    }
    
    @Override
    public List<KfwdRuntimeResult> getSortResultBySeasonId(final int seasonId) {
        final String hql = "from KfwdRuntimeResult where seasonId=? order by score desc ";
        return (List<KfwdRuntimeResult>)this.getResultByHQLAndParam(hql, seasonId);
    }
    
    @Override
    public int getMaxWinNumBySeasonId(final int curWdSeasonId) {
        final String hql = "select max(winNum) from KfwdRuntimeResult where seasonId=?";
        final List<Integer> list = (List<Integer>)this.getResultByHQLAndParam(hql, curWdSeasonId);
        if (list.size() > 0) {
            return list.get(0);
        }
        return 0;
    }
    
    @Override
    public List<KfwdRuntimeResult> getResultBySeaonIdAndWinNum(final int curWdSeasonId, final int winNum) {
        final String hql = "from KfwdRuntimeResult where seasonId=? and score=?";
        return (List<KfwdRuntimeResult>)this.getResultByHQLAndParam(hql, curWdSeasonId, winNum);
    }
    
    @Override
    public List<KfwdRuntimeResult> getTopScorePlayerBySeasonIdAndNum(final int curWdSeasonId, final int num) {
        final String hql = "from KfwdRuntimeResult where seasonId=? order by score desc";
        final Session session = this.getSession();
        final Query query = session.createQuery(hql);
        query.setCacheable(true);
        query.setParameter(0, curWdSeasonId);
        query.setFirstResult(0);
        query.setMaxResults(num);
        final List<KfwdRuntimeResult> newlist = query.list();
        this.releaseSession(session);
        return newlist;
    }
}
