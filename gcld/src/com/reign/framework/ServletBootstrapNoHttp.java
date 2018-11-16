package com.reign.framework;

import org.jboss.netty.bootstrap.*;
import org.jboss.netty.channel.socket.nio.*;
import java.util.concurrent.*;
import com.reign.framework.netty.tcp.*;
import org.jboss.netty.channel.*;
import java.net.*;
import com.reign.framework.netty.servlet.*;
import java.util.*;

public class ServletBootstrapNoHttp
{
    private Servlet servlet;
    private ServletConfig sc;
    private NettyConfig nc;
    private ServletContext servletContext;
    private static final String CONFIG_FILE_NAME = "conf.xml";
    
    public void startup() throws Exception {
        final XmlConfig config = new XmlConfig("conf.xml");
        this.sc = config.getServletConfig();
        this.nc = config.getNettyConfig();
        this.servletContext = new ServletContextImpl(this.sc);
        final List<Class<?>> listenerList = this.sc.getListeners();
        for (final Class<?> clazz : listenerList) {
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
        final ServerBootstrap sb = new ServerBootstrap();
        final NioServerSocketChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        sb.setFactory(factory);
        sb.setPipelineFactory(new TcpServletPipelineFactory(this.servlet, this.servletContext, this.nc));
        sb.setOptions(this.nc.getTcpParams());
        sb.bind(new InetSocketAddress((int)this.nc.getInitParam("port")));
        SessionManager.getInstance().setServletConfig(this.sc);
        SessionManager.getInstance().startSessionCheckThread();
        for (final Class<?> clazz2 : listenerList) {
            if (InitProjectListener.class.isAssignableFrom(clazz2)) {
                final InitProjectListener listener4 = (InitProjectListener)clazz2.newInstance();
                listener4.init(this.servletContext, this.nc);
            }
        }
    }
    
    public static void main(final String[] args) throws Exception {
        final ServletBootstrapNoHttp sbs = new ServletBootstrapNoHttp();
        sbs.startup();
    }
}
