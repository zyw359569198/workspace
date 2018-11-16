package com.reign.kfzb.service;

import java.util.*;
import com.reign.util.*;

public interface IKfzbTimeControlService
{
    long getRunDelayMillSecondsByRound(final int p0, final int p1);
    
    Date getRunMatchTime(final int p0, final int p1, final int p2, final int p3);
    
    Tuple<Integer, Long> getNowStateAndCD();
}
