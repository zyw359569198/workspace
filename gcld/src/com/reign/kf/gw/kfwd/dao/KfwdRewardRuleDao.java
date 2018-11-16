package com.reign.kf.gw.kfwd.dao;

import com.reign.kf.common.dao.*;
import com.reign.kf.gw.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfwdRewardRuleDao extends DirectBaseDao<KfwdRewardRule, Integer> implements IKfwdRewardRuleDao
{
    @Override
    public List<KfwdRewardRule> getRewardBySeasonId(final int seasonId, final int groupType) {
        final String hql = "from KfwdRewardRule";
        return (List<KfwdRewardRule>)this.getResultByHQLAndParam(hql);
    }
}
