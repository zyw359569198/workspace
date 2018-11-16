package com.reign.framework.netty.mvc.adaptor.injector;

import com.reign.framework.netty.servlet.*;

public class RequestInjector implements ParamInjector
{
    @Override
    public Object get(final ServletContext servletContext, final Request request, final Response response) {
        return request;
    }
}
