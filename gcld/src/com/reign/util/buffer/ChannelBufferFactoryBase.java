package com.reign.util.buffer;

import java.nio.*;

public abstract class ChannelBufferFactoryBase implements IChannelBufferFactory
{
    private final ByteOrder defaultOrder;
    
    protected ChannelBufferFactoryBase() {
        this(ByteOrder.BIG_ENDIAN);
    }
    
    protected ChannelBufferFactoryBase(final ByteOrder defaultOrder) {
        if (defaultOrder == null) {
            throw new NullPointerException("defaultOrder");
        }
        this.defaultOrder = defaultOrder;
    }
    
    @Override
    public IChannelBuffer getBuffer(final int capacity) {
        return this.getBuffer(this.getDefaultOrder(), capacity);
    }
    
    @Override
    public IChannelBuffer getBuffer(final byte[] array, final int offset, final int length) {
        return this.getBuffer(this.getDefaultOrder(), array, offset, length);
    }
    
    @Override
    public ByteOrder getDefaultOrder() {
        return this.defaultOrder;
    }
}
