package com.reign.framework.netty.servlet;

public class SessionEvent
{
    public Session session;
    
    public SessionEvent(final Session session) {
        this.session = session;
    }
}
