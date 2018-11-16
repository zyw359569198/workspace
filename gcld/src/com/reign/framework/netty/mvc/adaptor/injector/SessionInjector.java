package com.reign.framework.netty.mvc.adaptor.injector;

import com.reign.framework.netty.servlet.*;

public class SessionInjector implements ParamInjector
{
    private String key;
    
    public SessionInjector(final String key) {
        this.key = key;
    }
    
    @Override
    public Object get(final ServletContext servletContext, final Request request, final Response response) {
        return request.getSession().getAttribute(this.key);
    }
}
