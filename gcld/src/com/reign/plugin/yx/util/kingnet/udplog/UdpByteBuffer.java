package com.reign.plugin.yx.util.kingnet.udplog;

import java.io.*;
import java.nio.channels.*;
import java.nio.*;

public class UdpByteBuffer
{
    protected final byte[] array;
    private int writerIndex;
    
    protected UdpByteBuffer(final byte[] array) {
        if (array == null) {
            throw new NullPointerException("array");
        }
        this.array = array;
    }
    
    protected UdpByteBuffer(final int length) {
        this(new byte[length]);
    }
    
    public void writeByte(final int value) {
        this.setByte(this.writerIndex++, value);
    }
    
    public void writeShort(final int value) {
        this.setShort(this.writerIndex, value);
        this.writerIndex += 2;
    }
    
    public void writeMedium(final int value) {
        this.setMedium(this.writerIndex, value);
        this.writerIndex += 3;
    }
    
    public void writeInt(final int value) {
        this.setInt(this.writerIndex, value);
        this.writerIndex += 4;
    }
    
    public void writeLong(final long value) {
        this.setLong(this.writerIndex, value);
        this.writerIndex += 8;
    }
    
    public void writeChar(final int value) {
        this.writeShort(value);
    }
    
    public void writeFloat(final float value) {
        this.writeInt(Float.floatToRawIntBits(value));
    }
    
    public void writeDouble(final double value) {
        this.writeLong(Double.doubleToRawLongBits(value));
    }
    
    public void writeBytes(final byte[] src, final int srcIndex, final int length) {
        this.setBytes(this.writerIndex, src, srcIndex, length);
        this.writerIndex += length;
    }
    
    public void writeBytes(final byte[] src) {
        this.writeBytes(src, 0, src.length);
    }
    
    public void writeBytes(final ByteBuffer src) {
        final int length = src.remaining();
        this.setBytes(this.writerIndex, src);
        this.writerIndex += length;
    }
    
    public int writeBytes(final InputStream in, final int length) throws IOException {
        final int writtenBytes = this.setBytes(this.writerIndex, in, length);
        if (writtenBytes > 0) {
            this.writerIndex += writtenBytes;
        }
        return writtenBytes;
    }
    
    public int writeBytes(final ScatteringByteChannel in, final int length) throws IOException {
        final int writtenBytes = this.setBytes(this.writerIndex, in, length);
        if (writtenBytes > 0) {
            this.writerIndex += writtenBytes;
        }
        return writtenBytes;
    }
    
    public void writeZero(final int length) {
        if (length == 0) {
            return;
        }
        if (length < 0) {
            throw new IllegalArgumentException("length must be 0 or greater than 0.");
        }
        final int nLong = length >>> 3;
        final int nBytes = length & 0x7;
        for (int i = nLong; i > 0; --i) {
            this.writeLong(0L);
        }
        if (nBytes == 4) {
            this.writeInt(0);
        }
        else if (nBytes < 4) {
            for (int i = nBytes; i > 0; --i) {
                this.writeByte(0);
            }
        }
        else {
            this.writeInt(0);
            for (int i = nBytes - 4; i > 0; --i) {
                this.writeByte(0);
            }
        }
    }
    
    public void setByte(final int index, final int value) {
        this.array[index] = (byte)value;
    }
    
    public void setShort(final int index, final int value) {
        this.array[index] = (byte)(value >>> 8);
        this.array[index + 1] = (byte)value;
    }
    
    public void setMedium(final int index, final int value) {
        this.array[index] = (byte)(value >>> 16);
        this.array[index + 1] = (byte)(value >>> 8);
        this.array[index + 2] = (byte)value;
    }
    
    public void setInt(final int index, final int value) {
        this.array[index] = (byte)(value >>> 24);
        this.array[index + 1] = (byte)(value >>> 16);
        this.array[index + 2] = (byte)(value >>> 8);
        this.array[index + 3] = (byte)value;
    }
    
    public void setLong(final int index, final long value) {
        this.array[index] = (byte)(value >>> 56);
        this.array[index + 1] = (byte)(value >>> 48);
        this.array[index + 2] = (byte)(value >>> 40);
        this.array[index + 3] = (byte)(value >>> 32);
        this.array[index + 4] = (byte)(value >>> 24);
        this.array[index + 5] = (byte)(value >>> 16);
        this.array[index + 6] = (byte)(value >>> 8);
        this.array[index + 7] = (byte)value;
    }
    
    public void setBytes(final int index, final byte[] src, final int srcIndex, final int length) {
        System.arraycopy(src, srcIndex, this.array, index, length);
    }
    
    public void setBytes(final int index, final ByteBuffer src) {
        src.get(this.array, index, src.remaining());
    }
    
    public int setBytes(int index, final InputStream in, int length) throws IOException {
        int readBytes = 0;
        do {
            final int localReadBytes = in.read(this.array, index, length);
            if (localReadBytes < 0) {
                if (readBytes == 0) {
                    return -1;
                }
                break;
            }
            else {
                readBytes += localReadBytes;
                index += localReadBytes;
                length -= localReadBytes;
            }
        } while (length > 0);
        return readBytes;
    }
    
    public int setBytes(final int index, final ScatteringByteChannel in, final int length) throws IOException {
        final ByteBuffer buf = ByteBuffer.wrap(this.array, index, length);
        int readBytes = 0;
        while (true) {
            int localReadBytes;
            try {
                localReadBytes = in.read(buf);
            }
            catch (ClosedChannelException e) {
                localReadBytes = -1;
            }
            if (localReadBytes < 0) {
                if (readBytes == 0) {
                    return -1;
                }
                break;
            }
            else {
                if (localReadBytes == 0) {
                    break;
                }
                readBytes += localReadBytes;
                if (readBytes >= length) {
                    break;
                }
                continue;
            }
        }
        return readBytes;
    }
    
    public ByteBuffer toByteBuffer() {
        return ByteBuffer.wrap(this.array, 0, this.array.length).order(ByteOrder.BIG_ENDIAN);
    }
}
