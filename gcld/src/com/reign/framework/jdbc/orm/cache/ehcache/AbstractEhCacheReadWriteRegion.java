package com.reign.framework.jdbc.orm.cache.ehcache;

import com.reign.framework.jdbc.orm.*;
import net.sf.ehcache.*;
import com.reign.framework.jdbc.orm.cache.*;
import java.util.*;

public abstract class AbstractEhCacheReadWriteRegion<K, V> implements CacheRegion<K, V>
{
    protected net.sf.ehcache.Cache cache;
    protected JdbcEntity jdbcEntity;
    
    @Override
    public V get(final K key) {
        final Element element = this.cache.get(key);
        if (element == null) {
            return null;
        }
        final CacheItem<V> cacheItem = (CacheItem<V>)element.getValue();
        return (cacheItem == null) ? null : cacheItem.getValue();
    }
    
    @Override
    public LockItem<V> getLockItem(final K key) {
        final Element element = this.cache.get(key);
        if (element == null) {
            return null;
        }
        final CacheItem<V> cacheItem = (CacheItem<V>)element.getValue();
        if (cacheItem instanceof LockItem) {
            return (LockItem)cacheItem;
        }
        return null;
    }
    
    @Override
    public void removeLockItem(final K key) {
    }
    
    @Override
    public List<V> mget(final K... keys) {
        final List<V> resultList = new ArrayList<V>();
        for (final K key : keys) {
            final V value = this.get(key);
            if (value == null) {
                break;
            }
            resultList.add(value);
        }
        return resultList;
    }
    
    @Override
    public void put(final K key, final V value) {
        this.cache.put(new Element(key, new EhCacheItem(value)));
    }
    
    @Override
    public void put(final K key, final CacheItem<V> item) {
        this.cache.put(new Element(key, item));
    }
    
    @Override
    public void put(final K key, final V... values) {
        throw new RuntimeException("not supported");
    }
    
    @Override
    public void remove(final K key) {
        this.cache.remove(key);
    }
    
    @Override
    public void clear() {
        this.cache.removeAll();
    }
    
    @Override
    public void destory() {
        this.cache.dispose();
    }
    
    @Override
    public int size() {
        return 0;
    }
    
    public void init(final String name, final JdbcEntity jdbcEntity) {
        this.cache = EhCacheManager.getInstance().getCache(name);
        this.jdbcEntity = jdbcEntity;
    }
}
