package com.reign.gcld.kfwd.common.condition;

import org.apache.commons.lang.*;
import com.reign.gcld.common.*;
import java.util.*;

public class MatchConditionYx implements IMatchCondition
{
    private List<String> yxList;
    
    public MatchConditionYx(final String[] arg) {
        this.yxList = new ArrayList<String>();
        String yxStr = "";
        if (arg.length > 1) {
            yxStr = arg[1];
        }
        if (!StringUtils.isBlank(yxStr)) {
            String[] split;
            for (int length = (split = yxStr.split("\\|")).length, i = 0; i < length; ++i) {
                final String yx = split[i];
                this.yxList.add(yx);
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
        final String yx = Configuration.getProperty("gcld.yx");
        for (final String needYx : this.yxList) {
            if (yx.indexOf(needYx) != -1) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean checkPlayer(final int playerId, final IDataGetter dataGetter) {
        return true;
    }
}
