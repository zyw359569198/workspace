package com.reign.framework.jdbc.orm.cache;

import java.util.concurrent.locks.*;
import java.util.*;

public abstract class AbstractReadWriteCacheRegionAccessStrategy<K, V> implements CacheRegionAccessStrategy<K, V>
{
    private CacheRegion<K, V> reigon;
    protected Lock lock;
    
    public AbstractReadWriteCacheRegionAccessStrategy(final CacheRegion<K, V> region) {
        this.lock = new ReentrantLock(false);
        this.reigon = region;
    }
    
    @Override
    public V get(final K key) {
        return this.reigon.get(key);
    }
    
    @Override
    public List<V> mget(final K... keys) {
        return this.reigon.mget(keys);
    }
    
    @Override
    public void put(final K key, final V value) {
        try {
            this.lock.lock();
            final LockItem<V> lockItem = this.reigon.getLockItem(key);
            if (lockItem != null) {
                if (!lockItem.isWritable()) {
                    return;
                }
                this.reigon.removeLockItem(key);
            }
            this.reigon.put(key, value);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    @Override
    public void put(final K key, final CacheItem<V> item) {
        try {
            this.lock.lock();
            final LockItem<V> lockItem = this.reigon.getLockItem(key);
            if (lockItem != null) {
                if (!lockItem.isWritable()) {
                    return;
                }
                this.reigon.removeLockItem(key);
            }
            this.reigon.put(key, item);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    @Override
    public void put(final K key, final V... values) {
        this.reigon.put(key, values);
    }
    
    @Override
    public void remove(final K key) {
        try {
            this.lock.lock();
            final LockItem<V> lockItem = this.reigon.getLockItem(key);
            if (lockItem != null) {
                if (!lockItem.isWritable()) {
                    return;
                }
                this.reigon.removeLockItem(key);
            }
            this.reigon.remove(key);
        }
        finally {
            this.lock.unlock();
        }
        this.lock.unlock();
    }
    
    @Override
    public void clear() {
        this.reigon.clear();
    }
    
    @Override
    public void destory() {
        this.reigon.destory();
    }
    
    @Override
    public int size() {
        return this.reigon.size();
    }
    
    @Override
    public CacheRegion<K, V> getCacheRegion() {
        return this.reigon;
    }
    
    @Override
    public LockItem<V> lockItem(final K key) {
        try {
            this.lock.lock();
            LockItem<V> lockItem = this.reigon.getLockItem(key);
            if (lockItem == null) {
                lockItem = new LockItem<V>();
            }
            else {
                lockItem.lock();
            }
            this.reigon.put(key, lockItem);
            return lockItem;
        }
        finally {
            this.lock.unlock();
        }
    }
    
    @Override
    public boolean unlockItem(final K key, final LockItem<V> lockItem) {
        try {
            this.lock.lock();
            final LockItem<V> localLockItem = this.reigon.getLockItem(key);
            if (localLockItem != null && localLockItem == lockItem) {
                lockItem.unlock();
                return true;
            }
            return false;
        }
        finally {
            this.lock.unlock();
        }
    }
}
