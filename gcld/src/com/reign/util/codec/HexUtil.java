package com.reign.util.codec;

public final class HexUtil
{
    private static final char[] DIGITS_LOWER;
    private static final char[] DIGITS_UPPER;
    
    static {
        DIGITS_LOWER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        DIGITS_UPPER = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
    
    public static byte[] decodeHex(final char[] data) {
        final int len = data.length;
        if ((len & 0x1) != 0x0) {
            throw new RuntimeException("Odd number of characters.");
        }
        final byte[] out = new byte[len >> 1];
        int f;
        for (int i = 0, j = 0; j < len; ++j, f |= toDigit(data[j], j), ++j, out[i] = (byte)(f & 0xFF), ++i) {
            f = toDigit(data[j], j) << 4;
        }
        return out;
    }
    
    public static char[] encodeHex(final byte[] data) {
        return encodeHex(data, true);
    }
    
    public static char[] encodeHex(final byte[] data, final boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? HexUtil.DIGITS_LOWER : HexUtil.DIGITS_UPPER);
    }
    
    public static String encodeHexString(final byte[] data) {
        return encodeHexString(data, true);
    }
    
    public static String encodeHexString(final byte[] data, final boolean toLowerCase) {
        return new String(encodeHex(data, toLowerCase));
    }
    
    protected static char[] encodeHex(final byte[] data, final char[] toDigits) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        int i = 0;
        int j = 0;
        while (i < l) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0xF & data[i]];
            ++i;
        }
        return out;
    }
    
    protected static int toDigit(final char ch, final int index) {
        final int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }
}
