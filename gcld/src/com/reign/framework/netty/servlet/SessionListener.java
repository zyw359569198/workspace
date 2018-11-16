package com.reign.framework.netty.servlet;

import java.util.*;

public interface SessionListener extends EventListener
{
    void sessionCreated(final SessionEvent p0);
    
    void sessionDestroyed(final SessionEvent p0);
}
