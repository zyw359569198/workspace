package com.reign.framework.jdbc.orm.cache;

import java.util.*;

public interface Cache<K, V>
{
    V get(final K p0);
    
    List<V> mget(final K... p0);
    
    void put(final K p0, final V p1);
    
    void put(final K p0, final CacheItem<V> p1);
    
    void put(final K p0, final V... p1);
    
    void remove(final K p0);
    
    void clear();
    
    void destory();
    
    int size();
}
