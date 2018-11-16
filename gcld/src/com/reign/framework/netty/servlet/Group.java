package com.reign.framework.netty.servlet;

import java.util.concurrent.*;

public interface Group
{
    Group createGroup(final String p0);
    
    boolean join(final Session p0);
    
    boolean leave(final String p0);
    
    void clear();
    
    void notify(final Object p0);
    
    void notify(final String p0, final Object p1);
    
    int[] notify(final Object p0, final String... p1);
    
    String getGroupId();
    
    ConcurrentMap<String, Session> getUserMap();
}
