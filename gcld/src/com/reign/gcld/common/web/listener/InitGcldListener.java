package com.reign.gcld.common.web.listener;

import com.reign.framework.netty.mvc.*;
import com.reign.framework.netty.mvc.spring.*;
import org.springframework.context.*;
import com.reign.framework.netty.servlet.*;
import com.reign.gcld.common.web.*;
import com.reign.gcld.kfgz.service.*;

public class InitGcldListener implements InitProjectListener
{
    protected ServletContext context;
    protected static ObjectFactory objectFactory;
    
    public ServletContext getServletContext() {
        return this.context;
    }
    
    private ObjectFactory getObjectFactory() {
        if (InitGcldListener.objectFactory == null) {
            final SpringObjectFactory factory = new SpringObjectFactory();
            final ApplicationContext applicationContext = (ApplicationContext)this.getServletContext().getAttribute(ServletContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            if (applicationContext == null) {
                InitGcldListener.objectFactory = new ObjectFactory();
            }
            else {
                factory.setApplicationContext(applicationContext);
                InitGcldListener.objectFactory = (ObjectFactory)factory;
            }
        }
        return InitGcldListener.objectFactory;
    }
    
    @Override
	public void init(final ServletContext context, final NettyConfig config) {
        this.context = context;
        try {
            final IGcldInitManager initGcld = (IGcldInitManager)this.getObjectFactory().buildBean(GcldInitManager.class);
            initGcld.sysInit();
            final ApplicationContext applicationContext = (ApplicationContext)this.getServletContext().getAttribute(ServletContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
            final IKfgzSeasonService kfgzSeasonService = (IKfgzSeasonService)applicationContext.getBean("kfgzSeasonService");
            kfgzSeasonService.init();
        }
        catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e2) {
            e2.printStackTrace();
        }
    }
}
