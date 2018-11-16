package com.reign.framework.mongo.jdbc;

import com.reign.framework.mongo.*;
import com.reign.framework.common.*;
import java.util.*;
import com.reign.framework.jdbc.*;
import com.reign.util.*;
import com.reign.framework.mongo.util.*;
import java.lang.annotation.*;
import com.reign.framework.mongo.annotation.*;

public class JdbcEntity
{
    private MongoField[] fields;
    private IdEntity id;
    private Class<?> clazz;
    private NameStrategy nameStrategy;
    private String entityName;
    private MongoField[] idFields;
    private WrapperDBCollection dbCollection;
    
    public static JdbcEntity resolve(final Class<?> clazz, final NameStrategy nameStrategy) {
        final JdbcEntity entity = new JdbcEntity();
        entity.fields = parse(Lang.getFields(clazz));
        entity.nameStrategy = nameStrategy;
        entity.clazz = clazz;
        entity.entityName = clazz.getSimpleName();
        final List<MongoField> idFields = new ArrayList<MongoField>();
        MongoField[] fields;
        for (int length = (fields = entity.fields).length, i = 0; i < length; ++i) {
            final MongoField field = fields[i];
            if (field.isPrimary) {
                idFields.add(field);
            }
        }
        entity.idFields = idFields.toArray(new MongoField[0]);
        if (entity.idFields.length == 1) {
            entity.id = new SingleIdEntity(entity.idFields[0], nameStrategy);
        }
        else {
            entity.id = new ComplexIdEntity(entity.idFields, nameStrategy);
        }
        return entity;
    }
    
    public List<Param> builderInsertParams(final Object obj) {
        final Params params = new Params();
        MongoField[] fields;
        for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
            final MongoField field = fields[i];
            if (!field.insertIgnore) {
                params.addParam(ReflectUtil.get(field.field, obj), field.jdbcType);
            }
        }
        return params;
    }
    
    public MongoField[] getFields() {
        return this.fields;
    }
    
    public IdEntity getId() {
        return this.id;
    }
    
    public String getSQL() {
        final StringBuilder builder = new StringBuilder();
        builder.append("SELECT ").append("*").append(" FROM ").append(this.nameStrategy.propertyNameToColumnName(this.entityName)).append(" WHERE ");
        final int index = 1;
        MongoField[] idFields;
        for (int length = (idFields = this.idFields).length, i = 0; i < length; ++i) {
            final MongoField field = idFields[i];
            if (index != 1) {
                builder.append(" AND ");
            }
            builder.append(this.nameStrategy.propertyNameToColumnName(field.fieldName)).append("= ? ");
        }
        return builder.toString();
    }
    
    public List<Param> builderIdParams(final Object[] keys) {
        final Params params = new Params();
        int index = 0;
        MongoField[] idFields;
        for (int length = (idFields = this.idFields).length, i = 0; i < length; ++i) {
            final MongoField field = idFields[i];
            params.addParam(keys[index++], field.jdbcType);
        }
        return params;
    }
    
    public String getQueryCollectionName() {
        return "QUERY_" + this.entityName.toUpperCase();
    }
    
    public synchronized WrapperDBCollection getDBCollection(final DB db) {
        if (this.dbCollection == null) {
            final String collectionName = MongoDBUtil.getCollectionName(this.clazz);
            if (!db.getCollectionNames().contains(collectionName)) {
                final DBCollection collection = db.getCollection(collectionName);
                collection.createIndex(this.id.getMongoKey());
                this.dbCollection = new WrapperDBCollection(collection);
            }
            else {
                this.dbCollection = new WrapperDBCollection(db.getCollection(collectionName));
            }
        }
        return this.dbCollection;
    }
    
    public Class<?> getEntityClass() {
        return this.clazz;
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
