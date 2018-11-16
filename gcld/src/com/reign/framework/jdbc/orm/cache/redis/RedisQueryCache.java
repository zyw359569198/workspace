package com.reign.framework.jdbc.orm.cache.redis;

import com.reign.framework.jdbc.orm.*;
import org.apache.commons.logging.*;
import com.reign.framework.json.*;
import java.util.*;
import com.reign.framework.jdbc.orm.cache.*;

public class RedisQueryCache implements Cache<String, String[]>
{
    private static final Log log;
    private JedisPool pool;
    private JdbcEntity entity;
    private CacheConfig config;
    private Class<String[]> clazz;
    
    static {
        log = LogFactory.getLog("com.reign.jdbc.querycache");
    }
    
    public RedisQueryCache() {
    }
    
    public RedisQueryCache(final JedisPool pool, final JdbcEntity entity) {
        this.pool = pool;
        this.entity = entity;
        this.config = entity.getQueryCacheConfig();
        this.clazz = String[].class;
    }
    
    @Override
    public String[] get(final String key) {
        if (!this.entity.isQueryCacheEnable()) {
            CacheStatistics.addDisableHits();
            return null;
        }
        final Jedis jedis = (Jedis)this.pool.getResource();
        try {
            final byte[] bytes = jedis.get(this.getKeyBytes(key));
            if (bytes == null) {
                return null;
            }
            return Types.readValue(bytes, this.clazz);
        }
        catch (Throwable t) {
            RedisQueryCache.log.error("jedis get [key: " + key + "] error", t);
        }
        finally {
            this.pool.returnResource((Object)jedis);
        }
        return null;
    }
    
    @Override
    public List<String[]> mget(final String... keys) {
        if (!this.entity.isQueryCacheEnable()) {
            CacheStatistics.addDisableHits();
            return null;
        }
        final Jedis jedis = (Jedis)this.pool.getResource();
        try {
            final List<byte[]> bytesList = (List<byte[]>)jedis.mget(this.getKeyBytes(keys));
            if (bytesList == null) {
                return null;
            }
            final List<String[]> list = new ArrayList<String[]>(bytesList.size());
            for (final byte[] bytes : bytesList) {
                if (bytes == null) {
                    continue;
                }
                list.add(Types.readValue(bytes, this.clazz));
            }
            return list;
        }
        catch (Throwable t) {
            RedisQueryCache.log.error("jedis mget error", t);
        }
        finally {
            this.pool.returnResource((Object)jedis);
        }
        return null;
    }
    
    @Override
    public void put(final String key, final String[] value) {
        final Jedis jedis = (Jedis)this.pool.getResource();
        try {
            final byte[] keyBytes = this.getKeyBytes(key);
            jedis.setex(keyBytes, this.config.getMaxLiveTime(), Types.writeValueAsBytes(value));
        }
        catch (Throwable t) {
            RedisQueryCache.log.error("jedis put [key: " + key + "] error", t);
            return;
        }
        finally {
            this.pool.returnResource((Object)jedis);
        }
        this.pool.returnResource((Object)jedis);
    }
    
    @Override
    public void put(final String key, final String[]... values) {
        final Jedis jedis = (Jedis)this.pool.getResource();
        try {
            final byte[] keyBytes = this.getKeyBytes(key);
            final byte[][] valuesBytes = new byte[values.length][];
            for (int i = 0; i < values.length; ++i) {
                valuesBytes[i] = Types.writeValueAsBytes(values[i]);
            }
            jedis.lpush(keyBytes, valuesBytes);
            jedis.expire(keyBytes, this.config.getMaxLiveTime());
        }
        catch (Throwable t) {
            RedisQueryCache.log.error("jedis mput [key: " + key + "] error", t);
            return;
        }
        finally {
            this.pool.returnResource((Object)jedis);
        }
        this.pool.returnResource((Object)jedis);
    }
    
    @Override
    public void remove(final String key) {
        final Jedis jedis = (Jedis)this.pool.getResource();
        try {
            final byte[] keyBytes = this.getKeyBytes(key);
            jedis.del(new byte[][] { keyBytes });
        }
        catch (Throwable t) {
            RedisQueryCache.log.error("jedis remove [key: " + key + "] error", t);
            return;
        }
        finally {
            this.pool.returnResource((Object)jedis);
        }
        this.pool.returnResource((Object)jedis);
    }
    
    @Override
    public void clear() {
        final Jedis jedis = (Jedis)this.pool.getResource();
        try {
            final byte[][] keys = jedis.keys((String.valueOf(this.entity.getQueryPrefix()) + "*").getBytes()).toArray(new byte[0][]);
            if (keys.length > 0) {
                jedis.del(keys);
            }
        }
        catch (Throwable t) {
            RedisQueryCache.log.error("jedis clear error", t);
            return;
        }
        finally {
            this.pool.returnResource((Object)jedis);
        }
        this.pool.returnResource((Object)jedis);
    }
    
    @Override
    public void destory() {
        final Jedis jedis = (Jedis)this.pool.getResource();
        try {
            jedis.flushAll();
        }
        catch (Throwable t) {
            RedisQueryCache.log.error("jedis put flush all error", t);
            return;
        }
        finally {
            this.pool.returnResource((Object)jedis);
        }
        this.pool.returnResource((Object)jedis);
    }
    
    @Override
    public int size() {
        return 0;
    }
    
    private byte[][] getKeyBytes(final String[] keys) {
        final byte[][] bytess = new byte[keys.length][];
        for (int i = 0; i < keys.length; ++i) {
            bytess[i] = this.getKeyBytes(keys[i]);
        }
        return bytess;
    }
    
    private byte[] getKeyBytes(final String key) {
        return (String.valueOf(this.entity.getQueryPrefix()) + key).getBytes();
    }
    
    public void init(final JedisPool pool, final JdbcEntity entity) {
        this.pool = pool;
        this.entity = entity;
        this.config = entity.getCacheConfig();
        this.clazz = String[].class;
    }
    
    public JdbcEntity getEntity() {
        return this.entity;
    }
    
    @Override
    public void put(final String key, final CacheItem<String[]> item) {
    }
}
