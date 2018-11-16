package com.reign.gcld.kfwd.common.runner;

import com.reign.gcld.kfwd.common.*;
import com.reign.gcld.player.controller.*;
import com.reign.gcld.common.log.*;

public class SendQueryMatchReportRequestRunner implements Runnable
{
    private Match match;
    private MatchFight matchFight;
    private static final Logger log;
    
    static {
        log = CommonLog.getLog(ResourceController.class);
    }
    
    public SendQueryMatchReportRequestRunner(final Match match, final MatchFight matchFight) {
        this.match = match;
        this.matchFight = matchFight;
    }
    
    @Override
    public void run() {
        try {
            if (this.match.getState() == 7) {
                return;
            }
            this.match.sendQueryMatchReportRequest(this.matchFight);
        }
        catch (Exception e) {
            SendQueryMatchReportRequestRunner.log.error("SendQueryMatchReportRequestRunner", e);
        }
    }
}
