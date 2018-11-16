package com.reign.gcld.battle.common;

import com.reign.gcld.common.log.*;
import com.reign.gcld.common.*;
import com.reign.gcld.battle.scene.*;
import java.util.concurrent.*;
import com.reign.util.*;
import com.reign.gcld.log.*;

public class BattleScheduler extends Thread
{
    private static final Logger timerLog;
    private static final Logger errorLog;
    private IDataGetter dataGetter;
    private static final BattleScheduler instance;
    private static final Logger dayReportLogger;
    private static final int BATTLE_SIZE = 1024;
    private static final int EXECUTORS_SIZE = 16;
    private final PriorityBlockingQueue<Battle> battlesMinHeap;
    private final ScheduledThreadPoolExecutor exeutors;
    private static final long SLEEP_GAP = 50L;
    
    static {
        timerLog = new TimerLogger();
        errorLog = new ErrorLogger();
        instance = new BattleScheduler();
        dayReportLogger = new DayReportLogger();
    }
    
    private BattleScheduler() {
        this.dataGetter = null;
        this.battlesMinHeap = new PriorityBlockingQueue<Battle>(1024);
        this.exeutors = new ScheduledThreadPoolExecutor(16);
    }
    
    public static BattleScheduler getInstance() {
        return BattleScheduler.instance;
    }
    
    public void startBattleScheduler(final IDataGetter dataGetter) {
        try {
            this.dataGetter = dataGetter;
            BattleScheduler.instance.start();
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("PhantomManager.startBattleScheduler catch Exception", e);
        }
    }
    
    public boolean addBattleToScheduler(final Battle battle) {
        try {
            if (battle == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("battle is null").appendClassName("BattleScheduler").appendMethodName("addBattleToScheduler").flush();
                return false;
            }
            final boolean succ = this.battlesMinHeap.add(battle);
            if (succ) {
                return true;
            }
            ErrorSceneLog.getInstance().appendErrorMsg("fail").appendBattleId(battle.getBattleId()).appendClassName("BattleScheduler").appendMethodName("addBattleToScheduler").flush();
            return false;
        }
        catch (Exception e) {
            BattleScheduler.errorLog.error("BattleScheduler.addBattleToScheduler catch Exception", e);
            return false;
        }
    }
    
    public boolean hasBattle(final Battle battle) {
        return this.battlesMinHeap.contains(battle);
    }
    
    public boolean removeBattle(final Battle battle) {
        return this.battlesMinHeap.remove(battle);
    }
    
    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    final long now = System.currentTimeMillis();
                    Battle battle = this.battlesMinHeap.peek();
                    if (battle == null || now < battle.getNextExeTime()) {
                        Thread.sleep(50L);
                    }
                    else {
                        battle = this.battlesMinHeap.poll();
                        this.exeutors.execute(new BattleRunner(battle));
                    }
                }
            }
            catch (Exception e) {
                BattleScheduler.errorLog.error("BattleScheduler.run catch Exception", e);
                try {
                    Thread.sleep(50L);
                }
                catch (InterruptedException e2) {
                    BattleScheduler.errorLog.error("BattleScheduler.Exception sleep catch InterruptedException", e2);
                }
                continue;
            }
            break;
        }
    }
    
    static class BattleRunner implements Runnable
    {
        Battle battle;
        
        public BattleRunner(final Battle battle) {
            this.battle = null;
            this.battle = battle;
        }
        
        @Override
        public void run() {
            try {
                final long start = System.currentTimeMillis();
                final BattleResultRound battleResultRound = this.battle.doBattle(BattleScheduler.getInstance().dataGetter, this.battle.getStartTime());
                for (final String log : ThreadLocalFactory.getTreadLocalLogs()) {
                    BattleScheduler.dayReportLogger.info(log);
                }
                int state = 2;
                if (battleResultRound != null && battleResultRound.ended) {
                    state = 3;
                }
                BattleScheduler.timerLog.debug(LogUtil.formatThreadLog("BattleScheduler", "run", state, System.currentTimeMillis() - start, "battleType#" + this.battle.getBattleType() + "#battleId#" + this.battle.getBattleId() + "#roundNum#" + this.battle.getRoundNum()));
            }
            catch (Exception e) {
                BattleScheduler.errorLog.error("BattleRunner.run catch Exception", e);
                return;
            }
            finally {
                ThreadLocalFactory.clearTreadLocalLog();
                ThreadLocalFactory.getTreadLocalLog();
            }
            ThreadLocalFactory.clearTreadLocalLog();
            ThreadLocalFactory.getTreadLocalLog();
        }
    }
}
