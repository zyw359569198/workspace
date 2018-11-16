package com.reign.framework.jdbc.orm;

import com.reign.framework.jdbc.orm.cache.*;
import com.reign.framework.common.*;
import com.reign.framework.jdbc.orm.asm.*;
import com.reign.framework.jdbc.orm.util.*;
import java.util.*;
import java.beans.*;
import com.reign.framework.jdbc.orm.cache.ehcache.*;
import com.reign.framework.jdbc.orm.cache.redis.*;
import java.lang.annotation.*;
import com.reign.framework.jdbc.orm.annotation.*;
import com.reign.framework.jdbc.*;
import com.reign.util.*;

public class JdbcEntity
{
    private JdbcField[] fields;
    private IdEntity id;
    private Class<?> clazz;
    private Class<?> enhanceClazz;
    private NameStrategy nameStrategy;
    private String entityName;
    private JdbcField[] idFields;
    private boolean enhance;
    private String insertSQL;
    private String updateSQL;
    private String selectAllSQL;
    private String selectAllCountSQL;
    private String selectSQL;
    private String selectForUpdateSQL;
    private String deleteSQL;
    private String tableName;
    private CacheRegionAccessStrategy<String, Object> cache;
    private CacheRegionAccessStrategy<String, String[]> queryCache;
    private JdbcFactory context;
    private CacheConfig config;
    private ThreadLocal<Boolean> queryCacheEnable;
    private ThreadLocal<Boolean> objCacheEnable;
    private ThreadLocal<Boolean> delaySQLEnable;
    private String prefix;
    private String queryPrefix;
    
    public static JdbcEntity resolve(final Class<?> clazz, final NameStrategy nameStrategy, final JdbcFactory context, final CacheFactory factory) throws IntrospectionException {
        final JdbcEntity entity = new JdbcEntity();
        if (Lang.getAnnotation(clazz, DynamicUpdate.class) != null) {
            entity.enhanceClazz = JdbcModelEnhancer.enhance(clazz);
            entity.enhance = true;
        }
        entity.context = context;
        entity.clazz = clazz;
        entity.fields = JdbcUtil.createJdbcFields(entity.clazz, nameStrategy);
        entity.nameStrategy = nameStrategy;
        entity.entityName = clazz.getSimpleName();
        entity.tableName = nameStrategy.propertyNameToColumnName(entity.entityName);
        entity.prefix = String.valueOf(entity.tableName) + "::cache::";
        entity.queryPrefix = String.valueOf(entity.tableName) + "::query::";
        entity.insertSQL = generatorInsertSQL(entity);
        entity.updateSQL = generatorUpdateSQL(entity);
        entity.selectSQL = generatorSelectSQL(entity);
        entity.deleteSQL = generatorDeleteSQL(entity);
        entity.selectForUpdateSQL = String.valueOf(entity.selectSQL) + " FOR UPDATE";
        entity.selectAllSQL = "SELECT * FROM " + entity.tableName;
        entity.selectAllCountSQL = "SELECT COUNT(1) AS COUNT FROM " + entity.tableName;
        entity.cache = getCache(factory, entity);
        entity.queryCache = getQueryCache(factory, entity);
        entity.queryCacheEnable = new ThreadLocal<Boolean>();
        entity.objCacheEnable = new ThreadLocal<Boolean>();
        entity.delaySQLEnable = new ThreadLocal<Boolean>();
        Introspector.getBeanInfo(entity.clazz);
        JdbcUtil.createBeanMap(entity.clazz);
        final List<JdbcField> idFields = new ArrayList<JdbcField>();
        JdbcField[] fields;
        for (int length = (fields = entity.fields).length, i = 0; i < length; ++i) {
            final JdbcField field = fields[i];
            if (field.isPrimary) {
                idFields.add(field);
            }
        }
        entity.idFields = idFields.toArray(new JdbcField[0]);
        if (entity.idFields.length == 1) {
            entity.id = new SingleIdEntity(entity.idFields[0], entity);
        }
        else {
            entity.id = new ComplexIdEntity(entity.idFields, entity);
        }
        return entity;
    }
    
    private static CacheRegionAccessStrategy<String, Object> getCache(final CacheFactory factory, final JdbcEntity entity) {
        if (factory instanceof RedisCacheFactory) {
            final RedisCacheFactory cacheFactory = (RedisCacheFactory)factory;
            final CacheRegionAccessStrategy<String, Object> strategy = cacheFactory.getCache();
            final RedisCacheReadWriteRegionAccessStrategy<Object> redisStrategy = (RedisCacheReadWriteRegionAccessStrategy)strategy;
            redisStrategy.init(cacheFactory.getPool(), entity);
            return redisStrategy;
        }
        if (factory instanceof EhCacheFactory) {
            final EhCacheFactory cacheFactory2 = (EhCacheFactory)factory;
            final CacheRegionAccessStrategy<String, Object> strategy = cacheFactory2.getCache();
            final EhCacheReadWriteRegionAccessStrategy<String, Object> ehStrategy = (EhCacheReadWriteRegionAccessStrategy)strategy;
            ehStrategy.init(entity.clazz.getName(), entity);
            return strategy;
        }
        return null;
    }
    
    private static CacheRegionAccessStrategy<String, String[]> getQueryCache(final CacheFactory factory, final JdbcEntity entity) {
        if (factory instanceof RedisCacheFactory) {
            final RedisCacheFactory cacheFactory = (RedisCacheFactory)factory;
            final CacheRegionAccessStrategy<String, String[]> strategy = cacheFactory.getQueryCache();
            final RedisCacheReadWriteCollectionRegionAccessStrategy redisStrategy = (RedisCacheReadWriteCollectionRegionAccessStrategy)strategy;
            redisStrategy.init(cacheFactory.getPool(), entity);
            return redisStrategy;
        }
        if (factory instanceof EhCacheFactory) {
            final EhCacheFactory cacheFactory2 = (EhCacheFactory)factory;
            final CacheRegionAccessStrategy<String, String[]> strategy = cacheFactory2.getQueryCache();
            final EhCacheReadWriteRegionAccessStrategy<String, String[]> ehStrategy = (EhCacheReadWriteRegionAccessStrategy)strategy;
            ehStrategy.init(String.valueOf(entity.clazz.getName()) + "::com.reign.queryCache", entity);
            return strategy;
        }
        return null;
    }
    
    private static String generatorInsertSQL(final JdbcEntity entity) {
        final StringBuilder builder = new StringBuilder();
        builder.append("INSERT INTO ").append(entity.tableName).append("(");
        int index = 1;
        JdbcField[] fields;
        for (int length = (fields = entity.fields).length, i = 0; i < length; ++i) {
            final JdbcField field = fields[i];
            if (!field.ignore) {
                if (!field.insertIgnore) {
                    if (index != 1) {
                        builder.append(", ");
                    }
                    builder.append(field.columnName);
                    ++index;
                }
            }
        }
        builder.append(") VALUES (");
        index = 1;
        JdbcField[] fields2;
        for (int length2 = (fields2 = entity.fields).length, j = 0; j < length2; ++j) {
            final JdbcField field = fields2[j];
            if (!field.ignore) {
                if (!field.insertIgnore) {
                    if (index != 1) {
                        builder.append(", ");
                    }
                    builder.append("?");
                    ++index;
                }
            }
        }
        builder.append(")");
        return builder.toString();
    }
    
    private static String generatorUpdateSQL(final JdbcEntity entity) {
        final StringBuilder builder = new StringBuilder();
        builder.append("UPDATE ").append(entity.tableName).append(" SET ");
        int index = 1;
        JdbcField[] fields;
        for (int length = (fields = entity.fields).length, i = 0; i < length; ++i) {
            final JdbcField field = fields[i];
            if (!field.isPrimary) {
                if (!field.ignore) {
                    if (index != 1) {
                        builder.append(", ");
                    }
                    builder.append(field.columnName).append("=").append("?");
                    ++index;
                }
            }
        }
        builder.append(" WHERE ");
        index = 1;
        JdbcField[] fields2;
        for (int length2 = (fields2 = entity.fields).length, j = 0; j < length2; ++j) {
            final JdbcField field = fields2[j];
            if (field.isPrimary) {
                if (index != 1) {
                    builder.append(", ");
                }
                builder.append(field.columnName).append("=").append("?");
                ++index;
            }
        }
        return builder.toString();
    }
    
    private static String generatorSelectSQL(final JdbcEntity entity) {
        final StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM ").append(entity.tableName).append(" WHERE ");
        int index = 1;
        JdbcField[] fields;
        for (int length = (fields = entity.fields).length, i = 0; i < length; ++i) {
            final JdbcField field = fields[i];
            if (field.isPrimary) {
                if (index != 1) {
                    builder.append(", ");
                }
                builder.append(field.columnName).append("=").append("?");
                ++index;
            }
        }
        return builder.toString();
    }
    
    private static String generatorDeleteSQL(final JdbcEntity entity) {
        final StringBuilder builder = new StringBuilder();
        builder.append("DELETE FROM ").append(entity.tableName).append(" WHERE ");
        int index = 1;
        JdbcField[] fields;
        for (int length = (fields = entity.fields).length, i = 0; i < length; ++i) {
            final JdbcField field = fields[i];
            if (field.isPrimary) {
                if (index != 1) {
                    builder.append(", ");
                }
                builder.append(field.columnName).append("=").append("?");
                ++index;
            }
        }
        return builder.toString();
    }
    
    private static JdbcField[] parse(final Lang.MyField[] fields, final NameStrategy nameStrategy) {
        if (fields == null) {
            return null;
        }
        if (fields.length == 0) {
            return new JdbcField[0];
        }
        final JdbcField[] jdbcFields = new JdbcField[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            jdbcFields[i] = new JdbcField(fields[i], nameStrategy);
            jdbcFields[i].isPrimary = Lang.hasAnnotation(jdbcFields[i].field, Id.class);
            jdbcFields[i].insertIgnore = Lang.hasAnnotation(jdbcFields[i].field, InsertIgnoreField.class);
            jdbcFields[i].ignore = Lang.hasAnnotation(jdbcFields[i].field, IgnoreField.class);
            jdbcFields[i].jdbcType = Lang.getJdbcType(jdbcFields[i].field.getType());
        }
        return jdbcFields;
    }
    
    public List<Param> builderInsertParams(final Object obj) {
        final Params params = new Params();
        JdbcField[] fields;
        for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
            final JdbcField field = fields[i];
            if (!field.insertIgnore && !field.ignore) {
                params.addParam(ReflectUtil.get(field.field, obj), field.jdbcType);
            }
        }
        return params;
    }
    
    public List<Param> builderUpdateParams(final Object obj) {
        final Params params = new Params();
        JdbcField[] fields;
        for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
            final JdbcField field = fields[i];
            if (!field.ignore && !field.isPrimary) {
                params.addParam(ReflectUtil.get(field.field, obj), field.jdbcType);
            }
        }
        JdbcField[] idFields;
        for (int length2 = (idFields = this.idFields).length, j = 0; j < length2; ++j) {
            final JdbcField field = idFields[j];
            params.addParam(ReflectUtil.get(field.field, obj), field.jdbcType);
        }
        return params;
    }
    
    public JdbcField[] getFields() {
        return this.fields;
    }
    
    public IdEntity getId() {
        return this.id;
    }
    
    public NameStrategy getNameStrategy() {
        return this.nameStrategy;
    }
    
    public String getInsertSQL() {
        return this.insertSQL;
    }
    
    public String getUpdateSQL() {
        return this.updateSQL;
    }
    
    public List<Param> builderIdParams(final Object... keys) {
        final Params params = new Params();
        int index = 0;
        JdbcField[] idFields;
        for (int length = (idFields = this.idFields).length, i = 0; i < length; ++i) {
            final JdbcField field = idFields[i];
            params.addParam(keys[index++], field.jdbcType);
        }
        return params;
    }
    
    public boolean isAutoGenerator() {
        return this.id.isAutoGenerator();
    }
    
    public String getQueryCollectionName() {
        return "QUERY_" + this.entityName.toUpperCase();
    }
    
    public Class<?> getEntityClass() {
        return this.clazz;
    }
    
    public Class<?> getEntityEnhanceClass() {
        return this.enhanceClazz;
    }
    
    public String getSelectSQL(final boolean forUpdate) {
        if (!forUpdate) {
            return this.selectSQL;
        }
        return this.selectForUpdateSQL;
    }
    
    public String getDeleteSQL() {
        return this.deleteSQL;
    }
    
    public String getSelectAllSQL() {
        return this.selectAllSQL;
    }
    
    public String getSelectAllCountSQL() {
        return this.selectAllCountSQL;
    }
    
    public String getPrefix() {
        return this.prefix;
    }
    
    public String getQueryPrefix() {
        return this.queryPrefix;
    }
    
    public CacheConfig getCacheConfig() {
        if (this.config == null) {
            this.config = this.context.getCacheConfig(this.clazz.getName());
        }
        return this.config;
    }
    
    public CacheConfig getQueryCacheConfig() {
        if (this.config == null) {
            this.config = this.context.getCacheConfig("queryCache");
        }
        return this.config;
    }
    
    public CacheRegionAccessStrategy<String, Object> getCache() {
        return this.cache;
    }
    
    public CacheRegionAccessStrategy<String, String[]> getQueryCache() {
        return this.queryCache;
    }
    
    public JdbcFactory getContext() {
        return this.context;
    }
    
    public String getKeyValue(final String key) {
        return String.valueOf(this.prefix) + key;
    }
    
    public String getKeyValue(final Object obj) {
        return String.valueOf(this.prefix) + this.id.getIdStringValue(obj);
    }
    
    public boolean isEnhance() {
        return this.enhance;
    }
    
    public void copy(final Object src, final Object target) {
        JdbcField[] fields;
        for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
            final JdbcField field = fields[i];
            ReflectUtil.set(field.field, target, ReflectUtil.get(field.field, src));
        }
    }
    
    public String getTableName() {
        return this.tableName;
    }
    
    public void resetCacheFlag() {
        this.queryCacheEnable.remove();
        this.objCacheEnable.remove();
    }
    
    public void resetDelaySQLFlag() {
        this.delaySQLEnable.remove();
    }
    
    public void disableQueryCache() {
        this.queryCacheEnable.set(false);
    }
    
    public void disableObjCache() {
        this.objCacheEnable.set(false);
    }
    
    public void enableDelaySQL() {
        this.delaySQLEnable.set(true);
    }
    
    public boolean isQueryCacheEnable() {
        return this.queryCacheEnable.get() == null || this.queryCacheEnable.get();
    }
    
    public boolean isObjCacheEnable() {
        return this.objCacheEnable.get() == null || this.objCacheEnable.get();
    }
    
    public boolean isDelaySQLEnable() {
        return this.delaySQLEnable.get() != null && this.delaySQLEnable.get();
    }
}
