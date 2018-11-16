package com.reign.gcld.system.service;

import com.reign.framework.netty.servlet.*;

public interface IOfficialWebsiteService
{
    byte[] preLogin(final String p0, final String p1, final long p2, final String p3, final String p4, final Request p5);
    
    byte[] login(final String p0, final String p1, final String p2, final long p3, final String p4, final int p5, final String p6, final String p7, final Request p8, final Response p9);
    
    byte[] pay(final String p0, final String p1, final int p2, final String p3, final int p4, final long p5, final String p6, final Request p7);
    
    byte[] palayerInfo(final String p0, final String p1, final Request p2);
    
    byte[] rankList(final String p0, final int p1, final Request p2);
}
