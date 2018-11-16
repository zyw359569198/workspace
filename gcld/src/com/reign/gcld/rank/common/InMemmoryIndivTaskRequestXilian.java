package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestXilian extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestXilian(final String[] reqs) {
        super(reqs);
        this.identifier = "wash";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestXilian clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestXilian)super.clone();
    }
}
