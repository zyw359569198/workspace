package com.reign.framework.netty.mvc.view;

import com.reign.framework.netty.mvc.result.*;
import com.reign.framework.netty.servlet.*;

public interface ResponseView
{
    void render(final Result<?> p0, final Request p1, final Response p2) throws Exception;
    
    void setCompress(final boolean p0);
    
    boolean compress();
}
