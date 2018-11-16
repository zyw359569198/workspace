package com.reign.framework.hibernate.dao;

import com.reign.framework.hibernate.model.*;
import java.io.*;
import org.hibernate.criterion.*;
import com.reign.framework.hibernate.page.*;
import java.util.*;
import org.hibernate.*;

public interface IBaseDao<T extends IModel, PK extends Serializable>
{
    PK create(final T p0);
    
    T read(final PK p0);
    
    T readForUpdate(final PK p0);
    
    T load(final PK p0);
    
    T loadForUpdate(final PK p0);
    
    void saveOrUpdate(final T p0);
    
    void update(final T p0);
    
    void delete(final T p0);
    
    void deleteById(final PK p0);
    
    List<T> getModels();
    
    Long getModelSize();
    
    List<T> getModelByPage(final T p0, final int p1, final int p2);
    
    List<T> getModelByHibernateCriteria(final DetachedCriteria p0);
    
    List<T> getModelByHibernateCriteria(final DetachedCriteria p0, final int p1, final int p2);
    
    List<?> getResultByHQLAndParam(final String p0);
    
    T getFirstResultByHQLAndParam(final String p0);
    
    List<?> getResultByHQLAndParam(final String p0, final Object... p1);
    
    T getFirstResultByHQLAndParam(final String p0, final Object... p1);
    
    List<?> getResultByHQLAndParam(final String p0, final PagingData p1, final Object... p2);
    
    List<?> getResultByHQLAndParamNoUpdate(final String p0, final PagingData p1, final Object... p2);
    
    List<?> getResultByHQLAndParamForUpdate(final String p0, final String p1);
    
    List<?> getResultByHQLAndParamForUpdate(final String p0, final String p1, final Object... p2);
    
    Iterator<?> getIteratorByHQLAndParam(final String p0);
    
    T getFirstIteratorByHQLAndParam(final String p0);
    
    Iterator<?> getIteratorByHQLAndParam(final String p0, final Object... p1);
    
    T getFirstIteratorByHQLAndParam(final String p0, final Object... p1);
    
    Iterator<?> getIteratorByHQLAndParam(final String p0, final PagingData p1, final Object... p2);
    
    Iterator<?> getIteratorByHQLAndParamNoUpdate(final String p0, final PagingData p1, final Object... p2);
    
    Session getCurrentSession();
    
    void evict(final T p0);
    
    void evict(final PK p0);
    
    void evictAll();
}
