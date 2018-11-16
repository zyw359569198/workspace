package com.reign.kfgz.dao;

import com.reign.kf.common.dao.*;
import com.reign.kfgz.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class KfgzSeasonInfoDao extends DirectBaseDao<KfgzSeasonInfo, Integer> implements IKfgzSeasonInfoDao
{
    @Override
    public KfgzSeasonInfo getActiveSeasonInfo() {
        final String hql = "from KfgzSeasonInfo where state!=? and endTime>? order by endTime desc";
        return ((DirectBaseDao<KfgzSeasonInfo, PK>)this).getFirstResultByHQLAndParam(hql, 4, new Date());
    }
    
    @Override
    public KfgzSeasonInfo getLastSeasonInfo() {
        final String hql = "from KfgzSeasonInfo where state!=? order by endTime desc";
        return ((DirectBaseDao<KfgzSeasonInfo, PK>)this).getFirstResultByHQLAndParam(hql, 4);
    }
}
