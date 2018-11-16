package com.reign.framework.mongo.jdbc;

import com.reign.framework.mongo.*;
import com.reign.framework.jdbc.*;
import com.reign.framework.mongo.lang.*;
import java.io.*;

public class ComplexIdEntity implements IdEntity
{
    private MongoField[] fields;
    private DBObject mongoKey;
    private NameStrategy nameStrategy;
    private boolean autoGenerator;
    
    public ComplexIdEntity(final MongoField[] fields, final NameStrategy nameStrategy) {
        this.fields = fields;
        this.mongoKey = (DBObject)new BasicDBObject();
        for (final MongoField field : fields) {
            this.mongoKey.put(field.fieldName, (Object)1);
        }
        this.nameStrategy = nameStrategy;
        this.autoGenerator = false;
    }
    
    @Override
    public DBObject getMongoKey() {
        return this.mongoKey;
    }
    
    @Override
    public String getSelectColumn() {
        final StringBuilder builder = new StringBuilder();
        MongoField[] fields;
        for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
            final MongoField field = fields[i];
            builder.append(this.nameStrategy.propertyNameToColumnName(field.fieldName)).append(",");
        }
        builder.deleteCharAt(builder.length() - 1);
        return builder.toString();
    }
    
    @Override
    public boolean isGenerator() {
        return this.autoGenerator;
    }
    
    @Override
    public void setKey(final Object obj, final Object... args) {
        if (!this.autoGenerator) {
            return;
        }
        final int index = 0;
        try {
            MongoField[] fields;
            for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
                final MongoField field = fields[i];
                field.field.setAccessible(true);
                field.field.set(obj, args[index]);
            }
        }
        catch (Throwable t) {
            throw new RuntimeException("set key error", t);
        }
    }
    
    @Override
    public Query getMongoKeyQuery(final Object... keys) {
        final Query query = new Query();
        int index = 0;
        MongoField[] fields;
        for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
            final MongoField field = fields[i];
            query.add(new Where(this.nameStrategy.propertyNameToColumnName(field.fieldName), Op.eq, new Object[] { keys[index++] }));
        }
        return query;
    }
    
    @Override
    public Object[] getIdValue(final Object obj) {
        try {
            final Object[] array = new Object[this.fields.length];
            int index = 0;
            MongoField[] fields;
            for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
                final MongoField field = fields[i];
                field.field.setAccessible(true);
                array[index++] = field.field.get(obj);
            }
            return array;
        }
        catch (Throwable t) {
            throw new RuntimeException("get key error", t);
        }
    }
}
