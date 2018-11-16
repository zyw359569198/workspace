package com.reign.framework.netty.mvc.view;

import com.reign.framework.netty.mvc.result.*;
import com.reign.framework.netty.servlet.*;
import org.jboss.netty.buffer.*;
import com.reign.framework.netty.util.*;
import java.io.*;

public abstract class DefaultView implements ResponseView
{
    @Override
    public void render(final Result<?> result, final Request request, final Response response) throws Exception {
        this.prepareRender(response);
        this.doRender(result, request, response);
    }
    
    public ChannelBuffer convert(final Request request, final byte[] body) throws IOException {
        return WrapperUtil.wrapper(request.getCommand(), request.getRequestId(), body, this.compress());
    }
    
    public abstract void prepareRender(final Response p0);
    
    public abstract void doRender(final Result<?> p0, final Request p1, final Response p2) throws Exception;
}
