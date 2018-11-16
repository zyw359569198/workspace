package com.reign.framework.common.cache;

import org.springframework.beans.factory.*;
import java.util.concurrent.*;
import java.util.*;

public abstract class AbstractCache<K, V> implements ICache<K, V>, InitializingBean
{
    private ConcurrentMap<K, CacheObj> cacheMap;
    private Vector<V> list;
    private long timeout;
    private SDataLoader loader;
    
    public AbstractCache() {
        this.cacheMap = new ConcurrentHashMap<K, CacheObj>();
        this.list = new Vector<V>();
    }
    
    @Override
    public V get(final K key) {
        if (key == null) {
            return null;
        }
        final CacheObj obj = this.cacheMap.get(key);
        if (obj != null && (obj.getTimestamp() == 0L || System.currentTimeMillis() - obj.getCreateTime() <= obj.getTimestamp())) {
            return obj.getValue();
        }
        this.cacheMap.remove(key);
        return null;
    }
    
    @Override
    public void put(final K key, final V value) {
        this.cacheMap.put(key, new CacheObj(value));
        this.list.addElement(value);
    }
    
    @Override
    public void remove(final K key) {
        this.list.remove(this.cacheMap.get(key).value);
        this.cacheMap.remove(key);
    }
    
    @Override
    public Vector<V> getModels() {
        return this.list;
    }
    
    @Override
    public void clear() {
        this.cacheMap.clear();
        this.list.clear();
    }
    
    public long getTimeout() {
        return this.timeout;
    }
    
    public void setTimeout(final long timeout) {
        this.timeout = timeout;
    }
    
    public Map<K, V> getCacheMap() {
        final Map<K, V> map = new LinkedHashMap<K, V>();
        final Set<Map.Entry<K, CacheObj>> entrySet = this.cacheMap.entrySet();
        for (final Map.Entry<K, CacheObj> entry : entrySet) {
            map.put(entry.getKey(), entry.getValue().getValue());
        }
        return map;
    }
    
    @Override
    public void reload() throws Exception {
        this.clear();
        this.afterPropertiesSet();
    }
    
    @Override
    public void setSDataLoader(final SDataLoader loader) {
        this.loader = loader;
    }
    
    @Override
    public SDataLoader getSDataLoader() {
        return this.loader;
    }
    
    class CacheObj
    {
        private long timestamp;
        private V value;
        private long createTime;
        
        public CacheObj(final V value) {
            this.timestamp = AbstractCache.this.timeout;
            this.createTime = System.currentTimeMillis();
            this.value = value;
        }
        
        public long getTimestamp() {
            return this.timestamp;
        }
        
        public V getValue() {
            return this.value;
        }
        
        public long getCreateTime() {
            return this.createTime;
        }
    }
}
