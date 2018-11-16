package com.reign.framework.mongo;

import com.reign.framework.common.*;
import java.util.concurrent.atomic.*;
import com.reign.framework.mongo.annotation.*;
import com.reign.framework.mongo.lang.*;

public class MongoSingleIdEntity implements MongoIdEntity
{
    private Lang.MyField field;
    private DBObject mongoKey;
    private boolean autoGenerate;
    private AtomicInteger idGenerator;
    
    public MongoSingleIdEntity(final Lang.MyField field) {
        this.field = field;
        (this.mongoKey = (DBObject)new BasicDBObject()).put(field.fieldName, (Object)1);
        this.autoGenerate = (field.field.getAnnotation(AutoGenerator.class) != null);
        this.idGenerator = (this.autoGenerate ? new AtomicInteger(1) : null);
        if (this.isAutoGenerate() && !Lang.isInteger(this.field.field.getType())) {
            throw new RuntimeException("unsupport auto generate type " + this.field.field.getType());
        }
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
        query.add(new Where(this.field.fieldName, Op.eq, new Object[] { ((Object[])keys[0])[0] }));
        return query;
    }
    
    @Override
    public Query getMongoKeyQueryByObject(final Object obj) {
        final Query query = new Query();
        query.add(new Where(this.field.fieldName, Op.eq, new Object[] { this.getIdValue(obj) }));
        return query;
    }
    
    @Override
    public Object getIdValue(final Object obj) {
        try {
            this.field.field.setAccessible(true);
            final Object result = this.field.field.get(obj);
            return result;
        }
        catch (Throwable t) {
            throw new RuntimeException("get key error", t);
        }
    }
    
    @Override
    public Object setId(final Object obj) {
        final Object id = this.getIdValue(obj);
        if (id == null || (int)id == 0) {
            this.setKey(obj, this.idGenerator.getAndIncrement());
        }
        return obj;
    }
}
