package com.reign.kf.gw.kfwd.dao;

import com.reign.kf.common.dao.*;
import com.reign.kf.gw.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfwdSeasonInfoDao extends DirectBaseDao<KfwdSeasonInfo, Integer> implements IKfwdSeasonInfoDao
{
    @Override
    public KfwdSeasonInfo getActiveSeasonInfo() {
        final String hql = "from KfwdSeasonInfo where valid=1 and globalState<4 and endTime>? order by endTime asc";
        return ((DirectBaseDao<KfwdSeasonInfo, PK>)this).getFirstResultByHQLAndParam(hql, new Date(System.currentTimeMillis() - 1800000L));
    }
    
    @Override
    public KfwdSeasonInfo getActiveSeasonInfoWithOutEndTime() {
        final String hql = "from KfwdSeasonInfo where valid=1 and globalState<4";
        final List<KfwdSeasonInfo> kfsi = (List<KfwdSeasonInfo>)this.getResultByHQLAndParam(hql);
        if (kfsi.size() > 0) {
            return kfsi.get(0);
        }
        return null;
    }
}
