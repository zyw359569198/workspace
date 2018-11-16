package com.reign.gcld.kfwd.common.runner;

import com.reign.gcld.kfwd.common.*;
import com.reign.gcld.player.controller.*;
import com.reign.gcld.common.log.*;

public class SendQueryMatchResultRequestRunner implements Runnable
{
    private Match match;
    private static final Logger log;
    
    static {
        log = CommonLog.getLog(ResourceController.class);
    }
    
    public SendQueryMatchResultRequestRunner(final Match match) {
        this.match = match;
    }
    
    @Override
    public void run() {
        try {
            if (this.match.getState() == 7) {
                return;
            }
            this.match.sendQueryResultList();
        }
        catch (Exception e) {
            SendQueryMatchResultRequestRunner.log.error("SendQueryMatchResultRequestRunner Thread Error: ", e);
        }
    }
}
