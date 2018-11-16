package com.reign.framework.mongo;

import java.util.*;
import com.reign.framework.mongo.lang.*;

public interface MongoOperations
{
    void save(final Object p0);
    
    void save(final DBObject p0, final DBCollection p1);
    
    void save(final DBObject[] p0, final DBCollection p1);
    
     <E> void save(final List<E> p0, final Class<E> p1);
    
     <E> void save(final List<E> p0, final DBCollection p1);
    
    void remove(final ObjectId p0, final Class<?> p1);
    
    void remove(final ObjectId p0, final DBCollection p1);
    
    void remove(final Object p0);
    
    void remove(final Object p0, final DBCollection p1);
    
    void remove(final DBObject p0, final Class<?> p1);
    
    void remove(final DBObject p0, final DBCollection p1);
    
    void removeAll(final Class<?> p0);
    
    void removeAll(final DBCollection p0);
    
     <E> E query(final Query p0, final Class<E> p1);
    
    DBCursor query(final Query p0, final DBCollection p1);
    
    DBCursor query(final Query p0, final OrderBy p1, final DBCollection p2);
    
    DBCursor query(final Query p0, final Special p1, final DBCollection p2);
    
     <E> List<E> queryList(final Query p0, final Class<E> p1);
    
     <E> List<E> queryList(final Query p0, final OrderBy p1, final Class<E> p2);
    
     <E> List<E> queryList(final Query p0, final Special p1, final Class<E> p2);
    
    int update(final Query p0, final Update p1, final Class<?> p2);
    
    int update(final Query p0, final Update p1, final DBCollection p2);
    
    int update(final Query p0, final Object p1, final Class<?> p2);
    
    int update(final Query p0, final Object p1, final DBCollection p2);
    
    int update(final DBObject p0, final DBObject p1, final boolean p2, final boolean p3, final DBCollection p4);
    
    DBCollection getDBCollection(final Class<?> p0);
    
    DBCollection getDBCollection(final Class<?> p0, final boolean p1);
    
    DBCollection getDBCollection(final String p0);
    
    DBCollection getDBCollection(final String p0, final boolean p1);
}
