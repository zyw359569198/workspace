package com.reign.gcld.kfwd.common.condition;

import com.reign.gcld.common.*;
import com.reign.gcld.player.domain.*;

public class MatchConditionPlayerLvScope implements IMatchCondition
{
    private int minLv;
    private int maxLv;
    
    public MatchConditionPlayerLvScope(final String[] arg) {
        this.minLv = 0;
        this.maxLv = Integer.MAX_VALUE;
        if (arg.length > 1) {
            this.minLv = Integer.parseInt(arg[1]);
        }
        if (arg.length > 2) {
            this.maxLv = Integer.parseInt(arg[2]);
        }
    }
    
    @Override
    public int getConditionType() {
        return 2;
    }
    
    @Override
    public String getMsg() {
        return LocalMessages.T_KFWD_MATCH_10001;
    }
    
    @Override
    public boolean checkServer(final IDataGetter dataGetter) {
        return true;
    }
    
    @Override
    public boolean checkPlayer(final int playerId, final IDataGetter dataGetter) {
        final Player player = dataGetter.getPlayerDao().read(playerId);
        return player != null && player.getPlayerLv() >= this.minLv && player.getPlayerLv() <= this.maxLv;
    }
    
    public int getMinLv() {
        return this.minLv;
    }
    
    public int getMaxLv() {
        return this.maxLv;
    }
}
