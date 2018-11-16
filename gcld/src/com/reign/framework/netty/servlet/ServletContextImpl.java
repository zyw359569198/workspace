package com.reign.framework.netty.servlet;

import java.util.concurrent.*;

public class ServletContextImpl implements ServletContext
{
    private ConcurrentMap<String, Object> map;
    private ServletConfig config;
    
    public ServletContextImpl() {
        this.map = new ConcurrentHashMap<String, Object>();
    }
    
    public ServletContextImpl(final ServletConfig config) {
        this.map = new ConcurrentHashMap<String, Object>();
        this.config = config;
    }
    
    @Override
    public Object getAttribute(final String key) {
        return this.map.get(key);
    }
    
    @Override
    public Object setAttribute(final String key, final Object value) {
        return this.map.put(key, value);
    }
    
    @Override
    public boolean removeAttribute(final String key) {
        return this.map.remove(key) != null;
    }
    
    @Override
    public void invalidate() {
        this.map.clear();
    }
    
    @Override
    public Object getInitParam(final String paramName) {
        if (this.config == null) {
            return null;
        }
        return this.config.getInitParam(paramName);
    }
}
