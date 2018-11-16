package com.reign.framework.oio;

import java.io.*;

public interface Protocol
{
    void send(final OutputStream p0, final byte[] p1);
    
    Object read(final InputStream p0);
}
