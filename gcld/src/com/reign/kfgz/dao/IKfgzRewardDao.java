package com.reign.kfgz.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfgz.domain.*;
import java.util.*;

public interface IKfgzRewardDao extends IBaseDao<KfgzReward, Integer>
{
    List<KfgzReward> getRewardListByGId(final int p0);
    
    KfgzReward getRewardByGIdAndLayer(final int p0, final int p1);
}
