package com.reign.kfzb.dao;

import com.reign.kf.common.dao.*;
import com.reign.kfzb.domain.*;
import org.springframework.stereotype.*;

@Component
public class KfzbGameServerLimitDao extends DirectBaseDao<KfzbGameServerLimit, Integer> implements IKfzbGameServerLimitDao
{
    @Override
    public KfzbGameServerLimit getGameServerByName(final String serverKey) {
        final String hql = "from KfzbGameServerLimit where gameServer=?";
        return ((DirectBaseDao<KfzbGameServerLimit, PK>)this).getFirstResultByHQLAndParam(hql, serverKey);
    }
}
