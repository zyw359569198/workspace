package com.reign.framework.netty.mvc.adaptor.injector;

import com.reign.framework.netty.servlet.*;
import com.reign.framework.common.*;

public class ArrayInjector implements ParamInjector
{
    protected String name;
    protected Class<?> type;
    
    public ArrayInjector(final String name, final Class<?> type) {
        this.name = name;
        this.type = type;
    }
    
    @Override
    public Object get(final ServletContext servletContext, final Request request, final Response response) {
        final String[] params = request.getParamterValues(this.name);
        return Lang.castTo(params, this.type);
    }
}
