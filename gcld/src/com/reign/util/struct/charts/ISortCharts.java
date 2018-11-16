package com.reign.util.struct.charts;

import java.util.*;

public interface ISortCharts<T extends ISetSeqable<T>>
{
    void setNeedResetSeqWhenChange(final boolean p0);
    
    void add(final T p0);
    
    T getData(final int p0);
    
    T getData(final int p0, final boolean p1);
    
    T getDataAtSeq(final int p0);
    
    T getDataAtSeq(final int p0, final boolean p1);
    
    T getData(final int p0, final int p1);
    
    T getData(final int p0, final int p1, final boolean p2);
    
    boolean contains(final int p0);
    
    void remove(final int p0);
    
    void addOrChange(final T p0);
    
    List<T> inIterator();
    
    List<T> inIterator(final boolean p0);
    
    List<T> inIterator(final int p0);
    
    List<T> inIterator(final int p0, final boolean p1);
    
    List<T> inIterator(final int p0, final int p1);
    
    List<T> inIterator(final int p0, final int p1, final boolean p2);
    
    List<T> inIterator(final IChartsItemCondition<T> p0, final boolean p1);
    
    List<T> inIterator(final IChartsItemCondition<T> p0, final boolean p1, final boolean p2);
    
    void setSeq();
    
    void operateItem(final IChartsItemOperator<T> p0);
    
    int getSize();
}
