package com.reign.framework.netty.util;

public final class RequestUtil
{
    public static String[] getValue(final String[] values, final String value) {
        if (values == null || values.length == 0) {
            return new String[] { value };
        }
        final String[] result = new String[values.length + 1];
        System.arraycopy(values, 0, result, 0, values.length);
        result[values.length] = value;
        return result;
    }
}
