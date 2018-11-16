package com.reign.kfwd.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfwd.domain.*;
import java.util.*;

public interface IKfwdRuntimeMatchDao extends IBaseDao<KfwdRuntimeMatch, Integer>
{
    List<KfwdRuntimeMatch> getLastRoundMatch(final int p0, final int p1);
    
    List<KfwdRuntimeMatch> getSRoundMatch(final int p0, final int p1, final int p2, final int p3);
    
    List<KfwdRuntimeMatch> getRunMatchByRound(final int p0, final int p1, final int p2);
    
    KfwdRuntimeMatch getOneLastRoundLastMatch(final int p0);
}
