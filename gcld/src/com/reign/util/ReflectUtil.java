package com.reign.util;

import java.lang.reflect.*;

public final class ReflectUtil
{
    public static Object invoke(final Method method, final Object obj) {
        try {
            return method.invoke(obj, new Object[0]);
        }
        catch (Throwable t) {
            throw new RuntimeException("invoke error", t);
        }
    }
    
    public static void invoke(final Method method, final Object obj, final Object... args) {
        try {
            method.invoke(obj, args);
        }
        catch (Throwable t) {
            throw new RuntimeException("invoke error", t);
        }
    }
    
    public static Object get(final Field field, final Object obj) {
        try {
            field.setAccessible(true);
            return field.get(obj);
        }
        catch (Throwable t) {
            throw new RuntimeException("invoke error", t);
        }
    }
    
    public static void set(final Field field, final Object obj, final Object args) {
        try {
            field.setAccessible(true);
            field.set(obj, args);
        }
        catch (Throwable t) {
            throw new RuntimeException("set key error", t);
        }
    }
}
