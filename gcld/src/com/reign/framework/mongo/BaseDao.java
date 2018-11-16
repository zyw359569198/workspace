package com.reign.framework.mongo;

import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import com.reign.framework.mongo.lang.*;
import com.reign.framework.mongo.page.*;

public class BaseDao<E> implements IBaseDao<E>, InitializingBean
{
    @Autowired
    private MongoTemplate template;
    @Autowired
    private MongoContext mongoContext;
    private Class<E> clazz;
    private MongoEntity mongoEntity;
    
    public BaseDao() {
        try {
            this.clazz = (Class<E>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        }
        catch (Exception e) {
            this.clazz = (Class<E>)this.getClass().getGenericSuperclass();
        }
    }
    
    @Override
    public void save(final E e) {
        this.template.save(e, this.mongoEntity);
    }
    
    @Override
    public void saveList(final List<E> list) {
        this.template.save(list, this.mongoEntity);
    }
    
    @Override
    public int update(final E e) {
        this.template.save(e, this.mongoEntity);
        return 1;
    }
    
    @Override
    public int update(final Update update) {
        return this.template.update(update, this.mongoEntity);
    }
    
    @Override
    public int update(final Query query, final Update update) {
        return this.template.update(query, update, this.mongoEntity);
    }
    
    @Override
    public void delete(final E e) {
        this.delete(this.mongoEntity.getId().getMongoKeyQueryByObject(e));
    }
    
    @Override
    public void delete(final Query query) {
        this.template.remove(query, this.mongoEntity);
    }
    
    @Override
    public void deleteAll() {
        this.template.removeAll(this.mongoEntity);
    }
    
    @Override
    public E read(final Object... keys) {
        return this.template.query(this.mongoEntity.getId().getMongoKeyQuery(keys), this.mongoEntity);
    }
    
    @Override
    public List<E> readAll() {
        return this.template.readAll(this.mongoEntity);
    }
    
    @Override
    public int count(final Query query) {
        return this.template.count(query, this.mongoEntity);
    }
    
    @Override
    public List<E> selectList(final Query query) {
        return this.template.queryList(query, this.mongoEntity);
    }
    
    @Override
    public List<E> selectList(final Query query, final OrderBy orderBy) {
        return this.template.queryList(query, orderBy, this.mongoEntity);
    }
    
    @Override
    public List<E> selectList(final Query query, final PagingData pagingData) {
        final int count = this.count(query);
        pagingData.setRowsCount(count);
        pagingData.setPagesCount();
        return this.selectListNoUpdate(query, pagingData);
    }
    
    @Override
    public List<E> selectListNoUpdate(final Query query, final PagingData pagingData) {
        return this.template.queryList(query, pagingData.getCurrentPage() * pagingData.getRowsPerPage(), pagingData.getRowsPerPage(), this.mongoEntity);
    }
    
    @Override
    public List<E> selectList(final Query query, final OrderBy orderBy, final PagingData pagingData) {
        final int count = this.count(query);
        pagingData.setRowsCount(count);
        pagingData.setPagesCount();
        return this.selectListNoUpdate(query, orderBy, pagingData);
    }
    
    @Override
    public List<E> selectListNoUpdate(final Query query, final OrderBy orderBy, final PagingData pagingData) {
        return this.template.queryList(query, orderBy, pagingData.getCurrentPage() * pagingData.getRowsPerPage(), pagingData.getRowsPerPage(), this.mongoEntity);
    }
    
    @Override
    public MongoTemplate getMongoTemplate() {
        return this.template;
    }
    
    @Override
    public DBCollection getDBCollection() {
        return this.template.getDBCollection(this.mongoEntity);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        this.mongoEntity = this.mongoContext.getMongoEntity(this.clazz);
        if (this.mongoEntity == null) {
            throw new RuntimeException("this class is not a mongo entity, class " + this.clazz.getName());
        }
    }
    
    @Override
    public MongoLock getWriteLock(final Object... keys) {
        this.mongoEntity = this.mongoContext.getMongoEntity(this.clazz);
        return this.mongoEntity.getWriteLock(keys);
    }
    
    @Override
    public MongoLock getReadLock(final Object... keys) {
        this.mongoEntity = this.mongoContext.getMongoEntity(this.clazz);
        return this.mongoEntity.getReadLock(keys);
    }
}
