package com.reign.util.buffer;

import java.nio.*;

public final class ChannelBuffers
{
    public static final IChannelBuffer EMPTY_BUFFER;
    
    static {
        EMPTY_BUFFER = null;
    }
    
    public static IChannelBuffer buffer(final ByteOrder endianness, final int capacity) {
        if (endianness == ByteOrder.BIG_ENDIAN) {
            if (capacity == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new HeapChannelBufferBigEndian(capacity);
        }
        else {
            if (endianness != ByteOrder.LITTLE_ENDIAN) {
                throw new NullPointerException("endianness");
            }
            if (capacity == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new HeapChannelBufferLittleEndian(capacity);
        }
    }
    
    public static IChannelBuffer buffer(final int capacity) {
        return buffer(ByteOrder.BIG_ENDIAN, capacity);
    }
    
    public static IChannelBuffer dynamicBuffer() {
        return dynamicBuffer(ByteOrder.BIG_ENDIAN, 256);
    }
    
    public static IChannelBuffer dynamicBuffer(final IChannelBufferFactory factory) {
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        return new DynamicChannelBuffer(factory.getDefaultOrder(), 256, factory);
    }
    
    public static IChannelBuffer dynamicBuffer(final int estimatedLength) {
        return dynamicBuffer(ByteOrder.BIG_ENDIAN, estimatedLength);
    }
    
    public static IChannelBuffer dynamicBuffer(final ByteOrder endianness, final int estimatedLength) {
        return new DynamicChannelBuffer(endianness, estimatedLength);
    }
    
    public static IChannelBuffer dynamicBuffer(final int estimatedLength, final IChannelBufferFactory factory) {
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        return new DynamicChannelBuffer(factory.getDefaultOrder(), estimatedLength, factory);
    }
    
    public static IChannelBuffer dynamicBuffer(final ByteOrder endianness, final int estimatedLength, final IChannelBufferFactory factory) {
        return new DynamicChannelBuffer(endianness, estimatedLength, factory);
    }
    
    public static IChannelBuffer wrappedBuffer(final byte[] array) {
        return wrappedBuffer(ByteOrder.BIG_ENDIAN, array);
    }
    
    public static IChannelBuffer wrappedBuffer(final ByteOrder endianness, final byte[] array) {
        if (endianness == ByteOrder.BIG_ENDIAN) {
            if (array.length == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new HeapChannelBufferBigEndian(array);
        }
        else {
            if (endianness != ByteOrder.LITTLE_ENDIAN) {
                throw new NullPointerException("endianness");
            }
            if (array.length == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new HeapChannelBufferLittleEndian(array);
        }
    }
    
    public static IChannelBuffer wrappedBuffer(final byte[] array, final int offset, final int length) {
        return wrappedBuffer(ByteOrder.BIG_ENDIAN, array, offset, length);
    }
    
    public static IChannelBuffer wrappedBuffer(final ByteOrder endianness, final byte[] array, final int offset, final int length) {
        if (endianness == null) {
            throw new NullPointerException("endianness");
        }
        if (offset == 0) {
            if (length == array.length) {
                return wrappedBuffer(endianness, array);
            }
            if (length == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new TruncatedChannelBuffer(wrappedBuffer(endianness, array), length);
        }
        else {
            if (length == 0) {
                return ChannelBuffers.EMPTY_BUFFER;
            }
            return new SlicedChannelBuffer(wrappedBuffer(endianness, array), offset, length);
        }
    }
}
