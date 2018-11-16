package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestZhuZhao extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestZhuZhao(final String[] reqs) {
        super(reqs);
        this.identifier = "build";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestZhuZhao clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestZhuZhao)super.clone();
    }
}
