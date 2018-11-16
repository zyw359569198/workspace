package com.reign.util.buffer;

public class Utils
{
    public static short swapShort(final short value) {
        return (short)(value << 8 | (value >>> 8 & 0xFF));
    }
    
    public static int swapInt(final int value) {
        return swapShort((short)value) << 16 | (swapShort((short)(value >>> 16)) & 0xFFFF);
    }
    
    public static long swapLong(final long value) {
        return swapInt((int)value) << 32 | (swapInt((int)(value >>> 32)) & 0xFFFFFFFFL);
    }
}
