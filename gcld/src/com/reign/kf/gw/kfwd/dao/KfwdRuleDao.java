package com.reign.kf.gw.kfwd.dao;

import com.reign.kf.common.dao.*;
import com.reign.kf.gw.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfwdRuleDao extends DirectBaseDao<KfwdRule, Integer> implements IKfwdRuleDao
{
    @Override
    public List<KfwdRule> getRulesByRuleId(final int ruleId) {
        final String hql = "from KfwdRule where ruleId=? order by serverStartTime";
        return (List<KfwdRule>)this.getResultByHQLAndParam(hql, ruleId);
    }
}
