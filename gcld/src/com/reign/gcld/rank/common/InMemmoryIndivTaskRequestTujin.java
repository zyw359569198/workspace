package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestTujin extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestTujin(final String[] reqs) {
        super(reqs);
        this.identifier = "tujin";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestTujin clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestTujin)super.clone();
    }
}
