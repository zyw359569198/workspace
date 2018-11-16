package com.reign.framework.netty.util;

import java.util.regex.*;
import com.reign.util.*;
import org.apache.commons.lang.*;
import com.reign.util.characterFilter.*;
import java.net.*;
import java.util.*;
import java.io.*;
import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.tcp.*;
import com.reign.framework.netty.http.*;

public class WebUtils
{
    private static final Pattern PUNCT_STR_PATTERN;
    private static final Pattern WHITESPACE_PATTERN;
    private static Pattern HREF_PATTEN_1;
    private static Pattern HREF_PATTEN_2;
    private static Pattern HREF_PATTEN_3;
    private static Pattern HREF_PATTEN_4;
    
    static {
        PUNCT_STR_PATTERN = Pattern.compile("[\\p{Punct}]");
        WHITESPACE_PATTERN = Pattern.compile("[\\s\\u3000]");
        WebUtils.HREF_PATTEN_1 = Pattern.compile("www..+");
        WebUtils.HREF_PATTEN_2 = Pattern.compile("http://.+");
        WebUtils.HREF_PATTEN_3 = Pattern.compile("https://.+");
        WebUtils.HREF_PATTEN_4 = Pattern.compile("<a +href");
    }
    
    public static boolean containsPunctOrWhitespace(final String str) {
        return containsPunct(str) || containsWhitespace(str);
    }
    
    public static boolean containsPunct(final String str) {
        return WebUtils.PUNCT_STR_PATTERN.matcher(str).find();
    }
    
    public static boolean containsWhitespace(final String str) {
        return WebUtils.WHITESPACE_PATTERN.matcher(str).find();
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
        return WebUtils.HREF_PATTEN_1.matcher(content).find() || WebUtils.HREF_PATTEN_2.matcher(content).find() || WebUtils.HREF_PATTEN_3.matcher(content).find() || WebUtils.HREF_PATTEN_4.matcher(content).find();
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
        if (needCheckFilter) {
            final ICharacterFilter characterFilter = CharacterFilterFactory.getInstance().getFilter("playerName");
            if (characterFilter == null) {
                return 0;
            }
            if (!characterFilter.isValid(str)) {
                return 4;
            }
        }
        return 0;
    }
    
    public static String getURL(final String rootURL, final String append) {
        if (rootURL.endsWith("/") || rootURL.endsWith(".html") || rootURL.endsWith(".htm")) {
            return String.valueOf(rootURL) + append;
        }
        return String.valueOf(rootURL) + "/" + append;
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
            builder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
            ++index;
        }
        return builder.toString();
    }
    
    public static String getIp(final Request request) {
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
}
