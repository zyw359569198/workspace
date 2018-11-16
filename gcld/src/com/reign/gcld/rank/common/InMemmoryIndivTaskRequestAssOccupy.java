package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestAssOccupy extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestAssOccupy(final String[] reqs) {
        super(reqs);
        this.identifier = "zhugong";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestAssOccupy clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestAssOccupy)super.clone();
    }
}
