package com.reign.framework.netty.tcp.handler;

import org.apache.commons.logging.*;
import org.jboss.netty.channel.*;
import com.reign.framework.netty.tcp.*;
import com.reign.framework.netty.servlet.*;

public class TcpDefaultHandler extends SimpleChannelHandler implements ITcpHandler
{
    private static final Log log;
    private Servlet servlet;
    private ServletContext sc;
    
    static {
        log = LogFactory.getLog(TcpDefaultHandler.class);
    }
    
    public TcpDefaultHandler() {
    }
    
    public TcpDefaultHandler(final Servlet servlet, final ServletContext sc) {
        this.servlet = servlet;
        this.sc = sc;
    }
    
    @Override
	public void channelClosed(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        super.channelClosed(ctx, e);
    }
    
    @Override
	public void channelConnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
        final String sessionId = (String)ctx.getAttachment();
        if (sessionId == null) {
            final Session session = SessionManager.getInstance().getSession(null, true);
            session.setChannel(e.getChannel());
            ctx.setAttachment(session.getId());
        }
    }
    
    @Override
	public void channelDisconnected(final ChannelHandlerContext ctx, final ChannelStateEvent e) throws Exception {
    }
    
    @Override
	public void exceptionCaught(final ChannelHandlerContext ctx, final ExceptionEvent e) throws Exception {
        TcpDefaultHandler.log.error("channel error, channel[id:" + e.getChannel().getId() + ", interestOps:" + e.getChannel().getInterestOps() + ", bound:" + e.getChannel().isBound() + ", connected:" + e.getChannel().isConnected() + ", open:" + e.getChannel().isOpen() + ", readable:" + e.getChannel().isReadable() + ", writable:" + e.getChannel().isWritable() + "]", e.getCause());
    }
    
    @Override
	public void messageReceived(final ChannelHandlerContext ctx, final MessageEvent e) throws Exception {
        if (e.getMessage() instanceof RequestMessage) {
            final RequestMessage message = (RequestMessage)e.getMessage();
            message.setSessionId((String)ctx.getAttachment());
            final Response response = new TcpResponse(e.getChannel());
            final Request request = new TcpRequest(ctx, this.sc, e.getChannel(), message);
            this.servlet.service(request, response);
        }
    }
    
    @Override
	public void setServletContext(final ServletContext servletContext) {
        this.sc = servletContext;
    }
    
    @Override
	public void setServlet(final Servlet servlet) {
        this.servlet = servlet;
    }
}
