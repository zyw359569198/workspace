package com.reign.framework.jdbc.orm.cache.redis;

import java.lang.reflect.*;
import org.apache.commons.logging.*;
import com.reign.framework.jdbc.orm.*;
import com.reign.framework.json.*;
import java.util.*;
import com.reign.framework.jdbc.orm.cache.*;

public class RedisCache<V> implements Cache<String, V>
{
    private static final Log log;
    private JedisPool pool;
    private JdbcEntity entity;
    private CacheConfig config;
    private Class<V> clazz;
    private boolean isBinaryModel;
    private Constructor<V> constructor;
    
    static {
        log = LogFactory.getLog("com.reign.jdbc.cache");
    }
    
    public RedisCache() {
    }
    
    public RedisCache(final JedisPool pool, final JdbcEntity entity) {
        this.pool = pool;
        this.entity = entity;
        this.config = entity.getCacheConfig();
        this.clazz = (Class<V>)entity.getEntityClass();
        this.isBinaryModel = BinaryModel.class.isAssignableFrom(this.clazz);
        if (this.isBinaryModel) {
            try {
                this.constructor = this.clazz.getConstructor(byte[].class);
            }
            catch (Exception e) {
                throw new RuntimeException("init binay model error, can't found valid constructor");
            }
        }
    }
    
    @Override
    public V get(final String key) {
        if (!this.entity.isObjCacheEnable()) {
            CacheStatistics.addDisableHits();
            return null;
        }
        final Jedis jedis = (Jedis)this.pool.getResource();
        try {
            final byte[] bytes = jedis.get(this.getKeyBytes(key));
            if (bytes == null) {
                return null;
            }
            if (!this.isBinaryModel) {
                return Types.readValue(bytes, this.clazz);
            }
            return this.readValue(bytes);
        }
        catch (Throwable t) {
            RedisCache.log.error("jedis get [key: " + key + "] error", t);
        }
        finally {
            this.pool.returnResource((Object)jedis);
        }
        return null;
    }
    
    @Override
    public List<V> mget(final String... keys) {
        if (!this.entity.isObjCacheEnable()) {
            CacheStatistics.addDisableHits();
            return null;
        }
        final Jedis jedis = (Jedis)this.pool.getResource();
        try {
            if (keys.length == 0) {
                return null;
            }
            final List<byte[]> bytesList = (List<byte[]>)jedis.mget(this.getKeyBytes(keys));
            if (bytesList == null) {
                return null;
            }
            final List<V> list = new ArrayList<V>(bytesList.size());
            for (final byte[] bytes : bytesList) {
                if (bytes == null) {
                    continue;
                }
                if (!this.isBinaryModel) {
                    list.add(Types.readValue(bytes, this.clazz));
                }
                else {
                    final V value = this.readValue(bytes);
                    if (value == null) {
                        return null;
                    }
                    list.add(value);
                }
            }
            return list;
        }
        catch (Throwable t) {
            RedisCache.log.error("jedis mget error", t);
        }
        finally {
            this.pool.returnResource((Object)jedis);
        }
        return null;
    }
    
    @Override
    public void put(final String key, final V value) {
        final Jedis jedis = (Jedis)this.pool.getResource();
        try {
            final byte[] keyBytes = this.getKeyBytes(key);
            if (!this.isBinaryModel) {
                jedis.setex(keyBytes, this.config.getMaxLiveTime(), Types.writeValueAsBytes(value));
            }
            else {
                final BinaryModel model = (BinaryModel)value;
                jedis.setex(keyBytes, this.config.getMaxLiveTime(), model.toByte());
            }
        }
        catch (Throwable t) {
            RedisCache.log.error("jedis put [key: " + key + "] error", t);
            return;
        }
        finally {
            this.pool.returnResource((Object)jedis);
        }
        this.pool.returnResource((Object)jedis);
    }
    
    @Override
    public void put(final String key, final V... values) {
        final Jedis jedis = (Jedis)this.pool.getResource();
        try {
            final byte[] keyBytes = this.getKeyBytes(key);
            final byte[][] valuesBytes = new byte[values.length][];
            if (!this.isBinaryModel) {
                for (int i = 0; i < values.length; ++i) {
                    valuesBytes[i] = Types.writeValueAsBytes(values[i]);
                }
            }
            else {
                for (int i = 0; i < values.length; ++i) {
                    final BinaryModel model = (BinaryModel)values[i];
                    valuesBytes[i] = model.toByte();
                }
            }
            jedis.lpush(keyBytes, valuesBytes);
            jedis.expire(keyBytes, this.config.getMaxLiveTime());
        }
        catch (Throwable t) {
            RedisCache.log.error("jedis mput [key: " + key + "] error", t);
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
            RedisCache.log.error("jedis remove" + key + " error", t);
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
            final byte[][] keys = jedis.keys((String.valueOf(this.entity.getPrefix()) + "*").getBytes()).toArray(new byte[0][]);
            if (keys.length > 0) {
                jedis.del(keys);
            }
        }
        catch (Throwable t) {
            RedisCache.log.error("jedis clear error", t);
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
            RedisCache.log.error("jedis flush all error", t);
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
    
    public void init(final JedisPool pool, final JdbcEntity entity) {
        this.pool = pool;
        this.entity = entity;
        this.config = entity.getCacheConfig();
        this.clazz = (Class<V>)entity.getEntityClass();
        this.isBinaryModel = BinaryModel.class.isAssignableFrom(this.clazz);
        if (this.isBinaryModel) {
            try {
                this.constructor = this.clazz.getConstructor(byte[].class);
            }
            catch (Exception e) {
                throw new RuntimeException("init binay model error, can't found valid constructor");
            }
        }
    }
    
    public JdbcEntity getEntity() {
        return this.entity;
    }
    
    private byte[][] getKeyBytes(final String[] keys) {
        final byte[][] bytess = new byte[keys.length][];
        for (int i = 0; i < keys.length; ++i) {
            bytess[i] = this.getKeyBytes(keys[i]);
        }
        return bytess;
    }
    
    private byte[] getKeyBytes(final String key) {
        return (String.valueOf(this.entity.getPrefix()) + key).getBytes();
    }
    
    private V readValue(final byte[] bytes) {
        try {
            return this.constructor.newInstance(bytes);
        }
        catch (Exception e) {
            if (RedisCache.log.isWarnEnabled()) {
                RedisCache.log.warn("binary model deserialize error", e);
            }
            return null;
        }
    }
    
    @Override
    public void put(final String key, final CacheItem<V> item) {
    }
}
