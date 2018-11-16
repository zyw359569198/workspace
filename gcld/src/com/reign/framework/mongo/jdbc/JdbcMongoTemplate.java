package com.reign.framework.mongo.jdbc;

import org.springframework.beans.factory.*;
import com.reign.framework.mongo.convert.*;
import java.util.concurrent.*;
import com.reign.framework.mongo.util.*;
import java.util.*;
import com.reign.framework.mongo.lang.*;
import java.net.*;

public class JdbcMongoTemplate implements JdbcMongoOperations, InitializingBean
{
    private DB db;
    private AbstractConvert convert;
    private String dbName;
    private ConcurrentMap<String, WrapperDBCollection> collectionMap;
    
    public JdbcMongoTemplate() {
        this.convert = new DefaultConvert();
        this.collectionMap = new ConcurrentHashMap<String, WrapperDBCollection>();
    }
    
    public JdbcMongoTemplate(final DB db) {
        this.convert = new DefaultConvert();
        this.collectionMap = new ConcurrentHashMap<String, WrapperDBCollection>();
        this.db = db;
    }
    
    public void setConvert(final AbstractConvert convert) {
        this.convert = convert;
    }
    
    public void setDbName(final String dbName) {
        this.dbName = dbName;
    }
    
    @Override
	public void save(final Object obj) {
        final WrapperDBCollection dbCollection = this.getDBCollection(obj);
        try {
            dbCollection.getWriteLock().lock();
            dbCollection.getDbCollection().save(this.convert.convert(obj));
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
        dbCollection.getWriteLock().unlock();
    }
    
    @Override
	public <E> void save(final List<E> objList, final Class<E> clazz) {
        final DBObject[] array = (DBObject[])new BasicDBObject[objList.size()];
        for (int i = 0; i < objList.size(); ++i) {
            array[i] = this.convert.convert(objList.get(i));
        }
        final WrapperDBCollection dbCollection = this.getWrapperDBCollection(clazz);
        try {
            dbCollection.getWriteLock().lock();
            dbCollection.getDbCollection().insert(array);
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
        dbCollection.getWriteLock().unlock();
    }
    
    @Override
    public void save(final Object obj, final JdbcEntity entity) {
        final WrapperDBCollection dbCollection = entity.getDBCollection(this.db);
        try {
            dbCollection.getWriteLock().lock();
            dbCollection.getDbCollection().save(this.convert.convert(obj));
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
        dbCollection.getWriteLock().unlock();
    }
    
    @Override
    public <E> void save(final List<E> objList, final JdbcEntity entity) {
        final DBObject[] array = (DBObject[])new BasicDBObject[objList.size()];
        for (int i = 0; i < objList.size(); ++i) {
            array[i] = this.convert.convert(objList.get(i));
        }
        final WrapperDBCollection dbCollection = entity.getDBCollection(this.db);
        try {
            dbCollection.getWriteLock().lock();
            dbCollection.getDbCollection().insert(array);
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
        dbCollection.getWriteLock().unlock();
    }
    
    private synchronized WrapperDBCollection _getDBCollection(final String collectionName, final boolean create) {
        WrapperDBCollection dbCollection = this.collectionMap.get(collectionName);
        if (dbCollection == null && create) {
            final DBCollection collection = this.db.getCollection(collectionName);
            dbCollection = new WrapperDBCollection(collection);
            this.collectionMap.put(collectionName, dbCollection);
        }
        return dbCollection;
    }
    
    private WrapperDBCollection getDBCollection(final Object obj) {
        final String collectionName = MongoDBUtil.getCollectionName(obj.getClass());
        return this._getDBCollection(collectionName, true);
    }
    
    public void remove(final ObjectId id, final Class<?> clazz) {
        final DBObject dbObject = (DBObject)new BasicDBObject();
        dbObject.put("_id", (Object)id);
        final WrapperDBCollection dbCollection = this.getWrapperDBCollection(clazz);
        try {
            dbCollection.getWriteLock().lock();
            dbCollection.getDbCollection().remove(dbObject);
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
        dbCollection.getWriteLock().unlock();
    }
    
    @Override
	public void remove(final Object obj) {
        final WrapperDBCollection dbCollection = this.getDBCollection(obj);
        try {
            dbCollection.getWriteLock().lock();
            dbCollection.getDbCollection().remove(this.convert.convert(obj));
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
        dbCollection.getWriteLock().unlock();
    }
    
    @Override
	public void removeAll(final Class<?> clazz) {
        final WrapperDBCollection dbCollection = this.getWrapperDBCollection(clazz, false);
        if (dbCollection == null) {
            return;
        }
        try {
            dbCollection.getWriteLock().lock();
            dbCollection.getDbCollection().drop();
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
        dbCollection.getWriteLock().unlock();
    }
    
    @Override
    public void remove(final Object obj, final JdbcEntity entity) {
        final WrapperDBCollection dbCollection = entity.getDBCollection(this.db);
        try {
            dbCollection.getWriteLock().lock();
            dbCollection.getDbCollection().remove(this.convert.convert(obj));
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
        dbCollection.getWriteLock().unlock();
    }
    
    @Override
    public void removeAll(final JdbcEntity entity) {
        final WrapperDBCollection dbCollection = entity.getDBCollection(this.db);
        try {
            dbCollection.getWriteLock().lock();
            dbCollection.getDbCollection().drop();
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
        dbCollection.getWriteLock().unlock();
    }
    
    @Override
	public <E> E query(final Query query, final Class<E> clazz) {
        final WrapperDBCollection dbCollection = this.getWrapperDBCollection(clazz);
        try {
            dbCollection.getReadLock().lock();
            final DBObject dbObject = dbCollection.getDbCollection().findOne(QueryParse.parse(query));
            return this.convert.convert(dbObject, clazz);
        }
        finally {
            dbCollection.getReadLock().unlock();
        }
    }
    
    @Override
	public <E> List<E> queryList(final Query query, final Class<E> clazz) {
        final WrapperDBCollection dbCollection = this.getWrapperDBCollection(clazz);
        try {
            dbCollection.getReadLock().lock();
            final DBCursor cursor = dbCollection.getDbCollection().find(QueryParse.parse(query));
            final List<E> resultList = new ArrayList<E>();
            while (cursor.hasNext()) {
                final DBObject dbObject = cursor.next();
                resultList.add(this.convert.convert(dbObject, clazz));
            }
            return resultList;
        }
        finally {
            dbCollection.getReadLock().unlock();
        }
    }
    
    @Override
	public <E> List<E> queryList(final Query query, final OrderBy orderBy, final Class<E> clazz) {
        final WrapperDBCollection dbCollection = this.getWrapperDBCollection(clazz);
        try {
            dbCollection.getReadLock().lock();
            final DBCursor cursor = dbCollection.getDbCollection().find(QueryParse.parse(query)).addSpecial(SpecialOp.orderby.getValue(), (Object)orderBy.toDBObject());
            final List<E> resultList = new ArrayList<E>();
            while (cursor.hasNext()) {
                final DBObject dbObject = cursor.next();
                resultList.add(this.convert.convert(dbObject, clazz));
            }
            return resultList;
        }
        finally {
            dbCollection.getReadLock().unlock();
        }
    }
    
    @Override
	public <E> List<E> queryList(final Query query, final Special special, final Class<E> clazz) {
        final WrapperDBCollection dbCollection = this.getWrapperDBCollection(clazz);
        try {
            dbCollection.getReadLock().lock();
            final DBCursor cursor = dbCollection.getDbCollection().find(QueryParse.parse(query)).addSpecial(special.op.getValue(), special.value);
            final List<E> resultList = new ArrayList<E>();
            while (cursor.hasNext()) {
                final DBObject dbObject = cursor.next();
                resultList.add(this.convert.convert(dbObject, clazz));
            }
            return resultList;
        }
        finally {
            dbCollection.getReadLock().unlock();
        }
    }
    
    @Override
    public <E> E query(final JdbcEntity entity, final Query query) {
        final WrapperDBCollection dbCollection = entity.getDBCollection(this.db);
        try {
            dbCollection.getReadLock().lock();
            final DBObject dbObject = dbCollection.getDbCollection().findOne(QueryParse.parse(query));
            return this.convert.convert(dbObject, entity.getEntityClass());
        }
        finally {
            dbCollection.getReadLock().unlock();
        }
    }
    
    @Override
    public <E> List<E> queryList(final JdbcEntity entity, final Query query) {
        final WrapperDBCollection dbCollection = entity.getDBCollection(this.db);
        try {
            dbCollection.getReadLock().lock();
            final DBCursor cursor = dbCollection.getDbCollection().find(QueryParse.parse(query));
            final List<E> resultList = new ArrayList<E>();
            while (cursor.hasNext()) {
                final DBObject dbObject = cursor.next();
                resultList.add(this.convert.convert(dbObject, entity.getEntityClass()));
            }
            return resultList;
        }
        finally {
            dbCollection.getReadLock().unlock();
        }
    }
    
    @Override
    public <E> List<E> queryList(final JdbcEntity entity, final Query query, final OrderBy orderBy) {
        final WrapperDBCollection dbCollection = entity.getDBCollection(this.db);
        try {
            dbCollection.getReadLock().lock();
            final DBCursor cursor = dbCollection.getDbCollection().find(QueryParse.parse(query)).addSpecial(SpecialOp.orderby.getValue(), (Object)orderBy.toDBObject());
            final List<E> resultList = new ArrayList<E>();
            while (cursor.hasNext()) {
                final DBObject dbObject = cursor.next();
                resultList.add(this.convert.convert(dbObject, entity.getEntityClass()));
            }
            return resultList;
        }
        finally {
            dbCollection.getReadLock().unlock();
        }
    }
    
    @Override
    public <E> List<E> queryList(final JdbcEntity entity, final Query query, final Special special) {
        final WrapperDBCollection dbCollection = entity.getDBCollection(this.db);
        try {
            dbCollection.getReadLock().lock();
            final DBCursor cursor = dbCollection.getDbCollection().find(QueryParse.parse(query)).addSpecial(special.op.getValue(), special.value);
            final List<E> resultList = new ArrayList<E>();
            while (cursor.hasNext()) {
                final DBObject dbObject = cursor.next();
                resultList.add(this.convert.convert(dbObject, entity.getEntityClass()));
            }
            return resultList;
        }
        finally {
            dbCollection.getReadLock().unlock();
        }
    }
    
    @Override
	public int update(final Query query, final Update update, final Class<?> clazz) {
        final WrapperDBCollection dbCollection = this.getWrapperDBCollection(clazz);
        try {
            dbCollection.getWriteLock().lock();
            dbCollection.getDbCollection().update(QueryParse.parse(query), UpdateParse.parse(update), false, true);
            return -1;
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
    }
    
    @Override
	public int update(final Query query, final Object obj, final Class<?> clazz) {
        final WrapperDBCollection dbCollection = this.getWrapperDBCollection(clazz);
        try {
            dbCollection.getWriteLock().lock();
            dbCollection.getDbCollection().update(QueryParse.parse(query), this.convert.convert(obj), false, false);
            return -1;
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
    }
    
    @Override
    public void update(final JdbcEntity entity, final Query query, final Update update) {
        final WrapperDBCollection dbCollection = entity.getDBCollection(this.db);
        try {
            dbCollection.getWriteLock().lock();
            dbCollection.getDbCollection().update(QueryParse.parse(query), UpdateParse.parse(update), false, true);
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
        dbCollection.getWriteLock().unlock();
    }
    
    @Override
    public void update(final JdbcEntity entity, final Query query, final Object obj) {
        final WrapperDBCollection dbCollection = entity.getDBCollection(this.db);
        try {
            dbCollection.getWriteLock().lock();
            dbCollection.getDbCollection().update(QueryParse.parse(query), this.convert.convert(obj), false, false);
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
        dbCollection.getWriteLock().unlock();
    }
    
    @Override
    public void save(final String collectionName, final DBObject dbObject) {
        final WrapperDBCollection dbCollection = this._getDBCollection(collectionName, true);
        try {
            dbCollection.getWriteLock().lock();
            dbCollection.getDbCollection().save(dbObject);
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
        dbCollection.getWriteLock().unlock();
    }
    
    @Override
    public void remove(final String collectionName, final Query query) {
        final WrapperDBCollection dbCollection = this._getDBCollection(collectionName, true);
        try {
            dbCollection.getWriteLock().lock();
            dbCollection.getDbCollection().remove(QueryParse.parse(query));
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
        dbCollection.getWriteLock().unlock();
    }
    
    @Override
    public void removeAll(final String collectionName) {
        final WrapperDBCollection dbCollection = this._getDBCollection(collectionName, false);
        if (dbCollection == null) {
            return;
        }
        try {
            dbCollection.getReadLock().lock();
            dbCollection.getDbCollection().drop();
        }
        finally {
            dbCollection.getReadLock().unlock();
        }
        dbCollection.getReadLock().unlock();
    }
    
    @Override
    public DBObject query(final String collectionName, final Query query) {
        final WrapperDBCollection dbCollection = this._getDBCollection(collectionName, true);
        try {
            dbCollection.getWriteLock().lock();
            return dbCollection.getDbCollection().findOne(QueryParse.parse(query));
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
    }
    
    public void save(final DBObject dbObject, final DBCollection dbCollection) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    public <E> void save(final List<E> objList, final DBCollection dbCollection) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    public void remove(final ObjectId id, final DBCollection dbCollection) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    public void remove(final Object obj, final DBCollection dbCollection) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    public void remove(final DBObject dbObject, final Class<?> clazz) {
        final WrapperDBCollection dbCollection = this.getWrapperDBCollection(clazz);
        try {
            dbCollection.getWriteLock().lock();
            dbCollection.getDbCollection().remove(dbObject);
        }
        finally {
            dbCollection.getWriteLock().unlock();
        }
        dbCollection.getWriteLock().unlock();
    }
    
    public void remove(final DBObject dbObject, final DBCollection dbCollection) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    public void removeAll(final DBCollection dbCollection) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    public DBCursor query(final Query query, final DBCollection dbCollection) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    public DBCursor query(final Query query, final OrderBy orderBy, final DBCollection dbCollection) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    public DBCursor query(final Query query, final Special special, final DBCollection dbCollection) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    public int update(final Query query, final Update update, final DBCollection dbCollection) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    public int update(final Query query, final Object obj, final DBCollection dbCollection) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    @Override
	public DBCollection getDBCollection(final Class<?> clazz) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    @Override
	public DBCollection getDBCollection(final Class<?> clazz, final boolean create) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    @Override
	public DBCollection getDBCollection(final String collectionName) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    @Override
	public DBCollection getDBCollection(final String collectionName, final boolean create) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    public void save(final DBObject[] dbObject, final DBCollection dbCollection) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    public int update(final DBObject query, final DBObject update, final boolean upsert, final boolean multi, final DBCollection dbCollection) {
        throw new UnsupportedOperationException("unsupport this operation");
    }
    
    @Override
    public WrapperDBCollection getWrapperDBCollection(final Class<?> clazz) {
        final String collectionName = MongoDBUtil.getCollectionName(clazz);
        return this._getDBCollection(collectionName, true);
    }
    
    @Override
    public WrapperDBCollection getWrapperDBCollection(final Class<?> clazz, final boolean create) {
        final String collectionName = MongoDBUtil.getCollectionName(clazz);
        return this._getDBCollection(collectionName, create);
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        try {
            final Mongo mongo = new Mongo();
            this.db = mongo.getDB(this.dbName);
        }
        catch (UnknownHostException e) {
            e.printStackTrace();
        }
        catch (MongoException e2) {
            e2.printStackTrace();
        }
    }
}
