package com.reign.kfzb.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfzb.domain.*;

public interface IKfzbRewardInfoDDao extends IBaseDao<KfzbRewardInfoD, Integer>
{
    KfzbRewardInfoD getRewardInfo();
}
