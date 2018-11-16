package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestCrazyInvest extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestCrazyInvest(final String[] reqs) {
        super(reqs);
        this.identifier = "tznum";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestCrazyInvest clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestCrazyInvest)super.clone();
    }
}
