package com.reign.kf.gw.kfwd.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kf.gw.kfwd.domain.*;
import java.util.*;

public interface IKfwdRuleDao extends IBaseDao<KfwdRule, Integer>
{
    List<KfwdRule> getRulesByRuleId(final int p0);
}
