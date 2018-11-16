package com.reign.util.buffer;

import java.nio.*;

public class DynamicChannelBuffer extends ChannelBufferBase
{
    private final IChannelBufferFactory factory;
    private final ByteOrder endianness;
    private IChannelBuffer buffer;
    
    public DynamicChannelBuffer(final int estimatedLength) {
        this(ByteOrder.BIG_ENDIAN, estimatedLength);
    }
    
    public DynamicChannelBuffer(final ByteOrder endianness, final int estimatedLength) {
        this(endianness, estimatedLength, HeapChannelBufferFactory.getInstance(endianness));
    }
    
    public DynamicChannelBuffer(final ByteOrder endianness, final int estimatedLength, final IChannelBufferFactory factory) {
        if (estimatedLength < 0) {
            throw new IllegalArgumentException("estimatedLength: " + estimatedLength);
        }
        if (endianness == null) {
            throw new NullPointerException("endianness");
        }
        if (factory == null) {
            throw new NullPointerException("factory");
        }
        this.factory = factory;
        this.endianness = endianness;
        this.buffer = factory.getBuffer(this.getByteOrder(), estimatedLength);
    }
    
    @Override
    public void ensureWritableBytes(final int minWritableBytes) {
        if (minWritableBytes <= this.getWritableBytes()) {
            return;
        }
        int newCapacity;
        if (this.getCapacity() == 0) {
            newCapacity = 1;
        }
        else {
            newCapacity = this.getCapacity();
        }
        final int minNewCapacity = this.getWriterIndex() + minWritableBytes;
        while (newCapacity < minNewCapacity) {
            newCapacity <<= 1;
            if (newCapacity == 0) {
                throw new IllegalStateException("Maximum size of 2gb exceeded");
            }
        }
        final IChannelBuffer newBuffer = this.getFactory().getBuffer(this.getByteOrder(), newCapacity);
        newBuffer.writeBytes(this.buffer, 0, this.getWriterIndex());
        this.buffer = newBuffer;
    }
    
    @Override
    public IChannelBufferFactory getFactory() {
        return this.factory;
    }
    
    @Override
    public ByteOrder getByteOrder() {
        return this.endianness;
    }
    
    @Override
    public int getCapacity() {
        return this.buffer.getCapacity();
    }
    
    @Override
    public boolean hasArray() {
        return this.buffer.hasArray();
    }
    
    @Override
    public byte[] array() {
        return this.buffer.array();
    }
    
    @Override
    public byte getByte(final int index) {
        return this.buffer.getByte(index);
    }
    
    @Override
    public short getShort(final int index) {
        return this.buffer.getShort(index);
    }
    
    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        this.buffer.getBytes(index, dst, dstIndex, length);
    }
    
    @Override
    public IChannelBuffer copy(final int index, final int length) {
        final DynamicChannelBuffer copiedBuffer = new DynamicChannelBuffer(this.getByteOrder(), Math.max(length, 64), this.getFactory());
        copiedBuffer.buffer = this.buffer.copy(index, length);
        copiedBuffer.setIndex(0, length);
        return copiedBuffer;
    }
    
    @Override
    public IChannelBuffer duplicate() {
        return this.buffer.duplicate();
    }
    
    @Override
    public int getInt(final int index) {
        return this.buffer.getInt(index);
    }
    
    @Override
    public long getLong(final int index) {
        return this.buffer.getLong(index);
    }
    
    @Override
    public void setByte(final int index, final byte value) {
        this.buffer.setByte(index, value);
    }
    
    @Override
    public void setShort(final int index, final short value) {
        this.buffer.setShort(index, value);
    }
    
    @Override
    public void setInt(final int index, final int value) {
        this.buffer.setInt(index, value);
    }
    
    @Override
    public void setLong(final int index, final long value) {
        this.buffer.setLong(index, value);
    }
    
    @Override
    public void setBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        this.buffer.setBytes(index, src, srcIndex, length);
    }
    
    @Override
    public void setBytes(final int index, final IChannelBuffer src, final int srcIndex, final int length) {
        this.buffer.setBytes(index, src, srcIndex, length);
    }
    
    @Override
    public void writeByte(final byte value) {
        this.ensureWritableBytes(1);
        super.writeByte(value);
    }
    
    @Override
    public void writeShort(final short value) {
        this.ensureWritableBytes(2);
        super.writeShort(value);
    }
    
    @Override
    public void writeInt(final int value) {
        this.ensureWritableBytes(4);
        super.writeInt(value);
    }
    
    @Override
    public void writeLong(final long value) {
        this.ensureWritableBytes(8);
        super.writeLong(value);
    }
    
    @Override
    public void writeChar(final char value) {
        this.ensureWritableBytes(2);
        super.writeChar(value);
    }
    
    @Override
    public void writeFloat(final float value) {
        this.ensureWritableBytes(4);
        super.writeFloat(value);
    }
    
    @Override
    public void writeDouble(final double value) {
        this.ensureWritableBytes(8);
        super.writeDouble(value);
    }
    
    @Override
    public void writeBytes(final IChannelBuffer channelBuffer, final int length) {
        this.ensureWritableBytes(length);
        super.writeBytes(channelBuffer, length);
    }
    
    @Override
    public void writeBytes(final IChannelBuffer channelBuffer, final int startIndex, final int length) {
        this.ensureWritableBytes(length);
        super.writeBytes(channelBuffer, startIndex, length);
    }
    
    @Override
    public void writeBytes(final byte[] bytes) {
        this.ensureWritableBytes(bytes.length);
        super.writeBytes(bytes);
    }
    
    @Override
    public void writeBytes(final byte[] src, final int srcIndex, final int length) {
        this.ensureWritableBytes(length);
        super.writeBytes(src, srcIndex, length);
    }
    
    @Override
    public int arrayOffset() {
        return this.buffer.arrayOffset();
    }
}
