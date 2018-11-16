package com.reign.framework.jdbc.orm.cache.ehcache;

import com.reign.framework.jdbc.orm.cache.*;

public class EhCacheReadWriteRegion<K, V> extends AbstractEhCacheReadWriteRegion<K, V>
{
    @Override
    public V get(final K key) {
        if (!this.jdbcEntity.isObjCacheEnable()) {
            CacheStatistics.addDisableHits();
            return null;
        }
        return super.get(key);
    }
}
