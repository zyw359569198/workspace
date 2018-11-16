package com.reign.util;

import java.net.*;

public class UUIDHexGenerator
{
    private String sep;
    private static final int IP;
    private static short counter;
    private static final int JVM;
    private static UUIDHexGenerator uuidgen;
    
    static {
        UUIDHexGenerator.counter = 0;
        JVM = (int)(System.currentTimeMillis() >>> 8);
        UUIDHexGenerator.uuidgen = new UUIDHexGenerator();
        int ipadd;
        try {
            ipadd = toInt(InetAddress.getLocalHost().getAddress());
        }
        catch (Exception e) {
            ipadd = 0;
        }
        IP = ipadd;
    }
    
    public UUIDHexGenerator() {
        this.sep = "";
    }
    
    public static UUIDHexGenerator getInstance() {
        return UUIDHexGenerator.uuidgen;
    }
    
    public static int toInt(final byte[] bytes) {
        int result = 0;
        for (int i = 0; i < 4; ++i) {
            result = (result << 8) + 128 + bytes[i];
        }
        return result;
    }
    
    protected String format(final int intval) {
        final String formatted = Integer.toHexString(intval);
        final StringBuffer buf = new StringBuffer("00000000");
        buf.replace(8 - formatted.length(), 8, formatted);
        return buf.toString();
    }
    
    protected String format(final short shortval) {
        final String formatted = Integer.toHexString(shortval);
        final StringBuffer buf = new StringBuffer("0000");
        buf.replace(4 - formatted.length(), 4, formatted);
        return buf.toString();
    }
    
    protected int getJVM() {
        return UUIDHexGenerator.JVM;
    }
    
    protected synchronized short getCount() {
        if (UUIDHexGenerator.counter < 0) {
            UUIDHexGenerator.counter = 0;
        }
        final short counter = UUIDHexGenerator.counter;
        UUIDHexGenerator.counter = (short)(counter + 1);
        return counter;
    }
    
    protected int getIP() {
        return UUIDHexGenerator.IP;
    }
    
    protected short getHiTime() {
        return (short)(System.currentTimeMillis() >>> 32);
    }
    
    protected int getLoTime() {
        return (int)System.currentTimeMillis();
    }
    
    public String generate() {
        return new StringBuffer(36).append(this.format(this.getIP())).append(this.sep).append(this.format(this.getJVM())).append(this.sep).append(this.format(this.getHiTime())).append(this.sep).append(this.format(this.getLoTime())).append(this.sep).append(this.format(this.getCount())).toString();
    }
    
    public static void main(final String[] str) {
        final UUIDHexGenerator id = new UUIDHexGenerator();
        for (int i = 0; i <= 100; ++i) {
            System.out.println(id.generate());
        }
    }
}
