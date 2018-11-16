package com.reign.kfzb.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfzb.domain.*;
import java.util.*;

public interface IKfzbSeasonFeastInfoDao extends IBaseDao<KfzbSeasonFeastInfo, Integer>
{
    List<KfzbSeasonFeastInfo> getFeastInfoBySeasonId(final int p0);
    
    KfzbSeasonFeastInfo getInfoBySeasonAndPos(final int p0, final int p1);
}
