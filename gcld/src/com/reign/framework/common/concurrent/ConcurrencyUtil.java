package com.reign.framework.common.concurrent;

public final class ConcurrencyUtil
{
    private static final int DOUG_LEA_BLACK_MAGIC_OPERAND_1 = 20;
    private static final int DOUG_LEA_BLACK_MAGIC_OPERAND_2 = 12;
    private static final int DOUG_LEA_BLACK_MAGIC_OPERAND_3 = 7;
    private static final int DOUG_LEA_BLACK_MAGIC_OPERAND_4 = 4;
    
    public static int hash(final Object object) {
        int h = object.hashCode();
        h ^= (h >>> 20 ^ h >>> 12);
        return h ^ h >>> 7 ^ h >>> 4;
    }
    
    public static int selectLock(final Object key, final int numberOfLocks) {
        final int number = numberOfLocks & numberOfLocks - 1;
        if (number != 0) {
            throw new RuntimeException("Lock number must be a power of two: " + numberOfLocks);
        }
        if (key == null) {
            return 0;
        }
        final int hash = hash(key) & numberOfLocks - 1;
        return hash;
    }
}
