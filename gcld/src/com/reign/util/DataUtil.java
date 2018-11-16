package com.reign.util;

import org.apache.commons.lang.*;
import java.math.*;
import java.sql.*;
import java.util.*;
import java.lang.reflect.*;
import java.io.*;

public class DataUtil
{
    public static void setObject(final Object target, final String property, final Object value) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, InstantiationException {
        setObjectConversion(target, property, value);
    }
    
    public static void setObjectConversion(final Object target, final String property, final Object value) throws IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
        final String setterName = "set" + StringUtils.capitalize(property);
        if (value instanceof BigDecimal) {
            final BigDecimal dec = (BigDecimal)value;
            final String getterName = "get" + StringUtils.capitalize(property);
            final Method mGet = target.getClass().getMethod(getterName, new Class[0]);
            final Class<?> t = mGet.getReturnType();
            final Method m = target.getClass().getMethod(setterName, t);
            if (t == Integer.class || t == Integer.TYPE) {
                m.invoke(target, dec.intValue());
            }
            else if (t == BigDecimal.class) {
                m.invoke(target, dec);
            }
            else if (t == Long.class || t == Long.TYPE) {
                m.invoke(target, dec.longValue());
            }
            else if (t == BigInteger.class) {
                m.invoke(target, dec.toBigInteger());
            }
            else {
                m.invoke(target, dec.toString());
            }
        }
        else {
            final Method[] methods = target.getClass().getMethods();
            Method[] array;
            for (int length = (array = methods).length, j = 0; j < length; ++j) {
                final Method i = array[j];
                if (i.getName().equals(setterName)) {
                    if (value instanceof Timestamp) {
                        final Timestamp t2 = (Timestamp)value;
                        final Class[] types = i.getParameterTypes();
                        if (types.length > 0 && Date.class.equals(types[0])) {
                            final Date newValue = new Date(t2.getTime());
                            i.invoke(target, newValue);
                            break;
                        }
                    }
                    i.invoke(target, value);
                    break;
                }
            }
        }
    }
    
    public static Object depthClone(final Object srcObj) {
        Object cloneObj = null;
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ObjectOutputStream oo = new ObjectOutputStream(out);
            oo.writeObject(srcObj);
            final ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
            final ObjectInputStream oi = new ObjectInputStream(in);
            cloneObj = oi.readObject();
        }
        catch (IOException e) {
            throw new RuntimeException("io exception", e);
        }
        catch (ClassNotFoundException e2) {
            throw new RuntimeException("class not found", e2);
        }
        return cloneObj;
    }
}
