package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestZhengbing extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestZhengbing(final String[] reqs) {
        super(reqs);
        this.identifier = "mubing";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestZhengbing clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestZhengbing)super.clone();
    }
}
