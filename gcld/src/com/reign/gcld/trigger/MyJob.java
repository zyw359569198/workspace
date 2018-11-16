package com.reign.gcld.trigger;

public class MyJob
{
    public String jobId;
    public String triggerId;
    public String jobName;
    public String className;
    public String methodName;
    public String cronExpression;
    
    public MyJob(final int jobId, final String jobName, final String className, final String methodName, final String cronExpression) {
        this.jobId = "QuartzJOB_" + jobId;
        this.triggerId = "QuartzTrigger_" + jobId;
        this.jobName = jobName;
        this.className = className;
        this.methodName = methodName;
        this.cronExpression = cronExpression;
    }
}
