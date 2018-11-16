package com.reign.framework.netty.util;

import java.util.*;
import com.reign.util.*;
import org.jboss.netty.handler.codec.http.*;
import org.jboss.netty.buffer.*;

public final class HttpUtil
{
    public static Map<String, Cookie> getCookies(final HttpRequest httpRequest) {
        final Map<String, Cookie> cookies = new HashMap<String, Cookie>(16);
        final String value = httpRequest.getHeader("Cookie");
        if (value != null) {
            final Set<Cookie> cookieSet = new CookieDecoder().decode(value);
            if (cookieSet != null) {
                for (final Cookie cookie : cookieSet) {
                    final Cookie temp = new DefaultCookie(cookie.getName(), cookie.getValue());
                    temp.setPath(cookie.getPath());
                    temp.setDomain(cookie.getDomain());
                    temp.setSecure(cookie.isSecure());
                    temp.setHttpOnly(cookie.isHttpOnly());
                    cookies.put(temp.getName(), temp);
                }
            }
        }
        return cookies;
    }
    
    public static Map<String, String> getHeaders(final HttpRequest httpRequest) {
        final Map<String, String> headers = new HashMap<String, String>(16);
        for (final String key : httpRequest.getHeaderNames()) {
            headers.put(key, httpRequest.getHeader(key));
        }
        return headers;
    }
    
    public static Tuple<byte[], byte[]> getRequestContent(final HttpRequest httpRequest) {
        final Tuple<byte[], byte[]> tuple = new Tuple();
        final String uri = httpRequest.getUri();
        if (!uri.endsWith(".action")) {
            final String params = uri.substring(uri.indexOf(".action") + 8);
            tuple.left = params.getBytes();
        }
        if (httpRequest.getMethod().equals(HttpMethod.POST)) {
            final ChannelBuffer channelBuffer = httpRequest.getContent();
            final byte[] body = new byte[channelBuffer.readableBytes()];
            httpRequest.getContent().getBytes(channelBuffer.readerIndex(), body, 0, body.length);
            tuple.right = body;
        }
        return tuple;
    }
}
