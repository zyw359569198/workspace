package com.reign.gcld.rank.common;

import java.util.*;

public class RankComparator<T> implements Comparator<RankData>
{
    @Override
    public int compare(final RankData o1, final RankData o2) {
        if (o1.value < o2.value) {
            return 0;
        }
        return 1;
    }
}
