package com.reign.framework.common.cache;

import java.util.*;

public class SdataManager
{
    private static final SdataManager instance;
    private Map<Class<?>, ICache<?, ?>>[] cacheMap;
    private Set<Class<?>> cacheSet;
    private volatile int cursor;
    private ThreadLocal<Integer> cursorLocal;
    
    static {
        instance = new SdataManager();
    }
    
    private SdataManager() {
        (this.cacheMap = new HashMap[2])[0] = new HashMap<Class<?>, ICache<?, ?>>();
        this.cacheMap[1] = new HashMap<Class<?>, ICache<?, ?>>();
        this.cursorLocal = new ThreadLocal<Integer>();
        this.cacheSet = new HashSet<Class<?>>();
    }
    
    public static SdataManager getInstance() {
        return SdataManager.instance;
    }
    
    public void register(final Class<?> type) {
        this.cacheSet.add(type);
    }
    
    public ICache<?, ?> getCache(final Class<?> type) {
        Integer index = this.cursorLocal.get();
        if (index == null) {
            index = this.cursor;
            this.cursorLocal.set(index);
        }
        return this.cacheMap[this.cursor].get(type);
    }
    
    public void clear() {
        this.cursorLocal.remove();
    }
    
    public void reload(final SDataLoader loader) {
        final int index = 1 - this.cursor;
        try {
            SDataXMLLoader.reset();
            this.init(index, loader);
            this.cursor = index;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void init(final int index, final SDataLoader loader) throws Exception {
        for (final Class<?> clazz : this.cacheSet) {
            final Object obj = clazz.newInstance();
            if (obj instanceof ICache) {
                final ICache<?, ?> cache = (ICache<?, ?>)obj;
                cache.setSDataLoader(loader);
                cache.reload();
                this.cacheMap[index].put(clazz, cache);
            }
        }
    }
}
