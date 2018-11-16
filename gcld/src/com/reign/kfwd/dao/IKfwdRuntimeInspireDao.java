package com.reign.kfwd.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfwd.domain.*;
import java.util.*;

public interface IKfwdRuntimeInspireDao extends IBaseDao<KfwdRuntimeInspire, Integer>
{
    List<KfwdRuntimeInspire> getInspireByScheduleIdAndRound(final int p0, final int p1, final int p2);
    
    KfwdRuntimeInspire getInspire(final int p0, final int p1, final int p2);
}
