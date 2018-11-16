package com.reign.framework.jdbc.orm.cache.ehcache;

import com.reign.framework.jdbc.orm.cache.*;

public class EhCacheReadWriteCollectionRegion<K, V> extends AbstractEhCacheReadWriteRegion<K, V>
{
    @Override
    public V get(final K key) {
        if (!this.jdbcEntity.isQueryCacheEnable()) {
            CacheStatistics.addDisableHits();
            return null;
        }
        return super.get(key);
    }
}
