package com.reign.framework.jdbc.orm.cache.ehcache;

import com.reign.framework.jdbc.orm.cache.*;

public class EhCacheItem<T> implements CacheItem<T>
{
    private static final long serialVersionUID = -1200089964590979757L;
    private T value;
    
    public EhCacheItem(final T value) {
        this.value = value;
    }
    
    @Override
    public T getValue() {
        return this.value;
    }
    
    @Override
    public boolean isWritable() {
        return true;
    }
}
