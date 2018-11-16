package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestJiebing extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestJiebing(final String[] reqs) {
        super(reqs);
        this.identifier = "hy";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestJiebing clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestJiebing)super.clone();
    }
}
