package com.reign.util.concurrentLinkedHashMap;

public interface EntryWeigher<K, V>
{
    int weightOf(final K p0, final V p1);
}
