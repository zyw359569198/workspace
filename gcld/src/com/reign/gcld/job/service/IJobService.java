package com.reign.gcld.job.service;

public interface IJobService
{
    int addJob(final String p0, final String p1, final String p2, final long p3);
    
    int addJob(final String p0, final String p1, final String p2, final long p3, final boolean p4);
    
    boolean cancelJob(final Integer p0, final boolean p1);
    
    int reAddJob(final int p0, final String p1, final String p2, final String p3, final long p4);
    
    void InitExeJob();
    
    void initTimer();
    
    int addJob(final String p0, final String p1, final Object[] p2, final long p3);
}
