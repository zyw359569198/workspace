package com.reign.kfzb.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfzb.domain.*;
import java.util.*;

public interface IKfzbSeasonInfoDDao extends IBaseDao<KfzbSeasonInfoD, Integer>
{
    KfzbSeasonInfoD getActiveSeasonInfo();
    
    List<KfzbSeasonInfoD> getNeedEndSeasonInfo();
    
    KfzbSeasonInfoD getLastSeaonInfo();
}
