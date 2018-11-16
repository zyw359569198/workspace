package com.reign.plugin.yx.util;

import java.security.*;
import java.util.*;
import java.net.*;
import java.io.*;

public class AESUtil
{
    public static Object makeKey(final byte[] keyBytes) throws AESException {
        try {
            return AESAlgorithm.makeKey(keyBytes);
        }
        catch (InvalidKeyException e) {
            throw new AESException(e);
        }
    }
    
    public static byte[] encrypt(final byte[] in, final Object key) throws AESException {
        return encrypt(in, key, 0);
    }
    
    public static byte[] encrypt(final byte[] in, final Object key, final int offset) throws AESException {
        try {
            final int length = in.length - offset;
            final int blocks = length / 16;
            final int left = length % 16;
            final byte[] result = new byte[16 * (blocks + ((left != 0) ? 1 : 0)) + 4];
            int cur = 4;
            for (int i = 0; i < blocks; ++i) {
                final byte[] encodedData = AESAlgorithm.blockEncrypt(in, offset + i * 16, key);
                for (int k = 0; k < 16; ++k) {
                    result[cur++] = encodedData[k];
                }
            }
            if (left != 0) {
                final byte[] tmp = new byte[16];
                for (int j = 0; j < left; ++j) {
                    tmp[j] = in[blocks * 16 + j + offset];
                }
                for (int j = left; j < 16; ++j) {
                    tmp[j] = 0;
                }
                final byte[] encodedData = AESAlgorithm.blockEncrypt(tmp, 0, key);
                for (int k = 0; k < 16; ++k) {
                    result[cur++] = encodedData[k];
                }
            }
            final byte[] lengthBytes = CommonUtil.intToByteArray(cur - 4);
            result[0] = lengthBytes[0];
            result[1] = lengthBytes[1];
            result[2] = lengthBytes[2];
            result[3] = lengthBytes[3];
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new AESException(e);
        }
    }
    
    public static byte[] decrypt(final byte[] in, final Object key) throws AESException {
        return decrypt(in, key, 0);
    }
    
    public static byte[] decrypt(final byte[] in, final Object key, final int offset) throws AESException {
        try {
            final int len = CommonUtil.byteArrayToInt(in, offset);
            final int blocks = len / 16;
            final ArrayList<byte[]> list = new ArrayList<byte[]>(blocks);
            int size = 0;
            for (int i = 0; i < blocks; ++i) {
                final byte[] decodedData = AESAlgorithm.blockDecrypt(in, offset + i * 16 + 4, key);
                list.add(decodedData);
                size += decodedData.length;
            }
            final byte[] ret = new byte[size];
            int index = 0;
            for (int j = 0; j < list.size(); ++j) {
                final byte[] d = list.get(j);
                for (int k = 0; k < d.length && index != size; ++k, ++index) {
                    ret[index] = d[k];
                }
            }
            return ret;
        }
        catch (Exception e) {
            throw new AESException(e);
        }
    }
    
    public static byte[] encryptNoLen(final byte[] in, final Object key) throws AESException {
        return encryptNoLen(in, key, 0);
    }
    
    public static byte[] encryptNoLen(final byte[] in, final Object key, final int offset) throws AESException {
        try {
            final int length = in.length - offset;
            final int blocks = length / 16;
            final int left = length % 16;
            final byte[] result = new byte[16 * (blocks + ((left != 0) ? 1 : 0))];
            int cur = 0;
            for (int i = 0; i < blocks; ++i) {
                final byte[] encodedData = AESAlgorithm.blockEncrypt(in, offset + i * 16, key);
                for (int k = 0; k < 16; ++k) {
                    result[cur++] = encodedData[k];
                }
            }
            if (left != 0) {
                final byte[] tmp = new byte[16];
                for (int j = 0; j < left; ++j) {
                    tmp[j] = in[blocks * 16 + j + offset];
                }
                for (int j = left; j < 16; ++j) {
                    tmp[j] = 0;
                }
                final byte[] encodedData = AESAlgorithm.blockEncrypt(tmp, 0, key);
                for (int k = 0; k < 16; ++k) {
                    result[cur++] = encodedData[k];
                }
            }
            return result;
        }
        catch (Exception e) {
            throw new AESException(e);
        }
    }
    
    public static byte[] decryptNoLen(final byte[] in, final Object key) throws AESException {
        return decryptNoLen(in, key, 0);
    }
    
    public static byte[] decryptNoLen(final byte[] in, final Object key, final int offset) throws AESException {
        try {
            final int len = in.length;
            final int blocks = len / 16;
            final ArrayList<byte[]> list = new ArrayList<byte[]>(blocks);
            int size = 0;
            for (int i = 0; i < blocks; ++i) {
                final byte[] decodedData = AESAlgorithm.blockDecrypt(in, offset + i * 16, key);
                list.add(decodedData);
                size += decodedData.length;
            }
            final byte[] ret = new byte[size];
            int index = 0;
            for (int j = 0; j < list.size(); ++j) {
                final byte[] d = list.get(j);
                for (int k = 0; k < d.length && index != size; ++k, ++index) {
                    ret[index] = d[k];
                }
            }
            return ret;
        }
        catch (Exception e) {
            throw new AESException(e);
        }
    }
    
    public static void main(final String[] args) throws AESException, UnknownHostException, IOException {
    }
}
