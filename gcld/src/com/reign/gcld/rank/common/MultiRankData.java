package com.reign.gcld.rank.common;

import java.util.*;

public class MultiRankData implements Comparable<MultiRankData>
{
    public int playerId;
    public List<ComparableFactor> value;
    
    public MultiRankData(final int playerId, final ComparableFactor... params) {
        this.value = new ArrayList<ComparableFactor>();
        this.playerId = playerId;
        for (int i = 0; i < params.length; ++i) {
            this.value.add(params[i]);
        }
    }
    
    @Override
    public int compareTo(final MultiRankData o) {
        final List<ComparableFactor> l1 = this.value;
        final List<ComparableFactor> l2 = o.value;
        for (int length = Math.min(l1.size(), l2.size()), i = 0; i < length; ++i) {
            final int compare = l1.get(i).compareTo(l2.get(i));
            if (compare == 1) {
                return 0;
            }
            if (compare == -1) {
                return 1;
            }
        }
        return 0;
    }
    
    public static ComparableFactor[] orgnizeValue(final int a, final int at, final int b, final int bt) {
        final ComparableFactor[] arrays = { new ComparableFactor(at, a), new ComparableFactor(bt, b) };
        return arrays;
    }
    
    public MultiRankData add(final MultiRankData data) {
        if (data == null || data.value == null || data.value.size() != this.value.size()) {
            return this;
        }
        int value = this.value.get(0).getValue();
        value += data.value.get(0).getValue();
        this.value.get(0).setValue(value);
        final int max = Math.max(this.value.get(1).getValue(), data.value.get(1).getValue());
        this.value.get(1).setValue(max);
        return this;
    }
}
