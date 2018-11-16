package com.reign.util.buffer;

import java.nio.*;

public class ReadOnlyChannelBuffer extends ChannelBufferBase implements IWrappedChannelBuffer
{
    private final IChannelBuffer buffer;
    
    public ReadOnlyChannelBuffer(final IChannelBuffer buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        this.buffer = buffer;
        this.setIndex(buffer.getReaderIndex(), buffer.getWriterIndex());
    }
    
    private ReadOnlyChannelBuffer(final ReadOnlyChannelBuffer buffer) {
        this.buffer = buffer.buffer;
        this.setIndex(buffer.getReaderIndex(), buffer.getWriterIndex());
    }
    
    @Override
    public ByteOrder getByteOrder() {
        return this.buffer.getByteOrder();
    }
    
    @Override
    public boolean hasArray() {
        return false;
    }
    
    @Override
    public byte[] array() {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public int arrayOffset() {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public void discardReadBytes() {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public IChannelBufferFactory getFactory() {
        return this.buffer.getFactory();
    }
    
    @Override
    public int getCapacity() {
        return this.buffer.getCapacity();
    }
    
    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        this.buffer.getBytes(index, dst, dstIndex, length);
    }
    
    @Override
    public IChannelBuffer copy(final int index, final int length) {
        return this.buffer.copy(index, length);
    }
    
    @Override
    public IChannelBuffer duplicate() {
        return new ReadOnlyChannelBuffer(this);
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
    public int getInt(final int index) {
        return this.buffer.getInt(index);
    }
    
    @Override
    public long getLong(final int index) {
        return this.buffer.getLong(index);
    }
    
    @Override
    public void setByte(final int index, final byte value) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public void setShort(final int index, final short value) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public void setInt(final int index, final int value) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public void setLong(final int index, final long value) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public void setBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public void setBytes(final int index, final IChannelBuffer src, final int srcIndex, final int length) {
        throw new ReadOnlyBufferException();
    }
    
    @Override
    public IChannelBuffer getUnwrapBuffer() {
        return this.buffer;
    }
}
