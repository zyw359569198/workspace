package com.reign.gcld.common;

import com.reign.gcld.job.service.*;

public class AddJob
{
    private IJobService jobService;
    private static AddJob addJobService;
    
    public static AddJob getInstance() {
        if (AddJob.addJobService == null) {
            AddJob.addJobService = new AddJob();
        }
        return AddJob.addJobService;
    }
    
    public void init(final IJobService jobService) {
        getInstance().jobService = jobService;
    }
    
    public void addJob(final JobClassMethondEnum info, final String params, final long time) {
        this.jobService.addJob(info.getClassName(), info.getMethodName(), params, System.currentTimeMillis() + time, false);
    }
}
