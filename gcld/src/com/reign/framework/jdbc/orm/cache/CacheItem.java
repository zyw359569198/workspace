package com.reign.framework.jdbc.orm.cache;

import java.io.*;

public interface CacheItem<T> extends Serializable
{
    T getValue();
    
    boolean isWritable();
}
