package com.reign.kfzb.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfzb.domain.*;
import java.util.*;

public interface IKfzbRuntimeResultDao extends IBaseDao<KfzbRuntimeResult, Integer>
{
    List<KfzbRuntimeResult> getResultBySeasonId(final int p0);
    
    void updateTotalLayer(final int p0, final int p1);
    
    KfzbRuntimeResult getInfoByCIdAndSeasonId(final int p0, final int p1);
    
    List<KfzbRuntimeResult> getTop16PlayerInfo(final Integer p0);
    
    void updateResultByCreateMatch(final int p0, final int p1, final int p2);
}
