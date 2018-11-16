package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestJifen extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestJifen(final String[] reqs) {
        super(reqs);
        this.identifier = "score";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestJifen clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestJifen)super.clone();
    }
}
