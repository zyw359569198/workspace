package com.reign.gcld.timer;

import com.reign.gcld.common.log.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.*;
import com.reign.gcld.timer.domain.*;
import java.util.*;
import com.reign.gcld.log.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import java.lang.reflect.*;

public class Timer
{
    private static final Logger timerLog;
    private static final Logger dayReportLogger;
    private final LinkedList<TimerTask> taskList;
    private final ReentrantLock lock;
    private Executor executor;
    private TimerContext context;
    private TimerDBManager manager;
    private int maxJobId;
    
    static {
        timerLog = new TimerLogger();
        dayReportLogger = new DayReportLogger();
    }
    
    public Timer(final TimerContext context, final TimerDBManager manager, final int num) {
        this.taskList = new LinkedList<TimerTask>();
        this.lock = new ReentrantLock();
        this.context = context;
        this.manager = manager;
        this.executor = Executors.newFixedThreadPool(num);
        this.restoreMaxTaskId();
        final TimerRunner runner = new TimerRunner((TimerRunner)null);
        runner.start();
    }
    
    private void restoreMaxTaskId() {
        try {
            this.lock.lock();
            this.maxJobId = this.manager.getMaxJobId();
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    public void copyProperties(final PlayerJob task, final TimerTask timerTask) {
        timerTask.setTaskId(task.getId());
        timerTask.setParams(task.getParams());
        timerTask.setDone(task.getState() == 1);
        timerTask.setExecutionTime(task.getExecutionTime());
        timerTask.setClassName(task.getClassName());
        timerTask.setMethodName(task.getMethodName());
        timerTask.setSync(true);
        timerTask.setCancelled(false);
    }
    
    public int addTask(final TimerTask task, final boolean isSync) {
        try {
            this.lock.lock();
            if (isSync) {
                task.setTaskId(++this.maxJobId);
            }
            if (this.taskList.size() > 0) {
                int index = 0;
                final long executionTime = task.getExecutionTime();
                for (final TimerTask tt : this.taskList) {
                    if (tt.getExecutionTime() > executionTime) {
                        this.taskList.add(index, task);
                        if (isSync) {
                            this.manager.add(task);
                            return this.maxJobId;
                        }
                        return task.getTaskId();
                    }
                    else {
                        ++index;
                    }
                }
                this.taskList.add(task);
            }
            else {
                this.taskList.add(task);
            }
            if (isSync) {
                this.manager.add(task);
                return this.maxJobId;
            }
            return task.getTaskId();
        }
        finally {
            this.lock.unlock();
        }
    }
    
    public boolean cancelTask(final int taskId, final boolean delDb) {
        try {
            this.lock.lock();
            int index = 0;
            for (final TimerTask tt : this.taskList) {
                if (tt.getTaskId() == taskId) {
                    break;
                }
                ++index;
            }
            if (index <= this.taskList.size() - 1) {
                final TimerTask cacelledTask = this.taskList.remove(index);
                if (delDb) {
                    cacelledTask.setCancelled(true);
                    this.manager.add(cacelledTask);
                }
                return true;
            }
            return false;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    private class TimerRunner extends Thread
    {
        @Override
        public void run() {
            final long start = System.currentTimeMillis();
            while (!Thread.interrupted()) {
                try {
                    Label_0161: {
                        if (Timer.this.taskList.size() > 0) {
                            try {
                                Timer.this.lock.lock();
                                while (Timer.this.taskList.size() > 0) {
                                    final TimerTask task = Timer.this.taskList.get(0);
                                    if (task.getExecutionTime() > System.currentTimeMillis()) {
                                        break;
                                    }
                                    Timer.this.taskList.remove(0);
                                    Timer.this.executor.execute(new TimerTaskRunner(task));
                                }
                            }
                            catch (Exception e) {
                                Timer.timerLog.error("TimerRunner has a exception ", e);
                                break Label_0161;
                            }
                            finally {
                                Timer.this.lock.unlock();
                            }
                            Timer.this.lock.unlock();
                        }
                    }
                    Thread.sleep(300L);
                }
                catch (InterruptedException e2) {
                    Timer.timerLog.error("TimerRunner has a exception ", e2);
                }
            }
            Timer.timerLog.info(LogUtil.formatThreadLog("TimerRunner", "run", 2, System.currentTimeMillis() - start, "param:"));
        }
    }
    
    private class TimerTaskRunner implements Runnable
    {
        private TimerTask task;
        
        public TimerTaskRunner(final TimerTask task) {
            this.task = task;
        }
        
        @Override
        public void run() {
            try {
                final long start = System.currentTimeMillis();
                final Object obj = Timer.this.context.getBean(this.task.getExecutor());
                final Method method = ClassUtil.getMethod(obj.getClass(), this.task.getExecutorMethod(), ClassUtil.getParameterTypes(this.task.getParams()));
                final Object result = method.invoke(obj, this.task.getParams());
                if (result != null && result instanceof CallBack) {
                    final CallBack callback = (CallBack)result;
                    callback.call();
                }
                this.task.setDone(true);
                Timer.this.manager.add(this.task);
                for (final String log : ThreadLocalFactory.getTreadLocalLogs()) {
                    Timer.dayReportLogger.info(log);
                }
                final long exeTime = System.currentTimeMillis() - start;
                if (exeTime > 200L) {
                    Timer.timerLog.info(LogUtil.formatThreadLog("TimerTaskRunner", "run", 2, exeTime, String.valueOf(this.task.getClassName()) + "#" + this.task.getMethodName() + "#" + this.task.getParams()));
                }
            }
            catch (IllegalArgumentException e) {
                Timer.timerLog.error("TimerTaskRunner has a exception ", e);
            }
            catch (IllegalAccessException e2) {
                Timer.timerLog.error("TimerTaskRunner has a exception ", e2);
            }
            catch (InvocationTargetException e3) {
                Timer.timerLog.error("TimerTaskRunner has a exception ", e3.getTargetException());
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
