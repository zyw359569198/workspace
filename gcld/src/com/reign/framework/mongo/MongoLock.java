package com.reign.framework.mongo;

import java.util.concurrent.locks.*;
import java.util.concurrent.*;

public class MongoLock implements Comparable<MongoLock>
{
    public Lock lock;
    public int lockId;
    public MongoLock readLock;
    public MongoLock writeLock;
    
    public MongoLock(final int lockId) {
        final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        this.lockId = lockId;
        this.readLock = new MongoLock(lock.readLock(), lockId);
        this.writeLock = new MongoLock(lock.writeLock(), lockId);
    }
    
    private MongoLock(final Lock lock, final int lockId) {
        this.lock = lock;
        this.lockId = lockId;
    }
    
    public MongoLock getReadLock() {
        return this.readLock;
    }
    
    public MongoLock getWriteLock() {
        return this.writeLock;
    }
    
    public void tryLock(final long time, final TimeUnit timeUnit) throws InterruptedException {
        if (this.lock == null) {
            throw new IllegalMonitorStateException("lock is null");
        }
        this.lock.tryLock(time, timeUnit);
    }
    
    public void tryLock() {
        if (this.lock == null) {
            throw new IllegalMonitorStateException("lock is null");
        }
        this.lock.tryLock();
    }
    
    public void lock(final LockParam param) throws InterruptedException {
        if (this.lock == null) {
            throw new IllegalMonitorStateException("lock is null");
        }
        switch (param.lockMode) {
            case LOCK: {
                this.lock();
                break;
            }
            case TRY_LOCK_ONCE: {
                this.tryLock();
                break;
            }
            case TRY_LOCK: {
                this.tryLock(param.time, param.timeUnit);
                break;
            }
        }
    }
    
    public void lock() {
        if (this.lock == null) {
            throw new IllegalMonitorStateException("lock is null");
        }
        this.lock.lock();
    }
    
    public void unlock() {
        if (this.lock == null) {
            throw new IllegalMonitorStateException("lock is null");
        }
        this.lock.unlock();
    }
    
    @Override
    public int compareTo(final MongoLock o) {
        if (this.lockId > o.lockId) {
            return 1;
        }
        if (this.lockId == o.lockId) {
            return 0;
        }
        return -1;
    }
}
