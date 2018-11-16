package com.reign.gcld.kfwd.common.runner;

import com.reign.gcld.kfwd.common.*;
import com.reign.gcld.player.controller.*;
import com.reign.gcld.common.log.*;

public class getTurnRewardRunner implements Runnable
{
    private Match match;
    private MatchFight matchFight;
    private static final Logger log;
    
    static {
        log = CommonLog.getLog(ResourceController.class);
    }
    
    public getTurnRewardRunner(final Match match, final MatchFight matchFight) {
        this.match = match;
        this.matchFight = matchFight;
    }
    
    @Override
    public void run() {
        try {
            if (this.match.getState() == 7) {
                return;
            }
            this.match.getTurnReward(this.matchFight);
        }
        catch (Exception e) {
            getTurnRewardRunner.log.error("getTurnRewardRunner Thread Error:", e);
        }
    }
}
