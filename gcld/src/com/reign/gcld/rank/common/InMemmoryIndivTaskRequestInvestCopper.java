package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestInvestCopper extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestInvestCopper(final String[] reqs) {
        super(reqs);
        this.identifier = "tzyb";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestInvestCopper clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestInvestCopper)super.clone();
    }
}
