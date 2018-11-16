package com.reign.util.codec;

import java.security.spec.*;
import javax.crypto.*;
import java.security.interfaces.*;
import com.sun.crypto.provider.*;
import java.security.*;
import javax.crypto.spec.*;

public final class CodecUtil
{
    public static String md2(final String str) {
        try {
            final byte[] bytes = DigestUtil.digest(str, "MD2");
            return HexUtil.encodeHexString(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException("md5 encrypt error", e);
        }
    }
    
    public static String md5(final String str) {
        try {
            final byte[] bytes = DigestUtil.digest(str, "MD5");
            return HexUtil.encodeHexString(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException("md5 encrypt error", e);
        }
    }
    
    public static String md5(final String str, final String charsetName) {
        try {
            final byte[] bytes = DigestUtil.digest(str, "MD5", charsetName);
            return HexUtil.encodeHexString(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException("md5 encrypt error", e);
        }
    }
    
    public static String sha1(final String str) {
        try {
            final byte[] bytes = DigestUtil.digest(str, "SHA-1");
            return HexUtil.encodeHexString(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException("sha encrypt error", e);
        }
    }
    
    public static String sha256(final String str) {
        try {
            final byte[] bytes = DigestUtil.digest(str, "SHA-256");
            return HexUtil.encodeHexString(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException("sha encrypt error", e);
        }
    }
    
    public static String sha384(final String str) {
        try {
            final byte[] bytes = DigestUtil.digest(str, "SHA-384");
            return HexUtil.encodeHexString(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException("sha encrypt error", e);
        }
    }
    
    public static String sha512(final String str) {
        try {
            final byte[] bytes = DigestUtil.digest(str, "SHA-512");
            return HexUtil.encodeHexString(bytes);
        }
        catch (Exception e) {
            throw new RuntimeException("sha encrypt error", e);
        }
    }
    
    public static byte[] desEncrypt(final String desKey, final String data) {
        return encrypt(desKey, 8, "DES", data);
    }
    
    public static String desEncryptHex(final String desKey, final String data) {
        return HexUtil.encodeHexString(encrypt(desKey, 8, "DES", data));
    }
    
    public static byte[] desDecrypt(final String desKey, final byte[] data) {
        return decrypt(desKey, 8, "DES", data);
    }
    
    public static byte[] desDecryptHex(final String desKey, final String data) {
        return decrypt(desKey, 8, "DES", HexUtil.decodeHex(data.toCharArray()));
    }
    
    public static byte[] des3Encrypt(final String desKey, final String data) {
        return encrypt(desKey, 24, "DESede", data);
    }
    
    public static String des3EncryptHex(final String desKey, final String data) {
        return HexUtil.encodeHexString(encrypt(desKey, 24, "DESede", data));
    }
    
    public static byte[] des3Decrypt(final String desKey, final byte[] data) {
        return decrypt(desKey, 24, "DESede", data);
    }
    
    public static byte[] des3DecryptHex(final String desKey, final String data) {
        return decrypt(desKey, 24, "DESede", HexUtil.decodeHex(data.toCharArray()));
    }
    
    public static byte[] aesEncrypt(final String aesKey, final String data) {
        return encrypt(aesKey, 16, "AES", data);
    }
    
    public static String aesEncryptHex(final String aesKey, final String data) {
        return HexUtil.encodeHexString(encrypt(aesKey, 16, "AES", data));
    }
    
    public static byte[] aesDecrypt(final String aesKey, final byte[] data) {
        return decrypt(aesKey, 16, "AES", data);
    }
    
    public static byte[] aesDecryptHex(final String aesKey, final String data) {
        return decrypt(aesKey, 16, "AES", HexUtil.decodeHex(data.toCharArray()));
    }
    
    public static PrivateKey getPrivateKey(final String key) throws Exception {
        try {
            final byte[] keyBytes = Base64.decode(key);
            final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            final PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
        }
        catch (Exception e) {
            throw new RuntimeException("get privateKey error", e);
        }
    }
    
    public static PublicKey getPublicKey(final String key) throws Exception {
        try {
            final byte[] keyBytes = Base64.decode(key);
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            final PublicKey publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        }
        catch (Exception e) {
            throw new RuntimeException("get publicKey error", e);
        }
    }
    
    public static byte[] rsaEncryptByPublicKey(final RSAPublicKey publicKey, final String data) {
        if (publicKey != null) {
            try {
                final Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(1, publicKey);
                final byte[] resultBytes = cipher.doFinal(data.getBytes("utf-8"));
                return resultBytes;
            }
            catch (Exception e) {
                throw new RuntimeException("rsa encrypt error", e);
            }
        }
        throw new RuntimeException("key can't be null");
    }
    
    public static String rsaEncryptByPublicKeyHex(final RSAPublicKey publicKey, final String data) {
        return HexUtil.encodeHexString(rsaEncryptByPublicKey(publicKey, data));
    }
    
    public static byte[] rsaEncryptByPrivateKey(final RSAPrivateKey privateKey, final String data) {
        if (privateKey != null) {
            try {
                final Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(1, privateKey);
                final byte[] resultBytes = cipher.doFinal(data.getBytes("utf-8"));
                return resultBytes;
            }
            catch (Exception e) {
                throw new RuntimeException("rsa encrypt error", e);
            }
        }
        throw new RuntimeException("key can't be null");
    }
    
    public static String rsaEncryptByPrivateKeyHex(final RSAPrivateKey privateKey, final String data) {
        return HexUtil.encodeHexString(rsaEncryptByPrivateKey(privateKey, data));
    }
    
    public static byte[] rsaDecryptByPublicKey(final PublicKey publicKey, final byte[] data) {
        if (publicKey != null) {
            try {
                final Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(2, publicKey);
                final byte[] resultBytes = cipher.doFinal(data);
                return resultBytes;
            }
            catch (Exception e) {
                throw new RuntimeException("rsa decrypt error", e);
            }
        }
        throw new RuntimeException("key can't be null");
    }
    
    public static byte[] rsaDecryptByPublicKeyHex(final PublicKey publicKey, final String data) {
        return rsaDecryptByPublicKey(publicKey, HexUtil.decodeHex(data.toCharArray()));
    }
    
    public static byte[] rsaDecryptByPrivateKey(final RSAPrivateKey privateKey, final byte[] data) {
        if (privateKey != null) {
            try {
                final Cipher cipher = Cipher.getInstance("RSA");
                cipher.init(2, privateKey);
                final byte[] resultBytes = cipher.doFinal(data);
                return resultBytes;
            }
            catch (Exception e) {
                throw new RuntimeException("rsa decrypt error", e);
            }
        }
        throw new RuntimeException("key can't be null");
    }
    
    public static byte[] rsaDecryptByPrivateKeyHex(final RSAPrivateKey privateKey, final String data) {
        return rsaDecryptByPrivateKey(privateKey, HexUtil.decodeHex(data.toCharArray()));
    }
    
    protected static byte[] encrypt(final String keyStr, final int keyLen, final String algorithm, final String data) {
        try {
            Security.addProvider(new SunJCE());
            final Key key = getKey(keyStr.getBytes(), algorithm, keyLen);
            final Cipher encryptCipher = Cipher.getInstance(algorithm);
            encryptCipher.init(1, key);
            return encryptCipher.doFinal(data.getBytes("utf-8"));
        }
        catch (Exception e) {
            throw new RuntimeException("encrypt error", e);
        }
    }
    
    protected static byte[] decrypt(final String keyStr, final int keyLen, final String algorithm, final byte[] data) {
        try {
            Security.addProvider(new SunJCE());
            final Key key = getKey(keyStr.getBytes(), algorithm, keyLen);
            final Cipher encryptCipher = Cipher.getInstance(algorithm);
            encryptCipher.init(2, key);
            return encryptCipher.doFinal(data);
        }
        catch (Exception e) {
            throw new RuntimeException("encrypt error", e);
        }
    }
    
    protected static Key getKey(final byte[] keysData, final String algorithm, final int len) throws Exception {
        final byte[] keys = new byte[len];
        for (int i = 0; i < keysData.length && i < keys.length; ++i) {
            keys[i] = keysData[i];
        }
        final Key key = new SecretKeySpec(keys, algorithm);
        return key;
    }
    
    public static void main(final String[] args) throws Exception {
        final String msg = "\u90ed\u5fb7\u7eb2-\u7cbe\u54c1\u76f8\u58f0\u6280\u672f12zsd\u5ba3\u4f20\u53d1\u6539\u59d4";
        final String result = new String(des3EncryptHex("abcdef", msg));
        final String result2 = new String(des3DecryptHex("abcdef", result));
        System.out.println("\u660e\u6587\u662f\uff1a" + msg);
        System.out.println("\u5bc6\u6587\u662f\uff1a" + result + ", " + result.length());
        System.out.println("\u5bc6\u6587\u662f\uff1a" + result2 + ", " + result2.length());
        final PublicKey publicKey = getPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAs/nB3qN/mddivHPO1A2GWCMTtxgb9fXwiopbfzEOPgh2mIqqBThSgcPAL8cpF4457hmNpkiE7+aWzErip/6idbzZOeVVqD+O6ix/WLiSiAABOkKtmp539qqq0tqirKJhzSp3x4G6j47ERzOMjrssUj9rgV5U4nfowebAMo9M6itWPowYRe0OYDS6577FVUJH/f0n/EdHpRlVlx5jy29dN2rp+7eKEFg8dXPLH/6STKqRJDuMnDgtZTEtTW5D2il99WSHuD0mN+wZ0paDRJVmf7bPu9L+c3FyQtWIFA9RaBfv+7WZHN+Lm+OUsDchtvlOYBcuvYiRay1660KQBIdt9wIDAQAB");
        final String data = "lpi5Uloh9oXSZ1M9xAk+3+DVHX+xrvNR4h7szCxQX4WTlXnr/1oR3F813ptsxqecnbXToJuw8YIUtBOh+lMDfLT+lXGFBqFf7hWyllCz0tSLZBWwri2CQCDy/qamEujZ7B1Yw1CdybyASqPC907sBS9bSlydbnQmTW0MMcXaauzDoxVoyKItKfgrtKtSNizAYMv58LJZNoJfdjM3S5g+rZo1NkXx/7/eqkBL9ZWIBvV5QWlT3Kg5AYa1DnHpY+jct1hLxxMmW68L0ijXQpfonckEcx4TPwCdBmcUfwusRrcKLJ+mLRWmw/ICzCwTpDVeIqGcpronx7dh0hkrRsb6pQ==";
        final byte[] bytes = rsaDecryptByPublicKey(publicKey, Base64.decode(data.getBytes()));
        System.out.println(new String(bytes));
    }
}
