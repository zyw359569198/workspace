package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestGotCoupon extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestGotCoupon(final String[] reqs) {
        super(reqs);
        this.identifier = "fanbeiquan";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestGotCoupon clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestGotCoupon)super.clone();
    }
}
