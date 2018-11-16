package com.reign.kf.gw.kfwd.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kf.gw.kfwd.domain.*;
import java.util.*;

public interface IMatchServerInfoDao extends IBaseDao<MatchServerInfo, Integer>
{
    List<MatchServerInfo> getActiveMatch();
    
    MatchServerInfo getMatchInfoByMatchId(final int p0);
}
