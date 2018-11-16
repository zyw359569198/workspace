package com.reign.kfwd.dao;

import com.reign.kf.match.common.*;
import com.reign.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfwdTicketRewardDao extends DirectBaseDao<KfwdTicketReward, Integer> implements IKfwdTicketRewardDao
{
    @Override
    public List<KfwdTicketReward> getInfoBySeasonId(final int seasonId) {
        final String hql = "from KfwdTicketReward where seasonId=?";
        return (List<KfwdTicketReward>)this.getResultByHQLAndParam(hql, seasonId);
    }
}
