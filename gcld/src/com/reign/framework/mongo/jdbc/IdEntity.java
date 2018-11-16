package com.reign.framework.mongo.jdbc;

import com.reign.framework.mongo.lang.*;

public interface IdEntity
{
    DBObject getMongoKey();
    
    String getSelectColumn();
    
    boolean isGenerator();
    
    void setKey(final Object p0, final Object... p1);
    
    Query getMongoKeyQuery(final Object... p0);
    
    Object[] getIdValue(final Object p0);
}
