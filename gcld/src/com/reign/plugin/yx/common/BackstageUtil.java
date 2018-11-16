package com.reign.plugin.yx.common;

import com.reign.framework.json.*;

public class BackstageUtil
{
    public static byte[] returnError(final int errorCode) {
        return JsonBuilder.getJson(State.FAIL, new Integer(errorCode).toString().getBytes());
    }
    
    public static byte[] returnError(final String errorCode) {
        return JsonBuilder.getJson(State.FAIL, errorCode.getBytes());
    }
    
    public static byte[] returnSuccess() {
        return JsonBuilder.getJson(State.SUCCESS, "");
    }
    
    public static byte[] returnError() {
        return JsonBuilder.getJson(State.FAIL, "");
    }
}
