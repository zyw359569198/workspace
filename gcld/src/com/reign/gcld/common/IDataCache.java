package com.reign.gcld.common;

public interface IDataCache<T1, T2>
{
    T2 get(final T1 p0);
    
    T2 put(final T1 p0, final T2 p1);
    
    void remove(final T1 p0);
}
