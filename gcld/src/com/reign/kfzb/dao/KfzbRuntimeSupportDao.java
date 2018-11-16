package com.reign.kfzb.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfzb.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfzbRuntimeSupportDao extends BaseDao<KfzbRuntimeSupport, Integer> implements IKfzbRuntimeSupportDao
{
    @Override
	public List<KfzbRuntimeSupport> getAllSupportBySeasonId(final int curSeasonId) {
        final String hql = "from KfzbRuntimeSupport where seasonId=?";
        return (List<KfzbRuntimeSupport>)this.getResultByHQLAndParam(hql, new Object[] { curSeasonId });
    }
    
    @Override
	public KfzbRuntimeSupport getSupport(final int seaonId, final int matchId) {
        final String hql = "from KfzbRuntimeSupport where seasonId=? and matchId=? ";
        return this.getFirstResultByHQLAndParam(hql, new Object[] { seaonId, matchId });
    }
}
