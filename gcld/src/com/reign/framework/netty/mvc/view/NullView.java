package com.reign.framework.netty.mvc.view;

import com.reign.framework.netty.mvc.result.*;
import com.reign.framework.netty.servlet.*;

public class NullView extends DefaultView
{
    @Override
    public void setCompress(final boolean compress) {
    }
    
    @Override
    public boolean compress() {
        return false;
    }
    
    @Override
    public void prepareRender(final Response response) {
    }
    
    @Override
    public void doRender(final Result<?> result, final Request request, final Response response) throws Exception {
    }
}
