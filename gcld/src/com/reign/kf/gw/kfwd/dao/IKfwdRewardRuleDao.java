package com.reign.kf.gw.kfwd.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kf.gw.kfwd.domain.*;
import java.util.*;

public interface IKfwdRewardRuleDao extends IBaseDao<KfwdRewardRule, Integer>
{
    List<KfwdRewardRule> getRewardBySeasonId(final int p0, final int p1);
}
