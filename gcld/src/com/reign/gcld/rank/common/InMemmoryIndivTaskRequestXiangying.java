package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestXiangying extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestXiangying(final String[] reqs) {
        super(reqs);
        this.identifier = "jinling";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestXiangying clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestXiangying)super.clone();
    }
}
