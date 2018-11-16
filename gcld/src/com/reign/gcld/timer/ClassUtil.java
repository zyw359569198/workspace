package com.reign.gcld.timer;

import java.lang.reflect.*;

public class ClassUtil
{
    private static final Class<?>[] EMPTY_CLASS_ARRY;
    
    static {
        EMPTY_CLASS_ARRY = new Class[0];
    }
    
    public static Method getMethod(final Class<?> clazz, final String methodName, final Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(methodName, parameterTypes);
        }
        catch (Throwable t) {
            throw new RuntimeException("can't find method " + methodName + " in " + clazz.getName(), t);
        }
    }
    
    public static Class<?>[] getParameterTypes(final Object... params) {
        if (params == null || params.length == 0) {
            return ClassUtil.EMPTY_CLASS_ARRY;
        }
        final Class[] parameterTypes = new Class[params.length];
        int index = 0;
        for (final Object obj : params) {
            parameterTypes[index++] = obj.getClass();
        }
        return parameterTypes;
    }
}
