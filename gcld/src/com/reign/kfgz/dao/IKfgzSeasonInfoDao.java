package com.reign.kfgz.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfgz.domain.*;

public interface IKfgzSeasonInfoDao extends IBaseDao<KfgzSeasonInfo, Integer>
{
    KfgzSeasonInfo getActiveSeasonInfo();
    
    KfgzSeasonInfo getLastSeasonInfo();
}
