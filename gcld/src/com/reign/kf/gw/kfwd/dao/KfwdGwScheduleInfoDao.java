package com.reign.kf.gw.kfwd.dao;

import com.reign.kf.common.dao.*;
import com.reign.kf.gw.kfwd.domain.*;
import org.springframework.stereotype.*;
import org.hibernate.*;
import java.util.*;

@Component
public class KfwdGwScheduleInfoDao extends DirectBaseDao<KfwdGwScheduleInfo, Integer> implements IKfwdGwScheduleInfoDao
{
    @Override
    public void deleteAllBySeasonId(final int seasonId) {
        final String hql = "delete from KfwdGwScheduleInfo where seasonId=?";
        final Session session = this.getSession();
        final Query query = session.createQuery(hql);
        query.setParameter(0, seasonId);
        query.executeUpdate();
        this.releaseSession(session);
    }
    
    @Override
    public List<KfwdGwScheduleInfo> getScheduleInfoByMatchAndSeasonId(final String matchName, final int seasonId) {
        final String hql = "from KfwdGwScheduleInfo where seasonId=? and matchName=? ";
        final List<KfwdGwScheduleInfo> list = (List<KfwdGwScheduleInfo>)this.getResultByHQLAndParam(hql, seasonId, matchName);
        return list;
    }
    
    @Override
    public List<KfwdGwScheduleInfo> getScheduleInfoByAstdGameServer(final String serverKey, final int seasonId) {
        final String hql = "from KfwdGwScheduleInfo where seasonId=? and gameServer=? ";
        final List<KfwdGwScheduleInfo> list = (List<KfwdGwScheduleInfo>)this.getResultByHQLAndParam(hql, seasonId, serverKey);
        return list;
    }
    
    @Override
    public KfwdGwScheduleInfo getMatchAdressBySeasonId(final int seasonId) {
        final String hql = "from KfwdGwScheduleInfo where seasonId=?";
        final List<KfwdGwScheduleInfo> list = (List<KfwdGwScheduleInfo>)this.getResultByHQLAndParam(hql, seasonId);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }
}
