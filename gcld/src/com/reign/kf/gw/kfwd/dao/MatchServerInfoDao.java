package com.reign.kf.gw.kfwd.dao;

import com.reign.kf.common.dao.*;
import com.reign.kf.gw.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class MatchServerInfoDao extends DirectBaseDao<MatchServerInfo, Integer> implements IMatchServerInfoDao
{
    @Override
    public List<MatchServerInfo> getActiveMatch() {
        final String hql = "from MatchServerInfo where type=1";
        final List<MatchServerInfo> list = (List<MatchServerInfo>)this.getResultByHQLAndParam(hql);
        return list;
    }
    
    @Override
    public MatchServerInfo getMatchInfoByMatchId(final int matchId) {
        final String hql = "from MatchServerInfo where type=1 and matchId=?";
        final List<MatchServerInfo> list = (List<MatchServerInfo>)this.getResultByHQLAndParam(hql, matchId);
        return list.get(0);
    }
}
