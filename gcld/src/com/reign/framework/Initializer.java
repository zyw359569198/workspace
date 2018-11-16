package com.reign.framework;

import org.jboss.netty.bootstrap.*;

public interface Initializer
{
    void init() throws InstantiationException, IllegalAccessException;
    
    void destory();
    
    void afterInit() throws InstantiationException, IllegalAccessException;
    
    void initServerBootstrap(final ServerBootstrap p0);
    
    int getTcpPort();
    
    int getHttpPort();
}
