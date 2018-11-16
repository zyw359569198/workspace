package com.reign.framework.jdbc.orm;

import java.util.*;
import com.reign.framework.jdbc.*;

public class CommonDao extends AbstractJdbcExtractor implements BaseJdbcExtractor
{
    @Override
    public <T, PK> T read(final PK pk, final JdbcEntity entity, final ResultSetHandler<T> handler) {
        return this.query(entity.getSelectSQL(false), entity.builderIdParams(pk), handler);
    }
    
    @Override
    public <T, PK> void insert(final T newInstance, final JdbcEntity entity, final String... keys) {
        final int result = this.insert(entity.getInsertSQL(), entity.builderInsertParams(newInstance), entity.isAutoGenerator());
        if (entity.isAutoGenerator()) {
            entity.getId().setKey(newInstance, result);
        }
    }
    
    @Override
    public <T> void update(final T transientObject, final JdbcEntity entity) {
        this.update(entity.getUpdateSQL(), entity.builderUpdateParams(transientObject));
    }
    
    @Override
    public <PK> void delete(final PK id, final JdbcEntity entity) {
        this.update(entity.getDeleteSQL(), entity.builderIdParams(id));
    }
    
    @Override
    public <T> List<T> query(final String selectKey, final String sql, final List<Param> params, final JdbcEntity entity, final ResultSetHandler<List<T>> handler) {
        return this.query(sql, params, handler);
    }
    
    @Override
    public <T> T query(final String sql, final List<Param> params, final ResultSetHandler<T> handler) {
        return super.query(sql, params, handler);
    }
    
    @Override
    public <PK> void updateDelay(final String sql, final List<Param> params, final JdbcEntity entity, final PK pk, final String... keys) {
        this.update(sql, params, entity, pk, keys);
    }
    
    @Override
    public <PK> int update(final String sql, final List<Param> params, final JdbcEntity entity, final PK pk, final String... keys) {
        return this.update(sql, params);
    }
    
    @Override
    public void batch(final String sql, final List<List<Param>> paramsList, final JdbcEntity entity, final String... keys) {
        this.batch(sql, paramsList);
    }
    
    @Override
    public boolean callProcedure(final String sql, final List<Param> params, final JdbcEntity entity, final String... keys) {
        return this.callProcedure(sql, params);
    }
    
    @Override
    public List<Object> callProcedureWithReturn(final String sql, final List<Param> params, final JdbcEntity entity, final String... keys) {
        return this.callProcedureWithReturn(sql, params);
    }
}
