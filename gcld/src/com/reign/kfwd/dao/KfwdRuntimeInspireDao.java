package com.reign.kfwd.dao;

import com.reign.kf.match.common.*;
import com.reign.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfwdRuntimeInspireDao extends DirectBaseDao<KfwdRuntimeInspire, Integer> implements IKfwdRuntimeInspireDao
{
    @Override
    public List<KfwdRuntimeInspire> getInspireByScheduleIdAndRound(final int curSeasonId, final int scheduleId, final int round) {
        final String hql = "from KfwdRuntimeInspire where seasonId=? and scheduleId=? and round=?";
        return (List<KfwdRuntimeInspire>)this.getResultByHQLAndParam(hql, curSeasonId, scheduleId, round);
    }
    
    @Override
    public KfwdRuntimeInspire getInspire(final int curSeasonId, final int selfcId, final int round) {
        return null;
    }
}
