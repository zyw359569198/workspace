package com.reign.kf.gw.kfwd.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kf.gw.kfwd.domain.*;

public interface IKfwdSeasonInfoDao extends IBaseDao<KfwdSeasonInfo, Integer>
{
    KfwdSeasonInfo getActiveSeasonInfo();
    
    KfwdSeasonInfo getActiveSeasonInfoWithOutEndTime();
}
