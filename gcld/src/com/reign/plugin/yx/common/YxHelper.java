package com.reign.plugin.yx.common;

import com.reign.framework.netty.servlet.*;
import com.reign.framework.netty.mvc.result.*;
import com.reign.plugin.yx.*;
import org.apache.commons.lang.*;
import org.jboss.netty.handler.codec.http.*;
import com.reign.framework.json.*;
import com.reign.plugin.yx.result.*;
import com.reign.framework.netty.tcp.*;
import com.reign.framework.netty.http.*;

public class YxHelper
{
    public static ByteResult redirectUnlogPage(final int errorCode, final Request request, final Response response) {
        final String host = request.getHeader("X-Real-Server");
        if (host != null) {
            final String unloginUrl = PluginContext.configuration.getUnLoginRedirectURL(host.toLowerCase());
            if (!StringUtils.isBlank(unloginUrl) && !unloginUrl.equals("-1")) {
                response.addHeader("P3P", "CP=CAO PSA OUR");
                response.addHeader("Location", unloginUrl);
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
            }
        }
        final String rtn = new StringBuilder().append(errorCode).toString();
        return new ByteResult(JsonBuilder.getJson(State.FAIL, rtn.getBytes()));
    }
    
    public static ByteResult redirectUnlogPage(final String errorCode, final Request request, final Response response) {
        final String host = request.getHeader("X-Real-Server");
        if (host != null) {
            final String unloginUrl = PluginContext.configuration.getUnLoginRedirectURL(host.toLowerCase());
            if (!StringUtils.isBlank(unloginUrl) && !unloginUrl.equals("-1")) {
                response.addHeader("P3P", "CP=CAO PSA OUR");
                response.addHeader("Location", unloginUrl);
                response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
            }
        }
        return new ByteResult(JsonBuilder.getJson(State.FAIL, errorCode.getBytes()));
    }
    
    public static ByteResult getResult(final int result, final Request request, final Response response) {
        final String rtn = new StringBuilder().append(result).toString();
        return new ByteResult(JsonBuilder.getJson((result == 1) ? State.SUCCESS : State.FAIL, rtn.getBytes()));
    }
    
    public static PlainResult getPlainResult(final int result, final Request request, final Response response) {
        final String rtn = new StringBuilder().append(result).toString();
        return new PlainResult(JsonBuilder.getJson((result == 1) ? State.SUCCESS : State.FAIL, rtn.getBytes()));
    }
    
    public static boolean isTicketPass(final String ticket, final String sign) {
        return !StringUtils.isBlank(ticket) && !StringUtils.isBlank(sign) && ticket.equalsIgnoreCase(sign);
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
    
    public static ByteResult txRedirectUnlogPage(final int errorCode, String yx, final Request request, final Response response) {
        yx = yx.replace("_m", "");
        final String unloginUrl = PluginContext.configuration.getUnLoginRedirectURL(yx);
        if (!StringUtils.isBlank(unloginUrl) && !unloginUrl.equals("-1")) {
            response.addHeader("P3P", "CP=CAO PSA OUR");
            response.addHeader("Location", unloginUrl);
            response.setStatus(HttpResponseStatus.MOVED_PERMANENTLY);
        }
        final String rtn = new StringBuilder().append(errorCode).toString();
        return new ByteResult(JsonBuilder.getJson(State.FAIL, rtn.getBytes()));
    }
}
