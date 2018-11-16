package com.reign.gcld.common.util;

import java.util.*;
import java.util.concurrent.*;
import com.reign.gcld.common.log.*;
import com.reign.framework.netty.servlet.*;
import com.reign.util.*;
import com.reign.framework.netty.mvc.result.*;
import org.apache.commons.lang.*;
import com.reign.gcld.common.*;
import com.reign.framework.json.*;

public class YxUtil
{
    private static Map<String, String> yxMap;
    private static final Logger errorLogger;
    
    static {
        YxUtil.yxMap = new ConcurrentHashMap<String, String>();
        initYx();
        errorLogger = CommonLog.getLog(YxUtil.class);
    }
    
    public static Tuple<Boolean, ByteResult> checkIP(final Request request, final String yx) {
        final Tuple<Boolean, ByteResult> tuple = new Tuple();
        tuple.left = false;
        final String limit_yx_ip = Configuration.getProperty(yx, "gcld.limit.yx.ip");
        if (StringUtils.isBlank(limit_yx_ip) || -1 != limit_yx_ip.indexOf("0")) {
            return tuple;
        }
        final String ips = Configuration.getProperty(yx, "gcld.yx.ip");
        if (StringUtils.isBlank(ips) || !includeIp(ips, WebUtil.getIpAddr(request))) {
            tuple.left = true;
            tuple.right = new ByteResult(JsonBuilder.getJson(State.FAIL, LocalMessages.T_AUTH_10007));
            YxUtil.errorLogger.error("#yx_ip_forbidden#ips:+" + ips + "#ip:" + WebUtil.getIpAddr(request) + "#");
        }
        return tuple;
    }
    
    private static synchronized void initYx() {
        final String yx = Configuration.getProperty("gcld.yx");
        if (StringUtils.isNotBlank(yx)) {
            final String[] yxs = yx.split(",");
            String[] array;
            for (int length = (array = yxs).length, i = 0; i < length; ++i) {
                final String temp = array[i];
                YxUtil.yxMap.put(temp.trim(), temp.trim());
            }
        }
    }
    
    public static boolean isMatched(final String yx) {
        if (YxUtil.yxMap.get(yx.trim()) != null) {
            return true;
        }
        initYx();
        if (YxUtil.yxMap.get(yx.trim()) != null) {
            return true;
        }
        YxUtil.errorLogger.error("#yx#yx_not_match#yxs:" + Configuration.getProperty("gcld.yx") + "#yx:" + yx + "#");
        return false;
    }
    
    public static boolean includeIp(final String ips, final String ip) {
        if (StringUtils.isBlank(ips) || StringUtils.isBlank(ip)) {
            YxUtil.errorLogger.error("#include_ip_fail#ips#" + ips + "#ip#" + ip + "#");
            return false;
        }
        final String[] ipArray = ips.split(",");
        String[] array;
        for (int length = (array = ipArray).length, i = 0; i < length; ++i) {
            final String temp = array[i];
            if (temp.trim().equals(ip.trim())) {
                return true;
            }
        }
        YxUtil.errorLogger.error("#include_ip_fail#ips#" + ips + "#ip#" + ip + "#");
        return false;
    }
    
    public static Map<String, String> getYxMap() {
        return YxUtil.yxMap;
    }
}
