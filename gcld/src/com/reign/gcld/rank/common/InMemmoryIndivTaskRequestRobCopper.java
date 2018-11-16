package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestRobCopper extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestRobCopper(final String[] reqs) {
        super(reqs);
        this.identifier = "killyb";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestRobCopper clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestRobCopper)super.clone();
    }
}
