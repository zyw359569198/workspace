package com.reign.kfgz.dao;

import com.reign.kf.common.dao.*;
import com.reign.kfgz.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.hibernate.page.*;

@Component
public class KfgzNationResultDao extends DirectBaseDao<KfgzNationResult, Integer> implements IKfgzNationResultDao
{
    @Override
    public KfgzNationResult getInfoBySeasonIdAndGameSever(final String gameServer, final int seasonId, final int nation) {
        final String hql = "from KfgzNationResult where seasonId=? and gameServer=? and nation=?";
        return ((DirectBaseDao<KfgzNationResult, PK>)this).getFirstResultByHQLAndParam(hql, seasonId, gameServer, nation);
    }
    
    @Override
    public List<KfgzNationResult> getOrderedInfoBySeasonIdAndLayerId(final int seasonId, final int layerId, final int round) {
        final String hql = "from KfgzNationResult where seasonId=? and layerId=? and round=? order by selfCity desc,oppCity asc";
        final PagingData page = new PagingData();
        page.setCurrentPage(0);
        page.setRowsPerPage(10);
        return (List<KfgzNationResult>)this.getResultByHQLAndParamNoUpdate(hql, page, seasonId, layerId, round);
    }
    
    @Override
    public List<KfgzNationResult> getOrderedInfoBySeasonIdAndGId(final int seasonId, final int layerId, final int gId, final int round) {
        final String hql = "from KfgzNationResult where seasonId=? and layerId=? and round=? and gId=? order by selfCity desc,oppCity asc";
        final PagingData page = new PagingData();
        page.setCurrentPage(0);
        page.setRowsPerPage(10);
        return (List<KfgzNationResult>)this.getResultByHQLAndParamNoUpdate(hql, page, seasonId, layerId, round, gId);
    }
    
    @Override
    public int getLayerRoundInfoNum(final int seasonId, final int layerId, final int round) {
        final String hql = "select count(*) from KfgzNationResult where seasonId=? and layerId=? and round=? ";
        final List<Long> numList = (List<Long>)this.getResultByHQLAndParam(hql, seasonId, layerId, round);
        if (numList.size() > 0) {
            return (int)(Object)numList.get(0);
        }
        return 0;
    }
}
