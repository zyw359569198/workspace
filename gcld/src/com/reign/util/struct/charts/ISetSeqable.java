package com.reign.util.struct.charts;

public interface ISetSeqable<T> extends Comparable<T>
{
    void setSeq(final int p0);
    
    int getSeq();
    
    int getKey();
    
    T getClone();
}
