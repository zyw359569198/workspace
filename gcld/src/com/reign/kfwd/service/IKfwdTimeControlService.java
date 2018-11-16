package com.reign.kfwd.service;

import com.reign.kf.comm.entity.kfwd.response.*;
import java.util.*;
import com.reign.util.*;

public interface IKfwdTimeControlService
{
    void processTimeInfo(final KfwdSeasonInfo p0);
    
    void iniTimeInfo(final KfwdSeasonInfo p0);
    
    long getRunDelayMillSecondsByRound(final int p0, final int p1);
    
    Date getRunMatchTime(final int p0, final int p1, final int p2, final int p3);
    
    Tuple<Integer, Long> getNowStateAndCD();
}
