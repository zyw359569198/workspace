package com.reign.framework.netty.servlet;

public class SessionAttributeEvent extends SessionEvent
{
    public String key;
    public Object value;
    
    public SessionAttributeEvent(final String key, final Object value, final Session session) {
        super(session);
        this.key = key;
        this.value = value;
    }
}
