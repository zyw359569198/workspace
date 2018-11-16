package com.reign.util.struct.charts;

public class ChartsItemOperatorSetSeq<T extends ISetSeqable<T>> implements IChartsItemOperator<T>
{
    private int seq;
    
    public ChartsItemOperatorSetSeq(final int startSeq) {
        this.seq = startSeq;
    }
    
    @Override
    public void operate(final T t) {
        t.setSeq(this.seq);
        ++this.seq;
    }
}
