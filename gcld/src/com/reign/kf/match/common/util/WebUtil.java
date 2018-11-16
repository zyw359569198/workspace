package com.reign.kf.match.common.util;

import java.util.*;
import javax.servlet.http.*;
import com.reign.kf.match.log.*;
import java.lang.reflect.*;
import org.apache.commons.logging.*;
import org.apache.commons.lang.*;

public final class WebUtil
{
    private static final Random random;
    
    static {
        random = new Random();
    }
    
    public static double nextDouble() {
        return WebUtil.random.nextDouble();
    }
    
    public static int nextInt() {
        return WebUtil.random.nextInt();
    }
    
    public static int nextInt(final int n) {
        return WebUtil.random.nextInt(n);
    }
    
    public static float nextFloat() {
        return WebUtil.random.nextFloat();
    }
    
    public static boolean nextBoolean() {
        return WebUtil.random.nextBoolean();
    }
    
    public static String getIpAddr(final HttpServletRequest request) {
        String ip = request.getHeader("Cdn-Src-Ip");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("x-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    public static int hasCode(final Object obj) {
        return (obj == null) ? 0 : Math.abs(obj.hashCode());
    }
    
    public static String getCookie(final String cookieName, final HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        Cookie[] array;
        for (int length = (array = cookies).length, i = 0; i < length; ++i) {
            final Cookie cookie = array[i];
            if (cookieName.equalsIgnoreCase(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
    
    public static void setCookie(final HttpServletResponse response, final String name, final String value, final String path, final int maxAge) {
        final Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        response.addCookie(cookie);
    }
    
    public static void setCookie(final HttpServletResponse response, final HttpServletRequest request, final String name, final String value, final int maxAge) {
        final Cookie cookie = new Cookie(name, value);
        cookie.setMaxAge(maxAge);
        cookie.setPath(getPath(request));
        response.addCookie(cookie);
    }
    
    public static void setCookie(final HttpServletResponse response, final HttpServletRequest request, final String name, final String value) {
        final Cookie cookie = new Cookie(name, value);
        cookie.setPath(getPath(request));
        response.addCookie(cookie);
    }
    
    public static void setCookie(final HttpServletResponse response, final String name, final String value) {
        final Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    
    public static String getHeader(final HttpServletRequest request, final String key) {
        return request.getHeader(key);
    }
    
    public static String getPath(final HttpServletRequest request) {
        final String path = request.getContextPath();
        return (path == null || path.length() == 0) ? "/" : path;
    }
    
    public static void print(final Logger log, final String msg, final Throwable t) {
        if (t instanceof InvocationTargetException) {
            final InvocationTargetException invocationTargetException = (InvocationTargetException)t;
            log.error(msg, invocationTargetException.getTargetException());
        }
        else {
            log.error(msg, t);
        }
    }
    
    public static void print(final Log log, final String msg, final Throwable t) {
        if (t instanceof InvocationTargetException) {
            final InvocationTargetException invocationTargetException = (InvocationTargetException)t;
            log.error(msg, invocationTargetException.getTargetException());
        }
        else {
            log.error(msg, t);
        }
    }
    
    public static void print(final Log log, final Throwable t) {
        if (t instanceof InvocationTargetException) {
            final InvocationTargetException invocationTargetException = (InvocationTargetException)t;
            log.error("", invocationTargetException.getTargetException());
        }
        else {
            log.error("", t);
        }
    }
    
    public static void print(final Logger log, final Throwable t) {
        if (t instanceof InvocationTargetException) {
            final InvocationTargetException invocationTargetException = (InvocationTargetException)t;
            log.error("", invocationTargetException.getTargetException());
        }
        else {
            log.error("", t);
        }
    }
    
    public static int getValue(final char c) {
        switch (c) {
            case '0': {
                return 0;
            }
            case '1': {
                return 1;
            }
            default: {
                return 0;
            }
        }
    }
    
    public static String getURL(final String root, final String suffix) {
        if (root.endsWith("/")) {
            return String.valueOf(root) + suffix;
        }
        return String.valueOf(root) + "/" + suffix;
    }
    
    public static String converToString(final int[] array) {
        final StringBuilder builder = new StringBuilder(array.length * 2);
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                builder.append(",");
            }
            builder.append(array[i]);
        }
        return builder.toString();
    }
    
    public static int[] convertStringToArray(final String str) {
        if (StringUtils.isBlank(str)) {
            return new int[0];
        }
        final String[] strs = str.split(",");
        final int[] array = new int[strs.length];
        for (int i = 0; i < array.length; ++i) {
            array[i] = Integer.valueOf(strs[i]);
        }
        return array;
    }
}
