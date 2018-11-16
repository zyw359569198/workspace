package com.reign.util.struct.charts.RBTree;

import com.reign.util.struct.charts.*;
import java.util.*;

public class SortChartsRedBlackTreeRecordSpecialSeq<T extends ISetSeqable<T>> extends SortChartsRedBlackTree<T>
{
    private Map<Integer, T> seqMap;
    
    public SortChartsRedBlackTreeRecordSpecialSeq(final Collection<Integer> needRecordSeqList) {
        this.seqMap = new HashMap<Integer, T>();
        for (final int s : needRecordSeqList) {
            this.seqMap.put(s, null);
        }
    }
    
    public SortChartsRedBlackTreeRecordSpecialSeq(final boolean needResetSeqWhenChange, final Collection<Integer> needRecordSeqList) {
        super(needResetSeqWhenChange);
        this.seqMap = new HashMap<Integer, T>();
        for (final int s : needRecordSeqList) {
            this.seqMap.put(s, null);
        }
    }
    
    public SortChartsRedBlackTreeRecordSpecialSeq(final T data, final Collection<Integer> needRecordSeqList) {
        super(data);
        this.seqMap = new HashMap<Integer, T>();
        for (final int s : needRecordSeqList) {
            this.seqMap.put(s, null);
        }
    }
    
    public SortChartsRedBlackTreeRecordSpecialSeq(final T data, final boolean needResetSeqWhenChange, final Collection<Integer> needRecordSeqList) {
        super(data, needResetSeqWhenChange);
        this.seqMap = new HashMap<Integer, T>();
        for (final int s : needRecordSeqList) {
            this.seqMap.put(s, null);
        }
        if (needResetSeqWhenChange && this.seqMap.containsKey(1)) {
            this.seqMap.put(1, data);
        }
    }
    
    @Override
    public void handleAfterSetSeq(final int seq, final T data) {
        if (this.seqMap.containsKey(seq)) {
            this.seqMap.put(seq, data);
        }
    }
    
    @Override
    public T getDataAtSeq(final int seq) {
        T data = this.seqMap.get(seq);
        if (data == null) {
            data = super.getDataAtSeq(seq);
        }
        return data;
    }
    
    @Override
    public T getDataAtSeq(final int seq, final boolean needClone) {
        T data = this.seqMap.get(seq);
        if (data == null) {
            data = super.getDataAtSeq(seq, needClone);
        }
        else if (needClone) {
            data = data.getClone();
        }
        return data;
    }
}
