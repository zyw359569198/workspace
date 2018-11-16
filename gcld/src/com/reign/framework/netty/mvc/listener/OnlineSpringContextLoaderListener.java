package com.reign.framework.netty.mvc.listener;

import org.apache.commons.logging.*;
import com.reign.framework.netty.servlet.*;
import org.springframework.context.*;

public class OnlineSpringContextLoaderListener implements ServletContextListener
{
    private static final Log log;
    
    static {
        log = LogFactory.getLog(SpringContextLoaderListener.class);
    }
    
    @Override
    public void contextInitialized(final ServletContext sc) {
        this.init(sc);
    }
    
    private void init(final ServletContext sc) {
        if (sc.getAttribute(ServletContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) != null) {
            throw new IllegalStateException("Can not initialize context because there is already exists");
        }
        OnlineSpringContextLoaderListener.log.info("Inializing Sprint root WebApplicationContext");
        final long startTime = System.currentTimeMillis();
        final ApplicationContext ac = this.createApplicationContext(sc);
        sc.setAttribute(ServletContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ac);
        final long elapsedTime = System.currentTimeMillis() - startTime;
        OnlineSpringContextLoaderListener.log.info("Root WebApplicationContext: initialization completed in " + elapsedTime + "ms");
    }
    
    private ApplicationContext createApplicationContext(final ServletContext sc) {
        final MyXmlApplicationContext context = new MyXmlApplicationContext(sc);
        context.refresh();
        return context;
    }
    
    @Override
    public void contextDestoryed(final ServletContext sc) {
    }
}
