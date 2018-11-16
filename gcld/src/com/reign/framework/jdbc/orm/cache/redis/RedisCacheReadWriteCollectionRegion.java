package com.reign.framework.jdbc.orm.cache.redis;

import java.util.concurrent.*;
import java.util.*;
import com.reign.framework.jdbc.orm.cache.*;
import com.reign.framework.jdbc.orm.*;

public class RedisCacheReadWriteCollectionRegion implements CacheRegion<String, String[]>
{
    private RedisQueryCache cache;
    private ConcurrentMap<String, LockItem<String[]>> lockMap;
    
    public RedisCacheReadWriteCollectionRegion() {
        this.lockMap = new ConcurrentHashMap<String, LockItem<String[]>>();
    }
    
    @Override
    public String[] get(final String key) {
        final LockItem<String[]> lockItem = this.lockMap.get(key);
        return (lockItem != null) ? null : this.cache.get(key);
    }
    
    @Override
    public LockItem<String[]> getLockItem(final String key) {
        final LockItem<String[]> lockItem = this.lockMap.get(key);
        return lockItem;
    }
    
    @Override
    public void removeLockItem(final String key) {
        this.lockMap.remove(key);
    }
    
    @Override
    public List<String[]> mget(final String... keys) {
        boolean canGet = true;
        for (final String key : keys) {
            final LockItem<String[]> lockItem = this.lockMap.get(key);
            if (lockItem != null) {
                canGet = false;
                break;
            }
        }
        if (canGet) {
            return this.cache.mget(keys);
        }
        return Collections.emptyList();
    }
    
    @Override
    public void put(final String key, final String[] value) {
        this.cache.put(key, value);
    }
    
    @Override
    public void put(final String key, final CacheItem<String[]> item) {
        if (item instanceof LockItem) {
            this.lockMap.put(key, (LockItem)item);
        }
    }
    
    @Override
    public void put(final String key, final String[]... values) {
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
        final RedisQueryCache cache = new RedisQueryCache();
        cache.init(pool, entity);
        this.cache = cache;
    }
}
