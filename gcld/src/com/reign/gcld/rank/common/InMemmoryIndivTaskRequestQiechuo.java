package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestQiechuo extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestQiechuo(final String[] reqs) {
        super(reqs);
        this.identifier = "challenge";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestQiechuo clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestQiechuo)super.clone();
    }
}
