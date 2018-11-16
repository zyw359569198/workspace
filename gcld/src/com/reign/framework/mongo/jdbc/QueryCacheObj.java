package com.reign.framework.mongo.jdbc;

public class QueryCacheObj
{
    public String key;
    public Object[] value;
    
    public String getKey() {
        return this.key;
    }
    
    public void setKey(final String key) {
        this.key = key;
    }
    
    public Object[] getValue() {
        return this.value;
    }
    
    public void setValue(final Object[] value) {
        this.value = value;
    }
}
