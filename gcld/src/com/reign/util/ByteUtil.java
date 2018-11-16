package com.reign.util;

public class ByteUtil
{
    public static byte[] intToBytes(final int l, final int length) {
        if (length % 8 != 0) {
            throw new IllegalArgumentException("IllegalArgument [length:" + length + "]");
        }
        final byte[] bytes = new byte[length >> 3];
        for (int i = bytes.length - 1; i >= 0; --i) {
            bytes[i] = (byte)(l >> 8 * (bytes.length - i - 1));
        }
        return bytes;
    }
    
    public static int bytesToInt(final byte[] bytes) {
        int l = 0;
        for (int i = 0; i < bytes.length; ++i) {
            l |= (bytes[i] & 0xFF) << 8 * (bytes.length - i - 1);
        }
        return l;
    }
}
