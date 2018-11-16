package com.reign.util;

public class StopWatch
{
    private long start;
    private long end;
    
    public static StopWatch begin() {
        final StopWatch sw = new StopWatch();
        sw.start();
        return sw;
    }
    
    public static StopWatch run(final Runnable runnable) {
        final StopWatch sw = begin();
        runnable.run();
        sw.stop();
        return sw;
    }
    
    public void start() {
        this.start = System.currentTimeMillis();
    }
    
    public void stop() {
        this.end = System.currentTimeMillis();
    }
    
    public long getElapsedTime() {
        return this.end - this.start;
    }
}
