package com.reign.plugin.yx.util.kingnet.udplog;

import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.security.*;

public class MaintainBuffHelper
{
    private Map<String, Integer> uidMapping;
    private static MaintainBuffHelper mInstance;
    
    static {
        MaintainBuffHelper.mInstance = new MaintainBuffHelper();
    }
    
    public MaintainBuffHelper() {
        this.uidMapping = new ConcurrentHashMap<String, Integer>();
    }
    
    public static MaintainBuffHelper getInstance() {
        return MaintainBuffHelper.mInstance;
    }
    
    public void encodeString(final UdpByteBuffer buf, final String content) throws UnsupportedEncodingException {
        final byte[] contentByte = content.getBytes("UTF-8");
        buf.writeShort(contentByte.length + 1);
        buf.writeBytes(contentByte);
        buf.writeByte(0);
    }
    
    public int uidToInt(String uid) {
        if (this.uidMapping.containsKey(uid)) {
            return this.uidMapping.get(uid);
        }
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            final byte[] buffer = messageDigest.digest(uid.getBytes());
            final StringBuffer sb = new StringBuffer(buffer.length * 2);
            for (int i = 0; i < buffer.length; ++i) {
                sb.append(Character.forDigit((buffer[i] & 0xF0) >> 4, 16));
                sb.append(Character.forDigit(buffer[i] & 0xF, 16));
            }
            uid = sb.toString().substring(0, 2);
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        final int result = Integer.valueOf(uid, 16) + 1;
        this.uidMapping.put(uid, result);
        return (result > 0) ? result : (-result);
    }
}
