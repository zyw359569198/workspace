package com.reign.util.buffer;

import java.nio.*;

public class HeapChannelBufferFactory extends ChannelBufferFactoryBase
{
    private static final HeapChannelBufferFactory INSTANCE_BE;
    private static final HeapChannelBufferFactory INSTANCE_LE;
    
    static {
        INSTANCE_BE = new HeapChannelBufferFactory(ByteOrder.BIG_ENDIAN);
        INSTANCE_LE = new HeapChannelBufferFactory(ByteOrder.LITTLE_ENDIAN);
    }
    
    public static IChannelBufferFactory getInstance() {
        return HeapChannelBufferFactory.INSTANCE_BE;
    }
    
    public static IChannelBufferFactory getInstance(final ByteOrder endianness) {
        if (endianness == ByteOrder.BIG_ENDIAN) {
            return HeapChannelBufferFactory.INSTANCE_BE;
        }
        if (endianness == ByteOrder.LITTLE_ENDIAN) {
            return HeapChannelBufferFactory.INSTANCE_LE;
        }
        if (endianness == null) {
            throw new NullPointerException("endianness");
        }
        throw new IllegalStateException("Should not reach here");
    }
    
    public HeapChannelBufferFactory() {
    }
    
    public HeapChannelBufferFactory(final ByteOrder defaultOrder) {
        super(defaultOrder);
    }
    
    @Override
    public IChannelBuffer getBuffer(final ByteOrder order, final int capacity) {
        return ChannelBuffers.buffer(order, capacity);
    }
    
    @Override
    public IChannelBuffer getBuffer(final ByteOrder order, final byte[] array, final int offset, final int length) {
        return ChannelBuffers.wrappedBuffer(order, array, offset, length);
    }
}
