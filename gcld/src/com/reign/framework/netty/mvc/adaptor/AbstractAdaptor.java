package com.reign.framework.netty.mvc.adaptor;

import java.lang.reflect.*;
import com.reign.framework.netty.mvc.annotation.*;
import java.lang.annotation.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.adaptor.injector.*;

public abstract class AbstractAdaptor implements HttpAdaptor
{
    protected ParamInjector[] injectors;
    
    @Override
    public void init(final Method method) {
        final Class[] argTypes = method.getParameterTypes();
        this.injectors = new ParamInjector[argTypes.length];
        final Annotation[][] annss = method.getParameterAnnotations();
        for (int i = 0; i < annss.length; ++i) {
            final Annotation[] anns = annss[i];
            RequestParam requestParam = null;
            SessionParam sessionParam = null;
            for (int x = 0; x < anns.length; ++x) {
                if (anns[x] instanceof RequestParam) {
                    requestParam = (RequestParam)anns[x];
                    break;
                }
                if (anns[x] instanceof SessionParam) {
                    sessionParam = (SessionParam)anns[x];
                    break;
                }
            }
            if (sessionParam != null) {
                this.injectors[i] = new SessionInjector(sessionParam.value());
            }
            else {
                this.injectors[i] = this.evalInjectorByParamType(argTypes[i]);
                if (this.injectors[i] == null) {
                    this.injectors[i] = this.evalInjector(argTypes[i], requestParam);
                }
            }
        }
    }
    
    @Override
    public Object[] adapt(final ServletContext servletContext, final Request request, final Response response) {
        final Object[] args = new Object[this.injectors.length];
        for (int i = 0; i < this.injectors.length; ++i) {
            args[i] = this.injectors[i].get(servletContext, request, response);
        }
        return args;
    }
    
    public abstract ParamInjector evalInjector(final Class<?> p0, final RequestParam p1);
    
    private ParamInjector evalInjectorByParamType(final Class<?> clazz) {
        if (Request.class.isAssignableFrom(clazz)) {
            return new RequestInjector();
        }
        if (Response.class.isAssignableFrom(clazz)) {
            return new ResponseInjector();
        }
        return null;
    }
}
