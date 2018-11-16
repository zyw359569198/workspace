package com.reign.framework.netty.mvc.adaptor;

import java.lang.reflect.*;
import com.reign.framework.netty.servlet.*;

public interface HttpAdaptor
{
    void init(final Method p0);
    
    Object[] adapt(final ServletContext p0, final Request p1, final Response p2);
}
