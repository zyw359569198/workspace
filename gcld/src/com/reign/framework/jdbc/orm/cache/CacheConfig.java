package com.reign.framework.jdbc.orm.cache;

public class CacheConfig
{
    public String name;
    public int maxLiveTime;
    public AccessType accessType;
    
    public CacheConfig(final String name, final int maxLiveTime) {
        this.name = name;
        this.maxLiveTime = maxLiveTime;
    }
    
    public int getMaxLiveTime() {
        return this.maxLiveTime;
    }
    
    public void setMaxLiveTime(final int maxLiveTime) {
        this.maxLiveTime = maxLiveTime;
    }
    
    public void setAccessType(final AccessType accessType) {
        this.accessType = accessType;
    }
}
