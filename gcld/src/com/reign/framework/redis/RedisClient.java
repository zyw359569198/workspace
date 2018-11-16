package com.reign.framework.redis;

import org.apache.commons.logging.*;

public class RedisClient
{
    private static final Log log;
    private JedisPool pool;
    private String prefix;
    
    static {
        log = LogFactory.getLog("com.reign.redis");
    }
    
    public RedisClient(final JedisPool pool, final String prefix) {
        this.pool = pool;
        this.prefix = prefix;
    }
    
    public Jedis getJedis() {
        final Jedis jedis = (Jedis)this.pool.getResource();
        return jedis;
    }
    
    public void releaseJedis(final Jedis jedis) {
        this.pool.returnResource((Object)jedis);
    }
    
    public void remove(final String key) {
        final Jedis jedis = (Jedis)this.pool.getResource();
        try {
            final byte[] keyBytes = this.getKeyBytes(key);
            jedis.del(new byte[][] { keyBytes });
        }
        catch (Throwable t) {
            RedisClient.log.error("jedis remove" + key + " error", t);
            return;
        }
        finally {
            this.pool.returnResource((Object)jedis);
        }
        this.pool.returnResource((Object)jedis);
    }
    
    public void clear() {
        final Jedis jedis = (Jedis)this.pool.getResource();
        try {
            final byte[][] keys = jedis.keys((String.valueOf(this.prefix) + "*").getBytes()).toArray(new byte[0][]);
            if (keys.length > 0) {
                jedis.del(keys);
            }
        }
        catch (Throwable t) {
            RedisClient.log.error("jedis clear error", t);
            return;
        }
        finally {
            this.pool.returnResource((Object)jedis);
        }
        this.pool.returnResource((Object)jedis);
    }
    
    public void destory() {
        final Jedis jedis = (Jedis)this.pool.getResource();
        try {
            jedis.flushAll();
        }
        catch (Throwable t) {
            RedisClient.log.error("jedis flush all error", t);
            return;
        }
        finally {
            this.pool.returnResource((Object)jedis);
        }
        this.pool.returnResource((Object)jedis);
    }
    
    public byte[][] getKeyBytes(final String[] keys) {
        final byte[][] bytess = new byte[keys.length][];
        for (int i = 0; i < keys.length; ++i) {
            bytess[i] = this.getKeyBytes(keys[i]);
        }
        return bytess;
    }
    
    public byte[] getKeyBytes(final String key) {
        return (String.valueOf(this.prefix) + key).getBytes();
    }
}
