package com.reign.framework.netty.util;

import java.net.*;
import java.io.*;

public class Utils
{
    private static final String jvmRoute;
    
    static {
        jvmRoute = _getJvmRoute();
    }
    
    public static String getJvmRoute() {
        return Utils.jvmRoute;
    }
    
    public static String decode(final String str, final String encode) throws UnsupportedEncodingException {
        try {
            return URLDecoder.decode(str, encode);
        }
        catch (UnsupportedEncodingException e) {
            throw e;
        }
        catch (Throwable t) {
            return str;
        }
    }
    
    private static String _getJvmRoute() {
        return System.getProperty("jvmRoute");
    }
}
