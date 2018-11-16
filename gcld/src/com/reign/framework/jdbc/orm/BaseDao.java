package com.reign.framework.jdbc.orm;

import java.io.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.*;
import java.lang.reflect.*;
import com.reign.framework.jdbc.orm.page.*;
import com.reign.framework.jdbc.*;
import java.util.*;
import com.reign.framework.jdbc.handlers.*;

public class BaseDao<T extends JdbcModel, PK extends Serializable> implements IBaseDao<T, PK>, InitializingBean
{
    private Class<T> clazz;
    private JdbcEntity entity;
    private ResultSetHandler<T> handler;
    private ResultSetHandler<List<T>> listHandler;
    private ColumnListHandler columnListHandler;
    @Autowired
    private JdbcFactory jdbcFactory;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private SqlFactory factory;
    
    public BaseDao() {
        try {
            this.clazz = (Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        catch (Exception e) {
            this.clazz = (Class<T>)this.getClass().getGenericSuperclass();
        }
    }
    
    @Override
    public void create(final T newInstance) {
        this.jdbcTemplate.insert(newInstance, this.entity, "all");
    }
    
    @Override
    public void create(final T newInstance, final String... keys) {
        this.jdbcTemplate.insert(newInstance, this.entity, keys);
    }
    
    @Override
    public T read(final PK id) {
        return this.jdbcTemplate.read(id, this.entity, this.handler);
    }
    
    @Override
    public T readForUpdate(final PK id) {
        return this.jdbcTemplate.query(this.entity.getSelectSQL(true), this.entity.builderIdParams(id), this.handler);
    }
    
    @Override
    public void update(final T transientObject) {
        this.jdbcTemplate.update(transientObject, this.entity);
    }
    
    @Override
    public void update(final T transientObject, final String... keys) {
        this.jdbcTemplate.update(this.entity.getUpdateSQL(), this.entity.builderUpdateParams(transientObject), this.entity, this.entity.getId().getIdValue(transientObject), keys);
    }
    
    @Override
    public void delete(final PK id) {
        this.jdbcTemplate.delete(id, this.entity);
    }
    
    @Override
    public List<T> getModels() {
        return this.jdbcTemplate.query(this.entity.getSelectAllSQL(), this.entity.getSelectAllSQL(), Params.EMPTY, this.entity, this.listHandler);
    }
    
    @Override
    public Long getModelSize() {
        final List<Object> resultList = this.jdbcTemplate.query(this.entity.getSelectAllCountSQL(), Params.EMPTY, (ResultSetHandler<List<Object>>)this.columnListHandler);
        if (resultList.size() > 0) {
            return resultList.get(0);
        }
        return 0L;
    }
    
    @Override
    public T getFirstResultByHQLAndParam(final String sql) {
        final List<T> resultList = this.jdbcTemplate.query(sql, sql, Params.EMPTY, this.entity, this.listHandler);
        if (resultList.size() > 0) {
            return resultList.get(0);
        }
        return null;
    }
    
    @Override
    public T getFirstResultByHQLAndParam(final String sql, final Params params) {
        final List<T> resultList = this.jdbcTemplate.query(sql, sql, params, this.entity, this.listHandler);
        if (resultList.size() > 0) {
            return resultList.get(0);
        }
        return null;
    }
    
    @Override
    public List<T> getResultByHQLAndParam(final String sql) {
        return this.jdbcTemplate.query(sql, sql, Params.EMPTY, this.entity, this.listHandler);
    }
    
    @Override
    public List<T> getResultByHQLAndParam(final String sql, final Params params) {
        return this.jdbcTemplate.query(sql, sql, params, this.entity, this.listHandler);
    }
    
    @Override
    public List<T> getResultByHQLAndParam(String sql, final PagingData page, final Params params) {
        final String temp = this.factory.get(sql);
        final String selectKey = sql;
        if (temp != null) {
            sql = temp.trim();
        }
        final String countSQL = this.getCountSQL(sql);
        final long count = this.getCount(countSQL, params);
        page.setRowsCount((int)count);
        page.setPagesCount();
        sql = String.valueOf(sql) + " LIMIT ?, ?";
        params.addParam(page.getCurrentPage() * page.getRowsPerPage(), Type.Int);
        params.addParam(page.getRowsPerPage(), Type.Int);
        return this.jdbcTemplate.query(selectKey, sql, params, this.entity, this.listHandler);
    }
    
    @Override
    public void update(final String sql, final Params params) {
        this.update(sql, params, true);
    }
    
    @Override
    public int update(final String sql, final Params params, final boolean canDelay) {
        return this.update(sql, params, canDelay, null, "all");
    }
    
    @Override
    public int update(final String sql, final Params params, final boolean canDelay, final PK pk, final String... keys) {
        if (canDelay) {
            this.jdbcTemplate.updateDelay(sql, params, this.entity, pk, keys);
            return 0;
        }
        return this.jdbcTemplate.update(sql, params, this.entity, pk, keys);
    }
    
    @Override
    public void update(final String sql, final Params params, final PK pk, final String... keys) {
        this.update(sql, params, true, pk, keys);
    }
    
    @Override
    public void batch(final String sql, final List<List<Param>> paramsList, final String... keys) {
        this.jdbcTemplate.batch(sql, paramsList, this.entity, keys);
    }
    
    @Override
    public boolean callProcedure(final String sql, final List<Param> params, final String... keys) {
        return this.jdbcTemplate.callProcedure(sql, params, this.entity, keys);
    }
    
    @Override
    public List<Object> callProcedureWithReturn(final String sql, final List<Param> params, final String... keys) {
        return this.jdbcTemplate.callProcedureWithReturn(sql, params, this.entity, keys);
    }
    
    @Override
    public long count(final String sql, final Params params) {
        return this.getCount(sql, params);
    }
    
    @Override
    public void batch(final String sql, final List<List<Param>> paramsList) {
        this.jdbcTemplate.batch(sql, paramsList, this.entity, "all");
    }
    
    @Override
    public boolean callProcedure(final String sql, final List<Param> params) {
        return this.jdbcTemplate.callProcedure(sql, params, this.entity, "all");
    }
    
    @Override
    public List<Object> callProcedureWithReturn(final String sql, final List<Param> params) {
        return this.jdbcTemplate.callProcedureWithReturn(sql, params, this.entity, "all");
    }
    
    @Override
    public List<Map<String, Object>> callQueryProcedure(final String sql, final List<Param> params) {
        return this.jdbcTemplate.callQueryProcedure(sql, params);
    }
    
    @Override
    public List<Map<String, Object>> query(final String sql, final List<Param> params) {
        return this.jdbcTemplate.query(sql, params);
    }
    
    @Override
    public List<Map<String, Object>> query(String sql, final PagingData page, final Params params) {
        final String temp = this.factory.get(sql);
        if (temp != null) {
            sql = temp.trim();
        }
        final String countSQL = this.getCountSQL(sql);
        final long count = this.getCount(countSQL, params);
        page.setRowsCount((int)count);
        page.setPagesCount();
        sql = String.valueOf(sql) + " LIMIT ?, ?";
        params.addParam(page.getCurrentPage() * page.getRowsPerPage(), Type.Int);
        params.addParam(page.getRowsPerPage(), Type.Int);
        return this.jdbcTemplate.query(sql, params);
    }
    
    @Override
    public <E> E query(final String sql, final List<Param> params, final ResultSetHandler<E> handler) {
        return this.jdbcTemplate.query(sql, params, handler);
    }
    
    private long getCount(final String sql, final Params params) {
        final List<Object> resultList = this.jdbcTemplate.query(sql, params, (ResultSetHandler<List<Object>>)this.columnListHandler);
        if (resultList.size() > 0) {
            return resultList.get(0);
        }
        return 0L;
    }
    
    private String getCountSQL(final String sql) {
        return "SELECT COUNT(1) " + sql.toUpperCase().substring(sql.indexOf("FROM"));
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.entity = this.jdbcFactory.getJdbcEntity(this.clazz);
        this.handler = new BeanHandler<T>(this.clazz);
        this.listHandler = new BeanListHandler<T>(this.clazz);
        this.columnListHandler = new ColumnListHandler(1);
    }
}
