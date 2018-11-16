package com.reign.framework.netty.servlet;

public interface ServletContext
{
    public static final String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = String.valueOf(ServletContext.class.getName()) + ".Spring.Root";
    public static final String ROOT_WEB_APPLICATION_SERVLET_ATTRIBUTE = String.valueOf(ServletContext.class.getName()) + ".Servlet.Root";
    
    Object getAttribute(final String p0);
    
    Object setAttribute(final String p0, final Object p1);
    
    boolean removeAttribute(final String p0);
    
    void invalidate();
    
    Object getInitParam(final String p0);
}
