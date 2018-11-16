package com.reign.plugin.yx.util;

import java.security.*;

final class AESAlgorithm
{
    static final String NAME = "Rijndael_Algorithm";
    static final boolean IN = true;
    static final boolean OUT = false;
    static final int BLOCK_SIZE = 16;
    static final int[] alog;
    static final int[] log;
    static final byte[] S;
    static final byte[] Si;
    static final int[] T1;
    static final int[] T2;
    static final int[] T3;
    static final int[] T4;
    static final int[] T5;
    static final int[] T6;
    static final int[] T7;
    static final int[] T8;
    static final int[] U1;
    static final int[] U2;
    static final int[] U3;
    static final int[] U4;
    static final byte[] rcon;
    static final int[][][] shifts;
    
    static {
        alog = new int[256];
        log = new int[256];
        S = new byte[256];
        Si = new byte[256];
        T1 = new int[256];
        T2 = new int[256];
        T3 = new int[256];
        T4 = new int[256];
        T5 = new int[256];
        T6 = new int[256];
        T7 = new int[256];
        T8 = new int[256];
        U1 = new int[256];
        U2 = new int[256];
        U3 = new int[256];
        U4 = new int[256];
        rcon = new byte[30];
        shifts = new int[][][] { { new int[2], { 1, 3 }, { 2, 2 }, { 3, 1 } }, { new int[2], { 1, 5 }, { 2, 4 }, { 3, 3 } }, { new int[2], { 1, 7 }, { 3, 5 }, { 4, 4 } } };
        long time = System.currentTimeMillis();
        final int ROOT = 283;
        int j = 0;
        AESAlgorithm.alog[0] = 1;
        for (int i = 1; i < 256; ++i) {
            j = (AESAlgorithm.alog[i - 1] << 1 ^ AESAlgorithm.alog[i - 1]);
            if ((j & 0x100) != 0x0) {
                j ^= ROOT;
            }
            AESAlgorithm.alog[i] = j;
        }
        for (int i = 1; i < 255; ++i) {
            AESAlgorithm.log[AESAlgorithm.alog[i]] = i;
        }
        final byte[][] A = { { 1, 1, 1, 1, 1, 0, 0, 0 }, { 0, 1, 1, 1, 1, 1, 0, 0 }, { 0, 0, 1, 1, 1, 1, 1, 0 }, { 0, 0, 0, 1, 1, 1, 1, 1 }, { 1, 0, 0, 0, 1, 1, 1, 1 }, { 1, 1, 0, 0, 0, 1, 1, 1 }, { 1, 1, 1, 0, 0, 0, 1, 1 }, { 1, 1, 1, 1, 0, 0, 0, 1 } };
        final byte[] B = { 0, 1, 1, 0, 0, 0, 1, 1 };
        final byte[][] box = new byte[256][8];
        box[1][7] = 1;
        for (int i = 2; i < 256; ++i) {
            j = AESAlgorithm.alog[255 - AESAlgorithm.log[i]];
            for (int t = 0; t < 8; ++t) {
                box[i][t] = (byte)(j >>> 7 - t & 0x1);
            }
        }
        final byte[][] cox = new byte[256][8];
        for (int i = 0; i < 256; ++i) {
            for (int t = 0; t < 8; ++t) {
                cox[i][t] = B[t];
                for (j = 0; j < 8; ++j) {
                    final byte[] array = cox[i];
                    final int n = t;
                    array[n] ^= (byte)(A[t][j] * box[i][j]);
                }
            }
        }
        for (int i = 0; i < 256; ++i) {
            AESAlgorithm.S[i] = (byte)(cox[i][0] << 7);
            for (int t = 1; t < 8; ++t) {
                final byte[] s2 = AESAlgorithm.S;
                final int n2 = i;
                s2[n2] ^= (byte)(cox[i][t] << 7 - t);
            }
            AESAlgorithm.Si[AESAlgorithm.S[i] & 0xFF] = (byte)i;
        }
        final byte[][] G = { { 2, 1, 1, 3 }, { 3, 2, 1, 1 }, { 1, 3, 2, 1 }, { 1, 1, 3, 2 } };
        final byte[][] AA = new byte[4][8];
        for (int i = 0; i < 4; ++i) {
            for (j = 0; j < 4; ++j) {
                AA[i][j] = G[i][j];
            }
            AA[i][i + 4] = 1;
        }
        final byte[][] iG = new byte[4][4];
        for (int i = 0; i < 4; ++i) {
            byte pivot = AA[i][i];
            if (pivot == 0) {
                int t;
                for (t = i + 1; AA[t][i] == 0 && t < 4; ++t) {}
                if (t == 4) {
                    throw new RuntimeException("G matrix is not invertible");
                }
                for (j = 0; j < 8; ++j) {
                    final byte tmp = AA[i][j];
                    AA[i][j] = AA[t][j];
                    AA[t][j] = tmp;
                }
                pivot = AA[i][i];
            }
            for (j = 0; j < 8; ++j) {
                if (AA[i][j] != 0) {
                    AA[i][j] = (byte)AESAlgorithm.alog[(255 + AESAlgorithm.log[AA[i][j] & 0xFF] - AESAlgorithm.log[pivot & 0xFF]) % 255];
                }
            }
            for (int t = 0; t < 4; ++t) {
                if (i != t) {
                    for (j = i + 1; j < 8; ++j) {
                        final byte[] array2 = AA[t];
                        final int n3 = j;
                        array2[n3] ^= (byte)mul(AA[i][j], AA[t][i]);
                    }
                    AA[t][i] = 0;
                }
            }
        }
        for (int i = 0; i < 4; ++i) {
            for (j = 0; j < 4; ++j) {
                iG[i][j] = AA[i][j + 4];
            }
        }
        for (int t = 0; t < 256; ++t) {
            int s = AESAlgorithm.S[t];
            AESAlgorithm.T1[t] = mul4(s, G[0]);
            AESAlgorithm.T2[t] = mul4(s, G[1]);
            AESAlgorithm.T3[t] = mul4(s, G[2]);
            AESAlgorithm.T4[t] = mul4(s, G[3]);
            s = AESAlgorithm.Si[t];
            AESAlgorithm.T5[t] = mul4(s, iG[0]);
            AESAlgorithm.T6[t] = mul4(s, iG[1]);
            AESAlgorithm.T7[t] = mul4(s, iG[2]);
            AESAlgorithm.T8[t] = mul4(s, iG[3]);
            AESAlgorithm.U1[t] = mul4(t, iG[0]);
            AESAlgorithm.U2[t] = mul4(t, iG[1]);
            AESAlgorithm.U3[t] = mul4(t, iG[2]);
            AESAlgorithm.U4[t] = mul4(t, iG[3]);
        }
        AESAlgorithm.rcon[0] = 1;
        int r;
        for (r = 1, int t = 1; t < 30; AESAlgorithm.rcon[t++] = (byte)(r = mul(2, r))) {}
        time = System.currentTimeMillis() - time;
    }
    
    static final int mul(final int a, final int b) {
        return (a != 0 && b != 0) ? AESAlgorithm.alog[(AESAlgorithm.log[a & 0xFF] + AESAlgorithm.log[b & 0xFF]) % 255] : 0;
    }
    
    static final int mul4(int a, final byte[] b) {
        if (a == 0) {
            return 0;
        }
        a = AESAlgorithm.log[a & 0xFF];
        final int a2 = (b[0] != 0) ? (AESAlgorithm.alog[(a + AESAlgorithm.log[b[0] & 0xFF]) % 255] & 0xFF) : 0;
        final int a3 = (b[1] != 0) ? (AESAlgorithm.alog[(a + AESAlgorithm.log[b[1] & 0xFF]) % 255] & 0xFF) : 0;
        final int a4 = (b[2] != 0) ? (AESAlgorithm.alog[(a + AESAlgorithm.log[b[2] & 0xFF]) % 255] & 0xFF) : 0;
        final int a5 = (b[3] != 0) ? (AESAlgorithm.alog[(a + AESAlgorithm.log[b[3] & 0xFF]) % 255] & 0xFF) : 0;
        return a2 << 24 | a3 << 16 | a4 << 8 | a5;
    }
    
    public static Object makeKey(final byte[] k) throws InvalidKeyException {
        return makeKey(k, 16);
    }
    
    public static byte[] blockEncrypt(final byte[] in, int inOffset, final Object sessionKey) {
        final int[][] Ke = (int[][])((Object[])sessionKey)[0];
        final int ROUNDS = Ke.length - 1;
        int[] Ker = Ke[0];
        int t0 = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ Ker[0];
        int t2 = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ Ker[1];
        int t3 = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ Ker[2];
        int t4 = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ Ker[3];
        for (int r = 1; r < ROUNDS; ++r) {
            Ker = Ke[r];
            final int a0 = AESAlgorithm.T1[t0 >>> 24 & 0xFF] ^ AESAlgorithm.T2[t2 >>> 16 & 0xFF] ^ AESAlgorithm.T3[t3 >>> 8 & 0xFF] ^ AESAlgorithm.T4[t4 & 0xFF] ^ Ker[0];
            final int a2 = AESAlgorithm.T1[t2 >>> 24 & 0xFF] ^ AESAlgorithm.T2[t3 >>> 16 & 0xFF] ^ AESAlgorithm.T3[t4 >>> 8 & 0xFF] ^ AESAlgorithm.T4[t0 & 0xFF] ^ Ker[1];
            final int a3 = AESAlgorithm.T1[t3 >>> 24 & 0xFF] ^ AESAlgorithm.T2[t4 >>> 16 & 0xFF] ^ AESAlgorithm.T3[t0 >>> 8 & 0xFF] ^ AESAlgorithm.T4[t2 & 0xFF] ^ Ker[2];
            final int a4 = AESAlgorithm.T1[t4 >>> 24 & 0xFF] ^ AESAlgorithm.T2[t0 >>> 16 & 0xFF] ^ AESAlgorithm.T3[t2 >>> 8 & 0xFF] ^ AESAlgorithm.T4[t3 & 0xFF] ^ Ker[3];
            t0 = a0;
            t2 = a2;
            t3 = a3;
            t4 = a4;
        }
        final byte[] result = new byte[16];
        Ker = Ke[ROUNDS];
        int tt = Ker[0];
        result[0] = (byte)(AESAlgorithm.S[t0 >>> 24 & 0xFF] ^ tt >>> 24);
        result[1] = (byte)(AESAlgorithm.S[t2 >>> 16 & 0xFF] ^ tt >>> 16);
        result[2] = (byte)(AESAlgorithm.S[t3 >>> 8 & 0xFF] ^ tt >>> 8);
        result[3] = (byte)(AESAlgorithm.S[t4 & 0xFF] ^ tt);
        tt = Ker[1];
        result[4] = (byte)(AESAlgorithm.S[t2 >>> 24 & 0xFF] ^ tt >>> 24);
        result[5] = (byte)(AESAlgorithm.S[t3 >>> 16 & 0xFF] ^ tt >>> 16);
        result[6] = (byte)(AESAlgorithm.S[t4 >>> 8 & 0xFF] ^ tt >>> 8);
        result[7] = (byte)(AESAlgorithm.S[t0 & 0xFF] ^ tt);
        tt = Ker[2];
        result[8] = (byte)(AESAlgorithm.S[t3 >>> 24 & 0xFF] ^ tt >>> 24);
        result[9] = (byte)(AESAlgorithm.S[t4 >>> 16 & 0xFF] ^ tt >>> 16);
        result[10] = (byte)(AESAlgorithm.S[t0 >>> 8 & 0xFF] ^ tt >>> 8);
        result[11] = (byte)(AESAlgorithm.S[t2 & 0xFF] ^ tt);
        tt = Ker[3];
        result[12] = (byte)(AESAlgorithm.S[t4 >>> 24 & 0xFF] ^ tt >>> 24);
        result[13] = (byte)(AESAlgorithm.S[t0 >>> 16 & 0xFF] ^ tt >>> 16);
        result[14] = (byte)(AESAlgorithm.S[t2 >>> 8 & 0xFF] ^ tt >>> 8);
        result[15] = (byte)(AESAlgorithm.S[t3 & 0xFF] ^ tt);
        return result;
    }
    
    public static byte[] blockDecrypt(final byte[] in, int inOffset, final Object sessionKey) {
        final int[][] Kd = (int[][])((Object[])sessionKey)[1];
        final int ROUNDS = Kd.length - 1;
        int[] Kdr = Kd[0];
        int t0 = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ Kdr[0];
        int t2 = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ Kdr[1];
        int t3 = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ Kdr[2];
        int t4 = ((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ Kdr[3];
        for (int r = 1; r < ROUNDS; ++r) {
            Kdr = Kd[r];
            final int a0 = AESAlgorithm.T5[t0 >>> 24 & 0xFF] ^ AESAlgorithm.T6[t4 >>> 16 & 0xFF] ^ AESAlgorithm.T7[t3 >>> 8 & 0xFF] ^ AESAlgorithm.T8[t2 & 0xFF] ^ Kdr[0];
            final int a2 = AESAlgorithm.T5[t2 >>> 24 & 0xFF] ^ AESAlgorithm.T6[t0 >>> 16 & 0xFF] ^ AESAlgorithm.T7[t4 >>> 8 & 0xFF] ^ AESAlgorithm.T8[t3 & 0xFF] ^ Kdr[1];
            final int a3 = AESAlgorithm.T5[t3 >>> 24 & 0xFF] ^ AESAlgorithm.T6[t2 >>> 16 & 0xFF] ^ AESAlgorithm.T7[t0 >>> 8 & 0xFF] ^ AESAlgorithm.T8[t4 & 0xFF] ^ Kdr[2];
            final int a4 = AESAlgorithm.T5[t4 >>> 24 & 0xFF] ^ AESAlgorithm.T6[t3 >>> 16 & 0xFF] ^ AESAlgorithm.T7[t2 >>> 8 & 0xFF] ^ AESAlgorithm.T8[t0 & 0xFF] ^ Kdr[3];
            t0 = a0;
            t2 = a2;
            t3 = a3;
            t4 = a4;
        }
        final byte[] result = new byte[16];
        Kdr = Kd[ROUNDS];
        int tt = Kdr[0];
        result[0] = (byte)(AESAlgorithm.Si[t0 >>> 24 & 0xFF] ^ tt >>> 24);
        result[1] = (byte)(AESAlgorithm.Si[t4 >>> 16 & 0xFF] ^ tt >>> 16);
        result[2] = (byte)(AESAlgorithm.Si[t3 >>> 8 & 0xFF] ^ tt >>> 8);
        result[3] = (byte)(AESAlgorithm.Si[t2 & 0xFF] ^ tt);
        tt = Kdr[1];
        result[4] = (byte)(AESAlgorithm.Si[t2 >>> 24 & 0xFF] ^ tt >>> 24);
        result[5] = (byte)(AESAlgorithm.Si[t0 >>> 16 & 0xFF] ^ tt >>> 16);
        result[6] = (byte)(AESAlgorithm.Si[t4 >>> 8 & 0xFF] ^ tt >>> 8);
        result[7] = (byte)(AESAlgorithm.Si[t3 & 0xFF] ^ tt);
        tt = Kdr[2];
        result[8] = (byte)(AESAlgorithm.Si[t3 >>> 24 & 0xFF] ^ tt >>> 24);
        result[9] = (byte)(AESAlgorithm.Si[t2 >>> 16 & 0xFF] ^ tt >>> 16);
        result[10] = (byte)(AESAlgorithm.Si[t0 >>> 8 & 0xFF] ^ tt >>> 8);
        result[11] = (byte)(AESAlgorithm.Si[t4 & 0xFF] ^ tt);
        tt = Kdr[3];
        result[12] = (byte)(AESAlgorithm.Si[t4 >>> 24 & 0xFF] ^ tt >>> 24);
        result[13] = (byte)(AESAlgorithm.Si[t3 >>> 16 & 0xFF] ^ tt >>> 16);
        result[14] = (byte)(AESAlgorithm.Si[t2 >>> 8 & 0xFF] ^ tt >>> 8);
        result[15] = (byte)(AESAlgorithm.Si[t0 & 0xFF] ^ tt);
        return result;
    }
    
    public static int blockSize() {
        return 16;
    }
    
    public static synchronized Object makeKey(final byte[] k, final int blockSize) throws InvalidKeyException {
        if (k == null) {
            throw new InvalidKeyException("Empty key");
        }
        if (k.length != 16 && k.length != 24 && k.length != 32) {
            throw new InvalidKeyException("Incorrect key length");
        }
        final int ROUNDS = getRounds(k.length, blockSize);
        final int BC = blockSize / 4;
        final int[][] Ke = new int[ROUNDS + 1][BC];
        final int[][] Kd = new int[ROUNDS + 1][BC];
        final int ROUND_KEY_COUNT = (ROUNDS + 1) * BC;
        final int KC = k.length / 4;
        final int[] tk = new int[KC];
        for (int i = 0, j = 0; i < KC; tk[i++] = ((k[j++] & 0xFF) << 24 | (k[j++] & 0xFF) << 16 | (k[j++] & 0xFF) << 8 | (k[j++] & 0xFF))) {}
        int t = 0;
        for (int j = 0; j < KC && t < ROUND_KEY_COUNT; ++j, ++t) {
            Ke[t / BC][t % BC] = tk[j];
            Kd[ROUNDS - t / BC][t % BC] = tk[j];
        }
        int rconpointer = 0;
        while (t < ROUND_KEY_COUNT) {
            int tt = tk[KC - 1];
            final int[] array = tk;
            final int n = 0;
            array[n] ^= ((AESAlgorithm.S[tt >>> 16 & 0xFF] & 0xFF) << 24 ^ (AESAlgorithm.S[tt >>> 8 & 0xFF] & 0xFF) << 16 ^ (AESAlgorithm.S[tt & 0xFF] & 0xFF) << 8 ^ (AESAlgorithm.S[tt >>> 24 & 0xFF] & 0xFF) ^ (AESAlgorithm.rcon[rconpointer++] & 0xFF) << 24);
            if (KC != 8) {
                int[] array2;
                int n2;
                for (int i = 1, j = 0; i < KC; n2 = i++, array2[n2] ^= tk[j++]) {
                    array2 = tk;
                }
            }
            else {
                int[] array3;
                int n3;
                for (int i = 1, j = 0; i < KC / 2; n3 = i++, array3[n3] ^= tk[j++]) {
                    array3 = tk;
                }
                tt = tk[KC / 2 - 1];
                final int[] array4 = tk;
                final int n4 = KC / 2;
                array4[n4] ^= ((AESAlgorithm.S[tt & 0xFF] & 0xFF) ^ (AESAlgorithm.S[tt >>> 8 & 0xFF] & 0xFF) << 8 ^ (AESAlgorithm.S[tt >>> 16 & 0xFF] & 0xFF) << 16 ^ (AESAlgorithm.S[tt >>> 24 & 0xFF] & 0xFF) << 24);
                int[] array5;
                int n5;
                for (int j = KC / 2, i = j + 1; i < KC; n5 = i++, array5[n5] ^= tk[j++]) {
                    array5 = tk;
                }
            }
            for (int j = 0; j < KC && t < ROUND_KEY_COUNT; ++j, ++t) {
                Ke[t / BC][t % BC] = tk[j];
                Kd[ROUNDS - t / BC][t % BC] = tk[j];
            }
        }
        for (int r = 1; r < ROUNDS; ++r) {
            for (int j = 0; j < BC; ++j) {
                final int tt = Kd[r][j];
                Kd[r][j] = (AESAlgorithm.U1[tt >>> 24 & 0xFF] ^ AESAlgorithm.U2[tt >>> 16 & 0xFF] ^ AESAlgorithm.U3[tt >>> 8 & 0xFF] ^ AESAlgorithm.U4[tt & 0xFF]);
            }
        }
        final Object[] sessionKey = { Ke, Kd };
        return sessionKey;
    }
    
    public static byte[] blockEncrypt(final byte[] in, int inOffset, final Object sessionKey, final int blockSize) {
        if (blockSize == 16) {
            return blockEncrypt(in, inOffset, sessionKey);
        }
        final Object[] sKey = (Object[])sessionKey;
        final int[][] Ke = (int[][])sKey[0];
        final int BC = blockSize / 4;
        final int ROUNDS = Ke.length - 1;
        final int SC = (BC == 4) ? 0 : ((BC == 6) ? 1 : 2);
        final int s1 = AESAlgorithm.shifts[SC][1][0];
        final int s2 = AESAlgorithm.shifts[SC][2][0];
        final int s3 = AESAlgorithm.shifts[SC][3][0];
        final int[] a = new int[BC];
        final int[] t = new int[BC];
        final byte[] result = new byte[blockSize];
        int j = 0;
        for (int i = 0; i < BC; ++i) {
            t[i] = (((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ Ke[0][i]);
        }
        for (int r = 1; r < ROUNDS; ++r) {
            for (int i = 0; i < BC; ++i) {
                a[i] = (AESAlgorithm.T1[t[i] >>> 24 & 0xFF] ^ AESAlgorithm.T2[t[(i + s1) % BC] >>> 16 & 0xFF] ^ AESAlgorithm.T3[t[(i + s2) % BC] >>> 8 & 0xFF] ^ AESAlgorithm.T4[t[(i + s3) % BC] & 0xFF] ^ Ke[r][i]);
            }
            System.arraycopy(a, 0, t, 0, BC);
        }
        for (int i = 0; i < BC; ++i) {
            final int tt = Ke[ROUNDS][i];
            result[j++] = (byte)(AESAlgorithm.S[t[i] >>> 24 & 0xFF] ^ tt >>> 24);
            result[j++] = (byte)(AESAlgorithm.S[t[(i + s1) % BC] >>> 16 & 0xFF] ^ tt >>> 16);
            result[j++] = (byte)(AESAlgorithm.S[t[(i + s2) % BC] >>> 8 & 0xFF] ^ tt >>> 8);
            result[j++] = (byte)(AESAlgorithm.S[t[(i + s3) % BC] & 0xFF] ^ tt);
        }
        return result;
    }
    
    public static byte[] blockDecrypt(final byte[] in, int inOffset, final Object sessionKey, final int blockSize) {
        if (blockSize == 16) {
            return blockDecrypt(in, inOffset, sessionKey);
        }
        final Object[] sKey = (Object[])sessionKey;
        final int[][] Kd = (int[][])sKey[1];
        final int BC = blockSize / 4;
        final int ROUNDS = Kd.length - 1;
        final int SC = (BC == 4) ? 0 : ((BC == 6) ? 1 : 2);
        final int s1 = AESAlgorithm.shifts[SC][1][1];
        final int s2 = AESAlgorithm.shifts[SC][2][1];
        final int s3 = AESAlgorithm.shifts[SC][3][1];
        final int[] a = new int[BC];
        final int[] t = new int[BC];
        final byte[] result = new byte[blockSize];
        int j = 0;
        for (int i = 0; i < BC; ++i) {
            t[i] = (((in[inOffset++] & 0xFF) << 24 | (in[inOffset++] & 0xFF) << 16 | (in[inOffset++] & 0xFF) << 8 | (in[inOffset++] & 0xFF)) ^ Kd[0][i]);
        }
        for (int r = 1; r < ROUNDS; ++r) {
            for (int i = 0; i < BC; ++i) {
                a[i] = (AESAlgorithm.T5[t[i] >>> 24 & 0xFF] ^ AESAlgorithm.T6[t[(i + s1) % BC] >>> 16 & 0xFF] ^ AESAlgorithm.T7[t[(i + s2) % BC] >>> 8 & 0xFF] ^ AESAlgorithm.T8[t[(i + s3) % BC] & 0xFF] ^ Kd[r][i]);
            }
            System.arraycopy(a, 0, t, 0, BC);
        }
        for (int i = 0; i < BC; ++i) {
            final int tt = Kd[ROUNDS][i];
            result[j++] = (byte)(AESAlgorithm.Si[t[i] >>> 24 & 0xFF] ^ tt >>> 24);
            result[j++] = (byte)(AESAlgorithm.Si[t[(i + s1) % BC] >>> 16 & 0xFF] ^ tt >>> 16);
            result[j++] = (byte)(AESAlgorithm.Si[t[(i + s2) % BC] >>> 8 & 0xFF] ^ tt >>> 8);
            result[j++] = (byte)(AESAlgorithm.Si[t[(i + s3) % BC] & 0xFF] ^ tt);
        }
        return result;
    }
    
    public static int getRounds(final int keySize, final int blockSize) {
        switch (keySize) {
            case 16: {
                return (blockSize == 16) ? 10 : ((blockSize == 24) ? 12 : 14);
            }
            case 24: {
                return (blockSize != 32) ? 12 : 14;
            }
            default: {
                return 14;
            }
        }
    }
}
