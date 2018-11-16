package com.reign.framework.mongo;

import org.apache.commons.logging.*;
import com.reign.framework.mongo.convert.*;
import java.util.concurrent.*;
import java.util.*;
import com.reign.framework.mongo.lang.*;
import com.reign.framework.mongo.util.*;

public class MongoTemplate implements MongoOperations, MongoEntityOperations
{
    private static final Log log;
    private DB db;
    private AbstractConvert convert;
    private ConcurrentMap<String, DBCollection> collectionMap;
    private WriteConcernResolver writeConcernResolver;
    private boolean errorThrow;
    
    static {
        log = LogFactory.getLog(MongoTemplate.class);
    }
    
    public MongoTemplate() {
        this.convert = new DefaultConvert();
        this.collectionMap = new ConcurrentHashMap<String, DBCollection>();
        this.writeConcernResolver = new DefaultWriteConcernResolver();
        try {
            final Properties properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream("mongo.properties"));
            final String host = properties.getProperty("mongo.host");
            final int port = Integer.valueOf(properties.getProperty("mongo.port"));
            final String dbName = properties.getProperty("mongo.db");
            final Mongo mongo = new Mongo(host, port);
            this.db = mongo.getDB(dbName);
        }
        catch (Exception e) {
            throw new RuntimeException("init mongotemplate error", e);
        }
    }
    
    public MongoTemplate(final DB db) {
        this.convert = new DefaultConvert();
        this.collectionMap = new ConcurrentHashMap<String, DBCollection>();
        this.writeConcernResolver = new DefaultWriteConcernResolver();
        this.db = db;
    }
    
    public MongoTemplate(final String dbName) {
        this.convert = new DefaultConvert();
        this.collectionMap = new ConcurrentHashMap<String, DBCollection>();
        this.writeConcernResolver = new DefaultWriteConcernResolver();
        try {
            final Properties properties = new Properties();
            properties.load(this.getClass().getClassLoader().getResourceAsStream("mongo.properties"));
            final String host = properties.getProperty("mongo.host");
            final int port = Integer.valueOf(properties.getProperty("mongo.port"));
            final Mongo mongo = new Mongo(host, port);
            this.db = mongo.getDB(dbName);
        }
        catch (Exception e) {
            throw new RuntimeException("init mongotemplate error", e);
        }
    }
    
    public void setConvert(final AbstractConvert convert) {
        this.convert = convert;
    }
    
    public void setWriteConcernResolver(final WriteConcernResolver writeConcernResolver) {
        this.writeConcernResolver = writeConcernResolver;
    }
    
    public void setErrorThrow(final boolean errorThrow) {
        this.errorThrow = errorThrow;
    }
    
    @Override
    public void save(final Object obj) {
        final DBCollection dbCollection = this.getDBCollection(obj.getClass());
        this.save(this.convert.convert(obj), dbCollection);
    }
    
    @Override
    public void save(final DBObject dbObject, final DBCollection dbCollection) {
        final WriteConcern writeConcern = this.writeConcernResolver.resolve(MongoActionOperation.INSERT);
        if (writeConcern.getW() > WriteConcern.NORMAL.getW()) {
            dbCollection.save(dbObject, writeConcern);
        }
        else {
            try {
                this.db.requestStart();
                final WriteResult result = dbCollection.save(dbObject, writeConcern);
                final String error = result.getError();
                if (error != null) {
                    if (this.errorThrow) {
                        throw new RuntimeException("save error " + error);
                    }
                    MongoTemplate.log.error("save error " + error);
                }
            }
            finally {
                this.db.requestDone();
            }
            this.db.requestDone();
        }
    }
    
    @Override
    public void save(final DBObject[] dbObject, final DBCollection dbCollection) {
        final WriteConcern writeConcern = this.writeConcernResolver.resolve(MongoActionOperation.INSERT);
        if (writeConcern.getW() > WriteConcern.NORMAL.getW()) {
            dbCollection.insert(dbObject, writeConcern);
        }
        else {
            try {
                this.db.requestStart();
                final WriteResult result = dbCollection.insert(dbObject, writeConcern);
                final String error = result.getError();
                if (error != null) {
                    if (this.errorThrow) {
                        throw new RuntimeException("save error " + error);
                    }
                    MongoTemplate.log.error("save error " + error);
                }
            }
            finally {
                this.db.requestDone();
            }
            this.db.requestDone();
        }
    }
    
    @Override
    public void save(final Object obj, final MongoEntity entity) {
        this.save(this.convert.convert(obj), entity.getDBCollection(this.db));
    }
    
    @Override
    public <E> void save(final List<E> objList, final Class<E> clazz) {
        final DBObject[] array = (DBObject[])new BasicDBObject[objList.size()];
        for (int i = 0; i < objList.size(); ++i) {
            array[i] = this.convert.convert(objList.get(i));
        }
        final DBCollection dbCollection = this.getDBCollection(clazz);
        this.save(array, dbCollection);
    }
    
    @Override
    public <E> void save(final List<E> objList, final MongoEntity entity) {
        final DBObject[] array = (DBObject[])new BasicDBObject[objList.size()];
        for (int i = 0; i < objList.size(); ++i) {
            array[i] = this.convert.convert(objList.get(i));
        }
        this.save(array, entity.getDBCollection(this.db));
    }
    
    @Override
    public <E> void save(final List<E> objList, final DBCollection dbCollection) {
        final DBObject[] array = (DBObject[])new BasicDBObject[objList.size()];
        for (int i = 0; i < objList.size(); ++i) {
            array[i] = this.convert.convert(objList.get(i));
        }
        this.save(array, dbCollection);
    }
    
    @Override
    public void remove(final ObjectId id, final Class<?> clazz) {
        final DBObject dbObject = (DBObject)new BasicDBObject();
        dbObject.put("_id", (Object)id);
        final DBCollection dbCollection = this.getDBCollection(clazz);
        this.remove(dbObject, dbCollection);
    }
    
    @Override
    public void remove(final Object obj) {
        final DBCollection dbCollection = this.getDBCollection(obj.getClass());
        this.remove(this.convert.convert(obj), dbCollection);
    }
    
    @Override
    public void removeAll(final Class<?> clazz) {
        final DBCollection dbCollection = this.getDBCollection(clazz, false);
        if (dbCollection == null) {
            return;
        }
        dbCollection.drop();
    }
    
    @Override
    public void removeAll(final MongoEntity entity) {
        entity.getDBCollection(this.db).drop();
    }
    
    @Override
    public void removeAll(final DBCollection dbCollection) {
        dbCollection.drop();
    }
    
    @Override
    public void remove(final ObjectId id, final DBCollection dbCollection) {
        final DBObject dbObject = (DBObject)new BasicDBObject();
        dbObject.put("_id", (Object)id);
        this.remove(dbObject, dbCollection);
    }
    
    @Override
    public void remove(final Object obj, final DBCollection dbCollection) {
        this.remove(this.convert.convert(obj), dbCollection);
    }
    
    @Override
    public void remove(final DBObject dbObject, final Class<?> clazz) {
        final DBCollection dbCollection = this.getDBCollection(clazz);
        this.remove(dbObject, dbCollection);
    }
    
    @Override
    public void remove(final Query query, final MongoEntity entity) {
        this.remove(QueryParse.parse(query), entity.getDBCollection(this.db));
    }
    
    @Override
    public void remove(final DBObject dbObject, final DBCollection dbCollection) {
        final WriteConcern writeConcern = this.writeConcernResolver.resolve(MongoActionOperation.INSERT);
        if (writeConcern.getW() > WriteConcern.NORMAL.getW()) {
            dbCollection.remove(dbObject, writeConcern);
        }
        else {
            try {
                this.db.requestStart();
                final WriteResult result = dbCollection.remove(dbObject, writeConcern);
                final String error = result.getError();
                if (error != null) {
                    if (this.errorThrow) {
                        throw new RuntimeException("remove error " + error);
                    }
                    MongoTemplate.log.error("remove error " + error);
                }
            }
            finally {
                this.db.requestDone();
            }
            this.db.requestDone();
        }
    }
    
    @Override
    public <E> E query(final Query query, final Class<E> clazz) {
        final DBCollection dbCollection = this.getDBCollection(clazz);
        final DBObject dbObject = dbCollection.findOne(QueryParse.parse(query));
        return this.convert.convert(dbObject, clazz);
    }
    
    @Override
    public <E> E query(final Query query, final MongoEntity entity) {
        final DBObject dbObject = entity.getDBCollection(this.db).findOne(QueryParse.parse(query));
        return this.convert.convert(dbObject, entity.getEntityClass());
    }
    
    @Override
    public DBCursor query(final Query query, final DBCollection dbCollection) {
        return dbCollection.find(QueryParse.parse(query));
    }
    
    @Override
    public DBCursor query(final Query query, final OrderBy orderBy, final DBCollection dbCollection) {
        return dbCollection.find(QueryParse.parse(query)).addSpecial(SpecialOp.orderby.getValue(), (Object)orderBy.toDBObject());
    }
    
    @Override
    public DBCursor query(final Query query, final Special special, final DBCollection dbCollection) {
        return dbCollection.find(QueryParse.parse(query)).addSpecial(special.op.getValue(), special.value);
    }
    
    @Override
    public <E> List<E> readAll(final MongoEntity entity) {
        final DBCollection dbCollection = entity.getDBCollection(this.db);
        final DBCursor cursor = dbCollection.find();
        final List<E> resultList = new ArrayList<E>();
        while (cursor.hasNext()) {
            final DBObject dbObject = cursor.next();
            resultList.add(this.convert.convert(dbObject, entity.getEntityClass()));
        }
        return resultList;
    }
    
    @Override
    public int count(final Query query, final MongoEntity entity) {
        final DBCollection dbCollection = entity.getDBCollection(this.db);
        return (int)dbCollection.count(QueryParse.parse(query));
    }
    
    @Override
    public <E> List<E> queryList(final Query query, final Class<E> clazz) {
        final DBCollection dbCollection = this.getDBCollection(clazz);
        final DBCursor cursor = dbCollection.find(QueryParse.parse(query));
        final List<E> resultList = new ArrayList<E>();
        while (cursor.hasNext()) {
            final DBObject dbObject = cursor.next();
            resultList.add(this.convert.convert(dbObject, clazz));
        }
        return resultList;
    }
    
    @Override
    public <E> List<E> queryList(final Query query, final OrderBy orderBy, final Class<E> clazz) {
        final DBCollection dbCollection = this.getDBCollection(clazz);
        final DBCursor cursor = dbCollection.find(QueryParse.parse(query)).addSpecial(SpecialOp.orderby.getValue(), (Object)orderBy.toDBObject());
        final List<E> resultList = new ArrayList<E>();
        while (cursor.hasNext()) {
            final DBObject dbObject = cursor.next();
            resultList.add(this.convert.convert(dbObject, clazz));
        }
        return resultList;
    }
    
    @Override
    public <E> List<E> queryList(final Query query, final Special special, final Class<E> clazz) {
        final DBCollection dbCollection = this.getDBCollection(clazz);
        final DBCursor cursor = dbCollection.find(QueryParse.parse(query)).addSpecial(special.op.getValue(), special.value);
        final List<E> resultList = new ArrayList<E>();
        while (cursor.hasNext()) {
            final DBObject dbObject = cursor.next();
            resultList.add(this.convert.convert(dbObject, clazz));
        }
        return resultList;
    }
    
    @Override
    public <E> List<E> queryList(final Query query, final MongoEntity entity) {
        final DBCollection dbCollection = entity.getDBCollection(this.db);
        final DBCursor cursor = dbCollection.find(QueryParse.parse(query));
        final List<E> resultList = new ArrayList<E>();
        while (cursor.hasNext()) {
            final DBObject dbObject = cursor.next();
            resultList.add(this.convert.convert(dbObject, entity.getEntityClass()));
        }
        return resultList;
    }
    
    @Override
    public <E> List<E> queryList(final Query query, final OrderBy orderBy, final MongoEntity entity) {
        final DBCollection dbCollection = entity.getDBCollection(this.db);
        final DBCursor cursor = dbCollection.find(QueryParse.parse(query)).addSpecial(SpecialOp.orderby.getValue(), (Object)orderBy.toDBObject());
        final List<E> resultList = new ArrayList<E>();
        while (cursor.hasNext()) {
            final DBObject dbObject = cursor.next();
            resultList.add(this.convert.convert(dbObject, entity.getEntityClass()));
        }
        return resultList;
    }
    
    @Override
    public <E> List<E> queryList(final Query query, final int skip, final int limit, final MongoEntity entity) {
        final DBCollection dbCollection = entity.getDBCollection(this.db);
        final DBCursor cursor = dbCollection.find(QueryParse.parse(query)).skip(skip).limit(limit);
        final List<E> resultList = new ArrayList<E>();
        while (cursor.hasNext()) {
            final DBObject dbObject = cursor.next();
            resultList.add(this.convert.convert(dbObject, entity.getEntityClass()));
        }
        return resultList;
    }
    
    @Override
    public <E> List<E> queryList(final Query query, final OrderBy orderBy, final int skip, final int limit, final MongoEntity entity) {
        final DBCollection dbCollection = entity.getDBCollection(this.db);
        final DBCursor cursor = dbCollection.find(QueryParse.parse(query)).addSpecial(SpecialOp.orderby.getValue(), (Object)orderBy.toDBObject()).skip(skip).limit(limit);
        final List<E> resultList = new ArrayList<E>();
        while (cursor.hasNext()) {
            final DBObject dbObject = cursor.next();
            resultList.add(this.convert.convert(dbObject, entity.getEntityClass()));
        }
        return resultList;
    }
    
    @Override
    public int update(final Query query, final Update update, final Class<?> clazz) {
        final DBCollection dbCollection = this.getDBCollection(clazz);
        return this.update(QueryParse.parse(query), UpdateParse.parse(update), false, true, dbCollection);
    }
    
    @Override
    public int update(final Query query, final Object obj, final Class<?> clazz) {
        final DBCollection dbCollection = this.getDBCollection(clazz);
        return this.update(QueryParse.parse(query), this.convert.convert(obj), false, false, dbCollection);
    }
    
    @Override
    public int update(final Update update, final MongoEntity entity) {
        final Query query = new Query();
        query.add(new Where("_id", Op.exists, new Object[] { true }));
        return this.update(QueryParse.parse(query), UpdateParse.parse(update), false, true, entity.getDBCollection(this.db));
    }
    
    @Override
    public int update(final Query query, final Update update, final MongoEntity entity) {
        return this.update(QueryParse.parse(query), UpdateParse.parse(update), false, true, entity.getDBCollection(this.db));
    }
    
    @Override
    public int update(final Query query, final Update update, final DBCollection dbCollection) {
        return this.update(QueryParse.parse(query), UpdateParse.parse(update), false, true, dbCollection);
    }
    
    @Override
    public int update(final Query query, final Object obj, final DBCollection dbCollection) {
        return this.update(QueryParse.parse(query), this.convert.convert(obj), false, false, dbCollection);
    }
    
    @Override
    public int update(final DBObject query, final DBObject update, final boolean upsert, final boolean multi, final DBCollection dbCollection) {
        final WriteConcern writeConcern = this.writeConcernResolver.resolve(MongoActionOperation.INSERT);
        try {
            this.db.requestStart();
            if (writeConcern.getW() > WriteConcern.NORMAL.getW()) {
                final WriteResult result = dbCollection.update(query, update, upsert, multi, writeConcern);
                return result.getN();
            }
            final WriteResult result = dbCollection.update(query, update, upsert, multi, writeConcern);
            final String error = result.getError();
            if (error != null) {
                if (this.errorThrow) {
                    throw new RuntimeException("update error " + error);
                }
                MongoTemplate.log.error("update error " + error);
            }
            return result.getN();
        }
        finally {
            this.db.requestDone();
        }
    }
    
    @Override
    public DBCollection getDBCollection(final MongoEntity entity) {
        return entity.getDBCollection(this.db);
    }
    
    @Override
    public DBCollection getDBCollection(final Class<?> clazz) {
        final String collectionName = MongoDBUtil.getCollectionName(clazz);
        return this.getDBCollection(collectionName, true);
    }
    
    @Override
    public DBCollection getDBCollection(final Class<?> clazz, final boolean create) {
        final String collectionName = MongoDBUtil.getCollectionName(clazz);
        return this.getDBCollection(collectionName, create);
    }
    
    @Override
    public DBCollection getDBCollection(final String collectionName) {
        return this.getDBCollection(collectionName, false);
    }
    
    @Override
    public DBCollection getDBCollection(final String collectionName, final boolean create) {
        return this._getDBCollection(collectionName, create);
    }
    
    private synchronized DBCollection _getDBCollection(final String collectionName, final boolean create) {
        DBCollection dbCollection = this.collectionMap.get(collectionName);
        if (dbCollection == null && create) {
            dbCollection = this.db.getCollection(collectionName);
            this.collectionMap.put(collectionName, dbCollection);
        }
        return dbCollection;
    }
}
