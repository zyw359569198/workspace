package com.reign.gcld.kfwd.common.condition;

import com.reign.gcld.common.*;
import com.reign.gcld.common.util.*;

public class MatchConditionServerYearLess implements IMatchCondition
{
    private int minYear;
    
    public MatchConditionServerYearLess(final String[] arg) {
        if (arg.length > 1) {
            this.minYear = Integer.parseInt(arg[1]);
        }
    }
    
    @Override
    public int getConditionType() {
        return 1;
    }
    
    @Override
    public String getMsg() {
        return "";
    }
    
    @Override
    public boolean checkServer(final IDataGetter dataGetter) {
        return TimeUtil.getYear(System.currentTimeMillis()) < this.minYear;
    }
    
    @Override
    public boolean checkPlayer(final int playerId, final IDataGetter dataGetter) {
        return true;
    }
}
