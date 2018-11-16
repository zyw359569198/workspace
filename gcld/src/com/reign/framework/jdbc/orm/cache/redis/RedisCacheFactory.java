package com.reign.framework.jdbc.orm.cache.redis;

import com.reign.framework.jdbc.orm.cache.*;

public class RedisCacheFactory implements CacheFactory
{
    private JedisPool pool;
    
    public RedisCacheFactory(final String host, final int db) {
        this.pool = new JedisPool(new GenericObjectPool.Config(), host, 6379, 2000, (String)null, db);
    }
    
    @Override
    public CacheRegionAccessStrategy<String, Object> getCache() {
        final RedisCacheReadWriteRegion<Object> region = new RedisCacheReadWriteRegion<Object>();
        return new RedisCacheReadWriteRegionAccessStrategy(region);
    }
    
    @Override
    public CacheRegionAccessStrategy<String, String[]> getQueryCache() {
        final RedisCacheReadWriteCollectionRegion region = new RedisCacheReadWriteCollectionRegion();
        return new RedisCacheReadWriteCollectionRegionAccessStrategy(region);
    }
    
    public JedisPool getPool() {
        return this.pool;
    }
}
