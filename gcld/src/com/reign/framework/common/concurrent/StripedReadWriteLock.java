package com.reign.framework.common.concurrent;

import java.util.concurrent.*;
import java.util.*;

public class StripedReadWriteLock
{
    public static final int DEFAULT_NUMBER_OF_MUTEXES = 2048;
    private final ReadWriteLockItem[] mutexes;
    private final int numberOfStripes;
    
    public StripedReadWriteLock() {
        this(2048);
    }
    
    public StripedReadWriteLock(final int numberOfStripes) {
        if (numberOfStripes % 2 != 0) {
            throw new RuntimeException("Cannot create a CacheLockProvider with an odd number of stripes");
        }
        if (numberOfStripes == 0) {
            throw new RuntimeException("A zero size CacheLockProvider does not have useful semantics.");
        }
        this.numberOfStripes = numberOfStripes;
        this.mutexes = new ReadWriteLockItem[numberOfStripes];
        for (int i = 0; i < numberOfStripes; ++i) {
            this.mutexes[i] = new ReadWriteLockItem();
        }
    }
    
    public ReadWriteLockItem getLockForKey(final Object key) {
        final int lockNumber = ConcurrencyUtil.selectLock(key, this.numberOfStripes);
        return this.mutexes[lockNumber];
    }
    
    public ReadWriteLockItem[] getAndWriteLockAllForKeys(final Object... keys) {
        final SortedMap<Integer, ReadWriteLockItem> locksMap = this.getLockMap(keys);
        final ReadWriteLockItem[] locks = new ReadWriteLockItem[locksMap.size()];
        int i = 0;
        for (final Map.Entry<Integer, ReadWriteLockItem> entry : locksMap.entrySet()) {
            entry.getValue().lock(LockType.WRITE);
            locks[i++] = entry.getValue();
        }
        return locks;
    }
    
    public ReadWriteLockItem[] getAndWriteLockAllForKeys(final long timeout, final Object... keys) throws TimeoutException {
        final SortedMap<Integer, ReadWriteLockItem> locksMap = this.getLockMap(keys);
        final List<ReadWriteLockItem> heldLocks = new ArrayList<ReadWriteLockItem>();
        final ReadWriteLockItem[] locks = new ReadWriteLockItem[locksMap.size()];
        int i = 0;
        for (final Map.Entry<Integer, ReadWriteLockItem> entry : locksMap.entrySet()) {
            boolean lockHeld;
            try {
                final ReadWriteLockItem lock = entry.getValue();
                lockHeld = lock.tryLock(LockType.WRITE, timeout);
                if (lockHeld) {
                    heldLocks.add(lock);
                }
            }
            catch (InterruptedException e) {
                lockHeld = false;
            }
            if (!lockHeld) {
                for (int j = heldLocks.size() - 1; j >= 0; --j) {
                    final ReadWriteLockItem lock2 = heldLocks.get(j);
                    lock2.unlock(LockType.WRITE);
                }
                throw new TimeoutException("could not acquire all locks in " + timeout + " ms");
            }
            locks[i++] = entry.getValue();
        }
        return locks;
    }
    
    public void unlockWriteLockForAllKeys(final Object... keys) {
        final SortedMap<Integer, ReadWriteLockItem> locks = this.getLockMap(keys);
        for (final Map.Entry<Integer, ReadWriteLockItem> entry : locks.entrySet()) {
            entry.getValue().unlock(LockType.WRITE);
        }
    }
    
    private SortedMap<Integer, ReadWriteLockItem> getLockMap(final Object... keys) {
        final SortedMap<Integer, ReadWriteLockItem> locks = new TreeMap<Integer, ReadWriteLockItem>();
        for (final Object key : keys) {
            final int lockNumber = ConcurrencyUtil.selectLock(key, this.numberOfStripes);
            final ReadWriteLockItem lock = this.mutexes[lockNumber];
            locks.put(lockNumber, lock);
        }
        return locks;
    }
}
