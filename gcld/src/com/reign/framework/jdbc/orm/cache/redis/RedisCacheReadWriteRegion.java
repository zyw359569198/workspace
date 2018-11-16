package com.reign.framework.jdbc.orm.cache.redis;

import java.util.concurrent.*;
import java.util.*;
import com.reign.framework.jdbc.orm.cache.*;
import com.reign.framework.jdbc.orm.*;

public class RedisCacheReadWriteRegion<V> implements CacheRegion<String, V>
{
    private RedisCache<V> cache;
    private ConcurrentMap<String, LockItem<V>> lockMap;
    
    public RedisCacheReadWriteRegion() {
        this.lockMap = new ConcurrentHashMap<String, LockItem<V>>();
    }
    
    @Override
    public V get(final String key) {
        final LockItem<V> lockItem = this.lockMap.get(key);
        return (lockItem != null) ? null : this.cache.get(key);
    }
    
    @Override
    public LockItem<V> getLockItem(final String key) {
        final LockItem<V> lockItem = this.lockMap.get(key);
        return lockItem;
    }
    
    @Override
    public void removeLockItem(final String key) {
        this.lockMap.remove(key);
    }
    
    @Override
    public List<V> mget(final String... keys) {
        final List<V> resultList = new ArrayList<V>();
        boolean canGet = true;
        for (final String key : keys) {
            final LockItem<V> lockItem = this.lockMap.get(key);
            if (lockItem != null) {
                canGet = false;
                break;
            }
        }
        if (canGet) {
            return this.cache.mget(keys);
        }
        return resultList;
    }
    
    @Override
    public void put(final String key, final V value) {
        this.cache.put(key, value);
    }
    
    @Override
    public void put(final String key, final CacheItem<V> item) {
        if (item instanceof LockItem) {
            this.lockMap.put(key, (LockItem)item);
        }
    }
    
    @Override
    public void put(final String key, final V... values) {
        this.cache.put(key, values);
    }
    
    @Override
    public void remove(final String key) {
        this.cache.remove(key);
    }
    
    @Override
    public void clear() {
        this.cache.clear();
    }
    
    @Override
    public void destory() {
        this.cache.destory();
    }
    
    @Override
    public int size() {
        return 0;
    }
    
    public void init(final JedisPool pool, final JdbcEntity entity) {
        final RedisCache<V> cache = new RedisCache<V>();
        cache.init(pool, entity);
        this.cache = cache;
    }
}
