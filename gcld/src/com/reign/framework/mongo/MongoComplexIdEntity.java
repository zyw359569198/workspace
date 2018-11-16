package com.reign.framework.mongo;

import com.reign.framework.common.*;
import com.reign.framework.mongo.lang.*;
import com.reign.util.*;

public class MongoComplexIdEntity implements MongoIdEntity
{
    private Lang.MyField[] fields;
    private DBObject mongoKey;
    private boolean autoGenerate;
    
    public MongoComplexIdEntity(final Lang.MyField[] fields) {
        this.fields = fields;
        this.mongoKey = (DBObject)new BasicDBObject();
        for (final Lang.MyField field : fields) {
            this.mongoKey.put(field.fieldName, (Object)1);
        }
        this.autoGenerate = false;
    }
    
    @Override
    public DBObject getMongoKey() {
        return this.mongoKey;
    }
    
    @Override
    public boolean isAutoGenerate() {
        return this.autoGenerate;
    }
    
    @Override
    public void setKey(final Object obj, final Object... args) {
        if (!this.autoGenerate) {
            return;
        }
        final int index = 0;
        try {
            Lang.MyField[] fields;
            for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
                final Lang.MyField field = fields[i];
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
        Lang.MyField[] fields;
        for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
            final Lang.MyField field = fields[i];
            query.add(new Where(field.fieldName, Op.eq, new Object[] { keys[index++] }));
        }
        return query;
    }
    
    @Override
    public Query getMongoKeyQueryByObject(final Object obj) {
        final Query query = new Query();
        Lang.MyField[] fields;
        for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
            final Lang.MyField field = fields[i];
            field.field.setAccessible(true);
            final Object value = ReflectUtil.get(field.field, obj);
            query.add(new Where(field.fieldName, Op.eq, new Object[] { value }));
        }
        return query;
    }
    
    @Override
    public Object getIdValue(final Object obj) {
        try {
            final Object[] array = new Object[this.fields.length];
            int index = 0;
            Lang.MyField[] fields;
            for (int length = (fields = this.fields).length, i = 0; i < length; ++i) {
                final Lang.MyField field = fields[i];
                field.field.setAccessible(true);
                array[index++] = field.field.get(obj);
            }
            return array;
        }
        catch (Throwable t) {
            throw new RuntimeException("get key error", t);
        }
    }
    
    @Override
    public Object setId(final Object obj) {
        return obj;
    }
}
