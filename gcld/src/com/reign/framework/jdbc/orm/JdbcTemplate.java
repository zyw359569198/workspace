package com.reign.framework.jdbc.orm;

import com.reign.framework.jdbc.*;
import java.util.*;
import com.reign.framework.jdbc.orm.session.*;

public class JdbcTemplate implements BaseJdbcExtractor
{
    private JdbcFactory jdbcFactory;
    
    public JdbcFactory getJdbcFactory() {
        return this.jdbcFactory;
    }
    
    public void setJdbcFactory(final JdbcFactory jdbcFactory) {
        this.jdbcFactory = jdbcFactory;
    }
    
    @Override
    public <T> T query(final String sql, final List<Param> params, final ResultSetHandler<T> handler) {
        return this.doExecute((JdbcCallBack<T>)new JdbcCallBack<T>() {
            @Override
            public T doInJdbcSession(final JdbcSession session) {
                return session.query(sql, params, handler);
            }
        });
    }
    
    @Override
    public List<Map<String, Object>> query(final String sql, final List<Param> params) {
        return this.doExecute((JdbcCallBack<List<Map<String, Object>>>)new JdbcCallBack<List<Map<String, Object>>>() {
            @Override
            public List<Map<String, Object>> doInJdbcSession(final JdbcSession session) {
                return session.query(sql, params);
            }
        });
    }
    
    @Override
    public int update(final String sql, final List<Param> params) {
        return this.doExecute((JdbcCallBack<Integer>)new JdbcCallBack<Integer>() {
            @Override
            public Integer doInJdbcSession(final JdbcSession session) {
                return session.update(sql, params);
            }
        });
    }
    
    @Override
    public int insert(final String sql, final List<Param> params, final boolean autoGenerator) {
        return this.doExecute((JdbcCallBack<Integer>)new JdbcCallBack<Integer>() {
            @Override
            public Integer doInJdbcSession(final JdbcSession session) {
                return session.insert(sql, params, autoGenerator);
            }
        });
    }
    
    @Override
    public void batch(final String sql, final List<List<Param>> paramsList) {
        this.doExecute((JdbcCallBack<Object>)new JdbcCallBack<Object>() {
            @Override
            public Object doInJdbcSession(final JdbcSession session) {
                session.batch(sql, paramsList);
                return null;
            }
        });
    }
    
    @Override
    public boolean callProcedure(final String sql, final List<Param> params) {
        return this.doExecute((JdbcCallBack<Boolean>)new JdbcCallBack<Boolean>() {
            @Override
            public Boolean doInJdbcSession(final JdbcSession session) {
                return session.callProcedure(sql, params);
            }
        });
    }
    
    @Override
    public List<Object> callProcedureWithReturn(final String sql, final List<Param> params) {
        return this.doExecute((JdbcCallBack<List<Object>>)new JdbcCallBack<List<Object>>() {
            @Override
            public List<Object> doInJdbcSession(final JdbcSession session) {
                return session.callProcedureWithReturn(sql, params);
            }
        });
    }
    
    @Override
    public List<Map<String, Object>> callQueryProcedure(final String sql, final List<Param> params) {
        return this.doExecute((JdbcCallBack<List<Map<String, Object>>>)new JdbcCallBack<List<Map<String, Object>>>() {
            @Override
            public List<Map<String, Object>> doInJdbcSession(final JdbcSession session) {
                return session.callQueryProcedure(sql, params);
            }
        });
    }
    
    @Override
    public <T, PK> T read(final PK pk, final JdbcEntity entity, final ResultSetHandler<T> handler) {
        return this.doExecute((JdbcCallBack<T>)new JdbcCallBack<T>() {
            @Override
            public T doInJdbcSession(final JdbcSession session) {
                return session.read(pk, entity, handler);
            }
        });
    }
    
    @Override
    public <T, PK> void insert(final T newInstance, final JdbcEntity entity, final String... keys) {
        this.doExecute((JdbcCallBack<Object>)new JdbcCallBack<Object>() {
            @Override
            public Object doInJdbcSession(final JdbcSession session) {
                session.insert(newInstance, entity, keys);
                return null;
            }
        });
    }
    
    @Override
    public <T> void update(final T transientObject, final JdbcEntity entity) {
        this.doExecute((JdbcCallBack<Object>)new JdbcCallBack<Object>() {
            @Override
            public Object doInJdbcSession(final JdbcSession session) {
                session.update(transientObject, entity);
                return null;
            }
        });
    }
    
    @Override
    public <PK> void delete(final PK id, final JdbcEntity entity) {
        this.doExecute((JdbcCallBack<Object>)new JdbcCallBack<Object>() {
            @Override
            public Object doInJdbcSession(final JdbcSession session) {
                session.delete(id, entity);
                return null;
            }
        });
    }
    
    @Override
    public <T> List<T> query(final String selectKey, final String sql, final List<Param> params, final JdbcEntity entity, final ResultSetHandler<List<T>> handler) {
        return this.doExecute((JdbcCallBack<List<T>>)new JdbcCallBack<List<T>>() {
            @Override
            public List<T> doInJdbcSession(final JdbcSession session) {
                return session.query(selectKey, sql, params, entity, handler);
            }
        });
    }
    
    protected <T> T doExecute(final JdbcCallBack<T> action) {
        final JdbcSession session = this.getSession();
        final boolean existingTransaction = JdbcSessionUtil.isSessionTransactional(session, this.getJdbcFactory());
        try {
            final T result = action.doInJdbcSession(session);
            return result;
        }
        catch (RuntimeException ex) {
            throw ex;
        }
        finally {
            if (!existingTransaction) {
                session.close();
            }
        }
    }
    
    protected JdbcSession getSession() {
        return JdbcSessionUtil.getSession(this.jdbcFactory, true);
    }
    
    @Override
    public <PK> int update(final String sql, final List<Param> params, final JdbcEntity entity, final PK pk, final String... keys) {
        return this.doExecute((JdbcCallBack<Integer>)new JdbcCallBack<Integer>() {
            @Override
            public Integer doInJdbcSession(final JdbcSession session) {
                return session.update(sql, params, entity, pk, keys);
            }
        });
    }
    
    @Override
    public <PK> void updateDelay(final String sql, final List<Param> params, final JdbcEntity entity, final PK pk, final String... keys) {
        this.doExecute((JdbcCallBack<Object>)new JdbcCallBack<Object>() {
            @Override
            public Object doInJdbcSession(final JdbcSession session) {
                session.updateDelay(sql, params, entity, pk, keys);
                return null;
            }
        });
    }
    
    @Override
    public void batch(final String sql, final List<List<Param>> paramsList, final JdbcEntity entity, final String... keys) {
        this.doExecute((JdbcCallBack<Object>)new JdbcCallBack<Object>() {
            @Override
            public Object doInJdbcSession(final JdbcSession session) {
                session.batch(sql, paramsList, entity, keys);
                return null;
            }
        });
    }
    
    @Override
    public boolean callProcedure(final String sql, final List<Param> params, final JdbcEntity entity, final String... keys) {
        return this.doExecute((JdbcCallBack<Boolean>)new JdbcCallBack<Boolean>() {
            @Override
            public Boolean doInJdbcSession(final JdbcSession session) {
                return session.callProcedure(sql, params, entity, keys);
            }
        });
    }
    
    @Override
    public List<Object> callProcedureWithReturn(final String sql, final List<Param> params, final JdbcEntity entity, final String... keys) {
        return this.doExecute((JdbcCallBack<List<Object>>)new JdbcCallBack<List<Object>>() {
            @Override
            public List<Object> doInJdbcSession(final JdbcSession session) {
                return session.callProcedureWithReturn(sql, params, entity, keys);
            }
        });
    }
}
