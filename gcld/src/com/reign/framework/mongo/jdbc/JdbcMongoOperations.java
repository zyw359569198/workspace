package com.reign.framework.mongo.jdbc;

import com.reign.framework.mongo.*;
import java.util.*;
import com.reign.framework.mongo.lang.*;

public interface JdbcMongoOperations extends MongoOperations
{
    void save(final Object p0, final JdbcEntity p1);
    
     <E> void save(final List<E> p0, final JdbcEntity p1);
    
    void remove(final Object p0, final JdbcEntity p1);
    
    void removeAll(final JdbcEntity p0);
    
     <E> E query(final JdbcEntity p0, final Query p1);
    
     <E> List<E> queryList(final JdbcEntity p0, final Query p1);
    
     <E> List<E> queryList(final JdbcEntity p0, final Query p1, final OrderBy p2);
    
     <E> List<E> queryList(final JdbcEntity p0, final Query p1, final Special p2);
    
    void update(final JdbcEntity p0, final Query p1, final Update p2);
    
    void update(final JdbcEntity p0, final Query p1, final Object p2);
    
    WrapperDBCollection getWrapperDBCollection(final Class<?> p0);
    
    WrapperDBCollection getWrapperDBCollection(final Class<?> p0, final boolean p1);
    
    void removeAll(final String p0);
    
    DBObject query(final String p0, final Query p1);
    
    void remove(final String p0, final Query p1);
    
    void save(final String p0, final DBObject p1);
}
