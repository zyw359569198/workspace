package com.reign.framework.common.concurrent;

import java.util.concurrent.locks.*;
import java.util.concurrent.*;

public class ReadWriteLockItem
{
    private final ReadWriteLock readWriteLock;
    private final Lock readLock;
    private final Lock writeLock;
    
    public ReadWriteLockItem() {
        this.readWriteLock = new ReentrantReadWriteLock();
        this.readLock = this.readWriteLock.readLock();
        this.writeLock = this.readWriteLock.writeLock();
    }
    
    public void lock(final LockType lockType) {
        this.getLock(lockType).lock();
    }
    
    public void unlock(final LockType lockType) {
        this.getLock(lockType).unlock();
    }
    
    public boolean tryLock(final LockType lockType, final long msec) throws InterruptedException {
        return this.getLock(lockType).tryLock(msec, TimeUnit.MILLISECONDS);
    }
    
    public boolean tryLock(final LockType lockType) {
        return this.getLock(lockType).tryLock();
    }
    
    private Lock getLock(final LockType lockType) {
        switch (lockType) {
            case WRITE: {
                return this.writeLock;
            }
            case READ: {
                return this.readLock;
            }
            default: {
                return null;
            }
        }
    }
}
