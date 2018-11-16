package com.reign.util.buffer;

import java.nio.*;

public class DuplicatedChannelBuffer extends ChannelBufferBase implements IWrappedChannelBuffer
{
    private final IChannelBuffer buffer;
    
    public DuplicatedChannelBuffer(final IChannelBuffer buffer) {
        if (buffer == null) {
            throw new NullPointerException("buffer");
        }
        this.buffer = buffer;
        this.setIndex(buffer.getReaderIndex(), buffer.getWriterIndex());
    }
    
    private DuplicatedChannelBuffer(final DuplicatedChannelBuffer buffer) {
        this.buffer = buffer.buffer;
        this.setIndex(buffer.getReaderIndex(), buffer.getWriterIndex());
    }
    
    @Override
    public IChannelBuffer getUnwrapBuffer() {
        return this.buffer;
    }
    
    @Override
    public IChannelBufferFactory getFactory() {
        return this.buffer.getFactory();
    }
    
    @Override
    public ByteOrder getByteOrder() {
        return this.buffer.getByteOrder();
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
    public int arrayOffset() {
        return this.buffer.arrayOffset();
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
        return new DuplicatedChannelBuffer(this);
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
}
