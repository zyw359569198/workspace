package com.reign.kfgz.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfgz.domain.*;
import java.util.*;

public interface IKfgzNationResultDao extends IBaseDao<KfgzNationResult, Integer>
{
    KfgzNationResult getInfoBySeasonIdAndGameSever(final String p0, final int p1, final int p2);
    
    List<KfgzNationResult> getOrderedInfoBySeasonIdAndLayerId(final int p0, final int p1, final int p2);
    
    List<KfgzNationResult> getOrderedInfoBySeasonIdAndGId(final int p0, final int p1, final int p2, final int p3);
    
    int getLayerRoundInfoNum(final int p0, final int p1, final int p2);
}
