package com.reign.gcld.kfwd.common.runner;

import com.reign.gcld.kfwd.common.*;
import com.reign.gcld.player.controller.*;
import com.reign.gcld.common.log.*;

public class SendQueryMatchRankRequestRunner implements Runnable
{
    private Match match;
    private int turn;
    private static final Logger log;
    
    static {
        log = CommonLog.getLog(ResourceController.class);
    }
    
    public SendQueryMatchRankRequestRunner(final Match match, final int turn) {
        this.match = match;
        this.turn = turn;
    }
    
    @Override
    public void run() {
        try {
            if (this.match.getState() == 7) {
                return;
            }
            if (this.turn >= this.match.getTurn() - 1) {
                this.match.sendQueryRankList(this.turn);
            }
        }
        catch (Exception e) {
            SendQueryMatchRankRequestRunner.log.error("SendQueryMatchRankRequestRunner Thread Error:", e);
        }
    }
}
