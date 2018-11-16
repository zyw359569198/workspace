package com.reign.framework.jdbc.orm.cache;

public final class CacheStatistics
{
    public static int disableHits;
    public static int hits;
    public static int miss;
    public static int queryHits;
    public static int queryMiss;
    public static int noCache;
    public static boolean enable;
    
    static {
        CacheStatistics.enable = true;
    }
    
    public static final void addHits() {
        if (!CacheStatistics.enable) {
            return;
        }
        ++CacheStatistics.hits;
    }
    
    public static final void addMiss() {
        if (!CacheStatistics.enable) {
            return;
        }
        ++CacheStatistics.miss;
    }
    
    public static final double getRatio() {
        return (int)(CacheStatistics.hits * 1.0 * 100.0 / (CacheStatistics.hits + CacheStatistics.miss)) / 100.0;
    }
    
    public static final void addQueryHits() {
        if (!CacheStatistics.enable) {
            return;
        }
        ++CacheStatistics.queryHits;
    }
    
    public static final void addQueryMiss() {
        if (!CacheStatistics.enable) {
            return;
        }
        ++CacheStatistics.queryMiss;
    }
    
    public static final double getQueryRatio() {
        return (int)(CacheStatistics.queryHits * 1.0 * 100.0 / (CacheStatistics.queryHits + CacheStatistics.queryMiss)) / 100.0;
    }
    
    public static final double getTotalRatio() {
        return (int)((CacheStatistics.queryHits + CacheStatistics.hits) * 1.0 * 100.0 / (CacheStatistics.queryHits + CacheStatistics.queryMiss + CacheStatistics.hits + CacheStatistics.miss + CacheStatistics.noCache + CacheStatistics.disableHits)) / 100.0;
    }
    
    public static final void addNoCache() {
        if (!CacheStatistics.enable) {
            return;
        }
        ++CacheStatistics.noCache;
    }
    
    public static final void addDisableHits() {
        if (!CacheStatistics.enable) {
            return;
        }
        ++CacheStatistics.disableHits;
    }
}
