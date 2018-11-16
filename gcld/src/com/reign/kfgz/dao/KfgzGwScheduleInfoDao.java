package com.reign.kfgz.dao;

import com.reign.kf.common.dao.*;
import com.reign.kfgz.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfgzGwScheduleInfoDao extends DirectBaseDao<KfgzGwScheduleInfo, Integer> implements IKfgzGwScheduleInfoDao
{
    @Override
    public List<KfgzGwScheduleInfo> getInfoByGameServerSeasonIdAndRound(final int seasonId, final int realRound, final String serverKey) {
        final String hql = "from KfgzGwScheduleInfo where seasonId=? and round=? and (gameServer1=? or gameServer2=?)";
        return (List<KfgzGwScheduleInfo>)this.getResultByHQLAndParam(hql, seasonId, realRound, serverKey, serverKey);
    }
    
    @Override
    public List<KfgzGwScheduleInfo> getInfoByMatchNameSeasonIdAndRound(final int seasonId, final int realRound, final String matchName) {
        final String hql = "from KfgzGwScheduleInfo where seasonId=? and round=? and (matchName=?)";
        return (List<KfgzGwScheduleInfo>)this.getResultByHQLAndParam(hql, seasonId, realRound, matchName);
    }
    
    @Override
    public List<KfgzGwScheduleInfo> getInfoByGameServerAndSeasonId(final int seasonId, final String serverKey) {
        final String hql = "from KfgzGwScheduleInfo where seasonId=? and (gameServer1=? or gameServer2=?)";
        return (List<KfgzGwScheduleInfo>)this.getResultByHQLAndParam(hql, seasonId, serverKey, serverKey);
    }
    
    @Override
    public int getLayerScheduledOriginInfoGameServerSize(final int seasonId, final int layerId) {
        final String hql = "from KfgzGwScheduleInfo where seasonId=? and layerId=? and round=1 ";
        final List<KfgzGwScheduleInfo> list = (List<KfgzGwScheduleInfo>)this.getResultByHQLAndParam(hql, seasonId, layerId);
        int count = 0;
        for (final KfgzGwScheduleInfo sg : list) {
            if (sg.getNation1() > 0) {
                ++count;
            }
            if (sg.getNation2() > 0) {
                ++count;
            }
        }
        return count;
    }
    
    @Override
    public int getLayerGIdScheduledOriginInfoGameServerSize(final int seasonId, final int layerId, final int gId) {
        final String hql = "from KfgzGwScheduleInfo where seasonId=? and layerId=? and gId=? and round=1 ";
        final List<KfgzGwScheduleInfo> list = (List<KfgzGwScheduleInfo>)this.getResultByHQLAndParam(hql, seasonId, layerId, gId);
        int count = 0;
        for (final KfgzGwScheduleInfo sg : list) {
            if (sg.getNation1() > 0) {
                ++count;
            }
            if (sg.getNation2() > 0) {
                ++count;
            }
        }
        return count;
    }
    
    @Override
    public List<KfgzGwScheduleInfo> getInfoByGIdRound(final int seasonId, final int layerId, final int gId, final int round) {
        final String hql = "from KfgzGwScheduleInfo where seasonId=? and layerId=? and gId=? and round=? ";
        return (List<KfgzGwScheduleInfo>)this.getResultByHQLAndParam(hql, seasonId, layerId, gId, round);
    }
}
