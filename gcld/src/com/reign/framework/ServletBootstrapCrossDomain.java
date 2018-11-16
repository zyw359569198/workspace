package com.reign.framework;

import org.jboss.netty.bootstrap.*;
import org.jboss.netty.channel.socket.nio.*;
import java.util.concurrent.*;
import com.reign.framework.netty.tcp.*;
import org.jboss.netty.channel.*;
import java.net.*;

public class ServletBootstrapCrossDomain
{
    public void startup() throws Exception {
        final ServerBootstrap sb = new ServerBootstrap();
        final NioServerSocketChannelFactory factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
        sb.setFactory(factory);
        sb.setPipelineFactory(new TcpCrossDomainPipelineFactory());
        sb.bind(new InetSocketAddress(843));
    }
    
    public static void main(final String[] args) throws Exception {
        final ServletBootstrapCrossDomain sbs = new ServletBootstrapCrossDomain();
        sbs.startup();
    }
}
