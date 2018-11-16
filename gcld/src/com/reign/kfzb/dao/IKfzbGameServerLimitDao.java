package com.reign.kfzb.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfzb.domain.*;

public interface IKfzbGameServerLimitDao extends IBaseDao<KfzbGameServerLimit, Integer>
{
    KfzbGameServerLimit getGameServerByName(final String p0);
}
