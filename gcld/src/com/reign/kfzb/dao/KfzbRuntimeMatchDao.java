package com.reign.kfzb.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfzb.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfzbRuntimeMatchDao extends BaseDao<KfzbRuntimeMatch, Integer> implements IKfzbRuntimeMatchDao
{
    @Override
	public Integer getMinLayerBySeasonId(final int curSeasonId) {
        final String hql = "select min(layer) from KfzbRuntimeMatch where seasonId=?";
        final List<Integer> list = (List<Integer>)this.getResultByHQLAndParam(hql, new Object[] { curSeasonId });
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
    
    @Override
	public List<KfzbRuntimeMatch> getMatchBylayerAndSeasonId(final int layer, final int curSeasonId) {
        final String hql = "from KfzbRuntimeMatch where seasonId=? and layer=?";
        final List<KfzbRuntimeMatch> list = (List<KfzbRuntimeMatch>)this.getResultByHQLAndParam(hql, new Object[] { curSeasonId, layer });
        return list;
    }
    
    @Override
	public List<KfzbRuntimeMatch> getMatchBylayerRoundAndSeasonId(final int layer, final int round, final int curSeasonId) {
        final String hql = "from KfzbRuntimeMatch where seasonId=? and layer=? and round=?";
        final List<KfzbRuntimeMatch> list = (List<KfzbRuntimeMatch>)this.getResultByHQLAndParam(hql, new Object[] { curSeasonId, layer, round });
        return list;
    }
    
    @Override
	public List<KfzbRuntimeMatch> getUnfinishMatchByLayerAndSeasonId(final int curSeasonId, final int layer) {
        final String hql = "from KfzbRuntimeMatch where seasonId=? and layer=? and roundWinner=0";
        final List<KfzbRuntimeMatch> list = (List<KfzbRuntimeMatch>)this.getResultByHQLAndParam(hql, new Object[] { curSeasonId, layer });
        return list;
    }
    
    @Override
	public List<KfzbRuntimeMatch> getAllRound1Match(final int curSeasonId) {
        final String hql = "from KfzbRuntimeMatch where seasonId=? and round=1";
        final List<KfzbRuntimeMatch> list = (List<KfzbRuntimeMatch>)this.getResultByHQLAndParam(hql, new Object[] { curSeasonId });
        return list;
    }
    
    @Override
	public List<KfzbRuntimeMatch> getAllMatch(final int curSeasonId) {
        final String hql = "from KfzbRuntimeMatch where seasonId=? ";
        final List<KfzbRuntimeMatch> list = (List<KfzbRuntimeMatch>)this.getResultByHQLAndParam(hql, new Object[] { curSeasonId });
        return list;
    }
    
    @Override
	public List<KfzbRuntimeMatch> getCampains() {
        final String hql = "from KfzbRuntimeMatch where matchId=1 and layerWinner>0";
        final List<KfzbRuntimeMatch> list = (List<KfzbRuntimeMatch>)this.getResultByHQLAndParam(hql);
        return list;
    }
    
    @Override
	public KfzbRuntimeMatch getLastWinMatch(final int seasonId, final int layerBattleNum) {
        final String hql = "from KfzbRuntimeMatch where matchId=1 and layerWinner>0 and seasonId=? order by round desc";
        final KfzbRuntimeMatch match = this.getFirstResultByHQLAndParam(hql, new Object[] { seasonId });
        return match;
    }
    
    @Override
	public List<KfzbRuntimeMatch> getPhase2BattleInfo(final int curSeasonId) {
        final String hql = "from KfzbRuntimeMatch where seasonId=? and matchId<16 order by round";
        final List<KfzbRuntimeMatch> list = (List<KfzbRuntimeMatch>)this.getResultByHQLAndParam(hql, new Object[] { curSeasonId });
        return list;
    }
}
