package com.reign.framework.jdbc.orm.redis;

import org.springframework.transaction.support.*;

public final class RedisUtils
{
    public static final Jedis getRedis(final JedisPool pool) {
        Jedis jedis = (Jedis)TransactionSynchronizationManager.getResource(pool);
        if (jedis == null) {
            jedis = (Jedis)pool.getResource();
            TransactionSynchronizationManager.bindResource(pool, jedis);
            return jedis;
        }
        return jedis;
    }
    
    public static final void releaseRedis(final JedisPool pool) {
        final Jedis jedis = (Jedis)TransactionSynchronizationManager.unbindResource(pool);
        if (jedis == null) {
            pool.returnResource((Object)jedis);
        }
    }
}
