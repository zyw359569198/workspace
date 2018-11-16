package com.reign.plugin.yx.util;

import java.text.*;
import java.io.*;
import org.apache.commons.lang.math.*;
import java.util.*;
import java.lang.reflect.*;
import org.apache.commons.lang.*;

public class CommonUtil
{
    private static SimpleDateFormat format;
    private static SimpleDateFormat dateTimeFormat;
    private static final char[] hexDigit;
    
    static {
        CommonUtil.format = new SimpleDateFormat("yyyy-MM-dd");
        CommonUtil.dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        hexDigit = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
    }
    
    public static String byteArrayToHexString(final byte[] b) {
        final StringBuffer sb = new StringBuffer(b.length * 2);
        for (int i = 0; i < b.length; ++i) {
            final int v = b[i] & 0xFF;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase();
    }
    
    public static byte[] hexStringToByteArray(final String s) {
        final byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; ++i) {
            final int index = i * 2;
            final int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte)v;
        }
        return b;
    }
    
    public static void copyBytes(final byte[] fromBytes, final byte[] toBytes, final int fromOffset, final int toOffset, int length) {
        if (length + fromOffset > fromBytes.length) {
            length = fromBytes.length - fromOffset;
        }
        if (length + toOffset > toBytes.length) {
            length = toBytes.length - toOffset;
        }
        for (int i = 0; i < length; ++i) {
            toBytes[toOffset + i] = fromBytes[fromOffset + i];
        }
    }
    
    public static Date parseDate(final String dateStr) {
        try {
            return CommonUtil.format.parse(dateStr);
        }
        catch (ParseException e) {
            return null;
        }
    }
    
    public static String formatDate(final Date date) {
        if (date == null) {
            return null;
        }
        return CommonUtil.format.format(date);
    }
    
    public static int compareDate(final int year1, final int month1, final int date1, final int year2, final int month2, final int date2) {
        if (year1 < year2) {
            return -1;
        }
        if (year1 > year2) {
            return 1;
        }
        if (month1 < month2) {
            return -2;
        }
        if (month1 > month2) {
            return 2;
        }
        if (date1 < date2) {
            return -3;
        }
        if (date1 > date2) {
            return 3;
        }
        return 0;
    }
    
    public static synchronized int compareDate(final Date date1, final Date date2) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        final int year1 = calendar.get(1);
        final int month1 = calendar.get(2);
        final int d1 = calendar.get(5);
        calendar.setTime(date2);
        final int year2 = calendar.get(1);
        final int month2 = calendar.get(2);
        final int d2 = calendar.get(5);
        return compareDate(year1, month1, d1, year2, month2, d2);
    }
    
    public static String formatDateTime(final Date date) {
        return CommonUtil.dateTimeFormat.format(date);
    }
    
    public static byte[] intToByteArray(final int value) {
        final byte[] b = new byte[4];
        for (int i = 0; i < 4; ++i) {
            final int offset = (b.length - 1 - i) * 8;
            b[i] = (byte)(value >>> offset & 0xFF);
        }
        return b;
    }
    
    public static int byteArrayToInt(final byte[] b) {
        return byteArrayToInt(b, 0);
    }
    
    public static int byteArrayToInt(final byte[] b, final int offset) {
        int value = 0;
        for (int len = Math.min(b.length - offset, 4), i = 0; i < len; ++i) {
            final int shift = (len - 1 - i) * 8;
            value += (b[i + offset] & 0xFF) << shift;
        }
        return value;
    }
    
    public static byte[] longToByteArray(long i) {
        final byte[] buf = new byte[8];
        for (int j = 7; j >= 0; --j) {
            buf[j] = (byte)(i & 0xFFL);
            i >>>= 8;
        }
        return buf;
    }
    
    public static long byteArrayToLong(final byte[] buf) {
        return byteArrayToLong(buf, 0);
    }
    
    public static long byteArrayToLong(final byte[] buf, final int offset) {
        long i = 0L;
        if (buf.length - offset < 8) {
            throw new RuntimeException("Bad Length");
        }
        for (int j = offset; j < 7 + offset; ++j) {
            i |= (buf[j] & 0xFFL);
            i <<= 8;
        }
        i |= (buf[7 + offset] & 0xFFL);
        return i;
    }
    
    public static Date getStartPointOfThisWeek(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayofweek = c.get(7) - 1;
        if (dayofweek == 0) {
            dayofweek = 7;
        }
        c.add(5, 1 - dayofweek);
        c.set(11, 0);
        c.set(12, 0);
        c.set(13, 0);
        c.set(14, 0);
        return c.getTime();
    }
    
    public static Date getEndPointOfThisWeek(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayofweek = c.get(7) - 1;
        if (dayofweek == 0) {
            dayofweek = 7;
        }
        c.add(5, 7 - dayofweek);
        c.set(11, 23);
        c.set(12, 59);
        c.set(13, 59);
        c.set(14, 999);
        return c.getTime();
    }
    
    public static Date getStartPointOfThisMonth(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(5, 1);
        c.set(11, 0);
        c.set(12, 0);
        c.set(13, 0);
        c.set(14, 0);
        return c.getTime();
    }
    
    public static Date getEndPointOfThisMonth(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(2, 1);
        c.set(5, 1);
        c.add(5, -1);
        c.set(11, 23);
        c.set(12, 59);
        c.set(13, 59);
        c.set(14, 999);
        return c.getTime();
    }
    
    public static Date getStartPointOfPreviousWeek(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayofweek = c.get(7) - 1;
        if (dayofweek == 0) {
            dayofweek = 7;
        }
        c.add(5, 1 - dayofweek - 7);
        c.set(11, 0);
        c.set(12, 0);
        c.set(13, 0);
        c.set(14, 0);
        return c.getTime();
    }
    
    public static Date getEndPointOfPreviousWeek(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayofweek = c.get(7) - 1;
        if (dayofweek == 0) {
            dayofweek = 7;
        }
        c.add(5, 0 - dayofweek);
        c.set(11, 23);
        c.set(12, 59);
        c.set(13, 59);
        c.set(14, 999);
        return c.getTime();
    }
    
    public static Date getStartPointOfPreviousMonth(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(2, -1);
        c.set(5, 1);
        c.set(11, 0);
        c.set(12, 0);
        c.set(13, 0);
        c.set(14, 0);
        return c.getTime();
    }
    
    public static Date getEndPointOfPreviousMonth(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(5, 1);
        c.add(5, -1);
        c.set(11, 23);
        c.set(12, 59);
        c.set(13, 59);
        c.set(14, 999);
        return c.getTime();
    }
    
    public static Date getStartPointOfThisYear(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(2, 0);
        c.set(5, 1);
        c.set(11, 0);
        c.set(12, 0);
        c.set(13, 0);
        c.set(14, 0);
        return c.getTime();
    }
    
    public static Date getEndPointOfThisYear(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(1, 1);
        c.set(2, 0);
        c.set(5, 1);
        c.add(5, -1);
        c.set(11, 23);
        c.set(12, 59);
        c.set(13, 59);
        c.set(14, 999);
        return c.getTime();
    }
    
    public static long IP2LongValue(final String ipaddress) {
        final String[] splits = ipaddress.split("\\.");
        if (splits.length < 4) {
            return -1L;
        }
        long value = 0L;
        for (int i = 0; i < 4; ++i) {
            value = (value << 16) + Integer.parseInt(splits[i]);
        }
        return value;
    }
    
    public static long intIpToLongIp(final int intIP) {
        final int[] intArray = { intIP >>> 24, (intIP & 0xFFFFFF) >>> 16, (intIP & 0xFFFF) >>> 8, intIP & 0xFF };
        long value = 0L;
        for (int i = 0; i < 4; ++i) {
            value = (value << 16) + intArray[i];
        }
        return value;
    }
    
    public static int longIpToIntIp(final long longIP) {
        final int[] intArray = { (int)longIP >>> 48, (int)longIP >>> 32 & 0xFFFF, (int)longIP >>> 16 & 0xFFFF, (int)longIP & 0xFFFF };
        long value = 0L;
        for (int i = 0; i < 4; ++i) {
            value = (value << 16) + intArray[i];
        }
        return (intArray[0] << 24) + (intArray[1] << 16) + (intArray[2] << 8) + intArray[3];
    }
    
    public static int ipToInt(final String strIP) {
        final int[] ip = new int[4];
        final int position1 = strIP.indexOf(".");
        final int position2 = strIP.indexOf(".", position1 + 1);
        final int position3 = strIP.indexOf(".", position2 + 1);
        ip[0] = Integer.parseInt(strIP.substring(0, position1));
        ip[1] = Integer.parseInt(strIP.substring(position1 + 1, position2));
        ip[2] = Integer.parseInt(strIP.substring(position2 + 1, position3));
        ip[3] = Integer.parseInt(strIP.substring(position3 + 1));
        return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
    }
    
    public static String intToIP(final int intIP) {
        final StringBuffer sb = new StringBuffer("");
        sb.append(String.valueOf(intIP >>> 24));
        sb.append(".");
        sb.append(String.valueOf((intIP & 0xFFFFFF) >>> 16));
        sb.append(".");
        sb.append(String.valueOf((intIP & 0xFFFF) >>> 8));
        sb.append(".");
        sb.append(String.valueOf(intIP & 0xFF));
        return sb.toString();
    }
    
    public static String getSite(final String url) {
        final int index = url.indexOf("//");
        final int urlLength = url.length();
        int index2 = -1;
        for (int i = (index < 0) ? 0 : (index + 2); i < urlLength; ++i) {
            final char c = url.charAt(i);
            if (c == '/' || c == '?' || c == '&') {
                index2 = i;
                break;
            }
        }
        if (index2 < 0) {
            return url;
        }
        final String site = url.substring(0, index2);
        return site;
    }
    
    public static String getExplorer(final String userAgent) {
        if (userAgent.indexOf("NetCaptor") > 0) {}
        String explorer;
        if (userAgent.indexOf("Maxthon") > 0) {
            explorer = "Maxthon";
        }
        else if (userAgent.indexOf("MSIE 6") > 0) {
            explorer = "MSIE 6.x";
        }
        else if (userAgent.indexOf("MSIE 7") > 0) {
            explorer = "MSIE 7.x";
        }
        else if (userAgent.indexOf("MSIE 5") > 0) {
            explorer = "MSIE 5.x";
        }
        else if (userAgent.indexOf("Firefox") > 0) {
            explorer = "Firefox";
        }
        else if (userAgent.indexOf("NetCaptor") > 0) {
            explorer = "NetCaptor";
        }
        else if (userAgent.indexOf("MSIE 4") > 0) {
            explorer = "MSIE 4.x";
        }
        else if (userAgent.indexOf("MSIE") > 0) {
            explorer = "MSIE(Unknown Version)";
        }
        else if (userAgent.indexOf("Netscape") > 0) {
            explorer = "Netscape";
        }
        else if (userAgent.indexOf("Opera") > 0) {
            explorer = "Opera";
        }
        else {
            explorer = "Other";
        }
        return explorer;
    }
    
    public static String getOSInfo(final String userAgent) {
        String os;
        if (userAgent.indexOf("Windows NT 5.1") > 0) {
            os = "Windows XP";
        }
        else if (userAgent.indexOf("Windows NT 5.0") > 0) {
            os = "Windows 2000";
        }
        else if (userAgent.indexOf("Windows NT 5.2") > 0) {
            os = "Windows 2003";
        }
        else if (userAgent.indexOf("Windows NT 6.0") > 0) {
            os = "Windows Vista";
        }
        else if (userAgent.indexOf("Windows 98") > 0) {
            os = "Windows 98";
        }
        else if (userAgent.indexOf("Windows NT") > 0) {
            os = "Windows NT";
        }
        else if (userAgent.indexOf("Windows") > 0) {
            os = "Windows(Unknown Version)";
        }
        else if (userAgent.indexOf("unix") > 0) {
            os = "Unix";
        }
        else if (userAgent.indexOf("linux") > 0) {
            os = "Linux";
        }
        else if (userAgent.indexOf("Mac") > 0) {
            os = "Mac";
        }
        else {
            os = "Other";
        }
        return os;
    }
    
    public static String formatString(final String source, final int toLength, final char fill, final boolean fillRightSide) {
        if (source == null) {
            return null;
        }
        final byte[] bytes = source.getBytes();
        final int length = bytes.length;
        if (length == toLength) {
            return source;
        }
        if (length > toLength) {
            return new String(bytes, 0, toLength);
        }
        final StringBuffer buffer = new StringBuffer();
        if (fillRightSide) {
            buffer.append(source);
        }
        for (int i = 0; i < toLength - length; ++i) {
            buffer.append(fill);
        }
        if (!fillRightSide) {
            buffer.append(source);
        }
        return buffer.toString();
    }
    
    public static String formatString(final String source, final int toLength, final char fill, final String charset, final boolean fillRightSide) {
        if (source == null) {
            return null;
        }
        byte[] bytes = null;
        try {
            bytes = source.getBytes(charset);
        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final int length = bytes.length;
        if (length == toLength) {
            return source;
        }
        if (length > toLength) {
            return new String(bytes, 0, toLength);
        }
        final StringBuffer buffer = new StringBuffer();
        if (fillRightSide) {
            buffer.append(source);
        }
        for (int i = 0; i < toLength - length; ++i) {
            buffer.append(fill);
        }
        if (!fillRightSide) {
            buffer.append(source);
        }
        return buffer.toString();
    }
    
    public static String formatInt32(final int value) {
        return formatString(String.valueOf(value), 11, '0', false);
    }
    
    public static void main(final String[] args) {
        for (int i = 0; i < 100000; ++i) {
            final int i2 = RandomUtils.nextInt(256);
            final int i3 = RandomUtils.nextInt(256);
            final int i4 = RandomUtils.nextInt(256);
            final int i5 = RandomUtils.nextInt(256);
            final String ip = i2 + "." + i3 + "." + i4 + "." + i5;
            final int intip = ipToInt(ip);
            final String ip2 = intToIP(intip);
            System.out.println(ip);
            if (!ip.equals(ip2)) {
                System.out.println(ip);
                System.out.println(ip2);
            }
        }
    }
    
    public static byte[] getBytes(final String keyString) {
        if (keyString == null) {
            return null;
        }
        try {
            final byte[] key = new byte[16];
            final StringTokenizer tokenizer = new StringTokenizer(keyString, ",");
            for (int index = 0; index < key.length && tokenizer.hasMoreTokens(); key[index++] = Byte.valueOf(tokenizer.nextToken().trim())) {}
            return key;
        }
        catch (Exception e) {
            return null;
        }
    }
    
    public static void initialObject(final Object obj) throws Exception {
        final Class<?> objClazz = obj.getClass();
        final Field[] fields = objClazz.getDeclaredFields();
        Field[] array;
        for (int length = (array = fields).length, i = 0; i < length; ++i) {
            final Field field = array[i];
            final Class<?> type = field.getType();
            String fieldName = field.getName();
            fieldName = String.valueOf(fieldName.substring(0, 1).toUpperCase()) + fieldName.substring(1);
            final String getMethodName = "get" + fieldName;
            final String setMethodName = "set" + fieldName;
            Method getMethod = null;
            Method setMethod = null;
            try {
                getMethod = objClazz.getMethod(getMethodName, new Class[0]);
                setMethod = objClazz.getMethod(setMethodName, type);
            }
            catch (Exception e) {
                continue;
            }
            try {
                if (getMethod.invoke(obj, new Object[0]) == null) {
                    if (type.equals(String.class)) {
                        setMethod.invoke(obj, "");
                    }
                    else if (type.getSuperclass().equals(Number.class)) {
                        setMethod.invoke(obj, 0);
                    }
                }
            }
            catch (Exception e) {
                throw e;
            }
        }
    }
    
    public static String chineseToUnicode(final String str) {
        final boolean escapeSpace = false;
        final String result = "";
        if (StringUtils.isBlank(str)) {
            return result;
        }
        final int len = str.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        final StringBuffer outBuffer = new StringBuffer(bufLen);
        for (int x = 0; x < len; ++x) {
            final char aChar = str.charAt(x);
            if (aChar > '=' && aChar < '\u007f') {
                if (aChar == '\\') {
                    outBuffer.append('\\');
                    outBuffer.append('\\');
                }
                else {
                    outBuffer.append(aChar);
                }
            }
            else {
                switch (aChar) {
                    case ' ': {
                        if (x == 0 || escapeSpace) {
                            outBuffer.append('\\');
                        }
                        outBuffer.append(' ');
                        break;
                    }
                    case '\t': {
                        outBuffer.append('\\');
                        outBuffer.append('t');
                        break;
                    }
                    case '\n': {
                        outBuffer.append('\\');
                        outBuffer.append('n');
                        break;
                    }
                    case '\r': {
                        outBuffer.append('\\');
                        outBuffer.append('r');
                        break;
                    }
                    case '\f': {
                        outBuffer.append('\\');
                        outBuffer.append('f');
                        break;
                    }
                    case '!':
                    case '#':
                    case ':':
                    case '=': {
                        outBuffer.append('\\');
                        outBuffer.append(aChar);
                        break;
                    }
                    default: {
                        if (aChar < ' ' || aChar > '~') {
                            outBuffer.append('\\');
                            outBuffer.append('u');
                            outBuffer.append(toHex(aChar >> 12 & '\u000f'));
                            outBuffer.append(toHex(aChar >> 8 & '\u000f'));
                            outBuffer.append(toHex(aChar >> 4 & '\u000f'));
                            outBuffer.append(toHex(aChar & '\u000f'));
                            break;
                        }
                        outBuffer.append(aChar);
                        break;
                    }
                }
            }
        }
        return outBuffer.toString();
    }
    
    private static char toHex(final int nibble) {
        return CommonUtil.hexDigit[nibble & 0xF];
    }
    
    public static byte[] trimLine(final byte[] src) {
        if (src == null || src.length <= 0) {
            return src;
        }
        final String temp = new String(src);
        return temp.replaceAll("\\\\u", "\\u").getBytes();
    }
}
