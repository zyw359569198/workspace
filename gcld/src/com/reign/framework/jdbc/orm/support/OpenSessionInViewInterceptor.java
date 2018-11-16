package com.reign.framework.jdbc.orm.support;

import com.reign.framework.netty.mvc.interceptor.*;
import com.reign.framework.jdbc.orm.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.netty.mvc.servlet.*;
import java.util.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import org.springframework.transaction.support.*;
import com.reign.framework.jdbc.orm.session.*;

public class OpenSessionInViewInterceptor implements Interceptor
{
    @Autowired
    private JdbcFactory jdbcFactory;
    
    @Override
    public Result<?> interceptor(final ActionInvocation invocation, final Iterator<Interceptor> interceptors, final Request request, final Response response) throws Exception {
        boolean participate = false;
        if (TransactionSynchronizationManager.hasResource(this.jdbcFactory)) {
            participate = true;
        }
        else {
            final JdbcSession session = this.jdbcFactory.openSession();
            TransactionSynchronizationManager.bindResource(this.jdbcFactory, new JdbcSessionHolder(session));
        }
        try {
            return invocation.invoke(interceptors, request, response);
        }
        finally {
            if (!participate) {
                final JdbcSessionHolder sessionHolder = (JdbcSessionHolder)TransactionSynchronizationManager.unbindResource(this.jdbcFactory);
                sessionHolder.getSession().close();
            }
        }
    }
}
