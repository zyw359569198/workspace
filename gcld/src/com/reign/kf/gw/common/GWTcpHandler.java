package com.reign.kf.gw.common;

import java.util.concurrent.*;
import org.jboss.netty.channel.*;
import com.reign.framework.netty.tcp.handler.*;
import com.reign.framework.netty.tcp.*;
import com.reign.framework.netty.servlet.*;

public class GWTcpHandler extends SimpleChannelHandler implements ITcpHandler
{
    private Servlet servlet;
    private ServletContext sc;
    private ConcurrentMap<Integer, Channel> channelMap;
    
    public GWTcpHandler() {
        this.channelMap = new ConcurrentHashMap<Integer, Channel>();
    }
    
    @Override
	public void setServletContext(final ServletContext servletContext) {
        this.sc = servletContext;
    }
    
    @Override
	public void setServlet(final Servlet servlet) {
        this.servlet = servlet;
    }
    
    @Override
	public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        super.channelClosed(ctx, e);
    }
    
    @Override
	public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        this.channelMap.put(e.getChannel().getId(), e.getChannel());
        super.channelConnected(ctx, e);
    }
    
    @Override
	public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        super.channelDisconnected(ctx, e);
    }
    
    @Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        super.exceptionCaught(ctx, e);
    }
    
    @Override
	public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        if (e.getMessage() instanceof RequestMessage) {
            final RequestMessage message = (RequestMessage)e.getMessage();
            final Response response = new TcpResponse(e.getChannel());
            final Request request = new TcpRequest(ctx, this.sc, e.getChannel(), message);
            this.servlet.service(request, response);
        }
    }
}
