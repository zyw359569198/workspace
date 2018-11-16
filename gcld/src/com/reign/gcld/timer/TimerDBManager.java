package com.reign.gcld.timer;

import com.reign.gcld.timer.dao.*;
import com.reign.gcld.player.controller.*;
import com.reign.gcld.common.log.*;
import java.util.concurrent.*;
import com.reign.gcld.timer.domain.*;
import java.util.*;

public class TimerDBManager
{
    private static final Logger log;
    private final BlockingQueue<TimerTask> queue;
    private IPlayerJobDao playerJobDao;
    
    static {
        log = CommonLog.getLog(ResourceController.class);
    }
    
    private TimerDBManager(final IPlayerJobDao playerJobDao) {
        this.queue = new LinkedBlockingQueue<TimerTask>();
        this.playerJobDao = playerJobDao;
        final SyncThread thread = new SyncThread((SyncThread)null);
        thread.start();
    }
    
    public static TimerDBManager getInstance(final IPlayerJobDao dao) {
        return new TimerDBManager(dao);
    }
    
    public void add(final TimerTask task) {
        this.queue.offer(task);
    }
    
    public int getMaxJobId() {
        return this.playerJobDao.getMaxJobId();
    }
    
    public List<PlayerJob> getAllUnexecJobs() {
        return this.playerJobDao.getJobListByState(0);
    }
    
    private class SyncThread extends Thread
    {
        @Override
        public void run() {
            while (!Thread.interrupted()) {
                try {
                    final Set<TimerTask> set = new HashSet<TimerTask>();
                    TimerTask task = null;
                    while ((task = TimerDBManager.this.queue.poll()) != null) {
                        set.add(task);
                    }
                    for (final TimerTask t : set) {
                        if (!t.isSync()) {
                            final PlayerJob temp = new PlayerJob();
                            this.copyProperties(t, temp);
                            if (!t.isCancelled() && !t.isDone()) {
                                TimerDBManager.this.playerJobDao.create(temp);
                                t.setTaskId(temp.getId());
                            }
                        }
                        else if (t.isDone() || t.isCancelled()) {
                            if (t.getTaskId() <= 0) {
                                continue;
                            }
                            TimerDBManager.this.playerJobDao.deleteById(t.getTaskId());
                        }
                        t.setSync(true);
                    }
                    Thread.sleep(300L);
                }
                catch (InterruptedException e) {
                    TimerDBManager.log.error("SyncThread Error ", e);
                }
                catch (Exception e2) {
                    TimerDBManager.log.error("SyncThread Error2 ", e2);
                }
            }
        }
        
        private void copyProperties(final TimerTask timerTask, final PlayerJob task) {
            task.setClassName(timerTask.getExecutor());
            task.setMethodName(timerTask.getExecutorMethod());
            task.setParams(timerTask.getParams());
            task.setState(timerTask.isDone() ? 1 : 0);
            if (timerTask.getTaskId() != -1) {
                task.setId(timerTask.getTaskId());
            }
            task.setExecutionTime(timerTask.getExecutionTime());
        }
    }
}
