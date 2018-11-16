package com.reign.kfwd.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfwd.domain.*;
import java.util.*;

public interface IKfwdRuntimeResultDao extends IBaseDao<KfwdRuntimeResult, Integer>
{
    List<KfwdRuntimeResult> getResultByScheduleId(final int p0, final int p1);
    
    List<KfwdRuntimeResult> getSortResultByScheduleId(final int p0, final int p1);
    
    KfwdRuntimeResult getPlayerByCId(final int p0, final int p1, final int p2);
    
    List<KfwdRuntimeResult> getSortResultBySeasonId(final int p0);
    
    int getMaxWinNumBySeasonId(final int p0);
    
    List<KfwdRuntimeResult> getResultBySeaonIdAndWinNum(final int p0, final int p1);
    
    List<KfwdRuntimeResult> getTopScorePlayerBySeasonIdAndNum(final int p0, final int p1);
}
