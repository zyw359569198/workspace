package com.reign.framework.mongo.jdbc;

import com.reign.framework.mongo.jdbc.listener.*;
import org.springframework.beans.factory.annotation.*;
import com.reign.framework.mongo.convert.*;
import com.reign.framework.jdbc.*;
import com.reign.framework.jdbc.handlers.*;
import java.util.*;
import com.reign.framework.mongo.lang.*;
import java.sql.*;

public class MongoJdbcExtractor extends AbstractJdbcExtractor implements CommitListener
{
    private static ThreadLocal<List<MongoCallBack>> threadLocal;
    @Autowired
    protected JdbcMongoTemplate template;
    @Autowired
    protected JdbcMongoContext context;
    private DefaultConvert convert;
    
    static {
        MongoJdbcExtractor.threadLocal = new ThreadLocal<List<MongoCallBack>>() {
            @Override
            protected List<MongoCallBack> initialValue() {
                return new ArrayList<MongoCallBack>();
            }
        };
    }
    
    public MongoJdbcExtractor() {
        this.convert = new DefaultConvert();
        MongoConnection.registerListener(this);
    }
    
    public MongoJdbcExtractor(final JdbcMongoTemplate template, final JdbcMongoContext context) {
        this.convert = new DefaultConvert();
        this.template = template;
        this.context = context;
    }
    
    public int insert(final String sql, final Object obj) {
        final JdbcEntity entity = this.context.getJdbcEntity(obj.getClass());
        final List<Param> params = entity.builderInsertParams(obj);
        final int id = super.insert(sql, params, entity.getId().isGenerator());
        MongoJdbcExtractor.threadLocal.get().add(new MongoCallBack() {
            @Override
            public void execute(final JdbcMongoTemplate template) {
                entity.getId().setKey(obj, id);
                template.save(obj, entity);
                template.removeAll(entity.getQueryCollectionName());
            }
        });
        return id;
    }
    
    public <T> T read(final Class<T> clazz, final Object... keys) {
        final JdbcEntity entity = this.context.getJdbcEntity(clazz);
        T t = this.template.query(entity, entity.getId().getMongoKeyQuery(keys));
        if (t == null) {
            t = this.query(entity.getSQL(), entity.builderIdParams(keys), new BeanHandler<T>(clazz));
            if (t != null) {
                this.template.save(t, entity);
            }
        }
        return t;
    }
    
    public <T> List<T> queryList(final String sql, final List<Param> params, final Class<T> clazz, final boolean useCache) {
        if (useCache) {
            final String selectKey = this.builderSelectKey(sql, params);
            final JdbcEntity entity = this.context.getJdbcEntity(clazz);
            final DBObject dbObject = this.template.query(entity.getQueryCollectionName(), new Query().add(new Where("key", Op.eq, new Object[] { selectKey })));
            if (dbObject != null) {
                final QueryCacheObj queryCacheObj = this.convert.convert(dbObject, QueryCacheObj.class);
                final List<T> list = new ArrayList<T>();
                boolean fail = false;
                Object[] value;
                for (int length = (value = queryCacheObj.value).length, i = 0; i < length; ++i) {
                    final Object obj = value[i];
                    final T t = this.template.query(entity, entity.getId().getMongoKeyQuery(obj));
                    if (t == null) {
                        fail = true;
                        break;
                    }
                    list.add(t);
                }
                if (!fail) {
                    return list;
                }
                this.template.remove(entity.getQueryCollectionName(), new Query().add(new Where("key", Op.eq, new Object[] { selectKey })));
            }
            final List<T> list2 = this.query(sql, params, new BeanListHandler<T>(clazz));
            final Object[] obj2 = new Object[list2.size()];
            int index = 0;
            for (final T t2 : list2) {
                obj2[index++] = entity.getId().getIdValue(t2);
            }
            final DBObject temp = (DBObject)new BasicDBObject();
            temp.put("key", (Object)selectKey);
            temp.put("value", (Object)obj2);
            final QueryCacheObj queryCacheObj2 = new QueryCacheObj();
            queryCacheObj2.key = selectKey;
            queryCacheObj2.value = obj2;
            this.template.save(entity.getQueryCollectionName(), this.convert.convert(queryCacheObj2));
            this.template.save(list2, entity);
            return list2;
        }
        return this.query(sql, params, new BeanListHandler<T>(clazz));
    }
    
    private String builderSelectKey(final String sql, final List<Param> params) {
        final StringBuilder builder = new StringBuilder();
        builder.append(sql).append("::");
        for (final Param param : params) {
            builder.append(param.obj.toString()).append(",");
        }
        return builder.toString();
    }
    
    public int update(final String sql, final List<Param> params, final Class<?> clazz, final boolean clearCache) {
        final int rows = super.update(sql, params);
        MongoJdbcExtractor.threadLocal.get().add(new MongoCallBack() {
            @Override
            public void execute(final JdbcMongoTemplate template) {
                if (clearCache) {
                    template.removeAll(clazz);
                }
            }
        });
        return rows;
    }
    
    public int update(final String sql, final List<Param> params, final Class<?> clazz, final Query query, final Update update) {
        final int rows = super.update(sql, params);
        MongoJdbcExtractor.threadLocal.get().add(new MongoCallBack() {
            @Override
            public void execute(final JdbcMongoTemplate template) {
                template.update(query, update, clazz);
            }
        });
        return rows;
    }
    
    @Override
    protected Connection getConnection() throws SQLException {
        return new MongoConnection(super.getConnection());
    }
    
    @Override
    protected void releaseConnection(final Connection connection) {
        if (connection instanceof MongoConnection) {
            final MongoConnection conn = (MongoConnection)connection;
            super.releaseConnection(conn.getConnection());
        }
        else {
            super.releaseConnection(connection);
        }
    }
    
    @Override
    public void commit(final boolean succ) {
        try {
            if (succ) {
                final List<MongoCallBack> callBacks = MongoJdbcExtractor.threadLocal.get();
                for (final MongoCallBack callBack : callBacks) {
                    callBack.execute(this.template);
                }
            }
        }
        finally {
            MongoJdbcExtractor.threadLocal.remove();
        }
        MongoJdbcExtractor.threadLocal.remove();
    }
}
