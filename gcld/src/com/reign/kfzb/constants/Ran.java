package com.reign.kfzb.constants;

import java.io.*;
import java.util.concurrent.atomic.*;

public class Ran implements Serializable
{
    static final long serialVersionUID = 3905348978240129619L;
    private final AtomicLong seed;
    private static final long multiplier = 25214903917L;
    private static final long addend = 11L;
    private static final long mask = 281474976710655L;
    private static volatile long seedUniquifier;
    
    static {
        Ran.seedUniquifier = 8682522807148012L;
    }
    
    public Ran() {
        this(++Ran.seedUniquifier + System.nanoTime());
    }
    
    public Ran(long seed) {
        this.seed = new AtomicLong(0L);
        seed = ((seed ^ 0x5DEECE66DL) & 0xFFFFFFFFFFFFL);
        this.seed.set(seed);
    }
    
    public int nextInt(final int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n must be positive");
        }
        if ((n & -n) == n) {
            return n * this.next(31) >> 31;
        }
        int bits;
        int val;
        do {
            bits = this.next(31);
            val = bits % n;
        } while (bits - val + (n - 1) < 0);
        return val;
    }
    
    protected int next(final int bits) {
        final AtomicLong seed = this.seed;
        long oldseed;
        long nextseed;
        do {
            oldseed = seed.get();
            nextseed = (oldseed * 25214903917L + 11L & 0xFFFFFFFFFFFFL);
        } while (!seed.compareAndSet(oldseed, nextseed));
        return (int)(nextseed >>> 48 - bits);
    }
}
