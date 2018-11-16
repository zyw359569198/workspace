package com.reign.gcld.asynchronousDB.cache.obj;

public interface DBCacheObj
{
    void caculateThreshold(final Object... p0);
    
    void addValue(final int p0);
    
    void minusValue(final int p0);
    
    boolean needToCommit();
    
    void commitAsynchronousDBOperation();
    
    void reset();
}
