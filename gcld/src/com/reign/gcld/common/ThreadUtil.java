package com.reign.gcld.common;

import java.util.concurrent.*;

public class ThreadUtil
{
    public static Executor executor;
    
    static {
        ThreadUtil.executor = Executors.newFixedThreadPool(3);
    }
}
