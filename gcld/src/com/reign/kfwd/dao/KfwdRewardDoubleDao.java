package com.reign.kfwd.dao;

import com.reign.kf.match.common.*;
import com.reign.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfwdRewardDoubleDao extends DirectBaseDao<KfwdRewardDouble, Integer> implements IKfwdRewardDoubleDao
{
    @Override
    public List<KfwdRewardDouble> getInfoBySeasonId(final int seasonId) {
        final String hql = "from KfwdRewardDouble where seasonId=?";
        return (List<KfwdRewardDouble>)this.getResultByHQLAndParam(hql, seasonId);
    }
}
