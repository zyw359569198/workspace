package com.reign.plugin.yx.util;

import sun.misc.*;
import javax.crypto.spec.*;
import javax.crypto.*;
import java.security.*;

public abstract class Coder
{
    public static final String KEY_SHA = "SHA";
    public static final String KEY_MD5 = "MD5";
    public static final String KEY_MAC = "HmacMD5";
    
    public static byte[] decryptBASE64(final String key) throws Exception {
        return new BASE64Decoder().decodeBuffer(key);
    }
    
    public static String encryptBASE64(final byte[] key) throws Exception {
        return new BASE64Encoder().encodeBuffer(key);
    }
    
    public static byte[] encryptMD5(final byte[] data) throws Exception {
        final MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data);
        return md5.digest();
    }
    
    public static byte[] encryptSHA(final byte[] data) throws Exception {
        final MessageDigest sha = MessageDigest.getInstance("SHA");
        sha.update(data);
        return sha.digest();
    }
    
    public static String initMacKey() throws Exception {
        final KeyGenerator keyGenerator = KeyGenerator.getInstance("HmacMD5");
        final SecretKey secretKey = keyGenerator.generateKey();
        return encryptBASE64(secretKey.getEncoded());
    }
    
    public static byte[] encryptHMAC(final byte[] data, final String key) throws Exception {
        final SecretKey secretKey = new SecretKeySpec(decryptBASE64(key), "HmacMD5");
        final Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        return mac.doFinal(data);
    }
}
