package com.reign.util.buffer;

import java.nio.*;

public class SlicedChannelBuffer extends ChannelBufferBase implements IWrappedChannelBuffer
{
    private final IChannelBuffer buffer;
    private final int adjustment;
    private final int length;
    
    public SlicedChannelBuffer(final IChannelBuffer buffer, final int index, final int length) {
        if (index < 0 || index > buffer.getCapacity()) {
            throw new IndexOutOfBoundsException("Invalid index of " + index + ", maximum is " + buffer.getCapacity());
        }
        if (index + length > buffer.getCapacity()) {
            throw new IndexOutOfBoundsException("Invalid combined index of " + (index + length) + ", maximum is " + buffer.getCapacity());
        }
        this.buffer = buffer;
        this.adjustment = index;
        this.setWriterIndex(this.length = length);
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
        return this.length;
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
        return this.buffer.arrayOffset() + this.adjustment;
    }
    
    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        this.checkIndex(index, length);
        this.buffer.getBytes(index + this.adjustment, dst, dstIndex, length);
    }
    
    @Override
    public IChannelBuffer copy(final int index, final int length) {
        this.checkIndex(index, length);
        return this.buffer.copy(index + this.adjustment, length);
    }
    
    @Override
    public IChannelBuffer duplicate() {
        final IChannelBuffer duplicate = new SlicedChannelBuffer(this.buffer, this.adjustment, this.length);
        duplicate.setIndex(this.getReaderIndex(), this.getWriterIndex());
        return duplicate;
    }
    
    @Override
    public byte getByte(final int index) {
        this.checkIndex(index);
        return this.buffer.getByte(index + this.adjustment);
    }
    
    @Override
    public short getShort(final int index) {
        this.checkIndex(index, 2);
        return this.buffer.getShort(index + this.adjustment);
    }
    
    @Override
    public int getInt(final int index) {
        this.checkIndex(index, 4);
        return this.buffer.getInt(index + this.adjustment);
    }
    
    @Override
    public long getLong(final int index) {
        this.checkIndex(index, 8);
        return this.buffer.getLong(index + this.adjustment);
    }
    
    @Override
    public void setByte(final int index, final byte value) {
        this.checkIndex(index);
        this.buffer.setByte(index + this.adjustment, value);
    }
    
    @Override
    public void setShort(final int index, final short value) {
        this.checkIndex(index, 2);
        this.buffer.setShort(index + this.adjustment, value);
    }
    
    @Override
    public void setInt(final int index, final int value) {
        this.checkIndex(index, 4);
        this.buffer.setInt(index + this.adjustment, value);
    }
    
    @Override
    public void setLong(final int index, final long value) {
        this.checkIndex(index, 8);
        this.buffer.setLong(index + this.adjustment, value);
    }
    
    @Override
    public void setBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        this.checkIndex(index, length);
        this.buffer.setBytes(index + this.adjustment, src, srcIndex, length);
    }
    
    @Override
    public void setBytes(final int index, final IChannelBuffer src, final int srcIndex, final int length) {
        this.checkIndex(index, length);
        this.buffer.setBytes(index + this.adjustment, src, srcIndex, length);
    }
    
    private void checkIndex(final int index) {
        if (index < 0 || index >= this.getCapacity()) {
            throw new IndexOutOfBoundsException("Invalid index: " + index + ", maximum is " + this.getCapacity());
        }
    }
    
    private void checkIndex(final int startIndex, final int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length is negative: " + length);
        }
        if (startIndex < 0) {
            throw new IndexOutOfBoundsException("startIndex cannot be negative");
        }
        if (startIndex + length > this.getCapacity()) {
            throw new IndexOutOfBoundsException("Index too big - Bytes needed: " + (startIndex + length) + ", maximum is " + this.getCapacity());
        }
    }
}
