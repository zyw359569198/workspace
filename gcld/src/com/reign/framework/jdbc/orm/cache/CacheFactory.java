package com.reign.framework.jdbc.orm.cache;

public interface CacheFactory
{
    Cache getCache();
    
    Cache getQueryCache();
}
