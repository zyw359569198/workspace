package com.reign.framework.mongo;

import java.util.concurrent.atomic.*;
import java.util.*;

public class LockFactory
{
    private static AtomicInteger lockIdGenerator;
    
    static {
        LockFactory.lockIdGenerator = new AtomicInteger(1);
    }
    
    public static MongoLock[] createLock(final int num) {
        final MongoLock[] locks = new MongoLock[num];
        for (int i = 0; i < locks.length; ++i) {
            locks[i] = new MongoLock(LockFactory.lockIdGenerator.getAndIncrement());
        }
        return locks;
    }
    
    public static void lock(final MongoLock[] locks, final LockParam[] lockModes) throws InterruptedException {
        if (locks.length != lockModes.length) {
            throw new IllegalArgumentException("locks length doesn't equal lockmodes length");
        }
        final Map<Integer, LockParam> map = new HashMap<Integer, LockParam>();
        for (int i = 0; i < locks.length; ++i) {
            map.put(locks[i].lockId, lockModes[i]);
        }
        Arrays.sort(locks);
        for (final MongoLock lock : locks) {
            lock.lock(map.get(lock.lockId));
        }
    }
    
    public static void lock(final MongoLock[] locks) {
        Arrays.sort(locks);
        for (final MongoLock lock : locks) {
            lock.lock();
        }
    }
    
    public static void unlock(final MongoLock... locks) {
        Arrays.sort(locks);
        for (int i = locks.length - 1; i >= 0; --i) {
            locks[i].unlock();
        }
    }
}
