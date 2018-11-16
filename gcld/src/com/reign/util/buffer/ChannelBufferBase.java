package com.reign.util.buffer;

import java.nio.charset.*;
import java.nio.*;

public abstract class ChannelBufferBase implements IChannelBuffer
{
    private int readerIndex;
    private int writerIndex;
    private int markedReaderIndex;
    private int markedWriterIndex;
    private static final byte TRUE = 1;
    private static final byte FALSE = 0;
    
    @Override
    public int getReaderIndex() {
        return this.readerIndex;
    }
    
    @Override
    public void setReaderIndex(final int readerIndex) {
        if (readerIndex < 0 || readerIndex > this.writerIndex) {
            throw new IndexOutOfBoundsException();
        }
        this.readerIndex = readerIndex;
    }
    
    @Override
    public int getWriterIndex() {
        return this.writerIndex;
    }
    
    @Override
    public void setWriterIndex(final int writerIndex) {
        if (writerIndex < this.readerIndex || writerIndex > this.getCapacity()) {
            throw new IndexOutOfBoundsException("Invalid readerIndex: " + this.readerIndex + " - Maximum is " + writerIndex);
        }
        this.writerIndex = writerIndex;
    }
    
    @Override
    public void setIndex(final int readerIndex, final int writerIndex) {
        if (readerIndex < 0 || readerIndex > writerIndex || writerIndex > this.getCapacity()) {
            throw new IndexOutOfBoundsException("Invalid writerIndex: " + writerIndex + " - Maximum is " + readerIndex + " or " + this.getCapacity());
        }
        this.readerIndex = readerIndex;
        this.writerIndex = writerIndex;
    }
    
    @Override
    public int getReadableBytes() {
        return this.writerIndex - this.readerIndex;
    }
    
    @Override
    public int getWritableBytes() {
        return this.getCapacity() - this.writerIndex;
    }
    
    @Override
    public boolean isReadable() {
        return this.writerIndex - this.readerIndex > 0;
    }
    
    @Override
    public boolean isReadable(final int num) {
        return this.writerIndex - this.readerIndex >= num;
    }
    
    @Override
    public boolean isWritable() {
        return this.getCapacity() - this.writerIndex > 0;
    }
    
    @Override
    public boolean isWritable(final int num) {
        return this.getCapacity() - this.writerIndex >= num;
    }
    
    @Override
    public void clear() {
        this.readerIndex = 0;
        this.writerIndex = 0;
    }
    
    @Override
    public void markReaderIndex() {
        this.markedReaderIndex = this.readerIndex;
    }
    
    @Override
    public void resetReaderIndex() {
        this.setReaderIndex(this.markedReaderIndex);
    }
    
    @Override
    public void markWriterIndex() {
        this.markedWriterIndex = this.writerIndex;
    }
    
    @Override
    public void resetWriterIndex() {
        this.setWriterIndex(this.markedWriterIndex);
    }
    
    @Override
    public void discardReadBytes() {
        if (this.readerIndex == 0) {
            return;
        }
        this.setBytes(0, this, this.readerIndex, this.writerIndex - this.readerIndex);
        this.writerIndex -= this.readerIndex;
        this.markedReaderIndex = Math.max(this.markedReaderIndex - this.readerIndex, 0);
        this.markedWriterIndex = Math.max(this.markedWriterIndex - this.readerIndex, 0);
        this.readerIndex = 0;
    }
    
    @Override
    public void ensureWritableBytes(final int writableBytes) {
        if (writableBytes > this.getWritableBytes()) {
            throw new IndexOutOfBoundsException("Writable bytes exceeded: Got " + writableBytes + ", maximum is " + this.getWritableBytes());
        }
    }
    
    @Override
    public byte readByte() {
        if (this.readerIndex == this.writerIndex) {
            throw new IndexOutOfBoundsException("Readable byte limit exceeded: " + this.readerIndex);
        }
        final byte rtn = this.getByte(this.readerIndex);
        ++this.readerIndex;
        return rtn;
    }
    
    @Override
    public short readShort() {
        this.checkReadableBytes(2);
        final short rtn = this.getShort(this.readerIndex);
        this.readerIndex += 2;
        return rtn;
    }
    
    @Override
    public int readInt() {
        this.checkReadableBytes(4);
        final int rtn = this.getInt(this.readerIndex);
        this.readerIndex += 4;
        return rtn;
    }
    
    @Override
    public long readLong() {
        this.checkReadableBytes(8);
        final long rtn = this.getLong(this.readerIndex);
        this.readerIndex += 8;
        return rtn;
    }
    
    @Override
    public char readChar() {
        this.checkReadableBytes(2);
        final char rtn = this.getChar(this.readerIndex);
        this.readerIndex += 2;
        return rtn;
    }
    
    @Override
    public float readFloat() {
        this.checkReadableBytes(4);
        final float rtn = this.getFloat(this.readerIndex);
        this.readerIndex += 4;
        return rtn;
    }
    
    @Override
    public double readDouble() {
        this.checkReadableBytes(8);
        final double rtn = this.getDouble(this.readerIndex);
        this.readerIndex += 8;
        return rtn;
    }
    
    @Override
    public IChannelBuffer readBytesToBuff() {
        final int length = this.writerIndex - this.readerIndex;
        return this.readBytesToBuff(length);
    }
    
    @Override
    public IChannelBuffer readBytesToBuff(final int length) {
        if (length == 0) {
            return ChannelBuffers.EMPTY_BUFFER;
        }
        this.checkReadableBytes(length);
        final IChannelBuffer buf = this.getFactory().getBuffer(this.getByteOrder(), length);
        buf.writeBytes(this, this.readerIndex, length);
        this.readerIndex += length;
        return buf;
    }
    
    @Override
    public byte[] readBytes() {
        return this.readBytesToBuff().array();
    }
    
    @Override
    public byte[] readBytes(final int length) {
        return this.readBytesToBuff(length).array();
    }
    
    @Override
    public void skipBytes(final int length) {
        final int newReaderIndex = this.readerIndex + length;
        if (newReaderIndex > this.writerIndex) {
            throw new IndexOutOfBoundsException("Readable bytes exceeded - Need " + newReaderIndex + ", maximum is " + this.writerIndex);
        }
        this.readerIndex = newReaderIndex;
    }
    
    @Override
    public void writeByte(final byte value) {
        this.setByte(this.writerIndex, value);
        ++this.writerIndex;
    }
    
    @Override
    public void writeShort(final short value) {
        this.setShort(this.writerIndex, value);
        this.writerIndex += 2;
    }
    
    @Override
    public void writeInt(final int value) {
        this.setInt(this.writerIndex, value);
        this.writerIndex += 4;
    }
    
    @Override
    public void writeLong(final long value) {
        this.setLong(this.writerIndex, value);
        this.writerIndex += 8;
    }
    
    @Override
    public void writeChar(final char value) {
        this.setChar(this.writerIndex, value);
        this.writerIndex += 2;
    }
    
    @Override
    public void writeFloat(final float value) {
        this.setFloat(this.writerIndex, value);
        this.writerIndex += 4;
    }
    
    @Override
    public void writeDouble(final double value) {
        this.setDouble(this.writerIndex, value);
        this.writerIndex += 8;
    }
    
    @Override
    public void writeBytes(final IChannelBuffer channelBuffer, final int length) {
        if (length > channelBuffer.getReadableBytes()) {
            throw new IndexOutOfBoundsException("Too many bytes to write - Need " + length + ", maximum is " + channelBuffer.getReadableBytes());
        }
        this.writeBytes(channelBuffer, channelBuffer.getReaderIndex(), length);
        channelBuffer.setReaderIndex(channelBuffer.getReaderIndex() + length);
    }
    
    @Override
    public void writeBytes(final IChannelBuffer channelBuffer, final int startIndex, final int length) {
        this.setBytes(this.writerIndex, channelBuffer, startIndex, length);
        this.writerIndex += length;
    }
    
    @Override
    public void writeBytes(final byte[] bytes) {
        this.writeBytes(bytes, 0, bytes.length);
    }
    
    @Override
    public void writeBytes(final byte[] src, final int srcIndex, final int length) {
        this.setBytes(this.writerIndex, src, srcIndex, length);
        this.writerIndex += length;
    }
    
    @Override
    public IChannelBuffer copy() {
        return this.copy(this.readerIndex, this.getReadableBytes());
    }
    
    @Override
    public String toString(final Charset charset) {
        return this.toString(this.readerIndex, this.getReadableBytes(), charset);
    }
    
    @Override
    public int hashCode() {
        final int aLen = this.getReadableBytes();
        final int intCount = aLen >>> 2;
        final int byteCount = aLen & 0x3;
        int hashCode = 1;
        int arrayIndex = this.getReaderIndex();
        if (this.getByteOrder() == ByteOrder.BIG_ENDIAN) {
            for (int i = intCount; i > 0; --i) {
                hashCode = 31 * hashCode + this.getInt(arrayIndex);
                arrayIndex += 4;
            }
        }
        else {
            for (int i = intCount; i > 0; --i) {
                hashCode = 31 * hashCode + Utils.swapInt(this.getInt(arrayIndex));
                arrayIndex += 4;
            }
        }
        for (int i = byteCount; i > 0; --i) {
            hashCode = 31 * hashCode + this.getByte(arrayIndex++);
        }
        if (hashCode == 0) {
            hashCode = 1;
        }
        return hashCode;
    }
    
    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ChannelBufferBase)) {
            return false;
        }
        final ChannelBufferBase bufferB = (ChannelBufferBase)o;
        final int aLen = this.getReadableBytes();
        if (aLen != bufferB.getReadableBytes()) {
            return false;
        }
        final int longCount = aLen >>> 3;
        final int byteCount = aLen & 0x7;
        int aIndex = this.getReaderIndex();
        int bIndex = bufferB.getReaderIndex();
        if (this.getByteOrder() == bufferB.getByteOrder()) {
            for (int i = longCount; i > 0; --i) {
                if (this.getLong(aIndex) != bufferB.getLong(bIndex)) {
                    return false;
                }
                aIndex += 8;
                bIndex += 8;
            }
        }
        else {
            for (int i = longCount; i > 0; --i) {
                if (this.getLong(aIndex) != Utils.swapLong(bufferB.getLong(bIndex))) {
                    return false;
                }
                aIndex += 8;
                bIndex += 8;
            }
        }
        for (int i = byteCount; i > 0; --i) {
            if (this.getByte(aIndex) != bufferB.getByte(bIndex)) {
                return false;
            }
            ++aIndex;
            ++bIndex;
        }
        return true;
    }
    
    @Override
    public int compareTo(final IChannelBuffer that) {
        if (!(that instanceof ChannelBufferBase)) {
            return -1;
        }
        final ChannelBufferBase bufferB = (ChannelBufferBase)that;
        final int aLen = this.getReadableBytes();
        final int bLen = bufferB.getReadableBytes();
        final int minLength = Math.min(aLen, bLen);
        final int uintCount = minLength >>> 2;
        final int byteCount = minLength & 0x3;
        int aIndex = this.getReaderIndex();
        int bIndex = bufferB.getReaderIndex();
        if (this.getByteOrder() == bufferB.getByteOrder()) {
            for (int i = uintCount; i > 0; --i) {
                final long va = this.getInt(aIndex) & 0xFFFFFFFFL;
                final long vb = bufferB.getInt(bIndex) & 0xFFFFFFFFL;
                if (va > vb) {
                    return 1;
                }
                if (va < vb) {
                    return -1;
                }
                aIndex += 4;
                bIndex += 4;
            }
        }
        else {
            for (int i = uintCount; i > 0; --i) {
                final long va = this.getInt(aIndex) & 0xFFFFFFFFL;
                final long vb = Utils.swapInt(bufferB.getInt(bIndex)) & 0xFFFFFFFFL;
                if (va > vb) {
                    return 1;
                }
                if (va < vb) {
                    return -1;
                }
                aIndex += 4;
                bIndex += 4;
            }
        }
        for (int i = byteCount; i > 0; --i) {
            final short va2 = (short)(this.getByte(aIndex) & 0xFF);
            final short vb2 = (short)(bufferB.getByte(bIndex) & 0xFF);
            if (va2 > vb2) {
                return 1;
            }
            if (va2 < vb2) {
                return -1;
            }
            ++aIndex;
            ++bIndex;
        }
        return aLen - bLen;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this.getClass().getSimpleName()) + '(' + "ridx=" + this.readerIndex + ", " + "widx=" + this.writerIndex + ", " + "cap=" + this.getCapacity() + ')';
    }
    
    @Override
    public char getChar(final int index) {
        return (char)this.getShort(index);
    }
    
    @Override
    public Integer getIntBox(final int index) {
        final byte b = this.getByte(index);
        if (b == 1) {
            return this.getInt(index + 1);
        }
        return null;
    }
    
    @Override
    public Float getFloatBox(final int index) {
        final byte b = this.getByte(index);
        if (b == 1) {
            return this.getFloat(index + 1);
        }
        return null;
    }
    
    @Override
    public Double getDoubleBox(final int index) {
        final byte b = this.getByte(index);
        if (b == 1) {
            return this.getDouble(index + 1);
        }
        return null;
    }
    
    @Override
    public Long getLongBox(final int index) {
        final byte b = this.getByte(index);
        if (b == 1) {
            return this.getLong(index + 1);
        }
        return null;
    }
    
    @Override
    public Short getShortBox(final int index) {
        final byte b = this.getByte(index);
        if (b == 1) {
            return this.getShort(index + 1);
        }
        return null;
    }
    
    @Override
    public float getFloat(final int index) {
        return Float.intBitsToFloat(this.getInt(index));
    }
    
    @Override
    public double getDouble(final int index) {
        return Double.longBitsToDouble(this.getLong(index));
    }
    
    @Override
    public void setChar(final int index, final char value) {
        this.setShort(index, (short)value);
    }
    
    @Override
    public void setFloat(final int index, final float value) {
        this.setInt(index, Float.floatToIntBits(value));
    }
    
    @Override
    public void setDouble(final int index, final double value) {
        this.setLong(index, Double.doubleToLongBits(value));
    }
    
    @Override
    public String readString() {
        final byte b = this.readByte();
        if (b == 1) {
            final int len = this.readInt();
            final String str = new String(this.array(), this.readerIndex, len);
            this.readerIndex += len;
            return str;
        }
        return null;
    }
    
    @Override
    public void writeString(final String str) {
        final boolean isNull = str == null;
        if (isNull) {
            this.writeByte((byte)0);
        }
        else {
            this.writeByte((byte)1);
            final byte[] bytes = str.getBytes();
            final int length = bytes.length + 4;
            this.ensureWritableBytes(length);
            this.writeInt(bytes.length);
            this.writeBytes(bytes);
        }
    }
    
    @Override
    public boolean readBoolean() {
        final byte b = this.readByte();
        return b == 1;
    }
    
    @Override
    public void writeBoolean(final boolean value) {
        if (value) {
            final byte b = 1;
            this.writeByte(b);
        }
        else {
            final byte b = 0;
            this.writeByte(b);
        }
    }
    
    @Override
    public String readString(final int len) {
        return new String(this.readBytes(len));
    }
    
    @Override
    public Short readShortBox() {
        final byte b = this.readByte();
        if (b == 1) {
            return this.readShort();
        }
        return null;
    }
    
    @Override
    public Integer readIntBox() {
        final byte b = this.readByte();
        if (b == 1) {
            return this.readInt();
        }
        return null;
    }
    
    @Override
    public Long readLongBox() {
        final byte b = this.readByte();
        if (b == 1) {
            return this.readLong();
        }
        return null;
    }
    
    @Override
    public Float readFloatBox() {
        final byte b = this.readByte();
        if (b == 1) {
            return this.readFloat();
        }
        return null;
    }
    
    @Override
    public Double readDoubleBox() {
        final byte b = this.readByte();
        if (b == 1) {
            return this.readDouble();
        }
        return null;
    }
    
    @Override
    public void writeString(final String str, final int len) {
        final byte[] bytes = str.getBytes();
        if (bytes.length > len) {
            throw new IndexOutOfBoundsException("writeString - length of str > len");
        }
        this.ensureWritableBytes(len);
        this.writeBytes(bytes);
        for (int i = bytes.length + 1; i <= len; ++i) {
            this.writeByte((byte)32);
        }
    }
    
    @Override
    public void writeShortBox(final Short value) {
        final boolean isNull = value == null;
        if (isNull) {
            this.writeByte((byte)0);
        }
        else {
            this.writeByte((byte)1);
            this.writeShort(value);
        }
    }
    
    @Override
    public void writeIntBox(final Integer value) {
        final boolean isNull = value == null;
        if (isNull) {
            this.writeByte((byte)0);
        }
        else {
            this.writeByte((byte)1);
            this.writeInt(value);
        }
    }
    
    @Override
    public void writeLongBox(final Long value) {
        final boolean isNull = value == null;
        if (isNull) {
            this.writeByte((byte)0);
        }
        else {
            this.writeByte((byte)1);
            this.writeLong(value);
        }
    }
    
    @Override
    public void writeFloatBox(final Float value) {
        final boolean isNull = value == null;
        if (isNull) {
            this.writeByte((byte)0);
        }
        else {
            this.writeByte((byte)1);
            this.writeFloat(value);
        }
    }
    
    @Override
    public void writeDoubleBox(final Double value) {
        final boolean isNull = value == null;
        if (isNull) {
            this.writeByte((byte)0);
        }
        else {
            this.writeByte((byte)1);
            this.writeDouble(value);
        }
    }
    
    protected void checkReadableBytes(final int num) {
        if (!this.isReadable(num)) {
            throw new IndexOutOfBoundsException("Not enough readable bytes - Need " + num + ", maximum is " + this.getReadableBytes());
        }
    }
    
    protected void checkWritableBytes(final int num) {
        if (!this.isWritable(num)) {
            throw new IndexOutOfBoundsException("Not enough writable bytes - Need " + num + ", maximum is " + this.getWritableBytes());
        }
    }
    
    protected String toString(final int index, final int length, final Charset charset) {
        if (length == 0) {
            return "";
        }
        return "";
    }
}
