package com.reign.kf.gw.dao;

import com.reign.framework.jdbc.orm.*;
import com.reign.kf.gw.domain.*;
import org.springframework.stereotype.*;
import java.util.*;
import com.reign.framework.jdbc.*;

@Component("seasonInfoDao")
public class SeasonInfoDao extends BaseDao<SeasonInfo, Integer> implements ISeasonInfoDao
{
    @Override
	public List<SeasonInfo> getReadyAndCancelSeason() {
        final Params params = new Params();
        params.addParam(3, Type.Int);
        params.addParam(2, Type.Int);
        return this.getResultByHQLAndParam("com.reign.kf.gw.dao.getGameServerSeasonInfo", params);
    }
    
    @Override
	public List<SeasonInfo> getAssignedAndCancelSeason() {
        final Params params = new Params();
        params.addParam(0, Type.Int);
        params.addParam(3, Type.Int);
        params.addParam(2, Type.Int);
        return this.getResultByHQLAndParam("com.reign.kf.gw.dao.getMatchServerSeasonInfo", params);
    }
}
