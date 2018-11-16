package com.reign.framework.mongo.jdbc;

import com.reign.framework.mongo.*;
import com.reign.framework.jdbc.*;
import com.reign.framework.mongo.annotation.*;
import com.reign.framework.mongo.lang.*;

public class SingleIdEntity implements IdEntity
{
    private MongoField field;
    private DBObject mongoKey;
    private NameStrategy nameStrategy;
    private boolean autoGenerator;
    
    public SingleIdEntity(final MongoField field, final NameStrategy nameStrategy) {
        this.field = field;
        (this.mongoKey = (DBObject)new BasicDBObject()).put(field.fieldName, (Object)1);
        this.nameStrategy = nameStrategy;
        this.autoGenerator = (field.field.getAnnotation(AutoGenerator.class) != null);
    }
    
    @Override
    public DBObject getMongoKey() {
        return this.mongoKey;
    }
    
    @Override
    public String getSelectColumn() {
        return this.nameStrategy.propertyNameToColumnName(this.field.fieldName);
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
        try {
            this.field.field.setAccessible(true);
            this.field.field.set(obj, args[0]);
        }
        catch (Throwable t) {
            throw new RuntimeException("set key error", t);
        }
    }
    
    @Override
    public Query getMongoKeyQuery(final Object... keys) {
        final Query query = new Query();
        query.add(new Where(this.nameStrategy.propertyNameToColumnName(this.field.fieldName), Op.eq, new Object[] { ((Object[])keys[0])[0] }));
        return query;
    }
    
    @Override
    public Object[] getIdValue(final Object obj) {
        try {
            this.field.field.setAccessible(true);
            final Object result = this.field.field.get(obj);
            return new Object[] { result };
        }
        catch (Throwable t) {
            throw new RuntimeException("get key error", t);
        }
    }
}
