package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestZhancheng extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestZhancheng(final String[] reqs) {
        super(reqs);
        this.identifier = "zhancheng";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestZhancheng clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestZhancheng)super.clone();
    }
}
