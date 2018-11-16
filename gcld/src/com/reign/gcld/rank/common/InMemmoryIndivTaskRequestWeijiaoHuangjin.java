package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestWeijiaoHuangjin extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestWeijiaoHuangjin(final String[] reqs) {
        super(reqs);
        this.identifier = "killhj";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestWeijiaoHuangjin clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestWeijiaoHuangjin)super.clone();
    }
}
