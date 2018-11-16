package com.reign.framework.common.cache;

import java.util.*;

public interface ICache<K, V>
{
    V get(final K p0);
    
    void put(final K p0, final V p1);
    
    void reload() throws Exception;
    
    void remove(final K p0);
    
    void clear();
    
    Vector<V> getModels();
    
    void setSDataLoader(final SDataLoader p0);
    
    SDataLoader getSDataLoader();
}
