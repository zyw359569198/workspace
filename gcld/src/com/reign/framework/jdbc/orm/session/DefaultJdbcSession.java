package com.reign.framework.jdbc.orm.session;

import java.sql.*;
import javax.sql.*;
import org.apache.commons.logging.*;
import org.springframework.jdbc.datasource.*;
import com.reign.framework.jdbc.orm.transaction.*;
import com.reign.framework.common.*;
import com.reign.framework.jdbc.orm.*;
import org.apache.commons.lang.*;
import com.reign.framework.jdbc.*;
import com.reign.util.*;
import java.util.*;

public class DefaultJdbcSession implements JdbcSession, TransactionListener
{
    private static final Log log;
    private Connection connection;
    private DataSource dataSource;
    private Transaction transaction;
    private boolean hasTransaction;
    private boolean closed;
    private Map<String, Object> cache;
    private Map<String, Object> oldCache;
    private BaseJdbcExtractor jdbcExtractor;
    private JdbcFactory jdbcFactory;
    private Set<JdbcEntity> entitySet;
    private List<DefaultJdbcSessionTrigger> triggerList;
    
    static {
        log = LogFactory.getLog("com.reign.framework.jdbc");
    }
    
    public DefaultJdbcSession(final DataSource dataSource, final JdbcFactory jdbcFactory) {
        this.connection = DataSourceUtils.getConnection(dataSource);
        this.dataSource = dataSource;
        this.jdbcFactory = jdbcFactory;
        this.jdbcExtractor = jdbcFactory.getBaseJdbcExtractor();
    }
    
    @Override
    public Connection getConnection() {
        return this.connection;
    }
    
    @Override
    public Transaction getTransaction() {
        if (this.transaction == null) {
            this.transaction = new JdbcTransaction(this, this.connection, this.jdbcFactory);
        }
        this.hasTransaction = true;
        return this.transaction;
    }
    
    @Override
    public boolean isClosed() {
        return this.closed;
    }
    
    @Override
    public JdbcFactory getJdbcFactory() {
        return this.jdbcFactory;
    }
    
    @Override
    public void close() {
        if (this.isClosed()) {
            throw new RuntimeException("session is alreay closed");
        }
        if (this.cache != null) {
            this.cache.clear();
            this.cache = null;
        }
        if (this.oldCache != null) {
            this.oldCache.clear();
            this.oldCache = null;
        }
        if (this.transaction != null && this.transaction.isActive()) {
            this.transaction.commit();
        }
        DataSourceUtils.releaseConnection(this.connection, this.dataSource);
        if (this.entitySet != null) {
            for (final JdbcEntity entity : this.entitySet) {
                entity.resetCacheFlag();
            }
        }
        this.closed = true;
    }
    
    @Override
    public void clear() {
        if (this.cache != null) {
            this.cache.clear();
        }
        if (this.oldCache != null) {
            this.oldCache.clear();
        }
    }
    
    @Override
    public void evict(final String key) {
        if (this.cache != null) {
            this.cache.remove(key);
        }
        if (this.oldCache != null) {
            this.oldCache.remove(key);
        }
    }
    
    @Override
    public void evictAll() {
        if (this.cache != null) {
            this.cache.clear();
        }
        if (this.oldCache != null) {
            this.oldCache.clear();
        }
    }
    
    @Override
    public <T, PK> T read(final PK pk, final JdbcEntity entity, final ResultSetHandler<T> handler) {
        this.checkTrigger(entity);
        final String key = entity.getKeyValue(String.valueOf(pk));
        T t = this.getFromLocalCache(key);
        if (t != null) {
            return t;
        }
        t = this.jdbcExtractor.read(pk, entity, handler);
        if (t != null) {
            if (entity.isEnhance()) {
                final String id = entity.getKeyValue(t);
                final Object obj = Lang.createObject(entity.getEntityEnhanceClass());
                entity.copy(t, obj);
                this.putToLocalCache(id, obj);
                this.putToOldLocalCache(id, t);
                return (T)obj;
            }
            this.putToLocalCache(key, t);
        }
        return t;
    }
    
    @Override
    public <T, PK> void insert(final T newInstance, final JdbcEntity entity, final String... keys) {
        this.addTrigger(entity.getTableName(), 1, entity.isAutoGenerator() || !this.hasTransaction, entity, new JdbcSessionTrigger() {
            @Override
            public void trigger() {
                if (entity.isEnhance()) {
                    DefaultJdbcSession.this.jdbcExtractor.insert(newInstance, entity, keys);
                    final Object obj = Lang.createObject(entity.getEntityEnhanceClass());
                    entity.copy(newInstance, obj);
                    final String id = entity.getKeyValue(newInstance);
                    DefaultJdbcSession.this.putToLocalCache(id, obj);
                    DefaultJdbcSession.this.putToOldLocalCache(id, newInstance);
                    entity.disableQueryCache();
                    DefaultJdbcSession.this.addToEntitySet(entity);
                }
                else {
                    DefaultJdbcSession.this.jdbcExtractor.insert(newInstance, entity, keys);
                    DefaultJdbcSession.this.putToLocalCache(entity.getKeyValue(newInstance), newInstance);
                    entity.disableQueryCache();
                    DefaultJdbcSession.this.addToEntitySet(entity);
                }
            }
        });
    }
    
    @Override
    public <T> void update(final T transientObject, final JdbcEntity entity) {
        if (entity.isEnhance() && transientObject instanceof IDynamicUpdate) {
            final IDynamicUpdate update = (IDynamicUpdate)transientObject;
            final String pk = entity.getKeyValue(transientObject);
            final JdbcModel old = this.getFromOldLocalCache(pk);
            if (old != null) {
                final Tuple<String, List<Param>> tuple = update.dynamicUpdateSQL(old);
                if (StringUtils.isNotBlank(tuple.left)) {
                    final StringBuilder builder = new StringBuilder(tuple.left);
                    final String[] idColumns = entity.getId().getIdColumnName();
                    final int index = 1;
                    String[] array;
                    for (int length = (array = idColumns).length, i = 0; i < length; ++i) {
                        final String str = array[i];
                        if (index == 1) {
                            builder.append(" WHERE ");
                        }
                        else {
                            builder.append(" AND ");
                        }
                        builder.append(str).append(" = ? ");
                    }
                    final Object[] idValues = entity.getId().getIdValue(transientObject);
                    Object[] array2;
                    for (int length2 = (array2 = idValues).length, j = 0; j < length2; ++j) {
                        final Object obj = array2[j];
                        ((List)tuple.right).add(new Param(obj));
                    }
                    this.addTrigger(entity.getTableName(), 2, !this.hasTransaction, entity, new JdbcSessionTrigger() {
                        @Override
                        public void trigger() {
                            final String sql = MessageFormatter.format(builder.toString(), new Object[] { entity.getTableName() });
                            DefaultJdbcSession.this.jdbcExtractor.update(sql, tuple.right, entity, pk, new String[0]);
                            final String id = entity.getKeyValue(transientObject);
                            DefaultJdbcSession.this.putToLocalCache(id, transientObject);
                            entity.copy(transientObject, old);
                            DefaultJdbcSession.this.putToLocalCache(id, old);
                            entity.disableQueryCache();
                            DefaultJdbcSession.this.addToEntitySet(entity);
                        }
                    });
                }
                return;
            }
        }
        this.addTrigger(entity.getTableName(), 2, !this.hasTransaction, entity, new JdbcSessionTrigger() {
            @Override
            public void trigger() {
                DefaultJdbcSession.this.jdbcExtractor.update(transientObject, entity);
                entity.disableQueryCache();
                DefaultJdbcSession.this.addToEntitySet(entity);
                DefaultJdbcSession.this.putToLocalCache(entity.getKeyValue(transientObject), transientObject);
            }
        });
    }
    
    @Override
    public <PK> void delete(final PK id, final JdbcEntity entity) {
        this.addTrigger(entity.getTableName(), 2, !this.hasTransaction, entity, new JdbcSessionTrigger() {
            @Override
            public void trigger() {
                DefaultJdbcSession.this.jdbcExtractor.delete(id, entity);
                final String key = entity.getKeyValue(String.valueOf(id));
                DefaultJdbcSession.this.removeFromLocalCache(key);
                if (entity.isEnhance()) {
                    DefaultJdbcSession.this.removeFromOldLocalCache(key);
                }
                entity.disableQueryCache();
                DefaultJdbcSession.this.addToEntitySet(entity);
            }
        });
    }
    
    @Override
    public <PK> int update(final String sql, final List<Param> params, final JdbcEntity entity, final PK pk, final String... keys) {
        this.triggerNow();
        final int result = this.jdbcExtractor.update(sql, params, entity, pk, keys);
        if (pk != null) {
            this.removeFromLocalCache(entity.getKeyValue(String.valueOf(pk)));
        }
        else {
            this.clear();
        }
        entity.disableQueryCache();
        entity.disableObjCache();
        this.addToEntitySet(entity);
        return result;
    }
    
    @Override
    public <PK> void updateDelay(final String sql, final List<Param> params, final JdbcEntity entity, final PK pk, final String... keys) {
        this.addTrigger(entity.getTableName(), 2, !this.hasTransaction, entity, new JdbcSessionTrigger() {
            @Override
            public void trigger() {
                DefaultJdbcSession.this.jdbcExtractor.update(sql, params, entity, pk, keys);
                if (pk != null) {
                    DefaultJdbcSession.this.removeFromLocalCache(entity.getKeyValue(String.valueOf(pk)));
                }
                else {
                    DefaultJdbcSession.this.clear();
                }
                entity.disableQueryCache();
                entity.disableObjCache();
                DefaultJdbcSession.this.addToEntitySet(entity);
            }
        });
    }
    
    @Override
    public <T> List<T> query(final String selectKey, final String sql, final List<Param> params, final JdbcEntity entity, final ResultSetHandler<List<T>> handler) {
        this.checkTrigger(entity);
        final List<T> list = this.jdbcExtractor.query(selectKey, sql, params, entity, handler);
        if (list != null && list.size() > 0) {
            for (final T t : list) {
                this.putToLocalCache(entity.getKeyValue(t), t);
            }
        }
        return list;
    }
    
    @Override
    public <T> T query(final String sql, final List<Param> params, final ResultSetHandler<T> handler) {
        this.triggerNow();
        return this.jdbcExtractor.query(sql, params, handler);
    }
    
    @Override
    public void batch(final String sql, final List<List<Param>> paramsList, final JdbcEntity entity, final String... keys) {
        this.triggerNow();
        this.jdbcExtractor.batch(sql, paramsList, entity, keys);
        entity.disableQueryCache();
        entity.disableObjCache();
        this.addToEntitySet(entity);
    }
    
    @Override
    public boolean callProcedure(final String sql, final List<Param> params, final JdbcEntity entity, final String... keys) {
        this.triggerNow();
        final boolean result = this.jdbcExtractor.callProcedure(sql, params, entity, keys);
        entity.disableQueryCache();
        entity.disableObjCache();
        this.addToEntitySet(entity);
        return result;
    }
    
    @Override
    public List<Object> callProcedureWithReturn(final String sql, final List<Param> params, final JdbcEntity entity, final String... keys) {
        this.triggerNow();
        final List<Object> result = this.jdbcExtractor.callProcedureWithReturn(sql, params, entity, keys);
        entity.disableQueryCache();
        entity.disableObjCache();
        this.addToEntitySet(entity);
        return result;
    }
    
    @Override
    public List<Map<String, Object>> query(final String sql, final List<Param> params) {
        this.triggerNow();
        return this.jdbcExtractor.query(sql, params);
    }
    
    @Override
    public int update(final String sql, final List<Param> params) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public int insert(final String sql, final List<Param> params, final boolean autoGenerator) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void batch(final String sql, final List<List<Param>> paramsList) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public boolean callProcedure(final String sql, final List<Param> params) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<Object> callProcedureWithReturn(final String sql, final List<Param> params) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public List<Map<String, Object>> callQueryProcedure(final String sql, final List<Param> params) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public void begin(final Transaction transaction) {
    }
    
    @Override
    public void beforeCommit(final Transaction transaction, final boolean succ) {
        if (succ) {
            this.triggerNow();
        }
    }
    
    @Override
    public void commit(final Transaction transaction, final boolean succ) {
        this.transaction = null;
        this.hasTransaction = false;
    }
    
    private void addTrigger(final String tableName, final int triggerType, final boolean triggerNow, final JdbcEntity entity, final JdbcSessionTrigger trigger) {
        if (triggerNow) {
            trigger.trigger();
            return;
        }
        if (this.triggerList == null) {
            this.triggerList = new ArrayList<DefaultJdbcSessionTrigger>(8);
        }
        this.triggerList.add(new DefaultJdbcSessionTrigger(entity, tableName, triggerType, trigger));
    }
    
    private void checkTrigger(final JdbcEntity entity) {
        if (entity.isDelaySQLEnable()) {
            this.triggerNow();
        }
    }
    
    private void triggerNow() {
        if (this.triggerList == null || this.triggerList.size() == 0) {
            return;
        }
        if (DefaultJdbcSession.log.isDebugEnabled()) {
            DefaultJdbcSession.log.debug("trigger delay sql start");
        }
        Collections.sort(this.triggerList);
        for (final JdbcSessionTrigger trigger : this.triggerList) {
            trigger.trigger();
        }
        this.triggerList.clear();
        if (DefaultJdbcSession.log.isDebugEnabled()) {
            DefaultJdbcSession.log.debug("trigger delay sql end");
        }
    }
    
    private <T> void putToLocalCache(final String key, final T t) {
        if (this.cache == null) {
            this.cache = new HashMap<String, Object>();
        }
        this.cache.put(key, t);
    }
    
    private <T> void putToOldLocalCache(final String key, final T t) {
        if (this.oldCache == null) {
            this.oldCache = new HashMap<String, Object>();
        }
        this.oldCache.put(key, t);
    }
    
    private <T> T getFromLocalCache(final String key) {
        if (this.cache == null) {
            return null;
        }
        return (T)this.cache.get(key);
    }
    
    private <T> T getFromOldLocalCache(final String key) {
        if (this.oldCache == null) {
            return null;
        }
        return (T)this.oldCache.get(key);
    }
    
    private void removeFromLocalCache(final String key) {
        if (this.cache == null) {
            return;
        }
        this.cache.remove(key);
    }
    
    private void removeFromOldLocalCache(final String key) {
        if (this.oldCache == null) {
            return;
        }
        this.oldCache.remove(key);
    }
    
    private void addToEntitySet(final JdbcEntity entity) {
        if (this.entitySet == null) {
            this.entitySet = new HashSet<JdbcEntity>();
        }
        this.entitySet.add(entity);
    }
}
