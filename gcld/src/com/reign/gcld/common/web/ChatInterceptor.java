package com.reign.gcld.common.web;

import com.reign.framework.netty.mvc.interceptor.*;
import com.reign.gcld.chat.service.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.mvc.servlet.*;
import java.util.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;

public class ChatInterceptor implements Interceptor
{
    @Autowired
    private IChatService chatService;
    
    @Override
	public Result<?> interceptor(final ActionInvocation invocation, final Iterator<Interceptor> interceptors, final Request request, final Response response) throws Exception {
        if (!invocation.getIsInChatTransactional()) {
            return invocation.invoke(interceptors, request, response);
        }
        this.chatService.startTransactional();
        try {
            final Result<?> ret = invocation.invoke(interceptors, request, response);
            this.chatService.commitTransactional();
            return ret;
        }
        finally {
            this.chatService.endTransactional();
        }
    }
}
