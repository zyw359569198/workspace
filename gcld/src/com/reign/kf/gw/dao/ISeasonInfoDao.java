package com.reign.kf.gw.dao;

import com.reign.framework.jdbc.orm.*;
import com.reign.kf.gw.domain.*;
import java.util.*;

public interface ISeasonInfoDao extends IBaseDao<SeasonInfo, Integer>
{
    List<SeasonInfo> getReadyAndCancelSeason();
    
    List<SeasonInfo> getAssignedAndCancelSeason();
}
