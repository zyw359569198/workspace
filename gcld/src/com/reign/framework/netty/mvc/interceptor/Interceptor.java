package com.reign.framework.netty.mvc.interceptor;

import com.reign.framework.netty.mvc.servlet.*;
import java.util.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;

public interface Interceptor
{
    Result<?> interceptor(final ActionInvocation p0, final Iterator<Interceptor> p1, final Request p2, final Response p3) throws Exception;
}
