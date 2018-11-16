package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestDefeatN extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestDefeatN(final String[] reqs) {
        super(reqs);
        this.identifier = "win";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestDefeatN clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestDefeatN)super.clone();
    }
}
