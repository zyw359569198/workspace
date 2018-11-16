package com.reign.gcld.kfwd.common.condition;

import java.text.*;
import com.reign.gcld.common.*;
import com.reign.gcld.common.util.*;

public class MatchConditionServerTimeGreater implements IMatchCondition
{
    private long timestamp;
    
    public MatchConditionServerTimeGreater(final String[] arg) {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (arg.length > 1) {
            try {
                this.timestamp = sdf.parse(arg[1]).getTime();
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
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
        return TimeUtil.getStartServerTime() >= this.timestamp;
    }
    
    @Override
    public boolean checkPlayer(final int playerId, final IDataGetter dataGetter) {
        return true;
    }
}
