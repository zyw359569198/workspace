package com.reign.gcld.rank.common;

public class InMemmoryIndivTaskRequestKillBar extends InMemmoryIndivTaskRequestCount implements Cloneable
{
    public InMemmoryIndivTaskRequestKillBar(final String[] reqs) {
        super(reqs);
        this.identifier = "killmz";
    }
    
    @Override
    protected InMemmoryIndivTaskRequestKillBar clone() throws CloneNotSupportedException {
        return (InMemmoryIndivTaskRequestKillBar)super.clone();
    }
}
