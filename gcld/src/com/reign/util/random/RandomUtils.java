package com.reign.util.random;

import java.util.*;

public class RandomUtils
{
    private static final Random random;
    
    static {
        random = new Random();
    }
    
    public static double nextDouble() {
        return RandomUtils.random.nextDouble();
    }
    
    public static double nextDouble(final double max) {
        return RandomUtils.random.nextDouble() * max;
    }
    
    public static double nextDouble(final double min, final double max) {
        return min + RandomUtils.random.nextDouble() * (max - min);
    }
    
    public static int nextInt() {
        return RandomUtils.random.nextInt();
    }
    
    public static int nextInt(final int n) {
        return RandomUtils.random.nextInt(n);
    }
    
    public static int nextInt(final int min, final int max) {
        return min + RandomUtils.random.nextInt(max - min);
    }
    
    public static float nextFloat() {
        return RandomUtils.random.nextFloat();
    }
    
    public static boolean nextBoolean() {
        return RandomUtils.random.nextBoolean();
    }
    
    public static <T> void randomArray(final T[] array, final int start, final int end) {
        for (int i = start; i < end; ++i) {
            final int index = nextInt(end - i) + i;
            swap((Object[])array, i, index);
        }
    }
    
    public static <T> void swap(final T[] array, final int index1, final int index2) {
        final T temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }
    
    public static int getRandomIndex(final int[] probList) {
        final int randomNum = nextInt(100);
        int num = 0;
        for (int index = 0; index < probList.length; ++index) {
            num += probList[index];
            if (randomNum < num) {
                return index;
            }
        }
        return -1;
    }
}
