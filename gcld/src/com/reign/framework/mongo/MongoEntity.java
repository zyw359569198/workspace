package com.reign.framework.mongo;

import com.reign.framework.common.*;
import java.util.*;
import com.reign.framework.mongo.util.*;
import java.lang.annotation.*;
import com.reign.framework.mongo.annotation.*;

public class MongoEntity
{
    private MongoField[] fields;
    private MongoIdEntity id;
    private Class<?> clazz;
    private Lang.MyField[] idFields;
    private DBCollection dbCollection;
    private MongoLock[] locks;
    
    public static MongoEntity resolve(final Class<?> clazz) {
        final MongoEntity entity = new MongoEntity();
        entity.fields = parse(Lang.getFields(clazz));
        entity.clazz = clazz;
        final List<MongoField> idFields = new ArrayList<MongoField>();
        MongoField[] fields;
        for (int length = (fields = entity.fields).length, i = 0; i < length; ++i) {
            final MongoField field = fields[i];
            if (field.isPrimary) {
                idFields.add(field);
            }
        }
        entity.idFields = idFields.toArray(new Lang.MyField[0]);
        if (entity.idFields.length == 1) {
            entity.id = new MongoSingleIdEntity(entity.idFields[0]);
        }
        else {
            entity.id = new MongoComplexIdEntity(entity.idFields);
        }
        entity.locks = LockFactory.createLock(1024);
        return entity;
    }
    
    public Object setId(final Object obj) {
        return this.id.setId(obj);
    }
    
    public MongoField[] getFields() {
        return this.fields;
    }
    
    public MongoIdEntity getId() {
        return this.id;
    }
    
    public Class<?> getEntityClass() {
        return this.clazz;
    }
    
    public synchronized DBCollection getDBCollection(final DB db) {
        if (this.dbCollection == null) {
            final String collectionName = MongoDBUtil.getCollectionName(this.clazz);
            if (!db.getCollectionNames().contains(collectionName)) {
                final DBCollection collection = db.getCollection(collectionName);
                collection.createIndex(this.id.getMongoKey());
                this.dbCollection = collection;
            }
            else {
                this.dbCollection = db.getCollection(collectionName);
            }
        }
        return this.dbCollection;
    }
    
    public MongoLock getReadLock(final Object[] keys) {
        return this.locks[this.hashCode(keys) % this.locks.length].readLock;
    }
    
    public MongoLock getWriteLock(final Object[] keys) {
        return this.locks[this.hashCode(keys) % this.locks.length].writeLock;
    }
    
    private int hashCode(final Object[] keys) {
        int hash = 0;
        for (final Object obj : keys) {
            hash ^= obj.hashCode();
        }
        return hash;
    }
    
    private static MongoField[] parse(final Lang.MyField[] fields) {
        if (fields == null) {
            return null;
        }
        if (fields.length == 0) {
            return new MongoField[0];
        }
        final MongoField[] mongoFields = new MongoField[fields.length];
        for (int i = 0; i < fields.length; ++i) {
            mongoFields[i] = new MongoField(fields[i]);
            mongoFields[i].isPrimary = Lang.hasAnnotation(mongoFields[i].field, Primary.class);
            mongoFields[i].insertIgnore = Lang.hasAnnotation(mongoFields[i].field, InsertIgnore.class);
            mongoFields[i].jdbcType = Lang.getJdbcType(mongoFields[i].field.getType());
        }
        return mongoFields;
    }
}
