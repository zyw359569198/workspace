package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestDantiao extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestDantiao(final String[] reqs) {
        super(reqs);
        this.identifier = "dantiao";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestDantiao clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestDantiao)super.clone();
    }
}
