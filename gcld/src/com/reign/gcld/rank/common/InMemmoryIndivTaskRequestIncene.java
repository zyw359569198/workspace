package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestIncene extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestIncene(final String[] reqs) {
        super(reqs);
        this.identifier = "jisiyb";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestIncene clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestIncene)super.clone();
    }
}
