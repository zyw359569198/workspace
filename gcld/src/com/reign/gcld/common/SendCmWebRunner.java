package com.reign.gcld.common;

import com.reign.gcld.common.log.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.log.*;
import com.reign.plugin.yx.action.*;
import com.reign.util.*;

public class SendCmWebRunner implements Runnable
{
    private static final Logger timerLog;
    private Request request;
    private String userId;
    private String yxSource;
    private String yx;
    private String currentServerId;
    
    static {
        timerLog = new TimerLogger();
    }
    
    public SendCmWebRunner(final Request request, final String userId, final String yxSource, final String yx, final String currentServerId) {
        this.request = request;
        this.userId = userId;
        this.yxSource = yxSource;
        this.yx = yx;
        this.currentServerId = currentServerId;
    }
    
    @Override
    public void run() {
        try {
            YxCmwebgameOperationAction.callYxCm(this.request, this.userId, this.yxSource, this.yx, this.currentServerId);
        }
        catch (Exception e) {
            SendCmWebRunner.timerLog.error("SendRunner ", e);
            return;
        }
        finally {
            ThreadLocalFactory.clearTreadLocalLog();
        }
        ThreadLocalFactory.clearTreadLocalLog();
    }
}
