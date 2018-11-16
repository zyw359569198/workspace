package com.reign.gcld.common.util;

import java.util.regex.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.tcp.*;
import com.reign.framework.netty.http.*;
import org.apache.commons.lang.*;
import javax.servlet.http.*;
import com.reign.gcld.common.log.*;
import java.lang.reflect.*;
import org.apache.commons.logging.*;
import com.reign.gcld.common.*;
import com.reign.util.*;
import java.net.*;
import java.util.*;
import java.io.*;

public final class WebUtil
{
    private static final Random random;
    private static final Pattern PUNCT_STR_PATTERN;
    private static final Pattern WHITESPACE_PATTERN;
    private static Pattern HREF_PATTEN_1;
    private static Pattern HREF_PATTEN_2;
    private static Pattern HREF_PATTEN_3;
    private static Pattern HREF_PATTEN_4;
    
    static {
        random = new Random();
        PUNCT_STR_PATTERN = Pattern.compile("[\\p{Punct}]");
        WHITESPACE_PATTERN = Pattern.compile("[\\s\\u3000]");
        WebUtil.HREF_PATTEN_1 = Pattern.compile("www..+");
        WebUtil.HREF_PATTEN_2 = Pattern.compile("http://.+");
        WebUtil.HREF_PATTEN_3 = Pattern.compile("https://.+");
        WebUtil.HREF_PATTEN_4 = Pattern.compile("<a +href");
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
    
    public static int getMaxPos(final int width) {
        return width * 10 + width;
    }
    
    public static int getMaxNum(final int width) {
        return width * width;
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
    
    public static String getIpAddr(final Request request) {
        String ip = null;
        if (request instanceof TcpRequest) {
            ip = request.getRemoteAddress().getAddress().getHostAddress();
        }
        else {
            final HttpRequest httpRequest = (HttpRequest)request;
            ip = httpRequest.getHeader("Cdn-Src-Ip");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = httpRequest.getHeader("x-forwarded-for");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = httpRequest.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = httpRequest.getHeader("WL-Proxy-Client-IP");
            }
            if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("x-real-ip");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = httpRequest.getRemoteAddress().getAddress().getHostAddress();
            }
        }
        return ip;
    }
    
    public static int getSeasonChangeHour() {
        return 5;
    }
    
    public static int hasCode(final Object obj) {
        return (obj == null) ? 0 : Math.abs(obj.hashCode());
    }
    
    public static boolean containsPunctOrWhitespace(final String str) {
        return containsPunct(str) || containsWhitespace(str);
    }
    
    public static boolean containsPunct(final String str) {
        return WebUtil.PUNCT_STR_PATTERN.matcher(str).find();
    }
    
    public static boolean containsWhitespace(final String str) {
        return WebUtil.WHITESPACE_PATTERN.matcher(str).find();
    }
    
    public static Tuple<Boolean, String> getHTMLContent(final String contant) {
        final Tuple<Boolean, String> tuple = new Tuple();
        tuple.left = false;
        final StringBuilder builder = new StringBuilder(contant.length());
        char[] charArray;
        for (int length = (charArray = contant.toCharArray()).length, i = 0; i < length; ++i) {
            final char c = charArray[i];
            switch (c) {
                case '<': {
                    builder.append("&lt;");
                    break;
                }
                case '>': {
                    builder.append("&gt;");
                    break;
                }
                case '&': {
                    builder.append("&amp;");
                    break;
                }
                case '\"': {
                    builder.append("&quot;");
                    break;
                }
                default: {
                    builder.append(c);
                    break;
                }
            }
        }
        tuple.left = isHref(contant);
        tuple.right = builder.toString();
        return tuple;
    }
    
    public static boolean isHref(final String content) {
        return WebUtil.HREF_PATTEN_1.matcher(content).find() || WebUtil.HREF_PATTEN_2.matcher(content).find() || WebUtil.HREF_PATTEN_3.matcher(content).find() || WebUtil.HREF_PATTEN_4.matcher(content).find();
    }
    
    public static String getCronExpression(final String key) {
        return Configuration.getProperty(key);
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
    
    public static int validate(final String str, final int length, final Pattern pattern, final boolean needCheckFilter) {
        if (StringUtils.isBlank(str)) {
            return 1;
        }
        if (str.length() > length) {
            return 2;
        }
        if (!pattern.matcher(str).find()) {
            return 3;
        }
        return 0;
    }
    
    public static String getValidateMsg(final int result, final int length) {
        switch (result) {
            case 1: {
                return LocalMessages.T_VALIDATE_NO_INPUT;
            }
            case 2: {
                return MessageFormatter.format(LocalMessages.T_VALIDATE_LEN_LIMIT, new Object[] { length });
            }
            case 3: {
                return LocalMessages.T_VALIDATE_CHARACTER_SET;
            }
            case 4: {
                return LocalMessages.T_VALIDATE_FILTER;
            }
            case 5: {
                return LocalMessages.T_VALIDATE_FILTER_NOT_FOUND;
            }
            default: {
                return "";
            }
        }
    }
    
    public static String getReportURL(final String yx, final String reportId) {
        final String gameURL = Configuration.getProperty(yx, "gcld.game.url");
        return getURL(gameURL, "?id=" + reportId);
    }
    
    public static boolean needAntiAddiction(final String yx) {
        final String str = Configuration.getProperty(yx, "gcld.anti.addiction");
        return str != null && "1".equals(str.trim());
    }
    
    public static boolean isSingleRole(final String yx) {
        return getMaxPlayerNum(yx) <= 1;
    }
    
    public static final int getMaxPlayerNum(final String yx) {
        return Integer.parseInt(Configuration.getProperty(yx, "gcld.player.maxRoleNum"));
    }
    
    private static String getURL(final String rootURL, final String append) {
        if (rootURL.endsWith("/") || rootURL.endsWith(".html") || rootURL.endsWith(".htm")) {
            return String.valueOf(rootURL) + append;
        }
        return String.valueOf(rootURL) + "/" + append;
    }
    
    public static void main(final String[] args) {
    }
    
    public static String sendRequest(final String requestURL, final Map<String, Object> paramMap) {
        URL url = null;
        HttpURLConnection connection = null;
        BufferedInputStream bis = null;
        try {
            url = new URL(requestURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            final OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
            final String param = getParam(paramMap);
            out.write(param);
            out.flush();
            out.close();
            final int code = connection.getResponseCode();
            if (code == 200) {
                bis = new BufferedInputStream(connection.getInputStream());
                int length = -1;
                final byte[] buff = new byte[1024];
                final StringBuilder builder = new StringBuilder("");
                while ((length = bis.read(buff)) != -1) {
                    builder.append(new String(buff, 0, length));
                }
                return builder.toString();
            }
            return "";
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
        catch (Exception e3) {
            throw new RuntimeException(e3);
        }
        finally {
            if (bis != null) {
                try {
                    bis.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    public static String sendGetRequest(String requestURL, final Map<String, Object> paramMap) {
        URL url = null;
        HttpURLConnection connection = null;
        BufferedInputStream bis = null;
        try {
            requestURL = getURL(requestURL, paramMap);
            url = new URL(requestURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            final int code = connection.getResponseCode();
            if (code == 200) {
                bis = new BufferedInputStream(connection.getInputStream());
                int length = -1;
                final byte[] buff = new byte[1024];
                final StringBuilder builder = new StringBuilder("");
                while ((length = bis.read(buff)) != -1) {
                    builder.append(new String(buff, 0, length));
                }
                return builder.toString();
            }
            return "";
        }
        catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e2) {
            throw new RuntimeException(e2);
        }
        catch (Exception e3) {
            throw new RuntimeException(e3);
        }
        finally {
            if (bis != null) {
                try {
                    bis.close();
                }
                catch (IOException ex) {}
            }
        }
    }
    
    public static String getURL(final String url, final Map<String, Object> paramMap) throws UnsupportedEncodingException {
        final StringBuilder builder = new StringBuilder();
        builder.append(url);
        final Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
        int index = 0;
        for (final Map.Entry<String, Object> entry : entrySet) {
            if (index != 0) {
                builder.append("&");
            }
            else {
                builder.append("?");
            }
            builder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            ++index;
        }
        return builder.toString();
    }
    
    private static String getParam(final Map<String, Object> paramMap) throws UnsupportedEncodingException {
        final StringBuilder builder = new StringBuilder();
        final Set<Map.Entry<String, Object>> entrySet = paramMap.entrySet();
        int index = 0;
        for (final Map.Entry<String, Object> entry : entrySet) {
            if (index != 0) {
                builder.append("&");
            }
            builder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            ++index;
        }
        return builder.toString();
    }
    
    public static String getForceName(final int forceId) {
        switch (forceId) {
            case 1: {
                return LocalMessages.T_FORCE_WEI;
            }
            case 2: {
                return LocalMessages.T_FORCE_SHU;
            }
            case 3: {
                return LocalMessages.T_FORCE_WU;
            }
            default: {
                return "";
            }
        }
    }
    
    public static String[] getStringArray(final String src) {
        if (StringUtils.isBlank(src)) {
            return new String[0];
        }
        final String reg = "[,\uff0c]";
        return src.split(reg);
    }
    
    public static boolean isFt() {
        final String value = Configuration.getProperty("gcld.locale");
        return value != null && "tw".equalsIgnoreCase(value);
    }
    
    public static int getCodeWrongLimit() {
        return 5;
    }
}
