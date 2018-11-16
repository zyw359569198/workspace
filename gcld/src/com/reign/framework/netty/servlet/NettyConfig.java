package com.reign.framework.netty.servlet;

import java.util.*;

public interface NettyConfig
{
    Object getInitParam(final String p0);
    
    Map<String, Object> getInitParams();
    
    Map<String, Object> getTcpParams();
}
