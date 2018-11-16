package com.reign.framework.netty.mvc.view;

import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.framework.netty.mvc.exception.*;
import com.reign.framework.common.*;
import com.reign.framework.netty.util.*;

public class HtmlView extends DefaultView
{
    private boolean compress;
    
    @Override
    public void setCompress(final boolean compress) {
        this.compress = compress;
    }
    
    @Override
    public boolean compress() {
        return this.compress;
    }
    
    @Override
    public void prepareRender(final Response response) {
        response.addHeader("Content-Type", "text/html");
    }
    
    @Override
    public void doRender(final Result<?> result, final Request request, final Response response) throws Exception {
        if (!(result instanceof HtmlResult)) {
            throw new NotMatchResultException("un match result type, except ", HtmlResult.class);
        }
        final HtmlResult htmlResult = (HtmlResult)result;
        if (ServerProtocol.TCP.equals(response.getProtocol())) {
            throw new UnsupportedOperationException("html view not supported tcp");
        }
        response.write(WrapperUtil.wrapperBody(htmlResult.getResult(), this.compress()));
    }
}
