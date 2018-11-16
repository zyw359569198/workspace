package com.reign.framework.jdbc.orm.cache.redis;

import com.reign.framework.jdbc.orm.cache.*;
import com.reign.framework.jdbc.orm.*;

public class RedisCacheReadWriteCollectionRegionAccessStrategy extends AbstractReadWriteCacheRegionAccessStrategy<String, String[]>
{
    private RedisCacheReadWriteCollectionRegion region;
    
    public RedisCacheReadWriteCollectionRegionAccessStrategy(final RedisCacheReadWriteCollectionRegion region) {
        super(region);
        this.region = region;
    }
    
    public void init(final JedisPool pool, final JdbcEntity entity) {
        this.region.init(pool, entity);
    }
}
