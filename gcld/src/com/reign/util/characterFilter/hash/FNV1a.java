package com.reign.util.characterFilter.hash;

public class FNV1a
{
    private static final long prime = 16777619L;
    private static final long offset = 2166136261L;
    public static final long initHashCode = 2166136261L;
    
    public static long getInitHashCode() {
        return 2166136261L;
    }
    
    private static byte[] intToByteArray(final int value) {
        final byte[] rtn = new byte[4];
        for (int i = 0; i < 4; ++i) {
            final int o = (rtn.length - 1 - i) * 8;
            rtn[i] = (byte)(value >>> o & 0xFF);
        }
        return rtn;
    }
    
    public static long getHashCode(final String text, final int startIndex, final int count) {
        long currHashVal = 2166136261L;
        for (int index = startIndex; index < startIndex + count; ++index) {
            final int currCharVal;
            final char currChar = (char)(currCharVal = text.charAt(index));
            final byte[] array = intToByteArray(currCharVal);
            for (int i = 0; i < array.length; ++i) {
                currHashVal = (currHashVal ^ array[i]) * 16777619L;
            }
        }
        return currHashVal;
    }
    
    public static long getHashCode(final long lastHashCode, final char currChar) {
        long currHashVal = lastHashCode;
        final byte[] array = intToByteArray(currChar);
        for (int i = 0; i < array.length; ++i) {
            currHashVal = (currHashVal ^ array[i]) * 16777619L;
        }
        return currHashVal;
    }
}
