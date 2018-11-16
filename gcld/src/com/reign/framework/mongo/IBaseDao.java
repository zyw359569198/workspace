package com.reign.framework.mongo;

import java.util.*;
import com.reign.framework.mongo.lang.*;
import com.reign.framework.mongo.page.*;

public interface IBaseDao<E>
{
    void save(final E p0);
    
    void saveList(final List<E> p0);
    
    int update(final E p0);
    
    int update(final Update p0);
    
    int update(final Query p0, final Update p1);
    
    void delete(final E p0);
    
    void delete(final Query p0);
    
    void deleteAll();
    
    E read(final Object... p0);
    
    List<E> readAll();
    
    int count(final Query p0);
    
    List<E> selectList(final Query p0);
    
    List<E> selectList(final Query p0, final OrderBy p1);
    
    List<E> selectList(final Query p0, final PagingData p1);
    
    List<E> selectListNoUpdate(final Query p0, final PagingData p1);
    
    List<E> selectList(final Query p0, final OrderBy p1, final PagingData p2);
    
    List<E> selectListNoUpdate(final Query p0, final OrderBy p1, final PagingData p2);
    
    MongoTemplate getMongoTemplate();
    
    DBCollection getDBCollection();
    
    MongoLock getWriteLock(final Object... p0);
    
    MongoLock getReadLock(final Object... p0);
}
