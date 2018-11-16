package com.reign.util.buffer;

import java.nio.*;
import java.nio.charset.*;

public interface IChannelBuffer extends Comparable<IChannelBuffer>
{
    IChannelBufferFactory getFactory();
    
    int getCapacity();
    
    ByteOrder getByteOrder();
    
    int getReaderIndex();
    
    void setReaderIndex(final int p0);
    
    int getWriterIndex();
    
    void setWriterIndex(final int p0);
    
    void setIndex(final int p0, final int p1);
    
    int getReadableBytes();
    
    int getWritableBytes();
    
    boolean isReadable();
    
    boolean isReadable(final int p0);
    
    boolean isWritable();
    
    boolean isWritable(final int p0);
    
    void clear();
    
    void markReaderIndex();
    
    void resetReaderIndex();
    
    void markWriterIndex();
    
    void resetWriterIndex();
    
    void discardReadBytes();
    
    void ensureWritableBytes(final int p0);
    
    void getBytes(final int p0, final byte[] p1, final int p2, final int p3);
    
    boolean readBoolean();
    
    byte readByte();
    
    short readShort();
    
    Short readShortBox();
    
    int readInt();
    
    Integer readIntBox();
    
    long readLong();
    
    Long readLongBox();
    
    char readChar();
    
    float readFloat();
    
    Float readFloatBox();
    
    double readDouble();
    
    Double readDoubleBox();
    
    IChannelBuffer readBytesToBuff();
    
    IChannelBuffer readBytesToBuff(final int p0);
    
    byte[] readBytes();
    
    byte[] readBytes(final int p0);
    
    String readString();
    
    String readString(final int p0);
    
    void skipBytes(final int p0);
    
    void writeByte(final byte p0);
    
    void writeShort(final short p0);
    
    void writeShortBox(final Short p0);
    
    void writeInt(final int p0);
    
    void writeIntBox(final Integer p0);
    
    void writeLong(final long p0);
    
    void writeLongBox(final Long p0);
    
    void writeChar(final char p0);
    
    void writeFloat(final float p0);
    
    void writeFloatBox(final Float p0);
    
    void writeDouble(final double p0);
    
    void writeDoubleBox(final Double p0);
    
    void writeBytes(final IChannelBuffer p0, final int p1);
    
    void writeBytes(final IChannelBuffer p0, final int p1, final int p2);
    
    void writeBytes(final byte[] p0);
    
    void writeBytes(final byte[] p0, final int p1, final int p2);
    
    void writeString(final String p0);
    
    void writeString(final String p0, final int p1);
    
    void writeBoolean(final boolean p0);
    
    IChannelBuffer copy();
    
    IChannelBuffer copy(final int p0, final int p1);
    
    IChannelBuffer duplicate();
    
    boolean hasArray();
    
    byte[] array();
    
    String toString(final Charset p0);
    
    @Override
	int hashCode();
    
    @Override
	boolean equals(final Object p0);
    
    @Override
	int compareTo(final IChannelBuffer p0);
    
    @Override
	String toString();
    
    byte getByte(final int p0);
    
    short getShort(final int p0);
    
    Short getShortBox(final int p0);
    
    int getInt(final int p0);
    
    Integer getIntBox(final int p0);
    
    long getLong(final int p0);
    
    Long getLongBox(final int p0);
    
    char getChar(final int p0);
    
    float getFloat(final int p0);
    
    Float getFloatBox(final int p0);
    
    double getDouble(final int p0);
    
    Double getDoubleBox(final int p0);
    
    void setByte(final int p0, final byte p1);
    
    void setShort(final int p0, final short p1);
    
    void setInt(final int p0, final int p1);
    
    void setLong(final int p0, final long p1);
    
    void setChar(final int p0, final char p1);
    
    void setFloat(final int p0, final float p1);
    
    void setDouble(final int p0, final double p1);
    
    void setBytes(final int p0, final byte[] p1, final int p2, final int p3);
    
    void setBytes(final int p0, final IChannelBuffer p1, final int p2, final int p3);
    
    int arrayOffset();
}
