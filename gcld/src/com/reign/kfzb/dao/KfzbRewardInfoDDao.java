package com.reign.kfzb.dao;

import com.reign.kf.common.dao.*;
import com.reign.kfzb.domain.*;
import org.springframework.stereotype.*;

@Component
public class KfzbRewardInfoDDao extends DirectBaseDao<KfzbRewardInfoD, Integer> implements IKfzbRewardInfoDDao
{
    @Override
    public KfzbRewardInfoD getRewardInfo() {
        final String hql = "from KfzbRewardInfoD";
        return ((DirectBaseDao<KfzbRewardInfoD, PK>)this).getFirstResultByHQLAndParam(hql);
    }
}
