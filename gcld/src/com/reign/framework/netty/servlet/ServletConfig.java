package com.reign.framework.netty.servlet;

import java.util.*;

public interface ServletConfig
{
    String getServletName();
    
    Class<? extends Servlet> getServletClass();
    
    List<Class<?>> getListeners();
    
    Object getInitParam(final String p0);
    
    Map<String, Object> getInitParams();
    
    long getSessionTimeOutMillis();
    
    int getSessionTickTime();
}
