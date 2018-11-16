package com.reign.framework.netty.http;

import com.reign.framework.netty.servlet.*;
import org.jboss.netty.handler.execution.*;
import java.util.concurrent.*;
import com.reign.framework.netty.http.handler.*;
import org.jboss.netty.channel.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.handler.stream.*;

public class HttpServletPipelineFactory implements ChannelPipelineFactory
{
    private final NettyConfig config;
    private final ExecutionHandler executionHandler;
    private final Servlet servlet;
    private final ServletContext sc;
    
    public HttpServletPipelineFactory(final Servlet servlet, final ServletContext sc, final NettyConfig config) {
        this.servlet = servlet;
        this.sc = sc;
        this.config = config;
        this.executionHandler = new ExecutionHandler(new OrderedMemoryAwareThreadPoolExecutor((int)config.getInitParam("httpMaxThreads"), 0L, 0L));
    }
    
    @Override
	public ChannelPipeline getPipeline() throws Exception {
        final HttpDefaultHandler defaultHandler = new HttpDefaultHandler(this.servlet, this.sc);
        final ChannelPipeline pipeline = Channels.pipeline();
        pipeline.addLast("decoder", new HttpRequestDecoder());
        pipeline.addLast("encoder", new HttpResponseEncoder());
        pipeline.addLast("thread", this.executionHandler);
        pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
        pipeline.addLast("handler", defaultHandler);
        return pipeline;
    }
}
