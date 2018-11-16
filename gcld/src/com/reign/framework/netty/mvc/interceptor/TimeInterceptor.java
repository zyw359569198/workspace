package com.reign.framework.netty.mvc.interceptor;

import com.reign.framework.netty.mvc.servlet.*;
import java.util.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;

public class TimeInterceptor implements Interceptor
{
    @Override
    public Result<?> interceptor(final ActionInvocation invocation, final Iterator<Interceptor> interceptors, final Request request, final Response response) throws Exception {
        final Result<?> obj = invocation.invoke(interceptors, request, response);
        return obj;
    }
}
