package com.reign.util.struct.charts;

import java.util.*;

public class SyncSortCharts<T extends ISetSeqable<T>> implements ISortCharts<T>
{
    private ISortCharts<T> charts;
    
    public SyncSortCharts(final ISortCharts<T> charts) {
        this.charts = charts;
    }
    
    @Override
    public void setNeedResetSeqWhenChange(final boolean needResetSeqWhenChange) {
        synchronized (this) {
            this.charts.setNeedResetSeqWhenChange(needResetSeqWhenChange);
        }
    }
    
    @Override
    public void add(final T data) {
        synchronized (this) {
            this.charts.add(data);
        }
    }
    
    @Override
    public T getData(final int key) {
        synchronized (this) {
            return this.charts.getData(key);
        }
    }
    
    @Override
    public boolean contains(final int key) {
        synchronized (this) {
            return this.charts.contains(key);
        }
    }
    
    @Override
    public void remove(final int key) {
        synchronized (this) {
            this.charts.remove(key);
        }
    }
    
    @Override
    public void addOrChange(final T data) {
        synchronized (this) {
            this.charts.addOrChange(data);
        }
    }
    
    @Override
    public List<T> inIterator() {
        synchronized (this) {
            return this.charts.inIterator();
        }
    }
    
    @Override
    public List<T> inIterator(final int maxNum) {
        synchronized (this) {
            return this.charts.inIterator(maxNum);
        }
    }
    
    @Override
    public void setSeq() {
        synchronized (this) {
            this.charts.setSeq();
        }
    }
    
    @Override
    public void operateItem(final IChartsItemOperator<T> operator) {
        synchronized (this) {
            this.charts.operateItem(operator);
        }
    }
    
    @Override
    public int getSize() {
        synchronized (this) {
            return this.charts.getSize();
        }
    }
    
    @Override
    public List<T> inIterator(final int minSeq, final int maxSeq) {
        synchronized (this) {
            return this.charts.inIterator(minSeq, maxSeq);
        }
    }
    
    @Override
    public List<T> inIterator(final IChartsItemCondition<T> condition, final boolean meetFirstNotPassReturn) {
        synchronized (this) {
            return this.charts.inIterator(condition, meetFirstNotPassReturn);
        }
    }
    
    @Override
    public T getDataAtSeq(final int seq) {
        synchronized (this) {
            return this.charts.getDataAtSeq(seq);
        }
    }
    
    @Override
    public T getData(final int key, final int offset) {
        synchronized (this) {
            return this.charts.getData(key, offset);
        }
    }
    
    @Override
    public T getData(final int key, final boolean needClone) {
        synchronized (this) {
            return this.charts.getData(key, needClone);
        }
    }
    
    @Override
    public T getDataAtSeq(final int seq, final boolean needClone) {
        synchronized (this) {
            return this.charts.getDataAtSeq(seq, needClone);
        }
    }
    
    @Override
    public T getData(final int key, final int offset, final boolean needClone) {
        synchronized (this) {
            return this.charts.getData(key, offset, needClone);
        }
    }
    
    @Override
    public List<T> inIterator(final boolean needClone) {
        synchronized (this) {
            return this.charts.inIterator(needClone);
        }
    }
    
    @Override
    public List<T> inIterator(final int maxNum, final boolean needClone) {
        synchronized (this) {
            return this.charts.inIterator(maxNum, needClone);
        }
    }
    
    @Override
    public List<T> inIterator(final int minSeq, final int maxSeq, final boolean needClone) {
        synchronized (this) {
            return this.charts.inIterator(minSeq, maxSeq, needClone);
        }
    }
    
    @Override
    public List<T> inIterator(final IChartsItemCondition<T> condition, final boolean meetFirstNotPassReturn, final boolean needClone) {
        synchronized (this) {
            return this.charts.inIterator(condition, meetFirstNotPassReturn, needClone);
        }
    }
}
