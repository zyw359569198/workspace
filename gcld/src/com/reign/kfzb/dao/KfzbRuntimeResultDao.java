package com.reign.kfzb.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfzb.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import org.hibernate.*;

@Component
public class KfzbRuntimeResultDao extends BaseDao<KfzbRuntimeResult, Integer> implements IKfzbRuntimeResultDao
{
    @Override
	public List<KfzbRuntimeResult> getResultBySeasonId(final int curSeasonId) {
        final String hql = "from KfzbRuntimeResult where seasonId=?";
        return (List<KfzbRuntimeResult>)this.getResultByHQLAndParam(hql, new Object[] { curSeasonId });
    }
    
    @Override
	public void updateTotalLayer(final int seasonId, final int totalLay) {
        final String hql = "update KfzbRuntimeResult set layer=? where seasonId=?";
        final Session session = this.getSession();
        final Query query = session.createQuery(hql);
        query.setParameter(0, totalLay);
        query.setParameter(1, seasonId);
        final int count = query.executeUpdate();
        this.releaseSession(session);
    }
    
    @Override
	public KfzbRuntimeResult getInfoByCIdAndSeasonId(final int cId1, final int seasonId) {
        final String hql = "from KfzbRuntimeResult where seasonId=? and competitorId=?";
        return this.getFirstResultByHQLAndParam(hql, new Object[] { seasonId, cId1 });
    }
    
    @Override
	public List<KfzbRuntimeResult> getTop16PlayerInfo(final Integer seasonId) {
        final String hql = "from KfzbRuntimeResult where seasonId=? and layer<=4";
        return (List<KfzbRuntimeResult>)this.getResultByHQLAndParam(hql, new Object[] { seasonId });
    }
    
    @Override
	public void updateResultByCreateMatch(final int seasonId, final int layer, final int round) {
        final String hql1 = "update KfzbRuntimeResult set round=? where seasonId=? and layer=? and round<? and isfinsh<>3";
        final Session session = this.getSession();
        final Query query = session.createQuery(hql1);
        query.setParameter(0, round);
        query.setParameter(1, seasonId);
        query.setParameter(2, layer);
        query.setParameter(3, round);
        final int count = query.executeUpdate();
        this.releaseSession(session);
        final String hql2 = "update KfzbRuntimeResult set lastres=res ,res=0, layer=?, round=? where seasonId=? and layer>? and isfinsh<>3";
        final Session session2 = this.getSession();
        final Query query2 = session2.createQuery(hql2);
        query2.setParameter(0, layer);
        query2.setParameter(1, round);
        query2.setParameter(2, seasonId);
        query2.setParameter(3, layer);
        final int count2 = query2.executeUpdate();
        this.releaseSession(session2);
    }
}
