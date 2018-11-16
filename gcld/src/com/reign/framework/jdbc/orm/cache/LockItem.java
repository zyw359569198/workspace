package com.reign.framework.jdbc.orm.cache;

public class LockItem<T> implements CacheItem<T>
{
    private static final long serialVersionUID = 207296955842890151L;
    private int lockCount;
    
    public LockItem() {
        this.lockCount = 1;
    }
    
    @Override
    public T getValue() {
        return null;
    }
    
    public void lock() {
        ++this.lockCount;
    }
    
    @Override
    public boolean isWritable() {
        return this.lockCount <= 0;
    }
    
    public void unlock() {
        --this.lockCount;
    }
}
