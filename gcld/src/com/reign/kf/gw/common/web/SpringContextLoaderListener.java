package com.reign.kf.gw.common.web;

import org.apache.commons.logging.*;
import com.reign.framework.netty.servlet.*;
import org.springframework.context.*;
import org.springframework.context.support.*;

public class SpringContextLoaderListener implements ServletContextListener
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
        SpringContextLoaderListener.log.info("Inializing Sprint root WebApplicationContext");
        final long startTime = System.currentTimeMillis();
        final ApplicationContext ac = this.createApplicationContext(sc);
        sc.setAttribute(ServletContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ac);
        final long elapsedTime = System.currentTimeMillis() - startTime;
        SpringContextLoaderListener.log.info("Root WebApplicationContext: initialization completed in " + elapsedTime + "ms");
    }
    
    private ApplicationContext createApplicationContext(final ServletContext sc) {
        final ClassPathXmlApplicationContext cpxac = new ClassPathXmlApplicationContext("applicationContext.xml");
        return cpxac;
    }
    
    @Override
	public void contextDestoryed(final ServletContext sc) {
    }
}
