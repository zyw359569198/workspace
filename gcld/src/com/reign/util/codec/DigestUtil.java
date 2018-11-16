package com.reign.util.codec;

import java.security.*;
import java.io.*;

class DigestUtil
{
    static byte[] digest(final String input, final String algorithm) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest digest = MessageDigest.getInstance(algorithm);
        final byte[] srcBytes = input.getBytes("utf-8");
        digest.update(srcBytes);
        final byte[] resultBytes = digest.digest();
        return resultBytes;
    }
    
    static byte[] digest(final String input, final String algorithm, final String charsetName) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        final MessageDigest digest = MessageDigest.getInstance(algorithm);
        final byte[] srcBytes = input.getBytes(charsetName);
        digest.update(srcBytes);
        final byte[] resultBytes = digest.digest();
        return resultBytes;
    }
}
