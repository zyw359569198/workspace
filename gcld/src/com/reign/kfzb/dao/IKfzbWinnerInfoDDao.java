package com.reign.kfzb.dao;

import com.reign.framework.hibernate.dao.*;
import com.reign.kfzb.domain.*;
import java.util.*;

public interface IKfzbWinnerInfoDDao extends IBaseDao<KfzbWinnerInfoD, Integer>
{
    void deleteAllSeasonInfo(final int p0);
    
    List<KfzbWinnerInfoD> getTop16PlayerInfo();
}
