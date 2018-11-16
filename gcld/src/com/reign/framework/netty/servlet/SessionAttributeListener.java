package com.reign.framework.netty.servlet;

public interface SessionAttributeListener
{
    void attributeAdded(final SessionAttributeEvent p0);
    
    void attributeRemoved(final SessionAttributeEvent p0);
    
    void attributeReplaced(final SessionAttributeEvent p0);
}
