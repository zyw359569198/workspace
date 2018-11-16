package com.reign.plugin.yx.common;

import org.apache.commons.logging.*;
import java.net.*;
import java.util.*;
import java.io.*;
import java.security.*;
import javax.net.ssl.*;
import java.security.cert.*;

public class WebUtils
{
    private static final Log opReport;
    
    static {
        opReport = LogFactory.getLog("com.reign.gcld.opreport");
    }
    
    public static String sendGetRequest(String requestURL, final Map<String, Object> paramMap) {
        URL url = null;
        HttpURLConnection connection = null;
        BufferedInputStream bis = null;
        try {
            requestURL = getURL(requestURL, paramMap);
            System.out.println("test3#requestURL:" + requestURL);
            url = new URL(requestURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            final int code = connection.getResponseCode();
            System.out.println("test3#requestURL:" + requestURL + "#code:" + code);
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
    
    public static String sendRequestByPostWithJson(final String requestURL, final String json) {
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
            out.write(json);
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
                WebUtils.opReport.error("requestURL:" + requestURL + "#code:" + code + "#response:" + builder.toString() + "#json:" + json);
                return builder.toString();
            }
            bis = new BufferedInputStream(connection.getInputStream());
            int length = -1;
            final byte[] buff = new byte[1024];
            final StringBuilder builder = new StringBuilder("");
            while ((length = bis.read(buff)) != -1) {
                builder.append(new String(buff, 0, length));
            }
            WebUtils.opReport.error("requestURL:" + requestURL + "#code:" + code + "#response:" + builder.toString() + "#param:" + json);
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
    
    public static void main(final String[] args) throws UnsupportedEncodingException {
        System.out.println(URLEncoder.encode("abc=", "UTF-8"));
    }
    
    public static String sendSSLGetRequest(String requestURL, final Map<String, Object> paramMap) {
        URL url = null;
        HttpsURLConnection connection = null;
        BufferedInputStream bis = null;
        try {
            final SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, new TrustManager[] { new TrustAnyTrustManager(null) }, new SecureRandom());
            requestURL = getURL(requestURL, paramMap);
            url = new URL(requestURL);
            connection = (HttpsURLConnection)url.openConnection();
            connection.setSSLSocketFactory(sc.getSocketFactory());
            connection.setHostnameVerifier(new TrustAnyHostnameVerifier(null));
            connection.setDoOutput(false);
            connection.connect();
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
            if (code == 400) {
                bis = new BufferedInputStream(connection.getErrorStream());
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
    
    public static String sendRequest(final String requestURL, final String content) {
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
            out.write(content);
            out.flush();
            out.close();
            final int code = connection.getResponseCode();
            WebUtils.opReport.error("code:" + code);
            if (code == 200) {
                bis = new BufferedInputStream(connection.getInputStream());
                int length = -1;
                final byte[] buff = new byte[1024];
                final StringBuilder builder = new StringBuilder("");
                while ((length = bis.read(buff)) != -1) {
                    builder.append(new String(buff, 0, length));
                }
                WebUtils.opReport.error("buffer:" + buff.toString());
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
    
    public static String sendRequest(final String requestURL, final String content, final String charset) {
        URL url = null;
        HttpURLConnection connection = null;
        BufferedInputStream bis = null;
        try {
            url = new URL(requestURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            final OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), charset);
            out.write(content);
            out.flush();
            out.close();
            final int code = connection.getResponseCode();
            WebUtils.opReport.error("code:" + code);
            if (code == 200) {
                bis = new BufferedInputStream(connection.getInputStream());
                int length = -1;
                final byte[] buff = new byte[1024];
                final StringBuilder builder = new StringBuilder("");
                while ((length = bis.read(buff)) != -1) {
                    builder.append(new String(buff, 0, length));
                }
                WebUtils.opReport.error("buffer:" + buff.toString());
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
    
    private static class TrustAnyHostnameVerifier implements HostnameVerifier
    {
        @Override
        public boolean verify(final String hostname, final SSLSession session) {
            return true;
        }
    }
    
    private static class TrustAnyTrustManager implements X509TrustManager
    {
        @Override
        public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        }
        
        @Override
        public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
        }
        
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }
}
