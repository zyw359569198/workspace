package com.reign.util.buffer;

public abstract class HeapChannelBuffer extends ChannelBufferBase
{
    protected final byte[] array;
    
    protected HeapChannelBuffer(final int length) {
        this(new byte[length], 0, 0);
    }
    
    protected HeapChannelBuffer(final byte[] array) {
        this(array, 0, array.length);
    }
    
    protected HeapChannelBuffer(final byte[] array, final int readerIndex, final int writerIndex) {
        if (array == null) {
            throw new NullPointerException("array");
        }
        this.array = array;
        this.setIndex(readerIndex, writerIndex);
    }
    
    @Override
    public int getCapacity() {
        return this.array.length;
    }
    
    @Override
    public boolean hasArray() {
        return true;
    }
    
    @Override
    public byte[] array() {
        return this.array;
    }
    
    @Override
    public byte getByte(final int index) {
        return this.array[index];
    }
    
    @Override
    public void getBytes(final int index, final byte[] dst, final int dstIndex, final int length) {
        System.arraycopy(this.array, index, dst, dstIndex, length);
    }
    
    @Override
    public void setByte(final int index, final byte value) {
        this.array[index] = value;
    }
    
    @Override
    public void setBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        System.arraycopy(src, srcIndex, this.array, index, length);
    }
    
    @Override
    public void setBytes(final int index, final IChannelBuffer src, final int srcIndex, final int length) {
        if (src instanceof HeapChannelBuffer) {
            this.setBytes(index, ((HeapChannelBuffer)src).array, srcIndex, length);
        }
        else {
            src.getBytes(srcIndex, this.array, index, length);
        }
    }
    
    @Override
    public int arrayOffset() {
        return 0;
    }
}
