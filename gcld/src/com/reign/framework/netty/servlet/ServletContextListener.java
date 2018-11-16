package com.reign.framework.netty.servlet;

public interface ServletContextListener
{
    void contextInitialized(final ServletContext p0);
    
    void contextDestoryed(final ServletContext p0);
}
