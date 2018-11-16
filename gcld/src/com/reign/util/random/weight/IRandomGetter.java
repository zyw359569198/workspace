package com.reign.util.random.weight;

import java.util.*;

public interface IRandomGetter<T>
{
    void getRandom(final List<T> p0, final List<T> p1, final int p2);
    
    void getRandom(final List<T> p0, final List<T> p1, final int p2, final IWeightGetter<T> p3);
}
