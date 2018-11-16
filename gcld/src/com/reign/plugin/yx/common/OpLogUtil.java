package com.reign.plugin.yx.common;

import com.reign.framework.netty.mvc.result.*;
import java.util.*;

public class OpLogUtil
{
    private static final String SHARP = "#";
    private static final String UNDERLINE = "_";
    private static final String SUCCESS = "success";
    private static final String FAIL = "fail";
    private static final String COMMA = ",";
    
    public static String formatOpInterfaceLog(final String methodName, final String detail, final String ip, final Map<String, String[]> getParams, byte[] postContent, final boolean exception, final long time, final ByteResult returnContent, final int returnCode) {
        if (postContent == null) {
            postContent = "".getBytes();
        }
        final StringBuffer sb = new StringBuffer();
        sb.append("#");
        sb.append("methodName=").append(methodName).append("#");
        sb.append(String.valueOf(methodName) + "_" + ((1 == returnCode) ? "success" : "fail")).append("#");
        sb.append(detail).append("#");
        sb.append("ip=").append(ip).append("#");
        sb.append("getParams=");
        if (getParams != null && !getParams.isEmpty()) {
            final Set<Map.Entry<String, String[]>> entrySet = getParams.entrySet();
            int index = 0;
            for (final Map.Entry<String, String[]> entry : entrySet) {
                if (index != 0) {
                    sb.append(",");
                }
                if (entry.getValue() == null) {
                    continue;
                }
                final String key = entry.getKey();
                String paramComment = entry.getValue()[0];
                paramComment = paramComment.replaceAll(":", "|");
                paramComment = paramComment.replaceAll("#", "|");
                paramComment = paramComment.replaceAll(",", "|");
                paramComment = paramComment.replaceAll("\n", "");
                sb.append(key).append(":").append(paramComment);
                ++index;
            }
        }
        sb.append("#");
        sb.append("postContent=").append(new String(postContent)).append("#");
        sb.append("exception=").append(exception).append("#");
        sb.append("time=").append(time).append("#");
        sb.append("returnContent=").append(new String(returnContent.getResult())).append("#");
        sb.append("returnCode=").append(returnCode);
        return sb.toString();
    }
}
