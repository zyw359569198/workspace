package com.reign.gcld.slave.util;

public class SlaveUtil
{
    public static int get1Num(long a) {
        int num = 0;
        for (int i = 0; i < 64; ++i) {
            num += (int)(a & 0x1L);
            a >>= 1;
            if (0L == a) {
                break;
            }
        }
        return num;
    }
    
    public static int hasReward(long a, final int index) {
        a >>= index - 1;
        return (int)(a & 0x1L);
    }
    
    public static int getIndex(long a) {
        int index = 1;
        final int count = 64;
        while (count > 0) {
            if ((a & 0x1L) == 0x0L) {
                return index;
            }
            ++index;
            a >>= 1;
        }
        return 0;
    }
    
    public static int getFirst1Index(long a) {
        int index = 1;
        for (int count = 64; count > 0 && a > 0L; --count, a >>= 1) {
            if ((a & 0x1L) == 0x1L) {
                return index;
            }
            ++index;
        }
        return 0;
    }
    
    public static int getLast1Index(long a) {
        int result = 0;
        int index = 1;
        for (int count = 64; count > 0 && a > 0L; --count, a >>= 1) {
            if ((a & 0x1L) == 0x1L) {
                result = index;
            }
            ++index;
        }
        return result;
    }
    
    public static long set1(final long a, final int index) {
        final int result = hasReward(a, index);
        if (1 == result) {
            return a;
        }
        return a + (1L << index - 1);
    }
    
    public static long set0(final long a, final int index) {
        final long temp = (long)Math.pow(2.0, index - 1);
        if (temp >= a) {
            return 0L;
        }
        return a ^ temp;
    }
    
    public static long set1All(final int bit) {
        if (bit <= 0) {
            return 0L;
        }
        return (1L << bit - 1) + set1All(bit - 1);
    }
    
    public static void main(final String[] args) {
        for (int i = 0; i <= 100; ++i) {
            System.out.println(getLast1Index(i));
        }
    }
}
