package com.reign.kfwd.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfwd.domain.*;
import java.util.*;

public interface IKfwdRewardDoubleDao extends IBaseDao<KfwdRewardDouble, Integer>
{
    List<KfwdRewardDouble> getInfoBySeasonId(final int p0);
}
