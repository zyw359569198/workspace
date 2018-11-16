package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestWeijiao extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestWeijiao(final String[] reqs) {
        super(reqs);
        this.identifier = "killnpc";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestWeijiao clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestWeijiao)super.clone();
    }
}
