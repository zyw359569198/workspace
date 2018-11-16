package com.reign.kf.gw.kfwd.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kf.gw.kfwd.domain.*;
import java.util.*;

public interface IGameServerInfoDao extends IBaseDao<GameServerInfo, Integer>
{
    List<GameServerInfo> getAllActiveAstdServer();
    
    GameServerInfo getInfoByServerKey(final String p0);
}
