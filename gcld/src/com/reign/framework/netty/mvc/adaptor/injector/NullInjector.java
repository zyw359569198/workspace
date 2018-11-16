package com.reign.framework.netty.mvc.adaptor.injector;

import com.reign.framework.netty.servlet.*;
import com.reign.framework.common.*;

public class NullInjector implements ParamInjector
{
    private Class<?> type;
    
    public NullInjector(final Class<?> clazz) {
        this.type = clazz;
    }
    
    @Override
    public Object get(final ServletContext servletContext, final Request request, final Response response) {
        return Lang.getDefaultValue(this.type);
    }
}
