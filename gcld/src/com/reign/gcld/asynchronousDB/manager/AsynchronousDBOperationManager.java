package com.reign.gcld.asynchronousDB.manager;

import com.reign.gcld.common.log.*;
import com.reign.gcld.common.*;
import com.reign.gcld.asynchronousDB.operation.*;
import com.reign.gcld.log.*;
import com.reign.gcld.battle.common.*;
import com.reign.gcld.asynchronousDB.operation.basic.*;
import java.util.*;
import com.reign.gcld.asynchronousDB.operation.wrapper.*;
import com.reign.util.*;

public class AsynchronousDBOperationManager
{
    public static final AsynchronousDBOperationManager instance;
    private static final Logger timerLog;
    private static final Logger erroLogger;
    private static final Logger dayReportLogger;
    private static final int MAX_RETRY_TIMES = 3;
    private IDataGetter dataGetter;
    private List<IAsynchronousDBOperation> waitQueue;
    private List<IAsynchronousDBOperation> executeQueue;
    private OperateThread thread;
    
    static {
        instance = new AsynchronousDBOperationManager();
        timerLog = new TimerLogger();
        erroLogger = new ErrorLogger();
        dayReportLogger = new DayReportLogger();
    }
    
    private AsynchronousDBOperationManager() {
        this.waitQueue = new ArrayList<IAsynchronousDBOperation>();
        this.executeQueue = new ArrayList<IAsynchronousDBOperation>();
        this.thread = null;
    }
    
    public static AsynchronousDBOperationManager getInstance() {
        return AsynchronousDBOperationManager.instance;
    }
    
    public void init(final IDataGetter dataGetter) {
        this.dataGetter = dataGetter;
        (this.thread = new OperateThread()).setDaemon(true);
        this.thread.start();
    }
    
    public static IAsynchronousDBOperation BattleDropToDBOperation(final int playerId, final BattleDrop battleDrop, final String reason) {
        try {
            IAsynchronousDBOperation resultOperation = null;
            if (battleDrop == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("battleDrop is null.").appendPlayerId(playerId).append("reason", reason).appendClassName("AsynchronousDBOperationManager").appendMethodName("BattleDropToDBOperation").flush();
                return null;
            }
            if (battleDrop.num == 0) {
                return null;
            }
            switch (battleDrop.type) {
                case 1:
                case 1001: {
                    resultOperation = new AsynchronousDBOperationAddCopper(playerId, battleDrop.num, reason);
                    break;
                }
                case 2:
                case 1002: {
                    resultOperation = new AsynchronousDBOperationAddWood(playerId, battleDrop.num, reason);
                    break;
                }
                case 3:
                case 1003: {
                    resultOperation = new AsynchronousDBOperationAddFood(playerId, battleDrop.num, reason);
                    break;
                }
                case 4:
                case 1004: {
                    resultOperation = new AsynchronousDBOperationAddIron(playerId, battleDrop.num, reason);
                    break;
                }
                case 5:
                case 1005: {
                    resultOperation = new AsynchronousDBOperationAddChiefExp(playerId, battleDrop.num, reason);
                    break;
                }
                case 7:
                case 1007: {
                    resultOperation = new AsynchronousDBOperationAddGem(playerId, battleDrop.id, battleDrop.num, reason);
                    break;
                }
                case 23:
                case 1023: {
                    resultOperation = new AsynchronousDBOperationAddTouZiDoubleTicket(playerId, battleDrop.num, reason);
                    break;
                }
                case 30:
                case 1030: {
                    resultOperation = new AsynchronousDBOperationAddMoonCake(playerId, battleDrop.num, reason);
                    break;
                }
                case 31:
                case 1031: {
                    resultOperation = new AsynchronousDBOperationAddBaoMa(playerId, battleDrop.num, reason);
                    break;
                }
                case 32:
                case 1032: {
                    resultOperation = new AsynchronousDBOperationAddMeiJiu(playerId, battleDrop.num, reason);
                    break;
                }
                case 33:
                case 1033: {
                    resultOperation = new AsynchronousDBOperationAddShuHua(playerId, battleDrop.num, reason);
                    break;
                }
                case 34:
                case 1034: {
                    resultOperation = new AsynchronousDBOperationAddIronTicket(playerId, battleDrop.num, reason);
                    break;
                }
                case 35:
                case 1035: {
                    resultOperation = new AsynchronousDBOperationAddGiftBox(playerId, battleDrop.num, reason);
                    break;
                }
                case 36:
                case 1036: {
                    resultOperation = new AsynchronousDBOperationAddBall(playerId, battleDrop.num, reason);
                    break;
                }
                case 37:
                case 1037: {
                    resultOperation = new AsynchronousDBOperationAddSnow(playerId, battleDrop.num, reason);
                    break;
                }
                case 38:
                case 1038: {
                    resultOperation = new AsynchronousDBOperationAddBaoZhu(playerId, battleDrop.num, reason);
                    break;
                }
                case 42: {
                    resultOperation = new AsynchronousDBOperationAddMuBingLing(playerId, battleDrop.num, reason);
                    break;
                }
            }
            if (resultOperation == null) {
                ErrorSceneLog.getInstance().appendErrorMsg("battleDrop.type is invalid.").append("battleDrop.type", battleDrop.type).appendPlayerId(playerId).append("reason", reason).appendClassName("AsynchronousDBOperationManager").appendMethodName("BattleDropToDBOperation").flush();
                return null;
            }
            return resultOperation;
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleScheduler.addBattleToScheduler catch Exception", e);
            return null;
        }
    }
    
    public void addBattleDropMapRetry(final int playerId, final Map<Integer, BattleDrop> dropMap, final String reason) {
        try {
            for (final BattleDrop drop : dropMap.values()) {
                final IAsynchronousDBOperation operation = BattleDropToDBOperation(playerId, drop, reason);
                if (operation == null) {
                    return;
                }
                this.addDBRetryOperation(operation);
            }
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleScheduler.addBattleDropMapRetry catch Exception", e);
        }
    }
    
    public void addBattleDropRetry(final int playerId, final BattleDrop drop, final String reason) {
        try {
            final IAsynchronousDBOperation operation = BattleDropToDBOperation(playerId, drop, reason);
            if (operation == null) {
                return;
            }
            this.addDBRetryOperation(operation);
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleScheduler.addBattleDropRetry catch Exception", e);
        }
    }
    
    public void addDBOperation(IAsynchronousDBOperation operation) {
        try {
            operation = new AsynchronousDBOperationWrapper(operation);
            synchronized (AsynchronousDBOperationManager.instance) {
                this.waitQueue.add(operation);
                operation.handleWhenAddQueue();
            }
            // monitorexit(AsynchronousDBOperationManager.instance)
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleScheduler.addDBOperation catch Exception", e);
        }
    }
    
    public void addDBOperationNoLog(final IAsynchronousDBOperation operation) {
        try {
            synchronized (AsynchronousDBOperationManager.instance) {
                this.waitQueue.add(operation);
                operation.handleWhenAddQueue();
            }
            // monitorexit(AsynchronousDBOperationManager.instance)
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleScheduler.addDBOperationNoLog catch Exception", e);
        }
    }
    
    public void addDBOperationTrans(IAsynchronousDBOperation operation) {
        try {
            operation = new AsynchronousDBOperationWrapperTrans(operation);
            synchronized (AsynchronousDBOperationManager.instance) {
                this.waitQueue.add(operation);
                operation.handleWhenAddQueue();
            }
            // monitorexit(AsynchronousDBOperationManager.instance)
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleScheduler.addDBOperationTrans catch Exception", e);
        }
    }
    
    public void addDBRetryOperation(IAsynchronousDBOperation operation) {
        try {
            operation = new AsynchronousDBOperationWrapperRetry(3, operation);
            synchronized (AsynchronousDBOperationManager.instance) {
                this.waitQueue.add(operation);
                operation.handleWhenAddQueue();
            }
            // monitorexit(AsynchronousDBOperationManager.instance)
        }
        catch (Exception e) {
            ErrorSceneLog.getInstance().error("BattleScheduler.addDBRetryOperation catch Exception", e);
        }
    }
    
    private void exchangeQueue() {
        synchronized (AsynchronousDBOperationManager.instance) {
            final List<IAsynchronousDBOperation> temp = this.executeQueue;
            this.executeQueue = this.waitQueue;
            this.waitQueue = temp;
        }
        // monitorexit(AsynchronousDBOperationManager.instance)
    }
    
    private class OperateThread extends Thread
    {
        private static final long MIN_INTERVAL = 10L;
        
        public OperateThread() {
            super("asynchronous-DB-Operation-thread");
        }
        
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    final long start = System.currentTimeMillis();
                    AsynchronousDBOperationManager.this.exchangeQueue();
                    final int queSize = AsynchronousDBOperationManager.this.executeQueue.size();
                    if (queSize == 0) {
                        Thread.sleep(this.getSleepMilsec());
                    }
                    for (final IAsynchronousDBOperation operation : AsynchronousDBOperationManager.this.executeQueue) {
                        try {
                            operation.handleOperation(AsynchronousDBOperationManager.this.dataGetter);
                            operation.handleWhenExecuteSuccess();
                            try {
                                for (final String log : ThreadLocalFactory.getTreadLocalLogs()) {
                                    AsynchronousDBOperationManager.dayReportLogger.info(log);
                                }
                                ThreadLocalFactory.clearTreadLocalLog();
                            }
                            catch (Exception e) {
                                AsynchronousDBOperationManager.erroLogger.error("dayReport error.", e);
                            }
                        }
                        catch (Exception e) {
                            AsynchronousDBOperationManager.erroLogger.error("handleOperation error.", e);
                            operation.handleWhenExecuteException();
                        }
                    }
                    AsynchronousDBOperationManager.this.executeQueue.clear();
                }
                catch (Exception e2) {
                    AsynchronousDBOperationManager.erroLogger.error("asynchronous db operation thread loop error", e2);
                    ThreadLocalFactory.clearTreadLocalLog();
                    try {
                        Thread.sleep(this.getSleepMilsec());
                    }
                    catch (Throwable e3) {
                        AsynchronousDBOperationManager.erroLogger.error("asynchronous db operation thread sleep error", e3);
                    }
                    continue;
                }
                finally {
                    ThreadLocalFactory.clearTreadLocalLog();
                    try {
                        Thread.sleep(this.getSleepMilsec());
                    }
                    catch (Throwable e3) {
                        AsynchronousDBOperationManager.erroLogger.error("asynchronous db operation thread sleep error", e3);
                    }
                }
                ThreadLocalFactory.clearTreadLocalLog();
                try {
                    Thread.sleep(this.getSleepMilsec());
                }
                catch (Throwable e3) {
                    AsynchronousDBOperationManager.erroLogger.error("asynchronous db operation thread sleep error", e3);
                }
            }
        }
        
        private long getSleepMilsec() {
            return 10L;
        }
    }
}
