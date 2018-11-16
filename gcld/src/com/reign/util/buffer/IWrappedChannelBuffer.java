package com.reign.util.buffer;

public interface IWrappedChannelBuffer extends IChannelBuffer
{
    IChannelBuffer getUnwrapBuffer();
}
