package com.reign.util.buffer;

import java.nio.*;

public class HeapChannelBufferLittleEndian extends HeapChannelBuffer
{
    public HeapChannelBufferLittleEndian(final int length) {
        super(length);
    }
    
    public HeapChannelBufferLittleEndian(final byte[] array) {
        super(array);
    }
    
    private HeapChannelBufferLittleEndian(final byte[] array, final int readerIndex, final int writerIndex) {
        super(array, readerIndex, writerIndex);
    }
    
    @Override
    public IChannelBufferFactory getFactory() {
        return HeapChannelBufferFactory.getInstance(ByteOrder.LITTLE_ENDIAN);
    }
    
    @Override
    public ByteOrder getByteOrder() {
        return ByteOrder.LITTLE_ENDIAN;
    }
    
    @Override
    public short getShort(final int index) {
        return (short)((this.array[index] & 0xFF) | this.array[index + 1] << 8);
    }
    
    @Override
    public int getInt(final int index) {
        return (this.array[index] & 0xFF) | (this.array[index + 1] & 0xFF) << 8 | (this.array[index + 2] & 0xFF) << 16 | (this.array[index + 3] & 0xFF) << 24;
    }
    
    @Override
    public long getLong(final int index) {
        return (this.array[index] & 0xFFL) | (this.array[index + 1] & 0xFFL) << 8 | (this.array[index + 2] & 0xFFL) << 16 | (this.array[index + 3] & 0xFFL) << 24 | (this.array[index + 4] & 0xFFL) << 32 | (this.array[index + 5] & 0xFFL) << 40 | (this.array[index + 6] & 0xFFL) << 48 | (this.array[index + 7] & 0xFFL) << 56;
    }
    
    @Override
    public void setShort(final int index, final short value) {
        this.array[index] = (byte)value;
        this.array[index + 1] = (byte)(value >>> 8);
    }
    
    @Override
    public void setInt(final int index, final int value) {
        this.array[index] = (byte)value;
        this.array[index + 1] = (byte)(value >>> 8);
        this.array[index + 2] = (byte)(value >>> 16);
        this.array[index + 3] = (byte)(value >>> 24);
    }
    
    @Override
    public void setLong(final int index, final long value) {
        this.array[index] = (byte)value;
        this.array[index + 1] = (byte)(value >>> 8);
        this.array[index + 2] = (byte)(value >>> 16);
        this.array[index + 3] = (byte)(value >>> 24);
        this.array[index + 4] = (byte)(value >>> 32);
        this.array[index + 5] = (byte)(value >>> 40);
        this.array[index + 6] = (byte)(value >>> 48);
        this.array[index + 7] = (byte)(value >>> 56);
    }
    
    @Override
    public IChannelBuffer duplicate() {
        return new HeapChannelBufferLittleEndian(this.array, this.getReaderIndex(), this.getWriterIndex());
    }
    
    @Override
    public IChannelBuffer copy(final int index, final int length) {
        if (index < 0 || length < 0 || index + length > this.array.length) {
            throw new IndexOutOfBoundsException("Too many bytes to copy - Need " + (index + length) + ", maximum is " + this.array.length);
        }
        final byte[] copiedArray = new byte[length];
        System.arraycopy(this.array, index, copiedArray, 0, length);
        return new HeapChannelBufferLittleEndian(copiedArray);
    }
}
