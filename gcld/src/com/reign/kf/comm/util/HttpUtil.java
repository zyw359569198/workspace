package com.reign.kf.comm.util;

import java.nio.charset.*;
import org.codehaus.jackson.map.*;
import java.util.zip.*;
import java.io.*;
import java.net.*;

public class HttpUtil
{
    public static final int CONNECT_TIMEOUT = 30000;
    public static final int READ_TIMEOUT = 60000;
    public static final Charset UTF8;
    
    static {
        UTF8 = Charset.forName("UTF-8");
    }
    
    public static String sendRequest(final URL url, final Method method) throws IOException {
        return sendRequest(url, null, method);
    }
    
    public static String sendRequest(final URL url, final byte[] data, final Method method) throws IOException {
        final HttpURLConnection client = (HttpURLConnection)url.openConnection();
        client.setConnectTimeout(30000);
        client.setReadTimeout(60000);
        if (Method.POST.equals(method)) {
            client.setRequestMethod(method.value);
            if (data != null) {
                client.setDoOutput(true);
                final OutputStream out = client.getOutputStream();
                out.write(data);
                out.close();
            }
        }
        client.connect();
        if (client.getResponseCode() != 200) {
            throw new ServerUnavailable(url, client.getResponseCode(), client.getResponseMessage());
        }
        final BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), HttpUtil.UTF8));
        final StringBuilder response = new StringBuilder();
        for (String line = in.readLine(); line != null; line = in.readLine()) {
            response.append(line);
        }
        return response.toString();
    }
    
    public static String postByJSON(final URL url, final Object obj, final ObjectMapper objectMapper) throws IOException {
        final HttpURLConnection client = (HttpURLConnection)url.openConnection();
        client.setConnectTimeout(30000);
        client.setReadTimeout(60000);
        client.setRequestMethod(Method.POST.value);
        if (obj != null) {
            client.setDoOutput(true);
            final OutputStream out = client.getOutputStream();
            objectMapper.writeValue(out, obj);
            out.close();
        }
        client.connect();
        if (client.getResponseCode() != 200) {
            throw new ServerUnavailable(url, client.getResponseCode(), client.getResponseMessage());
        }
        final BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), HttpUtil.UTF8));
        final StringBuilder response = new StringBuilder();
        for (String line = in.readLine(); line != null; line = in.readLine()) {
            response.append(line);
        }
        return response.toString();
    }
    
    public static String postByGzipedJSON(final URL url, final Object obj, final ObjectMapper objectMapper) throws IOException {
        final HttpURLConnection client = (HttpURLConnection)url.openConnection();
        client.setConnectTimeout(30000);
        client.setReadTimeout(60000);
        client.setRequestMethod(Method.POST.value);
        if (obj != null) {
            client.setDoOutput(true);
            final GZIPOutputStream out = new GZIPOutputStream(client.getOutputStream());
            objectMapper.writeValue(out, obj);
            out.finish();
        }
        client.connect();
        if (client.getResponseCode() != 200) {
            throw new ServerUnavailable(url, client.getResponseCode(), client.getResponseMessage());
        }
        final BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream(), HttpUtil.UTF8));
        final StringBuilder response = new StringBuilder();
        for (String line = in.readLine(); line != null; line = in.readLine()) {
            response.append(line);
        }
        return response.toString();
    }
    
    public static String sendRequest(final String requestURL, final Object obj) {
        URL url = null;
        HttpURLConnection connection = null;
        BufferedInputStream bis = null;
        try {
            url = new URL(requestURL);
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setConnectTimeout(60000);
            connection.setReadTimeout(120000);
            connection.setRequestProperty("Accept-Charset", "utf-8");
            connection.setRequestProperty("Content-Type", "multiPart/form-data");
            connection.setDefaultUseCaches(false);
            final ObjectOutputStream oos = new ObjectOutputStream(connection.getOutputStream());
            oos.writeObject(obj);
            oos.flush();
            oos.close();
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
    
    public enum Method
    {
        GET("GET", 0, "GET"), 
        POST("POST", 1, "POST");
        
        private String value;
        
        private Method(final String s, final int n, final String value) {
            this.value = value;
        }
        
        public String getValue() {
            return this.value;
        }
    }
    
    public static class ServerUnavailable extends RuntimeException
    {
        private static final long serialVersionUID = 1L;
        
        public ServerUnavailable(final URL url, final int code, final String msg) {
            super("url: " + url + ", code: " + code + ", msg: " + msg);
        }
    }
}
