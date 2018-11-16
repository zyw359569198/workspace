package com.reign.kfgz.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfgz.domain.*;
import java.util.*;

public interface IKfgzGwScheduleInfoDao extends IBaseDao<KfgzGwScheduleInfo, Integer>
{
    List<KfgzGwScheduleInfo> getInfoByGameServerSeasonIdAndRound(final int p0, final int p1, final String p2);
    
    List<KfgzGwScheduleInfo> getInfoByMatchNameSeasonIdAndRound(final int p0, final int p1, final String p2);
    
    List<KfgzGwScheduleInfo> getInfoByGameServerAndSeasonId(final int p0, final String p1);
    
    int getLayerScheduledOriginInfoGameServerSize(final int p0, final int p1);
    
    int getLayerGIdScheduledOriginInfoGameServerSize(final int p0, final int p1, final int p2);
    
    List<KfgzGwScheduleInfo> getInfoByGIdRound(final int p0, final int p1, final int p2, final int p3);
}
