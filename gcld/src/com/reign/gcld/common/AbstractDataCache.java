package com.reign.gcld.common;

import java.util.concurrent.*;

public abstract class AbstractDataCache<K, V> implements IDataCache<K, V>
{
    private ConcurrentMap<K, V> cacheMap;
    
    public AbstractDataCache() {
        this.cacheMap = new ConcurrentHashMap<K, V>();
    }
    
    @Override
    public V get(final K key) {
        V v = this.cacheMap.get(key);
        if (v == null) {
            v = this.read(key);
            if (v != null) {
                this.cacheMap.putIfAbsent(key, v);
            }
        }
        return v;
    }
    
    public abstract V read(final K p0);
    
    @Override
    public V put(final K key, final V value) {
        final int number = this.update(key, value);
        V v = null;
        if (number > 0) {
            this.cacheMap.put(key, value);
            v = this.cacheMap.get(key);
        }
        return v;
    }
    
    public abstract int update(final K p0, final V p1);
    
    @Override
    public void remove(final K key) {
        this.cacheMap.remove(key);
    }
}
