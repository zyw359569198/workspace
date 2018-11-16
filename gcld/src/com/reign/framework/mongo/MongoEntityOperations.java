package com.reign.framework.mongo;

import java.util.*;
import com.reign.framework.mongo.lang.*;

public interface MongoEntityOperations
{
    void save(final Object p0, final MongoEntity p1);
    
     <E> void save(final List<E> p0, final MongoEntity p1);
    
    int update(final Update p0, final MongoEntity p1);
    
    int update(final Query p0, final Update p1, final MongoEntity p2);
    
    void remove(final Query p0, final MongoEntity p1);
    
    void removeAll(final MongoEntity p0);
    
     <E> List<E> readAll(final MongoEntity p0);
    
     <E> E query(final Query p0, final MongoEntity p1);
    
    int count(final Query p0, final MongoEntity p1);
    
     <E> List<E> queryList(final Query p0, final MongoEntity p1);
    
     <E> List<E> queryList(final Query p0, final int p1, final int p2, final MongoEntity p3);
    
     <E> List<E> queryList(final Query p0, final OrderBy p1, final MongoEntity p2);
    
     <E> List<E> queryList(final Query p0, final OrderBy p1, final int p2, final int p3, final MongoEntity p4);
    
    DBCollection getDBCollection(final MongoEntity p0);
}
