package com.reign.framework.netty.http.handler;

public interface ChunkAction<T>
{
    void invoke(final T p0);
}
