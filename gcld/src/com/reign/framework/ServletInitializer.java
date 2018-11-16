package com.reign.framework;

import org.jboss.netty.bootstrap.*;
import java.util.*;
import org.jboss.netty.channel.*;
import com.reign.framework.netty.tcp.*;
import com.reign.framework.netty.http.*;
import com.reign.framework.netty.servlet.*;
import java.lang.reflect.*;

public class ServletInitializer implements Initializer
{
    private Servlet servlet;
    private ServletConfig sc;
    private NettyConfig nc;
    private ServletContext servletContext;
    private static final String CONFIG_FILE_NAME = "conf.xml";
    private List<Class<?>> listenerList;
    
    @Override
    public void init() throws InstantiationException, IllegalAccessException {
        this.setPropertiesPath();
        final XmlConfig config = new XmlConfig("conf.xml");
        this.sc = config.getServletConfig();
        this.nc = config.getNettyConfig();
        this.servletContext = new ServletContextImpl(this.sc);
        this.listenerList = this.sc.getListeners();
        for (final Class<?> clazz : this.listenerList) {
            if (ServletContextListener.class.isAssignableFrom(clazz)) {
                final ServletContextListener listener = (ServletContextListener)clazz.newInstance();
                listener.contextInitialized(this.servletContext);
            }
            if (SessionListener.class.isAssignableFrom(clazz)) {
                final SessionListener listener2 = (SessionListener)clazz.newInstance();
                SessionManager.getInstance().addSessionListener(listener2);
            }
            if (SessionAttributeListener.class.isAssignableFrom(clazz)) {
                final SessionAttributeListener listener3 = (SessionAttributeListener)clazz.newInstance();
                SessionManager.getInstance().addSessionAttributeListener(listener3);
            }
        }
        (this.servlet = this.sc.getServletClass().newInstance()).init(this.sc, this.servletContext);
    }
    
    @Override
    public int getTcpPort() {
        return (int)this.nc.getInitParam("port");
    }
    
    @Override
    public int getHttpPort() {
        return (int)this.nc.getInitParam("httpPort");
    }
    
    @Override
    public void initServerBootstrap(final ServerBootstrap bootstrap) {
        bootstrap.setOptions(this.nc.getTcpParams());
    }
    
    public ChannelPipelineFactory getTcpChannelPipelineFactory() throws Exception {
        return new TcpServletPipelineFactory(this.servlet, this.servletContext, this.nc);
    }
    
    public ChannelPipelineFactory getHttpChannelPipelineFactory() {
        return new HttpServletPipelineFactory(this.servlet, this.servletContext, this.nc);
    }
    
    @Override
    public void afterInit() throws InstantiationException, IllegalAccessException {
        SessionManager.getInstance().setServletConfig(this.sc);
        SessionManager.getInstance().startSessionCheckThread();
        for (final Class<?> clazz : this.listenerList) {
            if (InitProjectListener.class.isAssignableFrom(clazz)) {
                final InitProjectListener listener = (InitProjectListener)clazz.newInstance();
                listener.init(this.servletContext, this.nc);
            }
        }
    }
    
    @Override
    public void destory() {
    }
    
    private void setPropertiesPath() {
        final String token = "${start.home}";
        String path = "${start.home}/apps/";
        int i = -1;
        while ((i = path.indexOf(token)) >= 0) {
            if (i > 0) {
                path = String.valueOf(path.substring(0, i)) + System.getProperty("start.home") + path.substring(i + token.length());
            }
            else {
                path = String.valueOf(System.getProperty("start.home")) + path.substring(token.length());
            }
        }
        try {
            final Class<?> clazz = this.getClass().getClassLoader().loadClass("com.reign.ndbd.common.ListenerConstants");
            final Field field = clazz.getField("WEB_PATH");
            field.set(clazz, path);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
