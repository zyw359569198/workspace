package com.reign.framework.netty.servlet;

import java.io.*;

public interface Servlet extends Serializable
{
    void init(final ServletConfig p0, final ServletContext p1);
    
    ServletConfig getServletConfig();
    
    ServletContext getServletContext();
    
    void service(final Request p0, final Response p1);
    
    void destroy();
}
