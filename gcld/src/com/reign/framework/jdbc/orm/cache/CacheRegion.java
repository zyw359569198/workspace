package com.reign.framework.jdbc.orm.cache;

public interface CacheRegion<K, V> extends Cache<K, V>
{
    LockItem<V> getLockItem(final K p0);
    
    void removeLockItem(final K p0);
}
