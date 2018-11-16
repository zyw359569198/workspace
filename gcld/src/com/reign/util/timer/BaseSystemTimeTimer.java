package com.reign.util.timer;

import java.util.concurrent.atomic.*;
import java.util.concurrent.*;
import java.util.*;

public class BaseSystemTimeTimer
{
    private LinkedList<BaseSystemTimeTimerTask> taskList;
    private ExecutorService executor;
    private Object lock;
    private Task task;
    private volatile boolean stop;
    private volatile boolean start;
    private static AtomicInteger id;
    
    static {
        BaseSystemTimeTimer.id = new AtomicInteger(1);
    }
    
    public BaseSystemTimeTimer() {
        this.taskList = new LinkedList<BaseSystemTimeTimerTask>();
        this.lock = new Object();
        this.executor = Executors.newFixedThreadPool(1);
        (this.task = new Task("timer-thread-" + BaseSystemTimeTimer.id.getAndIncrement())).start();
        this.start = true;
    }
    
    public BaseSystemTimeTimer(final int num) {
        this.taskList = new LinkedList<BaseSystemTimeTimerTask>();
        this.lock = new Object();
        this.executor = Executors.newFixedThreadPool(num);
        (this.task = new Task("timer-thread-" + BaseSystemTimeTimer.id.getAndIncrement())).start();
        this.start = true;
    }
    
    public BaseSystemTimeTimer(final int num, final boolean delay) {
        this.taskList = new LinkedList<BaseSystemTimeTimerTask>();
        this.lock = new Object();
        this.executor = Executors.newFixedThreadPool(num);
        this.task = new Task("timer-thread-" + BaseSystemTimeTimer.id.getAndIncrement());
        if (!delay) {
            this.task.start();
            this.start = true;
        }
    }
    
    public void start() {
        if (!this.start) {
            synchronized (this.lock) {
                if (!this.start) {
                    this.task.start();
                    this.start = true;
                }
            }
            // monitorexit(this.lock)
        }
    }
    
    public boolean cancel(final int taskId) {
        synchronized (this.lock) {
            int index = 0;
            int removeIndex = -1;
            for (final BaseSystemTimeTimerTask temp : this.taskList) {
                if (temp.getTaskId() == taskId) {
                    removeIndex = index;
                    break;
                }
                ++index;
            }
            if (-1 != removeIndex) {
                this.taskList.remove(removeIndex);
                // monitorexit(this.lock)
                return true;
            }
        }
        // monitorexit(this.lock)
        return false;
    }
    
    public void stop() {
        if (this.stop) {
            return;
        }
        synchronized (this.lock) {
            if (this.stop) {
                // monitorexit(this.lock)
                return;
            }
            this.taskList.clear();
            this.task.interrupt();
            this.stop = true;
        }
        // monitorexit(this.lock)
    }
    
    public void schedule(final BaseSystemTimeTimerTask task) {
        if (task.getTaskId() <= 0) {
            throw new RuntimeException("error task, except a none zore taskId");
        }
        synchronized (this.lock) {
            if (this.taskList.size() > 0) {
                final long executionTime = task.getExecutionTime();
                int index = 0;
                boolean isAdd = false;
                for (final BaseSystemTimeTimerTask temp : this.taskList) {
                    if (temp.getExecutionTime() > executionTime) {
                        this.taskList.add(index, task);
                        isAdd = true;
                        break;
                    }
                    ++index;
                }
                if (!isAdd) {
                    this.taskList.add(task);
                }
            }
            else {
                this.taskList.add(task);
            }
        }
        // monitorexit(this.lock)
    }
    
    private class Task extends Thread
    {
        public Task(final String threadName) {
            super(threadName);
        }
        
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    if (BaseSystemTimeTimer.this.taskList.size() > 0) {
                        synchronized (BaseSystemTimeTimer.this.lock) {
                            final long currentTime = System.currentTimeMillis();
                            while (BaseSystemTimeTimer.this.taskList.size() > 0) {
                                final BaseSystemTimeTimerTask task = BaseSystemTimeTimer.this.taskList.get(0);
                                if (task.getExecutionTime() > currentTime) {
                                    break;
                                }
                                BaseSystemTimeTimer.this.taskList.remove(0);
                                if (!task.canExecute()) {
                                    continue;
                                }
                                BaseSystemTimeTimer.this.executor.execute(task);
                            }
                        }
                        // monitorexit(BaseSystemTimeTimer.access$1(this.this$0))
                    }
                    Thread.sleep(300L);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
