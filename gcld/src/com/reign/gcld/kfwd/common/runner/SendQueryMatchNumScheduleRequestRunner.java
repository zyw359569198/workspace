package com.reign.gcld.kfwd.common.runner;

import com.reign.gcld.kfwd.common.*;
import com.reign.gcld.player.controller.*;
import com.reign.gcld.common.log.*;

public class SendQueryMatchNumScheduleRequestRunner implements Runnable
{
    private Match match;
    private MatchFight matchFight;
    private static final Logger log;
    
    static {
        log = CommonLog.getLog(ResourceController.class);
    }
    
    public SendQueryMatchNumScheduleRequestRunner(final Match match, final MatchFight matchFight) {
        this.match = match;
        this.matchFight = matchFight;
    }
    
    @Override
    public void run() {
        try {
            if (this.match.getState() == 7) {
                return;
            }
            this.match.sendQueryMatchNumScheduleRequest(this.matchFight);
        }
        catch (Exception e) {
            SendQueryMatchNumScheduleRequestRunner.log.error("SendQueryMatchNumScheduleRequestRunner Thread Error:", e);
        }
    }
}
