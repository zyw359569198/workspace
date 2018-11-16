package com.reign.gcld.job.service;

import org.springframework.beans.factory.*;
import org.springframework.stereotype.*;
import com.reign.gcld.timer.dao.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.gcld.timer.domain.*;
import java.util.*;
import com.reign.gcld.timer.*;

@Component("jobService")
public class JobService implements IJobService, InitializingBean
{
    @Autowired
    private IPlayerJobDao playerJobDao;
    @Autowired
    private TimerContext timerContext;
    private Timer timer;
    
    @Override
	public void afterPropertiesSet() throws Exception {
    }
    
    @Override
    public void InitExeJob() {
        final List<PlayerJob> jList = this.playerJobDao.getJobListByState(0);
        for (int i = 0; i < jList.size(); ++i) {
            final PlayerJob pj = jList.get(i);
            this.timer.addTask(new TimerTask(pj.getId(), pj.getExecutionTime(), pj.getClassName(), pj.getMethodName(), pj.getParams(), true), false);
        }
    }
    
    @Override
    public void initTimer() {
        this.timer = new Timer(this.timerContext, TimerDBManager.getInstance(this.playerJobDao), 15);
    }
    
    @Override
    public int addJob(final String className, final String methodName, final String params, final long executionTime, final boolean saveDb) {
        if (saveDb) {
            return this.timer.addTask(new TimerTask(executionTime, className, methodName, params), true);
        }
        return this.timer.addTask(new TimerTask(-1, executionTime, className, methodName, params, true), false);
    }
    
    @Override
    public int addJob(final String className, final String methodName, final Object[] params, final long executionTime) {
        return this.timer.addTask(new TimerObjectTask(executionTime, className, methodName, params), false);
    }
    
    @Override
    public int reAddJob(final int taskId, final String className, final String methodName, final String params, final long executionTime) {
        return this.timer.addTask(new TimerTask(taskId, executionTime, className, methodName, params, true), false);
    }
    
    @Override
    public int addJob(final String className, final String methodName, final String params, final long executionTime) {
        return this.timer.addTask(new TimerTask(executionTime, className, methodName, params), true);
    }
    
    @Override
    public boolean cancelJob(final Integer jobId, final boolean delDb) {
        return this.timer.cancelTask(jobId, delDb);
    }
}
