package com.reign.framework.netty.mvc.adaptor.injector;

import com.reign.framework.netty.servlet.*;

public interface ParamInjector
{
    Object get(final ServletContext p0, final Request p1, final Response p2);
}
