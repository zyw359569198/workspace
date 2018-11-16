package com.reign.util.timer;

import java.util.concurrent.atomic.*;

public abstract class BaseSystemTimeTimerTask implements Runnable, Comparable<BaseSystemTimeTimerTask>
{
    protected static AtomicInteger idGenerator;
    private long executionTime;
    private int id;
    private volatile boolean cancel;
    private volatile boolean executed;
    
    static {
        BaseSystemTimeTimerTask.idGenerator = new AtomicInteger(0);
    }
    
    public BaseSystemTimeTimerTask(final long executionTime) {
        this.id = BaseSystemTimeTimerTask.idGenerator.incrementAndGet();
        this.executionTime = executionTime;
        this.cancel = false;
        this.executed = false;
    }
    
    public long getExecutionTime() {
        return this.executionTime;
    }
    
    public int getTaskId() {
        return this.id;
    }
    
    public boolean canExecute() {
        return !this.cancel && (this.executed = true);
    }
    
    public boolean cancel() {
        return !this.executed && (this.cancel = true);
    }
    
    @Override
    public int compareTo(final BaseSystemTimeTimerTask o) {
        if (this.executionTime == o.executionTime) {
            return 0;
        }
        if (this.executionTime > o.executionTime) {
            return 1;
        }
        return -1;
    }
}
