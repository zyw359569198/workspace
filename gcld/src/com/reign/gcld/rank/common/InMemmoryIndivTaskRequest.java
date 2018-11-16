package com.reign.gcld.rank.common;

import com.reign.gcld.common.*;

public class InMemmoryIndivTaskRequest implements Cloneable
{
    protected String identifier;
    protected boolean isFinished;
    protected int hasRewarded;
    
    public InMemmoryIndivTaskRequest() {
        this.identifier = "default";
        this.isFinished = false;
        this.hasRewarded = 0;
    }
    
    public boolean handleMessage(final InMemmoryIndivTaskMessage message, final IDataGetter getter, final InMemmoryIndivTask task) {
        return false;
    }
    
    @Override
    protected InMemmoryIndivTaskRequest clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequest)super.clone();
    }
    
    public boolean isConcerned(final InMemmoryIndivTaskMessage message) {
        final String identifier = message.identifier;
        return this.identifier.equalsIgnoreCase(identifier) && !this.isFinished;
    }
    
    public void updateDbInfo(final IDataGetter getter, final InMemmoryIndivTask task, final InMemmoryIndivTaskMessage message) {
    }
    
    public String currentReqInfo(final InMemmoryIndivTask inTask) {
        return "";
    }
    
    public MultiResult getProcessInfo() {
        return null;
    }
    
    public void restore(final int count, final int hasRewarded) {
    }
    
    public boolean isFinished() {
        return this.isFinished;
    }
    
    public void setFinished(final boolean isFinished) {
        this.isFinished = isFinished;
    }
    
    public int getHasRewarded() {
        return this.hasRewarded;
    }
    
    public void setHasRewarded(final int hasRewarded) {
        this.hasRewarded = hasRewarded;
    }
}
