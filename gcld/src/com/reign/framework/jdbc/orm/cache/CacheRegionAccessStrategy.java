package com.reign.framework.jdbc.orm.cache;

public interface CacheRegionAccessStrategy<K, V> extends Cache<K, V>
{
    CacheRegion<K, V> getCacheRegion();
    
    LockItem<V> lockItem(final K p0);
    
    boolean unlockItem(final K p0, final LockItem<V> p1);
}
