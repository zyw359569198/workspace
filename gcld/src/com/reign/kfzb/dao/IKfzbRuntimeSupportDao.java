package com.reign.kfzb.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfzb.domain.*;
import java.util.*;

public interface IKfzbRuntimeSupportDao extends IBaseDao<KfzbRuntimeSupport, Integer>
{
    List<KfzbRuntimeSupport> getAllSupportBySeasonId(final int p0);
    
    KfzbRuntimeSupport getSupport(final int p0, final int p1);
}
