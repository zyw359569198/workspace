package com.reign.util.concurrentLinkedHashMap;

public interface EvictionListener<K, V>
{
    void onEviction(final K p0, final V p1);
}
