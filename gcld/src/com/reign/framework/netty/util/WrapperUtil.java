package com.reign.framework.netty.util;

import java.util.*;
import java.util.zip.*;
import java.io.*;
import org.jboss.netty.buffer.*;

public final class WrapperUtil
{
    public static boolean compress;
    public static final byte[] CRLF;
    public static final byte[] EMPTY_BYTE;
    
    static {
        CRLF = "\r\n".getBytes();
        EMPTY_BYTE = new byte[0];
    }
    
    public static ChannelBuffer wrapper(final String command, final int requestId, final byte[] body) {
        byte[] commandBytes = command.getBytes();
        commandBytes = Arrays.copyOf(commandBytes, 32);
        byte[] bodyBytes = body;
        if (WrapperUtil.compress) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final DeflaterOutputStream dis = new DeflaterOutputStream(out);
            try {
                dis.write(body);
                dis.finish();
                dis.close();
                bodyBytes = out.toByteArray();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (dis != null) {
                    try {
                        dis.close();
                    }
                    catch (IOException ex) {}
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                }
                catch (IOException ex2) {}
            }
        }
        final int dataLen = 36 + bodyBytes.length;
        final ChannelBuffer buffer = ChannelBuffers.buffer(dataLen + 4);
        buffer.writeInt(dataLen);
        buffer.writeBytes(commandBytes);
        buffer.writeInt(requestId);
        buffer.writeBytes(bodyBytes);
        return buffer;
    }
    
    public static ChannelBuffer wrapper(final String command, final int requestId, final byte[] body, final boolean compress) throws IOException {
        byte[] commandBytes = command.getBytes();
        commandBytes = Arrays.copyOf(commandBytes, 32);
        byte[] bodyBytes = body;
        if (compress) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final DeflaterOutputStream dis = new DeflaterOutputStream(out);
            dis.write(body);
            dis.finish();
            dis.close();
            bodyBytes = out.toByteArray();
        }
        final int dataLen = 36 + bodyBytes.length;
        final ChannelBuffer buffer = ChannelBuffers.buffer(dataLen + 4);
        buffer.writeInt(dataLen);
        buffer.writeBytes(commandBytes);
        buffer.writeInt(requestId);
        buffer.writeBytes(bodyBytes);
        return buffer;
    }
    
    public static ChannelBuffer wrapper(final byte[] bytes) {
        final ChannelBuffer buffer = ChannelBuffers.wrappedBuffer(bytes);
        return buffer;
    }
    
    public static ChannelBuffer wrapperString(final byte[] bytes) {
        final ChannelBuffer buffer = ChannelBuffers.buffer(bytes.length + 4);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
        return buffer;
    }
    
    public static byte[] wrapperBody(final byte[] bytes) {
        return _wrapperBody(bytes, WrapperUtil.compress);
    }
    
    public static byte[] wrapperBody(final byte[] bytes, final boolean compress) {
        return _wrapperBody(bytes, compress);
    }
    
    private static byte[] _wrapperBody(final byte[] bytes, final boolean compress) {
        byte[] bodyBytes = bytes;
        if (compress) {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final DeflaterOutputStream dis = new DeflaterOutputStream(out);
            try {
                dis.write(bodyBytes);
                dis.finish();
                dis.close();
                bodyBytes = out.toByteArray();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (dis != null) {
                    try {
                        dis.close();
                    }
                    catch (IOException ex) {}
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                }
                catch (IOException ex2) {}
            }
        }
        return bodyBytes;
    }
    
    public static String getContentType() {
        if (WrapperUtil.compress) {
            return "application/x-gzip-compressed";
        }
        return "application/json";
    }
    
    public static ChannelBuffer wrapperChunk(final byte[] chunk) {
        final int len = chunk.length + 4;
        final String lenHex = Integer.toHexString(len);
        final byte[] lenBytes = lenHex.getBytes();
        final ChannelBuffer buffer = ChannelBuffers.buffer(len + lenBytes.length + WrapperUtil.CRLF.length << 1);
        buffer.writeBytes(lenHex.getBytes());
        buffer.writeBytes(WrapperUtil.CRLF);
        buffer.writeInt(chunk.length);
        buffer.writeBytes(chunk);
        buffer.writeBytes(WrapperUtil.CRLF);
        return buffer;
    }
}
