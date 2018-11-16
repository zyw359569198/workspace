package com.reign.framework.jdbc.orm.cache.ehcache;

import org.apache.commons.logging.*;
import net.sf.ehcache.*;

public class EhCacheManager
{
    private static final Log log;
    private static final EhCacheManager instance;
    private static CacheManager manager;
    private static final long INTERVAL = 600000L;
    
    static {
        log = LogFactory.getLog(EhCacheManager.class);
        instance = new EhCacheManager();
    }
    
    private EhCacheManager() {
        this.init();
    }
    
    private void init() {
        EhCacheManager.manager = new CacheManager(this.getClass().getClassLoader().getResourceAsStream("ehcache.xml"));
        final CacheExpiredThread thread = new CacheExpiredThread();
        thread.start();
    }
    
    public static EhCacheManager getInstance() {
        return EhCacheManager.instance;
    }
    
    public Cache getCache(final String name) {
        Cache cache = EhCacheManager.manager.getCache(name);
        if (cache == null) {
            if (EhCacheManager.log.isWarnEnabled()) {
                EhCacheManager.log.warn(String.valueOf(name) + " cache can't not found configuration, using default cache");
            }
            EhCacheManager.manager.addCache(name);
            cache = EhCacheManager.manager.getCache(name);
        }
        return cache;
    }
    
    public void clear() {
        EhCacheManager.manager.clearAll();
    }
    
    private class CacheExpiredThread extends Thread
    {
        public CacheExpiredThread() {
            super("ehcache-expired-thread");
        }
        
        @Override
        public void run() {
            while (!this.isInterrupted()) {
                try {
                    final String[] caches = EhCacheManager.manager.getCacheNames();
                    String[] array;
                    for (int length = (array = caches).length, i = 0; i < length; ++i) {
                        final String cacheName = array[i];
                        final Cache cache = EhCacheManager.manager.getCache(cacheName);
                        if (cache != null) {
                            cache.evictExpiredElements();
                        }
                    }
                }
                catch (Exception e) {
                    EhCacheManager.log.error("expired ehcache error", e);
                }
                try {
                    Thread.sleep(600000L);
                }
                catch (InterruptedException e2) {
                    EhCacheManager.log.error("expired ehcache error", e2);
                }
            }
        }
    }
}
