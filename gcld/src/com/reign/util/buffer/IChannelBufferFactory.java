package com.reign.util.buffer;

import java.nio.*;

public interface IChannelBufferFactory
{
    IChannelBuffer getBuffer(final int p0);
    
    IChannelBuffer getBuffer(final ByteOrder p0, final int p1);
    
    IChannelBuffer getBuffer(final byte[] p0, final int p1, final int p2);
    
    IChannelBuffer getBuffer(final ByteOrder p0, final byte[] p1, final int p2, final int p3);
    
    ByteOrder getDefaultOrder();
}
