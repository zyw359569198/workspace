package com.reign.gcld.common;

import com.reign.gcld.common.log.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.log.*;
import com.reign.plugin.yx.action.*;
import com.reign.util.*;

public class SendYx360Runner implements Runnable
{
    private static final Logger timerLog;
    private Request request;
    private String userId;
    private int playerId;
    private String serverId;
    private String gKey;
    private int playerLv;
    private String playerName;
    
    static {
        timerLog = new TimerLogger();
    }
    
    public SendYx360Runner(final Request request, final String userId, final int playerId, final String serverId, final String gKey, final int playerLv, final String playerName) {
        this.request = request;
        this.userId = userId;
        this.playerId = playerId;
        this.serverId = serverId;
        this.gKey = gKey;
        this.playerLv = playerLv;
        this.playerName = playerName;
    }
    
    @Override
    public void run() {
        try {
            Yx360OperationAction.pushPlayerInfo(this.request, this.userId, this.playerId, this.serverId, this.gKey, this.playerLv, this.playerName);
        }
        catch (Exception e) {
            SendYx360Runner.timerLog.error("SendRunner ", e);
            return;
        }
        finally {
            ThreadLocalFactory.clearTreadLocalLog();
        }
        ThreadLocalFactory.clearTreadLocalLog();
    }
}
