package com.reign.util;

import java.util.concurrent.*;
import org.apache.commons.lang.*;
import java.util.*;

public final class PropertiesUtil
{
    private static Map<String, ResourceBundle> cacheMap;
    private static final String SPLIT = "-";
    
    static {
        PropertiesUtil.cacheMap = new ConcurrentHashMap<String, ResourceBundle>();
    }
    
    public static Integer getIntText(final Class<?> clazz, final String key) {
        try {
            final String text = getText(clazz, key);
            return Integer.parseInt(text);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException(String.valueOf(key) + " is not a integer value", e);
        }
    }
    
    public static Float getFloatText(final Class<?> clazz, final String key) {
        try {
            final String text = getText(clazz, key);
            return Float.parseFloat(text);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException(String.valueOf(key) + " is not a float value", e);
        }
    }
    
    public static Double getDoubleText(final Class<?> clazz, final String key) {
        try {
            final String text = getText(clazz, key);
            return Double.parseDouble(text);
        }
        catch (NumberFormatException e) {
            throw new RuntimeException(String.valueOf(key) + " is not a double value", e);
        }
    }
    
    public static String[] getStringArrayText(final Class<?> clazz, final String key, final String split) {
        final String text = getText(clazz, key);
        return text.split(split);
    }
    
    public static Integer[] getIntgetArrayText(final Class<?> clazz, final String key, final String split) {
        try {
            final String text = getText(clazz, key);
            final String[] strs = text.split(split);
            final Integer[] array = new Integer[strs.length];
            int index = 0;
            String[] array2;
            for (int length = (array2 = strs).length, i = 0; i < length; ++i) {
                final String str = array2[i];
                array[index] = Integer.parseInt(str);
                ++index;
            }
            return array;
        }
        catch (NumberFormatException e) {
            throw new RuntimeException(String.valueOf(key) + " is not a integer array expression, need split by " + split, e);
        }
    }
    
    public static String getText(final Class<?> clazz, final String key) {
        return getText(clazz, key, new Object[0]);
    }
    
    public static String getText(final Class<?> clazz, final String key, final List<String> args) {
        return getText(clazz, key, args.toArray());
    }
    
    public static String getText(final Class<?> clazz, final String key, final Object[] args) {
        final String result = findText(clazz, key, args);
        return result;
    }
    
    private static String findText(final Class<?> clazz, final String key, final Object[] args) {
        final String name = clazz.getName();
        final String _key = getKey(name, key);
        final ResourceBundle rb = PropertiesUtil.cacheMap.get(_key);
        if (rb != null) {
            return rb.getString(key);
        }
        return findText(name, key, _key, args);
    }
    
    private static String findText(String name, final String key, final String _key, final Object[] args) {
        while (true) {
            Label_0141: {
                try {
                    final ResourceBundle resourceBundle = ResourceBundle.getBundle(String.valueOf(name) + ".package", Locale.getDefault(), Thread.currentThread().getContextClassLoader());
                    if (resourceBundle != null) {
                        final String value = resourceBundle.getString(key);
                        PropertiesUtil.cacheMap.put(_key, resourceBundle);
                        return value;
                    }
                    break Label_0141;
                }
                catch (Exception ex) {
                    break Label_0141;
                }
                name = name.substring(0, name.lastIndexOf(46));
                try {
                    final ResourceBundle resourceBundle = ResourceBundle.getBundle(String.valueOf(name) + ".package", Locale.getDefault(), Thread.currentThread().getContextClassLoader());
                    if (resourceBundle != null) {
                        final String value = resourceBundle.getString(key);
                        PropertiesUtil.cacheMap.put(_key, resourceBundle);
                        return value;
                    }
                }
                catch (Exception ex2) {}
            }
            if (name.indexOf(".") == -1) {
                return "";
            }
            continue;
        }
    }
    
    public static final String toResourceName(final String bundleName, final String suffix) {
        final StringBuilder sb = new StringBuilder(bundleName.length() + 1 + suffix.length());
        sb.append(bundleName.replace('.', '/')).append('.').append(suffix);
        return sb.toString();
    }
    
    private static String getKey(final String className, final String key) {
        return new StringBuilder(50).append(className).append("-").append(key).toString();
    }
    
    public static List<String> getList(final Class<?> clazz, final String key) {
        final String value = getText(clazz, key);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return Arrays.asList(value.split(","));
    }
    
    public static Map<String, String> getMap(final Class<?> clazz, final String key) {
        final String value = getText(clazz, key);
        if (StringUtils.isBlank(value)) {
            return null;
        }
        final Map<String, String> map = new LinkedHashMap<String, String>();
        final String[] array = value.split(",");
        String strKey = null;
        String strValue = null;
        for (int i = 0; i < array.length; ++i) {
            if (i % 2 == 0) {
                strKey = array[i];
            }
            else {
                strValue = array[i];
                map.put(strKey, strValue);
            }
        }
        return map;
    }
}
