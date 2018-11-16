package com.reign.kf.comm.util;

import java.security.*;

public class MD5SecurityUtil
{
    private static final char[] hexDigits;
    
    static {
        hexDigits = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
    
    private static String bytesToHex(final byte[] bytes) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 16; ++i) {
            int t = bytes[i];
            if (t < 0) {
                t += 256;
            }
            sb.append(MD5SecurityUtil.hexDigits[t >>> 4]);
            sb.append(MD5SecurityUtil.hexDigits[t % 16]);
        }
        return sb.toString();
    }
    
    public static String code(final String input) {
        byte[] bytes = null;
        MessageDigest md = null;
        try {
            bytes = input.getBytes("utf-8");
            md = MessageDigest.getInstance(System.getProperty("MD5.algorithm", "MD5"));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return bytesToHex(md.digest(bytes));
    }
}
