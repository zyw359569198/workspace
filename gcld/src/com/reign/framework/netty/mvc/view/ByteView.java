package com.reign.framework.netty.mvc.view;

import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.framework.netty.mvc.exception.*;
import com.reign.framework.common.*;
import com.reign.framework.netty.util.*;

public class ByteView extends DefaultView
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
    }
    
    @Override
    public void doRender(final Result<?> result, final Request request, final Response response) throws Exception {
        if (!(result instanceof ByteResult)) {
            throw new NotMatchResultException("un match result type, except ", ByteResult.class);
        }
        final ByteResult byteResult = (ByteResult)result;
        if (ServerProtocol.TCP.equals(response.getProtocol())) {
            response.write(this.convert(request, byteResult.getResult()));
        }
        else {
            response.write(WrapperUtil.wrapperBody(byteResult.getResult(), this.compress()));
        }
    }
}
