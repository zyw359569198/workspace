package com.reign.kfwd.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfwd.domain.*;
import java.util.*;

public interface IKfwdTicketRewardDao extends IBaseDao<KfwdTicketReward, Integer>
{
    List<KfwdTicketReward> getInfoBySeasonId(final int p0);
}
