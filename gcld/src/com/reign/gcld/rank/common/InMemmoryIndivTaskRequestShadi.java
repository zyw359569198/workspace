package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestShadi extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestShadi(final String[] reqs) {
        super(reqs);
        this.identifier = "kill";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestShadi clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestShadi)super.clone();
    }
}
