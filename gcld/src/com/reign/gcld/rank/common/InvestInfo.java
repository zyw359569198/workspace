package com.reign.gcld.rank.common;

public class InvestInfo implements Comparable<InvestInfo>
{
    public int forceId;
    public long investNum;
    public long updateTime;
    public String name;
    
    public InvestInfo(final int i) {
        this.forceId = i;
        this.investNum = 0L;
        this.updateTime = 0L;
        this.name = "";
    }
    
    @Override
    public int compareTo(final InvestInfo o) {
        return (o == null || (this.investNum <= o.investNum && (this.investNum != o.investNum || this.updateTime >= o.updateTime))) ? 1 : 0;
    }
}
