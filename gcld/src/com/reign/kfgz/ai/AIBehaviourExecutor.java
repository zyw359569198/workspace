package com.reign.kfgz.ai;

import com.reign.kfgz.ai.behaviour.*;
import java.util.concurrent.locks.*;
import com.reign.kf.match.log.*;
import java.util.concurrent.*;
import java.util.*;

public class AIBehaviourExecutor implements Runnable
{
    private static final Logger logger;
    private static Thread singleThread;
    private static final long sleepTime = 100L;
    private boolean mark;
    private LinkedList<Behaviour> taskList;
    private ReentrantLock lock;
    private static AIBehaviourExecutor self;
    private static ExecutorService ext;
    
    static {
        logger = CommonLog.getLog(AIBehaviourExecutor.class);
        AIBehaviourExecutor.singleThread = null;
        AIBehaviourExecutor.self = new AIBehaviourExecutor();
        AIBehaviourExecutor.ext = Executors.newFixedThreadPool(1);
    }
    
    private AIBehaviourExecutor() {
        this.mark = true;
        this.taskList = new LinkedList<Behaviour>();
        this.lock = new ReentrantLock();
    }
    
    public void addAITask(final Behaviour ai) {
        this.lock.lock();
        try {
            int i = 0;
            for (final Behaviour a : this.taskList) {
                if (a.getExecuteTime() > ai.getExecuteTime()) {
                    break;
                }
                ++i;
            }
            this.taskList.add(i, ai);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public static AIBehaviourExecutor getInstance() {
        return AIBehaviourExecutor.self;
    }
    
    @Override
    public void run() {
        while (this.mark) {
            try {
                Behaviour ai = this.taskList.peekFirst();
                if (ai != null && ai.getExecuteTime() <= System.currentTimeMillis()) {
                    this.lock.lock();
                    try {
                        ai = this.taskList.pollFirst();
                    }
                    finally {
                        this.lock.unlock();
                    }
                    this.lock.unlock();
                    if (ai == null) {
                        continue;
                    }
                    try {
                        AIBehaviourExecutor.ext.execute(ai);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else {
                    try {
                        Thread.sleep(100L);
                    }
                    catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
            }
            catch (Exception e3) {
                AIBehaviourExecutor.logger.error("ai error!!!", e3);
            }
        }
    }
    
    public void init() {
        AIBehaviourExecutor.logger.info("AIBehaviourExecutor init start...");
        if (AIBehaviourExecutor.singleThread != null) {
            return;
        }
        (AIBehaviourExecutor.singleThread = new Thread(this, "AIThread")).start();
        AIBehaviourExecutor.logger.info("AIBehaviourExecutor init end...");
    }
}
