package com.reign.framework.mongo;

import com.reign.framework.mongo.lang.*;

public interface MongoIdEntity
{
    DBObject getMongoKey();
    
    boolean isAutoGenerate();
    
    void setKey(final Object p0, final Object... p1);
    
    Query getMongoKeyQuery(final Object... p0);
    
    Query getMongoKeyQueryByObject(final Object p0);
    
    Object getIdValue(final Object p0);
    
    Object setId(final Object p0);
}
