package com.reign.kf.match.dao;

import com.reign.framework.jdbc.orm.*;
import com.reign.kf.match.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.jdbc.*;
import com.reign.framework.jdbc.orm.page.*;

@Component("matchScoreDao")
public class MatchScoreDao extends BaseDao<MatchScore, Integer> implements IMatchScoreDao
{
    @Override
	public List<MatchScore> getRankInfo(final String matchTag, final int rank) {
        final Params params = new Params();
        params.addParam(matchTag, Type.String);
        final PagingData page = new PagingData();
        page.setCurrentPage(0);
        page.setRowsPerPage(rank);
        final List<MatchScore> resultList = this.getResultByHQLAndParam("com.reign.kf.match.dao.getRankInfo", page, params);
        return resultList;
    }
    
    @Override
	public List<MatchScore> getScoreRankInfo(final String matchTag, final int rank) {
        final Params params = new Params();
        params.addParam(matchTag, Type.String);
        final PagingData page = new PagingData();
        page.setCurrentPage(0);
        page.setRowsPerPage(rank);
        final List<MatchScore> resultList = this.getResultByHQLAndParam("com.reign.kf.match.dao.getScoreRankInfo", page, params);
        return resultList;
    }
    
    @Override
	public List<MatchScore> getMatchScore(final String matchTag) {
        final Params params = new Params();
        params.addParam(matchTag, Type.String);
        final List<MatchScore> resultList = this.getResultByHQLAndParam("com.reign.kf.match.dao.getMatchScore", params);
        return resultList;
    }
    
    @Override
	public List<MatchScore> getMatchResult(final String matchTag, final String machineId) {
        final Params params = new Params();
        params.addParam(matchTag, Type.String);
        params.addParam(machineId, Type.String);
        final List<MatchScore> resultList = this.getResultByHQLAndParam("com.reign.kf.match.dao.getMatchResult", params);
        return resultList;
    }
    
    @Override
	public MatchScore getLastScore(final String serverPlayerId, final long updateTime) {
        final Params params = new Params();
        params.addParam(serverPlayerId, Type.String);
        params.addParam(updateTime, Type.Long);
        final List<MatchScore> resultList = this.getResultByHQLAndParam("com.reign.kf.match.dao.getLastScore", params);
        if (resultList != null && !resultList.isEmpty()) {
            return resultList.get(0);
        }
        return null;
    }
}
