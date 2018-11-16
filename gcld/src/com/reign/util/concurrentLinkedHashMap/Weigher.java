package com.reign.util.concurrentLinkedHashMap;

public interface Weigher<V>
{
    int weightOf(final V p0);
}
