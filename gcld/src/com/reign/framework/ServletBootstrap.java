package com.reign.framework;

import org.apache.commons.logging.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.plugin.*;
import org.jboss.netty.bootstrap.*;
import org.jboss.netty.channel.socket.nio.*;
import java.util.concurrent.*;
import com.reign.framework.netty.tcp.*;
import org.jboss.netty.channel.*;
import java.net.*;
import com.reign.framework.netty.http.*;
import java.util.*;

public class ServletBootstrap
{
    private static final Log log;
    private Servlet servlet;
    private ServletConfig sc;
    private NettyConfig nc;
    private ServletContext servletContext;
    private static final String CONFIG_FILE_NAME = "conf.xml";
    
    static {
        log = LogFactory.getLog((Class)ServletBootstrap.class);
    }
    
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
        (this.servlet = (Servlet)this.sc.getServletClass().newInstance()).init(this.sc, this.servletContext);
        for (final Class<?> clazz : listenerList) {
            if (InitProjectListener.class.isAssignableFrom(clazz)) {
                final InitProjectListener listener4 = (InitProjectListener)clazz.newInstance();
                listener4.init(this.servletContext, this.nc);
            }
        }
        PluginLoader.getInstance().check();
        final ServerBootstrap sb = new ServerBootstrap();
        final NioServerSocketChannelFactory factory = new NioServerSocketChannelFactory((Executor)Executors.newCachedThreadPool(), (Executor)Executors.newCachedThreadPool());
        sb.setFactory((ChannelFactory)factory);
        sb.setPipelineFactory((ChannelPipelineFactory)new TcpServletPipelineFactory(this.servlet, this.servletContext, this.nc));
        sb.setOptions((Map)this.nc.getTcpParams());
        sb.bind((SocketAddress)new InetSocketAddress((int)this.nc.getInitParam("port")));
        ServletBootstrap.log.info((Object)("listen tcp port " + this.nc.getInitParam("port") + " success"));
        final NioServerSocketChannelFactory httpChannelFactory = new NioServerSocketChannelFactory((Executor)Executors.newCachedThreadPool(), (Executor)Executors.newCachedThreadPool());
        final ServerBootstrap httpsb = new ServerBootstrap();
        httpsb.setFactory((ChannelFactory)httpChannelFactory);
        httpsb.setPipelineFactory((ChannelPipelineFactory)new HttpServletPipelineFactory(this.servlet, this.servletContext, this.nc));
        httpsb.setOptions((Map)this.nc.getTcpParams());
        httpsb.bind((SocketAddress)new InetSocketAddress((int)this.nc.getInitParam("httpPort")));
        ServletBootstrap.log.info((Object)("listen http port " + this.nc.getInitParam("httpPort") + " success"));
        SessionManager.getInstance().setServletConfig(this.sc);
        SessionManager.getInstance().startSessionCheckThread();
    }
    
    public static void main(final String[] args) throws Exception {
        final ServletBootstrap sbs = new ServletBootstrap();
        sbs.startup();
    }
}
