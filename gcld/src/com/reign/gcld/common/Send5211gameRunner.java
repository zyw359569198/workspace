package com.reign.gcld.common;

import com.reign.gcld.common.log.*;
import com.reign.gcld.log.*;
import com.reign.plugin.yx.action.*;
import com.reign.util.*;

public class Send5211gameRunner implements Runnable
{
    private static final Logger timerLog;
    private String userId;
    private int playerId;
    private String playerName;
    private int playerLv;
    private String yx;
    private int id;
    
    static {
        timerLog = new TimerLogger();
    }
    
    public Send5211gameRunner(final String userId, final int playerId, final String playerName, final int playerLv, final int id, final String yx) {
        this.userId = userId;
        this.playerId = playerId;
        this.playerName = playerName;
        this.playerLv = playerLv;
        this.yx = yx;
        this.id = id;
    }
    
    @Override
    public void run() {
        try {
            Yx5211gameOperationAction.callYx5211Game(this.userId, this.playerId, this.playerName, this.playerLv, this.id, this.yx);
        }
        catch (Exception e) {
            Send5211gameRunner.timerLog.error("Send5211gameRunner ", e);
            return;
        }
        finally {
            ThreadLocalFactory.clearTreadLocalLog();
        }
        ThreadLocalFactory.clearTreadLocalLog();
    }
}
