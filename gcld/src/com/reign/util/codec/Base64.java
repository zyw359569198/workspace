package com.reign.util.codec;

import java.util.*;

public class Base64
{
    private static final boolean devLineSep = true;
    private static final char[] CA;
    private static final int[] IA;
    
    static {
        CA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();
        Arrays.fill(IA = new int[256], -1);
        for (int i = 0, iS = Base64.CA.length; i < iS; ++i) {
            Base64.IA[Base64.CA[i]] = i;
        }
        Base64.IA[61] = 0;
    }
    
    public static final char[] encodeToChar(final byte[] sArr) {
        return encodeToChar(sArr, true);
    }
    
    public static final char[] encodeToChar(final byte[] sArr, final boolean lineSep) {
        final int sLen = (sArr != null) ? sArr.length : 0;
        if (sLen == 0) {
            return new char[0];
        }
        final int eLen = sLen / 3 * 3;
        final int cCnt = (sLen - 1) / 3 + 1 << 2;
        final int dLen = cCnt + (lineSep ? ((cCnt - 1) / 76 << 1) : 0);
        final char[] dArr = new char[dLen];
        int s = 0;
        int d = 0;
        int cc = 0;
        while (s < eLen) {
            final int i = (sArr[s++] & 0xFF) << 16 | (sArr[s++] & 0xFF) << 8 | (sArr[s++] & 0xFF);
            dArr[d++] = Base64.CA[i >>> 18 & 0x3F];
            dArr[d++] = Base64.CA[i >>> 12 & 0x3F];
            dArr[d++] = Base64.CA[i >>> 6 & 0x3F];
            dArr[d++] = Base64.CA[i & 0x3F];
            if (lineSep && ++cc == 19 && d < dLen - 2) {
                dArr[d++] = '\r';
                dArr[d++] = '\n';
                cc = 0;
            }
        }
        final int left = sLen - eLen;
        if (left > 0) {
            final int j = (sArr[eLen] & 0xFF) << 10 | ((left == 2) ? ((sArr[sLen - 1] & 0xFF) << 2) : 0);
            dArr[dLen - 4] = Base64.CA[j >> 12];
            dArr[dLen - 3] = Base64.CA[j >>> 6 & 0x3F];
            dArr[dLen - 2] = ((left == 2) ? Base64.CA[j & 0x3F] : '=');
            dArr[dLen - 1] = '=';
        }
        return dArr;
    }
    
    public static final byte[] decode(final char[] sArr) {
        final int sLen = (sArr != null) ? sArr.length : 0;
        if (sLen == 0) {
            return new byte[0];
        }
        int sepCnt = 0;
        for (int i = 0; i < sLen; ++i) {
            if (Base64.IA[sArr[i]] < 0) {
                ++sepCnt;
            }
        }
        if ((sLen - sepCnt) % 4 != 0) {
            return null;
        }
        int pad = 0;
        int j = sLen;
        while (j > 1 && Base64.IA[sArr[--j]] <= 0) {
            if (sArr[j] == '=') {
                ++pad;
            }
        }
        final int len = ((sLen - sepCnt) * 6 >> 3) - pad;
        final byte[] dArr = new byte[len];
        int s = 0;
        int d = 0;
        while (d < len) {
            int k = 0;
            for (int l = 0; l < 4; ++l) {
                final int c = Base64.IA[sArr[s++]];
                if (c >= 0) {
                    k |= c << 18 - l * 6;
                }
                else {
                    --l;
                }
            }
            dArr[d++] = (byte)(k >> 16);
            if (d < len) {
                dArr[d++] = (byte)(k >> 8);
                if (d >= len) {
                    continue;
                }
                dArr[d++] = (byte)k;
            }
        }
        return dArr;
    }
    
    public static final byte[] decodeFast(final char[] sArr) {
        final int sLen = sArr.length;
        if (sLen == 0) {
            return new byte[0];
        }
        int sIx;
        int eIx;
        for (sIx = 0, eIx = sLen - 1; sIx < eIx; ++sIx) {
            if (Base64.IA[sArr[sIx]] >= 0) {
                break;
            }
        }
        while (eIx > 0 && Base64.IA[sArr[eIx]] < 0) {
            --eIx;
        }
        final int pad = (sArr[eIx] == '=') ? ((sArr[eIx - 1] == '=') ? 2 : 1) : 0;
        final int cCnt = eIx - sIx + 1;
        final int sepCnt = (sLen > 76) ? (((sArr[76] == '\r') ? (cCnt / 78) : 0) << 1) : 0;
        final int len = ((cCnt - sepCnt) * 6 >> 3) - pad;
        final byte[] dArr = new byte[len];
        int d = 0;
        int cc = 0;
        final int eLen = len / 3 * 3;
        while (d < eLen) {
            final int i = Base64.IA[sArr[sIx++]] << 18 | Base64.IA[sArr[sIx++]] << 12 | Base64.IA[sArr[sIx++]] << 6 | Base64.IA[sArr[sIx++]];
            dArr[d++] = (byte)(i >> 16);
            dArr[d++] = (byte)(i >> 8);
            dArr[d++] = (byte)i;
            if (sepCnt > 0 && ++cc == 19) {
                sIx += 2;
                cc = 0;
            }
        }
        if (d < len) {
            int j = 0;
            for (int k = 0; sIx <= eIx - pad; j |= Base64.IA[sArr[sIx++]] << 18 - k * 6, ++k) {}
            for (int r = 16; d < len; dArr[d++] = (byte)(j >> r), r -= 8) {}
        }
        return dArr;
    }
    
    public static final byte[] encodeToByte(final byte[] sArr) {
        return encodeToByte(sArr, true);
    }
    
    public static final byte[] encodeToByte(final byte[] sArr, final boolean lineSep) {
        final int sLen = (sArr != null) ? sArr.length : 0;
        if (sLen == 0) {
            return new byte[0];
        }
        final int eLen = sLen / 3 * 3;
        final int cCnt = (sLen - 1) / 3 + 1 << 2;
        final int dLen = cCnt + (lineSep ? ((cCnt - 1) / 76 << 1) : 0);
        final byte[] dArr = new byte[dLen];
        int s = 0;
        int d = 0;
        int cc = 0;
        while (s < eLen) {
            final int i = (sArr[s++] & 0xFF) << 16 | (sArr[s++] & 0xFF) << 8 | (sArr[s++] & 0xFF);
            dArr[d++] = (byte)Base64.CA[i >>> 18 & 0x3F];
            dArr[d++] = (byte)Base64.CA[i >>> 12 & 0x3F];
            dArr[d++] = (byte)Base64.CA[i >>> 6 & 0x3F];
            dArr[d++] = (byte)Base64.CA[i & 0x3F];
            if (lineSep && ++cc == 19 && d < dLen - 2) {
                dArr[d++] = 13;
                dArr[d++] = 10;
                cc = 0;
            }
        }
        final int left = sLen - eLen;
        if (left > 0) {
            final int j = (sArr[eLen] & 0xFF) << 10 | ((left == 2) ? ((sArr[sLen - 1] & 0xFF) << 2) : 0);
            dArr[dLen - 4] = (byte)Base64.CA[j >> 12];
            dArr[dLen - 3] = (byte)Base64.CA[j >>> 6 & 0x3F];
            dArr[dLen - 2] = (left == 2) ? ((byte)Base64.CA[j & 0x3F]) : 61;
            dArr[dLen - 1] = 61;
        }
        return dArr;
    }
    
    public static final byte[] decode(final byte[] sArr) {
        final int sLen = sArr.length;
        int sepCnt = 0;
        for (int i = 0; i < sLen; ++i) {
            if (Base64.IA[sArr[i] & 0xFF] < 0) {
                ++sepCnt;
            }
        }
        if ((sLen - sepCnt) % 4 != 0) {
            return null;
        }
        int pad = 0;
        int j = sLen;
        while (j > 1 && Base64.IA[sArr[--j] & 0xFF] <= 0) {
            if (sArr[j] == 61) {
                ++pad;
            }
        }
        final int len = ((sLen - sepCnt) * 6 >> 3) - pad;
        final byte[] dArr = new byte[len];
        int s = 0;
        int d = 0;
        while (d < len) {
            int k = 0;
            for (int l = 0; l < 4; ++l) {
                final int c = Base64.IA[sArr[s++] & 0xFF];
                if (c >= 0) {
                    k |= c << 18 - l * 6;
                }
                else {
                    --l;
                }
            }
            dArr[d++] = (byte)(k >> 16);
            if (d < len) {
                dArr[d++] = (byte)(k >> 8);
                if (d >= len) {
                    continue;
                }
                dArr[d++] = (byte)k;
            }
        }
        return dArr;
    }
    
    public static final byte[] decodeFast(final byte[] sArr) {
        final int sLen = sArr.length;
        if (sLen == 0) {
            return new byte[0];
        }
        int sIx;
        int eIx;
        for (sIx = 0, eIx = sLen - 1; sIx < eIx; ++sIx) {
            if (Base64.IA[sArr[sIx] & 0xFF] >= 0) {
                break;
            }
        }
        while (eIx > 0 && Base64.IA[sArr[eIx] & 0xFF] < 0) {
            --eIx;
        }
        final int pad = (sArr[eIx] == 61) ? ((sArr[eIx - 1] == 61) ? 2 : 1) : 0;
        final int cCnt = eIx - sIx + 1;
        final int sepCnt = (sLen > 76) ? (((sArr[76] == 13) ? (cCnt / 78) : 0) << 1) : 0;
        final int len = ((cCnt - sepCnt) * 6 >> 3) - pad;
        final byte[] dArr = new byte[len];
        int d = 0;
        int cc = 0;
        final int eLen = len / 3 * 3;
        while (d < eLen) {
            final int i = Base64.IA[sArr[sIx++]] << 18 | Base64.IA[sArr[sIx++]] << 12 | Base64.IA[sArr[sIx++]] << 6 | Base64.IA[sArr[sIx++]];
            dArr[d++] = (byte)(i >> 16);
            dArr[d++] = (byte)(i >> 8);
            dArr[d++] = (byte)i;
            if (sepCnt > 0 && ++cc == 19) {
                sIx += 2;
                cc = 0;
            }
        }
        if (d < len) {
            int j = 0;
            for (int k = 0; sIx <= eIx - pad; j |= Base64.IA[sArr[sIx++]] << 18 - k * 6, ++k) {}
            for (int r = 16; d < len; dArr[d++] = (byte)(j >> r), r -= 8) {}
        }
        return dArr;
    }
    
    public static final String encodeToString(final byte[] sArr, final boolean lineSep) {
        return new String(encodeToChar(sArr, lineSep));
    }
    
    public static final String encodeToString(final byte[] sArr) {
        return new String(encodeToChar(sArr, true));
    }
    
    public static final byte[] decode(final String str) {
        final int sLen = (str != null) ? str.length() : 0;
        if (sLen == 0) {
            return new byte[0];
        }
        int sepCnt = 0;
        for (int i = 0; i < sLen; ++i) {
            if (Base64.IA[str.charAt(i)] < 0) {
                ++sepCnt;
            }
        }
        if ((sLen - sepCnt) % 4 != 0) {
            return null;
        }
        int pad = 0;
        int j = sLen;
        while (j > 1 && Base64.IA[str.charAt(--j)] <= 0) {
            if (str.charAt(j) == '=') {
                ++pad;
            }
        }
        final int len = ((sLen - sepCnt) * 6 >> 3) - pad;
        final byte[] dArr = new byte[len];
        int s = 0;
        int d = 0;
        while (d < len) {
            int k = 0;
            for (int l = 0; l < 4; ++l) {
                final int c = Base64.IA[str.charAt(s++)];
                if (c >= 0) {
                    k |= c << 18 - l * 6;
                }
                else {
                    --l;
                }
            }
            dArr[d++] = (byte)(k >> 16);
            if (d < len) {
                dArr[d++] = (byte)(k >> 8);
                if (d >= len) {
                    continue;
                }
                dArr[d++] = (byte)k;
            }
        }
        return dArr;
    }
    
    public static final byte[] decodeFast(final String s) {
        final int sLen = s.length();
        if (sLen == 0) {
            return new byte[0];
        }
        int sIx;
        int eIx;
        for (sIx = 0, eIx = sLen - 1; sIx < eIx; ++sIx) {
            if (Base64.IA[s.charAt(sIx) & '\u00ff'] >= 0) {
                break;
            }
        }
        while (eIx > 0 && Base64.IA[s.charAt(eIx) & '\u00ff'] < 0) {
            --eIx;
        }
        final int pad = (s.charAt(eIx) == '=') ? ((s.charAt(eIx - 1) == '=') ? 2 : 1) : 0;
        final int cCnt = eIx - sIx + 1;
        final int sepCnt = (sLen > 76) ? (((s.charAt(76) == '\r') ? (cCnt / 78) : 0) << 1) : 0;
        final int len = ((cCnt - sepCnt) * 6 >> 3) - pad;
        final byte[] dArr = new byte[len];
        int d = 0;
        int cc = 0;
        final int eLen = len / 3 * 3;
        while (d < eLen) {
            final int i = Base64.IA[s.charAt(sIx++)] << 18 | Base64.IA[s.charAt(sIx++)] << 12 | Base64.IA[s.charAt(sIx++)] << 6 | Base64.IA[s.charAt(sIx++)];
            dArr[d++] = (byte)(i >> 16);
            dArr[d++] = (byte)(i >> 8);
            dArr[d++] = (byte)i;
            if (sepCnt > 0 && ++cc == 19) {
                sIx += 2;
                cc = 0;
            }
        }
        if (d < len) {
            int j = 0;
            for (int k = 0; sIx <= eIx - pad; j |= Base64.IA[s.charAt(sIx++)] << 18 - k * 6, ++k) {}
            for (int r = 16; d < len; dArr[d++] = (byte)(j >> r), r -= 8) {}
        }
        return dArr;
    }
    
    public static void main(final String[] args) {
    }
}
