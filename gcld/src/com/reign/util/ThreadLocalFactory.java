package com.reign.util;

import java.util.*;

public class ThreadLocalFactory
{
    public static ThreadLocal<StringBuilder> treadLocalLog;
    public static ThreadLocal<Object> treadLocalObj;
    public static ThreadLocal<List<String>> threadLocalLogList;
    
    static {
        ThreadLocalFactory.treadLocalLog = new ThreadLocal<StringBuilder>() {
            @Override
            protected StringBuilder initialValue() {
                return new StringBuilder(10);
            }
        };
        ThreadLocalFactory.treadLocalObj = new ThreadLocal<Object>();
        ThreadLocalFactory.threadLocalLogList = new ThreadLocal<List<String>>() {
            @Override
            protected List<String> initialValue() {
                return new ArrayList<String>();
            }
        };
    }
    
    public static String getTreadLocalLog() {
        final String result = ThreadLocalFactory.treadLocalLog.get().toString();
        ThreadLocalFactory.treadLocalLog.get().setLength(0);
        return result;
    }
    
    public static void setTreadLocalLog(final String content) {
        ThreadLocalFactory.treadLocalLog.get().append(content);
    }
    
    public static void setThreadLocalObj(final Object obj) {
        ThreadLocalFactory.treadLocalObj.set(obj);
    }
    
    public static Object getThreadLocalObj() {
        return ThreadLocalFactory.treadLocalObj.get();
    }
    
    public static List<String> getTreadLocalLogs() {
        final List<String> result = ThreadLocalFactory.threadLocalLogList.get();
        return result;
    }
    
    public static void addTreadLocalLog(final String content) {
        ThreadLocalFactory.threadLocalLogList.get().add(content);
    }
    
    public static void clearTreadLocalLog() {
        ThreadLocalFactory.threadLocalLogList.get().clear();
    }
}
