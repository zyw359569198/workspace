package com.reign.framework.netty.tcp.handler;

import org.jboss.netty.channel.*;
import com.reign.framework.netty.servlet.*;

public interface ITcpHandler extends ChannelHandler
{
    void setServletContext(final ServletContext p0);
    
    void setServlet(final Servlet p0);
}
