package com.reign.kf.gw.kfwd.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kf.gw.kfwd.domain.*;
import java.util.*;

public interface IKfwdGwScheduleInfoDao extends IBaseDao<KfwdGwScheduleInfo, Integer>
{
    void deleteAllBySeasonId(final int p0);
    
    List<KfwdGwScheduleInfo> getScheduleInfoByMatchAndSeasonId(final String p0, final int p1);
    
    List<KfwdGwScheduleInfo> getScheduleInfoByAstdGameServer(final String p0, final int p1);
    
    KfwdGwScheduleInfo getMatchAdressBySeasonId(final int p0);
}
