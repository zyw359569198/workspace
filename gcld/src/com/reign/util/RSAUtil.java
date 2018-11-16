package com.reign.util;

import javax.crypto.*;
import com.sun.org.apache.xml.internal.security.utils.*;
import java.security.*;
import java.security.interfaces.*;
import java.security.spec.*;
import java.io.*;
import java.net.*;

public class RSAUtil
{
    private static RSAUtil rsa;
    private static char[] HEXCHAR;
    
    static {
        RSAUtil.rsa = new RSAUtil();
        RSAUtil.HEXCHAR = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
    
    public String encrypt(final String message, final Key key) throws Exception {
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(1, key);
        final byte[] encryptBytes = cipher.doFinal(message.getBytes());
        return Base64.encode(encryptBytes);
    }
    
    public String decrypt(final String message, final Key key) throws Exception {
        final Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(2, key);
        final byte[] decryptBytes = cipher.doFinal(Base64.decode(message));
        return new String(decryptBytes);
    }
    
    public byte[] sign(final String message, final PrivateKey key) throws Exception {
        final Signature signetcheck = Signature.getInstance("MD5withRSA");
        signetcheck.initSign(key);
        signetcheck.update(message.getBytes("ISO-8859-1"));
        return signetcheck.sign();
    }
    
    public boolean verifySign(final String message, final String signStr, final PublicKey key) throws Exception {
        if (message == null || signStr == null || key == null) {
            return false;
        }
        final Signature signetcheck = Signature.getInstance("MD5withRSA");
        signetcheck.initVerify(key);
        signetcheck.update(message.getBytes("ISO-8859-1"));
        return signetcheck.verify(toBytes(signStr));
    }
    
    public static RSAPublicKey initPublicKey(final String keyFilePath) {
        try {
            final BufferedReader br = new BufferedReader(new FileReader(keyFilePath));
            String s = br.readLine();
            final StringBuffer publicBuff = new StringBuffer();
            for (s = br.readLine(); s.charAt(0) != '-'; s = br.readLine()) {
                publicBuff.append(String.valueOf(s) + "\r");
            }
            final byte[] keybyte = Base64.decode(publicBuff.toString());
            final KeyFactory kf = KeyFactory.getInstance("RSA");
            final X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keybyte);
            final RSAPublicKey publicKey = (RSAPublicKey)kf.generatePublic(keySpec);
            return publicKey;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static RSAPrivateKey initPrivateKey(final String keyFilePath) {
        try {
            final BufferedReader br = new BufferedReader(new FileReader(keyFilePath));
            String s = br.readLine();
            final StringBuffer privateBuff = new StringBuffer();
            for (s = br.readLine(); s.charAt(0) != '-'; s = br.readLine()) {
                privateBuff.append(String.valueOf(s) + "\r");
            }
            final byte[] keybyte = Base64.decode(privateBuff.toString());
            final KeyFactory kf = KeyFactory.getInstance("RSA");
            final PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keybyte);
            final RSAPrivateKey privateKey = (RSAPrivateKey)kf.generatePrivate(keySpec);
            return privateKey;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static String toHexString(final byte[] b) {
        final StringBuilder sb = new StringBuilder(b.length * 2);
        for (int i = 0; i < b.length; ++i) {
            sb.append(RSAUtil.HEXCHAR[(b[i] & 0xF0) >>> 4]);
            sb.append(RSAUtil.HEXCHAR[b[i] & 0xF]);
        }
        return sb.toString();
    }
    
    public static final byte[] toBytes(final String s) {
        final byte[] bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte)Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
    
    public static String encrypt(final int gameid, final String plainText) {
        String encryptText = "";
        final String privateKeyFilePath = RSAUtil.rsa.getKeyFilePath(gameid, false);
        try {
            if (RSAUtil.rsa.isExistKeyFile(privateKeyFilePath)) {
                final RSAPrivateKey privateKey = initPrivateKey(privateKeyFilePath);
                encryptText = RSAUtil.rsa.encrypt(plainText, privateKey);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return encryptText;
    }
    
    public static String decrypt(final int gameid, final String encryptText) {
        String plainText = "";
        try {
            final String publicKeyFilePath = RSAUtil.rsa.getKeyFilePath(gameid, true);
            if (RSAUtil.rsa.isExistKeyFile(publicKeyFilePath)) {
                final RSAPublicKey publickKey = initPublicKey(publicKeyFilePath);
                plainText = RSAUtil.rsa.decrypt(encryptText, publickKey);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return plainText;
    }
    
    private boolean isExistKeyFile(final String filePath) {
        final File file = new File(filePath);
        return file.exists();
    }
    
    private String getKeyFilePath(final int gameid, final boolean isPublicKey) {
        if (isPublicKey) {
            return String.valueOf(this.getClass().getResource("/").getPath()) + "apps/" + gameid + "_SignKey.pub";
        }
        return String.valueOf(this.getClass().getResource("/").getPath()) + "apps/" + gameid + ".pri";
    }
    
    public static void main(final String[] args) throws Exception {
        final long a = 123L;
        final String b = "123";
        System.out.println("b.equals(a):" + b.equals(new StringBuilder(String.valueOf(a)).toString()));
        final String gameId = "100096";
        final String sign = "xgli4nirQD4Q2kHi4ZApR8XA94gzka0SErG4RiWqx14XttRu%252525252525252BdxLluvELBhLIIorSYnlpGyGwubu%252525252525250D%252525252525250A%252525252525252B%252525252525252B6XudWnLyvgkWsB%252525252525252Fc%252525252525252B1glRtz%252525252525252FHt06yqfru20%252525252525252FTl60afs0x92s5mcsadsAn4jzMitw8rYwdnHA8U%252525252525250D%252525252525250A%252525252525252FoMmcoEIaqSgsraQIQ5BykK2gHcbk0R1IKJJBgDvkIEyfHfHTYirll%252525252525252BZxRn0ZZCz301N4XpZKGa%252525252525252B%252525252525250D%252525252525250AQ1L%252525252525252B3E8veTIBDRJHxo8y5%252525252525252Bjf4tuEJuBQC7aPPgj7OZ6wOJa97yBGtQhHA8ejIhQWFz7g5uKfTUW%252525252525252F%252525252525250D%252525252525250AacI2qQPvV%252525252525252BVyJ36O3aQXUM0U%252525252525252Fe5vbs3QqyecRQ%252525252525253D%252525252525253D";
        System.out.println(sign.length());
        System.out.println(sign.replace(" ", "").length());
        final String echo = decrypt(Integer.parseInt(gameId), sign.replace(" ", ""));
        System.out.println("*********echo:" + echo);
        final String src = "abc123";
        final String dst = Base64.encode(src.getBytes());
        System.out.println(dst);
        final byte[] src2 = Base64.decode(dst);
        System.out.println(new String(src2));
        final String kk = URLEncoder.encode("+-%", "utf-8");
        System.out.println(kk);
    }
}
