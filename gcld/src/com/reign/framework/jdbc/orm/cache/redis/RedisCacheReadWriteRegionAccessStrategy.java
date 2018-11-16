package com.reign.framework.jdbc.orm.cache.redis;

import com.reign.framework.jdbc.orm.cache.*;
import com.reign.framework.jdbc.orm.*;

public class RedisCacheReadWriteRegionAccessStrategy<V> extends AbstractReadWriteCacheRegionAccessStrategy<String, V>
{
    private RedisCacheReadWriteRegion<V> region;
    
    public RedisCacheReadWriteRegionAccessStrategy(final RedisCacheReadWriteRegion<V> region) {
        super(region);
        this.region = region;
    }
    
    public void init(final JedisPool pool, final JdbcEntity entity) {
        this.region.init(pool, entity);
    }
}
