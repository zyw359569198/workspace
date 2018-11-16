package com.reign.framework.jdbc.orm.asm.util;

import java.lang.reflect.*;
import javax.persistence.*;
import java.util.*;
import com.reign.framework.jdbc.*;

public class ASMUtil
{
    public static final String ANY_TYPE = "*";
    
    public static String getClassName(final Class<?> clazz) {
        final String className = clazz.getName();
        return className.replace(".", "/");
    }
    
    public static String getSignature(final Class<?> clazz, final Class<?>... args) {
        if (args == null || args.length == 0) {
            return null;
        }
        final StringBuilder builder = new StringBuilder();
        builder.append(getDesc(clazz, false)).append("<");
        for (final Class<?> arg : args) {
            builder.append(getDesc(arg, true));
        }
        builder.append(">;");
        return builder.toString();
    }
    
    public static String getSignature(final Class<?> clazz, final Class<?>[][] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        final StringBuilder builder = new StringBuilder();
        builder.append(getDesc(clazz, false)).append("<");
        for (final Class[] arg : args) {
            if (arg.length == 1) {
                builder.append(getDesc(arg[0], true));
            }
            else {
                final Class[] array = new Class[arg.length - 1];
                System.arraycopy(arg, 1, array, 0, array.length);
                builder.append(getSignature(arg[0], array));
            }
        }
        builder.append(">;");
        return builder.toString();
    }
    
    public static String getDesc(final Class<?> clazz, final boolean comma) {
        final StringBuilder builder = new StringBuilder();
        if (clazz.isArray()) {
            builder.append("[").append(getDesc(clazz.getComponentType(), false));
        }
        else if (!clazz.isPrimitive()) {
            builder.append("L");
            String clazzName = clazz.getCanonicalName();
            if (clazz.isMemberClass()) {
                final int dotIndex = clazzName.lastIndexOf(".");
                clazzName = String.valueOf(clazzName.substring(0, dotIndex)) + "$" + clazzName.substring(dotIndex + 1);
            }
            clazzName = clazzName.replaceAll("\\.", "/");
            builder.append(clazzName);
        }
        else {
            builder.append(getPrimitiveLetter(clazz));
        }
        if (comma) {
            builder.append(";");
        }
        return builder.toString();
    }
    
    public static String getDesc(final Method method) {
        final StringBuilder buf = new StringBuilder();
        buf.append("(");
        final Class[] types = method.getParameterTypes();
        for (int i = 0; i < types.length; ++i) {
            buf.append(getDesc(types[i], false));
        }
        buf.append(")");
        buf.append(getDesc(method.getReturnType(), false));
        return buf.toString();
    }
    
    private static Object getPrimitiveLetter(final Class<?> type) {
        if (Integer.TYPE.equals(type)) {
            return "I";
        }
        if (Void.TYPE.equals(type)) {
            return "V";
        }
        if (Boolean.TYPE.equals(type)) {
            return "Z";
        }
        if (Character.TYPE.equals(type)) {
            return "C";
        }
        if (Byte.TYPE.equals(type)) {
            return "B";
        }
        if (Short.TYPE.equals(type)) {
            return "S";
        }
        if (Float.TYPE.equals(type)) {
            return "F";
        }
        if (Long.TYPE.equals(type)) {
            return "J";
        }
        if (Double.TYPE.equals(type)) {
            return "D";
        }
        throw new IllegalStateException("Type: " + type.getCanonicalName() + " is not a primitive type");
    }
    
    public static void main(final String[] args) {
        System.out.println(getSignature(Tuple.class, new Class[][] { { String.class }, { List.class, Param.class } }));
    }
}
