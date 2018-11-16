package com.reign.framework.common.cache;

import java.util.concurrent.*;
import com.reign.framework.exception.*;
import java.util.*;

public abstract class SimpleCache<K, V> implements ICache<K, V>
{
    private ConcurrentMap<K, V> cacheMap;
    
    public SimpleCache() {
        this.cacheMap = new ConcurrentHashMap<K, V>();
    }
    
    @Override
    public V get(final K key) {
        if (key != null) {
            final V value = this.cacheMap.get(key);
            return value;
        }
        return null;
    }
    
    @Override
    public void put(final K key, final V value) {
        this.cacheMap.put(key, value);
    }
    
    @Override
    public void remove(final K key) {
        this.cacheMap.remove(key);
    }
    
    @Override
    public Vector<V> getModels() {
        throw new InternalException("not yet support");
    }
    
    @Override
    public void clear() {
        this.cacheMap.clear();
    }
    
    public Map<K, V> getCacheMap() {
        final Map<K, V> map = new LinkedHashMap<K, V>();
        final Set<Map.Entry<K, V>> entrySet = this.cacheMap.entrySet();
        for (final Map.Entry<K, V> entry : entrySet) {
            map.put(entry.getKey(), entry.getValue());
        }
        return map;
    }
}
