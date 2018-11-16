package com.reign.framework.jdbc.orm.redis;

import org.apache.commons.logging.*;
import com.reign.framework.jdbc.orm.*;
import com.reign.framework.jdbc.*;
import com.reign.framework.jdbc.orm.transaction.*;
import java.util.*;
import com.reign.framework.jdbc.orm.cache.*;

public class RedisJdbcExtractor extends AbstractJdbcExtractor implements BaseJdbcExtractor, TransactionListener
{
    private static final Log log;
    private static ThreadLocal<List<AbstractRedisCallBack>> afterTransaction;
    private static ThreadLocal<List<RedisCallBack>> beforeTransaction;
    
    static {
        log = LogFactory.getLog(RedisJdbcExtractor.class);
        RedisJdbcExtractor.afterTransaction = new ThreadLocal<List<AbstractRedisCallBack>>();
        RedisJdbcExtractor.beforeTransaction = new ThreadLocal<List<RedisCallBack>>();
    }
    
    @Override
    public <T, PK> T read(final PK pk, final JdbcEntity entity, final ResultSetHandler<T> handler) {
        final String key = String.valueOf(pk);
        T t = (T)entity.getCache().get(key);
        if (t != null) {
            CacheStatistics.addHits();
            return t;
        }
        CacheStatistics.addMiss();
        t = this.query(entity.getSelectSQL(false), entity.builderIdParams(pk), handler);
        if (t != null) {
            entity.getCache().put(key, t);
        }
        return t;
    }
    
    @Override
    public <T, PK> void insert(final T newInstance, final JdbcEntity entity, final String... keys) {
        final int result = this.insert(entity.getInsertSQL(), entity.builderInsertParams(newInstance), entity.isAutoGenerator());
        if (entity.isAutoGenerator()) {
            entity.getId().setKey(newInstance, result);
        }
        this.addAfterTransactionSuccCacheCallBack(new AbstractRedisCallBack(true) {
            @Override
            public void execute() throws Exception {
                entity.getCache().put(entity.getId().getIdStringValue(newInstance), newInstance);
                RedisJdbcExtractor.this.clearQueryCache(entity.getQueryCache(), keys);
            }
        });
    }
    
    @Override
    public <T> void update(final T transientObject, final JdbcEntity entity) {
        this.addBeforeTransactionCacheCallBack(entity, entity.getId().getIdStringValue(transientObject));
        final int result = this.update(entity.getUpdateSQL(), entity.builderUpdateParams(transientObject));
        if (result == 1) {
            this.addAfterTransactionSuccCacheCallBack(new AbstractRedisCallBack(true) {
                @Override
                public void execute() throws Exception {
                    RedisJdbcExtractor.this.clearQueryCache(entity.getQueryCache(), "all");
                }
            });
        }
    }
    
    @Override
    public <PK> void delete(final PK id, final JdbcEntity entity) {
        this.addBeforeTransactionCacheCallBack(entity, String.valueOf(id));
        final int result = this.update(entity.getDeleteSQL(), entity.builderIdParams(id));
    }
    
    @Override
    public <PK> void updateDelay(final String sql, final List<Param> params, final JdbcEntity entity, final PK pk, final String... keys) {
        this.update(sql, params, entity, pk, keys);
    }
    
    @Override
    public <PK> int update(final String sql, final List<Param> params, final JdbcEntity entity, final PK pk, final String... keys) {
        if (pk != null) {
            this.addBeforeTransactionCacheCallBack(entity, String.valueOf(pk));
        }
        final int result = this.update(sql, params);
        this.addAfterTransactionSuccCacheCallBack(new AbstractRedisCallBack(true) {
            @Override
            public void execute() throws Exception {
                if (pk == null) {
                    entity.getCache().clear();
                }
                RedisJdbcExtractor.this.clearQueryCache(entity.getQueryCache(), keys);
            }
        });
        return result;
    }
    
    @Override
    public <T> List<T> query(final String selectkey, final String sql, final List<Param> params, final JdbcEntity entity, final ResultSetHandler<List<T>> handler) {
        final String selectKey = this.builderSelectKey(selectkey, params);
        final String[] ids = entity.getQueryCache().get(selectKey);
        if (ids != null) {
            final List<T> list = (List<T>)entity.getCache().mget(ids);
            if (list != null && list.size() == ids.length) {
                CacheStatistics.addQueryHits();
                return list;
            }
        }
        CacheStatistics.addQueryMiss();
        final List<T> list = this.query(sql, params, handler);
        this.putToCache(selectKey, list, entity);
        return list;
    }
    
    @Override
    public <T> T query(final String sql, final List<Param> params, final ResultSetHandler<T> handler) {
        if (RedisJdbcExtractor.log.isDebugEnabled()) {
            RedisJdbcExtractor.log.debug("no cache: " + sql);
        }
        CacheStatistics.addNoCache();
        return super.query(sql, params, handler);
    }
    
    @Override
    public List<Map<String, Object>> query(final String sql, final List<Param> params) {
        if (RedisJdbcExtractor.log.isDebugEnabled()) {
            RedisJdbcExtractor.log.debug("no cache: " + sql);
        }
        CacheStatistics.addNoCache();
        return super.query(sql, params);
    }
    
    @Override
    public void batch(final String sql, final List<List<Param>> paramsList, final JdbcEntity entity, final String... keys) {
        this.batch(sql, paramsList);
        this.addAfterTransactionSuccCacheCallBack(new AbstractRedisCallBack(true) {
            @Override
            public void execute() throws Exception {
                entity.getCache().clear();
                RedisJdbcExtractor.this.clearQueryCache(entity.getQueryCache(), keys);
            }
        });
    }
    
    @Override
    public boolean callProcedure(final String sql, final List<Param> params, final JdbcEntity entity, final String... keys) {
        final boolean result = this.callProcedure(sql, params);
        this.addAfterTransactionSuccCacheCallBack(new AbstractRedisCallBack(true) {
            @Override
            public void execute() throws Exception {
                entity.getCache().clear();
                RedisJdbcExtractor.this.clearQueryCache(entity.getQueryCache(), keys);
            }
        });
        return result;
    }
    
    @Override
    public List<Object> callProcedureWithReturn(final String sql, final List<Param> params, final JdbcEntity entity, final String... keys) {
        final List<Object> result = this.callProcedureWithReturn(sql, params);
        this.addAfterTransactionSuccCacheCallBack(new AbstractRedisCallBack(true) {
            @Override
            public void execute() throws Exception {
                entity.getCache().clear();
                RedisJdbcExtractor.this.clearQueryCache(entity.getQueryCache(), keys);
            }
        });
        return result;
    }
    
    @Override
    public void beforeCommit(final Transaction transaction, final boolean succ) {
        final List<RedisCallBack> list = RedisJdbcExtractor.beforeTransaction.get();
        try {
            if (list == null) {
                return;
            }
            for (final RedisCallBack callBack : list) {
                try {
                    callBack.execute();
                }
                catch (Exception e) {
                    RedisJdbcExtractor.log.error("redis call error", e);
                }
            }
        }
        finally {
            RedisJdbcExtractor.beforeTransaction.remove();
        }
        RedisJdbcExtractor.beforeTransaction.remove();
    }
    
    @Override
    public void begin(final Transaction transaction) {
        RedisJdbcExtractor.beforeTransaction.set(new ArrayList<RedisCallBack>());
        RedisJdbcExtractor.afterTransaction.set(new ArrayList<AbstractRedisCallBack>());
    }
    
    @Override
    public void commit(final Transaction transaction, final boolean succ) {
        final List<AbstractRedisCallBack> list = RedisJdbcExtractor.afterTransaction.get();
        try {
            if (list != null) {
                for (final AbstractRedisCallBack callBack : list) {
                    try {
                        if (callBack.doInTransactionSucc && !succ) {
                            continue;
                        }
                        callBack.execute();
                    }
                    catch (Exception e) {
                        RedisJdbcExtractor.log.error("redis call error", e);
                    }
                }
            }
        }
        finally {
            RedisJdbcExtractor.afterTransaction.remove();
        }
        RedisJdbcExtractor.afterTransaction.remove();
    }
    
    protected final String builderSelectKey(final String sql, final List<Param> params) {
        final StringBuilder builder = new StringBuilder();
        builder.append(sql).append("::");
        for (final Param param : params) {
            builder.append(param.obj.toString()).append(",");
        }
        return builder.toString();
    }
    
    protected final <T> void putToCache(final String selectKey, final List<T> list, final JdbcEntity entity) {
        final String[] ids = new String[list.size()];
        int index = 0;
        for (final T t : list) {
            ids[index] = entity.getId().getIdStringValue(t);
            entity.getCache().put(ids[index], t);
            ++index;
        }
        entity.getQueryCache().put(selectKey, ids);
    }
    
    protected final void addBeforeTransactionCacheCallBack(final RedisCallBack redisCallBack) {
        final List<RedisCallBack> list = RedisJdbcExtractor.beforeTransaction.get();
        if (list == null) {
            try {
                redisCallBack.execute();
            }
            catch (Exception e) {
                RedisJdbcExtractor.log.error("redis call error", e);
            }
        }
        else {
            list.add(redisCallBack);
        }
    }
    
    protected final void addAfterTransactionSuccCacheCallBack(final AbstractRedisCallBack redisCallBack) {
        final List<AbstractRedisCallBack> list = RedisJdbcExtractor.afterTransaction.get();
        if (list == null) {
            try {
                redisCallBack.execute();
            }
            catch (Exception e) {
                RedisJdbcExtractor.log.error("redis call error", e);
            }
        }
        else {
            list.add(redisCallBack);
        }
    }
    
    protected final void addAfterTransactionCacheCallBack(final AbstractRedisCallBack redisCallBack) {
        final List<AbstractRedisCallBack> list = RedisJdbcExtractor.afterTransaction.get();
        if (list == null) {
            try {
                redisCallBack.execute();
            }
            catch (Exception e) {
                RedisJdbcExtractor.log.error("redis call error", e);
            }
        }
        else {
            list.add(redisCallBack);
        }
    }
    
    protected final void addBeforeTransactionCacheCallBack(final JdbcEntity entity, final String key) {
        this.addBeforeTransactionCacheCallBack(new RedisCallBack() {
            @Override
            public void execute() throws Exception {
                final LockItem<Object> lockItem = entity.getCache().lockItem(key);
                RedisJdbcExtractor.this.addAfterTransactionCacheCallBack(new AbstractRedisCallBack(false) {
                    @Override
                    public void execute() throws Exception {
                        entity.getCache().unlockItem(key, lockItem);
                    }
                });
            }
        });
    }
    
    protected final void clearQueryCache(final Cache<String, String[]> cache, final String... keys) {
        if (keys == null) {
            cache.clear();
            return;
        }
        if (keys.length == 0) {
            cache.clear();
            return;
        }
        for (final String key : keys) {
            if ("all".equalsIgnoreCase(key)) {
                cache.clear();
                break;
            }
            cache.remove(String.valueOf(key) + "*");
        }
    }
}
