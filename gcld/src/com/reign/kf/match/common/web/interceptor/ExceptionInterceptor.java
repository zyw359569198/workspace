package com.reign.kf.match.common.web.interceptor;

import com.reign.framework.netty.mvc.interceptor.*;
import org.apache.commons.logging.*;
import com.reign.framework.json.*;
import com.reign.framework.netty.mvc.servlet.*;
import java.util.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;

public class ExceptionInterceptor implements Interceptor
{
    private static Log errorlog;
    private static final ByteResult E0010_JSONRESULT;
    
    static {
        ExceptionInterceptor.errorlog = LogFactory.getLog("com.reign.gcld.error");
        E0010_JSONRESULT = new ByteResult(JsonBuilder.getExceptionJson(State.EXCEPTION, "E0010"));
    }
    
    @Override
	public Result<?> interceptor(final ActionInvocation invocation, final Iterator<Interceptor> interceptors, final Request request, final Response response) throws Exception {
        final String actionName = invocation.getActionName();
        final String methodName = invocation.getMethodName();
        final String ip = request.getRemoteAddress().getAddress().getHostAddress();
        final long starttime = System.currentTimeMillis();
        boolean exception = false;
        try {
            final Result<?> obj = invocation.invoke(interceptors, request, response);
            return obj;
        }
        catch (Throwable t) {
            ExceptionInterceptor.errorlog.info("", t);
            exception = true;
            t.printStackTrace();
            return ExceptionInterceptor.E0010_JSONRESULT;
        }
    }
}
