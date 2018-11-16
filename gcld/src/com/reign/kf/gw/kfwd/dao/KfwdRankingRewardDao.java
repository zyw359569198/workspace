package com.reign.kf.gw.kfwd.dao;

import com.reign.kf.common.dao.*;
import com.reign.kf.gw.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfwdRankingRewardDao extends DirectBaseDao<KfwdRankingReward, Integer> implements IKfwdRankingRewardDao
{
    @Override
    public List<KfwdRankingReward> getOrderedRanking() {
        final String hql = "from KfwdRankingReward order by rank";
        return (List<KfwdRankingReward>)this.getResultByHQLAndParam(hql);
    }
}
