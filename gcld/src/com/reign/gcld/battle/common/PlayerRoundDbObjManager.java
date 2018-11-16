package com.reign.gcld.battle.common;

import com.reign.gcld.common.log.*;
import com.reign.gcld.common.*;
import java.util.concurrent.*;
import com.reign.gcld.log.*;

public class PlayerRoundDbObjManager extends Thread
{
    protected static final Logger timerLog;
    IDataGetter dataGetter;
    private static final PlayerRoundDbObjManager instance;
    private LinkedBlockingQueue<PlayerRoundDbObj> PlayerRoundDbObjQueue;
    public static final long SLEEP_TIME_GAP = 100L;
    
    static {
        timerLog = new TimerLogger();
        instance = new PlayerRoundDbObjManager("PlayerRoundDbObjManager");
    }
    
    private PlayerRoundDbObjManager(final String name) {
        super(name);
        this.dataGetter = null;
        this.PlayerRoundDbObjQueue = new LinkedBlockingQueue<PlayerRoundDbObj>();
        this.start();
    }
    
    public static PlayerRoundDbObjManager getInstance() {
        return PlayerRoundDbObjManager.instance;
    }
    
    public void addPlayerRoundDbObj(final PlayerRoundDbObj playerRoundDbObj) {
        this.PlayerRoundDbObjQueue.add(playerRoundDbObj);
    }
    
    public void ini(final IDataGetter dataGetter) {
        this.dataGetter = dataGetter;
    }
    
    @Override
    public void run() {
    }
}
