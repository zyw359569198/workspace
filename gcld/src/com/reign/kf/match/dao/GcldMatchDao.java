package com.reign.kf.match.dao;

import com.reign.framework.jdbc.orm.*;
import com.reign.kf.match.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.jdbc.*;

@Component("gcldMatchDao")
public class GcldMatchDao extends BaseDao<GcldMatch, Integer> implements IGcldMatchDao
{
    @Override
	public List<GcldMatch> getMatch(final int turn, final int matchNum, final String matchTag) {
        final Params params = new Params();
        params.addParam(matchTag, Type.String);
        params.addParam(turn, Type.Int);
        params.addParam(matchNum, Type.Int);
        final List<GcldMatch> resultList = this.getResultByHQLAndParam("com.reign.kf.match.dao.getGcldMatch", params);
        return resultList;
    }
    
    @Override
	public GcldMatch getFirstMatch(final String matchTag, final int turn) {
        final Params params = new Params();
        params.addParam(matchTag, Type.String);
        params.addParam(turn, Type.Int);
        final List<GcldMatch> resultList = this.getResultByHQLAndParam("com.reign.kf.match.dao.getFirstMatch", params);
        if (resultList.size() > 0) {
            return resultList.get(0);
        }
        return null;
    }
    
    @Override
	public int getCurrentTurn(final String matchTag) {
        final Params params = new Params();
        params.addParam(matchTag, Type.String);
        final List<GcldMatch> resultList = this.getResultByHQLAndParam("com.reign.kf.match.dao.getCurrentTurn", params);
        if (resultList != null && resultList.size() > 0) {
            final Integer value = resultList.get(0).getTurn();
            return (value == null) ? 0 : value;
        }
        return 0;
    }
    
    @Override
	public List<GcldMatch> getMatchByTurn(final String matchTag, final int turn) {
        final Params params = new Params();
        params.addParam(matchTag, Type.String);
        params.addParam(turn, Type.Int);
        final List<GcldMatch> resultList = this.getResultByHQLAndParam("com.reign.kf.match.dao.getMatchByTurn", params);
        return resultList;
    }
    
    @Override
	public List<GcldMatch> getMatchNumMatch(final int turn, final String matchTag) {
        final Params params = new Params();
        params.addParam(matchTag, Type.String);
        params.addParam(turn, Type.Int);
        final List<GcldMatch> resultList = this.getResultByHQLAndParam("com.reign.kf.match.dao.getMatchNumMatch", params);
        return resultList;
    }
    
    @Override
	public GcldMatch getMatch(final int turn, final int matchNum, final int session, final String matchTag) {
        final Params params = new Params();
        params.addParam(matchTag, Type.String);
        params.addParam(turn, Type.Int);
        params.addParam(matchNum, Type.Int);
        params.addParam(session, Type.Int);
        final List<GcldMatch> resultList = this.getResultByHQLAndParam("com.reign.kf.match.dao.getMatchBySession", params);
        if (resultList.size() > 0) {
            return resultList.get(0);
        }
        return null;
    }
}
