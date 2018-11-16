package com.reign.kf.gw.kfwd.dao;

import com.reign.kf.common.dao.*;
import com.reign.kf.gw.kfwd.domain.*;
import org.springframework.stereotype.*;
import java.util.*;

@Component
public class GameServerInfoDao extends DirectBaseDao<GameServerInfo, Integer> implements IGameServerInfoDao
{
    @Override
    public List<GameServerInfo> getAllActiveAstdServer() {
        final String hql = "from GameServerInfo where type=? and lastSynDate>?";
        final Calendar c = Calendar.getInstance();
        c.add(10, -24);
        final Date d = c.getTime();
        final List<GameServerInfo> list = (List<GameServerInfo>)this.getResultByHQLAndParam(hql, 0, d);
        return list;
    }
    
    @Override
    public GameServerInfo getInfoByServerKey(final String serverKey) {
        final String hql = "from GameServerInfo where gameServer=? ";
        return ((DirectBaseDao<GameServerInfo, PK>)this).getFirstResultByHQLAndParam(hql, serverKey);
    }
}
