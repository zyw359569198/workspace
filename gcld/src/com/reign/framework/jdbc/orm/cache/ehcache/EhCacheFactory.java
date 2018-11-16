package com.reign.framework.jdbc.orm.cache.ehcache;

import com.reign.framework.jdbc.orm.cache.*;

public class EhCacheFactory implements CacheFactory
{
    public EhCacheFactory() {
        EhCacheManager.getInstance();
    }
    
    @Override
    public CacheRegionAccessStrategy<String, Object> getCache() {
        final EhCacheReadWriteRegion<String, Object> reigon = new EhCacheReadWriteRegion<String, Object>();
        return new EhCacheReadWriteRegionAccessStrategy<String, Object>(reigon);
    }
    
    @Override
    public CacheRegionAccessStrategy<String, String[]> getQueryCache() {
        final EhCacheReadWriteCollectionRegion<String, String[]> reigon = new EhCacheReadWriteCollectionRegion<String, String[]>();
        return new EhCacheReadWriteRegionAccessStrategy<String, String[]>(reigon);
    }
}
