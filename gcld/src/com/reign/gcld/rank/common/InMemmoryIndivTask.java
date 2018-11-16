package com.reign.gcld.rank.common;

public class InMemmoryIndivTask implements Cloneable
{
    public int id;
    public int taskType;
    public int indivTaskType;
    public int grade;
    public String name;
    public String intro;
    public InMemmoryIndivTaskRequest req;
    public InMemmoryIndivTaskReward reward;
    public boolean canUpdate;
    public boolean hasUpdate;
    public String pic;
    private InMemmoryIndivTaskDelegate delegate;
    
    public InMemmoryIndivTask() {
        this.canUpdate = false;
        this.hasUpdate = false;
    }
    
    public InMemmoryIndivTaskDelegate getDelegate() {
        if (this.delegate == null) {
            this.delegate = new InMemmoryIndivTaskDelegate(this);
        }
        return this.delegate;
    }
    
    public void setDelegate(final InMemmoryIndivTaskDelegate delegate) {
        this.delegate = delegate;
    }
    
    @Override
	public InMemmoryIndivTask clone() throws CloneNotSupportedException {
        final InMemmoryIndivTask task = (InMemmoryIndivTask)super.clone();
        if (this.req != null) {
            task.req = this.req.clone();
        }
        if (this.reward != null) {
            task.reward = this.reward.clone();
        }
        task.delegate = null;
        return task;
    }
}
