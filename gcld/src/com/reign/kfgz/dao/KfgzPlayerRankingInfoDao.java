package com.reign.kfgz.dao;

import com.reign.kf.common.dao.*;
import com.reign.kfgz.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.hibernate.page.*;
import com.reign.kfgz.dto.request.*;
import java.util.*;

@Component
public class KfgzPlayerRankingInfoDao extends DirectBaseDao<KfgzPlayerRankingInfo, Integer> implements IKfgzPlayerRankingInfoDao
{
    @Override
    public KfgzPlayerRankingInfo getInfoByCIdAndSeasonId(final int seasonId, final int cId) {
        final String hql = "from KfgzPlayerRankingInfo where seasonId=? and cId=?";
        return ((DirectBaseDao<KfgzPlayerRankingInfo, PK>)this).getFirstResultByHQLAndParam(hql, seasonId, cId);
    }
    
    @Override
    public List<KfgzPlayerRankingInfo> getOrderedKillArmyInfoByLayerId(final int seasonId, final int layerId) {
        final String hql = "from KfgzPlayerRankingInfo where seasonId=? and layerId=? order by killArmy desc";
        final PagingData page = new PagingData();
        page.setCurrentPage(0);
        page.setRowsPerPage(10);
        return (List<KfgzPlayerRankingInfo>)this.getResultByHQLAndParamNoUpdate(hql, page, seasonId, layerId);
    }
    
    @Override
    public List<KfgzPlayerRankingInfo> getOrderedSoloInfoByLayerId(final int seasonId, final int layerId) {
        final String hql = "from KfgzPlayerRankingInfo where seasonId=? and layerId=? order by soloNum desc";
        final PagingData page = new PagingData();
        page.setCurrentPage(0);
        page.setRowsPerPage(10);
        return (List<KfgzPlayerRankingInfo>)this.getResultByHQLAndParamNoUpdate(hql, page, seasonId, layerId);
    }
    
    @Override
    public List<KfgzPlayerRankingInfo> getOrderedOccupyCityoInfoByLayerId(final int seasonId, final int layerId) {
        final String hql = "from KfgzPlayerRankingInfo where seasonId=? and layerId=? order by occupyCity desc";
        final PagingData page = new PagingData();
        page.setCurrentPage(0);
        page.setRowsPerPage(10);
        return (List<KfgzPlayerRankingInfo>)this.getResultByHQLAndParamNoUpdate(hql, page, seasonId, layerId);
    }
    
    @Override
    public List<KfgzPlayerRankingInfo> getOrderedOccupyCityoInfoByLayerIdAndGId(final int seasonId, final int layerId, final int gId) {
        final String hql = "from KfgzPlayerRankingInfo where seasonId=? and layerId=? and gId=? order by occupyCity desc";
        final PagingData page = new PagingData();
        page.setCurrentPage(0);
        page.setRowsPerPage(10);
        return (List<KfgzPlayerRankingInfo>)this.getResultByHQLAndParamNoUpdate(hql, page, seasonId, layerId, gId);
    }
    
    @Override
    public List<KfgzPlayerRankingInfo> getOrderedKillerArmyInfoByLayerIdAndGId(final int seasonId, final int layerId, final int gId) {
        final String hql = "from KfgzPlayerRankingInfo where seasonId=? and layerId=? and gId=? order by killArmy desc";
        final PagingData page = new PagingData();
        page.setCurrentPage(0);
        page.setRowsPerPage(10);
        return (List<KfgzPlayerRankingInfo>)this.getResultByHQLAndParamNoUpdate(hql, page, seasonId, layerId, gId);
    }
    
    @Override
    public List<KfgzPlayerRankingInfo> getOrderedSoloInfoByLayerIdAndGId(final int seasonId, final int layerId, final int gId) {
        final String hql = "from KfgzPlayerRankingInfo where seasonId=? and layerId=? and gId=? order by soloNum desc";
        final PagingData page = new PagingData();
        page.setCurrentPage(0);
        page.setRowsPerPage(10);
        return (List<KfgzPlayerRankingInfo>)this.getResultByHQLAndParamNoUpdate(hql, page, seasonId, layerId, gId);
    }
    
    @Override
    public KfgzPlayerRankingInfo getTopPlayerKillerRankingInfo(final int seasonId, final int layerId, final int gId, final KfgzNationResultReq nationReq, final String gameServer, final int nation) {
        final PagingData page = new PagingData();
        final String hql = "from KfgzPlayerRankingInfo where seasonId=? and layerId=? and gId=? and gameServer=? and nation=? order by killArmy desc";
        page.setCurrentPage(0);
        page.setRowsPerPage(1);
        final List<KfgzPlayerRankingInfo> list = (List<KfgzPlayerRankingInfo>)this.getResultByHQLAndParamNoUpdate(hql, page, seasonId, layerId, gId, gameServer, nation);
        if (list.size() > 0) {
            return list.get(0);
        }
        return new KfgzPlayerRankingInfo();
    }
    
    @Override
    public KfgzPlayerRankingInfo getTopPlayerKillerRankingInfo(final int seasonId, final int layerId, final KfgzNationResultReq nationReq, final String gameServer, final int nation) {
        final PagingData page = new PagingData();
        final String hql = "from KfgzPlayerRankingInfo where seasonId=? and layerId=?  and gameServer=? and nation=? order by killArmy desc";
        page.setCurrentPage(0);
        page.setRowsPerPage(1);
        final List<KfgzPlayerRankingInfo> list = (List<KfgzPlayerRankingInfo>)this.getResultByHQLAndParamNoUpdate(hql, page, seasonId, layerId, gameServer, nation);
        if (list.size() > 0) {
            return list.get(0);
        }
        return new KfgzPlayerRankingInfo();
    }
    
    @Override
    public Map<Integer, KfgzPlayerRankingInfo> getInfoMapBySeasonIdLayerAndGId(final int seasonId, final int layerId, final int gId) {
        final String hql = "from KfgzPlayerRankingInfo where seasonId=? and layerId=? and gId=? ";
        final List<KfgzPlayerRankingInfo> list = (List<KfgzPlayerRankingInfo>)this.getResultByHQLAndParam(hql, seasonId, layerId, gId);
        final Map<Integer, KfgzPlayerRankingInfo> map = new HashMap<Integer, KfgzPlayerRankingInfo>();
        for (final KfgzPlayerRankingInfo rInfo : list) {
            map.put(rInfo.getcId(), rInfo);
        }
        return map;
    }
}
