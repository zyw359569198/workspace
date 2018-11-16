package com.reign.kf.match.dao;

import com.reign.framework.jdbc.orm.*;
import com.reign.kf.match.domain.*;
import org.springframework.stereotype.*;
import com.reign.framework.jdbc.*;
import com.reign.util.*;
import java.util.*;

@Component("matchPlayerDao")
public class MatchPlayerDao extends BaseDao<MatchPlayer, Integer> implements IMatchPlayerDao
{
    @Override
	public List<MatchPlayer> getSignPlayers(final String matchTag) {
        final Params params = new Params();
        params.addParam(matchTag, Type.String);
        final List<MatchPlayer> resultList = this.getResultByHQLAndParam("com.reign.kf.match.dao.getSignPlayers", params);
        return resultList;
    }
    
    @Override
	public MatchPlayer getMatchPlayer(final String queryKey) {
        final Params params = new Params();
        params.addParam(queryKey, Type.String);
        final List<MatchPlayer> resultList = this.getResultByHQLAndParam("com.reign.kf.match.dao.getMatchPlayer", params);
        if (resultList.size() > 0) {
            return resultList.get(0);
        }
        return null;
    }
    
    @Override
	public Set<Tuple<String, String>> getServerSet(final String machineId, final String matchTag) {
        final Params params = new Params();
        params.addParam(machineId, Type.String);
        params.addParam(matchTag, Type.String);
        final List<MatchPlayer> resultList = this.getResultByHQLAndParam("com.reign.kf.match.dao.getServerSet", params);
        final Set<Tuple<String, String>> serverSet = new HashSet<Tuple<String, String>>();
        for (final MatchPlayer mp : resultList) {
            serverSet.add(new Tuple(mp.getServerName(), mp.getServerId()));
        }
        return serverSet;
    }
}
