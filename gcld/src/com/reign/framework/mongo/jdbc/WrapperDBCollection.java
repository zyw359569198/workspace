package com.reign.framework.mongo.jdbc;

import java.util.concurrent.locks.*;

public class WrapperDBCollection
{
    private DBCollection dbCollection;
    private ReadWriteLock readWriteLock;
    
    public WrapperDBCollection(final DBCollection dbCollection) {
        this.dbCollection = dbCollection;
        this.readWriteLock = new ReentrantReadWriteLock(false);
    }
    
    public DBCollection getDbCollection() {
        return this.dbCollection;
    }
    
    public Lock getReadLock() {
        return this.readWriteLock.readLock();
    }
    
    public Lock getWriteLock() {
        return this.readWriteLock.writeLock();
    }
}
