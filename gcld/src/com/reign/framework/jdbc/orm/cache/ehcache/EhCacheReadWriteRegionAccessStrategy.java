package com.reign.framework.jdbc.orm.cache.ehcache;

import com.reign.framework.jdbc.orm.cache.*;
import com.reign.framework.jdbc.orm.*;

public class EhCacheReadWriteRegionAccessStrategy<K, V> extends AbstractReadWriteCacheRegionAccessStrategy<K, V>
{
    private AbstractEhCacheReadWriteRegion<K, V> region;
    
    public EhCacheReadWriteRegionAccessStrategy(final AbstractEhCacheReadWriteRegion<K, V> region) {
        super(region);
        this.region = region;
    }
    
    public void init(final String name, final JdbcEntity jdbcEntity) {
        this.region.init(name, jdbcEntity);
    }
}
