package com.reign.gcld.kfwd.common.condition;

import com.reign.gcld.common.*;

public interface IMatchCondition
{
    int getConditionType();
    
    String getMsg();
    
    boolean checkServer(final IDataGetter p0);
    
    boolean checkPlayer(final int p0, final IDataGetter p1);
}
