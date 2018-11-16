package com.reign.framework.netty.tcp;

import com.reign.framework.netty.servlet.*;
import org.jboss.netty.handler.execution.*;
import java.util.concurrent.*;
import com.reign.framework.netty.tcp.handler.*;
import org.jboss.netty.channel.*;
import com.reign.framework.netty.tcp.coder.*;

public class TcpServletPipelineFactory implements ChannelPipelineFactory
{
    private final ExecutionHandler executionHandler;
    private final FlashPolicyHandler flashPolicyHandler;
    private final Class<?> tcpHandlerClass;
    private final Servlet servlet;
    private final ServletContext servletContext;
    
    public TcpServletPipelineFactory(final Servlet servlet, final ServletContext servletContext, final NettyConfig config) throws Exception {
        this.executionHandler = new ExecutionHandler(new OrderedMemoryAwareThreadPoolExecutor((int)config.getInitParam("maxThreads"), 0L, 0L));
        this.flashPolicyHandler = new FlashPolicyHandler();
        final String tcpHandlerClassName = (String)config.getInitParam("tcpHandler");
        final Class<?> clazz = this.getClass().getClassLoader().loadClass(tcpHandlerClassName);
        if (clazz == null) {
            throw new ClassNotFoundException(tcpHandlerClassName);
        }
        if (!ITcpHandler.class.isAssignableFrom(clazz)) {
            throw new ClassCastException(String.valueOf(clazz.getName()) + " can't cast to " + ITcpHandler.class.getName());
        }
        this.tcpHandlerClass = clazz;
        this.servlet = servlet;
        this.servletContext = servletContext;
    }
    
    @Override
	public ChannelPipeline getPipeline() throws Exception {
        final ITcpHandler tcpHandler = (ITcpHandler)this.tcpHandlerClass.newInstance();
        tcpHandler.setServlet(this.servlet);
        tcpHandler.setServletContext(this.servletContext);
        final ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("flashPolicy", this.flashPolicyHandler);
        pipeline.addLast("decoder", new MessageEncoder());
        pipeline.addLast("encoder", new MessageDecoder());
        pipeline.addLast("thread", this.executionHandler);
        pipeline.addLast("handler", tcpHandler);
        return pipeline;
    }
}
